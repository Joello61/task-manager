package com.example.task_manager.service;

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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
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

        User user = userRepository.findByEmail(registerDto.getEmail()).orElse(null);
        if (user != null) {
            throw new UserAlreadyExistException();
        }

        CreateUserDto createUserDto = userMapper.toCreateDto(registerDto);
        createUserDto.setPassword(passwordEncoder.encode(createUserDto.getPassword()));
        userService.save(createUserDto);

    }

    public LoginResponseDto login(LoginDto loginDto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );

        UserDetailsImpl userDetails =
                (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtil.generateToken(userDetails.getUsername());

        UserResponseDto userResponseDto =
                userMapper.toResponseDto(userDetails.user());

        return new LoginResponseDto(userResponseDto, jwt);
    }
}
