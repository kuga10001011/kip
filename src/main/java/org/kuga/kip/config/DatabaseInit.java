package org.kuga.kip.config;

import org.kuga.kip.datastore.DatastoreRepository;
import org.kuga.kip.datastore.field.FieldRepository;
import org.kuga.kip.user.ERole;
import org.kuga.kip.user.Role;
import org.kuga.kip.user.RoleRepository;
import org.kuga.kip.user.User;
import org.kuga.kip.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DatabaseInit implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInit.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    FieldRepository fieldRepository;

    @Autowired
    DatastoreRepository datastoreRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String ... args) throws Exception {

        if (!roleRepository.existsByName(ERole.ROLE_ADMIN)) {
            Role adminRole = new Role();
            adminRole.setName(ERole.ROLE_ADMIN);

            Role userRole = new Role();
            userRole.setName(ERole.ROLE_USER);

            roleRepository.save(adminRole);
            roleRepository.save(userRole);
        }

        if (!userRepository.existsByUsername("kip_admin")) {
            User kipAdmin = new User("kip_admin", passwordEncoder.encode("53cr3t"), "kip_admin@gmail.com");
            User kipUser = new User("kip_user", passwordEncoder.encode("53cr3t"), "kip_user@gmail.com");

            Optional<Role> adminRole = roleRepository.findRoleByName(ERole.ROLE_ADMIN);
            Optional<Role> userRole = roleRepository.findRoleByName(ERole.ROLE_USER);

            if (adminRole.isPresent() && userRole.isPresent()) {
                kipAdmin.addRoles(adminRole.get(), userRole.get());
                kipUser.addRoles(userRole.get());
            }

            userRepository.save(kipAdmin);
            userRepository.save(kipUser);
        }
    }

}
