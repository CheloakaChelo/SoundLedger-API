package br.com.SoundLedger_API.api.blockchain.service;

import br.com.SoundLedger_API.api.blockchain.contracts.SoundLedgerContract;
import br.com.SoundLedger_API.service.MusicaOrquestradorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MissingRequestCookieException;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;
import java.util.List;

@Service
public class BlockchainService {

    private final Web3j web3j;
    private final String backendPrivateKey;

    private static final Logger logger = LoggerFactory.getLogger(BlockchainService.class);

    public BlockchainService(Web3j web3j, @Value("${wallet.key}") String backendPrivateKey) {
        this.web3j = web3j;
        this.backendPrivateKey = backendPrivateKey;
    }

    public String deployNewRoyaltyContract(String titulo, String isrc, List<String> wallets, List<BigInteger> splits) throws Exception {
        Credentials credentials = Credentials.create(backendPrivateKey);
        System.out.println("Criando smart contract para: " + titulo);
        SoundLedgerContract contract = null;
        TransactionReceipt receipt = null;
        String transactionHash = null;

        try {
            contract = SoundLedgerContract.deploy(web3j, credentials, new DefaultGasProvider(), titulo, isrc, wallets, splits).sendAsync().get();

            if (contract != null && contract.getTransactionReceipt().isPresent()) {
                receipt = contract.getTransactionReceipt().get();
                transactionHash = receipt.getTransactionHash();
                logger.info("--> Transação enviada! Hash: {}", transactionHash);
                logger.info("--> Aguardando mineração...");
            } else if (contract != null) {
                logger.warn("--> Objeto de contrato obtido, mas recibo/hash não imediatamente disponível. A transação pode estar pendente.");
            } else {
                logger.error("!!! Falha ao obter objeto de contrato após deploy Async.");
                throw new RuntimeException("Falha no deploy async inicial.");
            }

            logger.info("--> Mineração concluída (ou recibo obtido)! Status: {}", (receipt != null ? receipt.getStatus() : "N/A"));

        } catch (Exception e){
            logger.error("!!! ERRO durante o processo de deploy (send/get/wait): {}", e.getMessage(), e);
            throw e;
        }

        String contractAddress = contract.getContractAddress();
        System.out.println("Contrato criado com sucesso! Endereço: " + contractAddress);
        return contractAddress;
    }

    public TransactionReceipt updatePlayCount(String contractAddress, BigInteger newPlayCount) throws Exception {
        Credentials credentials = Credentials.create(backendPrivateKey);
        SoundLedgerContract contract = SoundLedgerContract.load(
                contractAddress, web3j, credentials, new DefaultGasProvider()
        );

        System.out.println("Enviando transacao 'updatePlayCount' para o contrato: " + contractAddress + " com valor: " + newPlayCount);

        return contract.updatePlayCount(newPlayCount).send();
    }

    public BigInteger getTotalPlays(String contractAddress) throws Exception {
        Credentials credentials = Credentials.create(backendPrivateKey);
        SoundLedgerContract contract = SoundLedgerContract.load(
                contractAddress, web3j, credentials, new DefaultGasProvider()
        );

        System.out.println("Lendo o total de plays reportados do contrato: " + contractAddress);

        BigInteger totalPlays = contract.getTotalPlays().send();

        System.out.println("Total de plays lido: " + totalPlays);
        return totalPlays;
    }

}
