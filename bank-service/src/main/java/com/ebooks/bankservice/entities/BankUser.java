package com.ebooks.bankservice.entities;

import com.ebooks.commonservice.entities.AccessGroup;
import com.ebooks.commonservice.entities.Bank;
import com.ebooks.commonservice.entities.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "bank_user")
@SQLDelete(sql = "UPDATE bank_user SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class BankUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    @Column(unique = true, nullable = false)
    private String email;
    private String mobileNumber;
    private String address;
    private String password;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @ManyToOne
    @JoinColumn(name = "access_group_id")
    @JsonIgnore
    private AccessGroup accessGroup;
    @ManyToOne
    @JoinColumn(name = "bank_id")
    @JsonIgnore
    private Bank bank;
    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Transient
    private Collection<? extends GrantedAuthority> authorities = List.of();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }


    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
