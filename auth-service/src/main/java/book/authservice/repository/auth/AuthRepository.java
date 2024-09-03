package book.authservice.repository.auth;

import book.authservice.domain.User;
import book.authservice.entity.otp.OtpEntity;
import book.authservice.entity.user.UserEntity;
import book.authservice.exception.InvalidAuthException;
import book.authservice.repository.otp.OtpJpaRepository;
import book.authservice.repository.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionOperations;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuthRepository {
    private final OtpJpaRepository otpJpaRepository;
    private final UserJpaRepository userJpaRepository;

    private final TransactionOperations readTransactionOperations;
    private final TransactionOperations writeTransactionOperations;

    public User createNewUser(User user) {
        return writeTransactionOperations.execute(status -> {
            Optional<UserEntity> userOptional = userJpaRepository.findUserEntityByUserId(user.getUserId());
            if (userOptional.isPresent()) {
                throw new RuntimeException(String.format("User [%s] already exists", user.getUserId()));
            }

            UserEntity saved = userJpaRepository.save(user.toEntity());
            return saved.toDomain();
        });
    }

    public User getUserByUserId(String userId) {
        return readTransactionOperations.execute(status ->
                userJpaRepository.findUserEntityByUserId(userId)
                        .orElseThrow(InvalidAuthException::new)
                        .toDomain());
    }

    public String getOtp(String userId) {
        return readTransactionOperations.execute(status -> otpJpaRepository.findOtpEntityByUserId(userId)
                .orElseThrow(() -> new RuntimeException(String.format("User [%s] does not exist", userId)))
                .getOtpCode());
    }

    public void upsertOtp(String userId, String newOtp) {
        writeTransactionOperations.executeWithoutResult(status -> {
            Optional<OtpEntity> optOptional = otpJpaRepository.findOtpEntityByUserId(userId);

            if (optOptional.isPresent()) {
                optOptional.get().renewOtp(newOtp);
            } else {
                otpJpaRepository.save(new OtpEntity(userId, newOtp));
            }
        });
    }
}
