package org.iesalixar.daw2.cine.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;

@Entity // Marca esta clase como una entidad gestionada por JPA.
@Table(name = "pelicula") // Especifica el nombre de la tabla asociada a esta entidad.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pelicula {
    // Campo que almacena el identificador único de la película.
    // Es una clave primaria autogenerada por la base de datos.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Campo que almacena el título de la película.
    @NotEmpty(message = "{msg.pelicula.titulo.notEmpty}")
    @Size(max = 30, message = "{msg.pelicula.titulo.size}") // Ajustado a SQL
    @Column(name = "titulo", nullable = false, length = 30)
    private String titulo;

    // Campo que almacena duración la película.
    @NotNull(message = "{msg.pelicula.duracion.notNull}")
    @Column(name = "duracion", nullable = false)
    private Time duracion;

    // Campo que almacena el identificador único del director.
    @ManyToOne
    @JoinColumn(name = "id_director", nullable = false)
    private Director director;


    /**
     * Este es un constructor personalizado que no incluye el campo `id`.
     * Se utiliza para crear instancias de `Película` cuando no es necesario o no
     se conoce el `id` de la película
     * (por ejemplo, antes de insertar la película en la base de datos, donde el
     `id` es autogenerado).
     * @param titulo Título de la película.
     * @param duracion Duración de la película.
     */


    public Pelicula(String titulo, Time duracion) {
        this.titulo = titulo;
        this.duracion = duracion;
    }
}
