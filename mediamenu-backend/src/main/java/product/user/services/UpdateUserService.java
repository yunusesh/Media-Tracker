package product.user.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import product.Command;
import product.user.UserRepository;
import product.user.model.AppUser;
import product.user.model.AppUserDTO;
import product.user.model.UpdateUserCommand;

import java.util.Optional;

@Service
public class UpdateUserService implements Command<UpdateUserCommand, AppUserDTO> {
    private UserRepository userRepository;

    public UpdateUserService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<AppUserDTO> execute(UpdateUserCommand command) {
        Optional<AppUser> productOptional = userRepository.findById(command.getId());
        if (productOptional.isPresent()) { //create a new user with the same id and updated object
            AppUser user = command.getUser();
            user.setId(command.getId());
            userRepository.save(user);
            return ResponseEntity.ok(new AppUserDTO(user));
        }
        return null;
    }
}
