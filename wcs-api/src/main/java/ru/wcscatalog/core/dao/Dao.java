package ru.wcscatalog.core.dao;

import org.springframework.stereotype.Component;
import ru.wcscatalog.core.model.User;

import java.util.List;

@Component
public interface Dao {

    List<User> getUsers();

    User findUserByUserName(String userName);

    User findUserById(long id);
}
