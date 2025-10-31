package br.com.SoundLedger_API.controller;

import br.com.SoundLedger_API.model.dto.CadastroRequestDTO;
import br.com.SoundLedger_API.model.dto.CadastroViaISRCRequestDTO;
import br.com.SoundLedger_API.model.dto.MusicDetailsDTO;
import br.com.SoundLedger_API.model.entity.Musica;
import br.com.SoundLedger_API.service.MusicaOrquestradorService;
import br.com.SoundLedger_API.service.MusicaService;
import org.bouncycastle.jcajce.provider.asymmetric.ec.KeyFactorySpi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/musica")
@CrossOrigin(origins = "http://localhost:5173")
public class MusicaController {

    @Autowired
    private final MusicaService service;

    @Autowired
    private final MusicaOrquestradorService orquestradorService;

    public MusicaController(MusicaService service, MusicaOrquestradorService orquestradorService) {
        this.service = service;
        this.orquestradorService = orquestradorService;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Musica>> listarMusicas (){
        List<Musica> musicaList = service.listarMusica();

        if (musicaList.isEmpty()){
            return ResponseEntity.noContent().build();
        }else {
            return ResponseEntity.ok(musicaList);
        }
    }

    @GetMapping("/listar/{id}")
    public ResponseEntity<Optional<Musica>> musicaPorId (@PathVariable String id){
        Optional<Musica> musicaPorId = service.listarPorId(id);

        if (musicaPorId.isPresent()){
            return ResponseEntity.ok(musicaPorId);
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/artista/{id}")
    public ResponseEntity<List<Musica>> musicaPorArtista (@PathVariable String artistaPrincipalId) {
        List<Musica> musicaPorArtista = service.listarPorArtista(artistaPrincipalId);

        if (musicaPorArtista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }else {
            return ResponseEntity.ok(musicaPorArtista);
        }
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<List<Musica>> musicaPorParticipacoes(@PathVariable String id){
        List<Musica> musicaList = service.listarPorParticipacoes(id);

        if(musicaList.isEmpty()){
            return ResponseEntity.noContent().build();
        }else {
            return ResponseEntity.ok(musicaList);
        }
    }

    @PostMapping("/orquestrador-cadastro")
    public ResponseEntity<Mono<Musica>> cadastrarMusicaZero (@RequestBody CadastroRequestDTO cadastroRequestDTO) throws Exception {
        Musica newMusica = orquestradorService.cadastrarNovaMusica(cadastroRequestDTO.artista(), cadastroRequestDTO.titulo());
        return (ResponseEntity<Mono<Musica>>) ResponseEntity.ok();
    }

    @GetMapping("/{isrc}")
    public ResponseEntity<MusicDetailsDTO> buscarPorIsrc(@PathVariable String isrc) throws RuntimeException {
        MusicDetailsDTO musicDetailsDTO = orquestradorService.getMusicDetailsByIsrc(isrc);
        return ResponseEntity.ok(musicDetailsDTO);
    }

    @PostMapping("/cadastrar-isrc")
    public ResponseEntity<?> cadastrarViaIsrc(@RequestBody CadastroViaISRCRequestDTO request) {
        try {
            Musica musicaSalva = orquestradorService.cadastrarMusicaComDadosConfirmados(request);

            return ResponseEntity.ok(musicaSalva);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Erro interno no servidor."));
        }
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<Musica> cadastrarMusica (@RequestBody Musica musica){
        Musica newMusica = service.cadastrarMusica(musica);
        return ResponseEntity.ok(newMusica);
    }

    @PutMapping("/editar")
    public ResponseEntity<Musica> editarMusica (@RequestBody Musica musica){
        Musica musicaEdit = service.editarMusica(musica);
        return ResponseEntity.ok(musicaEdit);
    }

    @DeleteMapping("/deletar/{id}")
    @Transactional
    public ResponseEntity<Optional<Musica>> deletarMusica (@PathVariable String id){
        service.deleteMusica(id);
        return ResponseEntity.noContent().build();
    }

}
