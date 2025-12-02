package product.user.model;

import lombok.Getter;

@Getter
public class UpdateUserCommand {
    private Integer id;
    private AppUser user;

    public UpdateUserCommand(Integer id, AppUser user) {
        this.user = user;
        this.id = id;
    }
}
