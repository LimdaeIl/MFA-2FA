package book.authservice.service;

import book.authservice.domain.User;
import book.authservice.exception.InvalidAuthException;
import book.authservice.repository.auth.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final EncryptService encryptService;
    private final OtpService otpService;
    private final AuthRepository authRepository;

    public User createNewUser(String userId, String password) {
        return authRepository.createNewUser(new User(userId, password));
    }

    public String auth(String userId, String password) {
        User user = authRepository.getUserByUserId(userId);
        if (encryptService.matches(password, user.getPassword())) {
            return otpService.renewOtp(userId);
        }

        throw new InvalidAuthException();
    }
}
