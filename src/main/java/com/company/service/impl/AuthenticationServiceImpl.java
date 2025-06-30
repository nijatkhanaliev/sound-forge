package com.company.service.impl;

import com.company.exception.JwtCreationException;
import com.company.exception.UserAlreadyExists;
import com.company.models.dto.request.AuthenticationRequest;
import com.company.models.dto.request.RegistrationRequest;
import com.company.models.dto.response.JwtResponse;
import com.company.models.entity.User;
import com.company.models.enums.Role;
import com.company.models.mapper.UserMapper;
import com.company.repository.UserRepository;
import com.company.service.AuthenticationService;
import com.company.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    //private final JavaMailSender mailSender;


    @Override
    public void register(RegistrationRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExists("User already exists");
        }

        User user = userMapper.toUser(request);
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        //sendActivationCode(user.getEmail());


        userRepository.save(user);
    }



    private String generateActivationCode(int length){
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for(int i=0;i<length;i++){
            int index = random.nextInt(characters.length());
            codeBuilder.append(characters.charAt(index));
        }

        return codeBuilder.toString();
    }

    @Override
    public JwtResponse login(AuthenticationRequest request) {
       var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwtToken = jwtUtils.generateToken(userDetails);

        if(!StringUtils.hasText(jwtToken)){
            throw new JwtCreationException("Failed to create jwt token");
        }

        return JwtResponse.builder()
                .token(jwtToken)
                .build();
    }

}
