package org.iesalixar.daw2.cine.entities;

import jakarta.persistence.*; // Anotaciones de JPA
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * La clase `Cliente` representa una entidad que modela una región dentro de la base de datos.
 * Contiene tres campos: `id`, `code` y `name`, donde `id` es el identificador único de la región,
 * `code` es un código asociado a la región, y `name` es el nombre de la región.
 *
 * Las anotaciones de Lombok ayudan a reducir el código repetitivo al generar automáticamente
 * métodos comunes como getters, setters, constructores, y otros métodos estándar de los objetos.
 */
@Entity // Marca esta clase como una entidad gestionada por JPA.
@Table(name = "clientes") // Especifica el nombre de la tabla asociada a esta entidad.
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Cliente {
    // Campo que almacena el identificador único de la región.
    // Es una clave primaria autogenerada por la base de datos.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Campo que almacena el email completo del cliente.
    @NotEmpty(message = "{msg.cliente.email.notEmpty}")
    @Size(max = 100, message = "{msg.cliente.email.size}")
    @Column(name = "email", nullable = false, length = 100) // Define la columna correspondiente en la tabla.
    private String email;

    // Campo que almacena el nombre completo del cliente.
    @NotEmpty(message = "{msg.cliente.nombre.notEmpty}")
    @Size(max = 100, message = "{msg.cliente.nombre.size}")
    @Column(name = "nombre", nullable = false, length = 100) // Define la columna correspondiente en la tabla.
    private String nombre;

    // Relación uno a muchos con la entidad `Boleto`. Un cliente puede tener muchos boletos.
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Boleto> boletos;

    public Cliente(String email, String nombre) {
        this.email = email;
        this.nombre = nombre;
    }
}
