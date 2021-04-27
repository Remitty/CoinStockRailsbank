package com.wyre.trade.chat;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.wyre.trade.R;
import com.google.firebase.database.FirebaseDatabase;


public class SocialGroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_group);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // mandatory
        // it creates the chat configurations
//        ChatManager.Configuration mChatConfiguration =
//                new ChatManager.Configuration.Builder(<APP_ID>)
//                    .firebaseUrl(<FIREBASE_DATABASE_URL>)
//                    .storageBucket(<STORAGE_BUCKET>)
//                    .build();
//
//        ChatManager.start(<CONTEXT>, mChatConfiguration, <LOGGED_USER>);

//        ChatUI.getInstance().setContext(this);
////        ChatUI.getInstance().showConversationsListActivity();
//
//        ChatManager.startWithEmailAndPassword(this, getString(R.string.google_app_id),
//                "john@nashville.it", "123456", new ChatAuthentication.OnChatLoginCallback() {
//                    @Override
//                    public void onChatLoginSuccess(IChatUser currentUser) {
//                        ChatManager.getInstance().createContactFor(currentUser.getId(), currentUser.getEmail(),
//                                "John", "Nashville", new OnContactCreatedCallback() {
//                                    @Override
//                                    public void onContactCreatedSuccess(ChatRuntimeException exception) {
//                                        if (exception == null) {
////                                            ChatUI.getInstance().openConversationsListActivity();
//                                            ChatUI.getInstance().openConversationMessagesActivity("81gLZhYmpTZM0GGuUI9ovD7RaCZ2", "Chuck Norris");
//                                        } else {
//                                            // TODO: handle the exception
//                                        }
//                                    }
//                                });
//                    }
//
//                    @Override
//                    public void onChatLoginError(Exception e) {
//                        // TODO: 22/02/18
//                    }
//                });
    }
}
