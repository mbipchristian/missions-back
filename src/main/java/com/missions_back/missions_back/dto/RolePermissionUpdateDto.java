package com.missions_back.missions_back.dto;

import java.util.List;

public record RolePermissionUpdateDto (Long roleId, List<Long> permissionIds) {
    
}
