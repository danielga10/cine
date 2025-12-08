package org.iesalixar.daw2.cine.entities;

import jakarta.persistence.*; // Anotaciones de JPA
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "trabajador")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trabajador {
    /**
     * Identificador único del trabajador.
     * Es una clave primaria autogenerada por la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre completo del trabajador (máximo 20 caracteres).
     */
    @NotEmpty(message = "{msg.trabajador.nombre.notEmpty}")
    @Size(max = 20, message = "{msg.trabajador.nombre.size}")
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    /**
     * Número de teléfono del trabajador (máximo 9 caracteres).
     */
    @NotEmpty(message = "{msg.trabajador.telefono.notEmpty}")
    @Size(max = 9, message = "{msg.trabajador.telefono.size}")
    @Column(name = "telefono", nullable = false, length = 9)
    private String telefono;

    /**
     * Correo electrónico del trabajador (máximo 35 caracteres).
     */
    @NotEmpty(message = "{msg.trabajador.correo.notEmpty}")
    @Size(max = 35, message = "{msg.trabajador.correo.size}")
    @Column(name = "correo", nullable = false, length = 50)
    private String correo;

    /**
     * Sala asignada al trabajador.
     * Relación One-to-One: un trabajador puede estar asignado a una sala.
     */
    @OneToOne
    @JoinColumn(name = "id_sala", referencedColumnName = "id", nullable = true)
    private Sala sala;

    /**
     * Constructor personalizado que permite crear un trabajador sin especificar el ID.
     * Útil cuando se crea un nuevo trabajador antes de persistirlo en la base de datos.
     * 
     * @param nombre Nombre del trabajador
     * @param telefono Teléfono del trabajador
     * @param correo Correo electrónico del trabajador
     * @param sala Sala asignada al trabajador (puede ser null)
     */
    public Trabajador(String nombre, String telefono, String correo, Sala sala) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.sala = sala;
    }
}
