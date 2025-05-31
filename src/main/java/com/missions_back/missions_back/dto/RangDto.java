package com.missions_back.missions_back.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RangDto {
    
    @NotBlank(message = "Le nom du rang est obligatoire")
    private String nom;

    @NotBlank(message = "Le code du rang est obligatoire")
    private String code;

    @NotNull(message = "Les frais internes sont obligatoires")
    @DecimalMin(value = "0.0", inclusive = true, message = "Les frais internes doivent être positifs ou nuls")
    private BigDecimal fraisInterne;

    @NotNull(message = "Les frais externes sont obligatoires")
    @DecimalMin(value = "0.0", inclusive = true, message = "Les frais externes doivent être positifs ou nuls")
    private BigDecimal fraisExterne;
}