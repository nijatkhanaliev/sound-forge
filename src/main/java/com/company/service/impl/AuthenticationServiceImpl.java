package com.company.service.impl;

import com.company.exception.AccountNotActivatedException;
import com.company.exception.TokenNotValidException;
import com.company.exception.UserAlreadyExistsException;
import com.company.models.dto.request.AuthenticationRequest;
import com.company.models.dto.request.RegistrationRequest;
import com.company.models.dto.response.AuthenticationResponse;
import com.company.models.entity.Token;
import com.company.models.entity.User;
import com.company.models.enums.Role;
import com.company.models.enums.UserStatus;
import com.company.models.mapper.UserMapper;
import com.company.repository.TokenRepository;
import com.company.repository.UserRepository;
import com.company.security.UserDetailsImpl;
import com.company.service.AuthenticationService;
import com.company.service.EmailService;
import com.company.util.JwtUtils;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
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
    public void register(RegistrationRequest request){
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }

        log.info("User registering using request: { {} }",request);
        User user = userMapper.toUser(request);
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(UserStatus.DISABLED);
        userRepository.save(user);

        try {
            log.info("Sending Activation code to user, userId: {}",user.getId());
            saveAndSendActivationCode(user);
        } catch (MessagingException e) {
            log.error("Messaging error. errorMessage: {}",e.getMessage());
        }
    }

    private void saveAndSendActivationCode(User user) throws MessagingException {
        log.info("Generating otp code for user, userId: {}",user.getId());
        String otp = generateActivationCode(6);
        if(!StringUtils.hasText(otp)){
            throw new TokenNotValidException("Otp code is not valid");
        }
        log.info("Creating Token for user, userId: {}",user.getId());
        Token token = new Token();
        token.setToken(otp);
        token.setUser(user);
        token.setIssuedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        tokenRepository.save(token);

        log.info("Sending email to user, userId: {}",user.getId());
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
    public AuthenticationResponse login(AuthenticationRequest request) {
       User user = userRepository.findByEmail(request.getEmail())
               .orElseThrow(()-> new UsernameNotFoundException("User not exists"));

       if(user.getStatus()==UserStatus.DISABLED){
           log.warn("User is disabled, userId: {}",user.getId());
           throw new AccountNotActivatedException("Account not activated");
       }
       var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),
                        request.getPassword())
        );

        log.info("Token is generating, userId: {}",user.getId());
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwtToken = jwtUtils.generateToken(userDetails);

        if(!StringUtils.hasText(jwtToken)){
            throw new TokenNotValidException("Jwt token cannot generated");
        }
        log.info("User is logging, userId: {}",user.getId());
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public void activateAccount(String activationToken) {
       Token token =  tokenRepository.findByToken(activationToken)
                .orElseThrow(()-> new TokenNotValidException("Token not valid"));

        if(LocalDateTime.now().isAfter(token.getExpiresAt())){
            throw new TokenNotValidException("Otp code has been expired");
        }

       User user = userRepository.findById(token.getUser().getId())
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
        log.info("User account is activating, userId: {}",user.getId());
        user.setStatus(UserStatus.ENABLED);
        userRepository.save(user);

        log.info("Token is validated");
        token.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(token);
    }

}
