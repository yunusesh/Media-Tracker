package product.user.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.user.model.AppUser;
import product.user.model.AppUserDTO;
import product.user.UserRepository;
import product.Query;

import java.util.Optional;

@Service
public class GetUserByIdService implements Query<Integer, AppUserDTO> {

    private final UserRepository userRepository;

    public GetUserByIdService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<AppUserDTO> execute(Integer input) {
        //account for null value if database cant find it
        Optional<AppUser> userOptional = userRepository.findById(input);
        if(userOptional.isPresent()){
            return ResponseEntity.ok(new AppUserDTO(userOptional.get()));
        }

        return null;//in future add a cant find response
    }
}
