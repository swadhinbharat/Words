package com.swadhin.study.words;

import java.util.ArrayList;
import java.util.Locale;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.swadhin.study.words.db.FlashCardDBHelper;
import com.swadhin.study.words.model.MyWord;
import com.swadhin.study.words.model.Word;

//@SuppressLint("ShowToast")
public class FlashCardActivity extends Activity implements OnClickListener,
		OnGestureListener, OnRatingBarChangeListener {

	// Constants
	private final int SWIPE_MIN_DISTANCE = 120;
	private final int SWIPE_THRESHOLD_VELOCITY = 200;

	// UI Elements of the Activity
	private Button bFirst, bPrev, bNext, bLast;
	private TextView tvWord, tvDefinition;
	private ProgressBar pbProgress;
	private RatingBar rbMyRating;

	// Preferences and Settings
	private SharedPreferences settings;

	// Helper and Parser
	private XMLParser myParser;

	// Model and Models
	private ArrayList<Word> words;
	private MyWord myWord;

	// Global Variables needed in cross-methods
	private int index = 0;

	// DB Helper and Database
	FlashCardDBHelper dbh;
	SQLiteDatabase db;

	// Extras and Added Features
	private TextToSpeech tts;
	private GestureDetector gestureScanner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flash_card_activity);

		// Initialize fields
		initialize();

		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			myParser = new XMLParser();
			xr.setContentHandler(myParser);
			xr.parse(new InputSource(this.getResources().openRawResource(
					R.raw.words)));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			words = myParser.getWordsList();
			// db = dbh.getWritableDatabase();
			// myWords = dbh.getMyWordsList();
			displayWord(words.get(index));
		}

		pbProgress.setMax(words.size());
	}

	private void displayWord(Word word) {
		// Set Values on the Screen
		tvWord.setText(word.getWord());
		tvDefinition.setText(word.getDefinition());

		// Set User Specific Values on the Screen
		rbMyRating.setRating(dbh.getMyWord(word).getMyRating());

		// Set Progress Bar
		pbProgress.setProgress(index + 1);

		// Speak Up the word
		if (settings.getBoolean("prefPronunce", false)) {
			tts.speak(word.getWord().toString(), TextToSpeech.QUEUE_FLUSH, null);
		}
	}

	private void initialize() {
		index = 0;
		bFirst = (Button) findViewById(R.id.bFirst);
		bPrev = (Button) findViewById(R.id.bPrev);
		bNext = (Button) findViewById(R.id.bNext);
		bLast = (Button) findViewById(R.id.bLast);
		tvWord = (TextView) findViewById(R.id.tvWord);
		tvDefinition = (TextView) findViewById(R.id.tvDefinition);
		pbProgress = (ProgressBar) findViewById(R.id.pbProgress);
		rbMyRating = (RatingBar) findViewById(R.id.rbMyRating);

		bFirst.setOnClickListener(this);
		bPrev.setOnClickListener(this);
		bNext.setOnClickListener(this);
		bLast.setOnClickListener(this);
		rbMyRating.setOnRatingBarChangeListener(this);

		// Database activities
		dbh = new FlashCardDBHelper(this);
		db = dbh.getWritableDatabase();

		// Settings
		settings = PreferenceManager.getDefaultSharedPreferences(this);

		// Gestures
		gestureScanner = new GestureDetector(getBaseContext(), this);

		// Text to Voice and Voice to Text
		tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

			@Override
			public void onInit(int status) {
				if (status != TextToSpeech.ERROR) {
					tts.setLanguage(Locale.getDefault());
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			// Toast.makeText(getBaseContext(), "Settings",
			// Toast.LENGTH_SHORT).show();
			// Intent i = new Intent(this, SettingsActivity.class);
			startActivity(new Intent(getBaseContext(), Preference.class));
			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		// TODO Auto-generated method stub
		super.onOptionsMenuClosed(menu);
		refreshDisplay();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refreshDisplay();
	}

	private void refreshDisplay() {
		if (settings.getBoolean("prefViewButtons", true) == true) {
			// bFirst.setVisibility(View.INVISIBLE);
			// bPrev.setVisibility(View.INVISIBLE);
			// bNext.setVisibility(View.INVISIBLE);
			// bLast.setVisibility(View.INVISIBLE);
			bFirst.setVisibility(View.GONE);
			bPrev.setVisibility(View.GONE);
			bNext.setVisibility(View.GONE);
			bLast.setVisibility(View.GONE);
		} else {
			bFirst.setVisibility(View.VISIBLE);
			bPrev.setVisibility(View.VISIBLE);
			bNext.setVisibility(View.VISIBLE);
			bLast.setVisibility(View.VISIBLE);

		}
	}

	public void toggleTheme() {
		// this.setTheme(android.R.style.Theme_Black);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bFirst:
			goToFirst();
			break;

		case R.id.bPrev:
			goToPrevious();
			break;

		case R.id.bNext:
			goToNext();
			break;

		case R.id.bLast:
			goToLast();
			break;

		}

	}

	private void goToLast() {
		if (index == (words.size() - 1) && index != 0) {
			Toast.makeText(this, "You are already at the last item.",
					Toast.LENGTH_SHORT).show();
		} else {
			index = words.size() - 1;
			displayWord(words.get(index));
		}
	}

	private void goToNext() {
		index++;
		if (index < words.size()) {
			displayWord(words.get(index));
		} else if (index == words.size()) {
			Toast.makeText(this, "You have reached the end of the list.",
					Toast.LENGTH_SHORT).show();
			index--;
		}
	}

	private void goToPrevious() {
		index--;
		if (index >= 0) {
			displayWord(words.get(index));
		} else if (index < 0) {
			Toast.makeText(this, "You have reached the beginning of the list.",
					Toast.LENGTH_SHORT).show();
			index++;
		}
	}

	private void goToFirst() {
		if (index == 0 && index != words.size()) {
			Toast.makeText(this, "You are already at the first item.",
					Toast.LENGTH_SHORT).show();
		} else {
			index = 0;
			displayWord(words.get(index));
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent me) {
		return gestureScanner.onTouchEvent(me);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// Toast.makeText(MainActivity.this, "Fling",
		// Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {

		if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
				&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
			// Right to left, your code here
			goToNext();
			return true;
		} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
				&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
			// Left to right, your code here
			goToPrevious();
			return true;
		}
		if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
				&& Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
			// Bottom to top, your code here
			return true;
		} else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
				&& Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
			// Top to bottom, your code here
			return true;
		}
		return false;

		//
		// Toast.makeText(MainActivity.this, "X = " + String.valueOf(velocityX)
		// + " Y = " + String.valueOf(velocityY), Toast.LENGTH_SHORT).show();
		// return false;
	}

	@Override
	public void onRatingChanged(RatingBar ratingBar, float rating,
			boolean fromUser) {

		myWord = dbh.getMyWord(words.get(index));
		myWord.setMyRating((int) rating);
		db = dbh.getWritableDatabase();
		dbh.update(db, myWord);
	}

}