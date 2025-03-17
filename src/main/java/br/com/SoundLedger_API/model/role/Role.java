package br.com.SoundLedger_API.model.role;

public enum Role {

    ARTISTA("artista"),
    GRAVADORA("gravadora");

    private final String role;

    Role(String role){
        this.role = role;
    }

    public String getRole(){
        return role;
    }
}
