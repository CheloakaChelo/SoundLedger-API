package br.com.SoundLedger_API.service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;

import org.springframework.stereotype.Service;
import org.web3j.crypto.Keys;

@Service
public class CarteiraService {

    public String gerarEnderecoCarteira() throws Exception{
        ECKeyPair keyPair = Keys.createEcKeyPair();

        Credentials credentials = Credentials.create(keyPair);
        String walletAddress = credentials.getAddress();

        return walletAddress;
    }
}
