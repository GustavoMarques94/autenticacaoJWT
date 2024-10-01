package gusdev.novidades.getaodeprojeto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
//Essa sequência é o hibernate quem faz e gerencia lá no BD
@SequenceGenerator(name = "generator_usuario", sequenceName = "sequence_usuario", initialValue = 1, allocationSize = 1) // name = "generator_usuario" --> nome do generator;    sequenceName = "sequence_usuario" --> gera uma camada de sequência no BD, começa em 1 e incrementa de 1 em 1
@Entity
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_usuario") // strategy = GenerationType.SEQUENCE --> Digo que a estratégia é uma sequência;    generator = "generator_usuario" --> gerador criado pelo hibernate
    private Long id;

    @Column(nullable = false) //Constraint = Não pode ser nulo, não é necessário para Id pois ele é PK
    private String nome;

    @Column(nullable = false, unique = true) //único, entidade forte do usuário
    private String email;

    @Column(nullable = false) //Não posso guardar a senha pública, vou guardar o hash dessa senha, verifico se a senha é válida e gero um token, um hash dela e salvo no BD.
    private String senha; //Toda vez que tentar se autenticar, vamos gerar um novo hash e comparar com o que está no BD.

    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    //Implementação do UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        //Esta conta pode expirar?
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        //A conta não está bloqueada?
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        //As credenciais não expiraram?
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
