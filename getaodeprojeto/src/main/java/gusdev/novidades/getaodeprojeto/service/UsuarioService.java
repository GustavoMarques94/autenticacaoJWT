package gusdev.novidades.getaodeprojeto.service;

import gusdev.novidades.getaodeprojeto.model.Usuario;
import gusdev.novidades.getaodeprojeto.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
}
