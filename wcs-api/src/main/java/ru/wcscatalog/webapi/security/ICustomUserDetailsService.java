package ru.wcscatalog.webapi.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface ICustomUserDetailsService extends UserDetailsService {
    UserDetails loadUserById(long id);
}
