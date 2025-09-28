package br.com.SoundLedger_API.controller;

import br.com.SoundLedger_API.model.dto.AuthenticationDTO;
import br.com.SoundLedger_API.model.dto.LoginResponseDTO;
import br.com.SoundLedger_API.model.entity.User;
import br.com.SoundLedger_API.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    TokenService tokenService;

    @PostMapping("/login")
    @CrossOrigin
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data){
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.senha());

        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

}
