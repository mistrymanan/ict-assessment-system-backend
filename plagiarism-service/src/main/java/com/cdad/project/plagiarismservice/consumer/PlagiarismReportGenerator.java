package com.cdad.project.plagiarismservice.consumer;


import com.rabbitmq.client.Channel;
import com.cdad.project.plagiarismservice.config.RabbitMQConfig;
import com.cdad.project.plagiarismservice.dto.PlagiarismMessageDTO;
import com.cdad.project.plagiarismservice.service.PlagiarismService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class PlagiarismReportGenerator {
    final private PlagiarismService plagiarismService;

    public PlagiarismReportGenerator(PlagiarismService plagiarismService) {
        this.plagiarismService = plagiarismService;
    }

    @RabbitListener(bindings = @QueueBinding(exchange = @Exchange(RabbitMQConfig.QUEUE_DIRECT_EXCHANGE),key = RabbitMQConfig.ROUTING_KEY,value = @Queue(RabbitMQConfig.QUEUE)))
//    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void plagiarismReportGenerator(Message message, PlagiarismMessageDTO messageDTO, Channel channel) throws IOException {
        //message.getMessageProperties()
        System.out.println(message.getMessageProperties().getDeliveryTag());
//        channel.basicAck(message.getMessageProperties().getDeliveryTag(),true);
        plagiarismService.plagiarismCheck(
                messageDTO.getId()
                , messageDTO.getClassroomSlug()
                , messageDTO.getAssignmentId()
                , messageDTO.getQuestionId()
                , UUID.randomUUID().toString()
                ,messageDTO.getJwtToken());
    }
}