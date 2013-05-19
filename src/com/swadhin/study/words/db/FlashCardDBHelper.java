package com.swadhin.study.words.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.swadhin.study.words.model.MyWord;
import com.swadhin.study.words.model.Word;

/**
 * Created by swadhinbharat on 19/05/13.
 */

public class FlashCardDBHelper extends SQLiteOpenHelper {

	static final String dbName = "demoDB";
	static final String tableWords = "Words";

	static final String colWord = "Word";
	static final String colRating = "Rating";

	private SQLiteDatabase db;
	private Cursor cursor;

	private Context context;

	private ArrayList<MyWord> myWords = new ArrayList<MyWord>();

	public FlashCardDBHelper(Context context) {
		super(context, dbName, null, 33);
		this.context = context;
	}

	public void onCreate(SQLiteDatabase db) {

		this.db = db;

		db.execSQL("CREATE TABLE " + tableWords + " (" + colWord
				+ " TEXT PRIMARY KEY , " + colRating + " INTEGER);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		db.execSQL("DROP TABLE IF EXISTS " + tableWords);

		onCreate(db);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly()) {
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	}

	// Add an Entry
	public void add(SQLiteDatabase db, MyWord mw) {

		ContentValues cv = new ContentValues();

		cv.put(FlashCardDBHelper.colWord, mw.getWord());
		cv.put(FlashCardDBHelper.colRating, mw.getMyRating());

		db.insert(FlashCardDBHelper.tableWords, null, cv);
		db.close();
	}

	// Remove an Entry
	public void remove(SQLiteDatabase db, MyWord mw) {
		db.delete(tableWords, "Word = ?", new String[] { mw.getWord() });
		db.close();
	}

	// Modify an Entry
	public void update(SQLiteDatabase db, MyWord mw) {

		ContentValues cv = new ContentValues();

		cv.put(FlashCardDBHelper.colWord, mw.getWord());
		cv.put(FlashCardDBHelper.colRating, mw.getMyRating());

		int rc = db.update(tableWords, cv, "Word = ?",
				new String[] { mw.getWord() });

		if (rc == 0) {
			Toast.makeText(context, String.valueOf(rc), Toast.LENGTH_SHORT)
					.show();
			add(db, mw);
		}

		db.close();
	}

	public ArrayList<MyWord> getMyWordsList() {
		MyWord mw = new MyWord();

		try {
			db = this.getWritableDatabase();
			cursor = db.rawQuery("SELECT * FROM " + tableWords, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (cursor.moveToFirst()) {

			while (cursor.isAfterLast() == false) {
				mw.setWord(cursor.getString(cursor.getColumnIndex(colWord)));
				mw.setMyRating(cursor.getInt(cursor.getColumnIndex(colRating)));

				myWords.add(mw);
				cursor.moveToNext();
			}
			cursor.close();
		}

		return myWords;
	}

	public MyWord getMyWord(Word w) {
		MyWord mw = new MyWord();

		try {
			db = this.getWritableDatabase();
			cursor = db.rawQuery("SELECT * FROM " + tableWords + " WHERE "
					+ colWord + " = '" + w.getWord() + "';", null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && cursor.moveToFirst()) {
				mw.setWord(cursor.getString(cursor.getColumnIndex(colWord)));
				mw.setMyRating(cursor.getInt(cursor.getColumnIndex(colRating)));
				cursor.close();
			} else {
				mw.setWord(w.getWord());
				mw.setMyRating(0);
				add(db, mw);
			}
		}

		db.close();
		return mw;
	}

}
