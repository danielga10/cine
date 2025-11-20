package org.iesalixar.daw2.cine.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;

@Entity // Marca esta clase como una entidad gestionada por JPA.
@Table(name = "directores") // Especifica el nombre de la tabla asociada a esta entidad.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Director {
    // Campo que almacena el identificador único de la director.
    // Es una clave primaria autogenerada por la base de datos.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Campo que almacena el nombre del director.
    @NotEmpty(message = "{msg.director.nombre.notEmpty}")
    @Size(max = 100, message = "{msg.director.nombre.size}")
    @Column(name = "nombre", nullable = false, length = 100) // Define la columna correspondiente en la tabla.
    private String nombre;

    // Campo que almacena nacionalidad del director.
    @NotEmpty(message = "{msg.director.nacionalidad.notEmpty}")
    @Size(max = 100, message = "{msg.director.nacionalidad.size}")
    @Column(name = "nacionalidad", nullable = false, length = 100) // Define la columna correspondiente en la tabla.
    private String nacionalidad;

    // Campo que almacena fecha de nacimiento del director.
    @NotEmpty(message = "{msg.director.fecha_nacimiento.notEmpty}")
    @Column(name = "fecha_nacimiento", nullable = false) // Define la columna correspondiente en la tabla.
    private Date fecha_nacimiento;

    /**
     * Este es un constructor personalizado que no incluye el campo `id`.
     * Se utiliza para crear instancias de `Película` cuando no es necesario o no
     se conoce el `id` de la director
     * (por ejemplo, antes de insertar la director en la base de datos, donde el
     `id` es autogenerado).
     * @param nombre Nombre de la director.
     * @param nacionalidad Nacionalidad de la director.
     * @param fecha_nacimiento Fecha de nacimiento del director.
     */


    public Director(String nombre, String nacionalidad, Date fecha_nacimiento) {
        this.nombre = nombre;
        this.nacionalidad = nacionalidad;
        this.fecha_nacimiento = fecha_nacimiento;
    }
}