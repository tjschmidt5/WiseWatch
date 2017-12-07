package edu.unc.tjschmidt.wisewatch;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by tjschmidt on 11/25/17.
 */

public class NewMedication extends AppCompatActivity{

    private NumberPicker pill_size;
    private NumberPicker num_pills;
    private ListView list;
    private ArrayList<Medication> medications;
    private ArrayAdapter<Medication> adapter;

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    private SQLiteDatabase db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_medication);
        pill_size = (NumberPicker) findViewById(R.id.pill_size);
        pill_size.setMaxValue(300);
        pill_size.setMinValue(0);

        num_pills = (NumberPicker) findViewById(R.id.num_pills);
        num_pills.setMaxValue(10);
        num_pills.setMinValue(1);

        alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(this, AlarmReceiver.class);
//        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        //This allows us to access that List object
        list = (ListView)findViewById(R.id.medlist);
        medications = new ArrayList<Medication>();
//        Medication m = new Medication("poo", 2, 3, 4, 4, true);
//        medications.add(m);

        //the adapter allows us to edit the listView
        //The simple spinner item thing might not work.
      //  adapter = new ArrayAdapter<Medication>(getApplicationContext(), android.R.layout.simple_list_item_1, medications);

     //   list.setAdapter(adapter);



    }

    public void saveMedication(View v){

        EditText e = (EditText)findViewById(R.id.med_name);
        String name = e.getText().toString();

        NumberPicker np1 = (NumberPicker)findViewById(R.id.pill_size);
        int pill_size = np1.getValue();

        NumberPicker np2 = (NumberPicker)findViewById(R.id.num_pills);
        int num_pills = np2.getValue();

        TimePicker tp = (TimePicker)findViewById(R.id.time);
        int hour = tp.getCurrentHour();
        int minute = tp.getCurrentMinute(); //deprecated methods
        boolean am = true;
        if(hour >= 12) am = false; //if the hour is greater/equal than 12, it's the afternoon

        Medication m = new Medication(name, pill_size, num_pills, hour, minute, am);
        //This sets
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("Name", name);
        intent.putExtra("Pill Size", pill_size);
        intent.putExtra("Number of Pills", num_pills);
        intent.putExtra("Hour", hour);
        intent.putExtra("Minute", minute);
        intent.putExtra("AM or PM", am);

        //used alarm manager docs to set this up

        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        //now we can go ahead and set up the alarm manager.
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000*60*60*24, alarmIntent);

        //1000*60*60*24 that's the right amount of time
        //Now we want to store this in a database. Medication main should be able to access this db.

        db = openOrCreateDatabase("MyDatabase", Context.MODE_PRIVATE, null);

        String ps_string = Integer.toString(pill_size);
        String np_string = Integer.toString(num_pills);
        String hour_string = Integer.toString(hour);
        String minute_string = Integer.toString(minute);
        String am_string = "0";
        //This switches us from our boolean representation of AM/PM into a string representation for our db.
        if(!am){
            am_string = "1";
        }

        String query = "INSERT INTO Medications VALUES ('" + name + "', '" + ps_string + "', '" +
                np_string + "', '" + hour_string + "', '" + minute_string + "', '" + am_string + "')";

        db.execSQL(query);

        Intent w = new Intent(this, MedicationMain.class);
        startActivity(w);

    }

    public void addAnotherMedication(View v){
        //this should all be broken up into its own method

        EditText e = (EditText)findViewById(R.id.med_name);
        String name = e.getText().toString();

        NumberPicker np1 = (NumberPicker)findViewById(R.id.pill_size);
        int pill_size = np1.getValue();

        NumberPicker np2 = (NumberPicker)findViewById(R.id.num_pills);
        int num_pills = np2.getValue();

        TimePicker tp = (TimePicker)findViewById(R.id.time);
        int hour = tp.getCurrentHour();
        int minute = tp.getCurrentMinute(); //deprecated methods
        boolean am = true;
        if(hour >= 12) am = false; //if the hour is greater/equal than 12, it's the afternoon

        Medication m = new Medication(name, pill_size, num_pills, hour, minute, am);
        //This sets
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("Name", name);
        intent.putExtra("Pill Size", pill_size);
        intent.putExtra("Number of Pills", num_pills);
        intent.putExtra("Hour", hour);
        intent.putExtra("Minute", minute);
        intent.putExtra("AM or PM", am);

        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        //now we can go ahead and set up the alarm manager.
        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, calendar.getTimeInMillis(), 1000*20, alarmIntent);

        //1000*60*60*24 that's the right amount of time
        //Now we want to store this in a database. Medication main should be able to access this db.

        db = openOrCreateDatabase("MyDatabase", Context.MODE_PRIVATE, null);

        String ps_string = Integer.toString(pill_size);
        String np_string = Integer.toString(num_pills);
        String hour_string = Integer.toString(hour);
        String minute_string = Integer.toString(minute);
        String am_string = "0";
        //This switches us from our boolean representation of AM/PM into a string representation for our db.
        if(!am){
            am_string = "1";
        }

        String query = "INSERT INTO Medications VALUES ('" + name + "', '" + ps_string + "', '" +
                np_string + "', '" + hour_string + "', '" + minute_string + "', '" + am_string + "')";

        db.execSQL(query);

        EditText et = (EditText)findViewById(R.id.med_name);
        et.setText("");

        NumberPicker ps_np = (NumberPicker)findViewById(R.id.pill_size);
        ps_np.setValue(0);

        NumberPicker np_np = (NumberPicker)findViewById(R.id.num_pills);
        np_np.setValue(0);

        TimePicker time = (TimePicker)findViewById(R.id.time);
        time.setCurrentHour(12);
        time.setCurrentMinute(0);



    }



}
