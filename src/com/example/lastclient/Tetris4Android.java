package com.example.lastclient;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.lastclient.*;

public class Tetris4Android extends Activity implements OnClickListener,
		OnCompletionListener
{
	public static final String TAG = "Tetris"; // for Logging

	// 전체 판의 크기 (칸과 줄 수)
	public final static int COLS = 7; // 판의 칸 수
	public final static int ROWS = 7; // 판의 줄 수

	private static final KeyEvent KEYCODE_DPAD_CENTER = null;

	static final KeyEvent KEYCODE_DPAD_LEFT = null;

	private static final KeyEvent KEYCODE_DPAD_RIGHT = null;

	private static final KeyEvent KEYCODE_DPAD_DOWN = null;

	private static final int OPTION = 0;

	private TetrisView tetrisView;
	static Button btn_Start;
	static Button btn_letf;
	private Button btn_right;
	private Button btn_down;

	private MediaPlayer P_readygo;
	public static MediaPlayer P_gameover;
	public static MediaPlayer P_soundsearch;
	public static MediaPlayer P_stageclear;
	public static MediaPlayer P_blockremove;
	public static MediaPlayer P_wall;
	public static MediaPlayer P_bgm;
	public static MediaPlayer P_seven;
	public static MediaPlayer P_six;
	public static MediaPlayer P_five;
	public static MediaPlayer P_four;
	public static MediaPlayer P_three;
	public static MediaPlayer P_two;
	public static MediaPlayer P_one;
	public static MediaPlayer P_gameclear;
	public static MediaPlayer p_result;
	public static MediaPlayer p_num_start;
	public static MediaPlayer p_r_start;
	public static MediaPlayer p_s_ten;
	public static MediaPlayer p_s_nine;
	public static MediaPlayer p_s_two;
	public static MediaPlayer p_s_three;
	public static MediaPlayer p_s_four;
	public static MediaPlayer p_s_five;
	public static MediaPlayer p_s_six;
	public static MediaPlayer p_s_seven;
	public static MediaPlayer p_s_eight;
	public static MediaPlayer p_s_hundred;
	public static MediaPlayer p_score_sound;

	private String mSdPath;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		Log.v(TAG, "onCreate()");

		setContentView(R.layout.maint);

		P_readygo = MediaPlayer.create(this, R.drawable.start);
		P_gameover = MediaPlayer.create(this, R.drawable.gameoverr);
		P_soundsearch = MediaPlayer.create(this, R.drawable.soundsearch);
		P_stageclear = MediaPlayer.create(this, R.drawable.stageclear);
		P_blockremove = MediaPlayer.create(this, R.drawable.lineclear);
		P_wall = MediaPlayer.create(this, R.drawable.failed);
		P_one = MediaPlayer.create(this, R.drawable.one);
		P_two = MediaPlayer.create(this, R.drawable.two);
		P_three = MediaPlayer.create(this, R.drawable.three);
		P_four = MediaPlayer.create(this, R.drawable.four);
		P_five = MediaPlayer.create(this, R.drawable.five);
		P_six = MediaPlayer.create(this, R.drawable.six);
		P_seven = MediaPlayer.create(this, R.drawable.seven);
		p_result = MediaPlayer.create(this, R.drawable.resultt);
		p_num_start = MediaPlayer.create(this, R.drawable.num_start);
		p_r_start = MediaPlayer.create(this, R.drawable.r_start);
		p_s_ten = MediaPlayer.create(this, R.drawable.score_ten);
		p_s_hundred = MediaPlayer.create(this, R.drawable.score_hundred);
		p_s_two = MediaPlayer.create(this, R.drawable.score_two);
		p_s_three = MediaPlayer.create(this, R.drawable.score_three);
		p_s_four = MediaPlayer.create(this, R.drawable.score_four);
		p_s_five = MediaPlayer.create(this, R.drawable.score_five);
		p_s_six = MediaPlayer.create(this, R.drawable.score_six);
		p_s_seven = MediaPlayer.create(this, R.drawable.score_seven);
		p_s_eight = MediaPlayer.create(this, R.drawable.score_eight);
		p_s_nine = MediaPlayer.create(this, R.drawable.score_nine);
		p_score_sound = MediaPlayer.create(this, R.drawable.score_sound);

		// P_bgm = MediaPlayer.create(this, R.drawable.bgm);

		// mSdPath =
		// Environment.getExternalStorageDirectory().getAbsolutePath();

		tetrisView = (TetrisView) findViewById(R.id.Tetris);
		TextView messageView = (TextView) findViewById(R.id.MessageDisplay);
		tetrisView.setMessageView(messageView);

		btn_Start = (Button) findViewById(R.id.btn_start);
		btn_letf = (Button) findViewById(R.id.btn_left);
		btn_right = (Button) findViewById(R.id.btn_right);
		btn_down = (Button) findViewById(R.id.btn_down);

		btn_Start.setOnClickListener(this);
		btn_letf.setOnClickListener(this);
		btn_right.setOnClickListener(this);
		btn_down.setOnClickListener(this);

		BoardView boardView = new BoardView(this);
		// boardView.vie(this);

		// Tetris4Android.P_bgm.start();
	}
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		case R.id.btn_down:
			Log.v(TAG, "down");
			tetrisView.onKeyDown(20, KEYCODE_DPAD_DOWN);
			btn_Start.setText(String.valueOf(BoardView.count));
			break;
		case R.id.btn_left:
			Log.v(TAG, "left");
			tetrisView.onKeyDown(21, KEYCODE_DPAD_LEFT);
			BoardView.count-=10;
			btn_Start.setText(String.valueOf(BoardView.count));
			break;
		case R.id.btn_right:
			Log.v(TAG, "right");
			tetrisView.onKeyDown(22, KEYCODE_DPAD_RIGHT);
			BoardView.count-=10;
			btn_Start.setText(String.valueOf(BoardView.count));
			break;
		case R.id.btn_start:
			Log.v(TAG, "start");
			BoardView.count=300;
			tetrisView.onKeyDown(23, KEYCODE_DPAD_CENTER);
			btn_Start.setText(String.valueOf(BoardView.count));
			// Tetris4Android.P_bgm.pause();
			// P_readygo.start();
			//p_result.start();
			break;
		default:
			break;
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(0,OPTION,menu.NONE,"랭킹추가");
		return true;	
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case OPTION:
			Intent goIntent=new Intent(Tetris4Android.this,MainActivity.class);
			startActivity(goIntent);
			break;	
		}
		return false;
	}
	@Override
	public void onCompletion(MediaPlayer mp)
	{
		// TODO Auto-generated method stub
	}
}