package org.iesalixar.daw2.cine.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * La clase `Sala` representa una entidad que representa una sala del cine.
 * Contiene campos como `id`, `numero`, `capacidad` y `id_cliente`,
 */
@Entity
@Table(name = "sala")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sala {

    // Identificador único de la sala. Es autogenerado y clave primaria.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Numero de la sala.
    @NotNull(message = "{msg.sala.numero.notNull}")
    @Column(name = "numero", nullable = false)
    private Integer numero;

    // Capacidad total de la sala.
    @NotNull(message = "{msg.sala.capacidad.notNull}")
    @Column(name = "capacidad", nullable = false)
    private Integer capacidad;

    @ManyToOne
    @JoinColumn(name = "id_pelicula", nullable = false)
    private Pelicula pelicula;

    // Lista de peliculas asociados a la sala.
    @OneToMany(mappedBy = "sala")
    private List<Funcion> funciones;

    public Sala(Integer numero, Integer capacidad, Pelicula pelicula) {
        this.numero = numero;
        this.capacidad = capacidad;
        this.pelicula = pelicula;
    }
}