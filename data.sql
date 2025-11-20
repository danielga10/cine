USE cine;

INSERT INTO director (nombre, nacionalidad, nacimiento) VALUES
('James Wan', 'Australia', '1977-02-26'),
('Greta Gerwig', 'Estados Unidos', '1983-08-04'),
('Christopher Nolan', 'Reino Unido', '1970-07-30'),
('Taika Waititi', 'Nueva Zelanda', '1975-08-16'),
('Denis Villeneuve', 'Canadá', '1967-10-03');

INSERT INTO pelicula (titulo, duracion, id_director) VALUES
('El Conjuro', '01:52:00', 1),
('Barbie', '01:54:00', 2),
('Inception', '02:28:00', 3),
('Jojo Rabbit', '01:48:00', 4),
('Dune', '02:35:00', 5);

INSERT INTO sala (numero, capacidad, id_pelicula) VALUES
(1, 120, 1),
(2, 150, 2),
(3, 200, 3),
(4, 130, 4),
(5, 180, 5);

INSERT INTO trabajador (nombre, telefono, correo, id_sala) VALUES
('Ana Perez', '600123456', 'ana.perez@cine.com', 1),
('Luis Gomez', '611987654', 'luis.gomez@cine.com', 2),
('Marta Ruiz', '622456789', 'marta.ruiz@cine.com', 3),
('Carlos Diaz', '633741852', 'carlos.diaz@cine.com', 4),
('Elena Soto', '644369258', 'elena.soto@cine.com', 5);

INSERT INTO funcion (id_sala, id_pelicula, horario) VALUES
(1, 1, '18:00:00'),
(2, 2, '20:30:00'),
(3, 3, '22:00:00'),
(4, 4, '17:15:00'),
(5, 5, '19:45:00');

INSERT INTO cliente (email, nombre) VALUES
('juan@gmail.com', 'Juan'),
('lucia@gmail.com', 'Lucía'),
('pedro@gmail.com', 'Pedro'),
('maria@gmail.com', 'María'),
('sofia@gmail.com', 'Sofía');

INSERT INTO boleto (asiento, precio, id_cliente, id_sala, id_pelicula, horario) VALUES
('A1', 8.50, 1, 1, 1, '18:00:00'),
('B5', 9.00, 2, 2, 2, '20:30:00'),
('C3', 10.00, 3, 3, 3, '22:00:00'),
('D7', 7.75, 4, 4, 4, '17:15:00'),
('E2', 9.50, 5, 5, 5, '19:45:00');