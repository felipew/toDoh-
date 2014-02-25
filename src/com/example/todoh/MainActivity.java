package com.example.todoh;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private DbUtils db;
	private int indiceRemove;
	ArrayList<ToDoh> itens = new ArrayList<MainActivity.ToDoh>();
	AdapterToDoh adapter;
	NotificationManager mNotificationManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Inicializando as variaveis globais.
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		db = new DbUtils(getApplicationContext());
		
		ListView lv = (ListView) findViewById(R.id.listView1);
		if( lv != null ){
			// Seta adapter
			inicializaAdapter(lv);
		}
		
		populaLista();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()) {
			case R.id.menuAdd:
				chamaTelaAdd();
				break;
			case R.id.menuLimpar:
				limparToDoh();
				break;
		}
		
		return super.onMenuItemSelected(featureId, item);
	}
	
	/**
	 * Aplica o adapter ao listView
	 * Estamos usando um adapter personalisado, porem poderiamos usar um cursorAdapter
	 * 
	 * @param lv
	 */
	public void inicializaAdapter(ListView lv){
		adapter = new AdapterToDoh(getApplicationContext());
		lv.setAdapter(adapter); // pronto lista recebe as atualizacoes
		
		// Long Click - Quando o usuario deixa o botao pressionado
		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int indice, long arg3) {
				indiceRemove = indice;
				mostraDialogo(); // Pergunta se deseja mesmo reover
				return true;
			}
		});
		
		// Click simples
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,	int indice, long arg3) {
				itens.get(indice).done = 1-itens.get(indice).done; // Inverte o estado
				db.updateTodoh( itens.get(indice).id,itens.get(indice).done ); // atualiza db
				adapter.notifyDataSetChanged(); // atualzia lista
			}
		});
	}
	
	/**
	 * chamaTelaAdd
	 * @author felipewagner
	 * 
	 * Chama Intent para a Tela de adicionar toDoh
	 */
	public void chamaTelaAdd() {
		Intent in = new Intent(getApplicationContext(),CadastraActivity.class);
		startActivityForResult(in,99);
	}
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( requestCode == 99 && resultCode == RESULT_OK ){
			populaLista();
		}
	}
	
	public void limparToDoh(){
		// TODO: V2 Chama dialog perguntando se deseja mesmo remover os itens ja concluidos
		// delete from tab where done = 1;
		db.limparConcluidos();
		for( int i=0;i<itens.size();i++){
			if( itens.get(i).done == 1 ) {
				mNotificationManager.cancel((int) itens.get(i).id);
			}
		}
		populaLista();
	}
	
	/**
	 * Recupera as informacoes do banco
	 * Atencao: esta loading nao eh otimizado.. ele le os dados do banco
	 * coloca em um array de objetos e este array eh passado para o adapter.
	 */
	public void populaLista(){
		itens.clear();

		// TODO: utilizar o cursor no adapter e nao o ArrayList
		Cursor c = db.selectTodohs();
		if(c != null && c.getCount() != 0){
			do {
				// Adiciona os itens na lista
				itens.add( 
						new ToDoh(c.getLong(Utils.IDX_TODOH_ID)
								,c.getString(Utils.IDX_TODOH_TODO)
								,c.getInt(Utils.IDX_TODOH_DONE)
								,c.getLong(Utils.IDX_TODOH_DATA)) 
						);
			} while(c.moveToNext()); // enquanto ainda houver resultado se mantem em LOOP
		}
		adapter.notifyDataSetChanged();
	}
	
	public void mostraDialogo(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Atencao").setMessage("Deseja excluir este ToDoh! ?");
		builder.setPositiveButton("SIM!", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Deletando item!", Toast.LENGTH_LONG).show();
				
				if( db.removeTodoh( itens.get(indiceRemove).id ) > 0 ) { // remove do bd
//					Log.d(Utils.APP_NAME,"ID sendo deletado: "+itens.get(indiceRemove).id);
					mNotificationManager.cancel((int) itens.get(indiceRemove).id);
					itens.remove(indiceRemove); // remove da lista, assim nao precisa refazer a query inteira.
				}
				adapter.notifyDataSetChanged(); // avisa que mudou
			}
		});
		
		builder.setNegativeButton("NAO!", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Ufa... quase!", Toast.LENGTH_LONG).show();
			}
		});
		builder.create().show();
	}
	
	/////////////////////////////////
	
	public class ToDoh {
		private long id;
		private String nome;
		private int done;
		private long data;
		
		
		public ToDoh(long id, String nome, int done, long data) {
			super();
			this.id = id;
			this.nome = nome;
			this.done = done;
			this.data = data;
		}
		
		public long getId(){
			return id;
		}
		public void setId(long id){
			this.id = id;
		}
		public String getNome() {
			return nome;
		}
		public void setNome(String nome) {
			this.nome = nome;
		}
		public int getDone() {
			return done;
		}
		public void setDone(int done) {
			this.done = done;
		}
		public long getData() {
			return data;
		}
		public void setData(long data) {
			this.data = data;
		}
	}
	
	class AdapterToDoh extends BaseAdapter {
		Context context;
		LayoutInflater inflater;
		
		public AdapterToDoh(Context context){
			this.context = context;
			// Inflate eh utilizado para expandir o layout
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return itens.size();
		}

		@Override
		public Object getItem(int indice) {
			// TODO Auto-generated method stub
			return itens.get(indice);
		}

		@Override
		public long getItemId(int indice) {
			// TODO Auto-generated method stub
			return indice;
		}

		@Override
		public View getView(int indice, View arg1, ViewGroup arg2) {
			// Popula a lista..
			View linha = arg1;
			Calendar cal = Calendar.getInstance();
			// Formato de data utilizado
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm",Locale.getDefault()); 
			String dataFormatada;
			
			
			if(linha == null) {
				linha = inflater.inflate(R.layout.todoh_linha,arg2,false);
			}
			
			TextView topText = (TextView) linha.findViewById(R.id.toptext);
			TextView bottomText = (TextView) linha.findViewById(R.id.bottomtext);
			
			// Seta texto para titulo do todoh
			topText.setText(itens.get(indice).nome);
			
			// Recupera a data em milisegundos.
			cal.setTimeInMillis(itens.get(indice).data);
			// Formata a data
			dataFormatada = sdf.format(cal.getTime());
			
			// Seta o texto da segunda label.
			bottomText.setText(dataFormatada);
			
			
			if( itens.get(indice).done == 1 ){
				topText.setTextColor(Color.DKGRAY);
				topText.setPaintFlags(topText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			} else {
				topText.setTextColor(Color.BLACK);
				topText.setPaintFlags(topText.getPaintFlags() &(~Paint.STRIKE_THRU_TEXT_FLAG));
			}
			
			return linha;
		}
	}
}
