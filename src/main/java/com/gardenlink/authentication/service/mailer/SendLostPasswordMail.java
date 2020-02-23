package com.gardenlink.authentication.service.mailer;


import com.gardenlink.authentication.domain.AuthUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.thymeleaf.context.Context;


@Service
public class SendLostPasswordMail {

    @Value("${app.front.url}")
    protected String frontUrl;

    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;


    public SendLostPasswordMail(TemplateEngine templateEngine, JavaMailSender javaMailSender) {
        this.templateEngine = templateEngine;
        this.javaMailSender = javaMailSender;
    }

    public String build(AuthUser authUser) {
        Context context = new Context();
        context.setVariable("username", authUser.getFirstName());
        context.setVariable("url", frontUrl + "#/lostPassword/"  + authUser.getResetToken());

        return templateEngine.process("lostPasswordMail", context);
    }

    public void prepareAndSend(AuthUser authUser) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setTo(authUser.getEmail());
            messageHelper.setFrom("gardenlink@orange.fr");
            messageHelper.setSubject("Réinitialisation de mot de passe");
            String content = build(authUser);
            messageHelper.setText(content, true);
        };
        try {
            javaMailSender.send(messagePreparator);
        } catch (MailException e) {
            System.out.println(e);
        }
    }

}