package ru.wcscatalog.core.repository;

import org.springframework.stereotype.Repository;
import ru.wcscatalog.core.model.User;
import javax.transaction.Transactional;

@Repository
@Transactional
public class UserRepository {
    private final Dao dao;

    public UserRepository(Dao dao) {
        this.dao = dao;
    }

    public Boolean existsByUsername(String username) {
        return false;
    }

    public User findByUsername(String username) {
        return dao.singleByProperty("username", username, User.class);
    }

    public User findUserById(Long id) {
        return dao.byId(id, User.class);
    }

    public User save(User user) {
        dao.add(user);
        return user;
    }
}
