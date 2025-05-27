package com.missions_back.missions_back.configuration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.missions_back.missions_back.model.Grade;
import com.missions_back.missions_back.model.Permission;
import com.missions_back.missions_back.model.Role;
import com.missions_back.missions_back.model.User;
import com.missions_back.missions_back.repository.GradeRepo;
import com.missions_back.missions_back.repository.PermissionRepo;
import com.missions_back.missions_back.repository.RoleRepo;
import com.missions_back.missions_back.repository.UserRepo;

@Configuration
public class AdminInitializer {
    @Value("${admin.username:admin}")
    private String adminUsername;

    @Value("${admin.email:admin@example.com}")
    private String adminEmail;

    @Value("${admin.password:Password}")
    private String adminPassword;
    
    @Value("${admin.matricule:admin}")
    private String adminMatricule;

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PermissionRepo permissionRepo;
    private final PasswordEncoder passwordEncoder;
    private final GradeRepo gradeRepo;

    @Autowired
    public AdminInitializer(
            GradeRepo gradeRepo,
            UserRepo userRepo,
            RoleRepo roleRepo,
            PermissionRepo permissionRepo,
            PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.permissionRepo = permissionRepo;
        this.passwordEncoder = passwordEncoder;
        this.gradeRepo = gradeRepo;
    }

    @Bean
    public CommandLineRunner initializeAdmin() {
        return args -> {
            // Vérifier si l'utilisateur admin existe déjà
            Optional<User> existingAdmin = userRepo.findByUsername(adminUsername);
            if (existingAdmin.isPresent()) {
                System.out.println("L'administrateur existe déjà.");
                return;
            }

            // Vérifier et créer les permissions de base si elles n'existent pas
            List<Permission> adminPermissions = createBasicPermissions();

            // Vérifier et créer le rôle admin s'il n'existe pas
            Role adminRole = createAdminRole(adminPermissions);

            // Vérifier et créer le grade admin s'il n'existe pas
            Grade adminGrade = createAdminGrade();

            // Créer l'utilisateur admin
            createAdminUser(adminRole, adminGrade);
            System.out.println("L'administrateur a été créé avec succès.");
        };
    }

    private Grade createAdminGrade() {
        Optional<Grade> existingGrade = gradeRepo.findByName("SOUS DIRECTEUR");
        if (existingGrade.isPresent()) {
            return existingGrade.get();
        } else {
            Grade adminGrade = new Grade();
            adminGrade.setName("SOUS DIRECTEUR");
            adminGrade.setCode("SD");
            adminGrade.setFraisInterne(23000L);
            adminGrade.setFraisExterne(25000L);
            adminGrade.setCreated_at(LocalDateTime.now());
            adminGrade.setUpdated_at(LocalDateTime.now());
            adminGrade.setActif(true);

            Grade savedGrade = gradeRepo.save(adminGrade);
            System.out.println("Grade crée : " + adminGrade.getName());
            return savedGrade;
        }
    }
    private List<Permission> createBasicPermissions() {
        List<Permission> permissions = new ArrayList<>();
        String[][] permissionData = {
    {"MANAGE_USERS", "Gérer les utilisateurs", "USER_MANAGE", "valeur1"},
    {"MANAGE_ROLES", "Gérer les rôles", "ROLE_MANAGE", "valeur2"},
    {"MANAGE_PERMISSIONS", "Gérer les permissions", "PERMISSION_MANAGE", "valeur3"},
    {"VIEW_USERS", "Voir les utilisateurs", "USER_VIEW", "valeur4"},
    {"VIEW_ROLES", "Voir les rôles", "ROLE_VIEW", "valeur5"},
    {"VIEW_PERMISSIONS", "Voir les permissions", "PERMISSION_VIEW", "valeur6"}
};

        for (String[] data : permissionData) {
            String name = data[0];
            String url = data[1];
            String code = data[2];
            String icone = data[3];

            Optional<Permission> existingPermission = permissionRepo.findByCode(code);
            if (existingPermission.isPresent()) {
                permissions.add(existingPermission.get());
            } else {
                Permission permission = new Permission();
                permission.setCode(code);
                permission.setName(name);
                permission.setUrl(url);
                permission.setIcone(icone);
                permission.setActif(true);
                permission.setCreated_at(LocalDateTime.now());
                permission.setUpdated_at(LocalDateTime.now());
                
                permissions.add(permissionRepo.save(permission));
                System.out.println("Permission créée: " + name);
            }
        }
        return permissionRepo.findAll();
    }

    private Role createAdminRole(List<Permission> permissions) {
        Optional<Role> existingRole = roleRepo.findByCode("ADMIN");
        if (existingRole.isPresent()) {
            Role role = existingRole.get();
            // Mettre à jour les permissions si nécessaire
            role.setPermissions(permissions);
            return roleRepo.save(role);
        } else {
            Role adminRole = new Role();
            adminRole.setName("Administrateur");
            adminRole.setCode("ADMIN");
            adminRole.setPermissions(permissions);
            adminRole.setActif(true);
            adminRole.setCreated_at(LocalDateTime.now());
            adminRole.setUpdated_at(LocalDateTime.now());
            
            Role savedRole = roleRepo.save(adminRole);
            System.out.println("Rôle admin créé avec " + permissions.size() + " permissions");
            return savedRole;
        }
    }

    private void createAdminUser(Role adminRole, Grade adminGrade) {
        User adminUser = new User();
        adminUser.setUsername(adminUsername);
        adminUser.setEmail(adminEmail);
        adminUser.setPassword(passwordEncoder.encode(adminPassword));
        adminUser.setRole(adminRole);
        adminUser.setMatricule(adminMatricule);
        adminUser.setQuotaAnnuel(0L); // Initialiser le quota annuel à 0
        adminUser.setActif(true);
        adminUser.setGrade(adminGrade);
        adminUser.setCreatedAt(LocalDateTime.now());
        adminUser.setUpdatedAt(LocalDateTime.now());
        
        userRepo.save(adminUser);
        System.out.println("Utilisateur administrateur créé: " + adminUsername);
    }
}
