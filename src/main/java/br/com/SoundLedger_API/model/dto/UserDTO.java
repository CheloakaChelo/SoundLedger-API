package br.com.SoundLedger_API.model.dto;

import br.com.SoundLedger_API.model.role.Role;
import java.util.Set;

public record UserDTO (String id, String nome, String email, String senha, String enderecoCarteira, Set<Role> roles){

}
