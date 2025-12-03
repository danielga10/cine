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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "{msg.boleto.code.notEmpty}")
    @Size(max = 4, message = "{msg.boleto.code.size}")
    @Column(name = "code", nullable = false, length = 4)
    private String code;

    @NotEmpty(message = "{msg.boleto.asiento.notEmpty}")
    @Size(max = 5, message = "{msg.boleto.asiento.size}")
    @Column(name = "asiento", nullable = false, length = 5)
    private String asiento;

    @NotNull(message = "{msg.boleto.precio.notNull}")
    @DecimalMin(value = "0.01", message = "{msg.boleto.precio.min}")
    @Digits(integer = 4, fraction = 2, message = "{msg.boleto.precio.digits}")
    @Column(name = "precio", nullable = false, precision = 6, scale = 2)
    private BigDecimal precio;

    // --- RELACIONES ---

    @NotNull(message = "{msg.boleto.cliente.notNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    @ToString.Exclude            // <--- IMPORTANTE: Evita bucles y errores Lazy
    @EqualsAndHashCode.Exclude   // <--- IMPORTANTE
    private Cliente cliente;

    @NotNull(message = "{msg.boleto.funcion.notNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_funcion", nullable = false)
    @ToString.Exclude            // <--- IMPORTANTE
    @EqualsAndHashCode.Exclude   // <--- IMPORTANTE
    private Funcion funcion;

    // Constructor personalizado (opcional, pero está bien tenerlo)
    public Boleto(String code, String asiento, BigDecimal precio, Cliente cliente, Funcion funcion) {
        this.code = code;
        this.asiento = asiento;
        this.precio = precio;
        this.cliente = cliente;
        this.funcion = funcion;
    }
}