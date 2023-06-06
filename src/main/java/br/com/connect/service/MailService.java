package br.com.connect.service;

import br.com.connect.configuration.rabbitmq.RabbitQueues;
import br.com.connect.model.enums.MailTypeEnum;
import br.com.connect.model.transport.MailDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);

    private final MessageService messageService;

    public MailService(MessageService messageService) {
        this.messageService = messageService;
    }

    public void buildAndpublish(String from, String to, String subject, MailTypeEnum type, Map<String, Object> properties) {
        LOGGER.info("Building and publish mail message...");
        from = (from != null && !from.isEmpty()) ? from : "noreply.uboard@gmail.com";
        MailDTO mailDTO = new MailDTO(from, to, subject, type, properties);
        this.messageService.enqueue(RabbitQueues.SEND_MAIL, mailDTO);
    }
}
