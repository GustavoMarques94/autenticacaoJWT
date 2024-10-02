package gusdev.novidades.getaodeprojeto.securiry;

import gusdev.novidades.getaodeprojeto.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.Optional;

//Classe responsável por gerar o token JWT, é um seviço para geração do token!
//Para que eu consiga utilizar essa classe de qualquer lugar, anoto ela com @Component
//@Component --> Digo que a classe é uma Bean, com isso o Spring irá conseguir injetar ela dentro do seu container de dependência, e quando precisar utilizar, utilizo o @Autowired
@Component
public class JWTService {

    // Chave secreta utilizada do JWT para codificar e decodificar o token
    // Geralmente não deixamos ela dentro do código, necessário deixar dentro de alguma config, algum lugar seguro, mas como é um ambiente de teste, vamos deixar dentro do nosso código.
    private static final String chavePrivadaJWT = "secretKey";

    /**
     * Método para gerar um token JWT
     * @param authentication Autenticação do usuário.
     * @return Token.
     */
    public String gerarChavePrivada(Authentication authentication) {

        // 1 Dia em milliseconds
        int tempoExpiracao = 86400000;

        // Data atual + o tempo definido no tempo de expiração
        Date dataExpiracao = new Date(System.currentTimeMillis() + tempoExpiracao);

        //Pegando o usuário atual da autenticação
        Usuario usuario = (Usuario) authentication.getPrincipal(); //Esse cara sempre retorna um objeto, como esperamos um usuário vamos realizar um cast para Usuario

        //Pega todos os dados e retorna um token do JWT
        return Jwts.builder() //O padrão Builder permiti construir de acordo com os parâmetros que desejar
                .setSubject(usuario.getId().toString()) //Alguma coisa que temos no usuário que seja única, para que possamos validar
                .setIssuedAt(new Date()) //Data que quero me basear para esse token, a data que está acontecendo essa autenticação
                .setExpiration(dataExpiracao) //Data de expiração
                .signWith(SignatureAlgorithm.HS512, chavePrivadaJWT) //Tipo de criptografia e qual a chave que queremos utilizar
                .compact();
    }

    /**
     * Método para retornar o id do usuário dono do token
     * @param token Token do usuário
     * @return id do usuário
     */
    public Optional<Long> obterIdDoUsuario(String token) {
        try {
            //Retorna as permissões do token
            Claims claims = parse(token).getBody();

            //Retorna o id de dentro do token se encontrar, caso contrário retoarn null
            return Optional.ofNullable( (Long) claims.get("id") );

        } catch (Exception e) {
            //Se não encontrar nada, devolve um optional null
            return Optional.empty();
        }
    }

    // Método que sabe descobrir de dentro do token com base na chave privada, qual as permissões (claims) do usuário
    private Jws<Claims> parse(String token) {
        return Jwts.parser().setSigningKey(chavePrivadaJWT).parseClaimsJws(token);
    }
}
