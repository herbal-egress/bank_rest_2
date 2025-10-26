package com.example.bankcards.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true)
    private RoleName name;
    public Role() {
    }
    public Role(RoleName name) {
        this.name = name;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public RoleName getName() {
        return name;
    }
    public void setName(RoleName name) {
        this.name = name;
    }
    public enum RoleName {
        ADMIN,
        USER
    }
}