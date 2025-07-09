package com.missions_back.missions_back.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.missions_back.missions_back.dto.MandatAvecPieceJointeResponseDto;
import com.missions_back.missions_back.dto.MandatDto;
import com.missions_back.missions_back.dto.MandatResponseDto;
import com.missions_back.missions_back.dto.PieceJointeResponseDto;
import com.missions_back.missions_back.dto.RapportResponseDto;
import com.missions_back.missions_back.dto.RessourceResponseDto;
import com.missions_back.missions_back.dto.RoleResponseDto;
import com.missions_back.missions_back.dto.UserResponseDto;
import com.missions_back.missions_back.dto.VilleResponseDto;
import com.missions_back.missions_back.model.Mandat;
import com.missions_back.missions_back.model.MandatStatut;
import com.missions_back.missions_back.model.OrdreMission;
import com.missions_back.missions_back.model.OrdreMissionStatut;
import com.missions_back.missions_back.model.PieceJointe;
import com.missions_back.missions_back.model.Rapport;
import com.missions_back.missions_back.model.Ressource;
import com.missions_back.missions_back.model.RoleEnum;
import com.missions_back.missions_back.model.User;
import com.missions_back.missions_back.model.Ville;
import com.missions_back.missions_back.repository.MandatRepo;
import com.missions_back.missions_back.repository.OrdreMissionRepo;
import com.missions_back.missions_back.repository.PieceJointeRepository;
import com.missions_back.missions_back.repository.UserRepo;
import com.missions_back.missions_back.repository.VilleRepo;
import com.missions_back.missions_back.repository.RessourceRepo;
import com.missions_back.missions_back.service.EmailService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class MandatService {
    private final MandatRepo mandatRepo;
    private final UserRepo userRepo;
    private final VilleRepo villeRepo;
    private final RessourceRepo ressourceRepo;
    private final OrdreMissionRepo ordreMissionRepo;
    private final OrdreMissionService ordreMissionService;
    private final PieceJointeRepository pieceJointeRepository;
    private final PieceJointeService pieceJointeService;
    private final EmailService emailService;

    public MandatService(MandatRepo mandatRepo, UserRepo userRepo, VilleRepo villeRepo,
                        RessourceRepo ressourceRepo, OrdreMissionRepo ordreMissionRepo, 
                        OrdreMissionService ordreMissionService, PieceJointeRepository pieceJointeRepository, PieceJointeService pieceJointeService, EmailService emailService) {
        this.mandatRepo = mandatRepo;
        this.userRepo = userRepo;
        this.villeRepo = villeRepo;
        this.ressourceRepo = ressourceRepo;
        this.ordreMissionRepo = ordreMissionRepo;
        this.ordreMissionService = ordreMissionService;
        this.pieceJointeRepository = pieceJointeRepository;
        this.pieceJointeService = pieceJointeService;
        this.emailService = emailService;
    }


    /**
     * Récupérer tous les mandats en attente d'exécution
     */
    public List<MandatResponseDto> getMandatsEnAttenteExecution() {
        List<Mandat> mandats = mandatRepo.findByStatutAndActifTrue(MandatStatut.EN_ATTENTE_EXECUTION);
        return mandats.stream()
                .map(this::convertToMandatResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer tous les mandats en cours
     */
    public List<MandatResponseDto> getMandatsEnCours() {
        List<Mandat> mandats = mandatRepo.findByStatutAndActifTrue(MandatStatut.EN_COURS);
        return mandats.stream()
                .map(this::convertToMandatResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer tous les mandats achevés
     */
    public List<MandatResponseDto> getMandatsAcheves() {
        List<Mandat> mandats = mandatRepo.findByStatutAndActifTrue(MandatStatut.ACHEVE);
        return mandats.stream()
                .map(this::convertToMandatResponseDto)
                .collect(Collectors.toList());
    }

@Transactional
public MandatResponseDto createMandat(MandatDto mandatDto, Long createdByUserId) {
    if (mandatRepo.findByReference(mandatDto.reference()).isPresent()) {
        throw new IllegalArgumentException("Mandat déjà existant avec cette référence");
    }

    // Vérifier que l'utilisateur créateur existe
    if (!userRepo.existsById(createdByUserId)) {
        throw new EntityNotFoundException("Utilisateur créateur non trouvé");
    }

    Mandat mandat = new Mandat();
    mandat.setReference(mandatDto.reference());
    mandat.setObjectif(mandatDto.objectif());
    mandat.setMissionDeControle(mandatDto.missionDeControle());
    mandat.setDateDebut(mandatDto.dateDebut());
    mandat.setDateFin(mandatDto.dateFin());
    mandat.setDuree(mandatDto.duree());
    mandat.setStatut(MandatStatut.EN_ATTENTE_CONFIRMATION);
    mandat.setCreated_at(LocalDateTime.now());
    mandat.setUpdated_at(LocalDateTime.now());
    mandat.setActif(true);
    
    // Définir automatiquement le créateur
    mandat.setCreatedByUserId(createdByUserId);

    // Ajouter les associations users
    if (mandatDto.userIds() != null && !mandatDto.userIds().isEmpty()) {
        List<User> users = userRepo.findAllById(mandatDto.userIds());
        if (users.size() != mandatDto.userIds().size()) {
            throw new EntityNotFoundException("Un ou plusieurs utilisateurs n'ont pas été trouvés");
        }
        mandat.setUsers(users);
    }

    // Ajouter les associations villes
    if (mandatDto.villeIds() != null && !mandatDto.villeIds().isEmpty()) {
        List<Ville> villes = villeRepo.findAllById(mandatDto.villeIds());
        if (villes.size() != mandatDto.villeIds().size()) {
            throw new EntityNotFoundException("Une ou plusieurs villes n'ont pas été trouvées");
        }
        mandat.setVilles(villes);
    }

    // Ajouter les associations ressources
    if (mandatDto.ressourceIds() != null && !mandatDto.ressourceIds().isEmpty()) {
        List<Ressource> ressources = ressourceRepo.findAllById(mandatDto.ressourceIds());
        if (ressources.size() != mandatDto.ressourceIds().size()) {
            throw new EntityNotFoundException("Une ou plusieurs ressources n'ont pas été trouvées");
        }
        mandat.setRessources(ressources);
    }

    Mandat createdMandat = mandatRepo.save(mandat);
    // Notifier tous les utilisateurs du mandat
    if (mandat.getUsers() != null && !mandat.getUsers().isEmpty()) {
        emailService.sendEmail(
            mandat.getUsers().stream().map(User::getEmail).toList(),
            "Nouveau mandat en attente de confirmation",
            "Le mandat " + mandat.getReference() + " est en attente de confirmation."
        );
    }
    // Notifier le DRH si le statut est EN_ATTENTE_CONFIRMATION
    if (mandat.getStatut() == MandatStatut.EN_ATTENTE_CONFIRMATION) {
        var drhList = userRepo.findByRole_NameAndActifTrue(com.missions_back.missions_back.model.RoleEnum.DIRECTEUR_RESSOURCES_HUMAINES);
        if (!drhList.isEmpty()) {
            emailService.sendEmail(
                drhList.stream().map(User::getEmail).toList(),
                "Mandat à confirmer",
                "Un mandat (" + mandat.getReference() + ") attend votre confirmation."
            );
        }
    }
    return convertToMandatResponseDto(createdMandat);
}

@Transactional
public MandatResponseDto confirmerMandat(Long mandatId, Authentication authentication) {
    // Extraire l'utilisateur depuis le token JWT
    String userEmail = authentication.getName();
    User user = userRepo.findByEmail(userEmail)
        .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
    
    Mandat mandat = mandatRepo.findById(mandatId)
        .orElseThrow(() -> new EntityNotFoundException("Mandat non trouvé"));

    if (mandat.getStatut() != MandatStatut.EN_ATTENTE_CONFIRMATION) {
        throw new IllegalArgumentException("Ce mandat ne peut pas être confirmé dans son état actuel");
    }

    // Valider les utilisateurs pour identifier les conformes et non conformes
    // ValidationResult validationResult = validerUtilisateursPourMandat(mandat);
    
    // Le mandat change de statut dans tous les cas
    mandat.setStatut(MandatStatut.EN_ATTENTE_EXECUTION);
    mandat.setConfirmeParUserId(user.getId());
    mandat.setDateConfirmation(LocalDateTime.now());
    
    Mandat confirmedMandat = mandatRepo.save(mandat);

    // Générer automatiquement les ordres de mission seulement pour les utilisateurs conformes
    // if (!validationResult.getUtilisateursConformes().isEmpty()) {
    //     genererOrdresMissionPourUtilisateurs(confirmedMandat, validationResult.getUtilisateursConformes());
    // }

    // Créer la réponse avec les informations de validation
    MandatResponseDto response = convertToMandatResponseDto(confirmedMandat);
    
    // Ajouter des informations sur les utilisateurs non conformes si nécessaire
    // if (!validationResult.getUtilisateursNonConformes().isEmpty()) {
    //     // Vous pouvez ajouter ces informations dans la réponse ou les logger
    //     String messageNonConformes = "Utilisateurs non conformes : " + 
    //         String.join(", ", validationResult.getUtilisateursNonConformes().stream()
    //             .map(u -> u.getName() + " (" + validationResult.getErreursParUtilisateur().get(u.getId()) + ")")
    //             .toList());
        
    //     // Si votre MandatResponseDto a un champ pour les messages, utilisez-le
    //     // response.setMessageValidation(messageNonConformes);
        
    //     // Sinon, vous pouvez logger l'information
    //     System.out.println("Mandat " + mandat.getReference() + " confirmé partiellement. " + messageNonConformes);
    // }

    return response;
}

/**
 * Valide tous les utilisateurs d'un mandat et sépare les conformes des non conformes
 */
// private ValidationResult validerUtilisateursPourMandat(Mandat mandat) {
//     List<User> utilisateursConformes = new ArrayList<>();
//     List<User> utilisateursNonConformes = new ArrayList<>();
//     Map<Long, String> erreursParUtilisateur = new HashMap<>();
    
//     for (User user : mandat.getUsers()) {
//         String erreur = validerUtilisateur(user, mandat);
        
//         if (erreur == null) {
//             utilisateursConformes.add(user);
//         } else {
//             utilisateursNonConformes.add(user);
//             erreursParUtilisateur.put(user.getId(), erreur);
//         }
//     }
    
//     return new ValidationResult(utilisateursConformes, utilisateursNonConformes, erreursParUtilisateur);
// }

// /**
//  * Valide un utilisateur spécifique pour un mandat
//  */
// private String validerUtilisateur(User user, Mandat mandat) {
//     // Vérification 1 : Ordre de mission en cours
//     Optional<OrdreMission> dernierOrdreMission = ordreMissionRepo
//         .findTopByUserAndActifTrueOrderByDateFinDesc(user);
    
//     if (dernierOrdreMission.isPresent()) {
//         Date dateFinDernierOrdre = dernierOrdreMission.get().getDateFin();
//         Date dateDebutMandat = mandat.getDateDebut();
        
//         // Convert to LocalDate for comparison
//         LocalDate finDernierOrdre = dateFinDernierOrdre.toInstant()
//             .atZone(ZoneId.systemDefault())
//             .toLocalDate();
//         LocalDate debutMandat = dateDebutMandat.toInstant()
//             .atZone(ZoneId.systemDefault())
//             .toLocalDate();
        
//         if (finDernierOrdre.isAfter(debutMandat)) {
//             return "Ordre de mission en cours jusqu'au " + 
//                    finDernierOrdre.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
//         }
//     }
    
//     // Vérification 2 : Quota annuel
//     Long quotaApresMandat = user.getQuotaAnnuel() + mandat.getDuree();
//     if (quotaApresMandat > 100) {
//         return "Dépassement du quota annuel (actuel: " + user.getQuotaAnnuel() + 
//                " jours, après mandat: " + quotaApresMandat + " jours)";
//     }
    
//     return null; // Utilisateur conforme
// }
// @Transactional
// private void genererOrdresMissionPourUtilisateurs(Mandat mandat, List<User> utilisateursConformes) {
//     for (User user : utilisateursConformes) {
//         OrdreMission ordreMission = new OrdreMission();
        
//         // Générer une référence unique
//         String reference = "OM-" + mandat.getReference() + "-" + user.getMatricule();
//         ordreMission.setReference(reference);
//         ordreMission.setObjectif(mandat.getObjectif());
//         ordreMission.setModePaiement("VIREMENT"); // Valeur par défaut
//         ordreMission.setDevise("FCFA"); // Valeur par défaut  
//         ordreMission.setTauxAvance(50L); // Valeur par défaut
//         ordreMission.setDateDebut(mandat.getDateDebut());
//         ordreMission.setDateFin(mandat.getDateFin());
//         ordreMission.setDuree((long) mandat.getDuree());
        
//         // Calculs financiers par défaut
//         Long decompteTotal = calculateDecompteTotal(user, mandat);
//         Long decompteAvance = decompteTotal * ordreMission.getTauxAvance() / 100;
//         Long decompteRelicat = decompteTotal - decompteAvance;
        
//         ordreMission.setDecompteTotal(decompteTotal);
//         ordreMission.setDecompteAvance(decompteAvance);
//         ordreMission.setDecompteRelicat(decompteRelicat);
        
//         ordreMission.setStatut(OrdreMissionStatut.EN_ATTENTE_JUSTIFICATIF);
//         ordreMission.setUser(user);
//         ordreMission.setMandat(mandat);
//         ordreMission.setActif(true);
        
//         ordreMissionRepo.save(ordreMission);
        
//         // Mettre à jour le quota de l'utilisateur
//         user.setQuotaAnnuel(user.getQuotaAnnuel() + mandat.getDuree());
//         userRepo.save(user);
//     }
// }
//     private Long calculateDecompteTotal(User user, Mandat mandat) {
//         Long fraisInterne = user.getRang().getFraisInterne().longValue();
    
//         // Multiplier par la durée du mandat
//         return fraisInterne * mandat.getDuree();
//     }

    public List<MandatResponseDto> getMandatsEnAttenteConfirmation() {
        List<Mandat> mandats = mandatRepo.findByStatutAndActifTrue(MandatStatut.EN_ATTENTE_CONFIRMATION);
        return mandats.stream()
                .map(this::convertToMandatResponseDto)
                .collect(Collectors.toList());
    }

    public List<MandatResponseDto> getMandatsVisiblesPourUtilisateur(Long userId) {
        List<Mandat> mandats = mandatRepo.findByUsersIdAndStatutNotAndActifTrue(userId, MandatStatut.EN_ATTENTE_CONFIRMATION);
        return mandats.stream()
                .map(this::convertToMandatResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void mettreAJourStatutsAutomatiquement() {
        Date maintenant = new Date();
        
        // Mettre à jour les mandats EN_ATTENTE_EXECUTION vers EN_COURS
        List<Mandat> mandatsADemarrer = mandatRepo.findByStatutAndDateDebutLessThanEqual(
            MandatStatut.EN_ATTENTE_EXECUTION, maintenant);
        
        for (Mandat mandat : mandatsADemarrer) {
            mandat.setStatut(MandatStatut.EN_COURS);
            mandatRepo.save(mandat);
        }
        
        // Mettre à jour les mandats EN_COURS vers ACHEVE
        List<Mandat> mandatsATerminer = mandatRepo.findByStatutAndDateFinLessThanEqual(
            MandatStatut.EN_COURS, maintenant);
        
        for (Mandat mandat : mandatsATerminer) {
            mandat.setStatut(MandatStatut.ACHEVE);
            mandatRepo.save(mandat);
        }
    }

    // Autres méthodes existantes...
    public List<MandatResponseDto> getAllMandats() {
        List<Mandat> mandats = mandatRepo.findByActifTrue();
        return mandats.stream()
                .map(this::convertToMandatResponseDto)
                .collect(Collectors.toList());
    }

    public MandatResponseDto getMandatById(Long id) {
        Mandat mandat = mandatRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mandat non trouvé avec l'ID: " + id));
        return convertToMandatResponseDto(mandat);
    }

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

        // Récupérer les informations du créateur
        String createdBy = "Utilisateur inconnu";
        if (mandat.getCreatedByUserId() != null) {
            Optional<User> creator = userRepo.findById(mandat.getCreatedByUserId());
            if (creator.isPresent()) {
                createdBy = creator.get().getUsername();
            }
        }

        return new MandatResponseDto(
            mandat.getId(),
            mandat.getReference(),
            mandat.getObjectif(),
            mandat.isMissionDeControle(),
            mandat.getDateDebut(),
            mandat.getDateFin(),
            mandat.getDuree(),
            mandat.getStatut(),
            mandat.getCreated_at(),
            mandat.getUpdated_at(),
            userDtos,
            villeDtos,
            ressourceDtos,
            rapportDto,
            createdBy,
            mandat.getConfirmeParUserId(),
            mandat.getDateConfirmation(),
            userDtos.size(), // usersCount
            villeDtos.size(), // villesCount
            ressourceDtos.size() // ressourcesCount
            
        );
    }
    
    // Méthodes de conversion auxiliaires
    private UserResponseDto convertToUserResponseDto(User user) {
        RoleResponseDto roleDto = user.getRole() != null ?
            new RoleResponseDto(
                user.getRole().getId(),
                user.getRole().getName(),
                user.getRole().getDescription(),
                user.getRole().getCreated_at(),
                user.getRole().getUpdated_at()
            ) : null;

        return new UserResponseDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getMatricule(),
            user.getQuotaAnnuel(),
            roleDto,
            user.getFonction(),
            user.getCreated_at(),
            user.getUpdated_at()
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
            ressource.getCreated_at(),
            ressource.getUpdated_at()
        );
    }
    
    private RapportResponseDto convertToRapportResponseDto(Rapport rapport) {
        return new RapportResponseDto(
            rapport.getId(),
            rapport.getReference(),
            rapport.getCreated_at(),
            rapport.getUpdated_at()
        );
    }
}