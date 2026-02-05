package com.example.pokemon.model;

public class Owner {
    private int ownerId;
    private String name;
    private String email;

    public Owner() {}
    public Owner(int ownerId, String name, String email) {
        this.ownerId = ownerId; this.name = name; this.email = email;
    }
    public Owner(String name, String email) { this.name = name; this.email = email; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "Owner{" + "ownerId=" + ownerId + ", name='" + name + '\'' + ", email='" + email + '\'' + '}';
    }
}
