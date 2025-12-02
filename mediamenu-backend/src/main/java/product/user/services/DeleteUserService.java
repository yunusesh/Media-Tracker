package product.user.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.Command;
import product.user.UserRepository;
import product.user.model.AppUser;

import java.util.Optional;

@Service
public class DeleteUserService implements Command<Integer, Void> {
    private UserRepository userRepository;

    public DeleteUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<Void> execute(Integer id) {
        Optional<AppUser> userOptional = userRepository.findById(id);
        if(userOptional.isPresent()){
            userRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return null; //add exception later

    }
}
