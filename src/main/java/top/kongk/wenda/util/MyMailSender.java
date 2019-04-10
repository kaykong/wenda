package top.kongk.wenda.util;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.internet.MimeUtility;
import java.util.Map;


/**
 * @author kk
 */
@Service
public class MyMailSender {
    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);


    @Autowired
    private VelocityEngine velocityEngine;

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private JavaMailSender mailSender;

    public boolean sendWithHTMLTemplate(String to, String subject,
                                        String template, Map<String, Object> model) {
        try {

            SimpleMailMessage message = new SimpleMailMessage();

            String nick = MimeUtility.encodeText("问答管理员");
            message.setFrom(nick + " <" + from + ">");
            message.setTo(to);
            message.setSubject(subject);

            String result = VelocityEngineUtils
                    .mergeTemplateIntoString(velocityEngine, template, "UTF-8", model);

            message.setText(result);
            mailSender.send(message);

            return true;
        } catch (Exception e) {
            logger.error("发送邮件失败" + e.getMessage());
            return false;
        }
    }
}
