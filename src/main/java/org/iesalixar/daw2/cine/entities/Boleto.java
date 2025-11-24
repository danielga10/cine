package org.iesalixar.daw2.cine.entities;

import jakarta.persistence.*; // Anotaciones de JPA
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity // Marca esta clase como una entidad JPA.
@Table(name = "boletos") // Define el nombre de la tabla asociada a esta entidad.
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Boleto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Campo que almacena el asiento del boleto, normalmente una cadena corta que identifica el boleto.
    // Ejemplo: "23" para boleto con id 1.
    @NotEmpty(message = "{msg.boleto.asiento.notEmpty}")
    @Size(max = 3, message = "{msg.boleto.asiento.size}")
    @Column(name = "asiento", nullable = false, length = 3) // Define la columna correspondiente en la tabla.
    private String asiento;

    @NotNull(message = "{msg.boleto.precio.notNull}")
    @DecimalMin(value = "0.0", inclusive = false, message = "{msg.boleto.precio.min}")
    @Digits(integer = 10, fraction = 2, message = "{msg.boleto.precio.digits}")
    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    // Relación con la entidad `Cliente`, representando el boleto al que pertenece el cliente.
    @NotNull(message = "{msg.boleto.cliente.notNull}")
    @ManyToOne(fetch = FetchType.LAZY) // Relación de muchos boletos a un cliente.
    @JoinColumn(name = "id_cliente", nullable = false) // Clave foránea en la tabla boleto que referencia a la tabla cliente.
    private Cliente cliente;

    // Relación con la entidad `Funcion`, representando el boleto al que pertenece la funcion.
    @NotNull(message = "{msg.boleto.funcion.notNull}")
    @ManyToOne(fetch = FetchType.LAZY) // Relación de muchos boletos a una funcion.
    @JoinColumn(name = "id_funcion", nullable = false) // Clave foránea en la tabla boleto que referencia a la tabla funcion.
    private Funcion funcion;

    public Boleto(String asiento, BigDecimal precio) {
        this.asiento = asiento;
        this.precio = precio;
        this.cliente = cliente;
        this.funcion = funcion;
    }
}
