package com.app.models.dao.interfaces;

import com.app.models.entities.User;

public interface IUser {
    User create(User user);

    User findByUsername(String username);

    // Add updateLastLogin if needed
    void updateLastLogin(int idUser);
}
