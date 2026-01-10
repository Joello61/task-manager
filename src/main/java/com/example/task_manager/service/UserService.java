package com.example.task_manager.service;

import com.example.task_manager.dto.UserDto;
import com.example.task_manager.entity.User;
import com.example.task_manager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;

    public User save(UserDto userDto) {
        User user = this.hydrateUser(userDto);
        return userRepository.save(user);
    }

    public User update(User user) {
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private User hydrateUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setRole(userDto.getRole());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());

        return user;
    }

}
