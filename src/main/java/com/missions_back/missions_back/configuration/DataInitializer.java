package com.missions_back.missions_back.configuration;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.missions_back.missions_back.dto.RegisterUserDto;
import com.missions_back.missions_back.model.Fonction;
import com.missions_back.missions_back.model.Rang;
import com.missions_back.missions_back.model.Role;
import com.missions_back.missions_back.model.RoleEnum;
import com.missions_back.missions_back.model.User;
import com.missions_back.missions_back.repository.FonctionRepository;
import com.missions_back.missions_back.repository.RangRepository;
import com.missions_back.missions_back.repository.RoleRepo;
import com.missions_back.missions_back.repository.UserRepo;
import com.missions_back.missions_back.service.RoleService;

import jakarta.annotation.PostConstruct;

@Service
public class DataInitializer {

    private final Logger logger = LoggerFactory.getLogger(RoleService.class);

    private final RoleRepo roleRepo;
    private final UserRepo userRepo;
    private final RoleService roleService;
    private final FonctionRepository fonctionRepo;
    private final RangRepository rangRepo;
    private PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepo roleRepo, UserRepo userRepo, RoleService roleService, 
                          PasswordEncoder passwordEncoder, FonctionRepository fonctionRepo, RangRepository rangRepo) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.fonctionRepo = fonctionRepo;
        this.rangRepo = rangRepo;
    }

    @PostConstruct
    void init() {
        initRangs();
        initFonctions();
        initRoles();
        initAdmin();
    }

    private void initRangs() {
        // Initialisation des rangs avec leurs codes et frais
        Map<String, RangData> rangDataMap = Map.of(
            "PCA", new RangData("Président du Conseil d'administration", new BigDecimal("70000"), new BigDecimal("90000")),
            "MCA", new RangData("Membre du Conseil d'administration", new BigDecimal("65000"), new BigDecimal("85000")),
            "DG", new RangData("Directeur Général", new BigDecimal("65000"), new BigDecimal("85000")),
            "DGA", new RangData("Directeur Général Adjoint", new BigDecimal("60000"), new BigDecimal("80000")),
            "D", new RangData("Directeur", new BigDecimal("50000"), new BigDecimal("75000")),
            "SD", new RangData("Sous Directeur", new BigDecimal("40000"), new BigDecimal("70000")),
            "CS", new RangData("Chef Service", new BigDecimal("35000"), new BigDecimal("65000")),
            "CB", new RangData("Chef de bureau", new BigDecimal("30000"), new BigDecimal("60000")),
            "CA", new RangData("Cadre d'Appui", new BigDecimal("30000"), new BigDecimal("60000")),
            "PA", new RangData("Personnel d'Appui", new BigDecimal("25000"), new BigDecimal("55000"))
        );

        rangDataMap.forEach((code, data) -> {
            Optional<Rang> existingRang = rangRepo.findByCodeAndActifTrue(code);
            if (existingRang.isEmpty()) {
                Rang rang = new Rang();
                rang.setCode(code);
                rang.setNom(data.nom());
                rang.setFraisInterne(data.fraisInterne());
                rang.setFraisExterne(data.fraisExterne());
                rangRepo.save(rang);
                logger.info("Created new rang: {} - {}", code, data.nom());
            } else {
                logger.info("Rang already exists: {}", code);
            }
        });
    }

    private void initFonctions() {
    // Récupération des rangs pour l'association
    Optional<Rang> rangPCA = rangRepo.findByCodeAndActifTrue("PCA");
    Optional<Rang> rangMCA = rangRepo.findByCodeAndActifTrue("MCA");
    Optional<Rang> rangDG = rangRepo.findByCodeAndActifTrue("DG");
    Optional<Rang> rangDGA = rangRepo.findByCodeAndActifTrue("DGA");
    Optional<Rang> rangD = rangRepo.findByCodeAndActifTrue("D");
    Optional<Rang> rangSD = rangRepo.findByCodeAndActifTrue("SD");
    Optional<Rang> rangCS = rangRepo.findByCodeAndActifTrue("CS");
    Optional<Rang> rangCB = rangRepo.findByCodeAndActifTrue("CB");
    Optional<Rang> rangCA = rangRepo.findByCodeAndActifTrue("CA");
    Optional<Rang> rangPA = rangRepo.findByCodeAndActifTrue("PA");

    // Initialisation des fonctions avec leurs rangs associés
    Map<String, FonctionData> fonctionDataMap = new HashMap<>();
    
    fonctionDataMap.put("Président du Conseil d'administration", new FonctionData("Président du Conseil d'administration", rangPCA.orElse(null)));
    fonctionDataMap.put("Membre du Conseil d'administration", new FonctionData("Membre du Conseil d'administration", rangMCA.orElse(null)));
    fonctionDataMap.put("Directeur Général", new FonctionData("Directeur Général", rangDG.orElse(null)));
    fonctionDataMap.put("Directeur Général Adjoint", new FonctionData("Directeur Général Adjoint", rangDGA.orElse(null)));
    fonctionDataMap.put("Inspecteur", new FonctionData("Inspecteur", rangD.orElse(null)));
    fonctionDataMap.put("Directeur", new FonctionData("Directeur", rangD.orElse(null)));
    fonctionDataMap.put("Responsable de l'Audit", new FonctionData("Responsable de l'Audit", rangD.orElse(null)));
    fonctionDataMap.put("chef de brigade", new FonctionData("chef de brigade", rangD.orElse(null)));
    fonctionDataMap.put("chef de division", new FonctionData("chef de division", rangD.orElse(null)));
    fonctionDataMap.put("Sous Directeur", new FonctionData("Sous Directeur", rangSD.orElse(null)));
    fonctionDataMap.put("Auditeur junior", new FonctionData("Auditeur junior", rangSD.orElse(null)));
    fonctionDataMap.put("Chargé d'étude", new FonctionData("Chargé d'étude", rangSD.orElse(null)));
    fonctionDataMap.put("Cadre PCA", new FonctionData("Cadre PCA", rangSD.orElse(null)));
    fonctionDataMap.put("Chef de cellule", new FonctionData("Chef de cellule", rangSD.orElse(null)));
    fonctionDataMap.put("Chef de brigade Adjoint", new FonctionData("Chef de brigade Adjoint", rangSD.orElse(null)));
    fonctionDataMap.put("Attaché de Direction", new FonctionData("Attaché de Direction", rangSD.orElse(null)));
    fonctionDataMap.put("Chef Service", new FonctionData("Chef Service", rangCS.orElse(null)));
    fonctionDataMap.put("chargée d'étude Assistant", new FonctionData("chargée d'étude Assistant", rangCS.orElse(null)));
    fonctionDataMap.put("chef Secretariat PCA", new FonctionData("chef Secretariat PCA", rangCS.orElse(null)));
    fonctionDataMap.put("Chef d'unité", new FonctionData("Chef d'unité", rangCS.orElse(null)));
    fonctionDataMap.put("chef Secretariat DG", new FonctionData("chef Secretariat DG", rangCS.orElse(null)));
    fonctionDataMap.put("Comptable matière DG", new FonctionData("Comptable matière DG", rangCS.orElse(null)));
    fonctionDataMap.put("auditeur Junior", new FonctionData("auditeur Junior", rangCB.orElse(null)));
    fonctionDataMap.put("Chef de bureau", new FonctionData("Chef de bureau", rangCB.orElse(null)));
    fonctionDataMap.put("Chef Secretariat", new FonctionData("Chef Secretariat", rangCB.orElse(null)));
    fonctionDataMap.put("Comptable matière", new FonctionData("Comptable matière", rangCB.orElse(null)));
    fonctionDataMap.put("Cadre", new FonctionData("Cadre", rangCA.orElse(null)));
    fonctionDataMap.put("Agent de maîtrise", new FonctionData("Agent de maîtrise", rangPA.orElse(null)));
    fonctionDataMap.put("Agent de liaison", new FonctionData("Agent de liaison", rangPA.orElse(null)));
    fonctionDataMap.put("Chauffeur", new FonctionData("Chauffeur", rangPA.orElse(null)));

    fonctionDataMap.forEach((nom, data) -> {
        Optional<Fonction> existingFonction = fonctionRepo.findByNomAndActifTrue(nom);
        if (existingFonction.isEmpty() && data.rang() != null) {
            Fonction fonction = new Fonction();
            fonction.setNom(data.nom());
            fonction.setRang(data.rang());
            fonctionRepo.save(fonction);
            logger.info("Created new fonction: {}", nom);
        } else if (data.rang() == null) {
            logger.warn("Skipping fonction {} - associated rang not found", nom);
        } else {
            logger.info("Fonction already exists: {}", nom);
        }
    });
}

    private void initRoles() {
        Map<RoleEnum, String> roleDescriptionMap = Map.of(
                RoleEnum.AGENT_ART, "Default user role",
                RoleEnum.AGENT_RESSOURCES_HUMAINES, "Administrator role",
                RoleEnum.DIRECTEUR_RESSOURCES_HUMAINES, "Super Administrator role",
                RoleEnum.DIRECTEUR_PATRIMOINE, "Directeur du patrimoine",
                RoleEnum.ADMIN, "l'administrateur"
        );

        roleDescriptionMap.forEach((roleName, description) ->
                roleRepo.findByName(roleName).ifPresentOrElse(
                        role -> logger.info("Role already exists: {}", role),
                        () -> {
                            Role roleToCreate = new Role()
                                    .setName(roleName)
                                    .setDescription(description);
                            roleRepo.save(roleToCreate);
                            logger.info("Created new role: {}", roleToCreate);
                        }
                )
        );
    }

    private void initAdmin(){
        // Récupération des entités nécessaires
        Optional<Role> optionalRole = roleService.findByName(RoleEnum.ADMIN);
        Optional<Fonction> optionalFonction = fonctionRepo.findByNomAndActifTrue("Directeur Général");
        Optional<Rang> optionalRang = rangRepo.findByCodeAndActifTrue("DG");
        Optional<User> optionalUser = userRepo.findByEmail("super.admin@email.com");

        if (optionalRole.isEmpty() || optionalUser.isPresent()) {
            logger.info("Admin user already exists or ADMIN role not found");
            return;
        }

        if (optionalFonction.isEmpty()) {
            logger.warn("Fonction 'Directeur Général' not found for admin user");
            return;
        }

        if (optionalRang.isEmpty()) {
            logger.warn("Rang 'DG' not found for admin user");
            return;
        }

        // Création du DTO avec tous les IDs nécessaires
        RegisterUserDto userDto = new RegisterUserDto(
            "MAT001",                           // matricule
            "super.admin@email.com",           // email
            "123456",                          // password
            "Super Admin",                     // username
            30L,                              // quotaAnnuel
            optionalRole.get().getId(),       // roleId
            optionalFonction.get().getId(),   // fonctionId
            optionalRang.get().getId()        // rangId
        );

        // Création de l'utilisateur
        User user = new User();
        user.setMatricule(userDto.matricule());
        user.setEmail(userDto.email());
        user.setPassword(passwordEncoder.encode(userDto.password()));
        user.setUsername(userDto.username());
        user.setQuotaAnnuel(userDto.quotaAnnuel());
        user.setRole(optionalRole.get());
        user.setFonction(optionalFonction.get());

        userRepo.save(user);
        logger.info("Created admin user with role ADMIN, fonction 'Directeur Général' and rang 'DG'");
    }

    // Classes record pour les données
    private record RangData(String nom, BigDecimal fraisInterne, BigDecimal fraisExterne) {}
    private record FonctionData(String nom, Rang rang) {}
}