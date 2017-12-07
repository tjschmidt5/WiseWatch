package edu.unc.tjschmidt.wisewatch;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MedicationMain extends AppCompatActivity implements SensorEventListener {

    private Button btn;
    private ListView list;
    private ArrayList<Medication> medications;
    private ArrayAdapter<Medication> adapter;
    private TextView voiceInput;
    private Button sendRequest;
    private Button emergency;
    private SensorManager sm;
    private Sensor s;
    private List<Sensor> l;
    private float sens_val1;
    private float sens_val2;
    private float sens_val3;
    private float sens_val4;
    private boolean free_fall;
    private boolean stop;

    SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_main);

        sens_val1 = 9.8f;
        sens_val2 = 9.8f;
        sens_val3 = 9.8f;
        sens_val4 = 9.8f;



        list = (ListView) findViewById(R.id.medlist);
        btn = (Button) findViewById(R.id.addBtn);
        emergency = (Button) findViewById(R.id.emergency);
        emergency.setBackgroundColor(Color.RED);

        voiceInput = (TextView) findViewById(R.id.voice);
        sendRequest = (Button) findViewById(R.id.sendrequest);
        //sendRequest.setBackgroundColor(Color.WHITE);
        sendRequest.setText(""); //this should ensure that the request button can't crash.
        sendRequest.setBackgroundColor(Color.TRANSPARENT); //again should make sure it won't show


        medications = new ArrayList<Medication>();

        //used documentation for arrayAdapter
        adapter = new ArrayAdapter<Medication>(getApplicationContext(), android.R.layout.simple_spinner_item, medications);

        list.setAdapter(adapter);

        db = openOrCreateDatabase("MyDatabase", Context.MODE_PRIVATE, null);

        String q1 = "DROP TABLE IF EXISTS Medications";

        String query = "CREATE TABLE IF NOT EXISTS Medications (name TEXT, pillsize INTEGER, numpills INTEGER, hour INTEGER," +
                "minute INTEGER, am INTEGER)";

        String q2 = "Select * from Medications";

       // db.execSQL(q1);

        db.execSQL(query);

        Cursor c = db.rawQuery(q2, null);

        if(c.getCount() > 0){
            Log.v("Tag", "Shouldn't make it here");
            for(int i = 0; i < c.getCount(); i++){
                c.moveToPosition(i);
                String name = c.getString(0);
                int pillsize = c.getInt(1);
                int numpills = c.getInt(2);
                int hour = c.getInt(3);
                int minute = c.getInt(4);
                int am_int = c.getInt(5);
                boolean am = true;
                if(am_int==1){
                    am = false;
                }

                Medication m = new Medication(name, pillsize, numpills, hour, minute, am);
                medications.add(m);

            }
            adapter.notifyDataSetChanged();

        }



        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        l = sm.getSensorList(Sensor.TYPE_ALL);
        s = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //samples every twentieth of a second

        sm.registerListener(this, s, 1000000/50);
        free_fall = false;
        stop = false;


    }



    protected void addItems(View v){

        Log.v("Tag", "Made it here");

        Intent x = new Intent(this, NewMedication.class);
        startActivity(x);


    }

    protected void requestHelp(View v) {
        //https://stackoverflow.com/questions/3042752/speech-recognition-in-android
        //used similar code to this to set up the speech recognition.

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak out loud to tell the staff what you want");
        try {
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException a) {

        }
    }


        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            //https://stackoverflow.com/questions/3042752/speech-recognition-in-android
            //similarly, based my code on this example ^

            switch (requestCode) {
                case 100: {
                    if (resultCode == RESULT_OK && null != data) {

                        ArrayList<String> result = data
                                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        voiceInput.setText(result.get(0));
                        sendRequest.setText("Send request to staff?");
                        sendRequest.setBackgroundColor(Color.BLUE);
                    }
                    break;
                }

            }
        }

        public void sendRequest(View v){
            if(sendRequest.getText().toString() == ""){
                //this should return if the person accidentally submits the text. This should only be active if
                //text is available.
                return;
            }else{
                String text = voiceInput.getText().toString(); //it's orignally a char array
//

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"schmidt5@live.unc.edu"});
                i.putExtra(Intent.EXTRA_SUBJECT, text);
                i.putExtra(Intent.EXTRA_TEXT   , text);
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                 //   Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }

            }
        }

        public void signalEmergency(View v){
            Intent phoneIntent = new Intent(Intent.ACTION_CALL);
            phoneIntent.setData(Uri.parse("tel:1-630-706-1425"));
            //check to see if you have the applicable permissions
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivity(phoneIntent);


        }

        public void geofenceButton(View v){
            Intent x = new Intent(this, GeofenceAct.class);
            startActivity(x);

        }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        float gx = sensorEvent.values[0];
        float gy = sensorEvent.values[1];
        float gz = sensorEvent.values[2];

        float total_val = (float) Math.sqrt(gx*gx + gy*gy + gz*gz);

        sens_val1 = sens_val2;
        sens_val2 = sens_val3;
        sens_val3 = sens_val4;
        sens_val4 = total_val;

        float moving_avg = (sens_val1 + sens_val2 + sens_val3 + sens_val4)/4;
        float stop_avg = (sens_val3 + sens_val4)/2;
        if(moving_avg < 2.0){
            Log.v("TAG", "Free fall detected");
            free_fall = true;
        }

        if(stop_avg > 25.0 && free_fall){
            Log.v("TAG", "Fall, then stop detected");
            free_fall = false;
            Intent x = new Intent(this, FallDetected.class);
            startActivity(x);
        }



        String value = Float.toString(total_val);
        Log.v("Val", value);


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}



