-- ===============================
-- 1️⃣ INSERTS EN DIRECTOR
-- ===============================
INSERT INTO director (nombre, nacionalidad, nacimiento) VALUES
('Christopher Nolan', 'Britanico', '1970-07-30');
INSERT INTO director (nombre, nacionalidad, nacimiento) VALUES
('Quentin Tarantino', 'Estadounidense', '1963-03-27');
INSERT INTO director (nombre, nacionalidad, nacimiento) VALUES
('Hayao Miyazaki', 'Japonés', '1941-01-05');
INSERT INTO director (nombre, nacionalidad, nacimiento) VALUES
('Patty Jenkins', 'Estadounidense', '1971-07-24');
INSERT INTO director (nombre, nacionalidad, nacimiento) VALUES
('Alfonso Cuarón', 'Mexicano', '1961-11-28');

-- ===============================
-- 2️⃣ INSERTS EN PELICULA
-- ===============================
INSERT INTO pelicula (titulo, duracion, id_director) VALUES
('Inception', '02:28:00', 1);
INSERT INTO pelicula (titulo, duracion, id_director) VALUES
('Pulp Fiction', '02:34:00', 2);
INSERT INTO pelicula (titulo, duracion, id_director) VALUES
('Spirited Away', '02:05:00', 3);
INSERT INTO pelicula (titulo, duracion, id_director) VALUES
('Wonder Woman', '02:21:00', 4);
INSERT INTO pelicula (titulo, duracion, id_director) VALUES
('Gravity', '01:31:00', 5);

-- ===============================
-- 3️⃣ INSERTS EN SALA
-- ===============================
INSERT INTO sala (numero, capacidad, id_pelicula) VALUES
('1', 120, 1);
INSERT INTO sala (numero, capacidad, id_pelicula) VALUES
('2', 150, 2);
INSERT INTO sala (numero, capacidad, id_pelicula) VALUES
('3', 100, 3);
INSERT INTO sala (numero, capacidad, id_pelicula) VALUES
('4', 140, 4);
INSERT INTO sala (numero, capacidad, id_pelicula) VALUES
('5', 160, 5);

-- ===============================
-- 4️⃣ INSERTS EN TRABAJADOR
-- (Se hace después de SALA)
-- ===============================
INSERT INTO trabajador (nombre, telefono, correo, id_sala) VALUES
('Ana Lopez', '600000001', 'ana@sala1.com', 1);
INSERT INTO trabajador (nombre, telefono, correo, id_sala) VALUES
('Carlos Perez', '600000002', 'carlos@sala2.com', 2);
INSERT INTO trabajador (nombre, telefono, correo, id_sala) VALUES
('Maria Diaz', '600000003', 'maria@sala3.com', 3);
INSERT INTO trabajador (nombre, telefono, correo, id_sala) VALUES
('David Ruiz', '600000004', 'david@sala4.com', 4);
INSERT INTO trabajador (nombre, telefono, correo, id_sala) VALUES
('Laura Garcia', '600000005', 'laura@sala5.com', 5);

-- ===============================
-- 5️⃣ INSERTS EN CLIENTE
-- ===============================
INSERT INTO cliente (email, nombre) VALUES
('cliente1@mail.com', 'Juan Torres');
INSERT INTO cliente (email, nombre) VALUES
('cliente2@mail.com', 'Lucia Ramos');
INSERT INTO cliente (email, nombre) VALUES
('cliente3@mail.com', 'Pedro Gomez');
INSERT INTO cliente (email, nombre) VALUES
('cliente4@mail.com', 'Sara Mendez');
INSERT INTO cliente (email, nombre) VALUES
('cliente5@mail.com', 'Elena Castro');

-- ===============================
-- 6️⃣ INSERTS EN FUNCION
-- ===============================
INSERT INTO funcion (code, id_sala, id_pelicula, horario) VALUES
('A001', 1, 1, '15:00:00');
INSERT INTO funcion (code, id_sala, id_pelicula, horario) VALUES
('A002', 2, 2, '17:00:00');
INSERT INTO funcion (code, id_sala, id_pelicula, horario) VALUES
('A003', 3, 3, '18:30:00');
INSERT INTO funcion (code, id_sala, id_pelicula, horario) VALUES
('A004', 4, 4, '20:00:00');
INSERT INTO funcion (code, id_sala, id_pelicula, horario) VALUES
('A005', 5, 5, '21:30:00');

-- ===============================
-- 7️⃣ INSERTS EN BOLETO
-- ===============================
INSERT INTO boleto (code, asiento, precio, id_cliente, id_funcion, horario) VALUES
('A001', 'A1', 8.50, 1, 1, '15:00:00');
INSERT INTO boleto (code, asiento, precio, id_cliente, id_funcion, horario) VALUES
('A002', 'B3', 9.00, 2, 2, '17:00:00');
INSERT INTO boleto (code, asiento, precio, id_cliente, id_funcion, horario) VALUES
('A003', 'C2', 7.50, 3, 3, '18:30:00');
INSERT INTO boleto (code, asiento, precio, id_cliente, id_funcion, horario) VALUES
('A004', 'D4', 9.20, 4, 4, '20:00:00');
INSERT INTO boleto (code, asiento, precio, id_cliente, id_funcion, horario) VALUES
('A005', 'E5', 8.80, 5, 5, '21:30:00');
