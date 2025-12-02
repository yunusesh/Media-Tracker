package product.user.services;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import product.user.UserRepository;
import product.user.model.AppUser;
import product.user.model.LoginUserDTO;
import product.user.model.RegisterUserDTO;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AppUser signup(RegisterUserDTO input) {
        AppUser user = new AppUser();
        user.setUsername(input.getUsername());
        user.setEmail(input.getEmail());
        user.setHashedPassword(passwordEncoder.encode(input.getPassword()));

        return userRepository.save(user);
    }

    public AppUser authenticate(LoginUserDTO input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getUsername(),
                        input.getPassword()
                )
        );

        return userRepository.findByUsername(input.getUsername())
                .orElseThrow();
    }
}
