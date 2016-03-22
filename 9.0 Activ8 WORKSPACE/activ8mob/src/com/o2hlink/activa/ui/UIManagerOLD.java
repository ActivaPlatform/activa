package com.o2hlink.activa.ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore.LoadStoreParameter;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TimerTask;
import java.util.Vector;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.opengl.Visibility;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.text.Editable;
import android.text.Html;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TableLayout.LayoutParams;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.o2hlink.activa.ActivaConfig;
import com.o2hlink.activa.ActivaUtil;
import com.o2hlink.activa.R;
import com.o2hlink.activa.Activa;
import com.o2hlink.activa.background.GetNews;
import com.o2hlink.activa.background.InitialConnection;
import com.o2hlink.activa.background.RefreshingConnection;
import com.o2hlink.activa.background.SendNote;
import com.o2hlink.activa.background.SendO2Message;
import com.o2hlink.activa.background.SendQuestionnaire;
import com.o2hlink.activa.background.SendSensorResult;
import com.o2hlink.activa.background.UserCheckout;
import com.o2hlink.activa.data.calendar.Event;
import com.o2hlink.activa.data.message.Contact;
import com.o2hlink.activa.data.message.O2Message;
import com.o2hlink.activa.data.message.O2UnregisteredMessage;
//import com.o2hlink.activa.data.program.Physical;
import com.o2hlink.activa.data.program.Program;
import com.o2hlink.activa.data.program.ProgramManager;
import com.o2hlink.activa.data.questionnaire.Question;
import com.o2hlink.activa.data.questionnaire.Questionnaire;
import com.o2hlink.activa.data.sensor.PulseOximeter;
import com.o2hlink.activa.data.sensor.Sensor;
import com.o2hlink.activa.data.sensor.SensorManager;
import com.o2hlink.activa.data.sensor.Spirometer;
import com.o2hlink.activa.mobile.MobileManager;
import com.o2hlink.activa.mobile.User;
import com.o2hlink.activa.news.Feed;
import com.o2hlink.activa.news.New;
import com.o2hlink.activa.notes.Note;

/**
 * 
 * @author Adrian Rejas<P>
 * 
 * This class will deal with the management of the User Interface. The interfaces will be 
 * designed through XML documents. This class will deal with the charge of these interfaces
 * and the interaction with their elements, ordering to the rest of managers to change 
 * the layout or to do activities depending of these interactions.			
 *
 */
public class UIManagerOLD {
	
	/**
	 * The instance of the manager.
	 */
	private static UIManagerOLD instance;
	
	/**
	 * Different constants for defining the state of the UI.
	 */
	public static final int UI_STATE_LOADING = 0;
	public static final int UI_STATE_LOGIN = 1;
	public static final int UI_STATE_USERINFO = 2;
	public static final int UI_STATE_TOTALQUESTIONNAIRE = 3;
	public static final int UI_STATE_QUESTIONNAIREINIT = 4;
	public static final int UI_STATE_QUESTION = 5;
	public static final int UI_STATE_TOTALSENSOR = 6;
	public static final int UI_STATE_SCHEDULE = 7;
	public static final int UI_STATE_TOTALPROGRAM = 8;
	public static final int UI_STATE_SENSORING = 9;
	public static final int UI_STATE_SENSORINFO = 10;
	public static final int UI_STATE_MEASURING = 11;
	public static final int UI_STATE_MAIN = 12;
	public static final int UI_STATE_MANUALMEAS = 13;
	public static final int UI_STATE_TIMEDMEAS = 14;
	public static final int UI_STATE_PROGRAM = 15;
	public static final int UI_STATE_REGISTER = 16;
	public static final int UI_STATE_SCHEDULEWEEK = 17;
	public static final int UI_STATE_SCHEDULEMONTH = 18;
	public static final int UI_STATE_MAP = 19;
	public static final int UI_STATE_MESSAGE = 20;
	public static final int UI_STATE_MESSAGEWRITING = 21;
	public static final int UI_STATE_MESSAGEREADING = 22;
	public static final int UI_STATE_EXERCISE = 23;
	public static final int UI_STATE_NEWS = 24;
	public static final int UI_STATE_NOTES = 24;

	/**
	 * State of the UI (Screen is appearing).
	 */
	public int state;
	
	/**
	 * Previous state of the UI.
	 */
	public int prevState;
	
	/**
	 * Static TextView for different purposes.
	 */
	public TextView genericTextView;
	
	/**
	 * Private constructor.
	 */
	private UIManagerOLD() {
		this.state = UI_STATE_LOADING;
		this.prevState = -1;
	}
	
	/**
	 * Method for getting the instance of the manager.
	 */
	public static UIManagerOLD getInstance() {
		if (UIManagerOLD.instance == null) 
			UIManagerOLD.instance = new UIManagerOLD();
		return UIManagerOLD.instance;
	}
	
	/**
	 * Method for freeing the instance of the manager.
	 */
	public static void freeInstance() {
		UIManagerOLD.instance = null;
	}
	
	public void preloadImages() {
        Activa.myApp.getResources().getAnimation(R.drawable.goingfromcalendar);
        Activa.myApp.getResources().getAnimation(R.drawable.goingtocalendar);
	}
	
	public void loadScreen(int screen) {
		Activa.myApp.setContentView(screen);
	}
	
	public void loadInitScreen() {
		this.state = UI_STATE_USERINFO;
		Activa.myApp.setContentView(R.layout.init);
		CountDownTimer timer = new CountDownTimer(3000,1000) {
			@Override
			public void onTick(long millisUntilFinished) {
			}
			
			@Override
			public void onFinish() {
				if (Activa.myMobileManager.users.isEmpty()) 
					Activa.myUIManager.loadRegisterScreen();
				else 
					Activa.myUIManager.loadLoginScreen();
			}
		};
		timer.start();
	}
	
	/**
	 * Load the login screen at the beginning of the program.
	 */
	public void loadLoginScreen() {
		Activa.myMobileManager.password = "";
		this.state = UI_STATE_LOGIN;
		Activa.myApp.setContentView(R.layout.password);
		((TextView) Activa.myApp.findViewById(R.id.startText)).setText(Activa.myLanguageManager.PSW_REQUEST);
		final FrameLayout board = (FrameLayout) Activa.myApp.findViewById(R.id.passLayout);
		board.addView(new PasswordView(Activa.myApp, 0));
		ImageButton add = (ImageButton) Activa.myApp.findViewById(R.id.add);
		add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Activa.myUIManager.loadRegisterScreen();
			}
		});
	}
	
	public void loadRegisterScreen() {
		this.state = UI_STATE_REGISTER;
		Activa.myApp.setContentView(R.layout.register);
		((TextView) Activa.myApp.findViewById(R.id.startText)).setText(Activa.myLanguageManager.PSW_REG_TITLE);
		((TextView) Activa.myApp.findViewById(R.id.requestText)).setText(Activa.myLanguageManager.PSW_REG_REQUEST);
		((TextView) Activa.myApp.findViewById(R.id.requestUser)).setText(Activa.myLanguageManager.PSW_REG_USERNAME);
		((TextView) Activa.myApp.findViewById(R.id.requestPass)).setText(Activa.myLanguageManager.PSW_REG_PASSWORD);
		((TextView) Activa.myApp.findViewById(R.id.requestPassagain)).setText(Activa.myLanguageManager.PSW_REG_PASSWORD_AGAIN);
		final EditText username = (EditText) Activa.myApp.findViewById(R.id.loginText);
		final EditText password = (EditText) Activa.myApp.findViewById(R.id.passwordText);
		final EditText passwordAgain = (EditText) Activa.myApp.findViewById(R.id.passwordTextagain);
		ImageButton ok = (ImageButton) Activa.myApp.findViewById(R.id.ok);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					String userText = username.getText().toString();
					String passText = password.getText().toString();
					String passAgain = passwordAgain.getText().toString();
					if (userText.equals("")) {
						throw new Exception();
					}
					if (passText.equals("")) {
						throw new Exception();
					}
					if (!passText.equals(passAgain)) {
						password.setText("");
						passwordAgain.setText("");
						throw new Exception();
					}
					Activa.myMobileManager.user = new User(userText, passText);
					Activa.myMobileManager.user.setCreated(false);
					Thread trd = new Thread(new UserCheckout());
					trd.start();
				} catch (Exception e) {
					loadInfoPopup(Activa.myLanguageManager.PSW_REG_BADDATA);
				}
			}
		});
	}
	
	public void loadRegisterDataScreen() {
		this.state = UI_STATE_REGISTER;
		Activa.myApp.setContentView(R.layout.registerdata);
		((TextView) Activa.myApp.findViewById(R.id.startText)).setText(Activa.myLanguageManager.PSW_REG_TITLE);
		((TextView) Activa.myApp.findViewById(R.id.requestText)).setText(Activa.myLanguageManager.PSW_REG_DATAREQUEST);
		((TextView) Activa.myApp.findViewById(R.id.requestFirst)).setText(Activa.myLanguageManager.PSW_REG_FIRSTNAME);
		((TextView) Activa.myApp.findViewById(R.id.requestLast)).setText(Activa.myLanguageManager.PSW_REG_LASTNAME);
		((TextView) Activa.myApp.findViewById(R.id.requestDate)).setText(Activa.myLanguageManager.PSW_REG_DATE);
		((RadioButton) Activa.myApp.findViewById(R.id.male)).setText(Activa.myLanguageManager.PSW_REG_MALE);
		((RadioButton) Activa.myApp.findViewById(R.id.female)).setText(Activa.myLanguageManager.PSW_REG_FEMALE);
		final EditText first = (EditText) Activa.myApp.findViewById(R.id.firstText);
		final EditText last = (EditText) Activa.myApp.findViewById(R.id.lastText);
		final EditText date = (EditText) Activa.myApp.findViewById(R.id.dateText);
		final RadioButton male = (RadioButton) Activa.myApp.findViewById(R.id.male);
		final RadioButton female = (RadioButton) Activa.myApp.findViewById(R.id.female);
		ImageButton ok = (ImageButton) Activa.myApp.findViewById(R.id.ok);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					if (first.getText().toString().equals("")) throw new Exception();
					if (last.getText().toString().equals("")) throw new Exception();
					Activa.myMobileManager.user.setFirstname(first.getText().toString());
					Activa.myMobileManager.user.setLastname(last.getText().toString());
					Activa.myMobileManager.user.setCountry(Activa.myApp.getResources().getConfiguration().locale.getDisplayCountry());
					Date birthdate;
					try {
						birthdate = ActivaUtil.universalReadableStringToDate(date.getText().toString());
					} catch (Exception e) {
						date.setText("");
						throw new Exception();
					}
					Activa.myMobileManager.user.setBirthdate(birthdate);
					boolean isMale = true;
					if (female.isChecked()) isMale = false;
					Activa.myMobileManager.user.setMale(isMale);
					loadMatrixPasswordForRegistering();
				} catch (Exception e) {
					Activa.myUIManager.loadInfoPopup(Activa.myLanguageManager.PSW_REG_BADDATA);
				} 
			}
		});
	}
	
	public void loadMatrixPasswordForRegistering() {
		Activa.myMobileManager.password = "";
		Activa.myApp.setContentView(R.layout.password);
		((TextView) Activa.myApp.findViewById(R.id.startText)).setText(Activa.myLanguageManager.PSW_REQUEST);
		ImageButton add = (ImageButton) Activa.myApp.findViewById(R.id.add);
		add.setVisibility(View.GONE);
		final FrameLayout board = (FrameLayout) Activa.myApp.findViewById(R.id.passLayout);
		board.addView(new PasswordView(Activa.myApp, 1, Activa.myMobileManager.user));
	}
	
	public void loadRequestDataScreen() {
		Activa.myApp.setContentView(R.layout.data);
		((TextView) Activa.myApp.findViewById(R.id.startText)).setText(Activa.myLanguageManager.PSW_REG_TITLE);
		((TextView) Activa.myApp.findViewById(R.id.requestText)).setText(Activa.myLanguageManager.PSW_REG_DATAREQUEST);
		((TextView) Activa.myApp.findViewById(R.id.requestWeight)).setText(Activa.myLanguageManager.PSW_REG_WEIGHT);
		((TextView) Activa.myApp.findViewById(R.id.requestHeight)).setText(Activa.myLanguageManager.PSW_REG_HEIGHT);
		final EditText weight = (EditText) Activa.myApp.findViewById(R.id.weightText);
		final EditText height = (EditText) Activa.myApp.findViewById(R.id.heightText);
		ImageButton ok = (ImageButton) Activa.myApp.findViewById(R.id.next);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					int heightText = Integer.parseInt(height.getText().toString());
					float weightText = Float.parseFloat(weight.getText().toString().replaceAll(",", "."));
					Activa.myMobileManager.user.setHeight(heightText);
					Activa.myMobileManager.user.setWeight(weightText);
					Activa.myMobileManager.user.setLastupdate(new Date());
					Activa.myMobileManager.addUserWithPassword(Activa.myMobileManager.user);
					if (Activa.mySensorManager.programassociated != null) {
						Activa.mySensorManager.programassociated.state--;
						Activa.mySensorManager.programassociated.nextStep();
					}
					else if (Activa.mySensorManager.eventAssociated != null) {
						Activa.myUIManager.loadScheduleDay(new Date());
						Activa.mySensorManager.sensorList.get(SensorManager.ID_SPIROMETER).startMeasurement();
					}
					else {
						Activa.myUIManager.loadSensorList();
						Activa.mySensorManager.sensorList.get(SensorManager.ID_SPIROMETER).startMeasurement();
					}
				} catch (NumberFormatException e) {
					Activa.myUIManager.loadInfoPopup(Activa.myLanguageManager.PSW_REG_BADDATA);
				}
			}
		});
		ImageButton no = (ImageButton) Activa.myApp.findViewById(R.id.back);
		no.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Activa.myUIManager.loadMainScreen(false, false);
			}
		});
	}
	
	public void loadRepeatPassword(User user, String prevPassword) {
		Activa.myMobileManager.password = "";
		Activa.myApp.setContentView(R.layout.password);
		((TextView) Activa.myApp.findViewById(R.id.startText)).setText(Activa.myLanguageManager.PSW_REQUEST_REPEAT);
		ImageButton add = (ImageButton) Activa.myApp.findViewById(R.id.add);
		add.setVisibility(View.GONE);
		final FrameLayout board = (FrameLayout) Activa.myApp.findViewById(R.id.passLayout);
		board.addView(new PasswordView(Activa.myApp, 2, user, prevPassword));
	}
	
	/**
	 * Load the info about the logged user.
	 */
	public void loadUserInfoScreen(final User user) {
		this.state = UI_STATE_USERINFO;
		Activa.myApp.setContentView(R.layout.info);
		TextView text = (TextView) Activa.myApp.findViewById(R.id.textInfo);
		text.append(Activa.myLanguageManager.PSW_INFO_USER + user.getName() + Activa.myLanguageManager.PSW_INFO_REGISTERED);
		CountDownTimer timer = new CountDownTimer(3000,1000) {
			@Override
			public void onTick(long millisUntilFinished) {
			}
			@Override
			public void onFinish() {
				Activa.myUIManager.loadMainScreen(true, true);
			}
		};
		timer.start();
	}
	
	/**
	 * Load the main screen
	 */

	@SuppressWarnings("deprecation")
	public void loadMainScreen(boolean login, boolean register) {
		this.state = UI_STATE_MAIN;
		Activa.myApp.setContentView(R.layout.main);
		if (Activa.myMenu != null) {
			Activa.myMenu.clear();
			Activa.myInflater.inflate(R.menu.main, Activa.myMenu);
		}
		ImageButton schedule = (ImageButton) Activa.myApp.findViewById(R.id.schedule);
		ImageButton programs = (ImageButton) Activa.myApp.findViewById(R.id.programs);
		ImageButton activities = (ImageButton) Activa.myApp.findViewById(R.id.activities);
		ImageButton questionnaires = (ImageButton) Activa.myApp.findViewById(R.id.questionnaires);
		ImageButton map = (ImageButton) Activa.myApp.findViewById(R.id.map);
		ImageButton messages = (ImageButton) Activa.myApp.findViewById(R.id.messages);
		ImageButton news = (ImageButton) Activa.myApp.findViewById(R.id.news);
		ImageButton notes = (ImageButton) Activa.myApp.findViewById(R.id.notes);
		ImageButton schedInto = (ImageButton) Activa.myApp.findViewById(R.id.scheduleEnter);
		ImageButton programsInto = (ImageButton) Activa.myApp.findViewById(R.id.programsEnter);
		ImageButton activitiesInto = (ImageButton) Activa.myApp.findViewById(R.id.activitiesEnter);
		ImageButton questionnairesInto = (ImageButton) Activa.myApp.findViewById(R.id.questionnairesEnter);
		ImageButton mapInto = (ImageButton) Activa.myApp.findViewById(R.id.mapEnter);
		ImageButton messagesInto = (ImageButton) Activa.myApp.findViewById(R.id.messagesEnter);
		ImageButton newsInto = (ImageButton) Activa.myApp.findViewById(R.id.newsEnter);
		ImageButton notesInto = (ImageButton) Activa.myApp.findViewById(R.id.notesEnter);
		schedInto.setBackgroundResource(Activa.myLanguageManager.MAIN_SCHED);
		programsInto.setBackgroundResource(Activa.myLanguageManager.MAIN_PROGRAMS);
		questionnairesInto.setBackgroundResource(Activa.myLanguageManager.MAIN_QUEST);
		activitiesInto.setBackgroundResource(Activa.myLanguageManager.MAIN_ACTIVITIES);
		messagesInto.setBackgroundResource(Activa.myLanguageManager.MAIN_MESSAGES);
		mapInto.setBackgroundResource(Activa.myLanguageManager.MAIN_MAP);
		newsInto.setBackgroundResource(Activa.myLanguageManager.MAIN_NEWS);
		notesInto.setBackgroundResource(Activa.myLanguageManager.MAIN_NOTES);
		AbsoluteLayout layout = (AbsoluteLayout) Activa.myApp.findViewById(R.id.Background);
		layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AbsoluteLayout layout = (AbsoluteLayout) Activa.myApp.findViewById(R.id.Background);
				layout.setBackgroundDrawable(Activa.myApp.getResources().getDrawable(R.drawable.scenario));
				ImageButton schedInto = (ImageButton) Activa.myApp.findViewById(R.id.scheduleEnter);
				ImageButton programsInto = (ImageButton) Activa.myApp.findViewById(R.id.programsEnter);
				ImageButton activitiesInto = (ImageButton) Activa.myApp.findViewById(R.id.activitiesEnter);
				ImageButton questionnairesInto = (ImageButton) Activa.myApp.findViewById(R.id.questionnairesEnter);
				ImageButton mapInto = (ImageButton) Activa.myApp.findViewById(R.id.mapEnter);
				ImageButton messagesInto = (ImageButton) Activa.myApp.findViewById(R.id.messagesEnter);
				ImageButton schedInit = (ImageButton) Activa.myApp.findViewById(R.id.scheduleInit);
				ImageButton programsInit = (ImageButton) Activa.myApp.findViewById(R.id.programsInit);
				ImageButton activitiesInit = (ImageButton) Activa.myApp.findViewById(R.id.activitiesInit);
				ImageButton questionnairesInit = (ImageButton) Activa.myApp.findViewById(R.id.questionnairesInit);
				ImageButton mapInit = (ImageButton) Activa.myApp.findViewById(R.id.mapInit);
				ImageButton messagesInit = (ImageButton) Activa.myApp.findViewById(R.id.messagesInit);
				ImageButton newsInit = (ImageButton) Activa.myApp.findViewById(R.id.newsInit);
				ImageButton newsInto = (ImageButton) Activa.myApp.findViewById(R.id.newsEnter);
				ImageButton notesInit = (ImageButton) Activa.myApp.findViewById(R.id.notesInit);
				ImageButton notesInto = (ImageButton) Activa.myApp.findViewById(R.id.notesEnter);
				notesInit.setVisibility(View.GONE);
				notesInto.setVisibility(View.GONE);
				newsInit.setVisibility(View.GONE);
				newsInto.setVisibility(View.GONE);
				schedInto.setVisibility(View.GONE);
				programsInto.setVisibility(View.GONE);
				activitiesInto.setVisibility(View.GONE);
				questionnairesInto.setVisibility(View.GONE);
				mapInto.setVisibility(View.GONE);
				messagesInto.setVisibility(View.GONE);
				schedInit.setVisibility(View.GONE);
				programsInit.setVisibility(View.GONE);
				activitiesInit.setVisibility(View.GONE);
				questionnairesInit.setVisibility(View.GONE);
				mapInit.setVisibility(View.GONE);
				messagesInit.setVisibility(View.GONE);
			}
		});
		schedule.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				AbsoluteLayout layout = (AbsoluteLayout) Activa.myApp.findViewById(R.id.Background);
//				layout.setBackgroundDrawable(Activa.myApp.getResources().getDrawable(R.drawable.scenariosched));
				ImageButton schedInto = (ImageButton) Activa.myApp.findViewById(R.id.scheduleEnter);
				ImageButton schedInit = (ImageButton) Activa.myApp.findViewById(R.id.scheduleInit);
				ImageButton programsInto = (ImageButton) Activa.myApp.findViewById(R.id.programsEnter);
				ImageButton activitiesInto = (ImageButton) Activa.myApp.findViewById(R.id.activitiesEnter);
				ImageButton questionnairesInto = (ImageButton) Activa.myApp.findViewById(R.id.questionnairesEnter);
				ImageButton mapInto = (ImageButton) Activa.myApp.findViewById(R.id.mapEnter);
				ImageButton messagesInto = (ImageButton) Activa.myApp.findViewById(R.id.messagesEnter);
				ImageButton newsInto = (ImageButton) Activa.myApp.findViewById(R.id.newsEnter);
				ImageButton notesInto = (ImageButton) Activa.myApp.findViewById(R.id.notesEnter);
				notesInto.setVisibility(View.GONE);
				newsInto.setVisibility(View.GONE);
				mapInto.setVisibility(View.GONE);
//				schedInto.setVisibility(View.VISIBLE);
//				schedInit.setVisibility(View.VISIBLE);
				programsInto.setVisibility(View.GONE);
				activitiesInto.setVisibility(View.GONE);
				questionnairesInto.setVisibility(View.GONE);
				messagesInto.setVisibility(View.GONE);
//				OnClickListener listenersched = new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						loadScheduleDay(new Date());
//					}
//				};
//				schedInto.setOnClickListener(listenersched);
//				schedInit.setOnClickListener(listenersched);
				v.setVisibility(View.GONE);
				ImageView animationFrame = (ImageView) Activa.myApp.findViewById(R.id.AnimationView);
				animationFrame.setVisibility(View.VISIBLE);
				animationFrame.setBackgroundResource(R.drawable.goingtocalendar);
				AnimationDrawable animation = (AnimationDrawable) animationFrame.getBackground();
				animation.start();
				scrollToCenter(animationFrame,  500, 1000);
				CountDownTimer timer = new CountDownTimer(2000,1000) {
					@Override
					public void onTick(long millisUntilFinished) {
					}
					@Override
					public void onFinish() {
						loadCalendarScreen();
					}
				};
				timer.start();
			}
		});
		programs.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AbsoluteLayout layout = (AbsoluteLayout) Activa.myApp.findViewById(R.id.Background);
				layout.setBackgroundDrawable(Activa.myApp.getResources().getDrawable(R.drawable.scenarioprog));
				ImageButton schedInto = (ImageButton) Activa.myApp.findViewById(R.id.scheduleEnter);
				ImageButton programsInto = (ImageButton) Activa.myApp.findViewById(R.id.programsEnter);
				ImageButton programsInit = (ImageButton) Activa.myApp.findViewById(R.id.programsInit);
				ImageButton activitiesInto = (ImageButton) Activa.myApp.findViewById(R.id.activitiesEnter);
				ImageButton questionnairesInto = (ImageButton) Activa.myApp.findViewById(R.id.questionnairesEnter);
				ImageButton mapInto = (ImageButton) Activa.myApp.findViewById(R.id.mapEnter);
				ImageButton messagesInto = (ImageButton) Activa.myApp.findViewById(R.id.messagesEnter);
				ImageButton newsInto = (ImageButton) Activa.myApp.findViewById(R.id.newsEnter);
				ImageButton notesInto = (ImageButton) Activa.myApp.findViewById(R.id.notesEnter);
				notesInto.setVisibility(View.GONE);
				newsInto.setVisibility(View.GONE);
				mapInto.setVisibility(View.GONE);
				programsInto.setVisibility(View.VISIBLE);
				programsInit.setVisibility(View.VISIBLE);
				schedInto.setVisibility(View.GONE);
				activitiesInto.setVisibility(View.GONE);
				questionnairesInto.setVisibility(View.GONE);
				messagesInto.setVisibility(View.GONE);
				programsInto.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						loadProgramList();
					}
				});
				programsInit.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						loadProgramList();
					}
				});
			}
		});
		activities.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AbsoluteLayout layout = (AbsoluteLayout) Activa.myApp.findViewById(R.id.Background);
				layout.setBackgroundDrawable(Activa.myApp.getResources().getDrawable(R.drawable.scenarioactiv));
				ImageButton schedInto = (ImageButton) Activa.myApp.findViewById(R.id.scheduleEnter);
				ImageButton programsInto = (ImageButton) Activa.myApp.findViewById(R.id.programsEnter);
				ImageButton activitiesInto = (ImageButton) Activa.myApp.findViewById(R.id.activitiesEnter);
				ImageButton activitiesInit = (ImageButton) Activa.myApp.findViewById(R.id.activitiesInit);
				ImageButton questionnairesInto = (ImageButton) Activa.myApp.findViewById(R.id.questionnairesEnter);
				ImageButton mapInto = (ImageButton) Activa.myApp.findViewById(R.id.mapEnter);
				ImageButton messagesInto = (ImageButton) Activa.myApp.findViewById(R.id.messagesEnter);
				ImageButton newsInto = (ImageButton) Activa.myApp.findViewById(R.id.newsEnter);
				ImageButton notesInto = (ImageButton) Activa.myApp.findViewById(R.id.notesEnter);
				notesInto.setVisibility(View.GONE);
				newsInto.setVisibility(View.GONE);
				mapInto.setVisibility(View.GONE);
				activitiesInto.setVisibility(View.VISIBLE);				
				activitiesInit.setVisibility(View.VISIBLE);				
				schedInto.setVisibility(View.GONE);
				programsInto.setVisibility(View.GONE);
				questionnairesInto.setVisibility(View.GONE);
				messagesInto.setVisibility(View.GONE);
				activitiesInto.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						loadSensorList();
					}
				});
				activitiesInit.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						loadSensorList();
					}
				});
			}
		});
		questionnaires.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AbsoluteLayout layout = (AbsoluteLayout) Activa.myApp.findViewById(R.id.Background);
				layout.setBackgroundDrawable(Activa.myApp.getResources().getDrawable(R.drawable.scenarioquest));
				ImageButton schedInto = (ImageButton) Activa.myApp.findViewById(R.id.scheduleEnter);
				ImageButton programsInto = (ImageButton) Activa.myApp.findViewById(R.id.programsEnter);
				ImageButton activitiesInto = (ImageButton) Activa.myApp.findViewById(R.id.activitiesEnter);
				ImageButton questionnairesInto = (ImageButton) Activa.myApp.findViewById(R.id.questionnairesEnter);
				ImageButton questionnairesInit = (ImageButton) Activa.myApp.findViewById(R.id.questionnairesInit);
				ImageButton mapInto = (ImageButton) Activa.myApp.findViewById(R.id.mapEnter);
				ImageButton messagesInto = (ImageButton) Activa.myApp.findViewById(R.id.messagesEnter);
				ImageButton newsInto = (ImageButton) Activa.myApp.findViewById(R.id.newsEnter);
				ImageButton notesInto = (ImageButton) Activa.myApp.findViewById(R.id.notesEnter);
				notesInto.setVisibility(View.GONE);
				newsInto.setVisibility(View.GONE);
				mapInto.setVisibility(View.GONE);
				questionnairesInto.setVisibility(View.VISIBLE);
				questionnairesInit.setVisibility(View.VISIBLE);
				schedInto.setVisibility(View.GONE);
				programsInto.setVisibility(View.GONE);
				activitiesInto.setVisibility(View.GONE);
				messagesInto.setVisibility(View.GONE);
				questionnairesInto.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						loadTotalQuestList();
					}
				});
				questionnairesInit.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						loadTotalQuestList();
					}
				});
			}
		});
		map.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AbsoluteLayout layout = (AbsoluteLayout) Activa.myApp.findViewById(R.id.Background);
				layout.setBackgroundDrawable(Activa.myApp.getResources().getDrawable(R.drawable.scenariomap));
				ImageButton schedInto = (ImageButton) Activa.myApp.findViewById(R.id.scheduleEnter);
				ImageButton programsInto = (ImageButton) Activa.myApp.findViewById(R.id.programsEnter);
				ImageButton activitiesInto = (ImageButton) Activa.myApp.findViewById(R.id.activitiesEnter);
				ImageButton questionnairesInto = (ImageButton) Activa.myApp.findViewById(R.id.questionnairesEnter);
				ImageButton mapInit = (ImageButton) Activa.myApp.findViewById(R.id.mapInit);
				ImageButton mapInto = (ImageButton) Activa.myApp.findViewById(R.id.mapEnter);
				ImageButton messagesInto = (ImageButton) Activa.myApp.findViewById(R.id.messagesEnter);
				ImageButton newsInto = (ImageButton) Activa.myApp.findViewById(R.id.newsEnter);
				ImageButton notesInto = (ImageButton) Activa.myApp.findViewById(R.id.notesEnter);
				notesInto.setVisibility(View.GONE);
				newsInto.setVisibility(View.GONE);
				mapInto.setVisibility(View.VISIBLE);
				mapInit.setVisibility(View.VISIBLE);
				schedInto.setVisibility(View.GONE);
				programsInto.setVisibility(View.GONE);
				activitiesInto.setVisibility(View.GONE);
				questionnairesInto.setVisibility(View.GONE);
				messagesInto.setVisibility(View.GONE);
				mapInto.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Activa.myMapManager.showMap();
					}
				});
				mapInit.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Activa.myMapManager.showMap();
					}
				});
			}
		});
		messages.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AbsoluteLayout layout = (AbsoluteLayout) Activa.myApp.findViewById(R.id.Background);
				layout.setBackgroundDrawable(Activa.myApp.getResources().getDrawable(R.drawable.scenariomap));
				ImageButton schedInto = (ImageButton) Activa.myApp.findViewById(R.id.scheduleEnter);
				ImageButton programsInto = (ImageButton) Activa.myApp.findViewById(R.id.programsEnter);
				ImageButton activitiesInto = (ImageButton) Activa.myApp.findViewById(R.id.activitiesEnter);
				ImageButton questionnairesInto = (ImageButton) Activa.myApp.findViewById(R.id.questionnairesEnter);
				ImageButton messagesInit = (ImageButton) Activa.myApp.findViewById(R.id.messagesInit);
				ImageButton mapInto = (ImageButton) Activa.myApp.findViewById(R.id.mapEnter);
				ImageButton messagesInto = (ImageButton) Activa.myApp.findViewById(R.id.messagesEnter);
				ImageButton newsInto = (ImageButton) Activa.myApp.findViewById(R.id.newsEnter);
				ImageButton notesInto = (ImageButton) Activa.myApp.findViewById(R.id.notesEnter);
				notesInto.setVisibility(View.GONE);
				newsInto.setVisibility(View.GONE);
				mapInto.setVisibility(View.GONE);
				messagesInit.setVisibility(View.VISIBLE);
				schedInto.setVisibility(View.GONE);
				programsInto.setVisibility(View.GONE);
				activitiesInto.setVisibility(View.GONE);
				questionnairesInto.setVisibility(View.GONE);
				messagesInto.setVisibility(View.VISIBLE);
				messagesInto.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Activa.myUIManager.loadMessageList();
					}
				});
				messagesInit.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Activa.myUIManager.loadMessageList();
					}
				});
			}
		});
		news.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AbsoluteLayout layout = (AbsoluteLayout) Activa.myApp.findViewById(R.id.Background);
				layout.setBackgroundDrawable(Activa.myApp.getResources().getDrawable(R.drawable.scenariomap));
				ImageButton schedInto = (ImageButton) Activa.myApp.findViewById(R.id.scheduleEnter);
				ImageButton programsInto = (ImageButton) Activa.myApp.findViewById(R.id.programsEnter);
				ImageButton activitiesInto = (ImageButton) Activa.myApp.findViewById(R.id.activitiesEnter);
				ImageButton questionnairesInto = (ImageButton) Activa.myApp.findViewById(R.id.questionnairesEnter);
				ImageButton mapInto = (ImageButton) Activa.myApp.findViewById(R.id.mapEnter);
				ImageButton messagesInto = (ImageButton) Activa.myApp.findViewById(R.id.messagesEnter);
				ImageButton newsInit = (ImageButton) Activa.myApp.findViewById(R.id.newsInit);
				ImageButton newsInto = (ImageButton) Activa.myApp.findViewById(R.id.newsEnter);
				ImageButton notesInto = (ImageButton) Activa.myApp.findViewById(R.id.notesEnter);
				notesInto.setVisibility(View.GONE);
				newsInit.setVisibility(View.VISIBLE);
				newsInto.setVisibility(View.VISIBLE);
				mapInto.setVisibility(View.GONE);
				schedInto.setVisibility(View.GONE);
				programsInto.setVisibility(View.GONE);
				activitiesInto.setVisibility(View.GONE);
				questionnairesInto.setVisibility(View.GONE);
				messagesInto.setVisibility(View.GONE);
				newsInto.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Activa.myUIManager.loadNewsList(true);
					}
				});
				newsInit.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Activa.myUIManager.loadNewsList(true);
					}
				});
			}
		});
		notes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AbsoluteLayout layout = (AbsoluteLayout) Activa.myApp.findViewById(R.id.Background);
				layout.setBackgroundDrawable(Activa.myApp.getResources().getDrawable(R.drawable.scenariomap));
				ImageButton schedInto = (ImageButton) Activa.myApp.findViewById(R.id.scheduleEnter);
				ImageButton programsInto = (ImageButton) Activa.myApp.findViewById(R.id.programsEnter);
				ImageButton activitiesInto = (ImageButton) Activa.myApp.findViewById(R.id.activitiesEnter);
				ImageButton questionnairesInto = (ImageButton) Activa.myApp.findViewById(R.id.questionnairesEnter);
				ImageButton mapInto = (ImageButton) Activa.myApp.findViewById(R.id.mapEnter);
				ImageButton messagesInto = (ImageButton) Activa.myApp.findViewById(R.id.messagesEnter);
				ImageButton notesInit = (ImageButton) Activa.myApp.findViewById(R.id.notesInit);
				ImageButton newsInto = (ImageButton) Activa.myApp.findViewById(R.id.newsEnter);
				ImageButton notesInto = (ImageButton) Activa.myApp.findViewById(R.id.notesEnter);
				notesInto.setVisibility(View.VISIBLE);
				notesInit.setVisibility(View.VISIBLE);
				newsInto.setVisibility(View.GONE);
				mapInto.setVisibility(View.GONE);
				schedInto.setVisibility(View.GONE);
				programsInto.setVisibility(View.GONE);
				activitiesInto.setVisibility(View.GONE);
				questionnairesInto.setVisibility(View.GONE);
				messagesInto.setVisibility(View.GONE);
				notesInto.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Activa.myUIManager.loadNotes();
					}
				});
				notesInit.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Activa.myUIManager.loadNotes();
					}
				});
			}
		});
		ImageButton refresh = (ImageButton) Activa.myApp.findViewById(R.id.refresh);
		refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((ImageButton)Activa.myApp.findViewById(R.id.refresh)).setVisibility(View.GONE);
				final RelativeLayout popupView = (RelativeLayout) Activa.myApp.findViewById(R.id.popupView);
				popupView.setVisibility(View.VISIBLE);
				TextView popupText = (TextView) Activa.myApp.findViewById(R.id.popupText);
				if (Activa.myMobileManager.identified) {
					RefreshingConnection refreshing = new RefreshingConnection();
					Thread thread = new Thread(refreshing, "REFRESH");
					thread.start();
				}
				else{
					popupText.setText(Activa.myLanguageManager.USER_NOID);
					CountDownTimer timer = new CountDownTimer(3000, 1000) {
						@Override
						public void onTick(long millisUntilFinished) {}
						@Override
						public void onFinish() {
							RelativeLayout popupView = (RelativeLayout) Activa.myApp.findViewById(R.id.popupView);
							if (popupView != null) popupView.setVisibility(View.INVISIBLE);
							ImageButton refresh = (ImageButton)Activa.myApp.findViewById(R.id.refresh);
							if (refresh != null) refresh.setVisibility(View.VISIBLE);
						}
					};
					timer.start();
				}
			}
		});
		if (login) {
			((ImageButton)Activa.myApp.findViewById(R.id.refresh)).setVisibility(View.GONE);
			final RelativeLayout popupView = (RelativeLayout) Activa.myApp.findViewById(R.id.popupView);
			popupView.setVisibility(View.VISIBLE);
			TextView popupText = (TextView) Activa.myApp.findViewById(R.id.popupText);
			if (Activa.myMobileManager.identified) {
				InitialConnection initial = new InitialConnection(register);
				Thread thread = new Thread(initial, "LOGIN");
				thread.start();
			}
			else{
				popupText.setText(Activa.myLanguageManager.USER_NOID);
				CountDownTimer timer = new CountDownTimer(3000, 1000) {
					@Override
					public void onTick(long millisUntilFinished) {}
					@Override
					public void onFinish() {
						RelativeLayout popupView = (RelativeLayout) Activa.myApp.findViewById(R.id.popupView);
						if (popupView != null) popupView.setVisibility(View.INVISIBLE);
						ImageButton refresh = (ImageButton)Activa.myApp.findViewById(R.id.refresh);
						if (refresh != null) refresh.setVisibility(View.VISIBLE);
					}
				};
				timer.start();
			}
		}	
	}
	
	public void loadCalendarScreen() {
		Activa.myApp.setContentView(R.layout.scenario);
		AbsoluteLayout background = (AbsoluteLayout) Activa.myApp.findViewById(R.id.Background);
		background.setBackgroundResource(R.drawable.calendarscenario);
		ImageView title = (ImageView) Activa.myApp.findViewById(R.id.title);
		title.setBackgroundResource(Activa.myLanguageManager.MAIN_SCHED);
		ImageButton inside = (ImageButton) Activa.myApp.findViewById(R.id.inside);
		ImageButton outside = (ImageButton) Activa.myApp.findViewById(R.id.outside);
		inside.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadScheduleDay(new Date());
			}
		});
		outside.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ImageView animationFrame = (ImageView) Activa.myApp.findViewById(R.id.AnimationView);
				animationFrame.setVisibility(View.VISIBLE);
				animationFrame.setBackgroundResource(R.drawable.goingfromcalendar);
				AnimationDrawable animation = (AnimationDrawable) animationFrame.getBackground();
				animation.start();
				scrollToLeft(animationFrame,  500, 1000);
				CountDownTimer timer = new CountDownTimer(2000,1000) {
					@Override
					public void onTick(long millisUntilFinished) {
					}
					@Override
					public void onFinish() {
						loadMainScreen(false, false);
					}
				};
				timer.start();
			}
		});
	}
	
	private void scrollToCenter(View view, int time, int delay) {
		AnimationSet set = new AnimationSet(true);
		set.setFillAfter(true);
		HorizontalScrollView scroll = (HorizontalScrollView) Activa.myApp.findViewById(R.id.ScrollView);
		int x = scroll.getScrollX();
		TranslateAnimation animation = new TranslateAnimation(-x, -160, 0, 0);
		animation.setDuration(time);
		animation.setFillAfter(true);
		set.addAnimation(animation);
		LayoutAnimationController controller = new LayoutAnimationController(set, delay);
		view.setAnimation(controller.getAnimation());
	}
	
	private void scrollToLeft(View view, int time, int delay) {
		AnimationSet set = new AnimationSet(true);
		set.setFillAfter(true);
		TranslateAnimation animation = new TranslateAnimation(-160, 0, 0, 0);
		animation.setDuration(time);
		animation.setFillAfter(true);
		set.addAnimation(animation);
		LayoutAnimationController controller = new LayoutAnimationController(set, delay);
		view.setAnimation(controller.getAnimation());
	}
	
	/**
	 * Load the list of questionnaires to do.
	 */
	public void loadTotalQuestList() {
		this.state = UI_STATE_TOTALQUESTIONNAIRE;
		Activa.myApp.setContentView(R.layout.list);
		((TextView) Activa.myApp.findViewById(R.id.startText)).setText(Activa.myLanguageManager.QUEST_TITLE);
		TableLayout questlisting = (TableLayout)Activa.myApp.findViewById(R.id.listing);
		Enumeration<Questionnaire> enumer = Activa.myQuestManager.questionnaireSet.elements();
		while (enumer.hasMoreElements()) {			
			Questionnaire quest = enumer.nextElement();
			TableRow buttonLayout = new TableRow(Activa.myApp);
			buttonLayout.setId(quest.id);
			buttonLayout.setOrientation(TableRow.HORIZONTAL);
			buttonLayout.setGravity(Gravity.CENTER_VERTICAL);
			buttonLayout.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));			
			ImageButton button = new ImageButton(Activa.myApp);
			button.setBackgroundResource(R.drawable.iconbg);
			button.setImageResource(R.drawable.quest);
			QuestionnaireButtonListenerDemo listener = new QuestionnaireButtonListenerDemo(quest.id);
			TextView text = new TextView(Activa.myApp);
			text.append(quest.name);
			text.setMaxWidth(240);
			text.setTextSize((float) 20);
			text.setTextColor(Color.BLACK);
			text.setTypeface(Typeface.SANS_SERIF);
			buttonLayout.addView(button);
			buttonLayout.addView(text);
			button.setOnClickListener(listener);
			text.setOnClickListener(listener);
			buttonLayout.setOnClickListener(listener);
			questlisting.addView(buttonLayout);
		}
		ImageButton back = (ImageButton) Activa.myApp.findViewById(R.id.previous);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadMainScreen(false, false);
			}
		});
	}

	public void loadQuestion(final Question question) {
		Activa.myQuestManager.currentQuestion = question;
		this.state = UI_STATE_QUESTION;
		TextView questionText;
		ImageButton next;
		switch (question.type) {
			case Question.MONO_QUESTION:
				Activa.myApp.setContentView(R.layout.monoquestion);
				questionText = (TextView) Activa.myApp.findViewById(R.id.questionText);
				questionText.setText(question.text);
				RadioGroup answersGroup = (RadioGroup) Activa.myApp.findViewById(R.id.options);
				next = (ImageButton) Activa.myApp.findViewById(R.id.next);
				for (int i = 0; i < question.answerSet.size(); i++) {
					RadioButton button = new RadioButton(Activa.myApp);
					button.setText(question.answerSet.get(i).text);
					button.setTextColor(Color.BLACK);
					button.setId(i);
					button.setLayoutParams(new LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT));
					answersGroup.addView(button);
				}
				answersGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {					
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						Questionnaire questionnaire = Activa.myQuestManager.questionnaireSet.get(Activa.myQuestManager.activeQuestionnaireId);
						questionnaire.questionSet.get(questionnaire.currentQuestionId).monoAnswer = checkedId;
						questionnaire.questionSet.get(questionnaire.currentQuestionId).answered = true;							
					}
				});
				next.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Questionnaire questionnaire = Activa.myQuestManager.questionnaireSet.get(Activa.myQuestManager.activeQuestionnaireId);
						if (questionnaire.questionSet.get(questionnaire.currentQuestionId).answered||questionnaire.questionSet.get(questionnaire.currentQuestionId).jumpable) {
							questionnaire.questionsAnswered.push(question);
							questionnaire.currentQuestionId = questionnaire.questionSet.get(questionnaire.currentQuestionId).calcNextQuestion();
							if (questionnaire.currentQuestionId > 0) Activa.myUIManager.loadQuestion(questionnaire.questionSet.get(questionnaire.currentQuestionId));	
							else Activa.myQuestManager.finishQuestionnaire();
						}
					}
				});
				break;
			case Question.POLI_QUESTION:
				question.poliAnswer = new HashSet<Integer>();
				Activa.myApp.setContentView(R.layout.poliquestion);
				questionText = (TextView) Activa.myApp.findViewById(R.id.questionText);
				questionText.setText(question.text);
				next = (ImageButton) Activa.myApp.findViewById(R.id.next);
				LinearLayout answersGroupPoli = (LinearLayout) Activa.myApp.findViewById(R.id.options);
				for (int i = 0; i < question.answerSet.size(); i++) {
					CheckBox button = new CheckBox(Activa.myApp);
					button.setText(question.answerSet.get(i).text);
					button.setTextColor(Color.BLACK);
					button.setId(i);
					button.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
					answersGroupPoli.addView(button);
					button.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {	
						@Override
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							Questionnaire questionnaire = Activa.myQuestManager.questionnaireSet.get(Activa.myQuestManager.activeQuestionnaireId);
							Question question = questionnaire.questionSet.get(questionnaire.currentQuestionId);
							if (isChecked) question.poliAnswer.add(buttonView.getId()); 
							else question.poliAnswer.remove(buttonView.getId()); 
						}
					});
				}
				next.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Questionnaire questionnaire = Activa.myQuestManager.questionnaireSet.get(Activa.myQuestManager.activeQuestionnaireId);
						if (!questionnaire.questionSet.get(questionnaire.currentQuestionId).poliAnswer.isEmpty()) 
							questionnaire.questionSet.get(questionnaire.currentQuestionId).answered = true;
						if (questionnaire.questionSet.get(questionnaire.currentQuestionId).answered||questionnaire.questionSet.get(questionnaire.currentQuestionId).jumpable) {
							questionnaire.questionsAnswered.push(question);
							questionnaire.currentQuestionId = questionnaire.questionSet.get(questionnaire.currentQuestionId).calcNextQuestion();
							if (questionnaire.currentQuestionId > 0) Activa.myUIManager.loadQuestion(questionnaire.questionSet.get(questionnaire.currentQuestionId));	
							else Activa.myQuestManager.finishQuestionnaire();
						}
					}
				});
				break;
			case Question.NUMERIC_QUESTION:
				Activa.myApp.setContentView(R.layout.numquestion);
				final String representation[] = {Activa.myLanguageManager.BORG_0, Activa.myLanguageManager.BORG_05, 
						Activa.myLanguageManager.BORG_1, Activa.myLanguageManager.BORG_1, 
						Activa.myLanguageManager.BORG_2, Activa.myLanguageManager.BORG_2, 
						Activa.myLanguageManager.BORG_3, Activa.myLanguageManager.BORG_3, 
						Activa.myLanguageManager.BORG_4, Activa.myLanguageManager.BORG_4, 
						Activa.myLanguageManager.BORG_5, Activa.myLanguageManager.BORG_5, 
						Activa.myLanguageManager.BORG_5, Activa.myLanguageManager.BORG_5, 
						Activa.myLanguageManager.BORG_7, Activa.myLanguageManager.BORG_7, 
						Activa.myLanguageManager.BORG_7, Activa.myLanguageManager.BORG_7, 
						Activa.myLanguageManager.BORG_9, Activa.myLanguageManager.BORG_9, 
						Activa.myLanguageManager.BORG_10, Activa.myLanguageManager.BORG_10};
				questionText = (TextView) Activa.myApp.findViewById(R.id.questionText);
				final TextView numText = (TextView) Activa.myApp.findViewById(R.id.numText);
				questionText.setText(question.text);
				if (question.borg)
					numText.setText("0 - " + representation[0]);
				else 
					numText.setText("0");
				SeekBar seekbar = (SeekBar) Activa.myApp.findViewById(R.id.seekbar);
				seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {				
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						
					}
					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						
					}
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
						if (question.borg) {
							int selection = progress/5;
							if ((selection == 1)) numText.setText("0.5 - " + representation[1]);
							else numText.setText("" + progress/10 + " - " + representation[selection]);
						}
						else 
							numText.setText("" + progress/10);						
					}
				});
				next = (ImageButton) Activa.myApp.findViewById(R.id.next);
				next.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Questionnaire questionnaire = Activa.myQuestManager.questionnaireSet.get(Activa.myQuestManager.activeQuestionnaireId);
						SeekBar seekbar = (SeekBar) Activa.myApp.findViewById(R.id.seekbar);
						questionnaire.questionSet.get(questionnaire.currentQuestionId).numericAnswer = (float) (seekbar.getProgress()/10);
						questionnaire.questionSet.get(questionnaire.currentQuestionId).answered = true;
						questionnaire.questionsAnswered.push(question);
						questionnaire.currentQuestionId = questionnaire.questionSet.get(questionnaire.currentQuestionId).calcNextQuestion();
						if (questionnaire.currentQuestionId > 0) Activa.myUIManager.loadQuestion(questionnaire.questionSet.get(questionnaire.currentQuestionId));	
						else Activa.myQuestManager.finishQuestionnaire();
					}
				});
				break;
			case Question.TEXT_QUESTION:
				question.poliAnswer = new HashSet<Integer>();
				Activa.myApp.setContentView(R.layout.textquestion);
				questionText = (TextView) Activa.myApp.findViewById(R.id.questionText);
				questionText.setText(question.text);
				next = (ImageButton) Activa.myApp.findViewById(R.id.next);
				next.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Questionnaire questionnaire = Activa.myQuestManager.questionnaireSet.get(Activa.myQuestManager.activeQuestionnaireId);
						EditText editable = (EditText) Activa.myApp.findViewById(R.id.editable);
						questionnaire.questionSet.get(questionnaire.currentQuestionId).textAnswer = editable.getText().toString();
						questionnaire.questionSet.get(questionnaire.currentQuestionId).answered = true;
						questionnaire.questionsAnswered.push(question);
						questionnaire.currentQuestionId = questionnaire.questionSet.get(questionnaire.currentQuestionId).calcNextQuestion();
						if (questionnaire.currentQuestionId > 0) Activa.myUIManager.loadQuestion(questionnaire.questionSet.get(questionnaire.currentQuestionId));	
						else Activa.myQuestManager.finishQuestionnaire();
					}
				});
				break;
			case Question.INFO_QUESTION:
				Activa.myApp.setContentView(R.layout.info);
				((RelativeLayout)Activa.myApp.findViewById(R.id.questcontrol)).setVisibility(View.VISIBLE);
				TextView text = (TextView) Activa.myApp.findViewById(R.id.textInfo); 
				text.setText(question.text);
				((ImageButton) Activa.myApp.findViewById(R.id.previous)).setVisibility(View.VISIBLE);
				next = (ImageButton) Activa.myApp.findViewById(R.id.next);
				next.setVisibility(View.VISIBLE);
				next.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Questionnaire questionnaire = Activa.myQuestManager.questionnaireSet.get(Activa.myQuestManager.activeQuestionnaireId);
						questionnaire.questionsAnswered.push(question);
						questionnaire.currentQuestionId = questionnaire.questionSet.get(questionnaire.currentQuestionId).calcNextQuestion();
						if (questionnaire.currentQuestionId > 0) Activa.myUIManager.loadQuestion(questionnaire.questionSet.get(questionnaire.currentQuestionId));	
						else Activa.myQuestManager.finishQuestionnaire();
					}
				});
				break;
			case Question.RESULT_QUESTION:
				break;			
		}
		ImageButton back = (ImageButton) Activa.myApp.findViewById(R.id.previous);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Questionnaire questionnaire = Activa.myQuestManager.questionnaireSet.get(Activa.myQuestManager.activeQuestionnaireId);
				if (!questionnaire.questionsAnswered.empty()) {
					Question prev = Activa.myQuestManager.questionnaireSet.get(Activa.myQuestManager.activeQuestionnaireId).questionsAnswered.pop();
					questionnaire.currentQuestionId = prev.id;
					Activa.myUIManager.loadQuestion(prev);
				}
				else {
					Activa.myQuestManager.questionnaireSet.get(Activa.myQuestManager.activeQuestionnaireId).currentQuestionId = 1;
					Activa.myUIManager.loadTotalQuestList();
				}
			}
		});
	}
	
	/**
	 * This method start the filling of the selected as active questionnaire.
	 */
	public void startActiveQuestionnaire() {
		Activa.myUIManager.state = UIManagerOLD.UI_STATE_QUESTIONNAIREINIT;
		Activa.myApp.setContentView(R.layout.yesnoquestion);
		Activa.myQuestManager.activeQuestionnaireId = Activa.myQuestManager.activeQuestionnaireId;
		TextView question = (TextView)Activa.myApp.findViewById(R.id.question);
		question.append(Activa.myLanguageManager.QUEST_START + Activa.myQuestManager.questionnaireSet.get(Activa.myQuestManager.activeQuestionnaireId).name + "?");
		ImageButton yes = (ImageButton)Activa.myApp.findViewById(R.id.yes);
		ImageButton no = (ImageButton)Activa.myApp.findViewById(R.id.no);
		yes.setOnClickListener(null);
		yes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Activa.myQuestManager.questionnaireSet.get(Activa.myQuestManager.activeQuestionnaireId).start();
			}
		});
		no.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Activa.myUIManager.loadMainScreen(false, false);
			}
		});
	}

	public void loadQuestResult(float result) {
		Activa.myApp.setContentView(R.layout.resultimage);
		TextView text = (TextView) Activa.myApp.findViewById(R.id.textInfo);
		if (result < 0.0f) text.setText(Activa.myLanguageManager.QUEST_WEIGHT_NOTDONE);
		else text.setText(Activa.myLanguageManager.QUEST_WEIGHT_DONE + String.format("%.1f", result) + " %. ");
		ImageView resultText = (ImageView) Activa.myApp.findViewById(R.id.result);
		if (result < 0.0) {
		}
		else if (result < 33.33) {
			text.append(Activa.myLanguageManager.QUEST_WEIGHT_0);
			resultText.setBackgroundResource(R.drawable.lightgreen);
		}
		else if (result < 66.66) {
			text.append(Activa.myLanguageManager.QUEST_WEIGHT_1);
			resultText.setBackgroundResource(R.drawable.lightorange);
		}
		else {
			text.append(Activa.myLanguageManager.QUEST_WEIGHT_2);
			resultText.setBackgroundResource(R.drawable.lightwhite);
		}
		CountDownTimer timer = new CountDownTimer(3000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {}
			@Override
			public void onFinish() {
				Activa.myApp.setContentView(R.layout.sending);
				SendQuestionnaire sending = new SendQuestionnaire(Activa.myQuestManager.questionnaireSet.get(Activa.myQuestManager.activeQuestionnaireId));
				Thread thread = new Thread(sending, "SENDQUESTIONNAIRE");
				thread.start();
			}
		};
		timer.start();
	}
	
	public void loadSensorList() {
		this.state = UI_STATE_TOTALSENSOR;
		Activa.myApp.setContentView(R.layout.list);
		if (Activa.myMenu != null) {
			Activa.myMenu.clear();
			Activa.myInflater.inflate(R.menu.sensors, Activa.myMenu);
		}
		((TextView) Activa.myApp.findViewById(R.id.startText)).append(Activa.myLanguageManager.SENSORS_TITLE);
		TableLayout sensorlisting = (TableLayout)Activa.myApp.findViewById(R.id.listing);
		Enumeration<Sensor> enumer = Activa.mySensorManager.sensorList.elements();
		int[] sensorIDs = new int[Activa.mySensorManager.sensorList.size()];
		int index = 3;
		while (enumer.hasMoreElements()) {
			Sensor sensor = enumer.nextElement();
			if (sensor.id == SensorManager.ID_SPIROMETER) {
				sensorIDs[0] = sensor.id;
			}
			else if (sensor.id == SensorManager.ID_PULSIOXYMETER) {
				sensorIDs[1] = sensor.id;
			}
			else if (sensor.id == SensorManager.ID_EXERCISE) {
				sensorIDs[2] = sensor.id;
			}
			else {
				sensorIDs[index] = sensor.id;
				index++;
			}
		}
		for (int i = 0; i < sensorIDs.length; i++) {
			Sensor sensor = Activa.mySensorManager.sensorList.get(sensorIDs[i]);
			TableRow buttonLayout = new TableRow(Activa.myApp);
			buttonLayout.setId(sensor.id);
			buttonLayout.setOrientation(TableRow.HORIZONTAL);
			buttonLayout.setGravity(Gravity.CENTER_VERTICAL);
			buttonLayout.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));			
			ImageButton button = new ImageButton(Activa.myApp);
			button.setBackgroundResource(R.drawable.iconbg);
			button.setImageResource(sensor.icon);
			button.setId(sensor.id);
			OnClickListener listener = new OnClickListener() {			
				@Override
				public void onClick(View v) {
					Activa.mySensorManager.eventAssociated = null;
					Activa.myProgramManager.eventAssociated = null;
					Activa.mySensorManager.programassociated = null;
					Activa.mySensorManager.startSensorMeasurement(v.getId());
				}
			};
			button.setOnClickListener(listener);
			TextView text = new TextView(Activa.myApp);
			text.append(sensor.name);
			text.setTextSize((float) 20);
			text.setTextColor(Color.BLACK);
			text.setTypeface(Typeface.SANS_SERIF);
			text.setOnClickListener(listener);
			text.setId(sensor.id);
			buttonLayout.addView(button);
			buttonLayout.addView(text);
			buttonLayout.setOnClickListener(listener);
			button.setOnClickListener(listener);
			text.setOnClickListener(listener);
			sensorlisting.addView(buttonLayout);
		}
		ImageButton help = (ImageButton) Activa.myApp.findViewById(R.id.help);
		help.setVisibility(View.INVISIBLE);
		ImageButton back = (ImageButton) Activa.myApp.findViewById(R.id.previous);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadMainScreen(false, false);
			}
		});
	}

	/*public void loadTimedSensorMeasurement(final String sensor, int seconds) {
		this.state = UI_STATE_SENSORING;
		Activa.myApp.setContentView(R.layout.list);
		((TextView) Activa.myApp.findViewById(R.id.startText)).append(sensor);
		TableLayout optionslisting = (TableLayout)Activa.myApp.findViewById(R.id.listing);
		
		final EditText editable = new EditText(Activa.myApp);
		editable.setText("" + seconds);
		
		TableRow buttonLayout = new TableRow(Activa.myApp);
		buttonLayout.setId(1);
		buttonLayout.setOrientation(TableRow.HORIZONTAL);
		buttonLayout.setGravity(Gravity.CENTER_VERTICAL);
		buttonLayout.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));			
		ImageButton button = new ImageButton(Activa.myApp);
		button.setBackgroundResource(R.drawable.iconbg);
		button.setImageResource(R.drawable.automatic);
		button.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				timedSensorMeasurement(sensor, Integer.parseInt(editable.getText().toString()));
			}
		});
		TextView text = new TextView(Activa.myApp);
		text.append("Lectura automatica");
		text.setTextSize((float) 20);
		text.setTextColor(Color.BLACK);
		text.setTypeface(Typeface.SANS_SERIF);
		buttonLayout.addView(button);
		buttonLayout.addView(text);
		optionslisting.addView(buttonLayout);
		
		buttonLayout = new TableRow(Activa.myApp);
		buttonLayout.setId(1);
		buttonLayout.setOrientation(TableRow.HORIZONTAL);
		buttonLayout.setGravity(Gravity.CENTER_VERTICAL);
		buttonLayout.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));			
		button = new ImageButton(Activa.myApp);
		button.setBackgroundResource(R.drawable.iconbg);
		button.setImageResource(R.drawable.sensor2);
		button.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				manualSensorMeasurement();
			}
		});
		text = new TextView(Activa.myApp);
		text.append("Lectura manual");
		text.setTextSize((float) 20);
		text.setTextColor(Color.BLACK);
		text.setTypeface(Typeface.SANS_SERIF);
		buttonLayout.addView(button);
		buttonLayout.addView(text);
		optionslisting.addView(buttonLayout);
		
		buttonLayout = new TableRow(Activa.myApp);
		buttonLayout.setId(1);
		buttonLayout.setOrientation(TableRow.HORIZONTAL);
		buttonLayout.setGravity(Gravity.CENTER_VERTICAL);
		buttonLayout.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));			
		button = new ImageButton(Activa.myApp);
		button.setBackgroundResource(R.drawable.iconbg);
		button.setImageResource(R.drawable.sensor3);
		button.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Activa.myUIManager.state = UI_STATE_SENSORINFO;
				Activa.myApp.setContentView(R.layout.info);
				TextView text = (TextView) Activa.myApp.findViewById(R.id.textInfo);
				text.setTextSize(17);
				text.append("Con la lectura automatica siga las instrucciones de la pantalla, " +
						"introduciendo previamente el numero de segundos durante los que medir." +
						"Con la lectura manual realiza la medida e introduce los datos en pantalla.");
				View appLayout = (View) Activa.myApp.findViewById(R.id.mainLayout);
				appLayout.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						Activa.myUIManager.loadTimedSensorMeasurement(sensor,10);
						return false;
					}
				});
			}
		});
		text = new TextView(Activa.myApp);
		text.append("Ayuda");
		text.setTextSize((float) 20);
		text.setTextColor(Color.BLACK);
		text.setTypeface(Typeface.SANS_SERIF);
		buttonLayout.addView(button);
		buttonLayout.addView(text);
		optionslisting.addView(buttonLayout);
		
		buttonLayout = new TableRow(Activa.myApp);
		buttonLayout.setId(1);
		buttonLayout.setOrientation(TableRow.HORIZONTAL);
		buttonLayout.setGravity(Gravity.CENTER_VERTICAL);
		buttonLayout.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));			
		text = new TextView(Activa.myApp);
		text.append("Segundos: ");
		text.setTextSize((float) 20);
		text.setTextColor(Color.BLACK);
		text.setTypeface(Typeface.SANS_SERIF);
		buttonLayout.addView(text);
		buttonLayout.addView(editable);
		optionslisting.addView(buttonLayout);
		
		ImageButton back = (ImageButton) Activa.myApp.findViewById(R.id.previous);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadSensorList();
			}
		});
	}*/
	
	/*public void automaticSensorMeasurement(String sensor) {
		this.state = UI_STATE_MEASURING;
		Activa.myApp.setContentView(R.layout.waiting);
		TextView title = (TextView) Activa.myApp.findViewById(R.id.startText);
		title.append(sensor);
		TextView text = (TextView) Activa.myApp.findViewById(R.id.infoText);
		text.append("Leyendo datos del sensor ...");
		AnimSensoringTimeTask timer = new AnimSensoringTimeTask(5000, 125, sensor, 0);
		timer.start();
	}
	
	public void manualSensorMeasurement() {
		this.state = UI_STATE_MANUALMEAS;
		Activa.myApp.setContentView(R.layout.manual);
		final EditText measure = (EditText) Activa.myApp.findViewById(R.id.measure);
		ImageButton next = (ImageButton) Activa.myApp.findViewById(R.id.next);
		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadSensorResult(measure.getText().toString(), "FVC");
			}
		});
	}*/
	
	/*public void loadSensorResult(String result, String measurement) {
		Activa.myApp.setContentView(R.layout.result);
		TextView text = (TextView) Activa.myApp.findViewById(R.id.textInfo);
		TextView resultText = (TextView) Activa.myApp.findViewById(R.id.result);
		text.append("La medicion de " + measurement + " es de ...");
		resultText.append(result);
		CountDownTimer timer = new CountDownTimer(3000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {}
			@Override
			public void onFinish() {
				Activa.myApp.setContentView(R.layout.sending);
				SendSensorResult sending = new SendSensorResult(new Spirometer());
				Thread thread = new Thread(sending, "SENDQUESTIONNAIRE");
				thread.start();
			}
		};
		timer.start();
	}*/
	
	/*public void timedSensorMeasurement(String sensor, int seconds) {
		this.state = UI_STATE_MEASURING;
		Activa.myApp.setContentView(R.layout.waiting);
		TextView title = (TextView) Activa.myApp.findViewById(R.id.startText);
		title.append(sensor);
		TextView text = (TextView) Activa.myApp.findViewById(R.id.infoText);
		text.append("Descubriendo el sensor ...");
		WaitTimeTask3 timer = new WaitTimeTask3(2000, 1000, seconds*1000);
		timer.start();
	}*/
	
	/**
	 * Show on the screen the schedule for the following hours.
	 */
	/*public void loadSchedule() {
		TextView time;
		Enumeration<String> enumeration;
		Event event = null; 
		Date currentDate;
		Date prevDate = new Date(0);
		Date date = new Date();
		Date dateday = new Date();
		Date dateLater = new Date();
		TableRow buttonLayout;
		this.state = UI_STATE_SCHEDULE;
		if (Activa.myMenu != null) {
			Activa.myMenu.clear();
			Activa.myInflater.inflate(R.menu.schedule, Activa.myMenu);
		}
		Activa.myApp.setContentView(R.layout.schedule);
		ImageButton back = (ImageButton) Activa.myApp.findViewById(R.id.previous);
		TableLayout schedule = (TableLayout)Activa.myApp.findViewById(R.id.schedule);
		Vector v = new Vector(Activa.myCalendarManager.events.keySet());
		Collections.sort(v);
		Iterator it = v.listIterator();
		date.setTime((date.getTime()/3600000)*3600000);
		dateday.setTime((date.getTime()/86400000)*86400000);
		while (it.hasNext()) {
			final long timeLong = (Long) it.next();
			event = Activa.myCalendarManager.events.get(timeLong);
			currentDate = new Date((event.getDate().getTime()/86400000)*86400000);
			if (date.compareTo(event.getDate()) > 0) {
				if (currentDate.compareTo(prevDate) > 0) {
					time = new TextView(Activa.myApp);	
					time.setText(ActivaUtil.dateToReadableString(event.date));
					time.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
					time.setPadding(5, 10, 5, 10);
					time.setTypeface(Typeface.DEFAULT_BOLD);
					time.setTextSize(20);
					time.setGravity(Gravity.CENTER);
					time.setTextColor(Color.BLACK);
					schedule.addView(time);		
					View separator = new View(Activa.myApp);
					LayoutParams separatorLayout = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, 1);
					separator.setLayoutParams(separatorLayout);
					separator.setBackgroundColor(Color.BLACK);				
					schedule.addView(separator);
					prevDate = new Date(currentDate.getTime());
				}
				buttonLayout = new TableRow(Activa.myApp);
				buttonLayout.setOrientation(TableRow.HORIZONTAL);
				buttonLayout.setGravity(Gravity.CENTER_VERTICAL);
				buttonLayout.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, 40));			
				time = new TextView(Activa.myApp);
				time.setPadding(5, 10, 5, 10);
				time.setTextSize(20);
				time.setTextColor(Color.BLACK);
				time.setTypeface(Typeface.SANS_SERIF);
				time.setText(String.format("%02d:%02d", event.date.getHours(), event.date.getMinutes()));
				TextView activity = new TextView(Activa.myApp);
				activity.setText(event.name);
				activity.setPadding(5, 5, 5, 5);
				activity.setTextSize(20);
				activity.setTextColor(Color.BLACK);
				activity.setTypeface(Typeface.SANS_SERIF);
				ImageView button = new ImageView(Activa.myApp);
				switch (event.state) {
					case 0:
						button.setBackgroundResource(R.drawable.oksmall);
						break;
					case 1:
						button.setBackgroundResource(R.drawable.nosmall);
						break;
					case 2:
						button.setBackgroundResource(R.drawable.gosmall);
						break;
				}		
				buttonLayout.addView(time);
				buttonLayout.addView(activity);
				buttonLayout.addView(button);	
				time.setOnClickListener(new OnClickListener() {				
					@Override
					public void onClick(View v) {
						Activa.myCalendarManager.events.get(timeLong).doActivity();
					}
				});	
				activity.setOnClickListener(new OnClickListener() {				
					@Override
					public void onClick(View v) {
						Activa.myCalendarManager.events.get(timeLong).doActivity();
					}
				});
				button.setOnClickListener(new OnClickListener() {				
					@Override
					public void onClick(View v) {
						Activa.myCalendarManager.events.get(timeLong).doActivity();
					}
				});
				schedule.addView(buttonLayout);
				View separator = new View(Activa.myApp);
				LayoutParams separatorLayout = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, 1);
				separator.setLayoutParams(separatorLayout);
				separator.setBackgroundColor(Color.BLACK);				
				schedule.addView(separator);
			}
		}
		dateLater.setTime((dateLater.getTime()/3600000)*3600000);
		dateLater.setHours(dateLater.getHours() + 1);
		for (int i = 0; i < ActivaConfig.SCHEDULE_HOURS; i++) {	
			currentDate = new Date((date.getTime()/86400000)*86400000);
			if ((date.getHours() == 0)||((currentDate.compareTo(prevDate) > 0)&&(i == 0))) {
				time = new TextView(Activa.myApp);	
				time.setText(ActivaUtil.dateToReadableString(date));
				time.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				time.setPadding(5, 10, 5, 10);
				time.setTextSize(20);
				time.setGravity(Gravity.CENTER);
				time.setTextColor(Color.BLACK);
				time.setTypeface(Typeface.DEFAULT_BOLD);
				schedule.addView(time);		
				View separator = new View(Activa.myApp);
				LayoutParams separatorLayout = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, 1);
				separator.setLayoutParams(separatorLayout);
				separator.setBackgroundColor(Color.BLACK);				
				schedule.addView(separator);	
			}
			buttonLayout = new TableRow(Activa.myApp);
			buttonLayout.setOrientation(TableRow.HORIZONTAL);
			buttonLayout.setGravity(Gravity.CENTER_VERTICAL);
			buttonLayout.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, 40));			
			time = new TextView(Activa.myApp);
			time.setText(String.format("%02d:00", date.getHours()));
			time.setPadding(5, 10, 5, 10);
			time.setTextSize(20);
			time.setTextColor(Color.BLACK);
			time.setTypeface(Typeface.SANS_SERIF);
			buttonLayout.addView(time);
			enumeration = Activa.myCalendarManager.events.keys();
			while (enumeration.hasMoreElements()) {
				final String id = enumeration.nextElement();
				event = Activa.myCalendarManager.events.get(id);
				if (date.compareTo(event.getDate()) == 0) {
					TextView activity = new TextView(Activa.myApp);
					activity.setText(event.name);
					activity.setPadding(5, 10, 5, 10);
					activity.setTextSize(20);
					activity.setTextColor(Color.BLACK);
					activity.setTypeface(Typeface.SANS_SERIF);
					ImageView button = new ImageView(Activa.myApp);
					switch (event.state) {
						case 0:
							button.setBackgroundResource(R.drawable.oksmall);
							break;
						case 1:
							button.setBackgroundResource(R.drawable.nosmall);
							break;
						case 2:
							button.setBackgroundResource(R.drawable.gosmall);
							break;
					}
					
					buttonLayout.addView(activity);
					buttonLayout.addView(button);	
					time.setOnClickListener(new OnClickListener() {				
						@Override
						public void onClick(View v) {
							Activa.myCalendarManager.events.get(id).doActivity();
						}
					});	
					activity.setOnClickListener(new OnClickListener() {				
						@Override
						public void onClick(View v) {
							Activa.myCalendarManager.events.get(id).doActivity();
						}
					});
					button.setOnClickListener(new OnClickListener() {				
						@Override
						public void onClick(View v) {
							Activa.myCalendarManager.events.get(id).doActivity();
						}
					});	
				}
				else if ((date.compareTo(event.getDate()) < 0)&&(dateLater.compareTo(event.getDate()) > 0)) {
					schedule.addView(buttonLayout);
					View separator = new View(Activa.myApp);
					LayoutParams separatorLayout = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, 1);
					separator.setLayoutParams(separatorLayout);
					separator.setBackgroundColor(Color.BLACK);
					schedule.addView(separator);
					buttonLayout = new TableRow(Activa.myApp);
					buttonLayout.setOrientation(TableRow.HORIZONTAL);
					buttonLayout.setGravity(Gravity.CENTER_VERTICAL);
					buttonLayout.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, 40));			
					time = new TextView(Activa.myApp);
					time.setPadding(5, 10, 5, 10);
					time.setTextSize(20);
					time.setTextColor(Color.BLACK);
					time.setTypeface(Typeface.SANS_SERIF);
					buttonLayout.addView(time);
					time.setText(String.format("%02d:%02d", event.date.getHours(), event.date.getMinutes()));
					TextView activity = new TextView(Activa.myApp);
					activity.setText(event.name);
					activity.setPadding(5, 10, 5, 10);
					activity.setTextSize(20);
					activity.setTextColor(Color.BLACK);
					activity.setTypeface(Typeface.SANS_SERIF);
					ImageView button = new ImageView(Activa.myApp);
					switch (event.state) {
						case 0:
							button.setBackgroundResource(R.drawable.oksmall);
							break;
						case 1:
							button.setBackgroundResource(R.drawable.nosmall);
							break;
						case 2:
							button.setBackgroundResource(R.drawable.gosmall);
							break;
					}
					
					buttonLayout.addView(activity);
					buttonLayout.addView(button);	
					time.setOnClickListener(new OnClickListener() {				
						@Override
						public void onClick(View v) {
							Activa.myCalendarManager.events.get(id).doActivity();
						}
					});	
					activity.setOnClickListener(new OnClickListener() {				
						@Override
						public void onClick(View v) {
							Activa.myCalendarManager.events.get(id).doActivity();
						}
					});
					button.setOnClickListener(new OnClickListener() {				
						@Override
						public void onClick(View v) {
							Activa.myCalendarManager.events.get(id).doActivity();
						}
					});	
				}
			}
			schedule.addView(buttonLayout);
			View separator = new View(Activa.myApp);
			LayoutParams separatorLayout = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, 1);
			separator.setLayoutParams(separatorLayout);
			separator.setBackgroundColor(Color.BLACK);
			
			schedule.addView(separator);

			date.setHours(date.getHours() + 1);
			dateLater.setHours(dateLater.getHours() + 1);
		}
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadMainScreen(false);
			}
		});
	}*/
	
	public void loadScheduleDay(final Date dategiven) {
		TextView time;
		Enumeration<String> enumeration;
		Event event = null; 
		Date date = new Date((dategiven.getTime()/3600000)*3600000);
		date.setHours(0);
		Date dateLater = new Date(date.getTime());
		dateLater.setHours(dateLater.getHours() + 1);
		TableRow buttonLayout;
		this.state = UI_STATE_SCHEDULE;
		if (Activa.myMenu != null) {
			Activa.myMenu.clear();
			Activa.myInflater.inflate(R.menu.schedule, Activa.myMenu);
		}
		Hashtable<Date, Event> eventsOrdered = new Hashtable<Date,Event>();
		Vector<Date> datesOrdered = new Vector<Date>();
		Enumeration<Event> enumer = Activa.myCalendarManager.events.elements();
		while (enumer.hasMoreElements()) {
			Event temp = enumer.nextElement();
			if (datesOrdered.contains(temp.date)) temp.date.setTime(temp.date.getTime() + 1);
			datesOrdered.add(temp.date);
			eventsOrdered.put(temp.date, temp);
		}
		Collections.sort(datesOrdered);
		Activa.myApp.setContentView(R.layout.scheduleday);
		((TextView) Activa.myApp.findViewById(R.id.startText)).setText(Activa.myLanguageManager.CALENDAR_TITLE);
		ImageButton back = (ImageButton) Activa.myApp.findViewById(R.id.previous);
		ImageButton prev = (ImageButton) Activa.myApp.findViewById(R.id.previousday);
		ImageButton next = (ImageButton) Activa.myApp.findViewById(R.id.nextday);
		TextView dateText = (TextView) Activa.myApp.findViewById(R.id.date);
		dateText.setText(ActivaUtil.dateToReadableString(date));
		TableLayout schedule = (TableLayout)Activa.myApp.findViewById(R.id.schedule);
		for (int i = 0; i < 24; i++) {	
			buttonLayout = new TableRow(Activa.myApp);
			buttonLayout.setOrientation(TableRow.HORIZONTAL);
			buttonLayout.setGravity(Gravity.CENTER_VERTICAL);
			buttonLayout.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, 40));			
			time = new TextView(Activa.myApp);
			time.setText(String.format("%02d:00", date.getHours()));
			time.setPadding(5, 10, 5, 10);
			time.setTextSize(20);
			time.setTextColor(Color.BLACK);
			time.setTypeface(Typeface.SANS_SERIF);
			buttonLayout.addView(time);
			for(int j = 0; j < datesOrdered.size(); j++) {
				event = eventsOrdered.get(datesOrdered.get(j));
				final String id = event.id;
				if (date.compareTo(event.getDate()) == 0) {
					TextView activity = new TextView(Activa.myApp);
					activity.setLayoutParams(new TableRow.LayoutParams(200, LayoutParams.WRAP_CONTENT));
					activity.setText(event.name);
					activity.setPadding(5, 10, 5, 10);
					activity.setTextSize(20);
					activity.setTextColor(Color.BLACK);
					activity.setTypeface(Typeface.SANS_SERIF);
					ImageView button = new ImageView(Activa.myApp);
					switch (event.state) {
						case 0:
							button.setBackgroundResource(R.drawable.oksmall);
							break;
						case 1:
							button.setBackgroundResource(R.drawable.cancel);
							break;
						case 2:
							button.setBackgroundResource(R.drawable.nextsmall);
							break;
					}
					
					buttonLayout.addView(activity);
					buttonLayout.addView(button);	
					time.setOnClickListener(new OnClickListener() {				
						@Override
						public void onClick(View v) {
							Activa.myCalendarManager.events.get(id).doActivity();
						}
					});	
					activity.setOnClickListener(new OnClickListener() {				
						@Override
						public void onClick(View v) {
							Activa.myCalendarManager.events.get(id).doActivity();
						}
					});
					button.setOnClickListener(new OnClickListener() {				
						@Override
						public void onClick(View v) {
							Activa.myCalendarManager.events.get(id).doActivity();
						}
					});	
					buttonLayout.setOnClickListener(new OnClickListener() {				
						@Override
						public void onClick(View v) {
							Activa.myCalendarManager.events.get(id).doActivity();
						}
					});
				}
				else if ((date.compareTo(event.getDate()) < 0)&&(dateLater.compareTo(event.getDate()) > 0)) {
					schedule.addView(buttonLayout);
					View separator = new View(Activa.myApp);
					LayoutParams separatorLayout = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, 1);
					separator.setLayoutParams(separatorLayout);
					separator.setBackgroundColor(Color.BLACK);
					schedule.addView(separator);
					buttonLayout = new TableRow(Activa.myApp);
					buttonLayout.setOrientation(TableRow.HORIZONTAL);
					buttonLayout.setGravity(Gravity.CENTER_VERTICAL);
					buttonLayout.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, 40));			
					time = new TextView(Activa.myApp);
					time.setPadding(5, 10, 5, 10);
					time.setTextSize(20);
					time.setTextColor(Color.BLACK);
					time.setTypeface(Typeface.SANS_SERIF);
					buttonLayout.addView(time);
					time.setText(String.format("%02d:%02d", event.date.getHours(), event.date.getMinutes()));
					TextView activity = new TextView(Activa.myApp);
					activity.setLayoutParams(new TableRow.LayoutParams(200, LayoutParams.WRAP_CONTENT));
					activity.setText(event.name);
					activity.setWidth(200);
					activity.setPadding(5, 10, 5, 10);
					activity.setTextSize(20);
					activity.setTextColor(Color.BLACK);
					activity.setTypeface(Typeface.SANS_SERIF);
					ImageView button = new ImageView(Activa.myApp);
					switch (event.state) {
						case 0:
							button.setBackgroundResource(R.drawable.oksmall);
							break;
						case 1:
							button.setBackgroundResource(R.drawable.cancel);
							break;
						case 2:
							button.setBackgroundResource(R.drawable.nextsmall);
							break;
					}
					
					buttonLayout.addView(activity);
					buttonLayout.addView(button);	
					time.setOnClickListener(new OnClickListener() {				
						@Override
						public void onClick(View v) {
							Activa.myCalendarManager.events.get(id).doActivity();
						}
					});	
					activity.setOnClickListener(new OnClickListener() {				
						@Override
						public void onClick(View v) {
							Activa.myCalendarManager.events.get(id).doActivity();
						}
					});
					button.setOnClickListener(new OnClickListener() {				
						@Override
						public void onClick(View v) {
							Activa.myCalendarManager.events.get(id).doActivity();
						}
					});	
					buttonLayout.setOnClickListener(new OnClickListener() {				
						@Override
						public void onClick(View v) {
							Activa.myCalendarManager.events.get(id).doActivity();
						}
					});	
				}
			}
			schedule.addView(buttonLayout);
			View separator = new View(Activa.myApp);
			LayoutParams separatorLayout = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, 1);
			separator.setLayoutParams(separatorLayout);
			separator.setBackgroundColor(Color.BLACK);
			
			schedule.addView(separator);

			date.setHours(date.getHours() + 1);
			dateLater.setHours(dateLater.getHours() + 1);
		}
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadMainScreen(false, false);
			}
		});
		prev.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Date nextDate = new Date(dategiven.getTime());
				nextDate.setDate(nextDate.getDate() - 1);
				Activa.myUIManager.loadScheduleDay(nextDate);
			}
		});

		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Date nextDate = new Date(dategiven.getTime());
				nextDate.setDate(nextDate.getDate() + 1);
				Activa.myUIManager.loadScheduleDay(nextDate);
			}
		});
	}
	
	public void loadScheduleWeek(final Date dategiven) {
		TextView time;
		Enumeration<String> enumeration;
		Event event = null; 
		Calendar cal = Calendar.getInstance();
		cal.setTime(dategiven);
		Date date = new Date((dategiven.getTime()/3600000)*3600000);
		date.setHours(0);
		int day = cal.get(Calendar.DAY_OF_WEEK)-2;
		if (day == -1) day = 6;
		date.setDate(date.getDate() - day);
		Date dateLater = new Date(date.getTime());
		dateLater.setDate(date.getDate() + 1);
		TableRow buttonLayout;
		this.state = UI_STATE_SCHEDULEWEEK;
		if (Activa.myMenu != null) {
			Activa.myMenu.clear();
			Activa.myInflater.inflate(R.menu.schedule, Activa.myMenu);
		}
		Hashtable<Date, Event> eventsOrdered = new Hashtable<Date,Event>();
		Vector<Date> datesOrdered = new Vector<Date>();
		Enumeration<Event> enumer = Activa.myCalendarManager.events.elements();
		while (enumer.hasMoreElements()) {
			Event temp = enumer.nextElement();
			if (datesOrdered.contains(temp.date)) temp.date.setTime(temp.date.getTime() + 1);
			datesOrdered.add(temp.date);
			eventsOrdered.put(temp.date, temp);
		}
		Collections.sort(datesOrdered);
		Activa.myApp.setContentView(R.layout.scheduleday);
		((TextView) Activa.myApp.findViewById(R.id.startText)).setText(Activa.myLanguageManager.CALENDAR_TITLE);
		ImageButton back = (ImageButton) Activa.myApp.findViewById(R.id.previous);
		ImageButton prev = (ImageButton) Activa.myApp.findViewById(R.id.previousday);
		ImageButton next = (ImageButton) Activa.myApp.findViewById(R.id.nextday);
		TextView dateText = (TextView) Activa.myApp.findViewById(R.id.date);
		dateText.setText(Activa.myLanguageManager.CALENDAR_WEEK + " " + cal.get(Calendar.WEEK_OF_YEAR) + " "+ Activa.myLanguageManager.CALENDAR_OF + " " + cal.get(Calendar.YEAR));
		TableLayout schedule = (TableLayout)Activa.myApp.findViewById(R.id.schedule);
		for (int i = 0; i < 7; i++) {	
			time = new TextView(Activa.myApp);	
			time.setText(ActivaUtil.dateToReadableString(date));
			time.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			time.setPadding(5, 10, 5, 10);
			time.setTypeface(Typeface.DEFAULT_BOLD);
			time.setTextSize(20);
			time.setGravity(Gravity.CENTER);
			time.setTextColor(Color.BLACK);
			schedule.addView(time);		
			View separator = new View(Activa.myApp);
			LayoutParams separatorLayout = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, 1);
			separator.setLayoutParams(separatorLayout);
			separator.setBackgroundColor(Color.BLACK);				
			schedule.addView(separator);
			for(int j = 0; j < datesOrdered.size(); j++) {
				event = eventsOrdered.get(datesOrdered.get(j));
				final String id = event.id;
				if ((date.compareTo(event.getDate()) <= 0)&&(dateLater.compareTo(event.getDate()) > 0)) {
					buttonLayout = new TableRow(Activa.myApp);
					buttonLayout.setOrientation(TableRow.HORIZONTAL);
					buttonLayout.setGravity(Gravity.CENTER_VERTICAL);
					buttonLayout.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, 40));			
					time = new TextView(Activa.myApp);
					time.setPadding(5, 10, 5, 10);
					time.setTextSize(20);
					time.setTextColor(Color.BLACK);
					time.setTypeface(Typeface.SANS_SERIF);
					time.setText(String.format("%02d:%02d", event.date.getHours(), event.date.getMinutes()));
					buttonLayout.addView(time);
					TextView activity = new TextView(Activa.myApp);
					activity.setLayoutParams(new TableRow.LayoutParams(200, LayoutParams.WRAP_CONTENT));
					activity.setText(event.name);
					activity.setPadding(5, 10, 5, 10);
					activity.setTextSize(20);
					activity.setTextColor(Color.BLACK);
					activity.setTypeface(Typeface.SANS_SERIF);
					buttonLayout.addView(activity);
					ImageView button = new ImageView(Activa.myApp);
					switch (event.state) {
						case 0:
							button.setBackgroundResource(R.drawable.oksmall);
							break;
						case 1:
							button.setBackgroundResource(R.drawable.cancel);
							break;
						case 2:
							button.setBackgroundResource(R.drawable.nextsmall);
							break;
					}
					buttonLayout.addView(button);	
					schedule.addView(buttonLayout);
					time.setOnClickListener(new OnClickListener() {				
						@Override
						public void onClick(View v) {
							Activa.myCalendarManager.events.get(id).doActivity();
						}
					});	
					activity.setOnClickListener(new OnClickListener() {				
						@Override
						public void onClick(View v) {
							Activa.myCalendarManager.events.get(id).doActivity();
						}
					});
					button.setOnClickListener(new OnClickListener() {				
						@Override
						public void onClick(View v) {
							Activa.myCalendarManager.events.get(id).doActivity();
						}
					});	
					buttonLayout.setOnClickListener(new OnClickListener() {				
						@Override
						public void onClick(View v) {
							Activa.myCalendarManager.events.get(id).doActivity();
						}
					});	
					separator = new View(Activa.myApp);
					separatorLayout = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, 1);
					separator.setLayoutParams(separatorLayout);
					separator.setBackgroundColor(Color.BLACK);
					schedule.addView(separator);
				}
			}
			date.setDate(date.getDate() + 1);
			dateLater.setDate(dateLater.getDate() + 1);
		}
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadMainScreen(false, false);
			}
		});
		prev.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Date nextDate = new Date(dategiven.getTime());
				nextDate.setDate(nextDate.getDate() - 7);
				Activa.myUIManager.loadScheduleWeek(nextDate);
			}
		});
		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Date nextDate = new Date(dategiven.getTime());
				nextDate.setDate(nextDate.getDate() + 7);
				Activa.myUIManager.loadScheduleWeek(nextDate);
			}
		});
	}
	
	public void loadScheduleMonth(final Date dategiven) {
		TextView time;
		View separator;
		LayoutParams separatorLayout;
		TableRow weekLayout;
		int days;
		Enumeration<Event> enumeration;
		Event event = null; 
		Date date = new Date((dategiven.getTime()/3600000)*3600000);
		date.setHours(0);
		date.setDate(1);
		Date dateLater = new Date(date.getTime());
		dateLater.setDate(2);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int weekday = cal.get(Calendar.DAY_OF_WEEK)-2;
		if (weekday == -1) weekday = 6;
		Date dateLimit = new Date(date.getTime());
		dateLimit.setMonth(date.getMonth() + 1);
		this.state = UI_STATE_SCHEDULEMONTH;
		if (Activa.myMenu != null) {
			Activa.myMenu.clear();
			Activa.myInflater.inflate(R.menu.schedule, Activa.myMenu);
		}
		Activa.myApp.setContentView(R.layout.schedulemonth);
		((TextView) Activa.myApp.findViewById(R.id.startText)).setText(Activa.myLanguageManager.CALENDAR_TITLE);
		ImageButton back = (ImageButton) Activa.myApp.findViewById(R.id.previous);
		ImageButton prev = (ImageButton) Activa.myApp.findViewById(R.id.previousday);
		ImageButton next = (ImageButton) Activa.myApp.findViewById(R.id.nextday);
		TextView dateText = (TextView) Activa.myApp.findViewById(R.id.date);
		dateText.setText(ActivaUtil.monthOfDate(date) + " " + cal.get(Calendar.YEAR));
		TableLayout schedule = (TableLayout)Activa.myApp.findViewById(R.id.schedule);
		weekLayout = new TableRow(Activa.myApp);
		weekLayout.setOrientation(TableRow.HORIZONTAL);
		weekLayout.setGravity(Gravity.CENTER_VERTICAL);
		weekLayout.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		separator = new View(Activa.myApp);
		separator.setLayoutParams(new TableRow.LayoutParams(1, 36));
		separator.setBackgroundColor(Color.BLACK);
		weekLayout.addView(separator);
		for (int i = 0; i < weekday; i++) {		
			View space = new View(Activa.myApp);
			space.setLayoutParams(new TableRow.LayoutParams(36, 36));	
			weekLayout.addView(space);
		}
		while(date.before(dateLimit)) {	
			time = new TextView(Activa.myApp);
			time.setGravity(Gravity.CENTER);
			time.setTextSize(20);
			time.setText("" + date.getDate());
			time.setTag(date.getDate());
			time.setTypeface(Typeface.SANS_SERIF);
			time.setLayoutParams(new TableRow.LayoutParams(36, 36));	
			int state = 3;
			enumeration = Activa.myCalendarManager.events.elements();
			while (enumeration.hasMoreElements()) {
				event = enumeration.nextElement();
				if ((date.compareTo(event.getDate()) <= 0)&&(dateLater.compareTo(event.getDate()) > 0)) {
					if ((event.state == 0)&&(state == 3)) {
						state = 0;
						continue;
					}
					if ((event.state == 2)&&((state == 3)||(state == 0))) {
						state = 2;
						continue;
					}
					if ((event.state == 1)&&((state == 3)||(state == 0)||(state == 2))) {
						state = 1;
						break;
					}
				}
			}
			switch (state) {
				case 0:
					time.setTextColor(Color.GREEN);
					break;
				case 1:
					time.setTextColor(Color.RED);
					break;
				case 2:
					time.setTextColor(Color.parseColor("#ffad00"));
					break;
				case 3:
					time.setTextColor(Color.BLACK);
					break;
			}
			time.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Date newDate = new Date(dategiven.getTime());
					newDate.setDate((Integer)v.getTag());
					Activa.myUIManager.loadScheduleDay(newDate);
				}
			});
			weekLayout.addView(time);
			date.setDate(date.getDate() + 1);
			dateLater.setDate(dateLater.getDate() + 1);
			weekday++;
			if (weekday == 7) {		
				separator = new View(Activa.myApp);
				separator.setLayoutParams(new TableRow.LayoutParams(1, 36));
				separator.setBackgroundColor(Color.BLACK);
				weekLayout.addView(separator);
				schedule.addView(weekLayout);	
				separator = new View(Activa.myApp);
				separatorLayout = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, 1);
				separator.setLayoutParams(separatorLayout);
				separator.setBackgroundColor(Color.BLACK);
				schedule.addView(separator);	
				weekday = 0;
				weekLayout = new TableRow(Activa.myApp);
				weekLayout.setOrientation(TableRow.HORIZONTAL);
				weekLayout.setGravity(Gravity.CENTER_VERTICAL);
				weekLayout.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				separator = new View(Activa.myApp);
				separator.setLayoutParams(new TableRow.LayoutParams(1, 36));
				separator.setBackgroundColor(Color.BLACK);
				weekLayout.addView(separator);
			}
		}
		for (int i = weekday; i < 7; i++) {	
			time = new TextView(Activa.myApp);
			time.setGravity(Gravity.CENTER);
			time.setTextSize(20);
			time.setTextColor(Color.BLACK);
			time.setTypeface(Typeface.SANS_SERIF);
			time.setLayoutParams(new TableRow.LayoutParams(36, 36));	
			weekLayout.addView(time);
		}	
		separator = new View(Activa.myApp);
		separator.setLayoutParams(new TableRow.LayoutParams(1, 36));
		separator.setBackgroundColor(Color.BLACK);
		weekLayout.addView(separator);
		schedule.addView(weekLayout);
		separator = new View(Activa.myApp);
		separatorLayout = new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, 1);
		separator.setLayoutParams(separatorLayout);
		separator.setBackgroundColor(Color.BLACK);
		schedule.addView(separator);	
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadMainScreen(false, false);
			}
		});
		prev.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Date nextDate = new Date(dategiven.getTime());
				nextDate.setMonth(nextDate.getMonth() - 1);
				Activa.myUIManager.loadScheduleMonth(nextDate);
			}
		});

		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Date nextDate = new Date(dategiven.getTime());
				nextDate.setMonth(nextDate.getMonth() + 1);
				Activa.myUIManager.loadScheduleMonth(nextDate);
			}
		});
	}
	
	
	public void loadProgramList() {
		this.state = UI_STATE_TOTALPROGRAM;
		Activa.myApp.setContentView(R.layout.list);
		((TextView) Activa.myApp.findViewById(R.id.startText)).append(Activa.myLanguageManager.PROGRAMS_TITLE);
		TableLayout programlisting = (TableLayout)Activa.myApp.findViewById(R.id.listing);
		Enumeration<Program> enumer = Activa.myProgramManager.programList.elements();
		while (enumer.hasMoreElements()) {
			Program program = enumer.nextElement();
			TableRow buttonLayout = new TableRow(Activa.myApp);
			buttonLayout.setId(program.id);
			buttonLayout.setOrientation(TableRow.HORIZONTAL);
			buttonLayout.setGravity(Gravity.CENTER_VERTICAL);
			buttonLayout.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));			
			ImageButton button = new ImageButton(Activa.myApp);
			button.setBackgroundResource(R.drawable.iconbg);
			button.setImageResource(program.icon);
			button.setId(program.id);
			OnClickListener listener = new OnClickListener() {			
				@Override
				public void onClick(View v) {
					Activa.myProgramManager.eventAssociated = null;
					Activa.mySensorManager.eventAssociated = null;
					loadProgram(Activa.myProgramManager.programList.get(v.getId()));
				}
			};
			button.setOnClickListener(listener);
			TextView text = new TextView(Activa.myApp);
			text.append(program.name);
			text.setTextSize((float) 20);
			text.setTextColor(Color.BLACK);
			text.setTypeface(Typeface.SANS_SERIF);
			text.setOnClickListener(listener);
			text.setId(program.id);
			buttonLayout.addView(button);
			buttonLayout.addView(text);
			buttonLayout.setOnClickListener(listener);
			button.setOnClickListener(listener);
			text.setOnClickListener(listener);
			programlisting.addView(buttonLayout);
		}
		ImageButton back = (ImageButton) Activa.myApp.findViewById(R.id.previous);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadMainScreen(false, false);
			}
		});
	}

	public void loadProgram(final Program program) {
		this.state = UI_STATE_PROGRAM;
		Activa.myApp.setContentView(R.layout.follow);
		TextView text = (TextView) Activa.myApp.findViewById(R.id.textInfo);
		text.setTextSize(19);
		text.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
		text.append(program.description);
		ImageButton appLayout = (ImageButton) Activa.myApp.findViewById(R.id.follow);
		appLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				program.startProgram();
			}
		});		
	}
	
	/*public void Program05(final String program) {

		this.state = UI_STATE_PROGRAM;
		Activa.myApp.setContentView(R.layout.info);
		TextView text = (TextView) Activa.myApp.findViewById(R.id.textInfo);
		text.append("Iniciar ejercicio ...");
		View appLayout = (View) Activa.myApp.findViewById(R.id.mainLayout);
		appLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
//				Activa.myUIManager.Program(program);
				Activa.myUIManager.state = UI_STATE_MEASURING;
				Activa.myApp.setContentView(R.layout.waiting);
				TextView title = (TextView) Activa.myApp.findViewById(R.id.startText);
				title.append(program);
				Activa.mySensorManager.eventAssociated = null;
				TextView text = (TextView) Activa.myApp.findViewById(R.id.infoText);
				text.append("Descubriendo el sensor ...");
				LinearLayout lay = (LinearLayout) Activa.myApp.findViewById(R.id.stoplay);
				lay.setVisibility(View.INVISIBLE);
				AnimSensoringTimeTask timer = new AnimSensoringTimeTask(2000, 125, "Sensor Zephyr", 1);
				timer.start();	
				return false;
			}
		});	
	}*/

	/*public void Program(String program) {
		this.state = UI_STATE_MEASURING;
		Activa.myApp.setContentView(R.layout.waiting);
		TextView title = (TextView) Activa.myApp.findViewById(R.id.startText);
		title.append(program);
		TextView text = (TextView) Activa.myApp.findViewById(R.id.infoText);
		text.append("Descubriendo el sensor ...");
		LinearLayout lay = (LinearLayout) Activa.myApp.findViewById(R.id.stoplay);
		lay.setVisibility(View.INVISIBLE);
		Activa.mySensorManager.eventAssociated = null;
		AnimSensoringTimeTask timer = new AnimSensoringTimeTask(2000, 125, "Sensor Zephyr", 1);
		timer.start();		
	}*/
	
	/*public void loadSensorResult2(String result, String measurement) {
		Activa.myApp.setContentView(R.layout.result);
		TextView text = (TextView) Activa.myApp.findViewById(R.id.textInfo);
		TextView resultText = (TextView) Activa.myApp.findViewById(R.id.result);
		text.append("La medicion de " + measurement + " es de ...");
		resultText.append(result);
		WaitTimeTask timer = new WaitTimeTask(3000, 1000, 1);
		timer.start();
	}*/
	
	/*public void Program2() {
		Activa.myQuestManager.partOfAProgram = true;
		Activa.myQuestManager.activeQuestionnaireId = 2;
		Activa.myQuestManager.questionnaireSet.get(Activa.myQuestManager.activeQuestionnaireId).start();
	}*/
	
	/*public void Program3() {
		this.state = UI_STATE_PROGRAM;
		Activa.myApp.setContentView(R.layout.info);
		TextView text = (TextView) Activa.myApp.findViewById(R.id.textInfo);
		text.append("Programa finalizado completamente.");
		BeginTimeTask timer = new BeginTimeTask(2000, 1000);
		timer.start();			
	}*/
	
	public void finishProgram() {
		this.state = UI_STATE_PROGRAM;
		Activa.myApp.setContentView(R.layout.info);
		TextView text = (TextView) Activa.myApp.findViewById(R.id.textInfo);
		text.append(Activa.myLanguageManager.PROGRAMS_FINISH);
		CountDownTimer timer = new CountDownTimer(3000,1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onFinish() {
				if (Activa.myProgramManager.eventAssociated != null) loadScheduleDay(Activa.myProgramManager.eventAssociated.date);
				else loadProgramList();
			}
		};
//		BeginTimeTask timer = new BeginTimeTask(3000, 1000);
		timer.start();			
	}
	
	public void loadQuestResult2(float result) {
		Activa.myApp.setContentView(R.layout.resultimage);
		TextView text = (TextView) Activa.myApp.findViewById(R.id.textInfo);
		if (result < 0.0f) text.setText(Activa.myLanguageManager.QUEST_WEIGHT_NOTDONE);
		else text.setText(Activa.myLanguageManager.QUEST_WEIGHT_DONE + String.format("%.1f", result) + " %. ");
		ImageView resultText = (ImageView) Activa.myApp.findViewById(R.id.result);
		if (result < 0.0) {
		}
		else if (result < 33.33) {
			text.append(Activa.myLanguageManager.QUEST_WEIGHT_0);
			resultText.setBackgroundResource(R.drawable.lightgreen);
		}
		else if (result < 66.66) {
			text.append(Activa.myLanguageManager.QUEST_WEIGHT_1);
			resultText.setBackgroundResource(R.drawable.lightorange);
		}
		else {
			text.append(Activa.myLanguageManager.QUEST_WEIGHT_2);
			resultText.setBackgroundResource(R.drawable.lightwhite);
		}
		CountDownTimer timer = new CountDownTimer(3000,1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onFinish() {
				if (Activa.myProgramManager.eventAssociated != null) loadScheduleDay(Activa.myProgramManager.eventAssociated.date);
				else loadProgramList();
			}
		};
//		WaitTimeTask timer = new WaitTimeTask(3000, 1000, 2);
		timer.start();
	}
	
	/*public void sensoring (String sensor, Event event) {
		Activa.mySensorManager.eventAssociated = event;
		AnimSensorTimeTask timer = new AnimSensorTimeTask(2000, 125, sensor);
		timer.start();
	}*/

	public void finishSensorMeasurement(String sensorString, boolean outcome, final Sensor sensor) {
//        Activa.myBluetoothAdapter.disable();
		if (outcome) {
			Activa.myApp.setContentView(R.layout.sensorresult);
			TextView textTitle = (TextView) Activa.myApp.findViewById(R.id.startText);
			TextView text = (TextView) Activa.myApp.findViewById(R.id.textResult);
			((TextView) Activa.myApp.findViewById(R.id.resultsWord)).setText(Activa.myLanguageManager.SENSORS_RESULTSWORD);
			textTitle.setText(sensorString);
			text.setText("");
			Enumeration<Integer> keys = sensor.results.keys();
			while (keys.hasMoreElements()) {
				int key = keys.nextElement();
				String meas = SensorManager.getMeasurementName(key);
				if (meas != null) {
					text.append(meas + ": " + String.format("%.1f", (float)sensor.results.get(key)));
					text.append(" " + SensorManager.getUnitForMeasurement(SensorManager.getUnitIDForMeasurementID(key)));
					if (keys.hasMoreElements()) text.append("\n");
					if (key > 1000) sensor.results.remove(key);
				}
			}
			CountDownTimer timer = new CountDownTimer(6000, 1000) {
				@Override
				public void onTick(long millisUntilFinished) {}
				@Override
				public void onFinish() {
//					Activa.myApp.setContentView(R.layout.sending);
//					SendSensorResult sending = new SendSensorResult(sensor);
//					Thread thread = new Thread(sending, "SENDQUESTIONNAIRE");
//					thread.start();
//					if (sensor.bluetoothPreviouslyConnected) 
//						Activa.myBluetoothAdapter.enable();
					String result = sensor.getSensorGlobalResult();
					int resultInt = Integer.parseInt(result.substring(0, 1));
					String resultString = result.substring(2);
					loadSensorGlobalResult(resultString, resultInt, sensor);
				}
			};
			timer.start();
		}
		else {
			Activa.myApp.setContentView(R.layout.info);
			TextView text = (TextView) Activa.myApp.findViewById(R.id.textInfo);
			text.setText(Activa.myLanguageManager.SENSORS_CANCELLED);
			CountDownTimer timer = new CountDownTimer(6000, 1000) {
				@Override
				public void onTick(long millisUntilFinished) {}
				@Override
				public void onFinish() {
//					if (sensor.bluetoothPreviouslyConnected) 
//						Activa.myBluetoothAdapter.enable();
					Activa.myUIManager.loadMainScreen(false, false);
				}
			};
			timer.start();
		}
	}
	
	public void finishSensorMeasurement(String sensorString, boolean outcome, final Sensor sensor, int[] order) {
//      Activa.myBluetoothAdapter.disable();
		if (outcome) {
			Activa.myApp.setContentView(R.layout.sensorresult);
			TextView textTitle = (TextView) Activa.myApp.findViewById(R.id.startText);
			TextView text = (TextView) Activa.myApp.findViewById(R.id.textResult);
			((TextView) Activa.myApp.findViewById(R.id.resultsWord)).setText(Activa.myLanguageManager.SENSORS_RESULTSWORD);
			textTitle.setText(sensorString);
			text.setText("");
			for (int i=0; i < order.length; i++) {
				int key = order[i];
				if (key == -1) {
					text.append("\n");
					continue;
				}
				String meas = SensorManager.getMeasurementName(key);
				if (meas != null) {
					if ((SensorManager.getUnitIDForMeasurementID(key) != SensorManager.UNIT_SEC)&&(SensorManager.getUnitIDForMeasurementID(key) != SensorManager.UNIT_NULL)) text.append(meas + ": " + String.format("%.1f", (float)sensor.results.get(key)));
					else text.append(meas + ": " + Math.round((float)sensor.results.get(key)));
					text.append(" " + SensorManager.getUnitForMeasurement(SensorManager.getUnitIDForMeasurementID(key)));
					if (i != (order.length - 1)) text.append("\n");
					if (key > 1000) sensor.results.remove(key);
				}
			}
			CountDownTimer timer = new CountDownTimer(6000, 1000) {
				@Override
				public void onTick(long millisUntilFinished) {}
				@Override
				public void onFinish() {
//					Activa.myApp.setContentView(R.layout.sending);
//					SendSensorResult sending = new SendSensorResult(sensor);
//					Thread thread = new Thread(sending, "SENDQUESTIONNAIRE");
//					thread.start();
//					if (sensor.bluetoothPreviouslyConnected) 
//						Activa.myBluetoothAdapter.enable();
					String result = sensor.getSensorGlobalResult();
					int resultInt = Integer.parseInt(result.substring(0, 1));
					String resultString = result.substring(2);
					loadSensorGlobalResult(resultString, resultInt, sensor);
				}
			};
			timer.start();
		}
		else {
			Activa.myApp.setContentView(R.layout.info);
			TextView text = (TextView) Activa.myApp.findViewById(R.id.textInfo);
			text.setText(Activa.myLanguageManager.SENSORS_CANCELLED);
			CountDownTimer timer = new CountDownTimer(6000, 1000) {
				@Override
				public void onTick(long millisUntilFinished) {}
				@Override
				public void onFinish() {
//					if (sensor.bluetoothPreviouslyConnected) 
//						Activa.myBluetoothAdapter.enable();
					Activa.myUIManager.loadMainScreen(false, false);
				}
			};
			timer.start();
		}
	}
	
	public void finishSpirometry(String sensorString, boolean outcome, final Spirometer sensor) {
//        Activa.myBluetoothAdapter.disable();
		if (outcome) {
			Activa.myApp.setContentView(R.layout.sensorresultwithgraph);
			TextView textTitle = (TextView) Activa.myApp.findViewById(R.id.startText);
			TextView text = (TextView) Activa.myApp.findViewById(R.id.textResult);
			((TextView) Activa.myApp.findViewById(R.id.resultsWord)).setText(Activa.myLanguageManager.SENSORS_RESULTSWORD);
			textTitle.setText(sensorString);
			text.setText("");
			Enumeration<Integer> keys = sensor.results.keys();
			while (keys.hasMoreElements()) {
				int key = keys.nextElement();
				String meas = SensorManager.getMeasurementName(key);
				text.append(meas + ": " + String.format("%.2f", (float)sensor.results.get(key)));
				text.append(" " + SensorManager.getUnitForMeasurement(SensorManager.getUnitIDForMeasurementID(key)));
				if (keys.hasMoreElements()) text.append("\n");
			}
			// Draw the spirometry graph
			final FrameLayout board = (FrameLayout) Activa.myApp.findViewById(R.id.graph);
			board.addView(new SpirometryGraphView(Activa.myApp, sensor));
			// A count down
			CountDownTimer timer = new CountDownTimer(12000, 1000) {
				@Override
				public void onTick(long millisUntilFinished) {}
				@Override
				public void onFinish() {
//					Activa.myApp.setContentView(R.layout.sending);
//					SendSensorResult sending = new SendSensorResult(sensor);
//					Thread thread = new Thread(sending, "SENDQUESTIONNAIRE");
//					thread.start();
//					if (sensor.bluetoothPreviouslyConnected) 
//						Activa.myBluetoothAdapter.enable();
					String result = sensor.getSensorGlobalResult();
					int resultInt = Integer.parseInt(result.substring(0, 1));
					String resultString = result.substring(2);
					loadSensorGlobalResult(resultString, resultInt, sensor);
				}
			};
			timer.start();
		}
		else {
			Activa.myApp.setContentView(R.layout.info);
			TextView text = (TextView) Activa.myApp.findViewById(R.id.textInfo);
			text.setText(Activa.myLanguageManager.SENSORS_CANCELLED);
			CountDownTimer timer = new CountDownTimer(6000, 1000) {
				@Override
				public void onTick(long millisUntilFinished) {}
				@Override
				public void onFinish() {
//					if (sensor.bluetoothPreviouslyConnected) 
//						Activa.myBluetoothAdapter.enable();
					Activa.myUIManager.loadMainScreen(false, false);
				}
			};
			timer.start();
		}
	}
	
	public void finishExercise(boolean outcome, final Sensor sensor) {
		if (outcome) {
			Activa.myApp.setContentView(R.layout.sensorresult);
			TextView textTitle = (TextView) Activa.myApp.findViewById(R.id.startText);
			TextView text = (TextView) Activa.myApp.findViewById(R.id.textResult);
			textTitle.setText(Activa.myLanguageManager.PROGRAMS_EXERCISE_TITLE);
			text.setText("");
			Enumeration<Integer> keys = sensor.results.keys();
			while (keys.hasMoreElements()) {
				int key = keys.nextElement();
				String meas = SensorManager.getMeasurementName(key);
				text.append(meas + ": " + String.format("%.1f", (float)sensor.results.get(key)));
				text.append(" " + SensorManager.getUnitForMeasurement(SensorManager.getUnitIDForMeasurementID(key)));
				if (keys.hasMoreElements()) text.append("\n");
			}
			CountDownTimer timer = new CountDownTimer(6000, 1000) {
				@Override
				public void onTick(long millisUntilFinished) {}
				@Override
				public void onFinish() {
//					Activa.myApp.setContentView(R.layout.sending);
//					SendSensorResult sending = new SendSensorResult(sensor);
//					Thread thread = new Thread(sending, "SENDQUESTIONNAIRE");
//					thread.start();
					String result = ((PulseOximeter)sensor).exercise.getSensorGlobalResult();
					int resultInt = Integer.parseInt(result.substring(0, 1));
					String resultString = result.substring(2);
					loadExerciseGlobalResult(resultString, resultInt, sensor);
				}
			};
			timer.start();
		}
		else {
			Activa.myApp.setContentView(R.layout.info);
			TextView text = (TextView) Activa.myApp.findViewById(R.id.textInfo);
			text.setText(Activa.myLanguageManager.PROGRAMS_EXERCISE_CANCELLED);
			CountDownTimer timer = new CountDownTimer(6000, 1000) {
				@Override
				public void onTick(long millisUntilFinished) {}
				@Override
				public void onFinish() {
					Activa.myUIManager.loadMainScreen(false, false);
				}
			};
			timer.start();
		}
	}
	
	public void loadSensorGlobalResult (String result, int resultInt, final Sensor sensor) {
		Activa.myApp.setContentView(R.layout.resultimage);
		TextView text = (TextView) Activa.myApp.findViewById(R.id.textInfo);
		text.setText(result);
		if (result.length() > 60) text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18.0f); 
		ImageView image = (ImageView) Activa.myApp.findViewById(R.id.result);
		switch (resultInt) {
			case 2:
				image.setBackgroundResource(R.drawable.lightgreen);
				break;
			case 1:
				image.setBackgroundResource(R.drawable.lightorange);
				break;
			case 0:
				image.setBackgroundResource(R.drawable.lightwhite);
				break;
		}
		CountDownTimer timer = new CountDownTimer(4000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {}
			@Override
			public void onFinish() {
				if (Activa.mySensorManager.programassociated != null) {
					Activa.mySensorManager.programassociated.nextStep();
				}
				else {
					Activa.myApp.setContentView(R.layout.sending);
					SendSensorResult sending = new SendSensorResult(sensor);
					Thread thread = new Thread(sending, "SENDSENSOR");
					thread.start();
				}
			}
		};
		timer.start();
	}
	
	public void loadExerciseGlobalResult (String result, int resultInt, final Sensor sensor) {
		Activa.myApp.setContentView(R.layout.resultimage);
		TextView text = (TextView) Activa.myApp.findViewById(R.id.textInfo);
		text.setText(result);
		if (result.length() > 60) text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18.0f); 
		ImageView image = (ImageView) Activa.myApp.findViewById(R.id.result);
		switch (resultInt) {
			case 2:
				image.setBackgroundResource(R.drawable.lightgreen);
				break;
			case 1:
				image.setBackgroundResource(R.drawable.lightorange);
				break;
			case 0:
				image.setBackgroundResource(R.drawable.lightwhite);
				break;
		}
		CountDownTimer timer = new CountDownTimer(4000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {}
			@Override
			public void onFinish() {
				Activa.myProgramManager.programList.get(ProgramManager.PROG_WALKING).nextStep();
			}
		};
		timer.start();
	}
	
	public void loadExerciseScreen (String resultGlobal, final long time) {
		this.state = UIManagerOLD.UI_STATE_EXERCISE;
		Activa.myApp.setContentView(R.layout.exercise);
		TextView hrtitle = (TextView) Activa.myApp.findViewById(R.id.hrTitle);
		TextView hrtext = (TextView) Activa.myApp.findViewById(R.id.hrText);
		TextView so2title = (TextView) Activa.myApp.findViewById(R.id.so2Title);
		TextView so2text = (TextView) Activa.myApp.findViewById(R.id.so2Text);
		TextView stepstitle = (TextView) Activa.myApp.findViewById(R.id.stepsTitle);
		TextView stepstext = (TextView) Activa.myApp.findViewById(R.id.stepsText);
		TextView global = (TextView) Activa.myApp.findViewById(R.id.textGlobal);
		hrtitle.setText(Activa.myLanguageManager.SENSORS_EXERCISE_HEARTRATE);
		so2title.setText(Activa.myLanguageManager.SENSORS_EXERCISE_O2SAT);
		stepstitle.setText(Activa.myLanguageManager.SENSORS_EXERCISE_STEPSTOP);
		hrtext.setText("-");
		so2text.setText("-");
		stepstext.setText("0/0");
		global.setText(resultGlobal);
		ImageView image = (ImageView) Activa.myApp.findViewById(R.id.resultgreen);
		image.setVisibility(View.VISIBLE);
		int seconds = (int) (time / 1000);
	    int minutes = seconds / 60;
	    seconds     = seconds % 60;
	    ((TextView) Activa.myApp.findViewById(R.id.timerText)).setText(String.format("%02d:%02d", minutes, seconds));			
	}
	
	public void updateExerciseScreen (int hr, int so2, int steps, int stops, String resultGlobal) {
		try {
			TextView global = (TextView) Activa.myApp.findViewById(R.id.textGlobal);
			int resultImage = Integer.parseInt(resultGlobal.substring(0,1));
			resultGlobal = resultGlobal.substring(2);
			global.setText(resultGlobal);
			TextView hrtext = (TextView) Activa.myApp.findViewById(R.id.hrText);
			TextView so2text = (TextView) Activa.myApp.findViewById(R.id.so2Text);
			TextView stepstext = (TextView) Activa.myApp.findViewById(R.id.stepsText);
			if (hr != 0) hrtext.setText(hr + " bpm");
			else hrtext.setText("-");
			if (so2 != 0) so2text.setText(so2 + " %");
			else so2text.setText("-");
			stepstext.setText(steps + "/" + stops);
			ImageView imagegreen = (ImageView) Activa.myApp.findViewById(R.id.resultgreen);
			ImageView imageorange = (ImageView) Activa.myApp.findViewById(R.id.resultorange);
			ImageView imagewhite = (ImageView) Activa.myApp.findViewById(R.id.resultwhite);
			switch (resultImage) {
				case 2:
					imagegreen.setVisibility(View.VISIBLE);
					imageorange.setVisibility(View.INVISIBLE);
					imagewhite.setVisibility(View.INVISIBLE);
					break;
				case 1:
					imagegreen.setVisibility(View.INVISIBLE);
					imageorange.setVisibility(View.VISIBLE);
					imagewhite.setVisibility(View.INVISIBLE);
					break;
				case 0:
					imagegreen.setVisibility(View.INVISIBLE);
					imageorange.setVisibility(View.INVISIBLE);
					imagewhite.setVisibility(View.VISIBLE);
					break;
			}
		} catch (NullPointerException e) {
		}
	}

	public void loadMessageList() {
		Activa.myUIManager.state = UIManagerOLD.UI_STATE_MESSAGE;
		Activa.myApp.setContentView(R.layout.messagelist);
		((TextView) Activa.myApp.findViewById(R.id.startText)).setText(Activa.myLanguageManager.MESSAGES_TITLE);
		Hashtable<Date, O2Message> messagesOrdered = new Hashtable<Date, O2Message>();
		Vector<Date> datesOrdered = new Vector<Date>();
		Enumeration<O2Message> enumer = Activa.myMessageManager.messageList.elements();
		while (enumer.hasMoreElements()) {
			O2Message temp = enumer.nextElement();
			if (datesOrdered.contains(temp.date)) temp.date.setTime(temp.date.getTime() + 1);
			datesOrdered.add(temp.date);
			messagesOrdered.put(temp.date, temp);
		}
		Collections.sort(datesOrdered);
		Collections.reverse(datesOrdered);
		Date today = new Date();
		today.setMinutes(0);
		today.setSeconds(0);
		today.setHours(0);
		Enumeration<Date> enumeration = datesOrdered.elements();
		TableLayout messagelist = (TableLayout)Activa.myApp.findViewById(R.id.listing);
		while (enumeration.hasMoreElements()) {
			final O2Message message = messagesOrdered.get(enumeration.nextElement());
			TableRow buttonLayout = new TableRow(Activa.myApp);
			buttonLayout.setOrientation(TableRow.HORIZONTAL);
			buttonLayout.setGravity(Gravity.CENTER_VERTICAL);
			buttonLayout.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));			
			TextView time = new TextView(Activa.myApp);	
			time.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			time.setPadding(5, 10, 5, 10);
			time.setTypeface(Typeface.DEFAULT_BOLD);
			time.setTextSize(20);
			time.setGravity(Gravity.LEFT);
			time.setTextColor(Color.BLACK);
			if (message.date.after(today)) time.setText(ActivaUtil.timeToReadableString(message.date));
			else time.setText(ActivaUtil.dateToReadableString(message.date));
			TextView topic = new TextView(Activa.myApp);	
			topic.setLayoutParams(new TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			topic.setPadding(5, 10, 5, 10);
			topic.setTypeface(Typeface.DEFAULT);
			topic.setTextSize(20);
			topic.setGravity(Gravity.LEFT);
			topic.setTextColor(Color.BLACK);
			String topicText = message.topic;
			if (topicText.length() > 20) topicText = topicText.substring(0, 17) + "...";
			topic.setText(topicText);
			OnClickListener listener = new OnClickListener() {			
				@Override
				public void onClick(View v) {
					Activa.myUIManager.showMessage(message);
				}
			};
			topic.setOnClickListener(listener);
			time.setOnClickListener(listener);
			buttonLayout.addView(topic);	
			buttonLayout.addView(time);		
			buttonLayout.setOnClickListener(listener);
			messagelist.addView(buttonLayout);
			View separator = new View(Activa.myApp);
			LayoutParams separatorLayout = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, 1);
			separator.setLayoutParams(separatorLayout);
			separator.setBackgroundColor(Color.BLACK);
			messagelist.addView(separator);
		}
		ImageButton back = (ImageButton) Activa.myApp.findViewById(R.id.previous);
		ImageButton create = (ImageButton) Activa.myApp.findViewById(R.id.create);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Activa.myUIManager.loadMainScreen(false, false);
			}
		});
		create.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Activa.myUIManager.createMessage();
			}
		});
	}

	public void showMessage(O2Message message) {
		Activa.myUIManager.state = UIManagerOLD.UI_STATE_MESSAGEREADING;
		Activa.myMessageManager.currentMessage = message;
		Activa.myApp.setContentView(R.layout.messagetoreceive);
		((TextView) Activa.myApp.findViewById(R.id.startText)).setText(Activa.myLanguageManager.MESSAGES_TITLE);
		((TextView) Activa.myApp.findViewById(R.id.senderText)).setText(Activa.myLanguageManager.MESSAGES_SENDER);
		((TextView) Activa.myApp.findViewById(R.id.topicText)).setText(Activa.myLanguageManager.MESSAGES_TOPIC);
		((TextView) Activa.myApp.findViewById(R.id.dateText)).setText(Activa.myLanguageManager.MESSAGES_DATE);
		((TextView) Activa.myApp.findViewById(R.id.sender)).setText(Activa.myMessageManager.contactList.get(message.sender).name);
		((TextView) Activa.myApp.findViewById(R.id.topic)).setText(message.topic);
		((TextView) Activa.myApp.findViewById(R.id.text)).setText(message.text);
		((TextView) Activa.myApp.findViewById(R.id.date)).setText(ActivaUtil.dateToReadableString(message.date) + ", " + ActivaUtil.timeToReadableString(message.date));
		ImageButton back = (ImageButton) Activa.myApp.findViewById(R.id.previous);
		ImageButton response = (ImageButton) Activa.myApp.findViewById(R.id.response);
		ImageButton resend = (ImageButton) Activa.myApp.findViewById(R.id.resend);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Activa.myUIManager.loadMessageList();
			}
		});
		response.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Activa.myUIManager.createMessage();
				((TextView) Activa.myApp.findViewById(R.id.receiver)).setText(Activa.myMessageManager.contactList.get(Activa.myMessageManager.currentMessage.sender).name);
				String bwtopic = "RE: " + Activa.myMessageManager.currentMessage.topic;
				((EditText) Activa.myApp.findViewById(R.id.topic)).setText(bwtopic);
				String bwtext = "RE --------------------------------\n\n" 
					+ "Sender: " + Activa.myMessageManager.contactList.get(Activa.myMessageManager.currentMessage.sender).name + "\n"
					+ "Receiver: " + Activa.myMessageManager.contactList.get(Activa.myMessageManager.currentMessage.receiver).name + "\n"
					+ "Topic: " + Activa.myMessageManager.currentMessage.topic + "\n"
					+ "Date: " + ActivaUtil.dateToReadableString(Activa.myMessageManager.currentMessage.date) + ", " + ActivaUtil.timeToReadableString(Activa.myMessageManager.currentMessage.date)
					+ "\n\n\n" + Activa.myMessageManager.currentMessage.text;
				((EditText) Activa.myApp.findViewById(R.id.text)).setText(bwtext);
			}
		});
		resend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Activa.myUIManager.createMessage();
				String bwtopic = "FW: " + Activa.myMessageManager.currentMessage.topic;
				((EditText) Activa.myApp.findViewById(R.id.topic)).setText(bwtopic);
				String bwtext = "FW --------------------------------\n\n" 
					+ "Sender: " + Activa.myMessageManager.contactList.get(Activa.myMessageManager.currentMessage.sender).name + "\n"
					+ "Receiver: " + Activa.myMessageManager.contactList.get(Activa.myMessageManager.currentMessage.receiver).name + "\n"
					+ "Topic: " + Activa.myMessageManager.currentMessage.topic + "\n"
					+ "Date: " + ActivaUtil.dateToReadableString(Activa.myMessageManager.currentMessage.date) + ", " + ActivaUtil.timeToReadableString(Activa.myMessageManager.currentMessage.date)
					+ "\n\n\n" + Activa.myMessageManager.currentMessage.text;
				((EditText) Activa.myApp.findViewById(R.id.text)).setText(bwtext);
			}
		});
	}

	public void createMessage() {
		Activa.myUIManager.state = UIManagerOLD.UI_STATE_MESSAGEWRITING;
		Activa.myApp.setContentView(R.layout.messagetosend);
		((TextView) Activa.myApp.findViewById(R.id.startText)).setText(Activa.myLanguageManager.MESSAGES_TITLE);
		((TextView) Activa.myApp.findViewById(R.id.receiverText)).setText(Activa.myLanguageManager.MESSAGES_RECEIVER);
		((TextView) Activa.myApp.findViewById(R.id.topicText)).setText(Activa.myLanguageManager.MESSAGES_TOPIC);
		ImageButton back = (ImageButton) Activa.myApp.findViewById(R.id.previous);
		ImageButton send = (ImageButton) Activa.myApp.findViewById(R.id.send);
		final TextView receiver = ((TextView) Activa.myApp.findViewById(R.id.receiver));
		receiver.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Enumeration<Contact> contacts = Activa.myMessageManager.contactList.elements();
				final CharSequence[] items = new CharSequence[Activa.myMessageManager.contactList.size() + 1];
				items[0] = Activa.myLanguageManager.MESSAGES_ERASE_RECEPTOR;
				String[] itemsTemp = new String[Activa.myMessageManager.contactList.size()];
				int j = 0;
				while (contacts.hasMoreElements()) {
					itemsTemp[j] = contacts.nextElement().name;
					j++;
				}
				Arrays.sort(itemsTemp);
				for (int i = 0; i < itemsTemp.length; i++) {
					items[i+1] = itemsTemp[i];
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(Activa.myApp);
				builder.setTitle(Activa.myLanguageManager.MESSAGES_CONTACT_REQUEST);
				builder.setItems(items, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	if (item == 0) receiver.setText("");
				    	else if (receiver.getText().toString().equals("")) receiver.setText(items[item]);
				    	else receiver.append("," + items[item]);
				    }
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Activa.myUIManager.loadMessageList();
			}
		});
		send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					String receiverString = ((TextView) Activa.myApp.findViewById(R.id.receiver)).getText().toString();
					StringTokenizer token = new StringTokenizer(receiverString,",");
					long[] receivers = new long[token.countTokens()];
					int j = 0;
					while (token.hasMoreTokens()) {
						receivers[j] = Activa.myMessageManager.getContactByName(token.nextToken()).id;
					}
					String topic = ((EditText) Activa.myApp.findViewById(R.id.topic)).getText().toString();
					String text = ((EditText) Activa.myApp.findViewById(R.id.text)).getText().toString();
					Activa.myApp.setContentView(R.layout.sending);
					long sender = Activa.myMobileManager.user.getId();
					Date date = new Date();
					O2UnregisteredMessage message = new O2UnregisteredMessage(sender, receivers, date, topic, text);
					SendO2Message sending = new SendO2Message(message);
					Thread thread = new Thread(sending, "SENDO2MESSAGE");
					thread.start();
				} catch (Exception e) {
					((EditText) Activa.myApp.findViewById(R.id.receiver)).setText("");
					loadInfoPopup(Activa.myLanguageManager.MESSAGES_ERROR_RECEIVER);
				}
			}
		});
	}

	public void refreshCreatingMessage(String receiverText, String topicText, String textText) {
		createMessage();
		((EditText) Activa.myApp.findViewById(R.id.receiver)).setText(receiverText);
		((EditText) Activa.myApp.findViewById(R.id.topic)).setText(topicText);
		((EditText) Activa.myApp.findViewById(R.id.text)).setText(textText);
	}

	public void loadInfoPopup(String string) {
		final RelativeLayout popupView = (RelativeLayout) Activa.myApp.findViewById(R.id.popupView);
		popupView.setVisibility(View.VISIBLE);
		TextView popupText = (TextView) Activa.myApp.findViewById(R.id.popupText);
		popupText.setText(string);
		CountDownTimer timer = new CountDownTimer(5000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
			}
			@Override
			public void onFinish() {
				popupView.setVisibility(View.INVISIBLE);			
			}
		};
		timer.start();
	}

	public void loadInfoPopupLong(String string) {
		final RelativeLayout popupView = (RelativeLayout) Activa.myApp.findViewById(R.id.popupView);
		popupView.setVisibility(View.VISIBLE);
		TextView popupText = (TextView) Activa.myApp.findViewById(R.id.popupText);
		popupText.setText(string);
		CountDownTimer timer = new CountDownTimer(10000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
			}
			@Override
			public void onFinish() {
				popupView.setVisibility(View.INVISIBLE);			
			}
		};
		timer.start();
	}
	
	public void loadInfoPopupWithoutExiting(String string) {
		final RelativeLayout popupView = (RelativeLayout) Activa.myApp.findViewById(R.id.popupView);
		popupView.setVisibility(View.VISIBLE);
		TextView popupText = (TextView) Activa.myApp.findViewById(R.id.popupText);
		popupText.setText(string);
	}
	
	public void loadNewsList(boolean refresh) {
		//TODO
		this.state = UI_STATE_NEWS;
		Activa.myApp.setContentView(R.layout.list);
		((TextView) Activa.myApp.findViewById(R.id.startText)).setText(Activa.myLanguageManager.NEWS_TITLE);
		TableLayout rsslisting = (TableLayout)Activa.myApp.findViewById(R.id.listing);
		if (!refresh) {
			Enumeration<Feed> enumer = Activa.myNewsManager.feedslist.elements();
			while (enumer.hasMoreElements()) {			
				Feed feed = enumer.nextElement();
				TableRow buttonLayout = new TableRow(Activa.myApp);
				buttonLayout.setId(feed.id);
				buttonLayout.setOrientation(TableRow.HORIZONTAL);
				buttonLayout.setGravity(Gravity.CENTER_VERTICAL);
				buttonLayout.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));			
				ImageButton button = new ImageButton(Activa.myApp);
				button.setBackgroundResource(R.drawable.iconbg);
				button.setImageResource(R.drawable.feed);
				button.setId(feed.id);
				TextView text = new TextView(Activa.myApp);
				text.append(feed.title);
				text.setMaxWidth(240);
				text.setTextSize((float) 20);
				text.setTextColor(Color.BLACK);
				text.setTypeface(Typeface.SANS_SERIF);
				text.setId(feed.id);
				buttonLayout.addView(button);
				buttonLayout.addView(text);
				OnClickListener listener = new OnClickListener() {
					@Override
					public void onClick(View v) {
						LoadFeedNewList(v.getId());
					}
				};
				button.setOnClickListener(listener);
				text.setOnClickListener(listener);
				buttonLayout.setOnClickListener(listener);
				rsslisting.addView(buttonLayout);
			}
		}
		else {
			GetNews sending = new GetNews();
			Thread thread = new Thread(sending, "GETNEWS");
			thread.start();
		}
		ImageButton back = (ImageButton) Activa.myApp.findViewById(R.id.previous);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadMainScreen(false, false);
			}
		});
	}
	
	public void LoadFeedNewList(final int feedId) {
		//TODO
		this.state = UI_STATE_NEWS;
		Activa.myApp.setContentView(R.layout.list);
		((TextView) Activa.myApp.findViewById(R.id.startText)).setText(Activa.myNewsManager.feedslist.get(feedId).title);
		TableLayout rsslisting = (TableLayout)Activa.myApp.findViewById(R.id.listing);
		Enumeration<New> enumer = Activa.myNewsManager.feedslist.get(feedId).newslist.elements();
		while (enumer.hasMoreElements()) {
			New notice = enumer.nextElement();
			TableRow buttonLayout = new TableRow(Activa.myApp);
			buttonLayout.setId(notice.id);
			buttonLayout.setOrientation(TableRow.HORIZONTAL);
			buttonLayout.setGravity(Gravity.CENTER_VERTICAL);
			buttonLayout.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));			
			ImageButton button = new ImageButton(Activa.myApp);
			button.setBackgroundResource(R.drawable.iconbg);
			button.setImageResource(R.drawable.notice);
			button.setId(notice.id);
			TextView text = new TextView(Activa.myApp);
			text.append(notice.title);
			text.setMaxWidth(240);
			text.setTextSize((float) 20);
			text.setTextColor(Color.BLACK);
			text.setTypeface(Typeface.SANS_SERIF);
			text.setId(notice.id);
			buttonLayout.addView(button);
			buttonLayout.addView(text);
			OnClickListener listener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					loadNew(v.getId(), feedId, true);
				}
			};
			button.setOnClickListener(listener);
			text.setOnClickListener(listener);
			buttonLayout.setOnClickListener(listener);
			rsslisting.addView(buttonLayout);
		}
		ImageButton back = (ImageButton) Activa.myApp.findViewById(R.id.previous);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadNewsList(false);
			}
		});
	}
	
	public void loadNew(int newId, final int feedId, boolean forward) {
		Activa.myApp.setContentView(R.layout.newviewer);
		Feed feed = Activa.myNewsManager.feedslist.get(feedId);
		if (newId < 0) newId = feed.newslist.size() - 1;
		if (newId >= feed.newslist.size()) newId = 0;
		New notice = feed.newslist.get(newId);
		while (notice == null) {
			if (forward) newId += 1;
			else newId -= 1;
			notice = feed.newslist.get(newId);
			if (newId < 0) newId = feed.newslist.size() - 1;
			if (newId >= feed.newslist.size()) newId = 0;
		}
		final int currentNewId = newId;
		((TextView) Activa.myApp.findViewById(R.id.startText)).setText(feed.title);
		TextView count = (TextView) Activa.myApp.findViewById(R.id.newcount);
		TextView title = (TextView) Activa.myApp.findViewById(R.id.newtitle);
		TextView snippet = (TextView) Activa.myApp.findViewById(R.id.newsnippet);
		ImageView photo = (ImageView) Activa.myApp.findViewById(R.id.newphoto);
		TextView text = (TextView) Activa.myApp.findViewById(R.id.newtext);
		ImageButton prevnew = (ImageButton) Activa.myApp.findViewById(R.id.previousnew);
		ImageButton nextnew = (ImageButton) Activa.myApp.findViewById(R.id.nextnew);
		ImageButton back = (ImageButton) Activa.myApp.findViewById(R.id.previous);
		count.setText((newId + 1) + "/" + feed.newslist.size());
		title.setText(notice.title);
		snippet.setText(notice.date + "\n\n" + notice.snippet);
//		if (notice.photoURL != null) {
//			try {
//				photo.setImageDrawable(Drawable.createFromStream((InputStream)(new URL(notice.photoURL)).getContent(), "src"));
//			} catch (Exception e) {
//				photo.setVisibility(View.GONE);
//			}
//		}
		text.setText(Html.fromHtml(notice.content));
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LoadFeedNewList(feedId);
			}
		});
		prevnew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadNew(currentNewId - 1, feedId, false);
			}
		});
		nextnew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadNew(currentNewId + 1, feedId, true);
			}
		});
	}
	
	public void loadNotes() {
		//TODO
		this.state = UI_STATE_NOTES;
		Activa.myApp.setContentView(R.layout.noteslist);
		((TextView) Activa.myApp.findViewById(R.id.startText)).setText(Activa.myLanguageManager.NOTES_TITLE);
		Hashtable<Date, Note> notesOrdered = new Hashtable<Date,Note>();
		Vector<Date> datesOrdered = new Vector<Date>();
		Enumeration<Note> enumeration = Activa.myNotesManager.notelist.elements();
		while (enumeration.hasMoreElements()) {
			Note temp = enumeration.nextElement();
			if (datesOrdered.contains(temp.date)) temp.date.setTime(temp.date.getTime() + 1);
			datesOrdered.add(temp.date);
			notesOrdered.put(temp.date, temp);
		}
		Collections.sort(datesOrdered);
		Collections.reverse(datesOrdered);
		final EditText newnote = (EditText)Activa.myApp.findViewById(R.id.newnotetext);
		LinearLayout notelist = (LinearLayout)Activa.myApp.findViewById(R.id.control);
		for(int i = 0; i < datesOrdered.size(); i++) {
			Note note = notesOrdered.get(datesOrdered.get(i));
			TextView time = new TextView(Activa.myApp);
			time.setText(ActivaUtil.dateToReadableString(note.date) + ", " + ActivaUtil.timeToReadableString(note.date));
			time.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			time.setPadding(10, 5, 10, 5);
			time.setTextColor(Color.BLACK);
			time.setTypeface(Typeface.DEFAULT_BOLD);
			TextView text = new TextView(Activa.myApp);
			text.setText(note.text);
			text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			text.setPadding(10, 0, 10, 5);
			text.setTextColor(Color.BLACK);
			text.setTypeface(Typeface.SANS_SERIF);					
			View separator = new View(Activa.myApp);
			LayoutParams separatorLayout = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, 1);
			separator.setLayoutParams(separatorLayout);
			separator.setBackgroundColor(Color.BLACK);
			notelist.addView(time);
			notelist.addView(text);
			notelist.addView(separator);
		}
		ImageButton addnote = (ImageButton) Activa.myApp.findViewById(R.id.addnote);
		addnote.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SendNote sending = new SendNote(newnote.getText().toString());
				Thread thread = new Thread(sending, "SENDNOTE");
				thread.start();
			}
		});
		ImageButton back = (ImageButton) Activa.myApp.findViewById(R.id.previous);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadMainScreen(false, false);
			}
		});
	}
	 
}

class QuestionnaireButtonListenerDemo implements OnClickListener {
	
	int questionnaireID;
	
	public QuestionnaireButtonListenerDemo(int id) {
		this.questionnaireID = id;
	}
	
	@Override
	public void onClick(View v) {
		boolean valid = Activa.myQuestManager.questionnaireSet.get(this.questionnaireID).validateQuestionnaire();
		if (valid) {
			Activa.myUIManager.state = UIManagerOLD.UI_STATE_QUESTIONNAIREINIT;
			Activa.myApp.setContentView(R.layout.yesnoquestion);
			Activa.myQuestManager.activeQuestionnaireId = this.questionnaireID;
			Activa.myQuestManager.eventAssociated = null;
			Activa.myUIManager.startActiveQuestionnaire();
		}
		else Activa.myUIManager.loadInfoPopup(Activa.myLanguageManager.QUEST_NOTVALID);
	}
	
}

/*class UpdateTimeTask extends CountDownTimer {
	
	public UpdateTimeTask(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
	}

	long startTime;

	@Override
	public void onFinish() {
		Activa.myUIManager.loadSensorResult("62 latidos/min", "Pulso cardiaco"); 
	}

	@Override
	public void onTick(long millisUntilFinished) {
	    int seconds = (int) (millisUntilFinished / 1000);
	    int minutes = seconds / 60;
	    seconds     = seconds % 60;
	    ((TextView) Activa.myApp.findViewById(R.id.timer)).setText(String.format("%02d:%02d", minutes, seconds));
	}
}*/

/*class WaitTimeTask extends CountDownTimer {
	
	static int count = 0;
	int step = 0;
	
	public WaitTimeTask(long millisInFuture, long countDownInterval, int step) {
		super(millisInFuture, countDownInterval);
		count++;
		this.step = step;
	}

	long startTime;

	@Override
	public void onFinish() {
//		Activa.goToFront();
		Activa.myApp.setContentView(R.layout.waiting);
		TextView title = (TextView) Activa.myApp.findViewById(R.id.startText);
		title.append("conectando");
		TextView text = (TextView) Activa.myApp.findViewById(R.id.infoText);
		text.append("Enviando datos ...");
		AnimSendingTimeTask timer = new AnimSendingTimeTask(3000, 125, step);
		timer.start();
	}

	@Override
	public void onTick(long millisUntilFinished) {
	    int seconds = (int) (millisUntilFinished / 1000);
	    int minutes = seconds / 60;
	    seconds     = seconds % 60;
	}
}*/

/*class WaitTimeTask2 extends CountDownTimer {
	
	static int count = 0;
	
	public WaitTimeTask2(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
		count++;
	}

	long startTime;

	@Override
	public void onFinish() {
		Activa.myUIManager.loadSensorResult("4.32 L", "FVC"); 
	}

	@Override
	public void onTick(long millisUntilFinished) {
	    int seconds = (int) (millisUntilFinished / 1000);
	    int minutes = seconds / 60;
	    seconds     = seconds % 60;
	}
}*/

/*class WaitTimeTask3 extends CountDownTimer {
	
	long proxmillis;
	
	public WaitTimeTask3(long millisInFuture, long countDownInterval, long proxMillis) {
		super(millisInFuture, countDownInterval);
		this.proxmillis = proxMillis;
	}

	long startTime;

	@Override
	public void onFinish() {
		Activa.myApp.setContentView(R.layout.waitingtimed);
		((TextView) Activa.myApp.findViewById(R.id.startText)).
		setText("Sensor Zephyr");
		((TextView) Activa.myApp.findViewById(R.id.infoText)).
				setText("Leyendo datos del sensor ...");
		UpdateTimeTask timer = new UpdateTimeTask(proxmillis, 1000);	
		timer.start();
	}

	@Override
	public void onTick(long millisUntilFinished) {
	    int seconds = (int) (millisUntilFinished / 1000);
	    int minutes = seconds / 60;
	    seconds     = seconds % 60;
	}
}*/

/*class WaitTimeTask4 extends CountDownTimer {
	
	long proxmillis;
	
	public WaitTimeTask4(long millisInFuture, long countDownInterval, long proxMillis) {
		super(millisInFuture, countDownInterval);
		this.proxmillis = proxMillis;
	}

	long startTime;

	@Override
	public void onFinish() {
		Activa.myApp.setContentView(R.layout.waitingtimed);
		((TextView) Activa.myApp.findViewById(R.id.startText)).
		setText("Sensor Zephyr");
		((TextView) Activa.myApp.findViewById(R.id.infoText)).
				setText("Leyendo datos del sensor ...");
		TimedTimeTask timer = new TimedTimeTask(proxmillis, 1000, "Sensor Zephyr");	
		timer.start();
	}

	@Override
	public void onTick(long millisUntilFinished) {
	    int seconds = (int) (millisUntilFinished / 1000);
	    int minutes = seconds / 60;
	    seconds     = seconds % 60;
	}
}*/

/*class AnimTimeTask extends CountDownTimer {
	
	int[] images = new int [50];
	int imagePointer;
	
	public AnimTimeTask(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
		images[0] = R.drawable.render_logo_movil0001;
		images[1] = R.drawable.render_logo_movil0002;
		images[2] = R.drawable.render_logo_movil0003;
		images[3] = R.drawable.render_logo_movil0004;
		images[4] = R.drawable.render_logo_movil0005;
		images[5] = R.drawable.render_logo_movil0006;
		images[6] = R.drawable.render_logo_movil0007;
		images[7] = R.drawable.render_logo_movil0008;
		images[8] = R.drawable.render_logo_movil0000;
		ImageView popupProgress = (ImageView) Activa.myApp.findViewById(R.id.popupImage);
		imagePointer = 0;
		popupProgress.setBackgroundResource(images[imagePointer]);
	}

	long startTime;

	@Override
	public void onFinish() {
		TextView popupText = (TextView) Activa.myApp.findViewById(R.id.popupText);
		ImageView popupProgress = (ImageView) Activa.myApp.findViewById(R.id.popupImage);
		popupText.setText("Excedido tiempo de espera para conectarse.");
		popupProgress.setVisibility(View.GONE);
		BeginTimeTask timer = new BeginTimeTask(3000, 1000);
		timer.start();
	}

	@Override
	public void onTick(long millisUntilFinished) {
		imagePointer++;
		if (imagePointer > 7) imagePointer = 0;
		ImageView popupProgress = (ImageView) Activa.myApp.findViewById(R.id.popupImage);
		popupProgress.setBackgroundResource(images[imagePointer]);
	}
}*/

/*class LoginTimeTask extends CountDownTimer {
	
	public LoginTimeTask(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
	}

	long startTime;

	@Override
	public void onFinish() {
		Activa.myUIManager.loadLoginScreen();
	}

	@Override
	public void onTick(long millisUntilFinished) {
	}
}*/

/*class BeginTimeTask extends CountDownTimer {
	
	public BeginTimeTask(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
	}

	long startTime;

	@Override
	public void onFinish() {
		Activa.myUIManager.loadMainScreen(false);
	}

	@Override
	public void onTick(long millisUntilFinished) {
	}
}*/

/*class AnimSensorTimeTask extends CountDownTimer {
	
	int[] images = new int [50];
	int imagePointer;
	String sensor;
	static boolean firstTime = true;
	
	public AnimSensorTimeTask(long millisInFuture, long countDownInterval, String sensor) {
		super(millisInFuture, countDownInterval);
		images[0] = R.drawable.render_logo_movil0001;
		images[1] = R.drawable.render_logo_movil0002;
		images[2] = R.drawable.render_logo_movil0003;
		images[3] = R.drawable.render_logo_movil0004;
		images[4] = R.drawable.render_logo_movil0005;
		images[5] = R.drawable.render_logo_movil0006;
		images[6] = R.drawable.render_logo_movil0007;
		images[7] = R.drawable.render_logo_movil0008;
		images[8] = R.drawable.render_logo_movil0000;
		ImageView progress = (ImageView) Activa.myApp.findViewById(R.id.animation);
		progress.setVisibility(View.VISIBLE);
		imagePointer = 0;
		this.sensor = sensor;
		progress.setBackgroundResource(images[imagePointer]);
	}

	long startTime;

	@Override
	public void onFinish() {
		ImageView progress = (ImageView) Activa.myApp.findViewById(R.id.animation);
		progress.setVisibility(View.GONE);
		if ((sensor.compareTo("Espirometria") == 0)&&(firstTime)){
			ImageButton help = (ImageButton) Activa.myApp.findViewById(R.id.help);
			help.setVisibility(View.VISIBLE);		
			firstTime = false;
		}
		else {
			Activa.myUIManager.automaticSensorMeasurement(this.sensor);
		}
	}

	@Override
	public void onTick(long millisUntilFinished) {
		imagePointer++;
		if (imagePointer > 8) imagePointer = 0;
		ImageView progress = (ImageView) Activa.myApp.findViewById(R.id.animation);
		progress.setBackgroundResource(images[imagePointer]);
	}
}*/

/*class AnimSensoringTimeTask extends CountDownTimer {
	
	int[] images = new int [50];
	int imagePointer;
	String sensor;
	int step;
	boolean finished = true;
	
	public AnimSensoringTimeTask(long millisInFuture, long countDownInterval, String sensor, int step) {
		super(millisInFuture, countDownInterval);
		images[0] = R.drawable.render_logo_movil0001c;
		images[1] = R.drawable.render_logo_movil0002c;
		images[2] = R.drawable.render_logo_movil0003c;
		images[3] = R.drawable.render_logo_movil0004c;
		images[4] = R.drawable.render_logo_movil0005c;
		images[5] = R.drawable.render_logo_movil0006c;
		images[6] = R.drawable.render_logo_movil0007c;
		images[7] = R.drawable.render_logo_movil0008c;
		images[8] = R.drawable.render_logo_movil0000c;
		ImageView progress = (ImageView) Activa.myApp.findViewById(R.id.progress);
		imagePointer = 0;
		this.sensor = sensor;
		progress.setBackgroundResource(images[imagePointer]);
		this.step = step;
		ImageButton button = (ImageButton) Activa.myApp.findViewById(R.id.stop);
		FinishClickListener listener = new FinishClickListener(this);
		button.setOnClickListener(listener);
	}

	long startTime;

	@Override
	public void onFinish() {
		ImageView progress = (ImageView) Activa.myApp.findViewById(R.id.progress);
		if (Activa.mySensorManager.eventAssociated != null) Activa.mySensorManager.eventAssociated.state = 0;
		if (!finished) Activa.myUIManager.loadMainScreen(false);
		else if (step == 0) {
			progress.setVisibility(View.GONE);
			if (sensor.compareTo("Espirometria") == 0) Activa.myUIManager.loadSensorResult("4.32 L", "FVC");
			else if (sensor.compareTo("Pulsioximetria") == 0) Activa.myUIManager.loadSensorResult("96%", "Saturacion de O2");
			else if (sensor.compareTo("Sensor Zephyr") == 0) Activa.myUIManager.loadSensorResult("157 latidos/min\n\n172 latidos/min maximo", "Pulso cardiaco");			
		}
		else if (step == 1) {
			Activa.myApp.setContentView(R.layout.waitingtimed);
			((TextView) Activa.myApp.findViewById(R.id.startText)).
			setText("Sensor Zephyr");
			((TextView) Activa.myApp.findViewById(R.id.infoText)).
					setText("Leyendo datos del sensor ...");
			TimedTimeTask timer = new TimedTimeTask(30000, 125, sensor);	
			timer.start();
		}
		else  {
			if (sensor.compareTo("Espirometria") == 0) Activa.myUIManager.loadSensorResult2("4.32 L", "FVC");
			else if (sensor.compareTo("Pulsioximetria") == 0) Activa.myUIManager.loadSensorResult2("16%", "Saturacion de O2");
			else if (sensor.compareTo("Sensor Zephyr") == 0) Activa.myUIManager.loadSensorResult2("157 latidos/min\n\n172 latidos/min maximo", "Pulso cardiaco");			
		}
	}

	@Override
	public void onTick(long millisUntilFinished) {
		imagePointer++;
		if (imagePointer > 8) imagePointer = 0;
		ImageView progress = (ImageView) Activa.myApp.findViewById(R.id.progress);
		progress.setBackgroundResource(images[imagePointer]);
	}
}*/

/*class AnimSendingTimeTask extends CountDownTimer {
	
	int[] images = new int [50];
	int imagePointer;
	int state = 0;
	
	public AnimSendingTimeTask(long millisInFuture, long countDownInterval, int state) {
		super(millisInFuture, countDownInterval);
		images[0] = R.drawable.render_logo_movil0001c;
		images[1] = R.drawable.render_logo_movil0002c;
		images[2] = R.drawable.render_logo_movil0003c;
		images[3] = R.drawable.render_logo_movil0004c;
		images[4] = R.drawable.render_logo_movil0005c;
		images[5] = R.drawable.render_logo_movil0006c;
		images[6] = R.drawable.render_logo_movil0007c;
		images[7] = R.drawable.render_logo_movil0008c;
		images[8] = R.drawable.render_logo_movil0000c;
		LinearLayout lay = (LinearLayout) Activa.myApp.findViewById(R.id.stoplay);
		lay.setVisibility(View.INVISIBLE);
		ImageView progress = (ImageView) Activa.myApp.findViewById(R.id.progress);
		imagePointer = 0;
		this.state = state;
		progress.setBackgroundResource(images[imagePointer]);
	}

	long startTime;

	@Override
	public void onFinish() {
		ImageView progress = (ImageView) Activa.myApp.findViewById(R.id.progress);
		if (state == 0) {
			if (Activa.mySensorManager.eventAssociated != null) Activa.myUIManager.loadScheduleDay(Activa.mySensorManager.eventAssociated.date);
			else Activa.myUIManager.loadMainScreen(false);
		}
		else if (state == 1) Activa.myUIManager.Program2();
		else if (state == 2) Activa.myUIManager.Program3();
	}

	@Override
	public void onTick(long millisUntilFinished) {
		imagePointer++;
		if (imagePointer > 8) imagePointer = 0;
		ImageView progress = (ImageView) Activa.myApp.findViewById(R.id.progress);
		progress.setBackgroundResource(images[imagePointer]);
	}
}*/

/*class TimedTimeTask extends CountDownTimer {
	int[] images = new int [50];
	int imagePointer = 0;
	String sensor;
	int seconds = 60;
	boolean finished = true;
	
	public TimedTimeTask(long millisInFuture, long countDownInterval, String sensor) {
		super(millisInFuture, countDownInterval);
		this.sensor = sensor;
		images[0] = R.drawable.render_logo_movil0001c;
		images[1] = R.drawable.render_logo_movil0002c;
		images[2] = R.drawable.render_logo_movil0003c;
		images[3] = R.drawable.render_logo_movil0004c;
		images[4] = R.drawable.render_logo_movil0005c;
		images[5] = R.drawable.render_logo_movil0006c;
		images[6] = R.drawable.render_logo_movil0007c;
		images[7] = R.drawable.render_logo_movil0008c;
		images[8] = R.drawable.render_logo_movil0000c;
		ImageView progress = (ImageView) Activa.myApp.findViewById(R.id.progress);
		imagePointer = 0;
		ImageButton button = (ImageButton) Activa.myApp.findViewById(R.id.stop);
		FinishClickListener listener = new FinishClickListener(this);
		button.setOnClickListener(listener);
	}

	long startTime;

	@Override
	public void onFinish() {
		if ((!finished)&&(seconds > 30)) Activa.myUIManager.loadMainScreen(false);
		else if (sensor.compareTo("Espirometria") == 0) Activa.myUIManager.loadSensorResult2("4.32 L", "FVC");
		else if (sensor.compareTo("Pulsioximetria") == 0) Activa.myUIManager.loadSensorResult2("96%", "Saturacion de O2");
		else if (sensor.compareTo("Sensor Zephyr") == 0) Activa.myUIManager.loadSensorResult2("157 latidos/min\n\n172 latidos/min maximo", "Pulso cardiaco");		
	 
	}

	@Override
	public void onTick(long millisUntilFinished) {
		imagePointer++;
		if (imagePointer > 8) imagePointer = 0;
		ImageView progress = (ImageView) Activa.myApp.findViewById(R.id.progress);
		progress.setBackgroundResource(images[imagePointer]);
	    seconds = (int) (millisUntilFinished / 1000);
	    int minutes = seconds / 60;
	    seconds     = seconds % 60;
	    if (seconds > 15) {
	    	seconds += 29;
	    	minutes = 44;
	    }
	    ((TextView) Activa.myApp.findViewById(R.id.timer)).setText(String.format("%02d:%02d", minutes, seconds));
	}
}*/

/*class FinishClickListener implements OnClickListener {
	
	AnimSensoringTimeTask anim = null;
	TimedTimeTask tim = null;
	
	public FinishClickListener(AnimSensoringTimeTask anim) {
		this.anim = anim;
	}
	
	public FinishClickListener(TimedTimeTask tim) {
		this.tim = tim;
	}
	
	@Override
	public void onClick(View v) {
		if (anim != null) {
			anim.finished = false;
			anim.cancel();
			anim.onFinish();
		}	
		else if (tim != null) {
			tim.finished = false;
			tim.cancel();
			tim.onFinish();
		}
	}
}*/