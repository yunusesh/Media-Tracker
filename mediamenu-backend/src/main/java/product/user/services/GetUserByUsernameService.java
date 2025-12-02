package product.user.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.Query;
import product.user.UserRepository;
import product.user.model.AppUser;
import product.user.model.AppUserDTO;

import java.util.Optional;

@Service
public class GetUserByUsernameService implements Query<String, AppUserDTO> {

    private final UserRepository userRepository;

    public GetUserByUsernameService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<AppUserDTO> execute(String username){
        Optional<AppUser> userOptional = userRepository.findByUsername(username);
        if(userOptional.isPresent()){
            AppUserDTO user =  new AppUserDTO(userOptional.get());
            return ResponseEntity.ok(user);
        }

        return null;
    }
}
