package com.strangerthings;

import com.strangerthings.dao.UserDao;
import com.strangerthings.dao.UserDao.UserRecord;

/** Database-backed authentication and registration rules. */
public class AuthService {
    private final UserDao userDao = new UserDao();

    public User login(String username, String password) {
        UserRecord account = userDao.findByCredentials(username, password);
        if (account == null) {
            return null;
        }
        try {
            return new User(account.username(), Role.valueOf(account.role()));
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    public boolean register(String username, String password) {
        return register(username, password, Role.MEMBER);
    }

    public boolean register(String username, String password, Role role) {
        return role != null && userDao.register(username, password, role.name());
    }
}
