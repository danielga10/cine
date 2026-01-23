package org.iesalixar.daw2.cine.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.Set;
/**
 * La clase `User` representa una entidad que modela un usuario en el sistema.
 * Contiene campos para almacenar información de autenticación y perfil del
 usuario,
 * incluyendo username, password, estado de habilitación, nombre, apellido e
 imagen de perfil.
 * Además, cuenta con campos de auditoría como la fecha de creación, última
 modificación
 * y la fecha del último cambio de contraseña para mejorar la ciberseguridad.
 *
 * Las anotaciones de Lombok ayudan a reducir el código repetitivo al generar
 automáticamente
 * métodos comunes como getters, setters, constructores, y otros métodos estándar
 de los objetos.
 */
@Entity // Marca esta clase como una entidad JPA.
@Table(name = "users") // Define el nombre de la tabla asociada a esta entidad.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "roles") // Excluye roles para evitar problemas de recursión en el toString.
@EqualsAndHashCode(exclude = "roles") // Excluye roles para evitar recursión en equals y hashCode.
@EntityListeners(AuditingEntityListener.class) // Habilita las anotaciones de auditoría.
        public class User {
            // Campo que almacena el identificador único del usuario. Es autogenerado y clave primaria.
            @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
            private Long id;
            @Column(name = "discord_id", unique = true, length = 50)
            private String discordId;
            // Campo que almacena el nombre de usuario. Actúa como la clave primaria.
            @NotEmpty(message = "{msg.user.username.notEmpty}")
            @Size(max = 50, message = "{msg.user.username.size}")
            @Column(name = "username", nullable = false, unique = true, length = 50)
            private String username;
            // Campo que almacena la contraseña encriptada del usuario.
            @NotEmpty(message = "{msg.user.password.notEmpty}")
            @Size(min = 8, message = "{msg.user.password.size}")
            @Column(name = "password", nullable = false)
            private String password;
            // Campo que indica si el usuario está habilitado.
            @NotNull(message = "{msg.user.enabled.notNull}")
            @Column(name = "enabled", nullable = false)
            private boolean enabled;
            // Campo que almacena el primer nombre del usuario.
            @NotEmpty(message = "{msg.user.firstName.notEmpty}")
            @Size(max = 50, message = "{msg.user.firstName.size}")
            @Column(name = "first_name", nullable = false, length = 50)
            private String firstName;
            // Campo que almacena el apellido del usuario.
            @NotEmpty(message = "{msg.user.lastName.notEmpty}")
            @Size(max = 50, message = "{msg.user.lastName.size}")
            @Column(name = "last_name", nullable = false, length = 50)
            private String lastName;
            // Fecha de creación del registro.
            @CreatedDate
            @Column(name = "created_date", updatable = false)
            private LocalDateTime createdDate;
            // Fecha de última modificación del registro.
            @LastModifiedDate
            @Column(name = "last_modified_date")
            private LocalDateTime lastModifiedDate;
            // Fecha del último cambio de contraseña.
            @Column(name = "last_password_change_date")
            private LocalDateTime lastPasswordChangeDate;
            // Relación muchos a muchos con la entidad `Role`.
            // Se establece FetchType.EAGER para que se carguen los roles junto al usuario
            @ManyToMany(fetch = FetchType.EAGER)
            @JoinTable(
                    name = "user_roles",
                    joinColumns = @JoinColumn(name = "user_id"),
                    inverseJoinColumns = @JoinColumn(name = "role_id")
            )
            private Set<Role> roles;
            /**
             * Establece la contraseña del usuario y actualiza la fecha del último cambio
             de contraseña.
             *
             * @param password Nueva contraseña encriptada del usuario.
             */
            public void setPassword(String password) {
                this.password = password;
                this.lastPasswordChangeDate = LocalDateTime.now();
            }
        }
