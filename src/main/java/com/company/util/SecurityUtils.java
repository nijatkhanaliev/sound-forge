package com.company.util;

import com.company.exception.OperationNotPermittedException;
import com.company.models.entity.Music;
import com.company.models.entity.User;
import com.company.repository.UserRepository;
import com.company.security.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Objects;

@Slf4j
public class SecurityUtils {

    public static User getCurrentUser(UserRepository userRepository) {
        log.info("Getting current user");
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not exists"));
    }

    public static void checkUser(User currentUser, Music music, String errorMessage) {
        log.info("Checking user is owner, userId: {}", currentUser.getId());
        if (Objects.equals(music.getUser().getId(), currentUser.getId())) {
            throw new OperationNotPermittedException(errorMessage);
        }
    }

}
