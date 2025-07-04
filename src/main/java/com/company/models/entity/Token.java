package com.company.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tokens")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "tokens_seq")
    @SequenceGenerator(name = "tokens_seq",sequenceName = "tokens_seq",allocationSize = 1)
    private long id;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false,updatable = false)
    private LocalDateTime issuedAt;

    @Column(nullable = false,updatable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime validatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
