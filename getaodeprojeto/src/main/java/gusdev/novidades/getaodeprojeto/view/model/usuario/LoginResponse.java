package gusdev.novidades.getaodeprojeto.view.model.usuario;

import gusdev.novidades.getaodeprojeto.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private Usuario usuario; // o correto seria devolver uma DTO
}
