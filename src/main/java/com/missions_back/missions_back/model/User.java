package com.missions_back.missions_back.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Where(clause = "actif = true")
@Table(name = "users")
public class User implements UserDetails {
//-------------------------------------------ATTRIBUTS DE MA TABLE--------------------------------------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String matricule;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Long quotaAnnuel;

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
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.getName().toString());
        return List.of(authority);
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

    public User setCreatedAt(LocalDateTime created_at) {
        this.created_at = created_at;
        return this;
    }

    public LocalDateTime getUpdatedAt() {
        return updated_at;
    }

    public User setUpdatedAt(LocalDateTime updated_at) {
        this.updated_at = updated_at;
        return this;
    }
    public boolean getActif() {
        return actif;
    }

    public User setActif (boolean actif) {
        this.actif = actif;
        return this;
    }

    public LocalDateTime getDeletedAt() {
        return deleted_at;
    }

    public void setDeletedAt(LocalDateTime deleted_at) {
        this.deleted_at = deleted_at;
    }

    public String getMatricule() {
        return matricule;
    }
    public User setMatricule(String matricule) {
        this.matricule = matricule;
        return this;
    }
    public Long getQuotaAnnuel() {
        return quotaAnnuel;
    }
    public User setQuotaAnnuel(Long quotaAnnuel) {
        this.quotaAnnuel = quotaAnnuel;
        return this;
    }

    public Role getRole() {
    return role;
}

public User setRole(Role role) {
    this.role = role;

    return this;
}
//------------------------------------RELATIONS AVEC LES AUTRES TABLES--------------------------

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fonction_id", nullable = false)
    private Fonction fonction;
    
    @ManyToMany()
    @JoinTable(name = "user_mandat",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "mandat_id"))
    private List<Mandat> mandats;
}
