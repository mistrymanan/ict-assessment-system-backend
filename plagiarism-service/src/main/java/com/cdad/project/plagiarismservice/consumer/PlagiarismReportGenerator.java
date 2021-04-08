package com.cdad.project.plagiarismservice.consumer;

import com.cdad.project.plagiarismservice.config.RabbitMQConfig;
import com.cdad.project.plagiarismservice.dto.PlagiarismMessageDTO;
import com.cdad.project.plagiarismservice.service.PlagiarismService;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PlagiarismReportGenerator {
    final private PlagiarismService plagiarismService;

    public PlagiarismReportGenerator(PlagiarismService plagiarismService) {
        this.plagiarismService = plagiarismService;
    }

    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(RabbitMQConfig.QUEUE_DIRECT_EXCHANGE),key = RabbitMQConfig.ROUTING_KEY,value = @Queue(RabbitMQConfig.QUEUE)))
    public void plagiarismReportGenerator(PlagiarismMessageDTO messageDTO) throws IOException {
        plagiarismService.plagiarismCheck(
                messageDTO.getId()
                , messageDTO.getClassroomSlug()
                , messageDTO.getAssignmentId()
                , messageDTO.getQuestionId()
                ,messageDTO.getJwtToken());
    }
}
