package com.authguard.dal.jdbc.model;

public class AccountJoinedRow {
    private String id;
    private boolean deleted;
    private boolean active;
    private String scopes;
    private String permissionId;
    private String roleId;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(final boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public String getScopes() {
        return scopes;
    }

    public void setScopes(final String scopes) {
        this.scopes = scopes;
    }

    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(final String permissionId) {
        this.permissionId = permissionId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(final String roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "AccountJoinedRow{" +
                "id='" + id + '\'' +
                ", deleted=" + deleted +
                ", active=" + active +
                ", scopes='" + scopes + '\'' +
                ", permissionId='" + permissionId + '\'' +
                ", roleId='" + roleId + '\'' +
                '}';
    }
}
