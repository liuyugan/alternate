package com.example.alternate;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.os.Bundle;
import android.app.Activity;
import android.appwidget.AppWidgetProvider;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.view.Menu;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void getResulte(View view) throws XmlPullParserException, IOException, ParseException
    {
    	//Find Result TextView
    	TextView displayTextView = (TextView) findViewById(R.id.txtResult);
                
        //Get your input
        Spinner numbersArray = (Spinner)findViewById(R.id.spinnerNumbers);
        String vehicleNumber = numbersArray.getSelectedItem().toString();
        
        //Get Current System Date.
        Date currentDate = new Date();
        TimeZone chinaTimeZone = TimeZone.getTimeZone("GTM+08:00");
        Calendar chinaCalendar = Calendar.getInstance(chinaTimeZone);
        currentDate = chinaCalendar.getTime();
         
        DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker1);
        currentDate.setDate(datePicker.getDayOfMonth());
        currentDate.setMonth(datePicker.getMonth());
        //datePicker get year from 1900 year.
        currentDate.setYear(datePicker.getYear() - 1900);
        
        //If Sunday or Saturday, then all car can be drive.
        chinaCalendar.setTime(currentDate);
		//Sunday is 1,Monday is 2,... Saturday is 7 
		int currentDayofWeek = chinaCalendar.get(Calendar.DAY_OF_WEEK);
		if( currentDayofWeek == 1 || currentDayofWeek == 7 )
		{
			setResult(displayTextView, false);
			return;
		}
        
        //If current date is working day.
        boolean foundYourVehivleNumberinXml = false;
        
        //Read Xml file
        Resources res = this.getResources();
        XmlResourceParser xpp = res.getXml(R.xml.numbers);
        xpp.next();
        
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT)
        {
        	String xmlNodeName = xpp.getName();
        	if( xmlNodeName != null) 
        	{
        		if( xmlNodeName.equalsIgnoreCase("TimeDuration") && eventType == XmlPullParser.START_TAG )
                {
        		   String fromDateStr = xpp.getAttributeValue(0);  
             	   String toDateStr = xpp.getAttributeValue(1);        	   
             	   
             	   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");   
             	   
             	   Date from = format.parse(fromDateStr);
             	   Date to = format.parse(toDateStr);
             	   to.setHours(23);
             	   to.setMinutes(59);
             	   to.setSeconds(59);
             	   
             	   boolean findDate = hasGetDate(from,to,currentDate);
             	   if( findDate )
             	   {
             		  while (! (eventType == XmlPullParser.END_TAG &&
             				 xmlNodeName.equalsIgnoreCase("TimeDuration") &&
             						 xmlNodeName != null))
             		  {
             			  
             		   //get xmlNode name: "WeekDay"
               		   xmlNodeName = xpp.getName();
               		   if( xmlNodeName != null && xmlNodeName.equalsIgnoreCase("WeekDay") && eventType == XmlPullParser.START_TAG )
               		   {
               			 int attributeCount = xpp.getAttributeCount();
               			 if( attributeCount > 0 )
               			 {
               				String dayofWeek = xpp.getAttributeValue(0);//Get Attribute "Day" value.
                  			//Sunday is 1,Monday is 2,... Saturday is 7 
               				currentDayofWeek--;
                   		 
                  			if( dayofWeek.equalsIgnoreCase(String.valueOf(currentDayofWeek) ))
                  			{
                  				 //Goto Next xmlnode : <Number>
                  				 xpp.next();
	                             eventType = xpp.getEventType();
	                             xmlNodeName = xpp.getName();
                  				
                  				String numbers = xpp.nextText();
                  				if(numbers!=null && numbers.contains(vehicleNumber) )
                  				{
                  					foundYourVehivleNumberinXml = true;
                  				     	break;//if found.
                  				}
                  				else
                  				{
                  				   //Skip to next node in "TimeDuration"
                  				   xpp.next();
                                   eventType = xpp.getEventType();
                                   xmlNodeName = xpp.getName();
                  				}
                  			}
	                   		 else
	                   		 {
	                   			//Skip to next node in "TimeDuration"
	                    			xpp.next();
	                                eventType = xpp.getEventType();
	                                xmlNodeName = xpp.getName();
	                   		 }
               			 }
               			 else
               			 {
               				 //Skip to next node in "TimeDuration"
               				 xpp.next();
               				 eventType = xpp.getEventType();
               				 xmlNodeName = xpp.getName();
               			 }
                		 
               		   }
               		   else
               		   {
               			   //Skip to next node in "TimeDuration"
               			   xpp.next();
               			   eventType = xpp.getEventType();
               			   xmlNodeName = xpp.getName();
               		   }
               		    
             		  }
             	   }
             	   else
             	   {
             		//Skip to next loop.
                   	xpp.next();
                   	eventType = xpp.getEventType();
             	   }
                }
        	}
        	//Skip to next loop.
        	xpp.next();
        	eventType = xpp.getEventType();
        }
               
        setResult(displayTextView, foundYourVehivleNumberinXml);
        
       }

	//Set Search result.
	private void setResult(TextView displayTextView,
			boolean foundYourVehivleNumberinXml) {
		if(foundYourVehivleNumberinXml)
        {
        	displayTextView.setText(this.getString(R.string.limited) );
        }
        else
        {
        	displayTextView.setText(this.getString(R.string.notlimited) );
        }
	}
        
   
    public static boolean hasGetDate(Date from,Date to,Date date)
    {
    	if( date.compareTo(from)>=0 && date.compareTo(to) <=0)
    		return true;
    	
    	return false;
    }

}

 
