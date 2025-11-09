package com.conexa.swapi.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // username único para login
    @Column(nullable = false, unique = true)
    private String username;

    // password encriptado
    @Column(nullable = false)
    private String password;
}