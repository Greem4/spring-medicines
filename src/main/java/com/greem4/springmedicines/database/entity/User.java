package com.greem4.springmedicines.database.entity;

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

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // fixme: пустая аннотация @Column не имеет смысла. Любое поле энтити-класса трактуется как колонка,
    //  если не указано иное (если не должно быть колонкой - поле должно быть аннотировано @Transient)
    @Column
    private String provider;

    @Column
    private String providerId;

    @Column(nullable = false)
    private boolean enabled ;
}
