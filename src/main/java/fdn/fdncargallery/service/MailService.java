package fdn.fdncargallery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private static final String TEMPORARY_PASSWORd_SUBJECT = "FDN Car Gallery - Hesabınız oluşturuldu";

    private static final String TEMPORARY_PASSWORD_BODY = """
            Merhaba,
            
            FDN Car Gallery sisteminde adınıza bir hesap oluşturuldu.
            
            Kullanıcı adı : %s
            Geçici şifre : %s
            
            İlk girişte sistem sizden bu şifreyi değiştirmenizi isteyecektir.
            Bu e-postayı beklemiyorsanız yöneticinizle iletişime geçiniz.
            """;

    private final JavaMailSender mailSender;

    @Value("${fdn.mail.from:${spring.mail.username:}}")
    private String from;

    public void sendTemporaryPassword(String to, String username, String temporaryPassword) {
        sendAfterCommit(to, TEMPORARY_PASSWORd_SUBJECT, TEMPORARY_PASSWORD_BODY.formatted(username, temporaryPassword));
    }

    private void sendAfterCommit(String to, String subject, String body) {

        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            send(to, subject, body);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                send(to, subject, body);
            }
        });
    }

    private void send(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            if (StringUtils.hasText(from)) {
                message.setFrom(from);
            }
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("E-posta gönderildi. alıcı: {}", to);

        } catch (MailException e) {
            log.error("E-posta gönderilemedi. alıcı: {}", to, e);
        }
    }
}
