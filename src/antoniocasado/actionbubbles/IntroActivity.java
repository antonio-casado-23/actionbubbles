package antoniocasado.actionbubbles;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.revmob.RevMob;
import com.revmob.RevMobAdsListener;

public class IntroActivity extends Activity {
	private RevMob revmob;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.introlayout);
        
        new Thread(new Runnable() { public void run() {
        	
        	// wait for the content view to load
        	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	
	        revmob = RevMob.start(IntroActivity.this);
	        revmob.showFullscreen(IntroActivity.this, new RevMobAdsListener() {
	        	
	    		@Override
	    		public void onRevMobAdReceived() {
	    		}
	
	    		@Override
	    		public void onRevMobAdNotReceived(String message) {
	    			startActivity(new Intent(IntroActivity.this, GameActivity.class));
	    		}
	
	    		@Override
	    		public void onRevMobAdDismiss() {
	    			startActivity(new Intent(IntroActivity.this, GameActivity.class));
	    		}
	
	    		@Override
	    		public void onRevMobAdClicked() {
	    		}
	
	    		@Override
	    		public void onRevMobAdDisplayed() {
	    		}
	    	});
	        
        }}).start();
    }
    
    @Override
    public void onBackPressed() {
       Intent setIntent = new Intent(Intent.ACTION_MAIN);
       setIntent.addCategory(Intent.CATEGORY_HOME);
       setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       startActivity(setIntent);
    }
    
    
}