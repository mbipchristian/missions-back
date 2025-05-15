package com.missions_back.missions_back.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User implements UserDetails {
//-------------------------------------------ATTRIBUTS DE MA TABLE--------------------------------------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime created_at;

    @UpdateTimestamp
    @Column
    private LocalDateTime updated_at;

    @Column
    private LocalDateTime deleted_at;

    @Column
    private boolean actif = true;

//--------------------------------------------METHODES DE LA CLASSE USERDETAILS------------------------------------------------
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }
//----------------------------------------------GETTERS ET SETTERS---------------------------------------------------
    public String getPassword() {
        return password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return username;
    }

    public User setName(String username) {
        this.username = username;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public LocalDateTime getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdatedAt() {
        return updated_at;
    }

    public void setUpdatedAt(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }
    public boolean getActif() {
        return actif;
    }

    public void setActif (boolean actif) {
        this.actif = actif;
    }

    public LocalDateTime getDeletedAt() {
        return deleted_at;
    }

    public void setDeletedAt(LocalDateTime deleted_at) {
        this.deleted_at = deleted_at;
    }
//------------------------------------RELATIONS AVEC LES AUTRES TABLES--------------------------

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "Grade_id")
    private Grade grade;

    @ManyToMany()
    @JoinTable(name = "user_mandat",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "mandat_id"))
    private List<Mandat> mandats;
}
