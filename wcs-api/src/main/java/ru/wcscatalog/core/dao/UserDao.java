package ru.wcscatalog.core.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.NamedQueryDefinitionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.wcscatalog.core.dao.Dao;
import ru.wcscatalog.core.model.User;

import java.util.List;
public class UserDao {

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

    public User findUserByUserName(String userName) {
        return getUsers().get(0);
    }

    public User findUserById(long id) {
        List<User> users = getUsers();
        for (User user: users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }
}
