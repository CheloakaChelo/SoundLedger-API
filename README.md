# 🎵 SoundLedger-API

Projeto de TCC em Ciências da Computação que consiste em uma API de um sistema Blockchain de direitos autorais em música.

## Funcionalidades Principais

* **Cadastro de Músicas:** Regista novas músicas no sistema.
    * Utiliza o **ISRC** como identificador único.
    * Busca metadados (título, artista, compositores) em APIs externas (MusicBrainz, Spotify).
    * Permite que o utilizador-cadastrador associe os detentores de direitos (compositores, etc.) a utilizadores registados no sistema.
    * Permite que o utilizador defina as **percentagens (splits)** de cada detentor.
* **Deploy de Smart Contract:** Cria e implanta (faz deploy) um **Smart Contract único** (`SoundLedgerContract`) na blockchain (Sepolia Testnet) para cada música cadastrada, armazenando permanentemente os detentores de direitos e as suas percentagens.
* **Monitoramento de Plays (Automático):**
    * Um serviço agendado (`@Scheduled`) corre periodicamente.
    * Busca a **contagem total de plays** (`playcount`) de cada música cadastrada a partir da API do **Last.fm**.
* **Depósito de Royalties (Automático):**
    * Calcula a **diferença** de plays (`deltaPlays`) desde a última verificação.
    * Calcula um valor de royalty simulado (ETH) baseado nos novos plays (`deltaPlays * ETH_PER_PLAY`).
    * Envia uma transação de **depósito de ETH** da carteira do backend para o smart contract da música.
* **Distribuição de Fundos (Automática):**
    * Após um depósito bem-sucedido, o backend aciona a função `distributeAccumulatedBalance()` no contrato.
    * O contrato distribui o ETH depositado para os saldos internos (`releasableRoyalties`) de cada detentor, de acordo com as percentagens definidas.
* **Consulta de Dashboard (Para Utilizadores):**
    * Expõe um endpoint (`GET /api/dashboard/meus-royalties/{userId}`) que:
        * Busca as músicas em que o utilizador participa (via MongoDB).
        * Lê da blockchain o `totalPlaysReported` de cada música.
        * Lê da blockchain o `saldoDoUsuarioEth` (lendo o `releasableRoyalties` daquele utilizador).
* **Saque de Royalties (Manual/Externo):**
    * O contrato expõe uma função `withdrawRoyalties()` que permite a qualquer detentor de direitos sacar o seu saldo acumulado (`releasableRoyalties`) diretamente da blockchain, usando a sua própria carteira (ex: MetaMask).

---

## 🛠️ Ferramentas e Tecnologias

* **Backend:**
    * Java (JDK 17+)
    * Spring Boot (Web, Data MongoDB, Security)
* **Base de Dados:**
    * MongoDB
* **Blockchain:**
    * Solidity (`^0.8.20`)
    * Ethereum (Rede de Testes Sepolia)
    * Web3j (Biblioteca Java para interação com Ethereum)
    * Infura (Provedor de nó para conexão à rede Sepolia)
    * MetaMask (Gestão de carteiras e depósitos/saques manuais)
* **APIs Externas:**
    * MusicBrainz API (Busca de dados das faixas)
    * Last.fm API (Busca de `playcount` para simulação de streams)
* **Build & Dependências:**
    * Apache Maven
    * `web3j-maven-plugin` (Para gerar wrappers Java a partir do Solidity)
* **Testes (Backend):**
    * JUnit 5
    * Mockito
* **Testes (API):**
    * Insomnia / Postman
* **Frontend (Separado):**
    * React.js

#  Front-end do sistema
https://github.com/CheloakaChelo/soundledger-front.git
