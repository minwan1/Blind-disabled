package com.example.lastclient;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends Activity
{
	private Button btn_Start;
	private Button button1;
	private Button button2;
	
	public static MediaPlayer p_generic_mode_start;
	public static MediaPlayer p_handicap_mode_start;
	public MediaPlayer p_game_help;
	public MediaPlayer p_main_help;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);	
		
		btn_Start=(Button) findViewById(R.id.btn_Start);
		button1=(Button) findViewById(R.id.button1);
		button2=(Button) findViewById(R.id.button2);
		
		p_generic_mode_start = MediaPlayer.create(this, R.drawable.generic_mode_start);
		p_handicap_mode_start = MediaPlayer.create(this, R.drawable.handicap_mode_start);
		p_game_help = MediaPlayer.create(this, R.drawable.game_help);
		p_main_help = MediaPlayer.create(this, R.drawable.main_help);
		
		btn_Start.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				p_handicap_mode_start.start();
				p_main_help.stop();
				Intent goInt=new Intent(StartActivity.this,Tetris4Android.class);
				BoardView.colors = new int[8];
				BoardView.colors[2] = 0xff00c800; // green
				BoardView.count=300;
				startActivity(goInt);
			}
		});
		
		button1.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				p_generic_mode_start.start();
				p_main_help.stop();
				Intent goInt=new Intent(StartActivity.this,Tetris4Android.class);
				BoardView.colors = new int[8];
				BoardView.count=300;
				BoardView.colors[2] = 0xff000000; // green
				startActivity(goInt);
			}
		});
		
		button2.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				p_game_help.start();
				p_main_help.stop();
			}
		});
	}
}
