package ru.wcscatalog.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.wcscatalog.core.model.Role;
import ru.wcscatalog.core.model.RoleName;

import java.util.Optional;

public interface IRoleRepository {
    Optional<Role> findByName(RoleName roleName);
}
