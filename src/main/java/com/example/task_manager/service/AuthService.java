package com.example.task_manager.service;

import com.example.task_manager.dto.auth.ChangePasswordDto;
import com.example.task_manager.dto.auth.LoginDto;
import com.example.task_manager.dto.auth.LoginResponseDto;
import com.example.task_manager.dto.auth.RegisterDto;
import com.example.task_manager.dto.user.CreateUserDto;
import com.example.task_manager.dto.user.UserResponseDto;
import com.example.task_manager.entity.User;
import com.example.task_manager.exception.UserAlreadyExistException;
import com.example.task_manager.mapper.UserMapper;
import com.example.task_manager.repository.UserRepository;
import com.example.task_manager.security.JwtUtil;
import com.example.task_manager.security.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(
            UserService userService,
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil
    ) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public void register(RegisterDto registerDto) {
        log.info("Tentative d'inscription pour l'email: {}", registerDto.getEmail());
        User user = userRepository.findByEmail(registerDto.getEmail()).orElse(null);
        if (user != null) {
            log.warn("Échec de l'inscription: l'utilisateur avec l'email {} existe déjà", registerDto.getEmail());
            throw new UserAlreadyExistException();
        }

        CreateUserDto createUserDto = userMapper.toCreateDto(registerDto);
        createUserDto.setPassword(passwordEncoder.encode(createUserDto.getPassword()));
        userService.save(createUserDto);
        log.info("Utilisateur inscrit avec succès: {}", registerDto.getEmail());

    }

    public LoginResponseDto login(LoginDto loginDto) {
        log.info("Tentative de connexion pour l'email: {}", loginDto.getEmail());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );

        UserDetailsImpl userDetails =
                (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtil.generateToken(userDetails.getUsername());
        log.info("Connexion réussie pour l'utilisateur: {}", loginDto.getEmail());

        UserResponseDto userResponseDto =
                userMapper.toResponseDto(userDetails.user());

        return new LoginResponseDto(userResponseDto, jwt);
    }

    @PreAuthorize("isAuthenticated()")
    public void changePassword(ChangePasswordDto changePasswordDto) {

        User user = getUser();
        log.info("Changement de mot de passe demandé pour l'utilisateur: {}", user.getEmail());


        if (!passwordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword())) {
            log.warn("Échec du changement de mot de passe: ancien mot de passe incorrect pour {}", user.getEmail());
            throw new BadCredentialsException("L'ancien mot de passe est incorrect");
        }

        if (changePasswordDto.getOldPassword().equals(changePasswordDto.getNewPassword())) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit être différent de l'ancien");
        }

        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmPassword())) {
            throw new IllegalArgumentException("Le nouveau mot de passe et la confirmation ne correspondent pas");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userRepository.save(user);
        log.info("Mot de passe mis à jour avec succès pour {}", user.getEmail());
    }

    private static User getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 2. Vérifier si l'utilisateur est authentifié
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new InsufficientAuthenticationException("Vous devez être connecté pour changer votre mot de passe");
        }

        // 3. Récupérer les détails de l'utilisateur
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return userDetails.user();
    }
}
