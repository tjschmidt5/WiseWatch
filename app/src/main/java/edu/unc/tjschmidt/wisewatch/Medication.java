package edu.unc.tjschmidt.wisewatch;

import android.app.AlarmManager;
import android.content.Context;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Timer;

/**
 * Created by tjschmidt on 11/25/17.
 */

public class Medication {

    //this class represents a Medication object. This is the core piece of data used for the medication reminder
    //At this point, it's basically just getters and setters because we'll manipulate this data elsewhere.

    private String name;
    private int pill_size;
    private int number_of_pills;
    //private ArrayList<Time> daily_times;  //These are the daily times they need to take a pill
    private int hour;
    private int minute;
    private boolean am; //true means that this time is AM/PM

    public Medication (String name, int pill_size, int num_pills, int hour, int minute, boolean am){
        this.name = name;
        this.pill_size = pill_size;
        this.number_of_pills = num_pills;
        this.hour = hour;
        this.minute = minute;
        this.am = am;

    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public double getPillSize(){
        return pill_size;
    }

    public void setPillSize(int size){
        pill_size = size;
    }

    public double getNumberOfPpills(){
        return number_of_pills;
    }

    public void setNumberOfPills(int number){
        number_of_pills = number;
    }

    public int getHour(){
        return this.hour;
    }
    public int getMinute(){
        return this.minute;
    }

    public boolean is_am(){ //true means that this is an AM time
        return am;
    }

    public void setHour(int hour){
        this.hour = hour;
    }

    public void setMinute(int minute){
        this.minute = minute;
    }

    public void setAm(boolean am){
        this.am = am;
    }


    //The toString method is important because it will give us a printed list of our medications.
    @Override
    public String toString(){
        int am_pm_hour = 0;
        String am_pm = "AM";
        if(!am){
            am_pm = "PM";
        }
        if(hour > 12){
            am_pm_hour = hour - 12;
        }
        else if(hour == 0){
            am_pm_hour = 12;

        }else{
            am_pm_hour = hour;
        }
        String f_minute = Integer.toString(minute);

        switch(minute){
            case 0: f_minute = "00";
                break;
            case 1: f_minute = "01";
                break;
            case 2: f_minute = "02";
                break;
            case 3: f_minute = "03";
                break;
            case 4: f_minute = "04";
                break;
            case 5: f_minute = "05";
                break;
            case 6: f_minute = "06";
                break;
            case 7: f_minute = "07";
                break;
            case 8: f_minute = "08";
                break;
            case 9: f_minute = "09";
                break;
            default: f_minute = Integer.toString(minute);
        }
    //    String string = "Name: " + this.name + " Pill size: " + Integer.toString(pill_size) + " Number of pills per dose:" +
      //          Integer.toString(number_of_pills) + "/nTaken at: " + Integer.toString(am_pm_hour) + ":" + Integer.toString(minute) +
        //        " " + am_pm;

        String string = this.name + " Taken at " + Integer.toString(am_pm_hour) + ":" + f_minute + " " + am_pm;

        return string;

    }






}
