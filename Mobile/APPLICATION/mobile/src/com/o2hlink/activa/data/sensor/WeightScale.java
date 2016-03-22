package com.o2hlink.activa.data.sensor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;
import java.util.UUID;

import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.BadTokenException;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.o2hlink.activ8.client.entity.Sample;
import com.o2hlink.activa.Activa;
import com.o2hlink.activa.ActivaUtil;
import com.o2hlink.activa.R;
import com.o2hlink.activa.background.MainThread;
import com.o2hlink.activa.background.SendSensorResult;
import com.o2hlink.activa.data.PendingDataManager;
import com.o2hlink.activa.ui.UIManager;

public class WeightScale extends Sensor {
	
	public Exercise exercise;

	public WeightScale() {
		this.name = Activa.myLanguageManager.SENSORS_WEIGHT_TITLE;
		this.icon = R.drawable.weight;
		this.id = SensorManager.ID_WEIGHTSCALE;
	}
	
	@Override
	public Sample getSample() {
		return null;
	}
	
	@Override
	public void finishMeasurements(boolean outcome, Hashtable<Integer, Float> results) {
		this.thread.interrupt();
		if (this.bluetoothPreviouslyConnected) Activa.mySensorManager.reinitBluetooth();
		else Activa.myBluetoothAdapter.disable();
		this.results = results;
		if ((Activa.mySensorManager.eventAssociated != null)&&outcome)  {
			Activa.mySensorManager.eventAssociated.state = 0;
			Activa.myCalendarManager.saveCalendar();
		}Activa.myUIManager.finishSensorMeasurement(this.name, outcome, this);
	}
	
//	@Override
//	public void finishMeasurements(boolean outcome, Hashtable<Integer, Float> results) {
//		Activa.myApp.finishActivity(SensorManager.ID_PULSIOXYMETER);
//		this.results = results;
//		if (Activa.mySensorManager.eventAssociated != null) Activa.mySensorManager.eventAssociated.state = 0;
//		Activa.myUIManager.finishSensorMeasurement(this.name, outcome, this);
//	}

//	public void finishExercise(boolean outcome, Hashtable<Integer, Float> results) {
//		this.thread.interrupt();
//		this.results = results;
//		if (Activa.myProgramManager.eventAssociated != null) Activa.myProgramManager.eventAssociated.state = 0;
//		Activa.myUIManager.finishExercise(outcome, this);		
//	}

	@Override
	public void startMeasurement() {
		this.results = new Hashtable<Integer, Float>();
		Activa.myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		StartMeasurementWeightScale st = new StartMeasurementWeightScale(this);
		Thread thr = new Thread(st);
		this.thread = thr;
		thr.start();
	}

//	@Override
//	public void startMeasurement() {
//		this.results = new Hashtable<Integer, Float>();
//		Activa.myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//		Activa.myApp.startActivityForResult(new Intent(Activa.myApp, com.o2hlink.activa.data.sensor.PulseOximeterActivity.class), SensorManager.ID_PULSIOXYMETER);
//	}
	
//	public void startExercise(long time, Hashtable<Long,String> timedMessages) {
//		this.results = new Hashtable<Integer, Float>();
//		Activa.myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//		this.exercise = new Exercise(this, time, timedMessages);
//		this.thread = new Thread(this.exercise);
//		this.thread.start();
//	}

	@Override
	public String passSensorResultToXML() {
		String returned = "<EVENT ID=\"1\" DATETIME=\"" + Activa.myMobileManager.device.getDateTime();
		returned += "\" IDGROUP=\"\" IDAGENDA=\"";
		if (Activa.mySensorManager.eventAssociated != null) returned += Activa.mySensorManager.eventAssociated.id + "\">";
		else returned += "\">";
		Enumeration<Integer> keys = this.results.keys();
		while (keys.hasMoreElements()) {
			int key = keys.nextElement();
			returned += "<DATA ID=\"" + key + "\" VALUE=\"" + this.results.get(key);
			returned += "\" UNITS=\"" + SensorManager.getUnitIDForMeasurementID(key) + "\"/>";
		}
		returned += "</EVENT>";
		return returned;
	}

	@Override
	public String getSensorGlobalResult() {
		String result;
		Float systolic = this.results.get(SensorManager.DATAID_SYSPRESS);
		Float diastolic = this.results.get(SensorManager.DATAID_DIAPRESS);
		
		if ((systolic < 50.0)&&(diastolic < 80.0)) {
			result = "0:" + Activa.myLanguageManager.SENSORS_BLOODPRESS_MESSAGE7;
		}
		else if ((systolic < 60.0)&&(diastolic < 90.0)) {
			result = "2:" + Activa.myLanguageManager.SENSORS_BLOODPRESS_MESSAGE0;
		}
		else if ((systolic < 80.0)&&(diastolic < 120.0)) {
			result = "2:" + Activa.myLanguageManager.SENSORS_BLOODPRESS_MESSAGE1;
		}
		else if ((systolic < 85.0)&&(diastolic < 130.0)) {
			result = "2:" + Activa.myLanguageManager.SENSORS_BLOODPRESS_MESSAGE2;
		}
		else if ((systolic < 90.0)&&(diastolic < 140.0)) {
			result = "1:" + Activa.myLanguageManager.SENSORS_BLOODPRESS_MESSAGE3;
		}
		else if ((systolic < 100.0)&&(diastolic < 160.0)) {
			result = "1:" + Activa.myLanguageManager.SENSORS_BLOODPRESS_MESSAGE4;
		}
		else if ((systolic < 110.0)&&(diastolic < 180.0)) {
			result = "0:" + Activa.myLanguageManager.SENSORS_BLOODPRESS_MESSAGE5;
		}
		else {
			result = "0:" + Activa.myLanguageManager.SENSORS_BLOODPRESS_MESSAGE6;
		}
		return result;
	}

}

class StartMeasurementWeightScale implements Runnable {

    private static final int REQUEST_ENABLE_BT = 2;

	private static final int MEASURE_WAIT_TIME = 15;
	
	private WeightScale weightscale;
	
	BroadcastReceiver myReceiver;
	
	BluetoothServerSocket serversocket;
	
	BluetoothSocket socket;
	
	InputStream in;
	
	OutputStream out;

	boolean finished;
	
	private final static String MEASURE_VALID_FLAG = "ST,+";
	
	private final static byte[] CMD_REFUSE = { // refuse command
		0x50, // P
		0x57, // W
		0x41, // A
		0x30, // 0
	};

	private final static byte[] CMD_ACCEPT = { // accept command
		0x50, // P
		0x57, // W
		0x41, // A
		0x31, // 1
	};
	
	public StartMeasurementWeightScale(WeightScale weightscale) {
		this.weightscale = weightscale;
	}
	
	@SuppressWarnings("unused")
	private void closeConnection() {
        if (in != null) {
            try {in.close();} catch (Exception e) {}
            in = null;
        }
        if (out != null) {
            try {out.close();} catch (Exception e) {}
            out = null;
        }
        if (socket != null) {
            try {socket.close();} catch (Exception e) {}
            socket = null;
        }	
	}

	private boolean checkHeader (byte[] header) {
		boolean valid = true;
		int packetType = ActivaUtil.byte2ToInt(header,0);
		if (packetType != 2) {  // It's a device packet
			valid = false;
		}
		int packetLenght = ActivaUtil.byte4ToInt(header,2);
		if (packetLenght <= 0) {  // There aren't body
			valid = false;
		}
		int deviceType = ActivaUtil.byte2ToInt(header,6);
		if (deviceType != 321) {  // It's a weight scale measurement
			valid = false;
		}
		return valid;
	}

	private boolean checkBody (byte[] body) {
		boolean valid = true;
		String packetValidation = new String(body, 0, 2);
		if (packetValidation.equals(MEASURE_VALID_FLAG)) {  // It's well formed
			valid = false;
		}
		return valid;
	}
	
	@Override
	public void run() {		
		finished = false;
		handler.sendEmptyMessage(0);
		if (Activa.myBluetoothAdapter == null) {
			handler.sendEmptyMessage(3);
			return;
		}
		if (Activa.mySensorManager.reiniting) {
			handler.sendEmptyMessage(31);
			while (Activa.mySensorManager.reiniting) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			handler.sendEmptyMessage(1);
		}
		else if (!Activa.myBluetoothAdapter.isEnabled()) {
			Activa.myBluetoothAdapter.enable();
			this.weightscale.bluetoothPreviouslyConnected = false;
			handler.sendEmptyMessage(4);
			while (!Activa.myBluetoothAdapter.isEnabled()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			handler.sendEmptyMessage(1);
		}
		else {
			this.weightscale.bluetoothPreviouslyConnected = true;
		}
		try {
			Intent discoverableIntent = new
			Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			Activa.myApp.startActivity(discoverableIntent);
			serversocket = Activa.myBluetoothAdapter.listenUsingRfcommWithServiceRecord("PWAccessP", UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
			socket = serversocket.accept(300000);
		    in = socket.getInputStream();
		    out = socket.getOutputStream();
			serversocket.close();
			handler.sendEmptyMessage(6);
		    byte [] header = new byte [60];
		    in.read(header);
		    if (!checkHeader(header)) {
		    	out.write(CMD_REFUSE);
		    	throw new OperationCanceledException();
		    }
		    byte [] body = new byte [10];
		    in.read(body);
		    if (!checkBody(body)) {
		    	out.write(CMD_REFUSE);
		    	throw new BadTokenException();
		    }
		    try {
				float weight = Float.parseFloat(new String(body, 4, 6));
				String unit = new String(body, 10, 2);
				if (unit.equals("lb")) {
					weight = weight*0.453592f;
				} else if (!unit.equals("kg")) {
			    	out.write(CMD_REFUSE);
			    	throw new BadTokenException();
				}
				weightscale.results.put(SensorManager.DATAID_WEIGHT, weight);
		    } catch (NumberFormatException e) {
		    	out.write(CMD_REFUSE);
		    	throw new BadTokenException();
			}
		    out.write(CMD_ACCEPT);
		    handler.sendEmptyMessage(2);
		    closeConnection();
		} catch (IOException e) {
			handler.sendEmptyMessage(7);
			closeConnection();
			e.printStackTrace();
		} catch (OperationCanceledException e) {
			handler.sendEmptyMessage(8);
			closeConnection();
			e.printStackTrace();
		} catch (BadTokenException e) {
			handler.sendEmptyMessage(9);
			closeConnection();
			e.printStackTrace();
		}    
	}

	private Handler handler = new Handler() {
		AnimationDrawable animation;
		ImageView animationFrame;
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				animationFrame = (ImageView) Activa.myApp.findViewById(R.id.animation);
				animationFrame.setVisibility(View.VISIBLE);
				animationFrame.setBackgroundResource(R.drawable.loading);
				animation = (AnimationDrawable) animationFrame.getBackground();	
				animation.start();			
				Activa.myUIManager.loadInfoPopupWithoutExiting(Activa.myLanguageManager.SENSORS_BLUETOOTH_CONFIG);
				break;
			case 1:
				animation.stop();	
				Activa.myUIManager.state = UIManager.UI_STATE_SENSORING;
				Activa.myUIManager.loadScreen(R.layout.waiting);
				TextView start = (TextView) Activa.myApp.findViewById(R.id.startText);
				TextView text = (TextView) Activa.myApp.findViewById(R.id.infoText);
				start.setText(Activa.myLanguageManager.SENSORS_BLOODPRESS_TITLE);
				text.setText(Activa.myLanguageManager.SENSORS_WAITING);
				animationFrame = (ImageView) Activa.myApp.findViewById(R.id.progress);
				animationFrame.setVisibility(View.VISIBLE);
				animationFrame.setBackgroundResource(R.drawable.loadingbig);
				animation = (AnimationDrawable) animationFrame.getBackground();	
				animation.start();
				ImageButton cancel = (ImageButton) Activa.myApp.findViewById(R.id.stop);
				cancel.setOnClickListener(new OnClickListener() {	
					@Override
					public void onClick(View v) {
						finished = true;
						weightscale.finishMeasurements(false, null);
					}
				});
				break;
			case 2:
				weightscale.finishMeasurements(true, weightscale.results); 
				break;
			case 3:
				animation.stop();
				Activa.myUIManager.loadInfoPopup(Activa.myLanguageManager.SENSORS_NOBLUETOOTH);
				break;
			case 4:
				Activa.myUIManager.loadInfoPopupWithoutExiting(Activa.myLanguageManager.SENSORS_BLUETOOTH_NOTCONNECTED);
				break;
			case 5:
				Activa.myUIManager.loadInfoPopupWithoutExiting(Activa.myLanguageManager.SENSORS_BLUETOOTH_CONNECTING);
				break;
			case 6:
				TextView text3 = (TextView) Activa.myApp.findViewById(R.id.infoText);
				text3.setText(Activa.myLanguageManager.SENSORS_READING);
				break;
			case 7:
				if ((Activa.mySensorManager.eventAssociated != null)||(Activa.myProgramManager.eventAssociated != null)) 
					Activa.myUIManager.loadScheduleDay(Activa.myProgramManager.eventAssociated.getDate()); 
				else if (Activa.mySensorManager.programassociated != null) 
					Activa.myUIManager.loadProgramList();
				else
					Activa.myUIManager.loadSensorList();
				if (weightscale.bluetoothPreviouslyConnected) Activa.mySensorManager.reinitBluetooth();
				else Activa.myBluetoothAdapter.disable();
				Activa.myUIManager.loadInfoPopup(Activa.myLanguageManager.SENSORS_BLUETOOTH_NOTCONNECTION);
				break;
			case 8:
				if ((Activa.mySensorManager.eventAssociated != null)||(Activa.myProgramManager.eventAssociated != null)) 
					Activa.myUIManager.loadScheduleDay(Activa.myProgramManager.eventAssociated.getDate()); 
				else if (Activa.mySensorManager.programassociated != null) 
					Activa.myUIManager.loadProgramList();
				else
					Activa.myUIManager.loadSensorList();
				if (weightscale.bluetoothPreviouslyConnected) Activa.mySensorManager.reinitBluetooth();
				else Activa.myBluetoothAdapter.disable();
				Activa.myUIManager.loadInfoPopup(Activa.myLanguageManager.SENSORS_BLUETOOTH_NOTCONFIG);
				break;
			case 9:
				if ((Activa.mySensorManager.eventAssociated != null)||(Activa.myProgramManager.eventAssociated != null)) 
					Activa.myUIManager.loadScheduleDay(Activa.myProgramManager.eventAssociated.getDate()); 
				else if (Activa.mySensorManager.programassociated != null) 
					Activa.myUIManager.loadProgramList();
				else
					Activa.myUIManager.loadSensorList();
				if (weightscale.bluetoothPreviouslyConnected) Activa.mySensorManager.reinitBluetooth();
				else Activa.myBluetoothAdapter.disable();
				Activa.myUIManager.loadInfoPopup(Activa.myLanguageManager.SENSORS_BLUETOOTH_NOTMEASURED);
				break;
			case 31:
				Activa.myUIManager.loadInfoPopupWithoutExiting(Activa.myLanguageManager.SENSORS_CONFIG);
				break;
			}
		}
	};
	
}