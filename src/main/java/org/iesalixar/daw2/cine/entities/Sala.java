package org.iesalixar.daw2.cine.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "sala")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sala {

    // Identificador único de la sala
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Número de sala (>=1)
    @NotNull(message = "{msg.sala.numero.notNull}")
    @Min(value = 1, message = "{msg.sala.numero.min}")
    @Column(nullable = false)
    private Integer numero;

    // Capacidad de la sala (>=1)
    @NotNull(message = "{msg.sala.capacidad.notNull}")
    @Min(value = 1, message = "{msg.sala.capacidad.min}")
    @Column(nullable = false)
    private Integer capacidad;

    // Relación 1:1 con Trabajador (lado inverso, la FK está en Trabajador)
    @OneToOne(mappedBy = "sala")
    private Trabajador trabajador;

    // Relación 1:N con Funciones
    @OneToMany(mappedBy = "sala")
    private List<Funcion> funciones;

    // Constructor sin ID
    public Sala(Integer numero, Integer capacidad) {
        this.numero = numero;
        this.capacidad = capacidad;
    }
}