package antoniocasado.actionbubbles;

import android.app.Application;
import android.content.Context;

public class ActionBubbles extends Application {
	public static int GAMES_UNTIL_ADS = 3;
    private static Context mContext;
    private int gamesPlayed;
    
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this.getApplicationContext();
    }
    
	public static ActionBubbles getApp() {
		return (ActionBubbles) mContext;
	}

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void addGamePlayed() {
        this.gamesPlayed++;
    }
}
