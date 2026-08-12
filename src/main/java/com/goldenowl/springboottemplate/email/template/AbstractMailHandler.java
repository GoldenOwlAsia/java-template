package com.goldenowl.springboottemplate.email.template;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.Charset;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

/**
 * Abstract email notifier used for sending emails. Concrete implementations provide template name
 * and required attributes.
 *
 * <p>This class follows the <strong>Template Method</strong> pattern: common send steps live here,
 * while subclasses implement the specific steps.
 */
@Getter
@Setter
@Slf4j
@AllArgsConstructor
public abstract class AbstractMailHandler {

  private JavaMailSender mailSender;

  private SpringTemplateEngine templateEngine;

  private String senderMail;

  private String[] recipientMails;

  private Map<String, Object> variables;

  /**
   * Retrieves the subject for the email.
   *
   * @return the subject for the email
   */
  public abstract String getMailSubject();

  /**
   * Retrieves the name of the email template to be used for sending emails.
   *
   * @return the name of the email template
   */
  public abstract String getTemplate();

  public abstract Charset getCharset();

  public void send() throws MessagingException {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper =
        new MimeMessageHelper(
            message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, getCharset().name());
    helper.setSubject(getMailSubject());
    helper.setFrom(senderMail);
    helper.setTo(recipientMails);
    Context context = new Context();
    context.setVariables(variables);
    String html = templateEngine.process(getTemplate(), context);
    helper.setText(html, true);

    log.info("Sending email with subject: {}", getMailSubject());
    mailSender.send(message);
  }
}
