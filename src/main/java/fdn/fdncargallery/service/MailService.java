package fdn.fdncargallery.service;

import fdn.fdncargallery.exception.BaseException;
import fdn.fdncargallery.exception.ErrorMessage;
import fdn.fdncargallery.exception.MessageType;
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

    private static final String TEMPORARY_PASSWORD_SUBJECT = "FDN Car Gallery - Hesabınız oluşturuldu";

    private static final String TEMPORARY_PASSWORD_BODY = """
            Merhaba,
            
            FDN Car Gallery sisteminde adınıza bir hesap oluşturuldu.
            
            Kullanıcı adı : %s
            Geçici şifre : %s

            İlk girişte sistem sizden bu şifreyi değiştirmenizi isteyecektir.
            Bu geçici şifre 24 saat içinde geçerliliğini yitirir; bu süre içinde giriş yapıp şifrenizi değiştirmezseniz yöneticinizden yeni bir geçici şifre talep etmeniz gerekir.
            Bu e-postayı beklemiyorsanız yöneticinizle iletişime geçiniz.
            """;

    private final JavaMailSender mailSender;

    @Value("${fdn.mail.from:${spring.mail.username:}}")
    private String from;

    public void sendTemporaryPassword(String to, String username, String temporaryPassword) {
        sendAfterCommit(to, TEMPORARY_PASSWORD_SUBJECT, TEMPORARY_PASSWORD_BODY.formatted(username, temporaryPassword));
    }

    public void resendTemporaryPassword(String to, String username, String temporaryPassword) {
        try {
            mailSender.send(buildMessage(to, TEMPORARY_PASSWORD_SUBJECT, TEMPORARY_PASSWORD_BODY.formatted(username, temporaryPassword)));
            log.info("Geçici şifre yeniden gönderildi. alıcı: {}", to);

        } catch (MailException e) {
            log.error("Geçici şifre yeniden gönderilemedi. alıcı: {}", to, e);
            throw new BaseException(new ErrorMessage(MessageType.MAIL_SEND_FAILED, to));
        }
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
            mailSender.send(buildMessage(to, subject, body));
            log.info("E-posta gönderildi. alıcı: {}", to);

        } catch (MailException e) {
            log.error("E-posta gönderilemedi. alıcı: {}", to, e);
        }
    }

    private SimpleMailMessage buildMessage(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        if (StringUtils.hasText(from)) {
            message.setFrom(from);
        }
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        return message;
    }
}