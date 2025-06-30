package com.company.models.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import static com.company.util.EmailUtils.maskEmail;

@Getter
@Setter
public class AuthenticationRequest {
    @Email(message = "Email is not valid")
    private String email;

    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$",message = "Password must contain at least one uppercase letter and one number")
    private String password;


    @Override
    public String toString() {
        return "AuthenticationRequest{" +
                ", email='" + maskEmail(this.email) + '\'' +
                '}';
    }
}
