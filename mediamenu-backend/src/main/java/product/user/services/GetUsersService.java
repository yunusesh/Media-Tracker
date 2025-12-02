package product.user.services;

import product.user.model.AppUser;
import product.user.model.AppUserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.user.UserRepository;
import product.Query;

import java.util.List;

@Service
public class GetUsersService implements Query<Void, List<AppUserDTO>> {

    private final UserRepository userRepository;

    public GetUsersService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }
    @Override

    public ResponseEntity<List<AppUserDTO>> execute(Void input) {
        List<AppUser> users = userRepository.findAll();
        List<AppUserDTO> appUserDTOS = users.stream()
                .map(AppUserDTO::new)
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(appUserDTOS);
    }

    }
