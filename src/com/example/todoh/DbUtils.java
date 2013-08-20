package com.example.todoh;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DbUtils {
	private ToDohDBHelper dbHelper;
	private SQLiteDatabase db;
	
	public DbUtils(Context context){
		dbHelper = new ToDohDBHelper(context);
		db = dbHelper.getWritableDatabase(); // Instancia a base de dados.
	}
	
//	public SQLiteDatabase abreDbWrite() {
//	}
	
	public long insereTodoh(String toDoh,long Alarme){
		ContentValues values = new ContentValues();
		
		values.put(ToDohDBHelper.TAB_USER_TODOH_TEXTO,toDoh);
		values.put(ToDohDBHelper.TAB_USER_TODOH_DATA,Alarme);
		values.put(ToDohDBHelper.TAB_USER_TODOH_DONE,0);
		return db.insert(ToDohDBHelper.DATABASE_TAB_USER_TODOH, null, values);
	}
	
	public int removeTodoh(long id) {
		return db.delete(ToDohDBHelper.DATABASE_TAB_USER_TODOH, ToDohDBHelper.TAB_USER_TODOH_ID+" = ?", new String[] {""+id} );
	}
	
	public Cursor selectTodohs(){
		String[] cols = new String[] {ToDohDBHelper.TAB_USER_TODOH_ID,ToDohDBHelper.TAB_USER_TODOH_TEXTO,ToDohDBHelper.TAB_USER_TODOH_DATA,ToDohDBHelper.TAB_USER_TODOH_DONE};
		Cursor mCursor = db.query(true,ToDohDBHelper.DATABASE_TAB_USER_TODOH,cols,null,null,null,null,null,null);
		
		if( mCursor != null ) {
			return (mCursor.moveToFirst()?mCursor:null); // se falhar ir para o primeiro retorna nulo
		}
		return mCursor;
	}
	
	public int updateTodoh(long id, int done){
		ContentValues values = new ContentValues();
		
		values.put(ToDohDBHelper.TAB_USER_TODOH_DONE, done);
		
		// Altera a tabela onde o id for igual o recebido
		return db.update(ToDohDBHelper.DATABASE_TAB_USER_TODOH, values, ToDohDBHelper.TAB_USER_TODOH_ID+" = ?", new String[] {""+id});
	}
	
	public void recriaDados(){
		db.execSQL( "DROP TABLE IF EXISTS "+ToDohDBHelper.DATABASE_TAB_USER_TODOH );
		dbHelper.onCreate(db);
	}
	
	public void limparConcluidos(){
		int i = db.delete(ToDohDBHelper.DATABASE_TAB_USER_TODOH,ToDohDBHelper.TAB_USER_TODOH_DONE+" = 1", null);
	}
}


