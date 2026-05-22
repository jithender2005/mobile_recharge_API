-- Users (passwords are BCrypt hashed)

-- admin / admin123
INSERT INTO users (username, password, role, operator, created_at)
VALUES ('admin', '$2a$10$cr5H0qpawejb01X88PIsde8d20wtGXSNkRcRSWwK2H9SOW0uaeScq', 'ADMIN', null, NOW())
ON CONFLICT (username) DO NOTHING;

-- jio_dealer / jio123
INSERT INTO users (username, password, role, operator, created_at)
VALUES ('jio_dealer', '$2a$10$ifOJckDZrVXyLTaCA3QZo.EjKIzCLFYRvyybaTpvB5fywkpz4teya', 'DEALER', 'JIO', NOW())
ON CONFLICT (username) DO NOTHING;

-- airtel_dealer / airtel123
INSERT INTO users (username, password, role, operator, created_at)
VALUES ('airtel_dealer', '$2a$10$jLl18lznOi8fDwkq9jEG6OlQAtL4ZlQ/MHOuNqVaTfy6IG88HN6ri', 'DEALER', 'AIRTEL', NOW())
ON CONFLICT (username) DO NOTHING;

-- bsnl_dealer / bsnl123
INSERT INTO users (username, password, role, operator, created_at)
VALUES ('bsnl_dealer', '$2a$10$/o6hNeQwH85RwWeK0CsSWexAdFjBVqtKjh5f02m62oatnj.icXuCK', 'DEALER', 'BSNL', NOW())
ON CONFLICT (username) DO NOTHING;

-- vi_dealer / vi123
INSERT INTO users (username, password, role, operator, created_at)
VALUES ('vi_dealer', '$2a$10$uGIi.jbt9lCpeNbY33X.NuEqLAAyi1rCMMfOn3FYM4zAIGZ0IZomS', 'DEALER', 'VI', NOW())
ON CONFLICT (username) DO NOTHING;
