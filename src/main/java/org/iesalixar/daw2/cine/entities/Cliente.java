package org.iesalixar.daw2.cine.entities;

import jakarta.persistence.*; // Anotaciones de JPA
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    /**
     * Identificador único del cliente.
     * Es una clave primaria autogenerada por la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Correo electrónico del cliente (máximo 100 caracteres).
     */
    @NotEmpty(message = "{msg.cliente.email.notEmpty}")
    @Size(max = 100, message = "{msg.cliente.email.size}")
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    /**
     * Nombre completo del cliente (máximo 100 caracteres).
     */
    @NotEmpty(message = "{msg.cliente.nombre.notEmpty}")
    @Size(max = 100, message = "{msg.cliente.nombre.size}")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    /**
     * Lista de boletos comprados por el cliente.
     * Relación One-to-Many: un cliente puede tener muchos boletos.
     * Se usa CascadeType.ALL para que las operaciones se propaguen a los boletos.
     * Se usa FetchType.LAZY para optimizar las consultas.
     */
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Boleto> boletos = new ArrayList<>();

    /**
     * Constructor personalizado que permite crear un cliente sin especificar el ID.
     * Útil cuando se crea un nuevo cliente antes de persistirlo en la base de datos.
     * 
     * @param email Correo electrónico del cliente
     * @param nombre Nombre completo del cliente
     */
    public Cliente(String email, String nombre) {
        this.email = email;
        this.nombre = nombre;
        this.boletos = new ArrayList<>();
    }
}
