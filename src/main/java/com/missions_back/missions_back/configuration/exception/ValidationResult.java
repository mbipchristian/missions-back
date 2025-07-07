package com.missions_back.missions_back.configuration.exception;

import java.util.List;
import java.util.Map;

import com.missions_back.missions_back.model.User;

public class ValidationResult {
    private final List<User> utilisateursConformes;
    private final List<User> utilisateursNonConformes;
    private final Map<Long, String> erreursParUtilisateur;
    
    public ValidationResult(List<User> utilisateursConformes, List<User> utilisateursNonConformes, 
                           Map<Long, String> erreursParUtilisateur) {
        this.utilisateursConformes = utilisateursConformes;
        this.utilisateursNonConformes = utilisateursNonConformes;
        this.erreursParUtilisateur = erreursParUtilisateur;
    }
    
    public List<User> getUtilisateursConformes() { return utilisateursConformes; }
    public List<User> getUtilisateursNonConformes() { return utilisateursNonConformes; }
    public Map<Long, String> getErreursParUtilisateur() { return erreursParUtilisateur; }
}
