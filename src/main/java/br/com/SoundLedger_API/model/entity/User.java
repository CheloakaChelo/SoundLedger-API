package br.com.SoundLedger_API.model.entity;


import br.com.SoundLedger_API.model.entity.perfil.PerfilArtista;
import br.com.SoundLedger_API.model.entity.perfil.PerfilCompositor;
import br.com.SoundLedger_API.model.entity.perfil.PerfilGravadora;
import br.com.SoundLedger_API.model.entity.perfil.PerfilProdutor;
import br.com.SoundLedger_API.model.role.Role;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import jakarta.validation.constraints.Email;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Document(collection = "user_entity")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    private String id;

    private String nome;

    @Email
    private String email;

    private String senha;

    private String enderecoCarteira;

    private Set<Role> roles;

    private PerfilArtista perfilArtista;
    private PerfilCompositor perfilCompositor;
    private PerfilProdutor perfilProdutor;
    private PerfilGravadora perfilGravadora;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
