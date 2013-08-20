package com.example.todoh;

import java.util.Calendar;

import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.support.v4.app.NotificationCompat;
import android.content.Context;
import android.content.Intent;

public class CadastraActivity extends Activity {
	DbUtils db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cadastra);
		
		// Inicializa o DB
		db = new DbUtils(getApplicationContext());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cadastra, menu);
		return true;
	}
	
	public void salvaTodoh(View v){
		TextView tv = (TextView) findViewById(R.id.editText1);
		long id = db.insereTodoh(tv.getText().toString(), Calendar.getInstance().getTimeInMillis());
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(tv.getText().toString())
				.setContentText("Atividade pendente...");
		
		Intent resultIntent = new Intent(this, MainActivity.class);
		
		PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext()
								,10 , resultIntent , PendingIntent.FLAG_UPDATE_CURRENT);
		
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify((int) id, mBuilder.build());
		Log.d(Utils.APP_NAME,"ID DA NOTIFICACAO: "+id);
		
		Intent in = new Intent();
		setResult(RESULT_OK,in);
		finish(); // Cai fora :)
	}

}
