package com.company.service.impl;

import com.company.exception.TokenNotValidException;
import com.company.exception.UserAlreadyExistsException;
import com.company.models.dto.request.AuthenticationRequest;
import com.company.models.dto.request.RegistrationRequest;
import com.company.models.dto.response.JwtResponse;
import com.company.models.entity.Token;
import com.company.models.entity.User;
import com.company.models.enums.Role;
import com.company.models.mapper.UserMapper;
import com.company.repository.TokenRepository;
import com.company.repository.UserRepository;
import com.company.service.AuthenticationService;
import com.company.service.EmailService;
import com.company.util.JwtUtils;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;
    private final TokenRepository tokenRepository;

    @Override
    public void register(RegistrationRequest request) throws MessagingException {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }

        User user = userMapper.toUser(request);
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        saveAndSendActivationCode(user);
    }

    private void saveAndSendActivationCode(User user) throws MessagingException {
        String otp = generateActivationCode(6);

        Token token = new Token();
        token.setToken(otp);
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(15));

        tokenRepository.save(token);

        emailService.sendEmail(user.getEmail(),user.getFullName(),"activate_account","newToken",otp);
    }


    private String generateActivationCode(int length){
        String characters = "0123456789abcdeftyoiurpomnzxqABCDEFTYOUIRPOMNZXQ";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for(int i = 0; i< length; i++){
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
            throw new TokenNotValidException("Something went wrong.");
        }

        return JwtResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public void activateAccount(String activationToken) {
       Token token =  tokenRepository.findByToken(activationToken)
                .orElseThrow(()-> new TokenNotValidException("Token not valid"));

        if(LocalDateTime.now().isAfter(token.getExpiresAt())){
            throw new TokenNotValidException("Token has been expired");
        }

       User user = userRepository.findById(token.getUser().getId())
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));

        user.setEnabled(true);
        userRepository.save(user);

        token.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(token);
    }

}
