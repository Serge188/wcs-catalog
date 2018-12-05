package ru.wcscatalog.core.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.wcscatalog.core.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserRepository {

    List<User> findByIdIn(List<Long> userIds);

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    User findUserByUserName(String userName);

    User findUserById(Long id);

    User save(User iser);
}
