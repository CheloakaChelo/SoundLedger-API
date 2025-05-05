package br.com.SoundLedger_API.model.entity.perfil;

import br.com.SoundLedger_API.model.entity.Musica;
import br.com.SoundLedger_API.model.entity.User;
import org.springframework.data.annotation.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "perfilartista_entity")
@Data
public class PerfilArtista {

    @Id
    private Long id;

    private User user;

    private String nomeArtistico;
    private String biografia;

    private List<Musica> listaMusicas;
}
