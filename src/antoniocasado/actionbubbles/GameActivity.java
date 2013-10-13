package antoniocasado.actionbubbles;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class GameActivity extends Activity {
	GameInstance myGame;
	GameView gameView;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gamelayout);
        
        gameView = new GameView(this);
        FrameLayout frame = (FrameLayout) findViewById(R.id.game_frame);
        frame.addView(gameView);
        if (myGame != null && myGame.GAME_PAUSED) {
        	gameView.myGame = myGame;
        } else {
        	myGame = gameView.startGame();
        }
        
        // check every 10 seconds to see if we should display ads
        final AdView ads = (AdView)findViewById(R.id.adView);
        new Thread(new Runnable() { public void run() {
        	while (true) try {
        		Thread.sleep(10000);
        		if (ActionBubbles.getApp().getGamesPlayed() >= ActionBubbles.GAMES_UNTIL_ADS) {
        			runOnUiThread(new Runnable() { public void run() {
        				ads.loadAd(new AdRequest());
        			}});
        			break;
        		}
        		
        	} catch (InterruptedException e) {
        		
        	}
        }}).start();
    }
    
    @Override
    public void onBackPressed() {
//       startActivity(new Intent(this, MenuActivity.class));
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	myGame = gameView.pauseGame();
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	myGame = gameView.pauseGame();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
    	gameView = new GameView(this);
        FrameLayout frame = (FrameLayout) findViewById(R.id.game_frame);
        frame.addView(gameView);
        
    	if (myGame != null && myGame.GAME_START) {
        	gameView.myGame = myGame;
    	} else {
    		myGame = gameView.startGame();
    	}
        
    }
}
