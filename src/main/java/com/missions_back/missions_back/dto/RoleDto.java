package com.missions_back.missions_back.dto;

import java.util.List;

public record RoleDto  (String code, String name, List<Long> permissionIds) {
    
}
