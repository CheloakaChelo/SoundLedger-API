package br.com.SoundLedger_API.model.role;

public enum Role {

    ARTISTA("artista"),
    COMPOSITOR("compositor"),
    PRODUTOR("produtor"),
    GRAVADORA("gravadora"),
    ADMIN("admin");

    private final String role;

    Role(String role){
        this.role = role;
    }

    public String getRole(){
        return role;
    }
}
