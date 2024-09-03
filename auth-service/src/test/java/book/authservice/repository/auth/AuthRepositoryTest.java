package book.authservice.repository.auth;

import book.authservice.domain.User;
import book.authservice.entity.otp.OtpEntity;
import book.authservice.entity.user.UserEntity;
import book.authservice.repository.otp.OtpJpaRepository;
import book.authservice.repository.user.UserJpaRepository;
import book.authservice.util.OtpCodeUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionOperations;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthRepositoryTest {

    @Mock
    OtpJpaRepository otpJpaRepository;

    @Mock
    UserJpaRepository userJpaRepository;

    AuthRepository sut;

    @BeforeEach
    public void setup() {
        sut = new AuthRepository(
                otpJpaRepository,
                userJpaRepository,
                TransactionOperations.withoutTransaction(),
                TransactionOperations.withoutTransaction()
        );
    }

    @Test
    @DisplayName("동일한 사용자 ID 로 사용자를 등록할 수 없다.")
    public void test1() {
        // given
        String userId = "danny.kim";
        UserEntity userEntity = new UserEntity(
                userId, "password"
        );

        given(userJpaRepository.findUserEntityByUserId(userId))
                .willReturn(Optional.of(userEntity));

        // when & then
        Assertions.assertThrows(RuntimeException.class, () -> {
            sut.createNewUser(userEntity.toDomain());
        });
    }

    @Test
    @DisplayName("사용자를 등록할 수 있다.")
    public void test2() {
        // given
        String userId = "danny.kim";
        String password = "1234";
        given(userJpaRepository.findUserEntityByUserId(userId))
                .willReturn(Optional.empty());

        User user = new User(userId, password);
        given(userJpaRepository.save(any()))
                .willReturn(user.toEntity());

        // when
        User result = sut.createNewUser(user);

        // then
        verify(userJpaRepository, atMostOnce()).save(user.toEntity());
        Assertions.assertEquals(result.getUserId(), userId);
        Assertions.assertEquals(result.getPassword(), password);
    }

    @Test
    @DisplayName("사용자가 존재하면 OTP 값을 업데이트 한다.")
    public void test3() {
        // given
        String userId = "danny.kim";
        String otp = OtpCodeUtil.generateOtpCode();
        OtpEntity otpEntity = new OtpEntity();
        given(otpJpaRepository.findOtpEntityByUserId(userId))
                .willReturn(Optional.of(otpEntity));

        // when
        sut.upsertOtp(userId, otp);

        // then
        Assertions.assertEquals(otp, otpEntity.getOtpCode());
    }

    @Test
    @DisplayName("사용자가 존재하지 않으면 새로운 OTP 를 생성한다.")
    public void test4() {
        // given
        String userId = "danny.kim";
        String otp = OtpCodeUtil.generateOtpCode();
        OtpEntity otpEntity = new OtpEntity();
        given(otpJpaRepository.findOtpEntityByUserId(userId))
                .willReturn(Optional.empty());

        // when
        sut.upsertOtp(userId, otp);

        // then
        verify(otpJpaRepository, atMostOnce()).save(any());
    }
}
