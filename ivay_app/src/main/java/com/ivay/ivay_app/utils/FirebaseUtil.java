package com.ivay.ivay_app.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

public class FirebaseUtil {
	
   public static void initFirebase() throws IOException {
	  FileInputStream serviceAccount = new FileInputStream("C:/firebaseKey/ovay-staging-firebase-adminsdk-y0oiy-5a971cabf6.json");
	  FirebaseOptions options = new FirebaseOptions.Builder()
	  .setCredentials(GoogleCredentials.fromStream(serviceAccount))
	  .setDatabaseUrl("https://ovay-staging.firebaseio.com")
	  .build();
	  FirebaseApp.initializeApp(options);
	  System.out.println("firebase初始化成功！");
   }
   
   public static void sendMsgToFmcToken() throws FirebaseMessagingException, InterruptedException, ExecutionException {
	   String registrationToken = "e0iIeVUJkqA:APA91bGRmnXuzGhAlIGu7CKpM48Ix_3wJyk3E8QM_1iNJtQVfWvmNzk1vJXMlXlfbs8Vn0eezk8xQgpTRv1Qk1VrAA_-e23_as4HJsEcN-C29ArTdSOUz2IwOeg_quW9jIxB_uOOA1fZ";

	   Message message = Message.builder()
	       .putData("score", "850")
	       .putData("time", "2:45")
	       .setToken(registrationToken)
	       .build();

	   System.out.println(message);
	   FirebaseMessaging fbmsg=FirebaseMessaging.getInstance();
	   System.out.println(fbmsg);
	   String response = fbmsg.send(message);
	   System.out.println("Successfully sent message: " + response);

   }
   
   public static void main(String[] args) throws IOException, FirebaseMessagingException, InterruptedException, ExecutionException {
	   initFirebase();
	   sendMsgToFmcToken();
   }
   
}
