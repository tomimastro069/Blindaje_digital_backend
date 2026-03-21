package com.blindaje.modules.user.dto;

import com.blindaje.modules.user.domain.Role;
import com.blindaje.modules.user.domain.UserStatus;

public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private Role role;
    private UserStatus status;
    private String tenantId;

    public UserResponse(Long id, String username, String email, String fullName,
                        Role role, UserStatus status, String tenantId) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.status = status;
        this.tenantId = tenantId;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public Role getRole() { return role; }
    public UserStatus getStatus() { return status; }
    public String getTenantId() { return tenantId; }
}