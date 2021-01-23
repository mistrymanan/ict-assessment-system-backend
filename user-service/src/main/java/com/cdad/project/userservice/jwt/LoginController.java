//package com.cdad.project.userservice.jwt;
//
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseAuthException;
//import com.google.firebase.auth.FirebaseToken;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.Resource;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import javax.annotation.PostConstruct;
//import java.io.IOException;
//import java.io.InputStream;
//
//@RestController
//public class LoginController {
////  @PostConstruct
////  public void init() throws IOException {
////    Resource resource = new ClassPathResource("firebase-creds.json");
////    GoogleCredentials credentials = GoogleCredentials.fromStream(resource.getInputStream());
////    FirebaseOptions options = FirebaseOptions
////            .builder()
////            .setCredentials(credentials)
////            .build();
////    FirebaseApp.initializeApp(options);
////  }
////
////  @GetMapping("/login")
////  public String verifyLogin(@RequestParam String idToken) throws FirebaseAuthException {
////    System.out.println(idToken);
////    FirebaseToken token = FirebaseAuth.getInstance().verifyIdToken(idToken);
////    return "Verified! : " + token.getEmail();
////  }
////
////  @ExceptionHandler(FirebaseAuthException.class)
////  public String error(Exception e) {
////    System.out.println(e.getMessage());
////    return "Not Verified";
////  }
//}
