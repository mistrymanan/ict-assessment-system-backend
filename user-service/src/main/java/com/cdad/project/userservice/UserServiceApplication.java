package com.cdad.project.userservice;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


@SpringBootApplication
public class UserServiceApplication {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    public static void main(String[] args) throws IOException, FirebaseAuthException, URISyntaxException {
//        FileInputStream serviceAccount =
  //              new FileInputStream(String.valueOf(UserServiceApplication.class.getClassLoader().getResource("cred.json")));
//        InputStream is= UserServiceApplication.class.getClassLoader().getResourceAsStream("cred.json");

        String uid="RSswiFamxocrEARR63DocvMHECM2";
        UserRecord userRecord= FirebaseAuth.getInstance().getUser(uid);
        System.out.println(userRecord.getCustomClaims());
        Map<String, Object> additionalClaims = new HashMap<String, Object>();
        additionalClaims.put("isAdmin", true);
        additionalClaims.put("hasCreateClassroomRights", true);
//        String customToken=FirebaseAuth.getInstance().createCustomToken("13EHylU27DPp12T2gqiRYSDE5hx2",additionalClaims);
//        System.out.println(customToken);
        FirebaseAuth.getInstance().setCustomUserClaims(uid,additionalClaims);
        System.out.println(userRecord.getCustomClaims());
        UserRecord user = FirebaseAuth.getInstance().getUser(uid);
        System.out.println(user.getCustomClaims().get("isAdmin"));
        SpringApplication.run(UserServiceApplication.class, args);

    }

//  @Bean
//  public RouteLocator routes(RouteLocatorBuilder builder) {
//    return builder.routes().build();
//  }
}
