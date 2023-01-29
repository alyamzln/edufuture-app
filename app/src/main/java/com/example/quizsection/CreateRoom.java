package com.example.quizsection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jitsi.meet.sdk.BroadcastEvent;
import org.jitsi.meet.sdk.BroadcastIntentHelper;
import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class CreateRoom extends AppCompatActivity {
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    public String currentDateAndTime = sdf.format(new Date());

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String uidUser = auth.getCurrentUser().getUid();

    private ImageView back;
    EditText codeBox;
    Button createBtn,shareBtn;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onBroadcastReceive(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        getSupportActionBar().setTitle("Create Room");

        back = findViewById(R.id.back_btn_room1);
        createBtn = findViewById(R.id.createBtn);
        codeBox = findViewById(R.id.codeBox);
        shareBtn = findViewById(R.id.shareBtn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CreateRoom.this, StudyRoom.class);
                startActivity(i);
                finish();
            }
        });

        URL serverURL;
        try {
            serverURL = new URL("https://meet.jit.si");
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Invalid server URL");
        }

        JitsiMeetConferenceOptions defaultOptions
                = new JitsiMeetConferenceOptions.Builder()
                .setServerURL(serverURL)
                .setFeatureFlag("welcomepage.enabled", false)
                .build();

        JitsiMeet.setDefaultConferenceOptions(defaultOptions);

        registerForBroadcastMessages();

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(codeBox.getText().toString())){
                    codeBox.setError("Empty field! Enter a room code.");
                    return;
                }

                else {
                    Map<String, Object> data = new HashMap<>();
                    data.put("Create Study Room Code", codeBox.getText().toString());
                    firebaseFirestore.collection("reports").document("users activity")
                            .collection(uidUser).document(currentDateAndTime).set(data);

                    Map<String, Object> code = new HashMap<>();
                    code.put("User ID", uidUser);
                    code.put("Room Code" , codeBox.getText().toString());
                    firebaseFirestore.collection("reports").document("rooms")
                            .collection("rooms").document(currentDateAndTime).set(code);

                    joinMeeting(codeBox.getText().toString());
                }
            }
        });


        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                if(TextUtils.isEmpty(codeBox.getText().toString())){
                    codeBox.setError("Empty field! Enter a room code.");
                    return;
                }

                else{
                String string = codeBox.getText().toString();
                Intent intent = new Intent();
                intent.setAction(intent.ACTION_SEND);
                intent.putExtra(intent.EXTRA_TEXT, string);
                intent.setType("text/plain");
                startActivity(intent);
                }
            }});

    }
    private void joinMeeting(String code) {
        JitsiMeetConferenceOptions options
                = new JitsiMeetConferenceOptions.Builder()
                .setRoom(code)
                .build();
        JitsiMeetActivity.launch(CreateRoom.this, options);
    }

    @Override
    protected void onDestroy(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }


    private void registerForBroadcastMessages() {
        IntentFilter intentFilter = new IntentFilter();

        for (BroadcastEvent.Type type :BroadcastEvent.Type.values())
        {
            intentFilter.addAction(type.getAction());
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }

    private void onBroadcastReceive(Intent intent){
        if(intent != null){
            BroadcastEvent event = new BroadcastEvent(intent);

            switch(event.getType()){
                case CONFERENCE_JOINED:
                    Timber.i("Conference Joined with url%s", event.getData().get("url"));
                    break;
                case PARTICIPANT_JOINED:
                    Timber.i("Participant joined%s", event.getData().get("name"));
                    break;
                case PARTICIPANT_LEFT:
                    Timber.i("Participant left%s", event.getData().get("name"));
                    break;


            }
        }
    }

    // Example for sending actions to JitsiMeetSDK
    private void hangUp() {
        Intent hangupBroadcastIntent = BroadcastIntentHelper.buildHangUpIntent();
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(hangupBroadcastIntent);


}
}