package com.servicecops.project.utils;

import com.alibaba.fastjson2.JSONObject;
import com.servicecops.project.models.database.SystemUserModel;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    private static final String MAIL_SENDER_ADDRESS = "ceres1738@gmail.com";
    private static final String MAIL_SENDER_NAME = "Ceres Support";

    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;

    public void dispatchNotificationMail(SystemUserModel receiver, String subject, Map<String, List<SystemUserModel>> schedule, JSONObject randomQuote, String template) throws MessagingException, UnsupportedEncodingException {
        if (receiver.getEmail() == null) {
            log.warn("Receiver email is null, cannot send email to {}", receiver.getFirstName() + " " + receiver.getLastName());
            return;
        }

        Context context = getContext(receiver, schedule, randomQuote);

        String process = templateEngine.process(template, context);
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setSubject(subject);
        helper.setFrom(MAIL_SENDER_ADDRESS, MAIL_SENDER_NAME);
        helper.setText(process, true);
        helper.setTo(receiver.getEmail());

        try {
//            emailSender.send(mimeMessage);
            log.info("Sending email to {}", receiver.getEmail());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalStateException(e.getMessage());
        }
    }

    private static Context getContext(SystemUserModel receiver, Map<String, List<SystemUserModel>> schedule, JSONObject randomQuote) {
        Context context = new Context();

        context.setVariable("quote", randomQuote.getString("quote"));
        context.setVariable("author", randomQuote.getString("author"));
        context.setVariable("year", LocalDate.now().getYear());

        context.setVariable("name", receiver.getFirstName() + " " + receiver.getLastName());
        context.setVariable("schedule", schedule);
        return context;
    }
}
