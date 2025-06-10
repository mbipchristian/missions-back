package com.missions_back.missions_back.configuration;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.missions_back.missions_back.dto.RegisterUserDto;
import com.missions_back.missions_back.model.Rang;
import com.missions_back.missions_back.model.Role;
import com.missions_back.missions_back.model.RoleEnum;
import com.missions_back.missions_back.model.User;
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
    private final RangRepository rangRepo;
    private PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepo roleRepo, UserRepo userRepo, RoleService roleService, 
                          PasswordEncoder passwordEncoder, RangRepository rangRepo) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.rangRepo = rangRepo;
    }

    @PostConstruct
    void init() {
        initRangs();
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
        Optional<Rang> optionalRang = rangRepo.findByCodeAndActifTrue("DG");
        Optional<User> optionalUser = userRepo.findByEmail("super.admin@email.com");

        if (optionalRole.isEmpty() || optionalUser.isPresent()) {
            logger.info("Admin user already exists or ADMIN role not found");
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
            "Sous Directeur",
            optionalRang.get().getId()        // rangId
        );

        // Création de l'utilisateur
        User user = new User();
        user.setMatricule(userDto.matricule());
        user.setEmail(userDto.email());
        user.setPassword(passwordEncoder.encode(userDto.password()));
        user.setUsername(userDto.username());
        user.setFonction(userDto.fonction());
        user.setQuotaAnnuel(userDto.quotaAnnuel());
        user.setRole(optionalRole.get());

        userRepo.save(user);
        logger.info("Created admin user with role ADMIN, fonction 'Sous Directeur' and rang 'DG'");
    }

    // Classes record pour les données
    private record RangData(String nom, BigDecimal fraisInterne, BigDecimal fraisExterne) {}
}