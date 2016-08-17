package com.example.lastclient;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

public class TetrisView extends BoardView
{

	// public static MediaPlayer P_gameover;
	// P_gameover = MediaPlayer.create(this, R.drawable.gameover);

	private static final String TAG = null;
	/**
	 * Create a simple handler that we can use to cause animation to happen. We
	 * set ourselves as a target and we can use the sleep() function to cause an
	 * update/invalidate to occur at a later date.
	 */
	private RefreshHandler mRedrawHandler = new RefreshHandler();

	class RefreshHandler extends Handler
	{

		@Override
		public void handleMessage(Message msg)
		{
			// TetrisView.this.update();//정지
			//newCreateLine();
			TetrisView.this.invalidate();
		}

		public void sleep(long delayMillis)
		{
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
	};

	private TextView messageView = null;

	public TetrisView(Context context)
	{
		super(context);
	}

	public TetrisView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public TetrisView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	void init()
	{
		setFocusable(true);

		super.init();
	}

	public void update()
	{

		if (getMode() == RUNNING)
		// newCreateLine();
		{

			if (!moveCurrentPiece(0, 1, false))
			{

				if (removeCompleteLines())
				{
					// if (count == 14)
					// {
					// stage++;
					// Tetris4Android.P_stageclear.start();
					// try
					// {
					// Thread.sleep(3000); // 1초 = 1000밀리초
					// } catch (InterruptedException ignore)
					// {
					// }
					// createLine(4);
					// }
					stage++;
					count=count+50;
				
					
					
				
					
	
					
					

					if (stage == 1)
					{
						// newCreateLine();

						//Tetris4Android.P_stageclear.start();
						// setMode(END);
						// Log.v(TAG, "값 : " + stage);
					}
				} else
				{

					Tetris4Android.P_wall.start();
				}

				// check();
				if (!newBlock())
				{
					if (messageView != null)
					{
						messageView.setText(R.string.mode_end);
						messageView.setVisibility(VISIBLE);
					}

					// count = 0;
					setMode(PAUSE);
					Tetris4Android.P_gameover.start();
					return;
				}
			}
			mRedrawHandler.sleep(1000);
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent msg) // 키이벤트로 블록 제어
	{

		Log.v(Tetris4Android.TAG, "KEYDOWN : " + keyCode);
		switch (keyCode)
		{
		case KeyEvent.KEYCODE_DPAD_CENTER:
			Log.v(TAG, "가운데 버튼 활성화()");
			stage = 0;

			cleanBoard(); //초기화
			messageView.setVisibility(INVISIBLE);
			invalidate();//그려주는거야
			newBlock(); //블록 선택해줌
			setMode(RUNNING); //게임 시작중 상태
			update();
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			// if(pieceCurr[1].getX()==0)
			// {
			// //벽에 닿으면
			// //Tetris4Android.P_wall.start();
			// //this.moveCurrentPiece(6, 0, false);
			// }
			if (pieceCurr[1].getX() == 1)
			{
				Tetris4Android.P_one.start();
			}
			if (pieceCurr[1].getX() == 2)
			{
				Tetris4Android.P_two.start();
			}
			if (pieceCurr[1].getX() == 3)
			{
				Tetris4Android.P_three.start();
			}
			if (pieceCurr[1].getX() == 4)
			{
				Tetris4Android.P_four.start();
			}
			if (pieceCurr[1].getX() == 5)
			{
				Tetris4Android.P_five.start();
			}
			if (pieceCurr[1].getX() == 6)
			{
				Tetris4Android.P_six.start();
			}
			if (pieceCurr[1].getX() == 7)
			{
				Tetris4Android.P_seven.start();
			}

			if (pieceCurr[1].getX() == 0 && getMode() == RUNNING)
			{

				this.moveCurrentPiece(6, 0, false);
				invalidate();
				Tetris4Android.P_seven.start();
			} else
			{
				this.moveCurrentPiece(-1, 0, false);
				invalidate();
				Log.v(TAG, "출력확인");
			}
			break;

		case KeyEvent.KEYCODE_DPAD_RIGHT:
			// if(pieceCurr[1].getX()==6)
			// {
			// //Tetris4Android.P_wall.start();
			// this.moveCurrentPiece(-6, 0, false);
			// }
			//
			if (pieceCurr[1].getX() == 0)
			{
				Tetris4Android.P_two.start();
			}
			if (pieceCurr[1].getX() == 1)
			{
				Tetris4Android.P_three.start();
			}
			if (pieceCurr[1].getX() == 2)
			{
				Tetris4Android.P_four.start();
			}
			if (pieceCurr[1].getX() == 3)
			{
				Tetris4Android.P_five.start();
			}
			if (pieceCurr[1].getX() == 4)
			{
				Tetris4Android.P_six.start();
			}
			if (pieceCurr[1].getX() == 5)
			{
				Tetris4Android.P_seven.start();
			}

			if (getMode() == RUNNING && pieceCurr[1].getX() == 6)
			{
				this.moveCurrentPiece(-6, 0, false);
				Tetris4Android.P_one.start();
				invalidate();
			} else
			{
				this.moveCurrentPiece(1, 0, false);
				invalidate();
			}

			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			if (getMode() == RUNNING)
				this.moveCurrentPiece(0, 0, true);
			invalidate();
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			if (getMode() == RUNNING)
				while (moveCurrentPiece(0, 1, false))
					;
			update();// 정지를 위해
			invalidate();
			break;
		case KeyEvent.KEYCODE_SPACE:
			switch (getMode())
			{
			case RUNNING:
				setMode(PAUSE);
				messageView.setText(R.string.mode_pause);
				messageView.setVisibility(VISIBLE);
				break;
			case PAUSE:
				messageView.setVisibility(INVISIBLE);
				setMode(RUNNING);
				update();
				break;
			}
			break;
		}

		return super.onKeyDown(keyCode, msg);
	}

	public void setMessageView(TextView messageView)
	{
		this.messageView = messageView;
	}

	@Override
	public void setMode(byte mode)
	{

		switch (mode)
		{
		case RUNNING:
			messageView.setVisibility(INVISIBLE);
			super.setMode(mode);
			// update(); //정지
			break;
		case PAUSE:
			messageView.setText(R.string.mode_pause);
			messageView.setVisibility(VISIBLE);
			break;
		case END:
			messageView.setText(R.string.mode_end);
			messageView.setVisibility(VISIBLE);
			break;
		case READY:
			messageView.setText(R.string.mode_ready);
			messageView.setVisibility(VISIBLE);
			break;
		}

		super.setMode(mode);
	}

	protected void newCreateLine()
	{
		int a;
		int r = 10;
		try
		{
			Thread.sleep(2000); // 1초 = 1000밀리초

			for (int c = 0; c < COLS; c++)
			{
				if (r == 0)
				{
					r = 10;
				}
				boardCurr[c][r] = 1;
			}

			a = (int) (Math.random() * 7);
			boardCurr[a][r] = 0;
			r--;

			Log.v(TAG, "출력확인");

		} catch (InterruptedException ignore)
		{

		}

		// int blockType = (int) (Math.random() * 7);
	}

}
