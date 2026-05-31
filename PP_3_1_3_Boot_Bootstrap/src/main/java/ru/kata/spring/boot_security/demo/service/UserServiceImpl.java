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

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            Role userRole = roleRepository.findByName("ROLE_USER");
            if (userRole != null) {
                roles.add(userRole);
            }
            user.setRoles(roles);
        }

        if (user.getRole() != null && !user.getRole().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            if ("ADMIN".equals(user.getRole())) {
                Role adminRole = roleRepository.findByName("ROLE_ADMIN");
                if (adminRole != null) roles.add(adminRole);
                Role userRole = roleRepository.findByName("ROLE_USER");
                if (userRole != null) roles.add(userRole);
            } else {
                Role userRole = roleRepository.findByName("ROLE_USER");
                if (userRole != null) roles.add(userRole);
            }
            user.setRoles(roles);
        } else if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Role userRole = roleRepository.findByName("ROLE_USER");
            if (userRole != null) {
                user.setRoles(Set.of(userRole));
            }
        }


        userRepository.save(user);
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