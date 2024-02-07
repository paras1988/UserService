package com.patti.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class SetUserRolesRequestDto {
    private Set<String> roleIds;

}
