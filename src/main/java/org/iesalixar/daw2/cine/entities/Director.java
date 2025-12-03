package org.iesalixar.daw2.cine.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

@Entity // Marca esta clase como una entidad gestionada por JPA.
@Table(name = "director") // Especifica el nombre de la tabla asociada a esta entidad.
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
    @Size(max = 20, message = "{msg.director.nombre.size}")
    @Column(name = "nombre", nullable = false, length = 20)
    private String nombre;

    // Campo que almacena nacionalidad del director.
    @NotEmpty(message = "{msg.director.nacionalidad.notEmpty}")
    @Size(max = 30, message = "{msg.director.nacionalidad.size}")
    @Column(name = "nacionalidad", nullable = false, length = 30)
    private String nacionalidad;

    // Campo que almacena fecha de nacimiento del director.
    @NotNull(message = "{msg.director.fecha_nacimiento.notNull}")
    @Column(name = "nacimiento", nullable = false)
    private Date fecha_nacimiento;

    // Lista de peliculas asociados al director.
    @OneToMany(mappedBy = "director")
    @JsonIgnore
    @ToString.Exclude
    private List<Pelicula> peliculas;

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