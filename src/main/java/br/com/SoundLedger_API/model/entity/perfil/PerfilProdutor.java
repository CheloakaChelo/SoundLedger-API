package br.com.SoundLedger_API.model.entity.perfil;

import br.com.SoundLedger_API.model.entity.Musica;
import br.com.SoundLedger_API.model.entity.User;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "perfilprodutor_entity")
@Data
public class PerfilProdutor {

    @Id
    private Long id;

    private User user;

    private String estudio;

    private List<Musica> musicasProduzidas;
}
