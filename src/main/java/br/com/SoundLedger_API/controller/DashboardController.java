package br.com.SoundLedger_API.controller;

import br.com.SoundLedger_API.model.dto.MusicRoyaltyInfoDTO;
import br.com.SoundLedger_API.model.entity.User;
import br.com.SoundLedger_API.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getMyRoyalties (@PathVariable String userId) {
        try{
            List<MusicRoyaltyInfoDTO> royaltiesInfo = dashboardService.getInformacoesRoyaltiesUsuario(userId);
            return ResponseEntity.ok(royaltiesInfo);
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Erro interno ao buscar royalties."));
        }
    }
}
