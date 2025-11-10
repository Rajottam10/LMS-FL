package com.ebooks.commonservice.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Generated;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(
        name = "bank"
)
@SQLDelete(
        sql = "UPDATE bank SET deleted = true WHERE id = ?"
)
@Where(
        clause = "deleted = false"
)
public class Bank {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;
    @Column(
            unique = true,
            nullable = false
    )
    private String bankCode;
    private String name;
    private String address;
    private Boolean isActive;
    @ManyToOne
    @JoinColumn(
            name = "status_id"
    )
    private Status status;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @OneToOne(
            mappedBy = "bank",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.EAGER
    )
    private BankAdmin bankAdmin;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Generated
    public Long getId() {
        return this.id;
    }

    @Generated
    public String getBankCode() {
        return this.bankCode;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public String getAddress() {
        return this.address;
    }

    @Generated
    public Boolean getIsActive() {
        return this.isActive;
    }

    @Generated
    public Status getStatus() {
        return this.status;
    }

    @Generated
    public boolean isDeleted() {
        return this.deleted;
    }

    @Generated
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    @Generated
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    @Generated
    public BankAdmin getBankAdmin() {
        return this.bankAdmin;
    }

    @Generated
    public void setId(final Long id) {
        this.id = id;
    }

    @Generated
    public void setBankCode(final String bankCode) {
        this.bankCode = bankCode;
    }

    @Generated
    public void setName(final String name) {
        this.name = name;
    }

    @Generated
    public void setAddress(final String address) {
        this.address = address;
    }

    @Generated
    public void setIsActive(final Boolean isActive) {
        this.isActive = isActive;
    }

    @Generated
    public void setStatus(final Status status) {
        this.status = status;
    }

    @Generated
    public void setDeleted(final boolean deleted) {
        this.deleted = deleted;
    }

    @Generated
    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Generated
    public void setUpdatedAt(final LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Generated
    public void setBankAdmin(final BankAdmin bankAdmin) {
        this.bankAdmin = bankAdmin;
    }

    @Generated
    public Bank() {
        this.deleted = Boolean.FALSE;
    }

    @Generated
    public Bank(final Long id, final String bankCode, final String name, final String address, final Boolean isActive, final Status status, final boolean deleted, final LocalDateTime createdAt, final LocalDateTime updatedAt, final BankAdmin bankAdmin) {
        this.deleted = Boolean.FALSE;
        this.id = id;
        this.bankCode = bankCode;
        this.name = name;
        this.address = address;
        this.isActive = isActive;
        this.status = status;
        this.deleted = deleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.bankAdmin = bankAdmin;
    }

    @Generated
    public String toString() {
        Long var10000 = this.getId();
        return "Bank(id=" + var10000 + ", bankCode=" + this.getBankCode() + ", name=" + this.getName() + ", address=" + this.getAddress() + ", isActive=" + this.getIsActive() + ", status=" + String.valueOf(this.getStatus()) + ", deleted=" + this.isDeleted() + ", createdAt=" + String.valueOf(this.getCreatedAt()) + ", updatedAt=" + String.valueOf(this.getUpdatedAt()) + ", bankAdmin=" + String.valueOf(this.getBankAdmin()) + ")";
    }
}
