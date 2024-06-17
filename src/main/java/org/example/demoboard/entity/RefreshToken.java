package org.example.demoboard.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Entity
@Data
@RequiredArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String token;
    private Date expiryDate;

    public RefreshToken(String username, String refreshToken, Date expiryDate) {
        this.username = username;
        this.token = refreshToken;
        this.expiryDate = expiryDate;
    }
}