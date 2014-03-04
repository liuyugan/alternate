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
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private Date _currentDateTime;
	private Calendar _chinaCalendar;
	private TextView _displayTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//TODO:Remove below control
		//Set numbers Array visible false.
		Spinner numbersArray = (Spinner)findViewById(R.id.spinnerNumbers);
		numbersArray.setVisibility(View.INVISIBLE);
		
		//Get Display result control.
		_displayTextView = (TextView) findViewById(R.id.txtResult);
		
		//TODO:Need Check for real devices.
		//Get Chinese Time Zone.
		//TimeZone chinaTimeZone = TimeZone.getTimeZone("GTM+08:00");//Asia/Shanghai
		TimeZone chinaTimeZone = TimeZone.getTimeZone("Asia/Shanghai");
		TimeZone defaultTimeZone = TimeZone.getDefault();
		
		String chinaTemp = chinaTimeZone.getDisplayName(true,TimeZone.LONG);
		String defaultTemp = defaultTimeZone.getDisplayName(true,TimeZone.LONG);
		
		String chTimeZoneId = chinaTimeZone.getID();
		String defaultTimeZoneId = defaultTimeZone.getID();
		//Time zone is not Chinese Beijing.
	
		
		if( defaultTimeZoneId.equalsIgnoreCase(chTimeZoneId) == false)
		{
			_displayTextView.setText(this.getString(R.string.notChineseTimeZone));
			//Button searchBtn = (Button)findViewById(R.id.btnSearch);
		}
		
		
		_chinaCalendar = Calendar.getInstance(chinaTimeZone);		
		//If today is Sunday or Saturday,then return.  		
		_currentDateTime = getCurrentTimeByChinaTimeZone(_chinaCalendar);
	}

	private Date getCurrentTimeByChinaTimeZone(Calendar chinaCalendar) {
		Date currentDate = new Date();
	    currentDate = chinaCalendar.getTime();
	    return currentDate;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void getResulte(View view) throws XmlPullParserException, IOException, ParseException
    {
    	Date currentDate = getCurrentTimeByChinaTimeZone(_chinaCalendar);
        
    	//Get current date by select datepicker
        DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker1);
        currentDate.setDate(datePicker.getDayOfMonth());
        currentDate.setMonth(datePicker.getMonth());
        //datePicker get year from 1900 year.
        currentDate.setYear(datePicker.getYear() - 1900);
        
               
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
                  			_chinaCalendar.setTime(currentDate);
                  			//Sunday is 1,Monday is 2,... Saturday is 7 
                  			int currentDayofWeek = _chinaCalendar.get(Calendar.DAY_OF_WEEK);
                  			currentDayofWeek--;
                  			
                  			if( currentDayofWeek == 0 || currentDayofWeek == 6 )
                  			{
                  				_displayTextView.setText(this.getString(R.string.notlimitedOn67) );
                  				break;
                  			}
                   		 
                  			if( dayofWeek.equalsIgnoreCase(String.valueOf(currentDayofWeek) ))
                  			{
                  				 //Goto Next xmlnode : <Number>
                  				 xpp.next();
	                             eventType = xpp.getEventType();
	                             xmlNodeName = xpp.getName();
                  				
                  				String numbers = xpp.nextText();
                  				                  				
                  				if(numbers!=null)
                  				{
                  					_displayTextView.setText(this.getString(R.string.limited)+":"+numbers );
                  					break;
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
        
       }
        
   
    public boolean hasGetDate(Date from,Date to,Date date)
    {
    	if( date.compareTo(from)>=0 && date.compareTo(to) <=0)
    		return true;
    	
    	return false;
    }

}
