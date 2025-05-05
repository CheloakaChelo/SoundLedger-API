package br.com.SoundLedger_API.model.entity.perfil;

import br.com.SoundLedger_API.model.entity.Musica;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "perfilgravadora_entity")
@Data
public class PerfilGravadora {

    @Id
    private Long id;

    private String nome;

    private String cnpj;

    private List<Musica> musicasDistribuidas;

}
