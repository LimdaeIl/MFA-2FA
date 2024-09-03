package book.authservice.controller.user;

import book.authservice.controller.request.EncryptedUserRequestBody;
import book.authservice.domain.User;
import book.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/api/v1/users")
    public User createNewUser(@RequestBody EncryptedUserRequestBody requestBody) {
        return userService.createNewUser(requestBody.getUserId(), requestBody.getPassword());
    }
}
