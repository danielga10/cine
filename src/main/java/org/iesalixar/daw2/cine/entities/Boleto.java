package org.iesalixar.daw2.cine.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "boleto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Boleto {

    /**
     * Identificador único del boleto.
     * Es una clave primaria autogenerada por la base de datos.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Código único del boleto (máximo 4 caracteres).
     */
    @NotEmpty(message = "{msg.boleto.code.notEmpty}")
    @Size(max = 4, message = "{msg.boleto.code.size}")
    @Column(name = "code", nullable = false, length = 4)
    private String code;

    /**
     * Número o identificador del asiento asignado (máximo 5 caracteres).
     */
    @NotEmpty(message = "{msg.boleto.asiento.notEmpty}")
    @Size(max = 5, message = "{msg.boleto.asiento.size}")
    @Column(name = "asiento", nullable = false, length = 5)
    private String asiento;

    /**
     * Precio del boleto.
     * Debe ser mayor a 0.01 y tener máximo 4 dígitos enteros y 2 decimales.
     */
    @NotNull(message = "{msg.boleto.precio.notNull}")
    @DecimalMin(value = "0.01", message = "{msg.boleto.precio.min}")
    @Digits(integer = 4, fraction = 2, message = "{msg.boleto.precio.digits}")
    @Column(name = "precio", nullable = false, precision = 6, scale = 2)
    private BigDecimal precio;

    // --- RELACIONES ---

    /**
     * Cliente que compró el boleto.
     * Relación Many-to-One: muchos boletos pueden pertenecer a un cliente.
     * Se usa FetchType.LAZY para optimizar las consultas.
     */
    @NotNull(message = "{msg.boleto.cliente.notNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    @ToString.Exclude            // Evita bucles infinitos en toString() y errores Lazy
    @EqualsAndHashCode.Exclude   // Evita problemas en equals() y hashCode() con relaciones lazy
    private Cliente cliente;

    /**
     * Función para la cual se emitió el boleto.
     * Relación Many-to-One: muchos boletos pueden pertenecer a una función.
     * Se usa FetchType.LAZY para optimizar las consultas.
     */
    @NotNull(message = "{msg.boleto.funcion.notNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_funcion", nullable = false)
    @ToString.Exclude            // Evita bucles infinitos en toString() y errores Lazy
    @EqualsAndHashCode.Exclude   // Evita problemas en equals() y hashCode() con relaciones lazy
    private Funcion funcion;

    /**
     * Constructor personalizado que permite crear un boleto sin especificar el ID.
     * Útil cuando se crea un nuevo boleto antes de persistirlo en la base de datos.
     * 
     * @param code Código del boleto
     * @param asiento Número de asiento
     * @param precio Precio del boleto
     * @param cliente Cliente que compra el boleto
     * @param funcion Función para la cual se emite el boleto
     */
    public Boleto(String code, String asiento, BigDecimal precio, Cliente cliente, Funcion funcion) {
        this.code = code;
        this.asiento = asiento;
        this.precio = precio;
        this.cliente = cliente;
        this.funcion = funcion;
    }
}