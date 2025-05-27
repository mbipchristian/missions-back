package com.missions_back.missions_back.dto;


public record GradeDto(
    String name, 
    String code, 
    Long fraisExterne, 
    Long fraisInterne) {
    
}
