package product.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import product.user.model.AppUser;
import product.user.model.AppUserDTO;
import product.user.model.UpdateUserCommand;
import product.user.services.*;

import java.util.List;

//examples of controllers that are used to write and read data with HTTP requests
@RestController
public class UserController {
    private final UpdateUserService updateUserService;
    private final GetUsersService getUsersService;
    private final DeleteUserService deleteUserService;
    private final GetUserByIdService getUserByIdService;
    private final GetUserByUsernameService getUserByUsernameService;


    public UserController(UpdateUserService updateUserService,
                          GetUsersService getUsersService,
                          DeleteUserService deleteUserService,
                          GetUserByIdService getUserByIdService,
                          GetUserByUsernameService getUserByUsernameService) {
        this.getUsersService = getUsersService;
        this.updateUserService = updateUserService;
        this.deleteUserService = deleteUserService;
        this.getUserByIdService = getUserByIdService;
        this.getUserByUsernameService = getUserByUsernameService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<AppUserDTO>> getUsers() {

        return getUsersService.execute(null);
    }

    @GetMapping("/user/id/{id}")
    public ResponseEntity<AppUserDTO> getUserById(@PathVariable Integer id) {

        return getUserByIdService.execute(id);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<AppUserDTO> getUserById(@PathVariable String username) {

        return getUserByUsernameService.execute(username);
    }

    @GetMapping("/user/me")
    public ResponseEntity<AppUserDTO> authenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        AppUser currentUser = (AppUser) auth.getPrincipal();
        AppUserDTO currentUserDTO = new AppUserDTO(currentUser);
        return ResponseEntity.ok(currentUserDTO);
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<AppUserDTO> updateUser(@PathVariable Integer id, @RequestBody AppUser user) {
        return updateUserService.execute(new UpdateUserCommand(id, user));
    }

    @DeleteMapping("/user/{id}") // id here must match id in ??
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {

        return deleteUserService.execute(id);
    }


}
