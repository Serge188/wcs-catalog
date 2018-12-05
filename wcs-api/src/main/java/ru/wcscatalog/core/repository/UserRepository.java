package ru.wcscatalog.core.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.wcscatalog.core.dao.Dao;
import ru.wcscatalog.core.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository implements IUserRepository{
//    @Autowired
//    Dao dao;

    @Autowired
    SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    public List<User> getUsers() {
        Session session;
        try {
            session = sessionFactory.getCurrentSession();
        } catch (Exception e) {
            session = sessionFactory.openSession();
        }
        return session.createQuery("from User").list();
    }

    @Override
    public List<User> findByIdIn(List<Long> userIds) {
        return null;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public Boolean existsByUsername(String username) {
        return false;
    }

    public User findUserByUserName(String userName) {
        List<User> usersList = getUsers();
        for (User user: usersList) {
            if (user.getName().equals(userName)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User findUserById(Long id) {
        List<User> users = getUsers();
        for (User user: users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User save(User user) {
        return user;
    }
}
