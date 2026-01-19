package com.example.task_manager.service;

import com.example.task_manager.dto.user.CreateUserDto;
import com.example.task_manager.dto.user.UpdateUserDto;
import com.example.task_manager.dto.user.UserResponseDto;
import com.example.task_manager.entity.User;
import com.example.task_manager.exception.UserAlreadyExistException;
import com.example.task_manager.exception.UserNotFoundException;
import com.example.task_manager.mapper.UserMapper;
import com.example.task_manager.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public UserResponseDto save(CreateUserDto userDto) {
        log.info("Tentative d'enregistrement d'un nouvel utilisateur avec l'email : {}", userDto.getEmail());

        userRepository.findByEmail(userDto.getEmail()).ifPresent(u -> {
            log.warn("Échec de l'enregistrement : l'email {} est déjà utilisé", userDto.getEmail());
            throw new UserAlreadyExistException();
        });

        User user = userMapper.toEntity(userDto);

        User userSave =  userRepository.save(user);

        log.info("Utilisateur enregistré avec succès. ID : {}, Email : {}", userSave.getId(), userSave.getEmail());
        return userMapper.toResponseDto(userSave);
    }

    @PreAuthorize("hasRole('ADMIN') or #idUser == authentication.principal.id")
   public UserResponseDto update(Long idUser, UpdateUserDto userDto) {
        log.info("Demande de mise à jour pour l'utilisateur ID : {}", idUser);

        User userFind = userRepository.findById(idUser).orElseThrow(() -> {
            log.warn("Mise à jour impossible : utilisateur ID {} non trouvé", idUser);
            return new UserNotFoundException(idUser);
        });

        userFind.setName(userDto.getName());
        userFind.setEmail(userDto.getEmail());

        User userUpdated = userRepository.save(userFind);
        log.info("Utilisateur ID {} mis à jour avec succès", idUser);
        return userMapper.toResponseDto(userUpdated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponseDto> findAll(Pageable pageable) {
        log.info("Récupération de la liste paginée des utilisateurs. Page : {}, Taille : {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return userRepository.findAll(pageable).map(userMapper::toResponseDto);
    }

    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public UserResponseDto findById(Long id) {
        log.info("Recherche de l'utilisateur par ID : {}", id);
        User userFind = userRepository.findById(id).orElseThrow(() -> {
            log.warn("Utilisateur ID {} introuvable", id);
            return new UserNotFoundException(id);
        });
        return userMapper.toResponseDto(userFind);
    }

    @PreAuthorize("hasRole('ADMIN') or #email == authentication.name")
    public UserResponseDto findByEmail(String email) {
        log.info("Recherche de l'utilisateur par email : {}", email);
        User userFind = userRepository.findByEmail(email).orElseThrow(() -> {
            log.warn("Utilisateur avec l'email {} introuvable", email);
            return new UserNotFoundException(email);
        });
        return userMapper.toResponseDto(userFind);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Long id) {
        log.info("Tentative de suppression de l'utilisateur ID : {}", id);
        if (!userRepository.existsById(id)) {
            log.warn("Suppression avortée : l'utilisateur ID {} n'existe pas", id);
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
        log.info("Utilisateur ID {} supprimé avec succès", id);
    }
}
