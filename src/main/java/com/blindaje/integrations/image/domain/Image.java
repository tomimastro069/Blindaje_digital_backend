package com.blindaje.integrations.image.domain;
import com.blindaje.integrations.camera.domain.Camera;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "camera_id")
    private Camera camera;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "captured_at")
    private LocalDateTime capturedAt;
    
    public Image() {}
    public Long getId() { return id; }
    public Camera getCamera() { return camera; }
    public String getImagePath() { return imagePath; }
    public LocalDateTime getCapturedAt() { return capturedAt; }
    public void setCamera(Camera camera) { this.camera = camera; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public void setCapturedAt(LocalDateTime capturedAt) { this.capturedAt = capturedAt; }
    public void setId(Long id) { this.id = id; }

}
