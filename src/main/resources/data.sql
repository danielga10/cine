-- ===============================
-- INSERTS EN DIRECTOR
-- ===============================
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

-- ===============================
-- INSERTS EN PELICULA
-- ===============================
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

-- ===============================
-- INSERTS EN SALA
-- ===============================
INSERT INTO sala (numero, capacidad) VALUES
('1', 120),
('2', 100),
('3', 150),
('4', 80),
('5', 60),
('6', 200),
('7', 90),
('8', 140),
('9', 160),
('10', 110);


-- ===============================
-- INSERTS EN TRABAJADOR
-- ===============================
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

-- ===============================
-- INSERTS EN CLIENTE
-- ===============================
INSERT INTO cliente (email, nombre) VALUES
('cliente1@example.com', 'Juan Pérez'),
('cliente2@example.com', 'María López'),
('cliente3@example.com', 'Carlos Ruiz'),
('cliente4@example.com', 'Laura Gómez'),
('cliente5@example.com', 'Jorge Sánchez'),
('cliente6@example.com', 'Esther Molina'),
('cliente7@example.com', 'Samuel Torres'),
('cliente8@example.com', 'Paula Díaz'),
('cliente9@example.com', 'Marta León'),
('cliente10@example.com', 'David Romero'),
('cliente11@example.com', 'Sergio Duarte'),
('cliente12@example.com', 'Rosa Villalba'),
('cliente13@example.com', 'Adrián Rico'),
('cliente14@example.com', 'Helena Torres'),
('cliente15@example.com', 'Iván Morales'),
('cliente16@example.com', 'Nadia Campos'),
('cliente17@example.com', 'Rubén Castillo'),
('cliente18@example.com', 'Alicia Ramos'),
('cliente19@example.com', 'Tomás Vera'),
('cliente20@example.com', 'Patricia Soler'),
('cliente21@example.com', 'Diego Navarro'),
('cliente22@example.com', 'Inés Romero'),
('cliente23@example.com', 'Gustavo Ríos'),
('cliente24@example.com', 'Nora Jiménez'),
('cliente25@example.com', 'Óscar Rey');

-- ===============================
-- INSERTS EN FUNCION
-- ===============================
INSERT INTO funcion (code, id_sala, id_pelicula, horario) VALUES
('F001', 1, 1, '16:00:00'),
('F002', 1, 2, '19:00:00'),
('F003', 2, 3, '17:00:00'),
('F004', 2, 4, '20:00:00'),
('F005', 3, 5, '15:00:00'),
('F006', 3, 6, '18:00:00'),
('F007', 4, 7, '16:30:00'),
('F008', 4, 1, '19:30:00'),
('F009', 5, 2, '17:15:00'),
('F010', 5, 3, '20:15:00'),
('F011', 6, 8,  '16:00:00'),
('F012', 6, 9,  '19:00:00'),
('F013', 7, 10, '17:00:00'),
('F014', 7, 11, '20:00:00'),
('F015', 8, 12, '15:30:00'),
('F016', 8, 13, '18:30:00'),
('F017', 9, 14, '16:45:00'),
('F018', 9, 15, '19:45:00'),
('F019', 10, 16, '17:20:00'),
('F020', 10, 17, '20:20:00'),
('F021', 1, 8,  '22:00:00'),
('F022', 2, 9,  '21:00:00'),
('F023', 3, 10, '22:15:00'),
('F024', 4, 11, '21:45:00'),
('F025', 5, 12, '22:30:00'),
('F026', 6, 13, '21:10:00'),
('F027', 7, 14, '22:40:00'),
('F028', 8, 15, '23:00:00'),
('F029', 9, 16, '21:55:00'),
('F030', 10, 17, '22:50:00');

-- ===============================
-- INSERTS EN BOLETO
-- ===============================
INSERT INTO boleto (code, asiento, precio, id_cliente, id_funcion) VALUES
('B001', 'A1', 7.50, 1, 1),
('B002', 'A2', 7.50, 2, 1),
('B003', 'B5', 8.00, 3, 2),
('B004', 'C3', 6.50, 4, 3),
('B005', 'D7', 9.00, 5, 4),
('B006', 'E9', 6.00, 6, 5),
('B007', 'F4', 7.50, 7, 6),
('B008', 'A10', 8.50, 8, 7),
('B009', 'B12', 7.00, 9, 8),
('B010', 'C8', 7.50, 10, 9),
('B011', 'A3', 7.00, 11, 11),
('B012', 'A4', 7.00, 12, 11),
('B013', 'B2', 8.50, 13, 12),
('B014', 'C1', 6.50, 14, 13),
('B015', 'D3', 9.20, 15, 14),
('B016', 'E4', 7.90, 16, 15),
('B017', 'F6', 6.80, 17, 16),
('B018', 'A7', 8.20, 18, 17),
('B019', 'B8', 7.10, 19, 18),
('B020', 'C4', 7.60, 20, 19),
('B021', 'D9', 8.10, 21, 20),
('B022', 'A11', 7.40, 22, 21),
('B023', 'B10', 7.90, 23, 22),
('B024', 'C12', 6.70, 24, 23),
('B025', 'F3', 9.30, 25, 24),
('B026', 'G5', 8.10, 11, 25),
('B027', 'H1', 6.90, 12, 26),
('B028', 'I2', 7.50, 13, 27),
('B029', 'J7', 7.20, 14, 28),
('B030', 'K4', 8.70, 15, 29),
('B031', 'L6', 6.90, 16, 30),
('B032', 'M2', 7.80, 17, 22),
('B033', 'N3', 8.10, 18, 23),
('B034', 'O1', 7.40, 19, 24),
('B035', 'P2', 6.80, 20, 25),
('B036', 'Q8', 7.90, 21, 26),
('B037', 'R4', 8.50, 22, 27),
('B038', 'S6', 6.90, 23, 28),
('B039', 'T1', 8.30, 24, 29),
('B040', 'U3', 7.60, 25, 30);