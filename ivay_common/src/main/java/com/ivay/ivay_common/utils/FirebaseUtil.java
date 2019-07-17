package com.ivay.ivay_common.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import com.ivay.ivay_common.dto.NoticeMsg;

@Component
public class FirebaseUtil {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseUtil.class);

    private static String firebaseSdkUrl;

    private static String firebaseProjectUrl;

    @Value("${firebase_sdk_url}")
    public void setFirebaseSdkUrl(String firebaseSdkUrl) {
        FirebaseUtil.firebaseSdkUrl = firebaseSdkUrl;
    }

    @Value("${firebase_project_url}")
    public void setFirebaseProjectUrl(String firebaseProjectUrl) {
        FirebaseUtil.firebaseProjectUrl = firebaseProjectUrl;
    }

    private static Map<String, FirebaseApp> instanceMap = new HashMap<String, FirebaseApp>();

    private FirebaseUtil() {

    }

    public static void addInstance(String key) throws IOException {
        if (!instanceMap.containsKey(key)) {
            FileInputStream serviceAccount = new FileInputStream(firebaseSdkUrl);
            FirebaseOptions options =
                new FirebaseOptions.Builder().setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(firebaseProjectUrl).build();
            FirebaseApp app = FirebaseApp.initializeApp(options);
            logger.info("firebase init success!");
            instanceMap.put(key, app);
        }

    }

    public static FirebaseApp getInstance(String key) {
        return instanceMap.get(key);
    }

    public static FirebaseApp initFirebase() throws IOException {
        FileInputStream serviceAccount = new FileInputStream(firebaseSdkUrl);
        FirebaseOptions options = new FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount)).setDatabaseUrl(firebaseProjectUrl).build();
        FirebaseApp firebaseApp = FirebaseApp.initializeApp(options);
        logger.info("firebase初始化成功！");
        return firebaseApp;
    }

    // 单个发送
    public static void sendMsgToFmcToken(NoticeMsg msg) throws Exception {
        addInstance("app");
        FirebaseApp firebaseApp = getInstance("app");
        logger.info("firebaseApp Instance get success......");
        Notification notification = new Notification(msg.getTitle(), msg.getFirebaseMsg());
        Map<String, String> map = new HashMap<String, String>();

        if (msg.getPageId() != null) {
            map.put("to", msg.getPageId()); // 跳转的页面
        }

        if (msg.getGid() != null) {
            map.put("gid", msg.getGid()); // 只有部分页面需要传递借款gid
        }

        if (firebaseApp != null) {
            Message message =
                Message.builder().setNotification(notification).putAllData(map).setToken(msg.getFmcToken()).build();

            logger.info("message=" + message);
            FirebaseMessaging fbmsg = FirebaseMessaging.getInstance();
            logger.info("fbmsg=" + fbmsg);
            String response = fbmsg.send(message);
            logger.info("Successfully sent message: " + response);
        }

    }

    // 多个发送
    public static void sendBatchMsgToFmcToken(List<String> registrationTokens, String title, String content)
        throws FirebaseMessagingException, IOException {
        addInstance("app");
        FirebaseApp firebaseApp = getInstance("app");
        logger.info("firebaseApp Instance get success......");
        Notification notification = new Notification(title, content);

        if (firebaseApp != null) {
            // These registration tokens come from the client FCM SDKs.
            MulticastMessage message =
                MulticastMessage.builder().setNotification(notification).addAllTokens(registrationTokens).build();
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
            if (response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();
                List<String> failedTokens = new ArrayList<>();
                for (int i = 0; i < responses.size(); i++) {
                    if (!responses.get(i).isSuccessful()) {
                        // The order of responses corresponds to the order of the registration tokens.
                        failedTokens.add(registrationTokens.get(i));
                    }
                }

                logger.info("List of tokens that caused failures: " + failedTokens);
            }
        }

    }

    // 旧的发送消息推送代码
    // 单个发送
    public static void sendMsgToFmcToken1(String registrationToken, String title, String msg, String pageId,
        String userGid) throws FirebaseMessagingException, InterruptedException, ExecutionException, IOException {
        // String registrationToken =
        // "e0iIeVUJkqA:APA91bGRmnXuzGhAlIGu7CKpM48Ix_3wJyk3E8QM_1iNJtQVfWvmNzk1vJXMlXlfbs8Vn0eezk8xQgpTRv1Qk1VrAA_-e23_as4HJsEcN-C29ArTdSOUz2IwOeg_quW9jIxB_uOOA1fZ";
        addInstance("app");
        FirebaseApp firebaseApp = getInstance("app");
        logger.info("firebaseApp Instance get success......");
        Notification notification = new Notification(title, msg);
        Map<String, String> map = new HashMap<String, String>();
        map.put("to", pageId); // 跳转的页面
        if (userGid != null) {
            map.put("gid", userGid); // 只有部分页面需要传递userGid
        }

        if (firebaseApp != null) {
            Message message =
                Message.builder().setNotification(notification).putAllData(map).setToken(registrationToken).build();

            logger.info("message=" + message);
            FirebaseMessaging fbmsg = FirebaseMessaging.getInstance();
            logger.info("fbmsg=" + fbmsg);
            String response = fbmsg.send(message);
            logger.info("Successfully sent message: " + response);
        }

    }

}
