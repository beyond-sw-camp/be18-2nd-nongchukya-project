package com.beyond.sportsmatch.auth.model.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final Random random = new Random();

    // 이메일별 인증코드 + 만료시간 저장
    private final Map<String, VerificationCode> verificationCodes = new ConcurrentHashMap<>();

    @Getter
    @AllArgsConstructor
    static class VerificationCode {
        private final String code;
        private final LocalDateTime expiry;
    }

    /**
     * 인증코드 발송 (HTML 메일)
     */
    public void sendVerificationCode(String email) {
        String code = String.format("%06d", random.nextInt(999999));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(10); // 10분 유효

        verificationCodes.put(email, new VerificationCode(code, expiry));

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setFrom("your_email@gmail.com"); // application.yml에 설정된 계정
            helper.setSubject("[SportsMatch] 이메일 인증 코드");

            String htmlContent = """
                <div style="font-family: Arial, sans-serif; padding: 20px; background-color: #f9f9f9;">
                  <h2 style="color: #1D61E7;">SportsMatch 이메일 인증</h2>
                  <p>안녕하세요! 회원가입을 위해 아래 인증코드를 입력해주세요.</p>
                  <div style="margin: 20px 0; padding: 15px; background: #1D61E7; color: white; font-size: 24px; text-align: center; border-radius: 8px;">
                    <b>%s</b>
                  </div>
                  <p style="font-size: 14px; color: #555;">이 코드는 10분 동안만 유효합니다.</p>
                  <p style="font-size: 12px; color: #aaa;">만약 본인이 요청하지 않았다면 이 메일을 무시하세요.</p>
                </div>
                """.formatted(code);

            helper.setText(htmlContent, true); // true → HTML 모드
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("이메일 발송 실패", e);
        }
    }

    /**
     * 인증코드 검증
     */
    public boolean verifyCode(String email, String code) {
        VerificationCode saved = verificationCodes.get(email);

        if (saved == null) return false;
        if (LocalDateTime.now().isAfter(saved.getExpiry())) {
            verificationCodes.remove(email); // 만료된 건 삭제
            return false;
        }
        return saved.getCode().equals(code);
    }
}
