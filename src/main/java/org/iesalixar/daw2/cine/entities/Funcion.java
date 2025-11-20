package org.iesalixar.daw2.cine.entities;

import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "funcion")
public class Funcion {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long idFuncion; // PK simple

        @ManyToOne
        @JoinColumn(name = "id_sala", nullable = false)
        private Sala sala;

        @ManyToOne
        @JoinColumn(name = "id_pelicula", nullable = false)
        private Pelicula pelicula;

        @Column(name = "horario", nullable = false)
        private LocalTime horario;

        @OneToMany(mappedBy = "funcion")
        private List<Boleto> boletos;

        public Funcion(){

        }
}


