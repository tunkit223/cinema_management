-- Sample Revenue Data for December 2025 (28 days)
-- Replace cinema_id_1, cinema_id_2 with actual cinema IDs from your database
-- Replace movie_id_1, movie_id_2, movie_id_3 with actual movie IDs from your database

-- Variables (you need to replace these with actual IDs)
-- @cinema_id_1 = 'your-cinema-id-1'
-- @cinema_id_2 = 'your-cinema-id-2'
-- @movie_id_1 = 'your-movie-id-1'
-- @movie_id_2 = 'your-movie-id-2'
-- @movie_id_3 = 'your-movie-id-3'

-- ============================================
-- DAILY REVENUE SUMMARY (Cinema 1)
-- ============================================
INSERT INTO daily_revenue_summary (id, cinema_id, report_date, ticket_revenue, combo_revenue, net_revenue, total_transactions) VALUES
(gen_random_uuid(), '295c4325-e13d-4ccb-a532-d7d6255225fd', '2025-12-01', 2500000, 800000, 3300000, 12),
(gen_random_uuid(), '295c4325-e13d-4ccb-a532-d7d6255225fd', '2025-12-02', 2800000, 950000, 3750000, 14),
(gen_random_uuid(), '295c4325-e13d-4ccb-a532-d7d6255225fd', '2025-12-03', 3200000, 1100000, 4300000, 16),
(gen_random_uuid(), '295c4325-e13d-4ccb-a532-d7d6255225fd', '2025-12-04', 3500000, 1200000, 4700000, 18),
(gen_random_uuid(), '295c4325-e13d-4ccb-a532-d7d6255225fd', '2025-12-05', 4000000, 1400000, 5400000, 20),
(gen_random_uuid(), '295c4325-e13d-4ccb-a532-d7d6255225fd', '2025-12-06', 5200000, 1800000, 7000000, 28),
(gen_random_uuid(), '295c4325-e13d-4ccb-a532-d7d6255225fd', '2025-12-07', 5800000, 2000000, 7800000, 32),
(gen_random_uuid(), '295c4325-e13d-4ccb-a532-d7d6255225fd', '2025-12-08', 4500000, 1500000, 6000000, 24),
(gen_random_uuid(), '295c4325-e13d-4ccb-a532-d7d6255225fd', '2025-12-09', 3000000, 1000000, 4000000, 15),
(gen_random_uuid(), '295c4325-e13d-4ccb-a532-d7d6255225fd', '2025-12-10', 3300000, 1100000, 4400000, 17),
(gen_random_uuid(), '295c4325-e13d-4ccb-a532-d7d6255225fd', '2025-12-11', 3600000, 1200000, 4800000, 19),
(gen_random_uuid(), '295c4325-e13d-4ccb-a532-d7d6255225fd', '2025-12-12', 3900000, 1300000, 5200000, 21),
(gen_random_uuid(), '295c4325-e13d-4ccb-a532-d7d6255225fd', '2025-12-13', 5000000, 1700000, 6700000, 26),
(gen_random_uuid(), '295c4325-e13d-4ccb-a532-d7d6255225fd', '2025-12-14', 6000000, 2100000, 8100000, 34),
(gen_random_uuid(), '295c4325-e13d-4ccb-a532-d7d6255225fd', '2025-12-15', 4200000, 1400000, 5600000, 22),
(gen_random_uuid(), '295c4325-e13d-4ccb-a532-d7d6255225fd', '2025-12-16', 3100000, 1050000, 4150000, 16),
(gen_random_uuid(), '295c4325-e13d-4ccb-a532-d7d6255225fd', '2025-12-17', 3400000, 1150000, 4550000, 18),
(gen_random_uuid(), '295c4325-e13d-4ccb-a532-d7d6255225fd', '2025-12-18', 3700000, 1250000, 4950000, 20),
(gen_random_uuid(), '295c4325-e13d-4ccb-a532-d7d6255225fd', '2025-12-19', 4100000, 1450000, 5550000, 23),
(gen_random_uuid(), '295c4325-e13d-4ccb-a532-d7d6255225fd', '2025-12-20', 5500000, 1900000, 7400000, 30),
(gen_random_uuid(), '295c4325-e13d-4ccb-a532-d7d6255225fd', '2025-12-21', 6200000, 2200000, 8400000, 36),
(gen_random_uuid(), '295c4325-e13d-4ccb-a532-d7d6255225fd', '2025-12-22', 4800000, 1600000, 6400000, 25),
(gen_random_uuid(), '295c4325-e13d-4ccb-a532-d7d6255225fd', '2025-12-23', 3200000, 1100000, 4300000, 17),
(gen_random_uuid(), '295c4325-e13d-4ccb-a532-d7d6255225fd', '2025-12-24', 5800000, 2000000, 7800000, 32),
(gen_random_uuid(), '295c4325-e13d-4ccb-a532-d7d6255225fd', '2025-12-25', 7000000, 2500000, 9500000, 42),
(gen_random_uuid(), '295c4325-e13d-4ccb-a532-d7d6255225fd', '2025-12-26', 6500000, 2300000, 8800000, 38),
(gen_random_uuid(), '295c4325-e13d-4ccb-a532-d7d6255225fd', '2025-12-27', 5000000, 1700000, 6700000, 28),
(gen_random_uuid(), '295c4325-e13d-4ccb-a532-d7d6255225fd', '2025-12-28', 4500000, 1500000, 6000000, 24);

-- ============================================
-- DAILY REVENUE SUMMARY (Cinema 2)
-- ============================================
INSERT INTO daily_revenue_summary (id, cinema_id, report_date, ticket_revenue, combo_revenue, net_revenue, total_transactions) VALUES
(UUID(), 'cinema_id_2', '2025-12-01', 1800000, 600000, 2400000, 10),
(UUID(), 'cinema_id_2', '2025-12-02', 2000000, 700000, 2700000, 11),
(UUID(), 'cinema_id_2', '2025-12-03', 2300000, 800000, 3100000, 13),
(UUID(), 'cinema_id_2', '2025-12-04', 2500000, 900000, 3400000, 14),
(UUID(), 'cinema_id_2', '2025-12-05', 2800000, 1000000, 3800000, 16),
(UUID(), 'cinema_id_2', '2025-12-06', 3800000, 1300000, 5100000, 22),
(UUID(), 'cinema_id_2', '2025-12-07', 4200000, 1500000, 5700000, 24),
(UUID(), 'cinema_id_2', '2025-12-08', 3200000, 1100000, 4300000, 18),
(UUID(), 'cinema_id_2', '2025-12-09', 2100000, 750000, 2850000, 12),
(UUID(), 'cinema_id_2', '2025-12-10', 2400000, 850000, 3250000, 13),
(UUID(), 'cinema_id_2', '2025-12-11', 2600000, 950000, 3550000, 15),
(UUID(), 'cinema_id_2', '2025-12-12', 2900000, 1050000, 3950000, 17),
(UUID(), 'cinema_id_2', '2025-12-13', 3600000, 1250000, 4850000, 20),
(UUID(), 'cinema_id_2', '2025-12-14', 4400000, 1600000, 6000000, 26),
(UUID(), 'cinema_id_2', '2025-12-15', 3000000, 1050000, 4050000, 16),
(UUID(), 'cinema_id_2', '2025-12-16', 2200000, 800000, 3000000, 13),
(UUID(), 'cinema_id_2', '2025-12-17', 2500000, 900000, 3400000, 14),
(UUID(), 'cinema_id_2', '2025-12-18', 2700000, 950000, 3650000, 15),
(UUID(), 'cinema_id_2', '2025-12-19', 3000000, 1100000, 4100000, 18),
(UUID(), 'cinema_id_2', '2025-12-20', 4000000, 1450000, 5450000, 23),
(UUID(), 'cinema_id_2', '2025-12-21', 4500000, 1650000, 6150000, 27),
(UUID(), 'cinema_id_2', '2025-12-22', 3500000, 1200000, 4700000, 19),
(UUID(), 'cinema_id_2', '2025-12-23', 2300000, 850000, 3150000, 14),
(UUID(), 'cinema_id_2', '2025-12-24', 4200000, 1500000, 5700000, 25),
(UUID(), 'cinema_id_2', '2025-12-25', 5000000, 1800000, 6800000, 32),
(UUID(), 'cinema_id_2', '2025-12-26', 4700000, 1700000, 6400000, 29),
(UUID(), 'cinema_id_2', '2025-12-27', 3600000, 1300000, 4900000, 21),
(UUID(), 'cinema_id_2', '2025-12-28', 3200000, 1150000, 4350000, 18);

-- ============================================
-- MOVIE REVENUE (Movie 1 - Cinema 1)
-- ============================================
INSERT INTO movie_revenue (id, movie_id, cinema_id, report_date, total_tickets_sold, total_revenue) VALUES
(UUID(), 'movie_id_1', 'cinema_id_1', '2025-12-01', 25, 1250000),
(UUID(), 'movie_id_1', 'cinema_id_1', '2025-12-02', 28, 1400000),
(UUID(), 'movie_id_1', 'cinema_id_1', '2025-12-03', 32, 1600000),
(UUID(), 'movie_id_1', 'cinema_id_1', '2025-12-04', 35, 1750000),
(UUID(), 'movie_id_1', 'cinema_id_1', '2025-12-05', 40, 2000000),
(UUID(), 'movie_id_1', 'cinema_id_1', '2025-12-06', 52, 2600000),
(UUID(), 'movie_id_1', 'cinema_id_1', '2025-12-07', 58, 2900000),
(UUID(), 'movie_id_1', 'cinema_id_1', '2025-12-08', 45, 2250000),
(UUID(), 'movie_id_1', 'cinema_id_1', '2025-12-09', 30, 1500000),
(UUID(), 'movie_id_1', 'cinema_id_1', '2025-12-10', 33, 1650000),
(UUID(), 'movie_id_1', 'cinema_id_1', '2025-12-11', 36, 1800000),
(UUID(), 'movie_id_1', 'cinema_id_1', '2025-12-12', 39, 1950000),
(UUID(), 'movie_id_1', 'cinema_id_1', '2025-12-13', 50, 2500000),
(UUID(), 'movie_id_1', 'cinema_id_1', '2025-12-14', 60, 3000000),
(UUID(), 'movie_id_1', 'cinema_id_1', '2025-12-15', 42, 2100000),
(UUID(), 'movie_id_1', 'cinema_id_1', '2025-12-16', 31, 1550000),
(UUID(), 'movie_id_1', 'cinema_id_1', '2025-12-17', 34, 1700000),
(UUID(), 'movie_id_1', 'cinema_id_1', '2025-12-18', 37, 1850000),
(UUID(), 'movie_id_1', 'cinema_id_1', '2025-12-19', 41, 2050000),
(UUID(), 'movie_id_1', 'cinema_id_1', '2025-12-20', 55, 2750000),
(UUID(), 'movie_id_1', 'cinema_id_1', '2025-12-21', 62, 3100000),
(UUID(), 'movie_id_1', 'cinema_id_1', '2025-12-22', 48, 2400000),
(UUID(), 'movie_id_1', 'cinema_id_1', '2025-12-23', 32, 1600000),
(UUID(), 'movie_id_1', 'cinema_id_1', '2025-12-24', 58, 2900000),
(UUID(), 'movie_id_1', 'cinema_id_1', '2025-12-25', 70, 3500000),
(UUID(), 'movie_id_1', 'cinema_id_1', '2025-12-26', 65, 3250000),
(UUID(), 'movie_id_1', 'cinema_id_1', '2025-12-27', 50, 2500000),
(UUID(), 'movie_id_1', 'cinema_id_1', '2025-12-28', 45, 2250000);

-- ============================================
-- MOVIE REVENUE (Movie 2 - Cinema 1)
-- ============================================
INSERT INTO movie_revenue (id, movie_id, cinema_id, report_date, total_tickets_sold, total_revenue) VALUES
(UUID(), 'movie_id_2', 'cinema_id_1', '2025-12-01', 20, 1000000),
(UUID(), 'movie_id_2', 'cinema_id_1', '2025-12-02', 23, 1150000),
(UUID(), 'movie_id_2', 'cinema_id_1', '2025-12-03', 26, 1300000),
(UUID(), 'movie_id_2', 'cinema_id_1', '2025-12-04', 29, 1450000),
(UUID(), 'movie_id_2', 'cinema_id_1', '2025-12-05', 33, 1650000),
(UUID(), 'movie_id_2', 'cinema_id_1', '2025-12-06', 43, 2150000),
(UUID(), 'movie_id_2', 'cinema_id_1', '2025-12-07', 48, 2400000),
(UUID(), 'movie_id_2', 'cinema_id_1', '2025-12-08', 37, 1850000),
(UUID(), 'movie_id_2', 'cinema_id_1', '2025-12-09', 25, 1250000),
(UUID(), 'movie_id_2', 'cinema_id_1', '2025-12-10', 27, 1350000),
(UUID(), 'movie_id_2', 'cinema_id_1', '2025-12-11', 30, 1500000),
(UUID(), 'movie_id_2', 'cinema_id_1', '2025-12-12', 32, 1600000),
(UUID(), 'movie_id_2', 'cinema_id_1', '2025-12-13', 41, 2050000),
(UUID(), 'movie_id_2', 'cinema_id_1', '2025-12-14', 50, 2500000),
(UUID(), 'movie_id_2', 'cinema_id_1', '2025-12-15', 35, 1750000),
(UUID(), 'movie_id_2', 'cinema_id_1', '2025-12-16', 26, 1300000),
(UUID(), 'movie_id_2', 'cinema_id_1', '2025-12-17', 28, 1400000),
(UUID(), 'movie_id_2', 'cinema_id_1', '2025-12-18', 31, 1550000),
(UUID(), 'movie_id_2', 'cinema_id_1', '2025-12-19', 34, 1700000),
(UUID(), 'movie_id_2', 'cinema_id_1', '2025-12-20', 45, 2250000),
(UUID(), 'movie_id_2', 'cinema_id_1', '2025-12-21', 51, 2550000),
(UUID(), 'movie_id_2', 'cinema_id_1', '2025-12-22', 40, 2000000),
(UUID(), 'movie_id_2', 'cinema_id_1', '2025-12-23', 27, 1350000),
(UUID(), 'movie_id_2', 'cinema_id_1', '2025-12-24', 48, 2400000),
(UUID(), 'movie_id_2', 'cinema_id_1', '2025-12-25', 58, 2900000),
(UUID(), 'movie_id_2', 'cinema_id_1', '2025-12-26', 54, 2700000),
(UUID(), 'movie_id_2', 'cinema_id_1', '2025-12-27', 42, 2100000),
(UUID(), 'movie_id_2', 'cinema_id_1', '2025-12-28', 37, 1850000);

-- ============================================
-- MOVIE REVENUE (Movie 3 - Cinema 1)
-- ============================================
INSERT INTO movie_revenue (id, movie_id, cinema_id, report_date, total_tickets_sold, total_revenue) VALUES
(UUID(), 'movie_id_3', 'cinema_id_1', '2025-12-01', 10, 250000),
(UUID(), 'movie_id_3', 'cinema_id_1', '2025-12-02', 11, 280000),
(UUID(), 'movie_id_3', 'cinema_id_1', '2025-12-03', 13, 320000),
(UUID(), 'movie_id_3', 'cinema_id_1', '2025-12-04', 14, 350000),
(UUID(), 'movie_id_3', 'cinema_id_1', '2025-12-05', 16, 400000),
(UUID(), 'movie_id_3', 'cinema_id_1', '2025-12-06', 21, 520000),
(UUID(), 'movie_id_3', 'cinema_id_1', '2025-12-07', 23, 580000),
(UUID(), 'movie_id_3', 'cinema_id_1', '2025-12-08', 18, 450000),
(UUID(), 'movie_id_3', 'cinema_id_1', '2025-12-09', 12, 300000),
(UUID(), 'movie_id_3', 'cinema_id_1', '2025-12-10', 13, 330000),
(UUID(), 'movie_id_3', 'cinema_id_1', '2025-12-11', 14, 360000),
(UUID(), 'movie_id_3', 'cinema_id_1', '2025-12-12', 16, 390000),
(UUID(), 'movie_id_3', 'cinema_id_1', '2025-12-13', 20, 500000),
(UUID(), 'movie_id_3', 'cinema_id_1', '2025-12-14', 24, 600000),
(UUID(), 'movie_id_3', 'cinema_id_1', '2025-12-15', 17, 420000),
(UUID(), 'movie_id_3', 'cinema_id_1', '2025-12-16', 12, 310000),
(UUID(), 'movie_id_3', 'cinema_id_1', '2025-12-17', 14, 340000),
(UUID(), 'movie_id_3', 'cinema_id_1', '2025-12-18', 15, 370000),
(UUID(), 'movie_id_3', 'cinema_id_1', '2025-12-19', 17, 410000),
(UUID(), 'movie_id_3', 'cinema_id_1', '2025-12-20', 22, 550000),
(UUID(), 'movie_id_3', 'cinema_id_1', '2025-12-21', 25, 620000),
(UUID(), 'movie_id_3', 'cinema_id_1', '2025-12-22', 19, 480000),
(UUID(), 'movie_id_3', 'cinema_id_1', '2025-12-23', 13, 320000),
(UUID(), 'movie_id_3', 'cinema_id_1', '2025-12-24', 23, 580000),
(UUID(), 'movie_id_3', 'cinema_id_1', '2025-12-25', 28, 700000),
(UUID(), 'movie_id_3', 'cinema_id_1', '2025-12-26', 26, 650000),
(UUID(), 'movie_id_3', 'cinema_id_1', '2025-12-27', 20, 500000),
(UUID(), 'movie_id_3', 'cinema_id_1', '2025-12-28', 18, 450000);

-- ============================================
-- MOVIE REVENUE (Movie 1 - Cinema 2)
-- ============================================
INSERT INTO movie_revenue (id, movie_id, cinema_id, report_date, total_tickets_sold, total_revenue) VALUES
(UUID(), 'movie_id_1', 'cinema_id_2', '2025-12-01', 18, 900000),
(UUID(), 'movie_id_1', 'cinema_id_2', '2025-12-02', 20, 1000000),
(UUID(), 'movie_id_1', 'cinema_id_2', '2025-12-03', 23, 1150000),
(UUID(), 'movie_id_1', 'cinema_id_2', '2025-12-04', 25, 1250000),
(UUID(), 'movie_id_1', 'cinema_id_2', '2025-12-05', 28, 1400000),
(UUID(), 'movie_id_1', 'cinema_id_2', '2025-12-06', 38, 1900000),
(UUID(), 'movie_id_1', 'cinema_id_2', '2025-12-07', 42, 2100000),
(UUID(), 'movie_id_1', 'cinema_id_2', '2025-12-08', 32, 1600000),
(UUID(), 'movie_id_1', 'cinema_id_2', '2025-12-09', 21, 1050000),
(UUID(), 'movie_id_1', 'cinema_id_2', '2025-12-10', 24, 1200000),
(UUID(), 'movie_id_1', 'cinema_id_2', '2025-12-11', 26, 1300000),
(UUID(), 'movie_id_1', 'cinema_id_2', '2025-12-12', 29, 1450000),
(UUID(), 'movie_id_1', 'cinema_id_2', '2025-12-13', 36, 1800000),
(UUID(), 'movie_id_1', 'cinema_id_2', '2025-12-14', 44, 2200000),
(UUID(), 'movie_id_1', 'cinema_id_2', '2025-12-15', 30, 1500000),
(UUID(), 'movie_id_1', 'cinema_id_2', '2025-12-16', 22, 1100000),
(UUID(), 'movie_id_1', 'cinema_id_2', '2025-12-17', 25, 1250000),
(UUID(), 'movie_id_1', 'cinema_id_2', '2025-12-18', 27, 1350000),
(UUID(), 'movie_id_1', 'cinema_id_2', '2025-12-19', 30, 1500000),
(UUID(), 'movie_id_1', 'cinema_id_2', '2025-12-20', 40, 2000000),
(UUID(), 'movie_id_1', 'cinema_id_2', '2025-12-21', 45, 2250000),
(UUID(), 'movie_id_1', 'cinema_id_2', '2025-12-22', 35, 1750000),
(UUID(), 'movie_id_1', 'cinema_id_2', '2025-12-23', 23, 1150000),
(UUID(), 'movie_id_1', 'cinema_id_2', '2025-12-24', 42, 2100000),
(UUID(), 'movie_id_1', 'cinema_id_2', '2025-12-25', 50, 2500000),
(UUID(), 'movie_id_1', 'cinema_id_2', '2025-12-26', 47, 2350000),
(UUID(), 'movie_id_1', 'cinema_id_2', '2025-12-27', 36, 1800000),
(UUID(), 'movie_id_1', 'cinema_id_2', '2025-12-28', 32, 1600000);

-- ============================================
-- MOVIE REVENUE (Movie 2 - Cinema 2)
-- ============================================
INSERT INTO movie_revenue (id, movie_id, cinema_id, report_date, total_tickets_sold, total_revenue) VALUES
(UUID(), 'movie_id_2', 'cinema_id_2', '2025-12-01', 15, 750000),
(UUID(), 'movie_id_2', 'cinema_id_2', '2025-12-02', 17, 850000),
(UUID(), 'movie_id_2', 'cinema_id_2', '2025-12-03', 19, 950000),
(UUID(), 'movie_id_2', 'cinema_id_2', '2025-12-04', 21, 1050000),
(UUID(), 'movie_id_2', 'cinema_id_2', '2025-12-05', 23, 1150000),
(UUID(), 'movie_id_2', 'cinema_id_2', '2025-12-06', 31, 1550000),
(UUID(), 'movie_id_2', 'cinema_id_2', '2025-12-07', 35, 1750000),
(UUID(), 'movie_id_2', 'cinema_id_2', '2025-12-08', 27, 1350000),
(UUID(), 'movie_id_2', 'cinema_id_2', '2025-12-09', 18, 900000),
(UUID(), 'movie_id_2', 'cinema_id_2', '2025-12-10', 20, 1000000),
(UUID(), 'movie_id_2', 'cinema_id_2', '2025-12-11', 22, 1100000),
(UUID(), 'movie_id_2', 'cinema_id_2', '2025-12-12', 24, 1200000),
(UUID(), 'movie_id_2', 'cinema_id_2', '2025-12-13', 30, 1500000),
(UUID(), 'movie_id_2', 'cinema_id_2', '2025-12-14', 37, 1850000),
(UUID(), 'movie_id_2', 'cinema_id_2', '2025-12-15', 25, 1250000),
(UUID(), 'movie_id_2', 'cinema_id_2', '2025-12-16', 18, 900000),
(UUID(), 'movie_id_2', 'cinema_id_2', '2025-12-17', 21, 1050000),
(UUID(), 'movie_id_2', 'cinema_id_2', '2025-12-18', 23, 1150000),
(UUID(), 'movie_id_2', 'cinema_id_2', '2025-12-19', 25, 1250000),
(UUID(), 'movie_id_2', 'cinema_id_2', '2025-12-20', 33, 1650000),
(UUID(), 'movie_id_2', 'cinema_id_2', '2025-12-21', 37, 1850000),
(UUID(), 'movie_id_2', 'cinema_id_2', '2025-12-22', 29, 1450000),
(UUID(), 'movie_id_2', 'cinema_id_2', '2025-12-23', 19, 950000),
(UUID(), 'movie_id_2', 'cinema_id_2', '2025-12-24', 35, 1750000),
(UUID(), 'movie_id_2', 'cinema_id_2', '2025-12-25', 42, 2100000),
(UUID(), 'movie_id_2', 'cinema_id_2', '2025-12-26', 39, 1950000),
(UUID(), 'movie_id_2', 'cinema_id_2', '2025-12-27', 30, 1500000),
(UUID(), 'movie_id_2', 'cinema_id_2', '2025-12-28', 27, 1350000);

-- ============================================
-- HOW TO USE THIS FILE:
-- ============================================
-- 1. First, get actual IDs from your database:
--    SELECT id, name FROM cinemas LIMIT 2;
--    SELECT id, title FROM movies LIMIT 3;
--
-- 2. Replace all occurrences of:
--    - 'cinema_id_1' with your actual first cinema ID
--    - 'cinema_id_2' with your actual second cinema ID  
--    - 'movie_id_1' with your actual first movie ID
--    - 'movie_id_2' with your actual second movie ID
--    - 'movie_id_3' with your actual third movie ID
--
-- 3. Run this SQL file in your MySQL database
--
-- Example using sed (Linux/Mac) or find-replace in text editor:
--    sed -i 's/cinema_id_1/actual-uuid-here/g' test_revenue_data.sql
--    sed -i 's/cinema_id_2/actual-uuid-here/g' test_revenue_data.sql
--    sed -i 's/movie_id_1/actual-uuid-here/g' test_revenue_data.sql
--    sed -i 's/movie_id_2/actual-uuid-here/g' test_revenue_data.sql
--    sed -i 's/movie_id_3/actual-uuid-here/g' test_revenue_data.sql
