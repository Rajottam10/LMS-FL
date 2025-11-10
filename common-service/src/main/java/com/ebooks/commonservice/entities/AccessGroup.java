package com.ebooks.commonservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Generated;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(
        name = "access_group"
)
@SQLDelete(
        sql = "UPDATE access_group SET deleted = true WHERE id = ? "
)
@Where(
        clause = "deleted = false"
)
public class AccessGroup {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;
    @Column(
            unique = true,
            nullable = false
    )
    private String name;
    private String description;
    private LocalDateTime recordedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String type;
    @ManyToOne
    @JoinColumn(
            name = "status_id"
    )
    private Status status;
    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "bank_id",
            nullable = true
    )
    private Bank bank;
    private boolean deleted;

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
    public String getName() {
        return this.name;
    }

    @Generated
    public String getDescription() {
        return this.description;
    }

    @Generated
    public LocalDateTime getRecordedAt() {
        return this.recordedAt;
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
    public String getType() {
        return this.type;
    }

    @Generated
    public Status getStatus() {
        return this.status;
    }

    @Generated
    public Bank getBank() {
        return this.bank;
    }

    @Generated
    public boolean isDeleted() {
        return this.deleted;
    }

    @Generated
    public void setId(final Long id) {
        this.id = id;
    }

    @Generated
    public void setName(final String name) {
        this.name = name;
    }

    @Generated
    public void setDescription(final String description) {
        this.description = description;
    }

    @Generated
    public void setRecordedAt(final LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
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
    public void setType(final String type) {
        this.type = type;
    }

    @Generated
    public void setStatus(final Status status) {
        this.status = status;
    }

    @Generated
    public void setBank(final Bank bank) {
        this.bank = bank;
    }

    @Generated
    public void setDeleted(final boolean deleted) {
        this.deleted = deleted;
    }

    @Generated
    public AccessGroup(final Long id, final String name, final String description, final LocalDateTime recordedAt, final LocalDateTime createdAt, final LocalDateTime updatedAt, final String type, final Status status, final Bank bank, final boolean deleted) {
        this.deleted = Boolean.FALSE;
        this.id = id;
        this.name = name;
        this.description = description;
        this.recordedAt = recordedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.type = type;
        this.status = status;
        this.bank = bank;
        this.deleted = deleted;
    }

    @Generated
    public AccessGroup() {
        this.deleted = Boolean.FALSE;
    }
}
