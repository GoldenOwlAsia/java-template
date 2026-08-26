---
name: add-mail-handler
description: >-
  Adds a Thymeleaf email handler in the email feature (AbstractMailHandler,
  MailService wiring, template HTML, AttributeConstant keys, optional
  UserMailDispatcher). Use when the user asks for a new email/mail template,
  verification mail, welcome mail, notification email, or AbstractMailHandler.
---

# Add a mail handler

Follow existing handlers: `VerifyUserMailHandler`, `CompleteUserMailHandler`.

## Steps

1. Add mail DTO under `email/dto/` (`@Data` `@Builder`) if needed.
2. Add keys to `AttributeConstant` for template variables.
3. Create `email/template/<Name>MailHandler.java` extending `AbstractMailHandler`.
4. Add HTML under `src/main/resources/templates/email/<name>-mail-template.html`.
5. Wire send method on `MailService` / `MailServiceImpl` (catch `MessagingException`, log, do not rethrow).
6. If mail is triggered from JPA lifecycle: keep `UserEntityListener` **sync**; put `@Async` on a Spring bean like `UserMailDispatcher`. Hibernate does not invoke Spring proxies on entity-listener methods.

## Do not

- Put `@Async` on `@PostPersist` / `@PostUpdate` methods.
- Send mail from controllers.
- Fail the business transaction solely because SMTP failed.
- Log credentials or full raw MIME unless debugging was requested.
