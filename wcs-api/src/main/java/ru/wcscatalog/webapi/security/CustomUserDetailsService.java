package ru.wcscatalog.webapi.security;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.wcscatalog.core.model.User;
import ru.wcscatalog.core.repository.UserRepository;

import javax.transaction.Transactional;

@Service
@ComponentScan(basePackages = "ru.wcscatalog")
public class CustomUserDetailsService implements ICustomUserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(long id) {
        User user = userRepository.findUserById(id);
        return UserPrincipal.create(user);
    }
}
