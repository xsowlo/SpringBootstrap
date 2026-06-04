package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailWithRoles(email);
        if (user == null) {
            throw new UsernameNotFoundException("Not naideno: " + email);
        }
        return user;
    }

    @Override
    public void save(User user) {
        encodePasswordIfPresent(user);
        Set<Role> roles = determineRoles(user);
        user.setRoles(roles);
        userRepository.save(user);
    }

    private void encodePasswordIfPresent(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
    }

    private Set<Role> determineRoles(User user) {
        Set<Role> roles = new HashSet<>();
        String roleInput = user.getRole();

        if (roleInput == null || roleInput.isEmpty()) {
            return getDefaultRoles();
        }

        String[] selectedRoles = roleInput.split(",");

        for (String selectedRole : selectedRoles) {
            String roleName = selectedRole.trim();

            if (isAdminRole(roleName)) {
                addRoleByName(roles, Role.ROLE_ADMIN);
            } else if (isUserRole(roleName)) {
                addRoleByName(roles, Role.ROLE_USER);
            }
        }
        if (roles.isEmpty()) {
            return getDefaultRoles();
        }

        return roles;
    }

    private boolean isAdminRole(String roleName) {
        return "ADMIN".equals(roleName);
    }

    private boolean isUserRole(String roleName) {
        return "USER".equals(roleName);
    }

    private void addRoleByName(Set<Role> roles, String roleName) {
        Role role = roleRepository.findByName(roleName);
        if (role != null) {
            roles.add(role);
        }
    }

    private Set<Role> getDefaultRoles() {
        Set<Role> roles = new HashSet<>();
        addRoleByName(roles, Role.ROLE_USER);
        return roles;
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User findByUsername(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void update(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepository.save(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}