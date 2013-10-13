package antoniocasado.actionbubbles;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ScoresDB extends SQLiteOpenHelper {
	private SQLiteDatabase db;
	
	public ScoresDB(Context context) {
		super(context, "defbubbles", null, 5);
		db = this.getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		this.db = db;
		// create tables
		db.execSQL("CREATE TABLE scores (" +
			"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
			"name TEXT, " +
			"score INTEGER, " +
			"bubbles INTEGER, " +
			"level INTEGER, " +
			"duration TEXT, " +
			"timestamp TEXT)");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		this.db = db;
	}
	
	public void save(String name, int score, int bubbles, int level) {
		db.execSQL("INSERT INTO scores (name,score,bubbles,level) VALUES ('"+name+"',"+score+","+bubbles+","+level+")");
	}
	
	public void delete() {
		db.execSQL("DELETE FROM scores");
	}
	
	public ArrayList<String[]> getScores() {
		Cursor c = db.rawQuery("SELECT name,score,bubbles,level FROM scores ORDER BY score DESC", null);
		ArrayList<String[]> scorelist = new ArrayList<String[]>();
		for (int i = 0; i < 10 && c.moveToNext(); i++) {
			String[] res = new String[4];
			res[0] = c.getString(0);
			res[1] = Integer.toString(c.getInt(1));
			res[2] = Integer.toString(c.getInt(2));
			res[3] = Integer.toString(c.getInt(3));
			scorelist.add(res);
		}
		return scorelist;
	}
}
