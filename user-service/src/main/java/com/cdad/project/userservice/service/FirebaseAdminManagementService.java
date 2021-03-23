package com.cdad.project.userservice.service;

import com.cdad.project.userservice.UserServiceApplication;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service
public class FirebaseAdminManagementService {

    public FirebaseAdminManagementService() throws URISyntaxException, IOException {
        URL resource = UserServiceApplication.class.getClassLoader().getResource("cred.json");
        File file = new File(resource.toURI());
        FileInputStream serviceAccount = new FileInputStream(file);
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
        FirebaseApp.initializeApp(options);
    }

    public void setAdminRights(String email,Boolean isAllowed) throws FirebaseAuthException {
        Map<String, Object> additionalClaims = new HashMap<String, Object>();
        additionalClaims.put("isAdmin", isAllowed);
        setClaims(email,additionalClaims);
    }
    public void setClassroomCreationRights(String email,Boolean isAllowed) throws FirebaseAuthException{
        Map<String, Object> additionalClaims = new HashMap<String, Object>();
        additionalClaims.put("hasCreateClassroomRights", isAllowed);
        setClaims(email,additionalClaims);
    }
    public void setClaims(String email,Map<String,Object> additionalClaims) throws FirebaseAuthException {
        UserRecord userRecord=FirebaseAuth.getInstance().getUserByEmail(email);
        String uid=userRecord.getUid();
        FirebaseAuth.getInstance().setCustomUserClaims(uid,additionalClaims);
    }

}
