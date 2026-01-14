package com.app.models.entities;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class User {
    private int idUser;
    private String username;
    private String password; // Hash
    private Timestamp createdAt;
    private Timestamp lastLogin;
    private List<Role> roles;

    public User() {
        this.roles = new ArrayList<>();
    }

    public User(int idUser, String username, String password) {
        this();
        this.idUser = idUser;
        this.username = username;
        this.password = password;
    }

    public User(String username, String password) {
        this();
        this.username = username;
        this.password = password;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role) {
        if (this.roles == null) {
            this.roles = new ArrayList<>();
        }
        this.roles.add(role);
    }
}
