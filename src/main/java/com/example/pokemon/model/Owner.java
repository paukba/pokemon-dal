package com.example.pokemon.model;

public class Owner {
    private Integer ownerId;
    private String name;
    private String email;

    public Owner() {}

    public Owner(Integer ownerId, String name, String email) {
        this.ownerId = ownerId;
        this.name = name;
        this.email = email;
    }

    public Integer getOwnerId() { return ownerId; }
    public void setOwnerId(Integer ownerId) { this.ownerId = ownerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "Owner{ownerId=" + ownerId + ", name='" + name + "', email='" + email + "'}";
    }
}