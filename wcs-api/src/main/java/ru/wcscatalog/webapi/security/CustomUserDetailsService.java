package ru.wcscatalog.webapi.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.wcscatalog.core.model.User;
import ru.wcscatalog.core.repository.IUserRepository;
import ru.wcscatalog.core.repository.UserRepository;

import javax.transaction.Transactional;

@Service
@ComponentScan(basePackages = "ru.wcscatalog")
public class CustomUserDetailsService implements ICustomUserDetailsService {

    @Autowired
    IUserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userName) {
        User user = userRepository.findUserByUserName(userName);
        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(long id) {
        User user = userRepository.findUserById(id);
        return UserPrincipal.create(user);
    }
}
