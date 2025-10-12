package br.com.SoundLedger_API.model.entity.perfil;

import br.com.SoundLedger_API.model.entity.Musica;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "perfilcompositor_entity")
@Data
public class PerfilCompositor {

    @Id
    private String userId;

    private String nomeArtistico;
    private String biografia;

    private List<Musica> musicasCompostas;
}
