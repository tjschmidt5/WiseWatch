package edu.unc.tjschmidt.wisewatch;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by tjschmidt on 12/3/17.
 */

public class FallDetected extends AppCompatActivity {

    private MediaPlayer media;
    private AudioManager audioManager;
 //   private Thread thread;
    private FallDetected my_class;
    private Button ob;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fall_detected);
        ob = (Button)findViewById(R.id.okay);

       my_class = new FallDetected();

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(10000);
        media = new MediaPlayer();

        //used similar code to this stack over flow post:
        // https://stackoverflow.com/questions/2618182/how-to-play-ringtone-alarm-sound-in-android


        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();

        //Used android documentation for this

        new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long l) {
                Log.v("Tag", "Ticking");
            }

            @Override
            public void onFinish() {
                String button_text = ob.getText().toString();
                if(button_text == "Non-emergency"){
                    return;
                }

                Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                phoneIntent.setData(Uri.parse("tel:1-630-706-1425"));
                //check to see if you have the applicable permissions
                //have to do it like this, because you can't pass in a context in here
                boolean permission = checkPermission();
                if(permission){
                    return;
                }

                startActivity(phoneIntent);

            }
        }.start();



        //https://stackoverflow.com/questions/8791754/how-to-make-a-phone-call-with-speaker-on
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setSpeakerphoneOn(true);



    }


    //private function to see if you have permission to make a call.
    //if you do, it returns false (counter intuitive), but made sense at the time
    private boolean checkPermission(){
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;

    }

    public void okayButton(View v){
        ob.setText("Non-emergency");
    }


}
