package com.o2hlink.activa.background;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;

import com.o2hlink.activa.Activa;
import com.o2hlink.activa.ActivaConfig;
import com.o2hlink.activa.R;
import com.o2hlink.activa.data.PendingDataManager;
import com.o2hlink.activa.data.calendar.Event;

import android.graphics.drawable.AnimationDrawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class InitialConnection implements Runnable {

	int currentImage = 0;
	int nextImage = 0;
	RelativeLayout popupView;
	TextView popupText;
	AnimationDrawable animation;
	
	public InitialConnection() {
		popupView = (RelativeLayout) Activa.myApp.findViewById(R.id.popupView);
		popupView.setVisibility(View.VISIBLE);
		popupText = (TextView) Activa.myApp.findViewById(R.id.popupText);
		ImageView animationFrame = (ImageView) Activa.myApp.findViewById(R.id.popupImage);
		animationFrame.setVisibility(View.VISIBLE);
		animationFrame.setBackgroundResource(R.drawable.loading);
		this.animation = (AnimationDrawable) animationFrame.getBackground();
	}

	@Override
	public void run() {	
		Activa.myApp.refreshing_foreground = true;
		animation.start();
		handler.sendEmptyMessage(100);
		boolean success = false;
		if (!Activa.myMobileManager.user.isCreated()) success = Activa.myProtocolManager.register();
		else success = Activa.myProtocolManager.login();
		if (success) {
			if (!Activa.myMobileManager.user.isCreated()) {
				Activa.myMobileManager.user.setCreated(true);
				Activa.myMobileManager.addUserWithPassword(Activa.myMobileManager.user);
			}
		    /* Send pending data */
	        Activa.myPendingDataManager = PendingDataManager.getInstance();
			if (!Activa.myPendingDataManager.pendingData.isEmpty()) handler.sendEmptyMessage(8);
		    ArrayList<String> tempList = (ArrayList<String>) Activa.myPendingDataManager.pendingData.clone();
		    Activa.myPendingDataManager.pendingData = new ArrayList<String>();
		    Iterator<String> it = tempList.iterator();
		    while (it.hasNext()) {
		    	String pendData = it.next();
		    	Activa.myProtocolManager.sendPendingData(pendData);
		    }
			handler.sendEmptyMessage(0);
			Activa.myQuestManager.getQuestionnaires();
			Activa.myQuestControlManager.getQuestionnaires();
		    handler.sendEmptyMessage(15);
		    Activa.myPatientManager.getPatientList();
			handler.sendEmptyMessage(1);
		    Activa.myCalendarManager.getCalendar();
			handler.sendEmptyMessage(5);
		    Activa.myNewsManager.getNews();
		    handler.sendEmptyMessage(55);
		    Activa.myMessageManager.requestContactList();
			handler.sendEmptyMessage(6);
			Date now = new Date();
			now.setDate(now.getDate() - 15);
		    Activa.myMessageManager.requestReceivedMessages(now);
			handler.sendEmptyMessage(66);
		    Activa.myNotesManager.getNotes();
//			handler.sendEmptyMessage(7);
//			/* Update event status and send event outcomes for canceled events */
//		    Enumeration<Event> enumeration = Activa.myCalendarManager.events.elements();
//		    while (enumeration.hasMoreElements()) {
//		    	Event event = enumeration.nextElement();
//		    	event.updateState();
//		    }
			handler.sendEmptyMessage(2);
			Activa.myApp.refreshing_foreground = false;
		}
		else {
	        Activa.myPendingDataManager = PendingDataManager.getInstance();
			Activa.myQuestManager.getQuestionnaires();
			handler.sendEmptyMessage(3);
		}
        Activa.mySensorManager.getSensorDBFromFile();
        if (Activa.myMainBackgroundThread.running) Activa.myMainBackgroundThread.stop();
		Thread thread = new Thread(Activa.myMainBackgroundThread, "MAIN");
		thread.start();
		handler.sendEmptyMessage(4);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0:
					popupText.setText(Activa.myLanguageManager.CONNECTION_QUESTIONNAIRES);
					break;
				case 100:
					if (!Activa.myMobileManager.user.isCreated()) popupText.setText(Activa.myLanguageManager.CONNECTION_REGISTERING);
					else popupText.setText(Activa.myLanguageManager.CONNECTION_CONNECTING);
					break;
				case 1:
					popupText.setText(Activa.myLanguageManager.CONNECTION_CALENDAR);
					break;
				case 15:
					popupText.setText(Activa.myLanguageManager.CONNECTION_PATIENTS);
					break;
				case 5:
					popupText.setText(Activa.myLanguageManager.CONNECTION_FEEDS);
					break;
				case 55:
					popupText.setText(Activa.myLanguageManager.CONNECTION_CONTACTS);
					break;
				case 6:
					popupText.setText(Activa.myLanguageManager.CONNECTION_MESSAGES);
					break;
				case 66:
					popupText.setText(Activa.myLanguageManager.CONNECTION_NOTES);
					break;
				case 7:
					popupText.setText(Activa.myLanguageManager.CONNECTION_UPDATING);
					break;
				case 8:
					popupText.setText(Activa.myLanguageManager.CONNECTION_PENDING);
					break;
				case 2:
					((ImageView) Activa.myApp.findViewById(R.id.popupImage)).setVisibility(View.GONE);
					animation.stop();
			    	popupText.setText(Activa.myLanguageManager.CONNECTION_CONNECTED1 + Activa.myMobileManager.user.getName() + Activa.myLanguageManager.CONNECTION_CONNECTED2);
					break;
				case 3:
					((ImageView) Activa.myApp.findViewById(R.id.popupImage)).setVisibility(View.GONE);
					animation.stop();
					popupText.setText(Activa.myLanguageManager.CONNECTION_FAILED);
					break;
				case 4:
					BeginTimeTask biniter = new BeginTimeTask(3000, 1000);
					biniter.start();
					break;
			}
		}

	};

}

class BeginTimeTask extends CountDownTimer {
	
	public BeginTimeTask(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
	}

	long startTime;

	@Override
	public void onFinish() {
		RelativeLayout popupView = (RelativeLayout) Activa.myApp.findViewById(R.id.popupView);
		if (popupView != null) popupView.setVisibility(View.INVISIBLE);
		ImageButton refresh = (ImageButton)Activa.myApp.findViewById(R.id.miscbutton);
		if (refresh != null) refresh.setVisibility(View.VISIBLE);
		Activa.myApp.refreshing_foreground = false;
	}

	@Override
	public void onTick(long millisUntilFinished) {
	}
}
