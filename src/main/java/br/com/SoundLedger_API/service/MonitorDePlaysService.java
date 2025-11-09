
package br.com.SoundLedger_API.service;

import br.com.SoundLedger_API.api.blockchain.service.BlockchainService;
import br.com.SoundLedger_API.api.lastfm.service.LastFmService;
import br.com.SoundLedger_API.dao.IMusica;
import br.com.SoundLedger_API.dao.IUser;
import br.com.SoundLedger_API.model.entity.Musica;
import br.com.SoundLedger_API.model.entity.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Component
public class MonitorDePlaysService {

    private static final Logger logger = LoggerFactory.getLogger(MonitorDePlaysService.class);

    private final LastFmService lastFmService;
    private final IMusica musicaDao;
    private final IUser userDao;
    private final BlockchainService blockchainService;

    private static final BigDecimal ETH_PER_PLAY = new BigDecimal("0.000001");

    @Autowired
    public MonitorDePlaysService(LastFmService lastFmService,
                                 IMusica musicaDao,
                                 BlockchainService blockchainService, IUser userDao) {
        this.lastFmService = lastFmService;
        this.musicaDao = musicaDao;
        this.blockchainService = blockchainService;
        this.userDao = userDao;
    }


    @Scheduled(fixedRate = 120000) // 2 minutos
    public void verificarPlays() {
        logger.info("-------------------------------------------");
        logger.info("Iniciando ciclo: Atualização/Depósito/Distribuição...");
        try {
            List<Musica> musicasMonitoradas = musicaDao.findAll();
            logger.info("Encontradas {} musicas para verificar.", musicasMonitoradas.size());

            for (Musica musica : musicasMonitoradas) {
                String titulo = musica.getTitulo();
                String artistaId = musica.getArtistaPrincipalId();
                String contractAddress = musica.getContratoBlockchain();
                String musicaId = musica.getId();

                String artista = userDao.findById(artistaId)
                        .map(User::getNome)
                        .orElse(null);

                if (titulo == null || titulo.isEmpty() ||
                        artista == null || artista.isEmpty() ||
                        contractAddress == null || contractAddress.isEmpty())
                {
                    logger.warn("Musica ID {} IGNORADA: Dados essenciais em falta (Titulo='{}', Artista='{}', Contrato='{}').",
                            musicaId, titulo, artista, contractAddress);
                    continue;
                }

                logger.debug("Processando musica '{}' por '{}' (Contrato: {})", titulo, artista, contractAddress);

                try {
                    BigInteger currentPlayCount = lastFmService.getTrackPlayCount(artista, titulo);

                    BigInteger previousPlayCount = blockchainService.getTotalPlays(contractAddress);
                    logger.debug("-> Contagens: Anterior(BC)={}, Atual(LF)={}", previousPlayCount, currentPlayCount);

                    BigInteger deltaPlays = currentPlayCount.subtract(previousPlayCount);

                    if (deltaPlays.compareTo(BigInteger.ZERO) > 0) {
                        logger.info("--> Aumento detectado para '{}': {} novos plays.", titulo, deltaPlays);

                        BigDecimal valorPorPlayWei = Convert.toWei(ETH_PER_PLAY, Convert.Unit.ETHER);
                        BigDecimal deltaPlaysDecimal = new BigDecimal(deltaPlays);
                        BigDecimal amountToSendDecimalWei = deltaPlaysDecimal.multiply(valorPorPlayWei);
                        BigInteger amountToSendWei = amountToSendDecimalWei.setScale(0, RoundingMode.HALF_UP).toBigIntegerExact();

                        if (amountToSendWei.compareTo(BigInteger.ZERO) > 0) {
                            logger.info("--> Calculado {} Wei para depositar.", amountToSendWei);
                            try {
                                blockchainService.depositRoyalties(contractAddress, amountToSendWei);

                                logger.info("--> Depósito bem-sucedido. Acionando distribuição de fundos para {}", contractAddress);
                                try {
                                    blockchainService.distributeFunds(contractAddress);
                                } catch (Exception distEx) {
                                    logger.error("--> FALHA ao distribuir fundos para {}: {}", contractAddress, distEx.getMessage());
                                }

                            } catch (Exception depositEx) {
                                logger.error("--> FALHA AO DEPOSITAR fundos para {}: {}", contractAddress, depositEx.getMessage());
                                continue;
                            }
                        } else {
                            logger.info("--> Valor de depósito calculado é zero Wei. Depósito/Distribuição ignorados.");
                        }

                        logger.info("--> Atualizando playcount na blockchain para {}: {}", contractAddress, currentPlayCount);
                        try {
                            blockchainService.updatePlayCount(contractAddress, currentPlayCount);
                        } catch (Exception updateEx) {
                            logger.error("--> FALHA ao atualizar playcount para {}: {}", contractAddress, updateEx.getMessage());
                        }

                    } else {
                        logger.info("-> Sem novos plays detectados para '{}'. Nenhuma acao na blockchain necessaria.", titulo);
                    }

                } catch (Exception e) {
                    logger.error("Erro ao processar musica '{}' (Contrato {}): {}", titulo, contractAddress, e.getMessage());
                }
            }

        } catch (Exception e) {
            logger.error("Erro GERAL no ciclo de verificacao/distribuicao: {}", e.getMessage(), e);
        } finally {
            logger.info("Ciclo concluido.");
            logger.info("-------------------------------------------");
        }
    }
}