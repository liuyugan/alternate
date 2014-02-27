package com.example.alternate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.xmlpull.v1.XmlPullParser;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.widget.DatePicker;
import android.widget.RemoteViews;


public class HelloWidgetProvider extends AppWidgetProvider{
	
	@Override
	public void onReceive(Context context, Intent intent) {
	      super.onReceive(context, intent);
	      RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.myappwidget);
	        remoteView.setTextViewText(R.id.textView1, "onReceive");
	
	    }
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
	        super.onUpdate(context, appWidgetManager, appWidgetIds);
	        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.myappwidget);
	        remoteView.setTextViewText(R.id.textView1, "onUpdate");
	    }
	
	    @Override
	
	    public void onDeleted(Context context, int[] appWidgetIds) {
	        super.onDeleted(context, appWidgetIds);
	    }
	    
	    @Override
	    public void onEnabled(Context context) {
	        super.onEnabled(context);
	        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.myappwidget);
	        remoteView.setTextViewText(R.id.textView1, "onEnabled");
		}
	    
	    @Override
	    public void onDisabled(Context context) {
	        super.onDisabled(context);
	    }
	    
	    


}
