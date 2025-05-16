// package com.missions_back.missions_back.component;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.ApplicationListener;
// import org.springframework.context.event.ContextRefreshedEvent;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Component;
// import org.springframework.transaction.annotation.Transactional;

// import com.missions_back.missions_back.model.Permission;
// import com.missions_back.missions_back.model.Role;
// import com.missions_back.missions_back.repository.PermissionRepo;
// import com.missions_back.missions_back.repository.RoleRepo;
// import com.missions_back.missions_back.repository.UserRepo;

// import io.jsonwebtoken.lang.Arrays;

// @Component
// public class SetupDataLoader implements ApplicationListener <ContextRefreshedEvent> {
//     Boolean alreadySetup = false;

//     @Autowired
//     private UserRepo userRepo;

//     @Autowired
//     private RoleRepo roleRepo;
//     @Autowired
//     private PermissionRepo permissionRepo;
//     @Autowired
//     private PasswordEncoder passwordEncoder;

//     @Override
//     @Transactional
//     public void onApplicationEvent (ContextRefreshedEvent event) {
//         if (alreadySetup)
//             return;
//         Permission createUserPermission = createPrivilegeIfNotFound("CREATE_USER");
//         Permission readAllUsersPermission = createPrivilegeIfNotFound("READ_USER");
        
//         List <Permission> adminPermissions = Arrays.asList(createUserPermission, readAllUsersPermission);
//         createRoleIfNotFound("ROLE_ADMIN", adminPermissions);

//         Role adminRole = roleRepo.findByName("ROLE_ADMIN").orElseThrow();
//     }
// }
