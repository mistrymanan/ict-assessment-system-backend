package com.cdad.project.plagiarismservice.service;

import com.cdad.project.plagiarismservice.ServiceClients.Language;
import com.cdad.project.plagiarismservice.ServiceClients.SubmissionServiceClient;
import com.cdad.project.plagiarismservice.ServiceClients.UserQuestionResponseDTO;
import com.cdad.project.plagiarismservice.ServiceClients.mossapi.GraphDataProducerServiceClient;
import com.cdad.project.plagiarismservice.ServiceClients.mossapi.dto.DataProducerUtility;
import com.cdad.project.plagiarismservice.ServiceClients.mossapi.dto.GetProcessedData;
import com.cdad.project.plagiarismservice.config.RabbitMQConfig;
import com.cdad.project.plagiarismservice.dto.PlagiarismDTO;
import com.cdad.project.plagiarismservice.dto.PlagiarismMessageDTO;
import com.cdad.project.plagiarismservice.dto.PlagiarismResultDTO;
import com.cdad.project.plagiarismservice.entity.GraphData;
import com.cdad.project.plagiarismservice.entity.Plagiarism;
import com.cdad.project.plagiarismservice.entity.Result;
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
    private final GraphDataProducerServiceClient graphDataProducerServiceClient;
    public PlagiarismService(PlagiarismRepository plagiarismRepository, ModelMapper modelMapper, RabbitTemplate rabbitTemplate, SubmissionServiceClient submissionServiceClient, GraphDataProducerServiceClient graphDataProducerServiceClient) {
        this.plagiarismRepository = plagiarismRepository;
        this.modelMapper = modelMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.submissionServiceClient = submissionServiceClient;
        this.graphDataProducerServiceClient = graphDataProducerServiceClient;
    }

    public Plagiarism save(Plagiarism plagiarism){
        return this.plagiarismRepository.save(plagiarism);
    }
    public Plagiarism getPlagiarismById(String id){
        return this.plagiarismRepository.getPlagiarismById(id);
    }
    public Plagiarism requestPlagiarismReportGeneration(String classroomSlug, String assignmentId, String questionId, Jwt jwt){
        Plagiarism plagiarism =new Plagiarism(classroomSlug
                ,assignmentId
                ,questionId
                ,Status.PROCESSING
                ,new LinkedList<>()
                ,LocalDateTime.ofInstant(Instant.now(), ZoneId.of("Asia/Kolkata")));
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

    public Result checkPlagiarismBasedOnLanguage(Language language,String pathString) throws MossException, IOException {

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
        Result result=new Result();
        result.setLink(socketClient.getResultURL().toString());
        result.setLanguage(language);

        GetProcessedData getProcessedData=DataProducerUtility.processData(this.graphDataProducerServiceClient
                .fetchDataForGraph(result.getLink()));
        GraphData graphData=this.modelMapper
                .map(getProcessedData,GraphData.class);
        result.setGraphData(graphData);
        System.out.println("Result available at "+ result.getLink());
        return result;
    }

    public void plagiarismCheck(String id,String classroomSlug, String assignmentId, String questionId,String time, Jwt jwt) throws IOException {

            Plagiarism plagiarism=this.plagiarismRepository.getPlagiarismById(id);
            List<UserQuestionResponseDTO> userQuestionResponseDTOS=this.submissionServiceClient
                    .getSubmittedCodes(assignmentId,questionId,jwt);
            plagiarism.setNumberOfSubmissions(userQuestionResponseDTOS.size());
            this.createFiles(userQuestionResponseDTOS,classroomSlug,assignmentId,questionId,time);

        Set<Language> languages=userQuestionResponseDTOS.stream().map(UserQuestionResponseDTO::getLanguage)
                .collect(Collectors.toSet());

//        System.out.println("printing files name");
        languages.stream().forEach(language -> {
            try {
                Result result=this.checkPlagiarismBasedOnLanguage(language,time+"/"+classroomSlug+"/"+assignmentId+"/"+questionId+"/"+language.toString());
                plagiarism.getResults().add(result);
            } catch (MossException | IOException e) {
                e.printStackTrace();
            }
        });
        this.cleanUpDirectory(time);
        plagiarism.setStatus(Status.GENERATED);
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

    public PlagiarismResultDTO getPlagiarismResultDTOById(String plagiarismId) {
        Plagiarism plagiarism=this.getPlagiarismById(plagiarismId);
        return this.modelMapper.map(plagiarism,PlagiarismResultDTO.class);
    }

    public GraphData getPlagiarismResultByLanguage(String plagiarismId, Language language) {
        Plagiarism plagiarism=this.getPlagiarismById(plagiarismId);
        return plagiarism.getResults()
                .stream()
                .filter(result1 -> result1.getLanguage().equals(language))
                .collect(Collectors.toList())
                .get(0).getGraphData();
    }
}