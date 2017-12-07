package edu.unc.tjschmidt.wisewatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by tjschmidt on 11/26/17.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("Tag", "Made it to the alarm receiver");
        //Toast.makeText(context, "This is my text", Toast.LENGTH_LONG).show();

        String name = intent.getStringExtra("Name");
        //int pill_size = intent.getIntExtra("Pill Size",30);
        int num_pills = intent.getIntExtra("Number of Pills",1);
        String pills_string = Integer.toString(num_pills);

        Toast.makeText(context, "Take " + pills_string + " " + name + " pills!", Toast.LENGTH_LONG).show();

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(2000);

        //https://stackoverflow.com/questions/2618182/how-to-play-ringtone-alarm-sound-in-android
        //used similar code to this stackoverflow post
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();






    }
}
