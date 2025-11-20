package org.iesalixar.daw2.cine.entities;

import org.iesalixar.daw2.cine.entities.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotEmpty(message = "{msg.sala.numero.notEmpty}")
    @Size(min = 2, max = 100, message = "{msg.sala.numero.size}")
    @Column(name = "numero", nullable = false)
    private int numero;

    // Capacidad total de la sala.
    @NotNull(message = "{msg.sala.capacidad.notNull}")
    @Column(name = "capacidad", nullable = false)
    private int capacidad;


    // Lista de peliculas asociados a la sala.
    @ManyToMany(mappedBy = "funcion")
    private List<Funcion> funciones;

    public Sala(int numero, int capacidad) {
        this.numero = numero;
        this.capacidad = capacidad;
    }
}