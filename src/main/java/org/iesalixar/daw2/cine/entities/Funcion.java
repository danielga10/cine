package org.iesalixar.daw2.cine.entities;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Entidad que representa una función de cine.
 * Una función es una proyección de una película en una sala específica a una hora determinada.
 * 
 * @author IES Alixar DAW2
 * @version 1.0
 */
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "funcion")
public class Funcion {

    /**
     * Identificador único de la función.
     * Es una clave primaria autogenerada por la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Código único de la función.
     */
    @Column(name="code", nullable=false)
    private String code;

    /**
     * Sala donde se proyecta la función.
     * Relación Many-to-One: muchas funciones pueden realizarse en una sala.
     */
    @ManyToOne
    @JoinColumn(name = "id_sala", nullable = false)
    private Sala sala;

    /**
     * Película que se proyecta en la función.
     * Relación Many-to-One: muchas funciones pueden proyectar la misma película.
     */
    @ManyToOne
    @JoinColumn(name = "id_pelicula", nullable = false)
    private Pelicula pelicula;

    /**
     * Horario de inicio de la función.
     */
    @Column(name = "horario", nullable = false)
    private LocalTime horario;

    /**
     * Lista de boletos vendidos para esta función.
     * Relación One-to-Many: una función puede tener muchos boletos.
     */
    @OneToMany(mappedBy = "funcion")
    private List<Boleto> boletos = new ArrayList<>();

    /**
     * Constructor personalizado que permite crear una función sin especificar el ID.
     * Útil cuando se crea una nueva función antes de persistirla en la base de datos.
     * 
     * @param sala Sala donde se proyecta la función
     * @param pelicula Película que se proyecta
     * @param horario Horario de inicio de la función
     */
    public Funcion(Sala sala, Pelicula pelicula, LocalTime horario) {
        this.sala = sala;
        this.pelicula = pelicula;
        this.horario = horario;
        this.boletos = new ArrayList<>();
    }
}
