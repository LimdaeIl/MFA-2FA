package book.authservice.service;


import book.authservice.repository.auth.AuthRepository;
import book.authservice.util.OtpCodeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final AuthRepository authRepository;

    public boolean checkOtp(String userId, String sourceOtp) {
        String targetOtp = authRepository.getOtp(userId);
        return targetOtp.equals(sourceOtp);
    }

    public String renewOtp(String userId) {
        String newOtp = OtpCodeUtil.generateOtpCode();
        authRepository.upsertOtp(userId, newOtp);
        return newOtp;
    }
}
