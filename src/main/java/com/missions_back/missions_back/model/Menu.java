package com.missions_back.missions_back.model;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "menus")
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String icone;

    @Column(nullable = false)
    private String url;

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

    //------------------------------------RELATIONS AVEC LES AUTRES TABLES--------------------------

    @ManyToMany(mappedBy = "menus")
    private List<Role> roles;
}
