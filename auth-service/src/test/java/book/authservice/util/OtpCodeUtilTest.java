package book.authservice.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OtpCodeUtilTest {

    @Test
    @DisplayName("6자리 숫자값이 나와야 함")
    public void test1() {
        // given & when
        String otp = OtpCodeUtil.generateOtpCode();

        // then
        //  1. 숫자값이어야 함
        Assertions.assertThat(otp.chars().allMatch(Character::isDigit));

        // 2. 6자리어야 함
        Assertions.assertThat(otp.length()).isEqualTo(6);
    }

}