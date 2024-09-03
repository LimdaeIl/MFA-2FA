package book.movieservice.doamin;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
public class User {

    private final String userId;
    private final String password;
    private final String otp;
}
