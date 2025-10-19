package br.com.SoundLedger_API.api.blockchain.service;

import br.com.SoundLedger_API.api.blockchain.contracts.SoundLedgerContract;
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

    public BlockchainService(Web3j web3j, @Value("${wallet.key}") String backendPrivateKey) {
        this.web3j = web3j;
        this.backendPrivateKey = backendPrivateKey;
    }

    public String deployNewRoyaltyContract(String titulo, String isrc, List<String> wallets, List<BigInteger> splits) throws Exception {
        Credentials credentials = Credentials.create(backendPrivateKey);
        System.out.println("Criando smart contract para: " + titulo);

        SoundLedgerContract contract = SoundLedgerContract.deploy(web3j, credentials, new DefaultGasProvider(), titulo, isrc, wallets, splits).send();

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

        // ✅ Chama o método Java que corresponde à nova função do Solidity
        return contract.updatePlayCount(newPlayCount).send();
    }

    public BigInteger getTotalPlays(String contractAddress) throws Exception {
        Credentials credentials = Credentials.create(backendPrivateKey);
        SoundLedgerContract contract = SoundLedgerContract.load(
                contractAddress, web3j, credentials, new DefaultGasProvider()
        );

        System.out.println("Lendo o total de plays reportados do contrato: " + contractAddress);

        // ✅ Chama o método de leitura correto (gerado a partir da função view getTotalPlays)
        BigInteger totalPlays = contract.getTotalPlays().send();

        System.out.println("Total de plays lido: " + totalPlays);
        return totalPlays;
    }

}
