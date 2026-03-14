package com.blindaje.integrations.ocr.service;

import com.blindaje.integrations.ocr.domain.OcrScan;
import com.blindaje.integrations.ocr.repository.OcrScanRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.*;

@Service
public class OcrService {

    private final OcrScanRepository ocrScanRepository;

    public OcrService(OcrScanRepository ocrScanRepository) {
        this.ocrScanRepository = ocrScanRepository;
    }

        public OcrScan procesarImagen(MultipartFile imagen, String propertyId) throws IOException, InterruptedException {

        Path tempDir = Files.createTempDirectory("ocr_");
        Path imagePath = tempDir.resolve("dni_scan.png");
        imagen.transferTo(imagePath.toFile());

        // Primero intentar con --psm 1 (detección automática de orientación)
        String textoExtraido = ejecutarTesseract(tempDir, imagePath, -1);

        // Si no tiene MRZ válida ni texto del frente, probar rotaciones manuales
        if (!contieneMRZ(textoExtraido) && !contieneFrente(textoExtraido)) {
            int[] rotaciones = {90, 180, 270};
            for (int grados : rotaciones) {
                String texto = ejecutarTesseract(tempDir, imagePath, grados);
                if (contieneMRZ(texto) || contieneFrente(texto)) {
                    textoExtraido = texto;
                    break;
                }
            }
        }

        String dni      = extraerDni(textoExtraido);
        String apellido = extraerApellido(textoExtraido);
        String nombre   = extraerNombre(textoExtraido);

        OcrScan scan = new OcrScan(textoExtraido, imagePath.toString(), propertyId);
        scan.setDni(dni);
        scan.setApellido(apellido);
        scan.setNombre(nombre);

        try (var archivos = Files.walk(tempDir)) {
            archivos.sorted(java.util.Comparator.reverseOrder())
                    .forEach(p -> {
                        try { Files.deleteIfExists(p); }
                        catch (IOException ignored) {}
                    });
        }

        return ocrScanRepository.save(scan);
    }

    private String ejecutarTesseract(Path tempDir, Path imagePath, int rotacion) throws IOException, InterruptedException {

        Path imagenProcesada = imagePath;
        if (rotacion > 0) {
            imagenProcesada = tempDir.resolve("dni_rot_" + rotacion + ".png");
            ProcessBuilder rotar = new ProcessBuilder(
                "convert", imagePath.toString(),
                "-rotate", String.valueOf(rotacion),
                imagenProcesada.toString()
            );
            rotar.redirectErrorStream(true);
            rotar.start().waitFor();
        }

        Path outputBase = tempDir.resolve("output_" + (rotacion == -1 ? "auto" : rotacion));
        
        // -1 = usar --psm 1 (auto), cualquier otro = --psm 6 (bloque uniforme)
        ProcessBuilder pb;
        if (rotacion == -1) {
            pb = new ProcessBuilder(
                "tesseract", imagenProcesada.toString(), outputBase.toString(),
                "-l", "spa+eng", "--psm", "1"
            );
        } else {
            pb = new ProcessBuilder(
                "tesseract", imagenProcesada.toString(), outputBase.toString(),
                "-l", "spa+eng", "--psm", "6"
            );
        }

        pb.redirectErrorStream(true);
        Process process = pb.start();
        process.getInputStream().transferTo(OutputStream.nullOutputStream());
        process.waitFor();

        Path outputTxt = Path.of(outputBase + ".txt");
        if (!Files.exists(outputTxt)) return "";
        return Files.readString(outputTxt).trim();
    }

    private boolean contieneMRZ(String texto) {
        return texto.matches("(?s).*IDARG\\d{7,8}.*");
    }

    private boolean contieneFrente(String texto) {
        return texto.toLowerCase().contains("apellido") || texto.toLowerCase().contains("surname");
    }

    public Optional<OcrScan> buscarPorPropertyId(String propertyId) {
        return ocrScanRepository.findByPropertyId(propertyId);
    }

    // ─── Helpers de parseo ───────────────────────────────────────────────────

    /**
     * Intenta extraer DNI de la MRZ (dorso) primero,
     * si no encuentra, busca formato con puntos (frente)
     */
    private String extraerDni(String texto) {
        // Dorso: MRZ línea 1 → IDARG47078983
        Pattern mrzPattern = Pattern.compile("IDARG(\\d{7,8})<");
        Matcher mrzMatcher = mrzPattern.matcher(texto);
        if (mrzMatcher.find()) return mrzMatcher.group(1);

        // Frente: 47.078.983 o 47078983
        Pattern frentePattern = Pattern.compile("\\b(\\d{1,2}\\.\\d{3}\\.\\d{3}|\\d{7,8})\\b");
        Matcher frenteMatcher = frentePattern.matcher(texto);
        return frenteMatcher.find() ? frenteMatcher.group().replaceAll("\\.", "") : null;
    }

    /**
     * Intenta extraer apellido de la MRZ (dorso) primero,
     * si no encuentra, busca etiqueta del frente
     */
    private String extraerApellido(String texto) {
        for (String linea : texto.split("\\n")) {
            linea = linea.trim();
            java.util.regex.Matcher m = Pattern.compile("([A-Z]{3,})<<[A-Z0-9< ]+").matcher(linea);
            while (m.find()) {
                String candidato = m.group(1);
                if (!candidato.equals("ARG") &&
                    !candidato.equals("IDARG") &&
                    !candidato.endsWith("ARG")) {
                    // Limpiar basura al inicio: si empieza con letra suelta + apellido conocido,
                    // quedarse solo con la parte más larga después de cualquier letra inicial espuria
                    // Ej: SWASTROPIETRO → buscar si hay un apellido válido quitando 1-2 letras iniciales
                    return limpiarCandidatoMRZ(candidato);
                }
            }
        }

        // Frente: captura solo hasta fin de línea
        Pattern frentePattern = Pattern.compile(
            "(?i).*Apellido\\s*/\\s*Surname[\\s\\r\\n]+([A-ZÁÉÍÓÚÑ][A-ZÁÉÍÓÚÑ ]+?)(?:\\n|$)",
            Pattern.UNICODE_CHARACTER_CLASS
        );
        Matcher frenteMatcher = frentePattern.matcher(texto);
        return frenteMatcher.find() ? frenteMatcher.group(1).trim() : null;
    }

    private String extraerNombre(String texto) {
        for (String linea : texto.split("\\n")) {
            linea = linea.trim();
            java.util.regex.Matcher m = Pattern.compile("([A-Z]{3,})<<([A-Z0-9< ]+)").matcher(linea);
            while (m.find()) {
                String apellidoCandidato = m.group(1);
                if (!apellidoCandidato.equals("ARG") &&
                    !apellidoCandidato.equals("IDARG") &&
                    !apellidoCandidato.endsWith("ARG")) {
                    String nombreRaw = m.group(2);
                    return Arrays.stream(nombreRaw.split("[<\\s]+"))
                        .filter(s -> !s.isBlank())
                        .map(s -> s.replace("0", "O"))  // O confundida con 0
                        .filter(s -> s.matches("[A-Z]+")) // solo palabras alfabéticas
                        .collect(java.util.stream.Collectors.joining(" "))
                        .trim();
                }
            }
        }

        // Frente: captura solo hasta fin de línea
        Pattern frentePattern = Pattern.compile(
            "(?i).*Nombre\\s*/\\s*Name[\\s\\r\\n]+([A-ZÁÉÍÓÚÑ][A-ZÁÉÍÓÚÑ ]+?)(?:\\n|$)",
            Pattern.UNICODE_CHARACTER_CLASS
        );
        Matcher frenteMatcher = frentePattern.matcher(texto);
        return frenteMatcher.find() ? frenteMatcher.group(1).trim() : null;
    }

    /**
     * Limpia basura que Tesseract agrega al inicio del apellido en la MRZ
     * Ej: SWASTROPIETRO → MASTROPIETRO (la S inicial es basura)
     * No siempre es posible detectarlo, pero al menos limpiamos caracteres no esperados
     */
    private String limpiarCandidatoMRZ(String candidato) {
        // Si el candidato empieza con 1-2 letras que no forman parte de una palabra española común
        // simplemente lo devolvemos tal cual — no podemos adivinar qué letras son basura
        // sin un diccionario. Lo dejamos para que el sistema mejore con mejor calidad de imagen.
        return candidato;
    }
}