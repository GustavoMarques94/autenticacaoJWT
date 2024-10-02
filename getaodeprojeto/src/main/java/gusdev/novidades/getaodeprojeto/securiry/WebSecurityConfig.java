package gusdev.novidades.getaodeprojeto.securiry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//Classe de configuração, toda autenticação, autorização, o que terá em cada rota, quais rotas irão precisar de autenticação, etc...
@Configuration //informa ao Spring que a classe contém definições de beans que serão gerenciadas pelo container Spring
@EnableWebSecurity //habilita a segurança web, permitindo que a classe seja usada para configurar autenticação, autorização, filtros e outras configurações de segurança.
public class WebSecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    /*
        Define o PasswordEncoder para codificação de senha.
        Devolve a instância do objeto que sabe devolver o nosso padrão de codificação.
        Isso não tem nada a ver com o JWT.
        Gera um hash seguro para as senhas.
    */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // cria e configura o AuthenticationManagerBuilder para usar o customUserDetailsService
    // Méthodo padrão: responsável pela autenticação dos usuários
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

        authManagerBuilder
            .userDetailsService(customUserDetailsService) // Aqui usamos o CustomUserDetailsService diretamente
            .passwordEncoder(passwordEncoder());

        return authManagerBuilder.build(); //O AuthenticationManager é construído e retornado para ser usado na autenticação de login.
    }

    // Configura o SecurityFilterChain, substituindo o antigo 'configure(HttpSecurity)'
    // Aqui você configura como o HttpSecurity será usado para definir as regras de segurança (autorização e autenticação).
    // Méthodo principal de config de segurança. Ele define como o HttpSecurity deve tratar a autenticação, autorização e proteção de rota
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Configura as permissões de acesso às rotas
        http
            .csrf(AbstractHttpConfigurer::disable) // Desabilita CSRF, geralmente desnecessária em APIs REST com autenticação via tokens (ex: JWT), pois não há sessão ou formulário HTML.
            .cors(Customizer.withDefaults()) // Cross-Origin Resource Sharing -->  permite ou restringe requisições vindas de diferentes domínios
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Define que a aplicação não manterá estado de sessão, ou seja, não armazenará informações de login ou sessão entre requisições.
            .authorizeRequests(authz -> authz
                .requestMatchers(HttpMethod.POST, "/api/usuarios", "/api/usuarios/login").permitAll() // Permite que essas rotas específicas sejam acessadas sem autenticação (por exemplo, endpoints de registro e login)
                .anyRequest().authenticated()  // Qualquer outra requisição deve ser autenticada
            );

        // Adiciona o filtro JWT antes do UsernamePasswordAuthenticationFilter --> garante que as requisições autenticadas tenham um token válido
        // Esse filtro irá interceptar as requisições e validar o token JWT antes de permitir que elas alcancem o serviço.
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
