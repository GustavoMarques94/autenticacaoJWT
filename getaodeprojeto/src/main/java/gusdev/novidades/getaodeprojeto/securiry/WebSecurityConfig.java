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
@Configuration //Não preciso instanciar, o próprio Spring faz isso.
@EnableWebSecurity //Informo que é uma classe de segurança do WebSecurity
public class WebSecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    /*
        Define o PasswordEncoder para codificação de senha.
        Devolve a instância do objeto que sabe devolver o nosso padrão de codificação.
        Isso não tem nada a ver com o JWT.
        Aqui será usado para codificar a senha do usuário gerando um hash.
    */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configura o AuthenticationManagerBuilder para usar o customUserDetailsService
    // Méthodo padrão: este méthodo é obrigatório para conseguirmos trabalhar com a autenticação no login.
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

        authManagerBuilder
            .userDetailsService(customUserDetailsService) // Aqui usamos o CustomUserDetailsService diretamente
            .passwordEncoder(passwordEncoder());

        return authManagerBuilder.build();
    }

    // Configura o SecurityFilterChain, substituindo o antigo 'configure(HttpSecurity)'
    // Aqui você configura como o HttpSecurity será usado para definir as regras de segurança (autorização e autenticação).
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Desabilita CSRF
            .cors(Customizer.withDefaults()) // Configurações de CORS padrão
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Define política de sessão
            .authorizeRequests(authz -> authz
                .requestMatchers(HttpMethod.POST, "/api/usuarios", "/api/usuarios/login").permitAll() // Define rotas públicas
                .anyRequest().authenticated()  // Outras requisições precisam de autenticação
            );

        // Adiciona o filtro JWT antes do UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Ou qualquer outro mecanismo de autenticação, como formLogin()

        return http.build();
    }

}
