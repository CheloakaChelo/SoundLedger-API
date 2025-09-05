package br.com.SoundLedger_API.model.entity;


import br.com.SoundLedger_API.model.entity.perfil.PerfilArtista;
import br.com.SoundLedger_API.model.entity.perfil.PerfilCompositor;
import br.com.SoundLedger_API.model.entity.perfil.PerfilGravadora;
import br.com.SoundLedger_API.model.entity.perfil.PerfilProdutor;
import br.com.SoundLedger_API.model.role.Role;
import org.springframework.aop.interceptor.PerformanceMonitorInterceptor;
import org.springframework.data.annotation.Id;
import jakarta.validation.constraints.Email;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection = "user_entity")
@Data
public class User {

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
}
