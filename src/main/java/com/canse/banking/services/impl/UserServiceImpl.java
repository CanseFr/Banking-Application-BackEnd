package com.canse.banking.services.impl;

import com.canse.banking.config.JwtUtils;
import com.canse.banking.dto.AccountDto;
import com.canse.banking.dto.AuthenticationRequest;
import com.canse.banking.dto.AuthenticationResponse;
import com.canse.banking.dto.UserDto;
import com.canse.banking.models.Account;
import com.canse.banking.models.Role;
import com.canse.banking.models.User;
import com.canse.banking.repositories.AccountRepository;
import com.canse.banking.repositories.RoleRepository;
import com.canse.banking.repositories.UserRepository;
import com.canse.banking.services.AccountService;
import com.canse.banking.services.UserService;
import com.canse.banking.validators.ObjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String ROLE_USER = "ROLE_USER";
    private final UserRepository repository;
    private final AccountService accountService;
    private final ObjectValidator<UserDto> validator;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authManager;
    private final RoleRepository roleRepository;

    @Override
    public Integer save(UserDto dto) {
        validator.validate(dto);
        User user = UserDto.toEntity(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        //User savedUser =  repository.save(user);
        return repository.save(user).getId();
    }

    @Override
    @Transactional
    public List<UserDto> findAll() {
        return repository.findAll()
                .stream()
                .map(userDto -> UserDto.fromEntity(userDto))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findById(Integer id) {
        return repository.findById(id)
                .map(UserDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("No User Found With Provided Id" + id));
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public Integer validateAccount(Integer id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No user was found for user account validation"));

        AccountDto account = AccountDto.builder()
                .user(UserDto.fromEntity(user))
                .build();

        var savedAccount = accountService.save(account);

        user.setActive(true);
        user.setAccount(Account.builder().id(savedAccount).build());
        repository.save(user);
        return user.getId();
    }

    @Override
    public Integer invalidateAccount(Integer id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No user waf found for user account validation"));
        user.setActive(false);

        user.setActive(false);
        repository.save(user);
        return user.getId();
    }

    @Override
    @Transactional
    public AuthenticationResponse register(UserDto dto) {
        validator.validate(dto);
        User user = UserDto.toEntity(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        System.out.println("Mot de passe generé -> "+user.getPassword()); // Pour info

        user.setRole(
                findOrCreateRole(ROLE_USER)
        );

        var savedUser =  repository.save(user);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", savedUser.getId());
        claims.put("fullname", savedUser.getFirstname() + " " + savedUser.getLastname());

        String token = jwtUtils.generateToken(savedUser,claims);
        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        final User user = repository.findByEmail(request.getEmail()).get();
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("fullname", user.getFirstname() + " " + user.getLastname());
        final String token = jwtUtils.generateToken(user,claims);
        return AuthenticationResponse.builder()
                        .token(token)
                        .build();
    }

    private Role findOrCreateRole(String roleName){
        Role role = roleRepository.findByName(roleName)
                .orElse(null);
        if (role == null){
            return roleRepository.save(
                    Role.builder()
                            .name(roleName)
                            .build()
            );
        }
            return role;
    }
}
