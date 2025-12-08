package org.iesalixar.daw2.cine.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una sala de proyección del cine.
 * Una sala puede tener un trabajador asignado y múltiples funciones programadas.
 * 
 * @author IES Alixar DAW2
 * @version 1.0
 */
@Entity
@Table(name = "sala")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sala {

    /**
     * Identificador único de la sala.
     * Es una clave primaria autogenerada por la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Número identificador de la sala (entre 1 y 100 caracteres).
     */
    @NotNull(message = "{msg.sala.numero.notNull}")
    @Size(min = 1, max = 100, message = "{msg.sala.numero.size}")
    @Column(nullable = false)
    private String numero;

    /**
     * Capacidad máxima de espectadores de la sala (mínimo 1).
     */
    @NotNull(message = "{msg.sala.capacidad.notNull}")
    @Min(value = 1, message = "{msg.sala.capacidad.min}")
    @Column(nullable = false)
    private Integer capacidad;

    /**
     * Trabajador asignado a esta sala.
     * Relación One-to-One (lado inverso, la FK está en Trabajador).
     */
    @OneToOne(mappedBy = "sala")
    private Trabajador trabajador;

    /**
     * Lista de funciones programadas en esta sala.
     * Relación One-to-Many: una sala puede tener muchas funciones.
     */
    @OneToMany(mappedBy = "sala")
    private List<Funcion> funciones = new ArrayList<>();

    /**
     * Constructor personalizado que permite crear una sala sin especificar el ID.
     * Útil cuando se crea una nueva sala antes de persistirla en la base de datos.
     * 
     * @param numero Número identificador de la sala
     * @param capacidad Capacidad máxima de espectadores
     */
    public Sala(String numero, Integer capacidad) {
        this.numero = numero;
        this.capacidad = capacidad;
        this.funciones = new ArrayList<>();
    }
}