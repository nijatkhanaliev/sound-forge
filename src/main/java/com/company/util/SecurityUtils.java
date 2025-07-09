package com.company.util;

import com.company.models.entity.User;
import com.company.repository.UserRepository;
import com.company.security.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Slf4j
public class SecurityUtils {

    public static User getCurrentUser(UserRepository userRepository){
        log.info("Getting current user");
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(()-> new UsernameNotFoundException("User not exists"));
    }

}
