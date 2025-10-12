// 👉 Garanta que este 'package' é o mesmo das suas outras classes de serviço
package br.com.SoundLedger_API.api.spotify.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class TesteWebClientService {

    private final WebClient.Builder webClientBuilder;

    // O Spring tentará injetar o Builder aqui
    public TesteWebClientService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @PostConstruct
    public void init() {
        // Se a injeção funcionar, esta mensagem aparecerá no console
        System.out.println();
        System.out.println("=========================================================");
        System.out.println("✅ SUCESSO! O Spring conseguiu injetar o WebClient.Builder!");
        System.out.println("   A configuração do seu projeto está CORRETA.");
        System.out.println("=========================================================");
        System.out.println();
    }
}