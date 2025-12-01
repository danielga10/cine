package org.iesalixar.daw2.cine.entities;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "funcion")
public class Funcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_funcion")
    private Long id_funcion;

    @Column(name="code", nullable=false)
    private String code;

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

    public Funcion() {
    }

    public Funcion(Sala sala, Pelicula pelicula, LocalTime horario) {
        this.sala = sala;
        this.pelicula = pelicula;
        this.horario = horario;
    }
}
