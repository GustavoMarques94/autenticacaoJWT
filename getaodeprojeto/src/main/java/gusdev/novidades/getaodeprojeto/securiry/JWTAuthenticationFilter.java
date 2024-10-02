package gusdev.novidades.getaodeprojeto.securiry;

import gusdev.novidades.getaodeprojeto.model.Usuario;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Optional;

//É uma clase padrão para todo projeto. Ela filtra toda autenticação.
//Essa classe terá um métodho que será executado antes de qualquer conexão com os endpoints.
//Quando alguém fizer uma requisição, antes de bater na controller, vai bater nessa classe para verificar se o usuário tem permissão ou não.
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter { // OncePerRequestFilter --> extende do "Spring Framework Web", antes de bater em qualquer endpoint, vai bater nessa classe

    @Autowired
    private JWTService jwtService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    //Métodho principal onde toda requisição bate antes de chegar no endpoint.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //Pego o Token da requisição
        String token = obterToken(request);

        //Pego o id do usuário que está dentro do token
        Optional<Long> id = jwtService.obterIdDoUsuario(token);

        //Se não achou o id, o usuário mandou o token errado.
        if(!id.isPresent()) {
            throw new InputMismatchException("Token invalido");
        }

        //Pego o usuário dono do token pelo seu id
        UserDetails usuario = customUserDetailsService.loadUserById(id.get());

        //Verficando se o usuário está autenticado ou não. Poderiamos validar as permissões.
        UsernamePasswordAuthenticationToken authentication = //Classe do Spring Security, irá criar uma instância de uma autenticação
                new UsernamePasswordAuthenticationToken(usuario, null, Collections.emptyList());

        //Mudando a autenticação para a própria requisição. Pego a autenticação atual do usuário e troco pela nova autenticação.
        //Ex: Usuário já está autenticado há muito tempo, então ele acessa o sistema para fazer alguma coisa, então atualiza a autenticação dele
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        //Repasso a autenticação para o contexto do security. A partir de agora o Spring toma conta de tudo.
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String obterToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        //Verifica se veio alguma coisa sem ser espaços em branco dentro do token.
        if(!StringUtils.hasText(token) && !token.startsWith("Bearer ")) {
            return null;
        }

        return token.substring(7);
    }
}
