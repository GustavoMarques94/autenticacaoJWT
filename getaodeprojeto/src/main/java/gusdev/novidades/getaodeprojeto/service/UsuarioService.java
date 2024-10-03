package gusdev.novidades.getaodeprojeto.service;

import gusdev.novidades.getaodeprojeto.model.Usuario;
import gusdev.novidades.getaodeprojeto.repository.UsuarioRepository;
import gusdev.novidades.getaodeprojeto.securiry.JWTService;
import gusdev.novidades.getaodeprojeto.view.model.usuario.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private static final String HEADER_PREFIX = "Bearer ";

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public List<Usuario> obterTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obterPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> obterPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario adicionar(Usuario usuario) {
        //Usuário com id --> repositório atualiza
        //Usuário sem id --> repositório salva
        usuario.setId(null);

        if(obterPorEmail(usuario.getEmail()).isPresent()) {
            throw new InputMismatchException("E-mail já cadastrado!");
        }

        String senhaCodificada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCodificada);

        return usuarioRepository.save(usuario);
    }

    public LoginResponse logar(String email, String senha){

        // tento autenticar o usuário, passo email e senha para ele, ele loga lá dentro e me devolve uma autenticação,
        // caso não consiga logar ele já estoura uma exceção
        Authentication autenticacao = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, senha, Collections.emptyList())
        );

        // passo para o Spring Security a autenticação do cara que está tentando logar
        SecurityContextHolder.getContext().setAuthentication(autenticacao);

        // gero um novo token para o usuário  Ex: Bearer 131h123hb12xayygh123hcsdk459dsfshsh.asdsnv2311
        String token = HEADER_PREFIX.concat(jwtService.gerarChavePrivada(autenticacao)) ;

        //Busco o usuário através do e-mail
        Usuario usuario = usuarioRepository.findByEmail(email).get();

        return new LoginResponse(token, usuario);
    }
}
