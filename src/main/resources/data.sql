INSERT INTO director (nombre, nacionalidad, nacimiento) VALUES
('James Wan', 'Australiano', '1977-02-26'),
('Christopher Nolan', 'Británico', '1970-07-30'),
('Patty Jenkins', 'Estadounidense', '1971-07-24'),
('Denis Villeneuve', 'Canadiense', '1967-10-03'),
('Taika Waititi', 'Neozelandés', '1975-08-16'),
('Sofia Coppola', 'Estadounidense', '1971-05-14'),
('Bong Joon-ho', 'Coreano', '1969-09-14'),
('Guillermo del Toro', 'Mexicano', '1964-10-09'),
('Peter Jackson', 'Neozelandés', '1961-10-31'),
('Wes Anderson', 'Estadounidense', '1969-05-01'),
('Greta Gerwig', 'Estadounidense', '1983-08-04'),
('Martin Scorsese', 'Estadounidense', '1942-11-17'),
('Ridley Scott', 'Británico', '1937-11-30'),
('Quentin Tarantino', 'Estadounidense', '1963-03-27'),
('Hayao Miyazaki', 'Japonés', '1941-01-05');

INSERT INTO pelicula (titulo, duracion, id_director) VALUES
('Insidious', '01:43:00', 1),
('Inception', '02:28:00', 2),
('Wonder Woman', '02:21:00', 3),
('Dune', '02:35:00', 4),
('Thor Ragnarok', '02:11:00', 5),
('Lost in Translation', '01:42:00', 6),
('Parasite', '02:12:00', 7),
('El Laberinto del Fauno', '01:58:00', 8),
('El Hobbit', '02:49:00', 9),
('Isle of Dogs', '01:41:00', 10),
('Barbie', '01:54:00', 11),
('Shutter Island', '02:18:00', 12),
('Gladiator', '02:35:00', 13),
('Pulp Fiction', '02:34:00', 14),
('Spirited Away', '02:05:00', 15),
('Kill Bill', '01:51:00', 14),
('The Departed', '02:31:00', 12);

INSERT INTO sala (numero, capacidad) VALUES
('1', 120), ('2', 100), ('3', 150), ('4', 80), ('5', 60),
('6', 200), ('7', 90), ('8', 140), ('9', 160), ('10', 110);

INSERT INTO trabajador (nombre, telefono, correo, id_sala) VALUES
('Ana Ruiz', '612345678', 'ana@sala1.com', 1),
('Marcos Gil', '623456789', 'marcos@sala2.com', 2),
('Lucia Pons', '634567890', 'lucia@sala3.com', 3),
('Pedro León', '645678901', 'pedro@sala4.com', 4),
('Sara Mora', '656789012', 'sara@sala5.com', 5),
('Nuria Roldán', '667890123', 'nuria@sala6.com', 6),
('Ismael Torre', '678901234', 'ismael@sala7.com', 7),
('Eva Marín', '689012345', 'eva@sala8.com', 8),
('Raul Pardo', '690123456', 'raul@sala9.com', 9),
('Celia Costa', '601234567', 'celia@sala10.com', 10);

INSERT INTO cliente (email, nombre) VALUES
('cliente1@example.com', 'Juan Pérez'),
('cliente2@example.com', 'María López'),
('cliente3@example.com', 'Carlos Ruiz'),
('cliente4@example.com', 'Laura Gómez'),
('cliente5@example.com', 'Jorge Sánchez');

INSERT INTO funcion (code, id_sala, id_pelicula, horario) VALUES
('F001', 1, 1, '16:00:00'),
('F002', 1, 2, '19:00:00'),
('F003', 2, 3, '17:00:00');

INSERT INTO roles (id, name) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_MANAGER'),
(3, 'ROLE_USER');

INSERT INTO users (id, username, password, enabled, first_name, last_name, created_date, last_modified_date, last_password_change_date)
VALUES
(1, 'admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMpJ.z6yQMC6', true, 'Admin', 'User', NOW(), NOW(), NOW()),
(2, 'manager', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMpJ.z6yQMC6', true, 'Manager', 'User', NOW(), NOW(), NOW()),
(3, 'normal', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMpJ.z6yQMC6', true, 'Regular', 'User',  NOW(), NOW(), NOW()),
(4, 'ElDiavloLoKoTV', '', true, 'ElDiavloLoKoTV', 'Discord', NOW(), NOW(), NULL),
(5, 'jaimeramirezmuela@gmail.com', '', true, 'Jaime', 'Ramirez', NOW(), NOW(), NOW()),
(6, 'Daniel-goga-GIT', '', true, 'Daniel', 'Gonzalez', NOW(), NOW(), NOW());

INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 2),
(5, 2),
(6, 2);