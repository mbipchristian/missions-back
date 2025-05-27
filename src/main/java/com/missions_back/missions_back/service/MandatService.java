package com.missions_back.missions_back.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.missions_back.missions_back.dto.GradeResponseDto;
import com.missions_back.missions_back.dto.MandatDto;
import com.missions_back.missions_back.dto.MandatResponseDto;
import com.missions_back.missions_back.dto.PermissionResponseDto;
import com.missions_back.missions_back.dto.RapportResponseDto;
import com.missions_back.missions_back.dto.RessourceResponseDto;
import com.missions_back.missions_back.dto.RoleDto;
import com.missions_back.missions_back.dto.RoleResponseDto;
import com.missions_back.missions_back.dto.UserResponseDto;
import com.missions_back.missions_back.dto.VilleResponseDto;
import com.missions_back.missions_back.model.Mandat;
import com.missions_back.missions_back.model.Permission;
import com.missions_back.missions_back.model.Rapport;
import com.missions_back.missions_back.model.Ressource;
import com.missions_back.missions_back.model.User;
import com.missions_back.missions_back.model.Ville;
import com.missions_back.missions_back.repository.MandatRepo;
import com.missions_back.missions_back.repository.RapportRepo;
import com.missions_back.missions_back.repository.RessourceRepo;
import com.missions_back.missions_back.repository.UserRepo;
import com.missions_back.missions_back.repository.VilleRepo;

import jakarta.persistence.EntityNotFoundException;

@Service
public class MandatService {
    private final MandatRepo mandatRepo;
    private final UserRepo userRepo;
    private final RessourceRepo ressourceRepo;
    private final VilleRepo villeRepo;
    private final RapportRepo rapportRepo;

    public MandatService(MandatRepo mandatRepo, UserRepo userRepo, RessourceRepo ressourceRepo, VilleRepo villeRepo, RapportRepo rapportRepo) {
        this.mandatRepo = mandatRepo;
        this.userRepo = userRepo;
        this.ressourceRepo = ressourceRepo;
        this.villeRepo = villeRepo;
        this.rapportRepo = rapportRepo;
    }

    //Creation d'un nouveau mandat de mission
    @Transactional
    public MandatResponseDto createMandat(MandatDto mandatDto) {
        // Vérifier si le MANDAT existe déjà
        if (mandatRepo.findByReference(mandatDto.reference()).isPresent()) {
            throw new IllegalArgumentException("mandat already exists");
        }

        // Créer le mandat en question
        Mandat mandat = new Mandat();
        mandat.setReference(mandatDto.reference());
        mandat.setObjectif(mandatDto.objectif());
        mandat.setMissionDeControle(mandatDto.missionDeControle());
        mandat.setDateDebut(mandatDto.dateDebut());
        mandat.setDateFin(mandatDto.dateFin());
        mandat.setDuree(mandatDto.duree());
        mandat.setPieceJointe(mandatDto.pieceJointe());
        mandat.setCreated_at(LocalDateTime.now());
        mandat.setUpdated_at(LocalDateTime.now());
        mandat.setActif(true);
        


        // Ajouter les utilisateurs au mandat
        if (mandatDto.userIds() != null && !mandatDto.userIds().isEmpty()) {
            List<User> users = userRepo.findAllById(mandatDto.userIds());
            
            // Vérifier si tous les utilisateurs ont été trouvées
            if (users.size() != mandatDto.userIds().size()) {
                throw new EntityNotFoundException("Un ou plusieurs utilisateurs n'ont pas été trouvés");
            }
            
            mandat.setUsers(users);
        }
        // Ajouter les ressources au mandat
        if (mandatDto.ressourceIds() != null && !mandatDto.ressourceIds().isEmpty()) {
            List<Ressource> ressources = ressourceRepo.findAllById(mandatDto.ressourceIds());
            
            if (ressources.size() != mandatDto.ressourceIds().size()) {
                throw new EntityNotFoundException("Une ou plusieurs ressources n'ont pas été trouvées");
            }
            
            mandat.setRessources(ressources);
        }

        // Ajouter les villes au mandat
        if (mandatDto.villeIds() != null && !mandatDto.villeIds().isEmpty()) {
            List<Ville> villes = villeRepo.findAllById(mandatDto.villeIds());
            
            if (villes.size() != mandatDto.villeIds().size()) {
                throw new EntityNotFoundException("Une ou plusieurs villes n'ont pas été trouvées");
            }
            
            mandat.setVilles(villes);
        }

        Mandat createdMandat = mandatRepo.save(mandat);
        return convertToMandatResponseDto(createdMandat);
    }
    // Récupérer tous les mandats
    public List<MandatResponseDto> getAllMandats() {
        List<Mandat> mandats = mandatRepo.findAll();
        return mandats.stream()
                .map(this::convertToMandatResponseDto)
                .collect(Collectors.toList());
    }
    // Récupérer un mandat par ID
    public MandatResponseDto getMandatById(Long id) {
        Mandat mandat = mandatRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mandat non trouvé avec l'ID: " + id));
        return convertToMandatResponseDto(mandat);
    }
    // Mettre à jour un mandat
    @Transactional
    public MandatResponseDto updateMandat(Long id, MandatDto mandatDto) {
        Mandat mandat = mandatRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mandat non trouvé avec l'ID: " + id));

        // Vérifier si une autre référence existe déjà (sauf pour ce mandat)
        mandatRepo.findByReference(mandatDto.reference())
                .filter(existingMandat -> !existingMandat.getId().equals(id))
                .ifPresent(existingMandat -> {
                    throw new IllegalArgumentException("Un mandat avec cette référence existe déjà");
                });

        // Mettre à jour les champs
        mandat.setReference(mandatDto.reference());
        mandat.setObjectif(mandatDto.objectif());
        mandat.setMissionDeControle(mandatDto.missionDeControle());
        mandat.setPieceJointe(mandatDto.pieceJointe());
        mandat.setUpdated_at(LocalDateTime.now());

        // Mettre à jour les utilisateurs
        if (mandatDto.userIds() != null) {
            List<User> users = userRepo.findAllById(mandatDto.userIds());
            if (!mandatDto.userIds().isEmpty() && users.size() != mandatDto.userIds().size()) {
                throw new EntityNotFoundException("Un ou plusieurs utilisateurs n'ont pas été trouvés");
            }
            mandat.setUsers(users);
        }

        // Mettre à jour les ressources
        if (mandatDto.ressourceIds() != null) {
            List<Ressource> ressources = ressourceRepo.findAllById(mandatDto.ressourceIds());
            if (!mandatDto.ressourceIds().isEmpty() && ressources.size() != mandatDto.ressourceIds().size()) {
                throw new EntityNotFoundException("Une ou plusieurs ressources n'ont pas été trouvées");
            }
            mandat.setRessources(ressources);
        }

        // Mettre à jour les villes
        if (mandatDto.villeIds() != null) {
            List<Ville> villes = villeRepo.findAllById(mandatDto.villeIds());
            if (!mandatDto.villeIds().isEmpty() && villes.size() != mandatDto.villeIds().size()) {
                throw new EntityNotFoundException("Une ou plusieurs villes n'ont pas été trouvées");
            }
            mandat.setVilles(villes);
        }

        Mandat updatedMandat = mandatRepo.save(mandat);
        return convertToMandatResponseDto(updatedMandat);
    }
    // Supprimer un mandat
    @Transactional
    public void deleteMandat(Long id) {
        if (!mandatRepo.existsById(id)) {
            throw new EntityNotFoundException("Mandat non trouvé avec l'ID: " + id);
        }
        mandatRepo.deleteById(id);
    }
    // Récupérer les mandats par utilisateur
    public List<MandatResponseDto> getMandatsByUser(Long userId) {
        List<Mandat> mandats = mandatRepo.findByUsersId(userId);
        return mandats.stream()
                .map(this::convertToMandatResponseDto)
                .collect(Collectors.toList());
    }

    // Récupérer tous les mandats actifs
    public List<MandatResponseDto> getActiveMandats() {
        List<Mandat> mandats = mandatRepo.findByActifTrue();
        return mandats.stream()
                .map(this::convertToMandatResponseDto)
                .collect(Collectors.toList());
    }
    // Méthode de conversion vers MandatResponseDto
    private MandatResponseDto convertToMandatResponseDto(Mandat mandat) {
        // Convertir les utilisateurs
        List<UserResponseDto> userDtos = mandat.getUsers() != null ? 
            mandat.getUsers().stream()
                .map(this::convertToUserResponseDto)
                .collect(Collectors.toList()) : 
            List.of();

        // Convertir les villes
        List<VilleResponseDto> villeDtos = mandat.getVilles() != null ? 
            mandat.getVilles().stream()
                .map(this::convertToVilleResponseDto)
                .collect(Collectors.toList()) : 
            List.of();

        // Convertir les ressources
        List<RessourceResponseDto> ressourceDtos = mandat.getRessources() != null ? 
            mandat.getRessources().stream()
                .map(this::convertToRessourceResponseDto)
                .collect(Collectors.toList()) : 
            List.of();

        // Convertir le rapport
        RapportResponseDto rapportDto = mandat.getRapport() != null ? 
            convertToRapportResponseDto(mandat.getRapport()) : null;

        return new MandatResponseDto(
            mandat.getId(),
            mandat.getReference(),
            mandat.getObjectif(),
            mandat.isMissionDeControle(),
            mandat.getDateDebut(),
            mandat.getDateFin(),
            mandat.getDuree(),
            mandat.getPieceJointe(),
            mandat.getCreated_at(),
            mandat.getUpdated_at(),
            userDtos,
            villeDtos,
            ressourceDtos,
            rapportDto
        );
    }
    // Méthodes de conversion auxiliaires
    private UserResponseDto convertToUserResponseDto(User user) {
        RoleResponseDto roleDto = user.getRole() != null ?
            new RoleResponseDto(
                user.getRole().getId(),
                user.getRole().getName(),
                user.getRole().getCode(),
                user.getRole().getCreated_at(),
                user.getRole().getUpdated_at(),
                user.getRole().getPermissions() != null ?
                    user.getRole().getPermissions().stream()
                        .map(this::convertToPermissionResponseDto)
                        .collect(Collectors.toList()) :
                    List.of()
            ) : null;

        GradeResponseDto gradeDto = user.getGrade() != null ?
            new GradeResponseDto(
                user.getGrade().getId(),
                user.getGrade().getName(),
                user.getGrade().getCode(),
                user.getGrade().getFraisExterne(),
                user.getGrade().getFraisInterne(),
                user.getGrade().getCreated_at(),
                user.getGrade().getUpdated_at()
            ) : null;

        return new UserResponseDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getMatricule(),
            user.getQuotaAnnuel(),
            roleDto,
            gradeDto,
            user.getCreated_at(),
            user.getUpdated_at()
        );
    }

    private PermissionResponseDto convertToPermissionResponseDto(Permission permission) {
        return new PermissionResponseDto(
            permission.getId(),
            permission.getName(),
            permission.getCode(),
            permission.getCreated_at(),
            permission.getUpdated_at()
        );
    }

    private VilleResponseDto convertToVilleResponseDto(Ville ville) {
        return new VilleResponseDto(
            ville.getId(),
            ville.getName(),
            ville.getCode(),
            ville.isInterieur(),
            ville.getCreated_at(),
            ville.getUpdated_at()
        );
    }

    private RessourceResponseDto convertToRessourceResponseDto(Ressource ressource) {
        return new RessourceResponseDto(
            ressource.getId(),
            ressource.getName(),
            ressource.getCode(),
            ressource.getQuantite(),
            ressource.getCreated_at(),
            ressource.getUpdated_at()
        );
    }
    private RapportResponseDto convertToRapportResponseDto(Rapport rapport) {
        return new RapportResponseDto(
            rapport.getId(),
            rapport.getReference(),
            rapport.getPieceJointe(),
            rapport.getCreated_at(),
            rapport.getUpdated_at()
        );
    }

    
}
