package com.app.models.services;

import com.app.models.dao.implementations.UserImpl;
import com.app.models.dao.interfaces.IUser;
import com.app.models.entities.User;
import com.app.utils.PasswordUtil;

public class AuthService {

    private final IUser userDao;
    private final UserSession userSession;

    public AuthService() {
        this.userDao = new UserImpl();
        this.userSession = UserSession.getInstance();
    }

    /**
     * Attempts to authenticate a user.
     * 
     * @param username The username.
     * @param password The raw password.
     * @return true if successful, false otherwise.
     */
    public boolean login(String username, String password) {
        User user = userDao.findByUsername(username);

        if (user != null) {
            String storedHash = user.getPassword();
            if (PasswordUtil.validatePassword(password, storedHash)) {

                // Update Last Login
                userDao.updateLastLogin(user.getIdUser());

                // Set Session
                userSession.setCurrentUser(user);
                return true;
            }
        }
        return false;
    }

    public void logout() {
        userSession.cleanUserSession();
    }

    public User getCurrentUser() {
        return userSession.getCurrentUser();
    }

    /**
     * Registers a new user (admin function usually).
     * 
     * @param username
     * @param password Raw password (will be hashed).
     * @return The created User.
     */
    public User registerUser(String username, String password) {
        String hashedPassword = PasswordUtil.hashPassword(password);
        User newUser = new User(username, hashedPassword);
        return userDao.create(newUser);
    }
}
