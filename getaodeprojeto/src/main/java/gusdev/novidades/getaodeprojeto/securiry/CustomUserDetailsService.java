package gusdev.novidades.getaodeprojeto.securiry;

import gusdev.novidades.getaodeprojeto.model.Usuario;
import gusdev.novidades.getaodeprojeto.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.function.Supplier;

//CustomUserDetailsService --> Nome padrão
//São configurações de detalhes de serviço do usuário, algumas coisas que o JWT, algumas camadas nossas irão utilizar para saber quem é o usuário, se ele está autenticado ou não
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioService usuarioService;

    //Obter o usuário
    @Override
    public UserDetails loadUserByUsername(String email) {
        //return usuarioService.obterPorEmail(email).get();
        return getUser(() -> usuarioService.obterPorEmail(email));
    }

    public UserDetails loadUserById(Long id) {
        //return usuarioService.obterPorId(id).get();
        return getUser(() -> usuarioService.obterPorId(id));
    }

    //Supplier --> é como se fosse uma lambda, que vai receber um Optional de Usuário
    private Usuario getUser(Supplier<Optional<Usuario>> supplier) {
        //Tente fazer o get, se não conseguir lance a exceção
        return supplier.get().orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));
    }
}
