package br.com.connect.model.transport.mail;

import br.com.connect.model.enums.MailTypeEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record MailDTO(@Email String from, @Email String to, @NotBlank String subject, MailTypeEnum type,
                      Map<String, Object> properties) {
}
