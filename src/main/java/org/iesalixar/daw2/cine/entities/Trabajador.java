package org.iesalixar.daw2.cine.entities;

import jakarta.persistence.*; // Anotaciones de JPA
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * La clase `Province` representa una entidad que modela una provincia dentro de la base de datos.
 * Contiene cuatro campos: `id`, `code`, `name`, y `region`, donde `id` es el identificador único de la provincia,
 * `code` es un código asociado a la provincia, `name` es el nombre de la provincia, y `region` es la relación
 * con la entidad `Region`, representando la comunidad autónoma a la que pertenece la provincia.
 *
 * Las anotaciones de Lombok ayudan a reducir el código repetitivo al generar automáticamente
 * métodos comunes como getters, setters, constructores, y otros métodos estándar de los objetos.
 */
@Entity // Marca esta clase como una entidad JPA.
@Table(name = "trabajador") // Define el nombre de la tabla asociada a esta entidad.
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Trabajador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Campo que almacena el nombre completo del trabajador.
    @NotEmpty(message = "{msg.trabajador.nombre.notEmpty}")
    @Size(max = 100, message = "{msg.trabajador.nombre.size}")
    @Column(name = "nombre", nullable = false, length = 100) // Define la columna correspondiente en la tabla.
    private String nombre;

    // Campo que almacena el telefono completo del trabajador.
    @NotEmpty(message = "{msg.trabajador.telefono.notEmpty}")
    @Size(max = 100, message = "{msg.trabajador.telefono.size}")
    @Column(name = "telefono", nullable = false, length = 100) // Define la columna correspondiente en la tabla.
    private String telefono;

    // Campo que almacena el correo completo del trabajador.
    @NotEmpty(message = "{msg.trabajador.correo.notEmpty}")
    @Size(max = 100, message = "{msg.trabajador.correo.size}")
    @Column(name = "correo", nullable = false, length = 100) // Define la columna correspondiente en la tabla.
    private String correo;

    @OneToOne(cascade = CascadeType.ALL) // Cascada para persistencia
    @JoinColumn(name = "id_sala", referencedColumnName = "id") // FK en Trabajador
    private Sala sala;

    public Trabajador(String nombre, String telefono, String correo) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.sala = sala;
    }
}
