package org.iesalixar.daw2.cine.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.List;

@Entity // Marca esta clase como una entidad gestionada por JPA.
@Table(name = "peliculas") // Especifica el nombre de la tabla asociada a esta entidad.
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
    @Size(max = 100, message = "{msg.pelicula.titulo.size}")
    @Column(name = "titulo", nullable = false, length = 100) // Define la columna correspondiente en la tabla.
    private String titulo;

    // Campo que almacena duración la película.
    @NotEmpty(message = "{msg.pelicula.duracion.notEmpty}")
    @Column(name = "duracion", nullable = false) // Define la columna correspondiente en la tabla.
    private Time duracion;

    // Campo que almacena el identificador único del director.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_director;

    // Lista de peliculas asociadas al director.
    @OneToMany(mappedBy = "director")
    private List<Pelicula> peliculas;

    /**
     * Este es un constructor personalizado que no incluye el campo `id`.
     * Se utiliza para crear instancias de `Película` cuando no es necesario o no
     se conoce el `id` de la película
     * (por ejemplo, antes de insertar la película en la base de datos, donde el
     `id` es autogenerado).
     * @param titulo Título de la película.
     * @param duracion Duración de la película.
     * @param id_director Id del director.
     */


    public Pelicula(String titulo, Time duracion, Long id_director) {
        this.titulo = titulo;
        this.duracion = duracion;
        this.id_director = id_director;
    }
}
