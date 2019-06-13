package com.ivay.ivay_app.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

@Component
public class FirebaseUtil {
	
   private static final Logger logger=LoggerFactory.getLogger("adminLogger");
   
   private static String firebaseSdkUrl;
   
   private static String firebaseProjectUrl;
   
   @Value("${firebase_sdk_url}")
   public  void setFirebaseSdkUrl(String firebaseSdkUrl) {
	   FirebaseUtil.firebaseSdkUrl = firebaseSdkUrl;
   }
   
   @Value("${firebase_project_url}")
   public  void setFirebaseProjectUrl(String firebaseProjectUrl) {
	   FirebaseUtil.firebaseProjectUrl = firebaseProjectUrl;
   }

  public static FirebaseApp initFirebase() throws IOException {
	  FileInputStream serviceAccount = new FileInputStream(firebaseSdkUrl);
	  FirebaseOptions options = new FirebaseOptions.Builder()
	  .setCredentials(GoogleCredentials.fromStream(serviceAccount))
	  .setDatabaseUrl(firebaseProjectUrl)
	  .build();
	  FirebaseApp firebaseApp=FirebaseApp.initializeApp(options);
	  logger.info("firebase初始化成功！");
	   return firebaseApp;
   }
   
   public static void sendMsgToFmcToken(String registrationToken,String msg) throws FirebaseMessagingException, InterruptedException, ExecutionException, IOException {
//	   String registrationToken = "e0iIeVUJkqA:APA91bGRmnXuzGhAlIGu7CKpM48Ix_3wJyk3E8QM_1iNJtQVfWvmNzk1vJXMlXlfbs8Vn0eezk8xQgpTRv1Qk1VrAA_-e23_as4HJsEcN-C29ArTdSOUz2IwOeg_quW9jIxB_uOOA1fZ";
	   FirebaseApp firebaseApp=initFirebase();
	   
	   Message message = Message.builder()
	       .putData("msg", msg)
	       .setToken(registrationToken)
	       .build();

	   logger.info("message="+message);
	   FirebaseMessaging fbmsg=FirebaseMessaging.getInstance();
	   logger.info("fbmsg="+fbmsg); 
	   String response = fbmsg.send(message);
	   logger.info("Successfully sent message: " + response);
   }

   
   public static void main(String[] args) throws IOException, FirebaseMessagingException, InterruptedException, ExecutionException {
	   initFirebase();
	   String registrationToken = "e0iIeVUJkqA:APA91bGRmnXuzGhAlIGu7CKpM48Ix_3wJyk3E8QM_1iNJtQVfWvmNzk1vJXMlXlfbs8Vn0eezk8xQgpTRv1Qk1VrAA_-e23_as4HJsEcN-C29ArTdSOUz2IwOeg_quW9jIxB_uOOA1fZ";
	   sendMsgToFmcToken(registrationToken,"test");
   }
   
}
