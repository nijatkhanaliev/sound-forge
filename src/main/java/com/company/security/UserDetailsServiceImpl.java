package com.company.security;

import com.company.models.entity.User;
import com.company.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
      User user = userRepository.findByEmail(email)
               .orElseThrow(()-> new UsernameNotFoundException("User not found with email: " + email)) ;

        return new UserDetailsImpl(user.getEmail(),user.getPassword(),user.getRole());
    }
}
