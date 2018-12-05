package ru.wcscatalog.core.repository;

import org.springframework.stereotype.Repository;
import ru.wcscatalog.core.model.Role;
import ru.wcscatalog.core.model.RoleName;

import java.util.Optional;

@Repository
public class RoleRepository implements IRoleRepository {
    @Override
    public Optional<Role> findByName(RoleName roleName) {
        return Optional.of(new Role(RoleName.ROLE_USER));
    }
}
