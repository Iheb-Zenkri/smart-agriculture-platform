INSERT INTO irrigation_recommendations (parcel_id, recommendation_date, water_amount, irrigation_frequency,
                                       optimal_time, reasoning, confidence_score, based_on_temperature,
                                       based_on_humidity, based_on_precipitation, created_at)
VALUES
    (1, CURRENT_DATE, 12.5, 'Every 2 days', 'Early morning (6-8 AM)',
     'Based on weather: moderate temperature and low precipitation', 0.85, 22.5, 55.0, 1.5, NOW()),
    (2, CURRENT_DATE, 8.0, 'Every 3 days', 'Early morning (6-8 AM)',
     'Recent rainfall reduces irrigation needs', 0.90, 20.0, 65.0, 8.0, NOW())
ON CONFLICT DO NOTHING;

-- Sample Crop Plans
INSERT INTO crop_plans (parcel_id, recommended_crop, recommended_variety, planting_date,
                       expected_harvest_date, expected_yield, confidence_score, reasoning,
                       soil_suitability, climate_suitability, created_at)
VALUES
    (1, 'Wheat', 'Durum', CURRENT_DATE + INTERVAL '30 days', CURRENT_DATE + INTERVAL '150 days',
     8.5, 0.80, 'Wheat is well-suited for clay soil in this climate', 'Good', 'Excellent', NOW()),
    (2, 'Tomato', 'San Marzano', CURRENT_DATE + INTERVAL '45 days', CURRENT_DATE + INTERVAL '120 days',
     12.0, 0.75, 'Sandy loam soil is ideal for tomatoes', 'Excellent', 'Good', NOW())
ON CONFLICT DO NOTHING;