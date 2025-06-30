package com.company.models.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import static com.company.util.EmailUtils.maskEmail;

@Getter
@Setter
public class RegistrationRequest {
    @Pattern(regexp = "^[a-zA-Z]{1,40}$", message = "FirstName must contain only letters (1–40)")
    private String firstName;

    @Pattern(regexp = "^[a-zA-Z]{1,80}$", message = "LastName must contain only letters (1-80)")
    private String lastName;

    @Email(message = "Email is not valid")
    private String email;

    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$", message = "Password must contain at least one uppercase letter and one number")
    private String password;



    @Override
    public String toString() {
        return "RegistrationRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + maskEmail(this.email) + '\'' +
                '}';
    }
}
