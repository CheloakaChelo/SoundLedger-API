package br.com.SoundLedger_API.controller;

import br.com.SoundLedger_API.model.entity.Musica;
import br.com.SoundLedger_API.service.MusicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/musica")
public class MusicaController {

    @Autowired
    private final MusicaService service;

    public MusicaController(MusicaService service) {
        this.service = service;
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
