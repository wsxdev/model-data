package com.app.models.dao.interfaces;

import com.app.models.entities.Role;

public interface IRole {
    Role findByName(String name);

    // Add create if needed for seeding
    Role create(Role role);
}
