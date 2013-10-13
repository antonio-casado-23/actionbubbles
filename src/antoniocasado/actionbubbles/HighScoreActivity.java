package antoniocasado.actionbubbles;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class HighScoreActivity extends Activity {
	ScoresDB db;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newscore);
        db = new ScoresDB(this);
        
        // get info
        final EditText username = (EditText)findViewById(R.id.nameText);
        String[] scoredata = getIntent().getStringExtra("scoredata").split(",");
        final int score = Integer.parseInt(scoredata[0]);
        final int bubbles = Integer.parseInt(scoredata[1]);
        final int level = Integer.parseInt(scoredata[2]);
        
        // display score
        TextView scoreLabel = (TextView)findViewById(R.id.scoreText);
        TextView bubbleLabel = (TextView)findViewById(R.id.bubblesText);
        TextView levelLabel = (TextView)findViewById(R.id.levelText);
        scoreLabel.setText(scoreLabel.getText() + " " + score);
        bubbleLabel.setText(bubbleLabel.getText() + " " + bubbles);
        levelLabel.setText(levelLabel.getText() + " " + level);
        
        // save when name isn't null
        ((Button)findViewById(R.id.saveButton)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = username.getText().toString().trim();
				if (name.length() > 0) {
					db.save(name, score, bubbles, level);
					startActivity(new Intent(HighScoreActivity.this, ListActivity.class));
				}
			}
        });
    }
    
    @Override
    public void onBackPressed() {}
}