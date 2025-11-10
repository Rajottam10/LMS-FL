package com.ebooks.commonmoduleloan.entities;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseAccountingEntity {
    private LocalDate createdDate;
    private LocalDate updatedDate;

    @PrePersist
    public void onCreate(){
        setCreatedDate(LocalDate.now());
        setUpdatedDate(LocalDate.now());
    }

    @PreUpdate
    public void onUpdate(){
        setUpdatedDate(LocalDate.now());
    }
}
