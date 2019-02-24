package ru.wcscatalog.webapi.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.wcscatalog.core.model.Role;
import ru.wcscatalog.core.model.User;
import ru.wcscatalog.core.repository.UserRepository;
import ru.wcscatalog.webapi.payload.ApiResponse;
import ru.wcscatalog.webapi.payload.JwtAuthenticationResponse;
import ru.wcscatalog.webapi.payload.LoginRequest;
import ru.wcscatalog.webapi.payload.SignUpRequest;
import ru.wcscatalog.webapi.security.JwtTokenProvider;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthController(@NonNull UserRepository userRepository,
                          @NonNull AuthenticationManager authenticationManager,
                          @NonNull PasswordEncoder passwordEncoder,
                          @NonNull JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @GetMapping("/test")
    public ResponseEntity<?> testApi() {
        return ResponseEntity.ok("Test successful");
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if(userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity(new ApiResponse(false, "Username is already taken!"),
                    HttpStatus.BAD_REQUEST);
        }
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.getRoleByName(signUpRequest.getRole()));

        User result = userRepository.save(user);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(result.getUsername()).toUri();
        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }
}
