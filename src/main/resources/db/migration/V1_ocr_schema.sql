CREATE TABLE cameras (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(255),
    ip_address VARCHAR(50),
    property_id INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE images (
    id SERIAL PRIMARY KEY,
    camera_id INTEGER,
    image_path TEXT NOT NULL,
    captured_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_image_camera
        FOREIGN KEY (camera_id)
        REFERENCES cameras(id)
);

CREATE TABLE ocr_scans (
    id SERIAL PRIMARY KEY,
    extracted_text TEXT,
    image_path TEXT,
    scanned_at TIMESTAMP,
    property_id VARCHAR(100)
);