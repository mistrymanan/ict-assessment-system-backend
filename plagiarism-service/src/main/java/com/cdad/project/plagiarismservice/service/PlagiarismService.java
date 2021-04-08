package com.cdad.project.plagiarismservice.service;

import ch.qos.logback.core.util.FileUtil;
import com.cdad.project.plagiarismservice.ServiceClients.Language;
import com.cdad.project.plagiarismservice.ServiceClients.SubmissionServiceClient;
import com.cdad.project.plagiarismservice.ServiceClients.UserQuestionResponseDTO;
import com.cdad.project.plagiarismservice.repository.PlagiarismRepository;
import it.zielke.moji.MossException;
import it.zielke.moji.SocketClient;
import org.apache.catalina.User;
import org.apache.commons.io.FileUtils;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlagiarismService {

    private final PlagiarismRepository plagiarismRepository;
    private final SubmissionServiceClient submissionServiceClient;
    public PlagiarismService(PlagiarismRepository plagiarismRepository, SubmissionServiceClient submissionServiceClient) {
        this.plagiarismRepository = plagiarismRepository;
        this.submissionServiceClient = submissionServiceClient;
    }

    public void  createFiles(List<UserQuestionResponseDTO> submissions, String classroomSlug,String assignmentId,String questionId){
        submissions.stream().forEach(
                submission->{
                    try {
                        String name=submission.getName().replaceAll("\\s+","-");
                        Path userDirectoryPath=Files.createDirectories(Path.of(classroomSlug,assignmentId,questionId,submission.getLanguage().toString(),name));
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

    public void checkPlagiarismBasedOnLanguage(Language language,String pathString) throws MossException, IOException {

    Collection<File> fileCollection= FileUtils.listFiles(new File(pathString),
            new String[]{getFileExtension(language)},true);
        System.out.println("in checkPlagiarism function");
            fileCollection.stream().forEach(file -> {
                System.out.println(file.getAbsolutePath());
            });
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
    }

    public void plagiarismCheck(String classroomSlug, String assignmentId, String questionId, Jwt jwt){
            List<UserQuestionResponseDTO> userQuestionResponseDTOS=this.submissionServiceClient.getSubmittedCodes(assignmentId,questionId,jwt);
            this.createFiles(userQuestionResponseDTOS,classroomSlug,assignmentId,questionId);
        Set<Language> languages=userQuestionResponseDTOS.stream().map(UserQuestionResponseDTO::getLanguage)
                .collect(Collectors.toSet());
        System.out.println("printing files name");
        languages.stream().forEach(language -> {
            try {
                this.checkPlagiarismBasedOnLanguage(language,classroomSlug+"/"+assignmentId+"/"+questionId+"/"+language.toString());
            } catch (MossException | IOException e) {
                e.printStackTrace();
            }
        });
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
