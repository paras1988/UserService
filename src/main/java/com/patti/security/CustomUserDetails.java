package com.patti.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.patti.models.Role;
import com.patti.models.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.HashSet;

@Getter
@Setter
@JsonDeserialize(as=CustomUserDetails.class)
public class CustomUserDetails implements UserDetails {
    User user;

    CustomUserDetails(){}
    CustomUserDetails(User user){
        this.user = user;
    }


    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<CustomGrantedAuthority> grantedAuthorities = new HashSet<>();
        for(Role r:user.getRoles()){
            grantedAuthorities.add(new CustomGrantedAuthority(r));
        }
        return grantedAuthorities;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
}
