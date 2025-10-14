package com.example.bankcards.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponseDTO {
    private String token; 
    private String username; 
    private String role; 

}