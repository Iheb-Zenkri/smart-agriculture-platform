CREATE TABLE weather_data (
    id BIGSERIAL PRIMARY KEY,
    location VARCHAR(255) NOT NULL,
    latitude DECIMAL(9,6),
    longitude DECIMAL(9,6),
    date DATE NOT NULL,
    temperature_min DECIMAL(5,2),
    temperature_max DECIMAL(5,2),
    temperature_avg DECIMAL(5,2),
    humidity DECIMAL(5,2),
    precipitation DECIMAL(6,2),  -- in mm
    wind_speed DECIMAL(5,2),
    pressure DECIMAL(6,2),
    cloud_cover INTEGER,
    weather_condition VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(location, date)
);

-- Table: climate_indices
CREATE TABLE climate_indices (
    id BIGSERIAL PRIMARY KEY,
    location VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    growing_degree_days DECIMAL(6,2),
    evapotranspiration DECIMAL(6,2),
    drought_index DECIMAL(5,2),
    heat_stress_index DECIMAL(5,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_weather_location_date ON weather_data(location, date);
CREATE INDEX idx_climate_location ON climate_indices(location);

-- Insert sample weather data
INSERT INTO weather_data (location, latitude, longitude, date, temperature_min, temperature_max,
                         temperature_avg, humidity, precipitation, wind_speed, pressure,
                         weather_condition, cloud_cover, created_at)
VALUES
    ('Tunis North', 36.8065, 10.1815, '2024-11-20', 15.5, 24.3, 19.9, 65.0, 0.0, 12.5, 1013.2, 'Clear', 20, NOW()),
    ('Tunis North', 36.8065, 10.1815, '2024-11-21', 16.2, 25.1, 20.6, 62.0, 2.5, 15.3, 1012.8, 'Light Rain', 45, NOW()),
    ('Ariana', 36.8625, 10.1956, '2024-11-20', 14.8, 23.5, 19.1, 68.0, 0.0, 11.2, 1013.5, 'Clear', 15, NOW()),
    ('Ariana', 36.8625, 10.1956, '2024-11-21', 15.5, 24.2, 19.8, 65.0, 1.2, 13.5, 1013.0, 'Partly Cloudy', 35, NOW()),
    ('Ben Arous', 36.7489, 10.2302, '2024-11-20', 16.0, 25.0, 20.5, 60.0, 0.0, 10.8, 1013.0, 'Clear', 10, NOW()),
    ('Ben Arous', 36.7489, 10.2302, '2024-11-21', 16.8, 26.2, 21.5, 58.0, 0.0, 12.0, 1012.5, 'Sunny', 5, NOW())
ON CONFLICT DO NOTHING;

-- Insert sample climate indices
INSERT INTO climate_indices (location, date, growing_degree_days, evapotranspiration,
                            drought_index, heat_stress_index, created_at)
VALUES
    ('Tunis North', '2024-11-20', 9.9, 3.2, 0.15, 25.5, NOW()),
    ('Tunis North', '2024-11-21', 10.6, 3.5, 0.12, 26.8, NOW()),
    ('Ariana', '2024-11-20', 9.1, 3.0, 0.18, 24.2, NOW()),
    ('Ariana', '2024-11-21', 9.8, 3.2, 0.16, 25.1, NOW())
ON CONFLICT DO NOTHING;