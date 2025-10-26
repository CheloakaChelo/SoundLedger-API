package br.com.SoundLedger_API.api.blockchain.service;

import br.com.SoundLedger_API.api.blockchain.contracts.SoundLedgerContract;
import br.com.SoundLedger_API.service.MusicaOrquestradorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;
import org.web3j.tx.response.TransactionReceiptProcessor;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class BlockchainService {

    private final Web3j web3j;
    private final String backendPrivateKey;

    private static final BigInteger GAS_LIMIT_TRANSFER = BigInteger.valueOf(150_000);
    private static final BigInteger GAS_LIMIT_DISTRIBUTE = BigInteger.valueOf(1_000_000);
    private static final BigInteger GAS_LIMIT_UPDATE_PLAY = BigInteger.valueOf(100_000);
    private static final long POLLING_INTERVAL = 5000;
    private static final int POLLING_ATTEMPTS = 40;

    private static final Logger logger = LoggerFactory.getLogger(BlockchainService.class);

    public BlockchainService(Web3j web3j, @Value("${wallet.key}") String backendPrivateKey) {
        this.web3j = web3j;
        this.backendPrivateKey = backendPrivateKey;
    }

    public String deployNewRoyaltyContract(String titulo, String isrc, List<String> wallets, List<BigInteger> splits) throws Exception {

        Credentials credentials = Credentials.create(backendPrivateKey);
        logger.info("Tentando fazer deploy (via .send()) do contrato para: {}", titulo);
        try {
            SoundLedgerContract contract = SoundLedgerContract.deploy(
                    web3j, credentials, new DefaultGasProvider(),
                    titulo, isrc, wallets, splits
            ).send();
            TransactionReceipt receipt = contract.getTransactionReceipt().orElseThrow(() -> new RuntimeException("Recibo nulo após deploy.send()"));
            logger.info("--> Deploy TX Enviada/Minerada! Hash: {}", receipt.getTransactionHash());
            logger.info("--> Status: {}", receipt.getStatus());
            if (!receipt.isStatusOK()) {
                throw new RuntimeException("Falha na TX de Deploy. Status: " + receipt.getStatus() + ". Hash: " + receipt.getTransactionHash());
            }
            String contractAddress = contract.getContractAddress();
            if (contractAddress == null || contractAddress.isEmpty()) {
                throw new RuntimeException("Endereço do contrato nulo/vazio após deploy. Hash: " + receipt.getTransactionHash());
            }
            logger.info("✅ Contrato criado com sucesso! Endereço: {}", contractAddress);
            return contractAddress;
        } catch (Exception e) {
            logger.error("!!! ERRO durante deploy (.send()): {}", e.getMessage(), e);
            throw e;
        }
    }

    public TransactionReceipt updatePlayCount(String contractAddress, BigInteger newPlayCount) throws Exception {
        Credentials credentials = Credentials.create(backendPrivateKey);
        logger.info("Enviando TX 'updatePlayCount' (manual tx) para {} com valor {}", contractAddress, newPlayCount);
        Function function = new Function("updatePlayCount", Arrays.asList(new Uint256(newPlayCount)), Collections.emptyList());
        String encodedFunction = FunctionEncoder.encode(function);
        return sendRawTransaction(
                credentials, contractAddress, BigInteger.ZERO, GAS_LIMIT_UPDATE_PLAY, encodedFunction
        );
    }

    public BigInteger getReleasableRoyalties(String contractAddress, String walletAddress) throws Exception {
        Credentials credentials = Credentials.create(backendPrivateKey);
        SoundLedgerContract contract = SoundLedgerContract.load(
                contractAddress, web3j, credentials, new DefaultGasProvider()
        );

        logger.debug("Lendo saldo sacavel para {} no contrato {}", walletAddress, contractAddress);

        try {
            BigInteger balance = contract.releasableRoyalties(walletAddress).send();
            logger.info("Saldo sacável para {} no contrato {}: {} Wei", walletAddress, contractAddress, balance);
            return balance;
        } catch (Exception e) {
            logger.error("!!! Erro ao ler releasableRoyalties para {} no contrato {}: {}", walletAddress, contractAddress, e.getMessage(), e);
            throw e;
        }
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

    public TransactionReceipt depositRoyalties(String contractAddress, BigInteger amountInWei) throws Exception {
        Credentials credentials = Credentials.create(backendPrivateKey);
        logger.info("Enviando depósito (manual tx) de {} Wei para {}", amountInWei, contractAddress);
        return sendRawTransaction(
                credentials, contractAddress, amountInWei, GAS_LIMIT_TRANSFER, ""
        );
    }

    public TransactionReceipt distributeFunds(String contractAddress) throws Exception {
        Credentials credentials = Credentials.create(backendPrivateKey);
        SoundLedgerContract contract = SoundLedgerContract.load(
                contractAddress, web3j, credentials, new DefaultGasProvider()
        );
        logger.info("Enviando transacao 'distributeAccumulatedBalance' para o contrato: {}", contractAddress);
        TransactionReceipt receipt = contract.distributeAccumulatedBalance().send();
        if (!receipt.isStatusOK()) {
            logger.error("!!! Falha na transação distributeAccumulatedBalance! Status: {}", receipt.getStatus());
            throw new RuntimeException("Falha na distribuicao. Status: " + receipt.getStatus() + ". Hash: " + receipt.getTransactionHash());
        }
        logger.info("✅ Distribuição de fundos concluída para contrato {}. Hash: {}", contractAddress, receipt.getTransactionHash());
        return receipt;
    }

    private TransactionReceipt sendRawTransaction(Credentials credentials, String toAddress, BigInteger valueInWei, BigInteger gasLimit, String data) throws Exception {
        String fromAddress = credentials.getAddress();
        TransactionReceipt transactionReceipt = null;
        String transactionHash = null;
        logger.debug("Preparando transação: De={}, Para={}, Valor={}, GasLimit={}, Data={}", fromAddress, toAddress, valueInWei, gasLimit, data.isEmpty() ? "vazio" : data.substring(0, Math.min(data.length(), 10)) + "...");

        try {
            BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
            BigInteger nonce = web3j.ethGetTransactionCount(fromAddress, DefaultBlockParameterName.LATEST).send().getTransactionCount();
            logger.debug("GasPrice: {}, Nonce: {}", gasPrice, nonce);

            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, toAddress, valueInWei, data);
            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            String hexValue = Numeric.toHexString(signedMessage);

            logger.info("-> Enviando transação crua...");
            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();

            if (ethSendTransaction.hasError()) {
                logger.error("!!! Erro ao ENVIAR transação crua: {} (Code: {})", ethSendTransaction.getError().getMessage(), ethSendTransaction.getError().getCode());
                throw new RuntimeException("Erro ao enviar transacao: " + ethSendTransaction.getError().getMessage());
            }
            transactionHash = ethSendTransaction.getTransactionHash();
            logger.info("--> Transação enviada! Hash: {}. Aguardando recibo...", transactionHash);

            TransactionReceiptProcessor receiptProcessor = new PollingTransactionReceiptProcessor(web3j, POLLING_INTERVAL, POLLING_ATTEMPTS);
            transactionReceipt = receiptProcessor.waitForTransactionReceipt(transactionHash);

            logger.info("--> Recibo obtido! Status: {}", transactionReceipt.getStatus());
            if (!transactionReceipt.isStatusOK()) {
                logger.error("!!! Transação FALHOU na blockchain! Status: {}. Hash: {}", transactionReceipt.getStatus(), transactionHash);
                throw new RuntimeException("Falha na execucao da transacao. Status: " + transactionReceipt.getStatus() + ". Hash: " + transactionHash);
            }
            logger.info("✅ Transação (Hash: {}) concluída com sucesso!", transactionHash);

        } catch (Exception e) {
            logger.error("!!! ERRO GERAL durante envio/espera da transação crua. Hash (se disponível): {}", transactionHash, e);
            if (transactionHash != null) { throw new RuntimeException("Erro no processo da transacao " + transactionHash + ": " + e.getMessage(), e); }
            else { throw e; }
        }
        return transactionReceipt;
    }

}
