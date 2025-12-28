-- Initialize default payment methods
INSERT INTO payment_methods (id, name, description, is_active, created_at)
VALUES 
  (gen_random_uuid(), 'VNPay', 'Vietnam Payment - VNPay Gateway', true, now()),
  (gen_random_uuid(), 'Bank Transfer', 'Direct bank transfer payment method', true, now()),
  (gen_random_uuid(), 'Credit Card', 'Credit card payment', true, now())
ON CONFLICT (name) DO NOTHING;
