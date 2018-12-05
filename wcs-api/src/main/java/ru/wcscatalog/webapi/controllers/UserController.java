package ru.wcscatalog.webapi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.wcscatalog.core.dao.Dao;
import ru.wcscatalog.core.model.User;
import ru.wcscatalog.core.repository.IUserRepository;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@Controller
@RequestMapping("/")
@ComponentScan(basePackages = "ru.wcscatalog")
public class UserController {

    @Autowired
    IUserRepository userService;


    @GetMapping
    public ResponseEntity<User> getUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Name");
        return ResponseEntity.ok(user);
    }
}
