package com.patti.dtos;

import com.patti.models.Role;
import com.patti.models.User;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserDto {
    private String email;
    private Set<String> roles = new HashSet<>();

    public static UserDto from(User user) {
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setRoles(user.getRoles().stream().map(Role::getRole).collect(Collectors.toSet()));

        return userDto;
    }
}