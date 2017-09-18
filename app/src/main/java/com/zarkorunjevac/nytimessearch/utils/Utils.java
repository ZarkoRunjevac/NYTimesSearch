package com.zarkorunjevac.nytimessearch.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by zarko.runjevac on 9/18/2017.
 */

public class Utils {
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy",
      Locale.getDefault());

  public static Calendar dateFromString(String dateString){
    Calendar date = Calendar.getInstance();
    try {
      date.setTime(DATE_FORMAT.parse(dateString));
    }catch (Exception e){
      e.printStackTrace();
    }

    return date;
  }

  public static String stringFromDate(Date date){
    return DATE_FORMAT.format(date);
  }


}
