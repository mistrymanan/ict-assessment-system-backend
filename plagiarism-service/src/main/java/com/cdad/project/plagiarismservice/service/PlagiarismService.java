package com.cdad.project.plagiarismservice.service;

import com.cdad.project.plagiarismservice.ServiceClients.Language;
import com.cdad.project.plagiarismservice.ServiceClients.SubmissionServiceClient;
import com.cdad.project.plagiarismservice.ServiceClients.UserQuestionResponseDTO;
import com.cdad.project.plagiarismservice.config.RabbitMQConfig;
import com.cdad.project.plagiarismservice.dto.PlagiarismDTO;
import com.cdad.project.plagiarismservice.dto.PlagiarismMessageDTO;
import com.cdad.project.plagiarismservice.entity.Plagiarism;
import com.cdad.project.plagiarismservice.entity.Status;
import com.cdad.project.plagiarismservice.repository.PlagiarismRepository;
import it.zielke.moji.MossException;
import it.zielke.moji.SocketClient;
import org.apache.commons.io.FileUtils;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlagiarismService {

    private final PlagiarismRepository plagiarismRepository;
    private final ModelMapper modelMapper;
    final private RabbitTemplate rabbitTemplate;
    private final SubmissionServiceClient submissionServiceClient;
    public PlagiarismService(PlagiarismRepository plagiarismRepository, ModelMapper modelMapper, RabbitTemplate rabbitTemplate, SubmissionServiceClient submissionServiceClient) {
        this.plagiarismRepository = plagiarismRepository;
        this.modelMapper = modelMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.submissionServiceClient = submissionServiceClient;
    }

    public Plagiarism save(Plagiarism plagiarism){
        return this.plagiarismRepository.save(plagiarism);
    }

    public Plagiarism requestPlagiarismReportGeneration(String classroomSlug, String assignmentId, String questionId, Jwt jwt){
        Plagiarism plagiarism =new Plagiarism();
        plagiarism.setClassroomSlug(classroomSlug);
        plagiarism.setAssignmentId(assignmentId);
        plagiarism.setQuestionId(questionId);
        plagiarism.setTime(LocalDateTime.ofInstant(Instant.now(), ZoneId.of("Asia/Kolkata")));
        plagiarism.setStatus(Status.PROCESSING);
        plagiarism.setResultLinkMap(new HashMap<>());
        plagiarism=this.save(plagiarism);
        PlagiarismMessageDTO plagiarismMessageDTO=this.modelMapper.map(plagiarism,PlagiarismMessageDTO.class);
        plagiarismMessageDTO.setJwtToken(jwt);
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_DIRECT_EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,plagiarismMessageDTO);
        return plagiarism;
    }
    public List<PlagiarismDTO> getPlagiarisms(String classroomSlug,String assignmentId,String questionId){
        return this.plagiarismRepository.
                getPlagiarismByClassroomSlugAndAssignmentIdAndQuestionId(classroomSlug
                        , assignmentId, questionId)
                .stream().map(plagiarism -> modelMapper.map(plagiarism,PlagiarismDTO.class))
                .collect(Collectors.toList());
    }


    public void  createFiles(List<UserQuestionResponseDTO> submissions, String classroomSlug,String assignmentId,String questionId,String time){
        submissions.stream().forEach(
                submission->{
                    try {
                        String name=submission.getName().replaceAll("\\s+","-");
                        Path userDirectoryPath=Files.createDirectories(Path.of(time,classroomSlug,assignmentId,questionId,submission.getLanguage().toString(),name));
                        System.out.println(userDirectoryPath.toAbsolutePath());
                        Path path=Files.createFile(Path.of(userDirectoryPath.toAbsolutePath().toString(),
                                "Solution"+"."+this.getFileExtension(submission.getLanguage())
                                ));
                        Files.writeString(path,submission.getSourceCode());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );

    }
    public void cleanUpDirectory(String time) {

        try {
            System.out.println("trying to delete"+Path.of(time).toAbsolutePath().toFile());
            FileUtils.deleteDirectory(Path.of(time).toAbsolutePath().toFile());
            System.out.println("seems like deleted");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String checkPlagiarismBasedOnLanguage(Language language,String pathString) throws MossException, IOException {

    Collection<File> fileCollection= FileUtils.listFiles(new File(pathString),
            new String[]{getFileExtension(language)},true);
//            fileCollection.stream().forEach(file -> {
//                System.out.println(file.getAbsolutePath());
//            });
        SocketClient socketClient=new SocketClient();
        socketClient.setUserID("875085204");
        System.out.println(language.getValue());
        if(language.equals(Language.CPP)){
            socketClient.setLanguage("cc");
        }else {
            socketClient.setLanguage(language.getValue());
        }
        socketClient.run();
            for(File f: fileCollection){
                socketClient.uploadFile(f);
            }
            socketClient.sendQuery();
        URL results=socketClient.getResultURL();

        System.out.println("Result available at "+ results.toString());
        return results.toString();
    }

    public void plagiarismCheck(String id,String classroomSlug, String assignmentId, String questionId,String time, Jwt jwt) throws IOException {

            Plagiarism plagiarism=this.plagiarismRepository.getPlagiarismById(id);
            List<UserQuestionResponseDTO> userQuestionResponseDTOS=this.submissionServiceClient
                    .getSubmittedCodes(assignmentId,questionId,jwt);

            this.createFiles(userQuestionResponseDTOS,classroomSlug,assignmentId,questionId,time);

        Set<Language> languages=userQuestionResponseDTOS.stream().map(UserQuestionResponseDTO::getLanguage)
                .collect(Collectors.toSet());

//        System.out.println("printing files name");
        languages.stream().forEach(language -> {
            try {
                String resultLink=this.checkPlagiarismBasedOnLanguage(language,time+"/"+classroomSlug+"/"+assignmentId+"/"+questionId+"/"+language.toString());
                plagiarism.getResultLinkMap().put(language,resultLink);
            } catch (MossException | IOException e) {
                e.printStackTrace();
            }
        });
        this.cleanUpDirectory(time);
        this.save(plagiarism);
        System.out.println(plagiarism);
    }
    public String getFileExtension(Language language){
        if (language.equals(Language.PYTHON)) {
            return "py";
        } else if (language.equals(Language.JAVA)) {
            return "java";
        } else if (language.equals(Language.C)) {
            return "c";
        } else if (language.equals(Language.CPP)) {
            return "cpp";
        }
        else {
            return "";
        }
    }
}
