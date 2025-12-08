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
import java.util.List;

@Entity
@Table(name = "director")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Director {
    /**
     * Identificador único del director.
     * Es una clave primaria autogenerada por la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del director (máximo 20 caracteres).
     */
    @NotEmpty(message = "{msg.director.nombre.notEmpty}")
    @Size(max = 20, message = "{msg.director.nombre.size}")
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    /**
     * Nacionalidad del director (máximo 30 caracteres).
     */
    @NotEmpty(message = "{msg.director.nacionalidad.notEmpty}")
    @Size(max = 30, message = "{msg.director.nacionalidad.size}")
    @Column(name = "nacionalidad", nullable = false, length = 30)
    private String nacionalidad;

    /**
     * Fecha de nacimiento del director.
     */
    @NotNull(message = "{msg.director.fecha_nacimiento.notNull}")
    @Column(name = "nacimiento", nullable = false)
    private Date fecha_nacimiento;

    /**
     * Lista de películas dirigidas por este director.
     * Relación One-to-Many: un director puede tener muchas películas.
     * Se usa @JsonIgnore para evitar problemas de serialización JSON.
     * Se usa @ToString.Exclude para evitar bucles infinitos en toString().
     */
    @OneToMany(mappedBy = "director")
    @JsonIgnore
    @ToString.Exclude
    private List<Pelicula> peliculas;

    /**
     * Constructor personalizado que permite crear un director sin especificar el ID.
     * Útil cuando se crea un nuevo director antes de persistirlo en la base de datos.
     * 
     * @param nombre Nombre del director
     * @param nacionalidad Nacionalidad del director
     * @param fecha_nacimiento Fecha de nacimiento del director
     */
    public Director(String nombre, String nacionalidad, Date fecha_nacimiento) {
        this.nombre = nombre;
        this.nacionalidad = nacionalidad;
        this.fecha_nacimiento = fecha_nacimiento;
    }
}