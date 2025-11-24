-- Table: parcels
CREATE TABLE parcels (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    latitude DECIMAL(9,6),
    longitude DECIMAL(9,6),
    surface_area DECIMAL(10,2),  -- in hectares
    soil_type VARCHAR(100),
    irrigation_system VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: crops
CREATE TABLE crops (
    id BIGSERIAL PRIMARY KEY,
    parcel_id BIGINT REFERENCES parcels(id) ON DELETE CASCADE,
    crop_type VARCHAR(100) NOT NULL,
    variety VARCHAR(100),
    planting_date DATE NOT NULL,
    expected_harvest_date DATE,
    growth_stage VARCHAR(50),
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: harvests
CREATE TABLE harvests (
    id BIGSERIAL PRIMARY KEY,
    crop_id BIGINT REFERENCES crops(id) ON DELETE CASCADE,
    harvest_date DATE NOT NULL,
    quantity DECIMAL(10,2),  -- in tons
    quality_grade VARCHAR(50),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_parcels_location ON parcels(location);
CREATE INDEX idx_crops_parcel ON crops(parcel_id);
CREATE INDEX idx_crops_status ON crops(status);
CREATE INDEX idx_harvests_crop ON harvests(crop_id);

-- Insert sample parcels
INSERT INTO parcels (name, location, latitude, longitude, surface_area, soil_type, irrigation_system, created_at, updated_at)
VALUES
    ('North Field', 'Tunis North', 36.8065, 10.1815, 5.50, 'Clay', 'Drip Irrigation', NOW(), NOW()),
    ('South Orchard', 'Tunis South', 36.7538, 10.2275, 3.20, 'Sandy Loam', 'Sprinkler', NOW(), NOW()),
    ('East Vineyard', 'Ariana', 36.8625, 10.1956, 4.75, 'Loamy', 'Drip Irrigation', NOW(), NOW()),
    ('West Garden', 'Ben Arous', 36.7489, 10.2302, 2.80, 'Clay Loam', 'Manual', NOW(), NOW()),
    ('Central Farm', 'Manouba', 36.8090, 10.0965, 10.00, 'Silty Clay', 'Center Pivot', NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Insert sample crops
INSERT INTO crops (parcel_id, crop_type, variety, planting_date, expected_harvest_date, growth_stage, status, created_at, updated_at)
VALUES
    (1, 'Wheat', 'Durum', '2024-11-01', '2025-06-15', 'Vegetative', 'Active', NOW(), NOW()),
    (1, 'Barley', 'Spring', '2024-10-15', '2025-05-20', 'Germination', 'Active', NOW(), NOW()),
    (2, 'Olive', 'Chetoui', '2023-03-01', '2024-11-30', 'Fruiting', 'Active', NOW(), NOW()),
    (3, 'Grape', 'Muscat', '2022-04-01', '2024-09-15', 'Ripening', 'Harvested', NOW(), NOW()),
    (4, 'Tomato', 'San Marzano', '2024-09-01', '2025-01-15', 'Flowering', 'Active', NOW(), NOW()),
    (5, 'Corn', 'Sweet Corn', '2024-08-15', '2024-12-20', 'Mature', 'Active', NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Insert sample harvests
INSERT INTO harvests (crop_id, harvest_date, quantity, quality_grade, notes, created_at)
VALUES
    (4, '2024-09-15', 8.50, 'A', 'Excellent quality grapes, perfect ripeness', NOW()),
    (4, '2024-09-20', 7.20, 'A', 'Second harvest, very good quality', NOW()),
    (3, '2023-12-01', 12.30, 'B+', 'Good olive harvest, some weather impact', NOW()),
    (3, '2024-11-28', 13.50, 'A', 'Excellent olive harvest this year', NOW())
ON CONFLICT DO NOTHING;