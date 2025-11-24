INSERT INTO director (nombre, nacionalidad, nacimiento) VALUES
('Christopher Nolan', 'Británico', '1970-07-30'),
('Quentin Tarantino', 'Estadounidense', '1963-03-27'),
('Hayao Miyazaki', 'Japonés', '1941-01-05'),
('Patty Jenkins', 'Estadounidense', '1971-07-24'),
('Alfonso Cuarón', 'Mexicano', '1961-11-28');
INSERT INTO pelicula (titulo, duracion, id_director) VALUES
('Inception', '02:28:00', 1),
('Pulp Fiction', '02:34:00', 2),
('Spirited Away', '02:05:00', 3),
('Wonder Woman', '02:21:00', 4),
('Gravity', '01:31:00', 5);
INSERT INTO sala (numero, capacidad, id_pelicula) VALUES
(1, 120, 1),
(2, 150, 2),
(3, 100, 3),
(4, 140, 4),
(5, 160, 5);
INSERT INTO trabajador (nombre, telefono, correo, id_sala) VALUES ('Ana Lopez', '600000001', 'ana@sala1.com', 1);
INSERT INTO trabajador (nombre, telefono, correo, id_sala) VALUES ('Carlos Perez', '600000002', 'carlos@sala2.com', 2);
INSERT INTO trabajador (nombre, telefono, correo, id_sala) VALUES ('Maria Diaz', '600000003', 'maria@sala3.com', 3);
INSERT INTO trabajador (nombre, telefono, correo, id_sala) VALUES ('David Ruiz', '600000004', 'david@sala4.com', 4);
INSERT INTO trabajador (nombre, telefono, correo, id_sala) VALUES ('Laura Garcia', '600000005', 'laura@sala5.com', 5);

INSERT INTO cliente (email, nombre) VALUES
('cliente1@mail.com', 'Juan Torres'),
('cliente2@mail.com', 'Lucia Ramos'),
('cliente3@mail.com', 'Pedro Gomez'),
('cliente4@mail.com', 'Sara Mendez'),
('cliente5@mail.com', 'Elena Castro');
INSERT INTO funcion (id_sala, id_pelicula, horario) VALUES
(1, 1, '15:00:00'),
(2, 2, '17:00:00'),
(3, 3, '18:30:00'),
(4, 4, '20:00:00'),
(5, 5, '21:30:00');
INSERT INTO boleto (asiento, precio, id_cliente, id_funcion, horario) VALUES
('A1', 8.50, 1, 1, '15:00:00'),
('B3', 9.00, 2, 2, '17:00:00'),
('C2', 7.50, 3, 3, '18:30:00'),
('D4', 9.20, 4, 4, '20:00:00'),
('E5', 8.80, 5, 5, '21:30:00');
