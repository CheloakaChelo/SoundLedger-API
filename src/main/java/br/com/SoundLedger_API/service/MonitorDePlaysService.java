
package br.com.SoundLedger_API.service;

import br.com.SoundLedger_API.api.blockchain.service.BlockchainService;
import br.com.SoundLedger_API.api.lastfm.service.LastFmService;
import br.com.SoundLedger_API.dao.IMusica;
import br.com.SoundLedger_API.model.entity.Musica;
import br.com.SoundLedger_API.model.entity.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Component
public class MonitorDePlaysService {

    private static final Logger logger = LoggerFactory.getLogger(MonitorDePlaysService.class);

    private final LastFmService lastFmService;
    private final IMusica musicaDao;
    private final BlockchainService blockchainService;

    @Autowired
    public MonitorDePlaysService(LastFmService lastFmService,
                                 IMusica musicaDao,
                                 BlockchainService blockchainService) {
        this.lastFmService = lastFmService;
        this.musicaDao = musicaDao;
        this.blockchainService = blockchainService;
        // this.userDao = userDao;
    }


    @Scheduled(fixedRate = 300000)
    public void verificarPlays() {
        logger.info("-------------------------------------------");
        logger.info("Iniciando ciclo de ATUALIZACAO de playcounts...");

        try {
            List<Musica> musicasMonitoradas = musicaDao.findAll();
            logger.info("Encontradas {} musicas para verificar playcount.", musicasMonitoradas.size());

            // 2. Itera por cada música monitorada
            for (Musica musica : musicasMonitoradas) {
                String titulo = musica.getTitulo();
                String artista = musica.getArtista();
                String contractAddress = musica.getContratoBlockchain();

                if (contractAddress == null || contractAddress.isEmpty() || titulo == null || artista == null || artista.equals("Artista Desconhecido")) {
                    logger.warn("Musica ignorada (dados incompletos no DB ou artista não encontrado): ID {}", musica.getId()); // Assumindo getId()
                    continue;
                }

                try {
                    BigInteger currentPlayCount = lastFmService.getTrackPlayCount(artista, titulo);

                    logger.info("-> Atualizando playcount para '{}' por '{}' (Contrato: {}): {}", titulo, artista, contractAddress, currentPlayCount);
                    blockchainService.updatePlayCount(contractAddress, currentPlayCount);

                } catch (Exception e) {
                    logger.error("Erro ao processar playcount para a musica '{}' (Contrato {}): {}", titulo, contractAddress, e.getMessage());
                }
            }

        } catch (Exception e) {
            logger.error("Erro geral no ciclo de atualizacao de playcounts: {}", e.getMessage(), e);
        } finally {
            logger.info("Ciclo de atualizacao de playcounts concluido.");
            logger.info("-------------------------------------------");
        }
    }
}