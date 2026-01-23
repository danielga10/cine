package org.iesalixar.daw2.cine.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.Set;
/**
 * La clase `Role` representa un rol o autoridad en el sistema.
 * Está diseñada para trabajar en conjunto con la entidad `User` para gestionar
 la autorización
 * y el control de acceso, permitiendo que cada usuario pueda tener uno o más
 roles asignados.
 *
 * Las anotaciones de Lombok ayudan a reducir el código repetitivo al generar
 automáticamente
 * métodos comunes como getters, setters, constructores, y otros métodos estándar
 de los objetos.
 */
@Entity // Marca esta clase como una entidad JPA.
@Table(name = "roles") // Define el nombre de la tabla asociada a esta entidad.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "users") // Excluye `users` para evitar problemas de recursión en toString.
@EqualsAndHashCode(exclude = "users") // Excluye `users` para evitar problemas de recursión en equals y hashCode.
        public class Role {
            // Campo que almacena el identificador único del rol. Es autogenerado y clave primaria.
            @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
            private Long id;
            // Nombre del rol, como "ROLE_ADMIN" o "ROLE_USER".
            @NotEmpty(message = "{msg.role.name.notEmpty}")
            @Size(max = 50, message = "{msg.role.name.size}")
            @Column(name = "name", nullable = false, unique = true, length = 50)
            private String name;
            // Relación muchos a muchos con la entidad `User`. Un rol puede pertenecer a muchos usuarios.
            @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
            private Set<org.iesalixar.daw2.cine.entities.User> users;
            /**
             * Constructor que excluye el campo `id`. Se utiliza para crear instancias
             de `Role`
             * cuando el `id` aún no se ha generado (por ejemplo, antes de insertarlo en
             la base de datos).
             * @param name Nombre del rol.
             */
            public Role(String name) {
                this.name = name;
            }
        }