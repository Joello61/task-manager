package com.example.task_manager.service;

import com.example.task_manager.dto.user.CreateUserDto;
import com.example.task_manager.dto.user.UserResponseDto;
import com.example.task_manager.entity.User;
import com.example.task_manager.exception.UserNotFoundException;
import com.example.task_manager.mapper.UserMapper;
import com.example.task_manager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponseDto save(CreateUserDto userDto) {
        User user = userMapper.toEntity(userDto);
        User userSave =  userRepository.save(user);

        return userMapper.toResponseDto(userSave);
    }

    public UserResponseDto update(Long idUser, CreateUserDto userDto) {

        User userFind = userRepository.findById(idUser).orElseThrow(() -> new UserNotFoundException(idUser));

        userFind.setName(userDto.getName());
        userFind.setPassword(userDto.getPassword());
        userFind.setEmail(userDto.getEmail());
        userFind.setRole(userDto.getRole());

        User userUpdated = userRepository.save(userFind);
        return userMapper.toResponseDto(userUpdated);
    }

    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream().map(userMapper::toResponseDto).toList();
    }

    public UserResponseDto findById(Long id) {
        User userFind = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toResponseDto(userFind);
    }

    public UserResponseDto findByEmail(String email) {
        User userFind = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
        return userMapper.toResponseDto(userFind);
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

}
