package com.example.todoh;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ToDohDBHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "ToDoh.db";	
	public static final int DATABASE_VERSION = 4;
	public static final String DATABASE_TAB_USER_TODOH = "tab_user_todoh";
	public static final String TAB_USER_TODOH_ID = "_id";
	public static final String TAB_USER_TODOH_TEXTO = "texto";
	public static final String TAB_USER_TODOH_DATA = "data_aviso";
	public static final String TAB_USER_TODOH_DONE = "done";
	public static final String DATABASE_CREATE = "CREATE TABLE "+DATABASE_TAB_USER_TODOH+" " +
			"( "+TAB_USER_TODOH_ID+" integer primary key" +
			", "+TAB_USER_TODOH_TEXTO+" text not null" +
			", "+TAB_USER_TODOH_DATA+" int" +
			", "+TAB_USER_TODOH_DONE+" int);";
	
	public ToDohDBHelper(Context context) {
		super(context,DATABASE_NAME,null,DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.d(Utils.APP_NAME, "Atualizando DB "+oldVersion+" >> "+newVersion);
		db.execSQL( "DROP TABLE IF EXISTS "+DATABASE_TAB_USER_TODOH );
		onCreate(db);
	}
}