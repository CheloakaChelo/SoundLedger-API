package br.com.SoundLedger_API.api.blockchain.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

import java.util.List;

@Service
public class BlockchainService {

    private final Web3j web3j;
    private final String backendPrivateKey;

    public BlockchainService(Web3j web3j, @Value("${wallet.key}") String backendPrivateKey) {
        this.web3j = web3j;
        this.backendPrivateKey = backendPrivateKey;
    }

}
