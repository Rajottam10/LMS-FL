package com.ebooks.prepaymentservice.entities;

import com.ebooks.commonmoduleloan.enums.ChargeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "prepayment_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrepaymentConfig {

    @Id
    @Column(name = "bank_code", nullable = false, length = 10)
    @NotBlank(message = "Bank code is mandatory")
    private String bankCode;

    @Column(name = "charge_type", nullable = false, length = 20)
    @NotBlank(message = "Charge type is mandatory")
    private String chargeType; // PERCENTAGE, FLAT, GREATER

    @Column(name = "charge_value", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Charge value is mandatory")
    @DecimalMin(value = "0.0", inclusive = false, message = "Charge value must be positive")
    private BigDecimal chargeValue;

}