package org.iesalixar.daw2.cine.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true) // Evita errores si Discord envía campos que no tenemos aquí
public class DiscordUserDTO {

    private String id; // El ID único (Snowflake) que guardaremos en discord_id

    private String username; // Nombre de usuario (ej: ElDiavloLoKoTV)

    private String email; // El correo electrónico (requiere scope 'email')

    private String avatar; // El hash de la imagen de perfil

    @JsonProperty("global_name")
    private String globalName; // El nombre visible (Display Name)

    private String discriminator; // El antiguo #0000 (ya casi no se usa, pero viene en el JSON)

    private boolean verified; // Indica si el usuario ha verificado su email en Discord

    /**
     * Método útil para obtener la URL de la imagen de perfil del usuario.
     */
    public String getAvatarUrl() {
        if (avatar == null) {
            return "https://cdn.discordapp.com/embed/avatars/0.png";
        }
        return "https://cdn.discordapp.com/avatars/" + id + "/" + avatar + ".png";
    }
}
