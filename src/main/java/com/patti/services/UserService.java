package com.patti.services;

import com.patti.dtos.UserDto;
import com.patti.models.User;
import com.patti.repositories.SessionRepository;
import com.patti.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    public UserDto getUserDetails(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(UserDto::from).orElse(null);
    }

    public UserDto setUserRoles(Long userId, Set<String> roleIds) {
        return null;
    }
}
