package antoniocasado.actionbubbles;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ListActivity extends Activity {
	ScoresDB db;
	ListView lv;
	
	ArrayList<String[]> scores;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.highscores);
		
		// get the high scores from database
		db = new ScoresDB(this.getApplicationContext());
		scores = db.getScores();
		
		// Create an ArrayAdapter, that will actually make the Strings above appear in the ListView
		ArrayAdapter<String> aa = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
		
		// populate the array adapter
		for (int i = 0; i < 10 && i < scores.size(); i++) {
			String[] score = scores.get(i);
			aa.add((i + 1) + ". " + score[0] + ": " + score[1]);
		}
		
		// hook the adapter to this view
		lv = (ListView) findViewById(R.id.listView);
		lv.setAdapter(aa);
		
		// handle long click
//		lv.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
//			@Override
//			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo cinfo) {
//				menu.setHeaderTitle("Context Menu");
//				menu.add("Delete score");
//		}});
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> av, View v, int position, long id) {
				String name = scores.get(position)[0];
				String score = scores.get(position)[1];
				String bubbles = scores.get(position)[2];
				String level = scores.get(position)[3];
				AlertDialog alert = (new AlertDialog.Builder(ListActivity.this)).create();
				alert.setMessage(
						getString(R.string.score_label) + " " + score + "\n" +
						getString(R.string.bubbles_label) + " " + bubbles + "\n" +
						getString(R.string.level_label) + " " + level);
				alert.setTitle(name);
				alert.show();
			}

		});
			
	}
	@Override
	public boolean onContextItemSelected(android.view.MenuItem aitem) {
		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) aitem.getMenuInfo();
		String name = (String)lv.getAdapter().getItem(menuInfo.position);
//		db.delete(name); // delete record
		
		// reload the list view
		Toast.makeText(this, "Deleted: " + name, Toast.LENGTH_LONG).show();
		this.onCreate(null);
		return true;
	}

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
			case android.R.id.home:
//				startActivity(new Intent(this, MenuActivity.class));
				return true;
		}
		
		return(super.onOptionsItemSelected(item));
	}
    
    @Override
    public void onBackPressed() {
       startActivity(new Intent(this, GameActivity.class));
    }
}