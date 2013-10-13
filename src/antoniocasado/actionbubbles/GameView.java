package antoniocasado.actionbubbles;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.WindowManager;

public class GameView extends SurfaceView implements Callback, OnGestureListener {
	Context ctx;
	int minScore;
	GameInstance myGame;
	
	// drawing objects
	private float textSize;
    private CanvasThread canvasThread;
    private GestureDetector gestureScanner;
	private Paint scorePaint, gameOverPaint, gameStartPaint, gameEndPaint;
	
	// sound objects
	private MediaPlayer soundPop, soundMiss;
    
//    private static float DECREASE_RATE = 0.005f;
    
    // tmp
    String scoreText = "Score: ";
    String levelText = "Level: ";
    String bubblesText = "Bubbles: ";
    String maxScoreText = "Max score: ";
    String gameOverText = "GAME OVER";
    String gameStartText = "START GAME";
    String gamePausedText = "RESUME";
    String highScoresText = "SWIPE DOWN FOR HIGH SCORES";
    
    public GameInstance startGame() {
		myGame = new GameInstance();
		return myGame;
    }
    
    public void resumeGame(GameInstance game) {
    	myGame = game;
		myGame.GAME_PAUSED = false;
    }
    
    public GameInstance pauseGame() {
    	if (myGame.GAME_START && !myGame.GAME_OVER) {
    		myGame.GAME_PAUSED = true;
    	}
		return myGame;
    }
    
    public GameView(Context context) {
        super(context);
        this.ctx = context;
        this.getHolder().addCallback(this);
        this.canvasThread = new CanvasThread(getHolder());
        this.setFocusable(true);
        gestureScanner = new GestureDetector(context, this);
        gestureScanner.setIsLongpressEnabled(false);
        
        // get screen size
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		@SuppressWarnings("deprecation")
		int width = display.getWidth() - 40;
		@SuppressWarnings("deprecation")
		int height = display.getHeight() - 40;
        
        // initialize paint objects
		scorePaint = new Paint();
		scorePaint.setStyle(Paint.Style.FILL);
		scorePaint.setColor(Color.LTGRAY);
		scorePaint.setAntiAlias(true);
		
		gameOverPaint = new Paint();
		gameOverPaint.setStyle(Paint.Style.FILL);
		gameOverPaint.setColor(Color.RED);
		gameOverPaint.setAntiAlias(true);
		gameOverPaint.setTextAlign(Align.CENTER);
		
		gameStartPaint = new Paint();
		gameStartPaint.setStyle(Paint.Style.FILL);
		gameStartPaint.setColor(Color.LTGRAY);
		gameStartPaint.setStrokeWidth(10);
		gameStartPaint.setAntiAlias(true);
		gameStartPaint.setTextAlign(Align.CENTER);
		
		gameEndPaint = new Paint();
		gameEndPaint.setStyle(Paint.Style.FILL);
		gameEndPaint.setColor(Color.LTGRAY);
		gameEndPaint.setAntiAlias(true);
		
		// initialize sound objects
		soundPop = MediaPlayer.create(ctx, R.raw.bubble_pop);
		soundMiss = MediaPlayer.create(ctx, R.raw.bubble_miss);
//		try {
//			soundPop.prepare();
//			soundMiss.prepare();
//		} catch (IllegalStateException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		// set text sizes
		textSize = 10;
		while (gameOverPaint.measureText(gameOverText) < width && textSize * 4 < height) {
			gameOverPaint.setTextSize(textSize++);
			gameStartPaint.setTextSize(textSize - 10);
			gameEndPaint.setTextSize(textSize / 2);
			scorePaint.setTextSize(textSize / 4);
		}
		
		// get the minimum score needed to be in the list
		ScoresDB db = new ScoresDB(ctx);
		ArrayList<String[]> scoreList = db.getScores();
		if (scoreList.size() < 10) {
			minScore = 1;
		} else {
			minScore = Integer.parseInt(scoreList.get(10)[1]);
		}
    }
    
	/**
	 * the thread that performs all the heavy-lifting of drawing
	 */
    private class CanvasThread extends Thread {
    	
        private SurfaceHolder mSurfaceHolder;
        private boolean mRun = false;
 
        public CanvasThread(SurfaceHolder holder) {
            this.mSurfaceHolder = holder;
        }
 
        public void setRunning(boolean run) {
            this.mRun = run;
        }
 
        @Override
        public void run() {
        	
        	// actual game drawing
        	while (mRun) {
	            Canvas c = null;
	            try {
	            	c = mSurfaceHolder.lockCanvas();
	            	synchronized (mSurfaceHolder) {
	            		if (myGame != null) {
	            			defbubbles(c);
	            		}
	            	}
	            	mSurfaceHolder.unlockCanvasAndPost(c);
				} catch (NullPointerException e) {
					e.printStackTrace();
	            } catch (IllegalArgumentException e) {
	            	e.printStackTrace();
	            }
        	}
        }
    }
    
    /*
     * game engine main method, called every frame
     */
    public void defbubbles(Canvas c) {
    	
    	// initialize canvas
    	c.drawColor(0, Mode.CLEAR);
    	
    	if (!myGame.GAME_START) {
    		
    		c.drawText(
    				gameStartText, c.getWidth() / 2, 
    				c.getHeight() / 2 + textSize / 2, 
    				gameStartPaint);
    		
    		// draw up arrow
    		Path arrow = new Path();
    		arrow.moveTo(c.getWidth() / 2 - textSize, c.getHeight() / 2 - textSize / 2);
    		arrow.lineTo(c.getWidth() / 2 + textSize, c.getHeight() / 2 - textSize / 2);
    		arrow.lineTo(c.getWidth() / 2, c.getHeight() / 2 - textSize * 1.5f);
    		arrow.close();
    		
    		c.drawPath(arrow, gameStartPaint);
    		return;
    		
    	} else if (myGame.GAME_PAUSED) {
    		
    		c.drawText(
    				gamePausedText, c.getWidth() / 2, 
    				c.getHeight() / 2 + textSize / 2, 
    				gameStartPaint);
    		
    		// draw up arrow
    		Path arrow = new Path();
    		arrow.moveTo(c.getWidth() / 2 - textSize, c.getHeight() / 2 - textSize / 2);
    		arrow.lineTo(c.getWidth() / 2 + textSize, c.getHeight() / 2 - textSize / 2);
    		arrow.lineTo(c.getWidth() / 2, c.getHeight() / 2 - textSize * 1.5f);
    		arrow.close();
    		
    		c.drawPath(arrow, gameStartPaint);
    		return;
    	}
    	
    	// draw pain background if we just missed
    	if (myGame.painTime > System.currentTimeMillis()) {
    		c.drawColor(Color.RED);
    		myGame.painTime = 0;
    	}
    	
    	// update score?
    	if (myGame.nextScore > myGame.score) {
    		myGame.score++;
    	} else if (myGame.nextScore < myGame.score) {
    		myGame.score--;
    	}
    	
    	// draw all existing bubbles
    	ArrayList<Bubble> deleteBubbles = new ArrayList<Bubble>();
    	for (Bubble b : myGame.bubbleList) {
    		
    		// if the bubble does no longer exist
    		if (b.gone) {
    			deleteBubbles.add(b);
    			
			// if we paused the game for more than 0.5 seconds
    		} else if (System.currentTimeMillis() > b.lastTime + 500) {
    				b.lastTime = System.currentTimeMillis();
    		
    		// if the bubble is on screen
    		} else {
    			b.draw(c);
    		}
    	}
    	
    	// delete old bubbles
    	for (Bubble b : deleteBubbles) {
    		
    		// if the bubble was never burst... ouch!
    		if (!b.burst) {
    			missedBubble();
    		}
    		myGame.bubbleList.remove(b);
    		b = null;
    	}
    	deleteBubbles.clear();
    	
    	// add new ones
    	long currTime = System.currentTimeMillis();
    	int bubble_timeout = (int) Math.max(
    			GameInstance.TIME_BETWEEN_BUBBLES_BASE
    			/ GameInstance.CHEAT_MULTIPLIER
    			- GameInstance.TIME_BETWEEN_BUBBLES_LEVEL_MULTIPLIER
    			* myGame.level, 
    			GameInstance.TIME_BETWEEN_BUBBLES_MIN);
    	if (currTime - myGame.bubbleTime > bubble_timeout) {
    		Log.d("bubble", "new bubble created");
    		myGame.totalBubbles++;
    		myGame.bubbleTime = currTime;
    		myGame.bubbleList.add(Bubble.create(myGame.level));
    		
        	// level up?
        	if (!myGame.GAME_OVER && myGame.totalBubbles % 10 == 0) {
        		myGame.level++;
        	}
    	}
    	
    	// did the user already lose this game?
    	if (myGame.GAME_OVER) {
    		c.drawText(gameOverText, c.getWidth() / 2, textSize, gameOverPaint);
	    	c.drawText(maxScoreText + myGame.maxScore, 20, textSize * 2, gameEndPaint);
	    	c.drawText(bubblesText + myGame.burstBubbles, 20, textSize * 2.5f, gameEndPaint);
	    	c.drawText(levelText + myGame.level, 20, textSize * 3, gameEndPaint);
	    	
	    	Paint tmpPaint = new Paint(gameEndPaint);
	    	tmpPaint.setTextAlign(Align.RIGHT);
	    	c.drawText("HIGH", c.getWidth() - 20, textSize * 2, tmpPaint);
	    	c.drawText("SCORES", c.getWidth() - 20, textSize * 2.5f, tmpPaint);
	    	
	    	// draw down arrow
    		Path arrow = new Path();
    		arrow.moveTo(c.getWidth() - 20, textSize * 2.6f);
    		arrow.lineTo(c.getWidth() - 20 - textSize / 2, textSize * 2.6f);
    		arrow.lineTo(c.getWidth() - 20 - textSize / 4, textSize * 3);
    		arrow.close();
    		c.drawPath(arrow, tmpPaint);
    	
    	// otherwise draw bubbles, score and level
    	} else {
	    	c.drawText(scoreText + myGame.score, 20, textSize * 0.25f, scorePaint);
	    	c.drawText(bubblesText + myGame.burstBubbles, 20, textSize * 0.5f, scorePaint);
	    	c.drawText(levelText + myGame.level, 20, textSize * 0.75f, scorePaint);
    	}

    }
    
    /**
     * This method is called every time a bubble goes unpopped or we missed a hit
     */
    private void missedBubble() {
    	
    	if (!myGame.GAME_OVER) {
    		
    		// play sound
        	try {
        		if (soundMiss.isPlaying()) {
        			soundMiss.seekTo(0);
        		} else {
        			soundMiss.start();
        		}
    		} catch (IllegalStateException e1) {
    			soundMiss.prepareAsync();
    			e1.printStackTrace();
    		}
        	
    		myGame.painTime = System.currentTimeMillis() + GameInstance.PAIN_TIMEOUT;
    		myGame.nextScore -= myGame.level * GameInstance.MISSED_BUBBLE_LEVEL_MULTIPLIER;
    		
    		// when the score drops below zero, the game is over
	    	if (myGame.nextScore < 0) {
	    		myGame.nextScore = 0;
	    		myGame.GAME_OVER= true;
	    		ActionBubbles.getApp().addGamePlayed();
	    		
	    		// high score?
	    		if (myGame.maxScore > minScore) {
	    			final Intent intent = new Intent(ctx, HighScoreActivity.class);
	    			intent.putExtra("scoredata", myGame.maxScore+","+myGame.burstBubbles+","+myGame.level);
	    			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    			new Thread(new Runnable() { public void run() {
	    				try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
	    				ctx.getApplicationContext().startActivity(intent);
	    			}}).start();
	    		}
	    	}
    	}
    }
    
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder surface) {
		canvasThread.setRunning(true);
		
		try {
			if(!canvasThread.isAlive()) canvasThread.start();
		} catch (IllegalThreadStateException e) {
			canvasThread = new CanvasThread(surface);
			canvasThread.start();
		}
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		boolean retry = true;
		canvasThread.setRunning(false);
        while (retry) {
            try {
            	canvasThread.join();
                retry = false;
            } catch (InterruptedException e) {}
        }		
	}
	
	/**
	 * Implement the touch functionality
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		// parse touch event
	    gestureScanner.onTouchEvent(event);
 		return true;
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if (e1.getY() - e2.getY() > 100) {
			if (myGame.GAME_OVER) {
				startGame();
				
			} else if (!myGame.GAME_START) {
				myGame.GAME_START = true;
				
			} else if (myGame.GAME_PAUSED) {
				myGame.GAME_PAUSED = false;
	
			} else {
				pauseGame();
			}
		} else if (e2.getY() - e1.getY() > 100) {
			if (myGame.GAME_OVER) {
				ctx.startActivity(new Intent(ctx, ListActivity.class));
			}
		}
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	// this is never called because of the setLongPressEnabled(false) call on gestureDetector
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return true;
	}

	/**
	 * Handle "tap" on items that are not enabled here
	 */
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		
		if (myGame.GAME_OVER) {
			
		} else if (!myGame.GAME_START) {
//			myGame.GAME_START = true;
			
		} else if (myGame.GAME_PAUSED) {
//			myGame.GAME_PAUSED = false;

		} else {
		
			float x = e.getX();
			float y = e.getY();
			
			// when the user taps a bubble
			boolean hitFlag = false;
			for (int i = myGame.bubbleList.size() - 1; i >= 0; i--) {
				Bubble b = myGame.bubbleList.get(i);
				
				if (!b.burst 
				&& Math.abs(x - b.absoluteX) < b.r + GameInstance.MISS_ALLOWANCE 
				&& Math.abs(y - b.absoluteY) < b.r + GameInstance.MISS_ALLOWANCE) {
					
					// play sound
			    	try {
			    		if (soundPop.isPlaying()) {
			    			soundPop.seekTo(0);
			    		} else {
					    	soundPop.start();
			    		}
					} catch (IllegalStateException e1) {
						soundPop.prepareAsync();
						e1.printStackTrace();
					}
					
					// burst bubble
					myGame.nextScore += b.burst();
					hitFlag = true;
					myGame.burstBubbles++;
					
					// max score?
					if (myGame.nextScore > myGame.maxScore) {
						myGame.maxScore = myGame.nextScore;
					}
					
					break;
					
				} else {
					Log.d("bubble", "Missed by " + (Math.abs(x - b.absoluteX) - b.r) + "," + (Math.abs(y - b.absoluteY) - b.r));
				}
			}
			
			// did the user miss?
			if (!hitFlag) {
				missedBubble();
			}
		}

		return true;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
	}
}

class Bubble {
	
	float x, y, r, absoluteX, absoluteY;
	Paint insidePaint, borderPaint;
	long currTime, lastTime;
	boolean burst, gone;
	int level, points;
	
	Bubble(float x, float y, int level) {
		this.x = x;
		this.y = y;
		this.r = (float) (100.0f - Math.random() * 5.0f);
		this.level = level;
		
        // initialize paint objects
		insidePaint = new Paint();
		insidePaint.setStyle(Paint.Style.FILL);
		insidePaint.setColor(Color.WHITE);
		insidePaint.setAntiAlias(true);
		
		borderPaint = new Paint();
		borderPaint.setStyle(Paint.Style.FILL);
		borderPaint.setColor(Color.DKGRAY);
		borderPaint.setAntiAlias(true);
		
		lastTime = System.currentTimeMillis();
		currTime = System.currentTimeMillis();
	}
	
	static Bubble create(int level) {
		float x = (float) (Math.random() * 1.5 - 0.75);
		float y = (float) (Math.random() * 1.5 - 0.75);
		return new Bubble(x, y, level);
	}
	
	void draw(Canvas c) {
		r = calcRadius();
		
		if (r == 0.0f) {
			gone = true;
		} else if (burst) {
			borderPaint.setTextSize(r);
			c.drawText(String.valueOf(points), absoluteX, absoluteY, borderPaint);
		} else {
			absoluteX = (x + 1) * c.getWidth() / 2;
			absoluteY = (y + 1) * c.getHeight() / 2;
			c.drawCircle(absoluteX, absoluteY, r + 5, borderPaint);
			c.drawCircle(absoluteX, absoluteY, r, insidePaint);
		}

		lastTime = System.currentTimeMillis();
	}
	
	public int burst() {
		burst = true;
		points = (int) (level * 100 / r);
		level = 10;
    	return points;
    }
	
	private float calcRadius() {
		return Math.max(
				(float)(r // base
						- (System.currentTimeMillis() - lastTime) 
						* GameInstance.DECREASE_RATE // decrease rate
						* Math.sqrt(level) // level
						* GameInstance.CHEAT_MULTIPLIER) //  cheat multiplier
						, 0.0f);
	}
}

class GameInstance {
	
	// game vars
	public long bubbleTime, painTime;
	public ArrayList<Bubble> bubbleList;
	public int burstBubbles, totalBubbles;
	public int level, score, nextScore, maxScore;
	public boolean GAME_OVER, GAME_START, GAME_PAUSED;
    
    // game params
	public static int PAIN_TIMEOUT = 200;
	public static int MISS_ALLOWANCE = 20;
	public static int SCORE_FADE_SPEED = 10;
	public static float DECREASE_RATE = 0.02f;
	public static int TIME_BETWEEN_BUBBLES_MIN = 200;
	public static int TIME_BETWEEN_BUBBLES_BASE = 1000;
    public static int MISSED_BUBBLE_LEVEL_MULTIPLIER = 5;
    public static int TIME_BETWEEN_BUBBLES_LEVEL_MULTIPLIER = 50;
    public static float CHEAT_MULTIPLIER = 0.1f; 
    
    public GameInstance() {
    	
    	// initialize bubble list and scores
    	level = 1;
    	score = maxScore = nextScore = 0;
    	bubbleList = new ArrayList<Bubble>();
    }
    
}