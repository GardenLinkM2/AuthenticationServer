package com.gardenlink.authentication.service.mailer;


import com.gardenlink.authentication.domain.AuthUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Service
public class SendNewsletter {

    @Value("${app.front.url}")
    protected String frontUrl;

    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;


    public SendNewsletter(TemplateEngine templateEngine, JavaMailSender javaMailSender) {
        this.templateEngine = templateEngine;
        this.javaMailSender = javaMailSender;
    }

    public String build(String content, String title) {
        Context context = new Context();
        context.setVariable("content", content);
        context.setVariable("title", title);

        return templateEngine.process("sendNewsletter", context);
    }

    public void prepareAndSend(String mailContent, String title, AuthUser authUser) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setTo(authUser.getEmail());
            messageHelper.setFrom("gardenlink@orange.fr");
            messageHelper.setSubject("Newsletter GardenLink - " + title);
            String content = build(mailContent, title);
            messageHelper.setText(content, true);
        };
        try {
            javaMailSender.send(messagePreparator);
        } catch (MailException e) {
            //Ign
        }
    }

}