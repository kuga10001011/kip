package org.kuga.kip.security.jwt;

import org.json.JSONArray;
import org.json.JSONObject;
import org.kuga.kip.user.ERole;
import org.kuga.kip.user.Role;
import org.kuga.kip.user.RoleRepository;
import org.kuga.kip.user.User;
import org.kuga.kip.user.UserDetailsImpl;
import org.kuga.kip.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:49155")
@RestController
@RequestMapping("/auth")
@Transactional
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<Map<String, Object>> authenticateUser(@RequestBody String requestString) {
        JSONObject requestObject = new JSONObject(requestString);
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestObject.getString("username"), requestObject.getString("password")));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Set<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("token", jwt);
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", userDetails.getId());
        userMap.put("username", userDetails.getUsername());
        userMap.put("password", requestObject.getString("password"));
        userMap.put("email", userDetails.getEmail());
        userMap.put("roles", roles);
        responseMap.put("user", userMap);
        return ResponseEntity.ok(responseMap);
    }

    @PostMapping(value = "/signup", consumes = "application/json")
    public ResponseEntity<User> registerUser(@RequestBody String requestString) {
        try {
            JSONObject requestObject = new JSONObject(requestString);
            User user = new User(requestObject.getString("username"), passwordEncoder.encode(requestObject.getString("password")), requestObject.getString("email"));

            Set<Role> roles = new HashSet<>();

            if (!requestObject.has("roles")) {
                Role userRole = roleRepository.findRoleByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("role not found"));
                roles.add(userRole);
            }
            else {
                JSONArray rolesArray = requestObject.getJSONArray("roles");
                for (Object o : rolesArray) {
                    String role = o.toString();
                    switch (role) {
                        case "ADMIN":
                            Role adminRole = roleRepository.findRoleByName(ERole.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("role not found"));
                            roles.add(adminRole);
                            break;
                        default:
                            Role userRole = roleRepository.findRoleByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("role not found"));
                            roles.add(userRole);
                            break;
                    }
                }
            }

            user.setRoles(roles);
            User _user = userRepository.save(user);
            return new ResponseEntity<>(_user, HttpStatus.CREATED);
        }
        catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
