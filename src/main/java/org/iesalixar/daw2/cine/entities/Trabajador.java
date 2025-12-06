package org.iesalixar.daw2.cine.entities;

import jakarta.persistence.*; // Anotaciones de JPA
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

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
    @Size(max = 20, message = "{msg.trabajador.nombre.size}")
    @Column(name = "nombre", nullable = false, length = 20)
    private String nombre;

    // Campo que almacena el telefono completo del trabajador.
    @NotEmpty(message = "{msg.trabajador.telefono.notEmpty}")
    @Size(max = 9, message = "{msg.trabajador.telefono.size}")
    @Column(name = "telefono", nullable = false, length = 9)
    private String telefono;

    // Campo que almacena el correo completo del trabajador.
    @NotEmpty(message = "{msg.trabajador.correo.notEmpty}")
    @Size(max = 35, message = "{msg.trabajador.correo.size}")
    @Column(name = "correo", nullable = false, length = 35)
    private String correo;

    @OneToOne
    @JoinColumn(name = "id_sala", referencedColumnName = "id", nullable = true)
    private Sala sala;

    public Trabajador(String nombre, String telefono, String correo, Sala sala) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.sala = sala;
    }
}
