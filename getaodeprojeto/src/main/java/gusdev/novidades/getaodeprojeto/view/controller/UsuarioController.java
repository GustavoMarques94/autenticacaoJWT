package gusdev.novidades.getaodeprojeto.view.controller;

import gusdev.novidades.getaodeprojeto.model.Usuario;
import gusdev.novidades.getaodeprojeto.service.UsuarioService;
import gusdev.novidades.getaodeprojeto.view.model.usuario.LoginRequest;
import gusdev.novidades.getaodeprojeto.view.model.usuario.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin("*") //Para o cross não bloquear a requisição, digo que estou aceitando requisições de qualquer lugar
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<Usuario> obterTodos() {
        return usuarioService.obterTodos();
    }

    @GetMapping("/{id}")
    public Optional<Usuario> obterPorId(@PathVariable("id") Long id) {
        return usuarioService.obterPorId(id);
    }

    @PostMapping
    public Usuario adicionar(@RequestBody Usuario usuario) {
        return usuarioService.adicionar(usuario);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        return usuarioService.logar(loginRequest.getEmail(), loginRequest.getSenha());
    }
    // em alguns casos podemos criar um controller separado só para autenticação, login, loggout, refresh token
    // ex: criar outros méthodos como login refresh para atualizar o token de tempos em tempos
}
