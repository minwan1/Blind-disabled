package com.example.lastclient;

import com.example.lastclient.*;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class BoardView extends View {
	Toast toast;

	static int squareLength; // square 의 크기 (in pixel)

	static int COLS = Tetris4Android.COLS; // columns
	static int ROWS = Tetris4Android.ROWS; // rows
	public static int colors[];

	Rect rectClient = new Rect(); // 사용 가능한 화면 영역
	Rect rectBoard = new Rect(); // 순수하게 테트리스 판 영역

	// 판의 내용
	byte boardPrev[][]; // 직전 판의 내용
	byte boardCurr[][]; // 현재 (변경 후) 판의 내용
	byte boardVibrator[][];// 진동을 확인 판내용
	Square pieceCurr[] = new Square[4]; // 현재의 블럭

	private byte mode = READY; // 현재 게임의 상태

	public static final byte PAUSE = 0; // 일시 중지
	public static final byte READY = 1; // 게임 시작 전 준비 완료
	public static final byte RUNNING = 2; // 게임 진행 중
	public static final byte END = 3; // 게임 종료됨
	public static byte stage = 1;
	public static int count = 300;
	private static final String TAG = null;

	private static final String MyTAG = null;
	public static int mission[] = new int[5]; // 미션 저장 배열
	public int line = 0; // 지운 줄 수

	private static Context mContext;

	public BoardView(Context context) {
		super(context);
		this.mContext = context;

		Log.v(TAG, "생성자 확인");

		init();
	}

	public BoardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public BoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	// 빈칸 찾음//
	public void vie(Context context) {
		this.mContext = context;
		Vibrator vibrator = (Vibrator) mContext
				.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(300);
	}

	// 빈칸 찾음//

	/**
	 * 내부에서 사용되는 각종 내용에 대한 초기 설정
	 */
	void init() {

		// colors = new int[8]; // 내부에서 사용되는 색깔들을 지정한다.

		// colors[0] = 0xff000060; // 배경색
		colors[0] = 0xff000000; // 검은색

		// 블럭에서 사용되는 색깔
		colors[1] = 0xffff0000; // red
		// colors[2] = 0xff00c800; // green
		colors[3] = 0xff00c8ff; // light blue
		colors[4] = 0xffffff00; // yellow
		colors[5] = 0xffff9600; // orange
		colors[6] = 0xffd200f0; // purple
		colors[7] = 0xff2800f0; // dark bluer

		// 판을 저장하는 공간을 할당한다.
		boardPrev = new byte[Tetris4Android.COLS][Tetris4Android.ROWS + 4];
		boardCurr = new byte[Tetris4Android.COLS][Tetris4Android.ROWS + 4];
		boardVibrator = new byte[Tetris4Android.COLS][Tetris4Android.ROWS + 4];

		// fillBoard();
		showAllBlocks();
		// cleanBoard();
	}

	/* 영역에 표시가 잘 되는지 확인하기 위한 테스트용 */
	private void fillBoard() {
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				boardCurr[c][r] = (byte) (((r + c) % (colors.length - 1)) + 1);
			}
		}
	}

	public byte getMode() {
		return mode;
	}

	public void setMode(byte mode) {
		this.mode = mode;
	}

	/* 모든 블럭을 생성하여 화면에 출력해 준다 */
	private void showAllBlocks() {
		for (int i = 0; i <= 6; i++) {
			newBlock(i);
			moveCurrentPiece(0, (6 - i) * 3, false);
		}
	}

	/* 판의 모든 내용을 지우고 현재의 블럭도 초기화한다. 게임 시작 전 준비 단계이다. */
	void cleanBoard() {
		for (int c = 0; c < COLS; c++) {
			for (int r = 0; r < ROWS; r++) {
				boardPrev[c][r] = -1;
				boardCurr[c][r] = 0;
			}
		}
		//
		createLine(4);
		// newCreateLine();
		//
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		// 주어진 화면 영역을 바탕으로 실제 사용될 영역을 계산한다.
		int width = w / COLS;
		int height = h / ROWS;

		squareLength = Math.min(width, height);

		rectClient = new Rect(0, 0, w, h);

		int xoffset = (w - squareLength * COLS) / 2;
		int yoffset = (h - squareLength * ROWS) / 2;

		rectBoard = new Rect(xoffset, yoffset, xoffset + squareLength * COLS,
				yoffset + squareLength * ROWS);

		Log.v(Tetris4Android.TAG, " SQUARE LEN = " + squareLength);
		Log.v(Tetris4Android.TAG, "CLIENT RECT (" + rectClient.left + ","
				+ rectClient.top + ")-(" + rectClient.right + ","
				+ rectClient.bottom + ")");
		Log.v(Tetris4Android.TAG, " BOARD RECT (" + rectBoard.left + ","
				+ rectBoard.top + ")-(" + rectBoard.right + ","
				+ rectBoard.bottom + ")");
	}

	/*
	 * 변경 전 위치에서 변경 후 위치로 옮긴다. 이 때, 옮겨질 수 있는지 여부도 확인한다.
	 */
	private boolean moveBlocks(Square from[], Square to[]) {

		outerlabel:

		for (int i = 0; i < to.length; i++) {

			if (!to[i].isInBounds()) {
				return false;
			}

			if (boardCurr[to[i].getX()][to[i].getY()] != 0) {
				for (int j = 0; j < from.length; j++) {
					if (to[i].isEqual(from[j])) {
						continue outerlabel;
					}
				}
				return false;
			}

			// if(pieceCurr[1].getX()==0 && event.getAction()){
			// //KeyEvent.ACTION_DOWN==KeyEvent.KEYCODE_DPAD_LEFT) {
			// Tetris4Android.P_wall.start();

			// }
		}

		// blank old piece
		for (int i = 0; i < from.length; i++) {
			if (from[i].isInBounds()) {
				boardCurr[from[i].getX()][from[i].getY()] = 0;
				boardPrev[from[i].getX()][from[i].getY()] = -1;
			}
		}

		for (int i = 0; i < to.length; i++) {
			boardCurr[to[i].getX()][to[i].getY()] = to[i].getColor();
		}

		return true;

	}

	/*
	 * 새로운 블럭을 생성한다. 만약, 새로운 블럭을 생성할 수 없다면 (공간이 꽉 찬 경우) false 를 반환한다.
	 */
	protected boolean newBlock() {
		line = 0;
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				if (boardCurr[j][i] > 0)
					line++;

			}
		}
		if (line < 2) {
			Tetris4Android.P_stageclear.start();
			Log.d(MyTAG, "minwan");
			toast.makeText(mContext, "랭킹추가하세요!", toast.LENGTH_LONG).show();
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			int three; // 100의 자리
			int two; // 10의 자리
			
			if(count >= 100){
				three = count / 100;
				two = (count % 100) / 10;
			}else{
				three = 0;
				two = count / 10;
			}
			// 점수 사운드
			if(three >= 1){
				if(three == 2) Tetris4Android.p_s_two.start();
				if(three == 3) Tetris4Android.p_s_three.start();
				if(three == 4) Tetris4Android.p_s_four.start();
				if(three == 5) Tetris4Android.p_s_five.start();
				
				if(three != 1){
					try
					{
						Thread.sleep(300);
					} catch (InterruptedException e){
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Tetris4Android.p_s_hundred.start();
				try
				{
					Thread.sleep(300);
				} catch (InterruptedException e){
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(two == 2) Tetris4Android.p_s_two.start();
				if(two == 3) Tetris4Android.p_s_three.start();
				if(two == 4) Tetris4Android.p_s_four.start();
				if(two == 5) Tetris4Android.p_s_five.start();
				if(two == 6) Tetris4Android.p_s_six.start();
				if(two == 7) Tetris4Android.p_s_seven.start();
				if(two == 8) Tetris4Android.p_s_eight.start();
				if(two == 9) Tetris4Android.p_s_nine.start();
				if(two > 1){
					try
					{
						Thread.sleep(300);
					} catch (InterruptedException e){
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(two != 0) Tetris4Android.p_s_ten.start();
				try
				{
					Thread.sleep(300);
				} catch (InterruptedException e){
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Tetris4Android.p_score_sound.start();
			}
			if(three == 0){
				if(two == 2) Tetris4Android.p_s_two.start();
				if(two == 3) Tetris4Android.p_s_three.start();
				if(two == 4) Tetris4Android.p_s_four.start();
				if(two == 5) Tetris4Android.p_s_five.start();
				if(two == 6) Tetris4Android.p_s_six.start();
				if(two == 7) Tetris4Android.p_s_seven.start();
				if(two == 8) Tetris4Android.p_s_eight.start();
				if(two == 9) Tetris4Android.p_s_nine.start();
				if(two > 1){
					try
					{
						Thread.sleep(300);
					} catch (InterruptedException e){
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(two != 0) Tetris4Android.p_s_ten.start();
				try
				{
					Thread.sleep(300);
				} catch (InterruptedException e){
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Tetris4Android.p_score_sound.start();
			}
			
		}
		try
		{
			Thread.sleep(400);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(line > 2) Tetris4Android.P_one.start();
		// 새로운 블럭이므로, 기존의 위치는 판 밖의 영역으로 지정한다.
		Square old[] = new Square[4];
		old[0] = old[1] = old[2] = old[3] = new Square(-1, -1, (byte) 0);

		// 임의의 모양을 가져오도록 한다.
		int blockType = (int) (Math.random() * 7);

		// 블럭을 생성한다.
		newBlock(blockType);

		// 생성한 블럭을 이동한다.
		// 만약, 이동에 실패한다면(화면 중앙 상단에 새로운 블럭이 생성되는 위치에
		// 이미 다른 블럭이 존재하는 경우) false 를 반환한다.
		return moveBlocks(old, pieceCurr);

	}

	/*
	 * 화면 중앙 상단에 지정된 형태의 새로운 블럭을 생성한다. 생성된 블럭은 현재의 블럭으로 지정된다.
	 */
	private void newBlock(int type) {
		int a = 4;

		int m = COLS / 2;

		errorClear();

		// if(line == 5){ // 게임 끝났을 시
		// Tetris4Android.P_stageclear.start();
		// try
		// {
		// Thread.sleep(1000);
		// } catch (InterruptedException e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// line = 0;
		// }

		switch (a) {
		case 0:
			// ####
			pieceCurr[0] = new Square(m - 1, 0, (byte) 1);
			pieceCurr[1] = new Square(m - 2, 0, (byte) 1);
			pieceCurr[2] = new Square(m, 0, (byte) 1);
			pieceCurr[3] = new Square(m + 1, 0, (byte) 1);
			break;

		case 1:
			// ###
			// #
			pieceCurr[0] = new Square(m, 0, (byte) 5);
			pieceCurr[1] = new Square(m, 1, (byte) 5);
			pieceCurr[2] = new Square(m - 1, 0, (byte) 5);
			pieceCurr[3] = new Square(m + 1, 0, (byte) 5);
			break;

		case 2:
			// ##
			// ##
			pieceCurr[0] = new Square(m, 0, (byte) 2);
			pieceCurr[1] = new Square(m - 1, 1, (byte) 2);
			pieceCurr[2] = new Square(m, 1, (byte) 2);
			pieceCurr[3] = new Square(m + 1, 0, (byte) 2);
			break;

		case 3:
			// ##
			// // ##
			pieceCurr[0] = new Square(m, 0, (byte) 7);
			pieceCurr[1] = new Square(m + 1, 1, (byte) 7);
			pieceCurr[2] = new Square(m, 1, (byte) 7);
			pieceCurr[3] = new Square(m - 1, 0, (byte) 7);
			break;

		case 4:
			// ##
			// ##
			pieceCurr[0] = new Square(0, 0, (byte) 7);
			pieceCurr[1] = new Square(0, 0, (byte) 7);
			pieceCurr[2] = new Square(0, 0, (byte) 7);
			pieceCurr[3] = new Square(0, 0, (byte) 7); // (0,0 블럭에 대한 시작 좌표)
			break;

		case 5:
			// #
			// ###
			pieceCurr[0] = new Square(m, 1, (byte) 6);
			pieceCurr[1] = new Square(m - 1, 1, (byte) 6);
			pieceCurr[2] = new Square(m + 1, 1, (byte) 6);
			pieceCurr[3] = new Square(m + 1, 0, (byte) 6);
			break;

		case 6:
			// #
			// ###
			pieceCurr[0] = new Square(m, 1, (byte) 4);
			pieceCurr[1] = new Square(m + 1, 1, (byte) 4);
			pieceCurr[2] = new Square(m - 1, 1, (byte) 4);
			pieceCurr[3] = new Square(m - 1, 0, (byte) 4);
			break;
		}
	}

	/**
	 * 현재의 블럭을 이동한다.
	 * 
	 * @param byx
	 *            수평 이동 변위
	 * @param byy
	 *            수직 이동 변위
	 * @param rotate
	 *            회전 여부
	 * 
	 * @return 이동 가능한 경우 true, 그렇지 못할 경우 false
	 */
	synchronized boolean moveCurrentPiece(int byx, int byy, boolean rotate) {

		Square newpos[] = new Square[4];

		for (int i = 0; i < 4; i++) {
			if (rotate) {
				int dx = pieceCurr[i].getX() - pieceCurr[0].getX();
				int dy = pieceCurr[i].getY() - pieceCurr[0].getY();

				newpos[i] = new Square(pieceCurr[0].getX() + dy,
						pieceCurr[0].getY() - dx, pieceCurr[i].getColor());

			} else {
				newpos[i] = new Square(pieceCurr[i].getX() + byx,
						pieceCurr[i].getY() + byy, pieceCurr[i].getColor());
			}
		}

		if (!moveBlocks(pieceCurr, newpos))
			return false;

		pieceCurr = newpos;

		return true;
	}

	/**
	 * 화면에 그리는 부분이다.
	 */

	// 판 그린다.
	protected void createLine(int stage) // 랜덤으로 미션
	{
		int a;
		boolean check = false;

		for (int i = 1; i < 5; i++)
			mission[i] = -1;

		if (stage == 4) {
			for (int r = 2; r < ROWS; r++) {
				while (true) {
					a = (int) (Math.random() * 7);
					for (int i = 0; i < 5; i++) {
						check = false;
						if (a == mission[i]) {
							check = true;
							break;
						}
					}
					if (check == false)
						break;
				}
				mission[r - 2] = a;
				for (int c = 0; c < COLS; c++) {
					if (c != mission[r - 2])
						boardCurr[c][r] = 2;
					else
						boardCurr[c][r] = 0;
				}

				// boardCurr[a][r] = 0;

				// int blockType = (int) (Math.random() * 7);
			}
		}
		for (int i = 0; i < 5; i++) { // 미션 사운드 출력
			if ((mission[i] + 1) == 1)
				Tetris4Android.P_one.start();
			if ((mission[i] + 1) == 2)
				Tetris4Android.P_two.start();
			if ((mission[i] + 1) == 3)
				Tetris4Android.P_three.start();
			if ((mission[i] + 1) == 4)
				Tetris4Android.P_four.start();
			if ((mission[i] + 1) == 5)
				Tetris4Android.P_five.start();
			if ((mission[i] + 1) == 6)
				Tetris4Android.P_six.start();
			if ((mission[i] + 1) == 7)
				Tetris4Android.P_seven.start();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	// for (int i = 0; i < boardCurr.length; i++)
	// {
	// for (int j = 0; j < boardCurr[i].length; j++)
	// {
	// // int b = (int) (Math.random() * 7);
	// boardCurr[i][j] = 1;
	// }
	// }
	// a = (int) (Math.random() * 7);

	// boardCurr[0][0] = 0;
	// boardCurr[5][2] = 0;
	// boardCurr[6][3] = 0;
	// boardCurr[5][4] = 0;
	// boardCurr[2][5] = 0;
	// boardCurr[1][6] = 0;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Log.v(Tetris4Android.TAG, "DRAWING BOARD");

		Paint p = new Paint();

		p.setColor(0xe0ffffff);
		canvas.drawRect(rectClient, p);

		p.setColor(colors[0] & 0xe0ffffff);
		canvas.drawRect(rectBoard, p);

		for (int c = 0; c < COLS; c++) {
			for (int r = 0; r < ROWS; r++) {
				{
					p.setColor(colors[boardCurr[c][r]]);

					// Log.v(Tetris4Android.TAG4LOG,
					// "("+rectSquare.left+","+rectSquare.top+")-("+rectSquare.right+","+rectSquare.bottom+") COLOR = "
					// + colors[boardCurr[c][r]]);

					// RoundRect
					{
						RectF rectSquare = new RectF(rectBoard.left
								+ squareLength * c, rectBoard.top
								+ squareLength * r, rectBoard.left
								+ squareLength * (c + 1) - 1, rectBoard.top
								+ squareLength * (r + 1) - 1);
						canvas.drawRoundRect(rectSquare, 4F, 4F, p);
					}
					// // BasicRect
					// {
					// Rect rectSquare = new Rect(
					// rectBoard.left + squareLength * c, rectBoard.top +
					// squareLength * r
					// , rectBoard.left + squareLength * (c + 1) - 1,
					// rectBoard.top + squareLength * (r + 1) - 1
					// );
					// canvas.drawRect(rectSquare, p);
					// }
					boardPrev[c][r] = boardCurr[c][r];
				}
			}
		}
		SoundSearch();
	}

	/* 채워진 라인이 있다면 지운다. */
	public boolean removeCompleteLines() {

		for (int r = ROWS - 1; r >= 0; r--) {
			int c;
			for (c = 0; c < COLS; c++) {
				if (boardCurr[c][r] <= 0) // 빈 공간이 있다면, 더 살펴볼 필요가 없다.
					break;
			}

			// 채워졌다면...
			if (c == COLS) {
				line = line + 1;

				Tetris4Android.P_blockremove.start();
				for (int k = r; k > 0; k--) {
					for (int l = 0; l < COLS; l++) {
						boardCurr[l][k] = boardCurr[l][k - 1];

					}
				}

				// 현재 한 줄이 올라갔으므로, 다시 현재의 줄을 검사한다.
				r++;
				return true;
			}
			// else
			// {
			// Tetris4Android.P_wall.start();
			// }
		}
		return false;
	}

	/* 채워진 공간이 있다면 표시한다. */
	public void check() {
		for (int c = 0; c < COLS; c++) {
			for (int r = 0; r < ROWS; r++) {
				if (boardCurr[c][r] != 0) {
					Log.v(TAG, "공간이 있으면 출력 확인" + boardCurr[c][r]);
					boardVibrator[c][r] = 1;
				}
				// Log.v(TAG, "공간이 있으면 출력 확인" + count);
			}
		}
	}

	void SoundSearch() {
		int c, r, col = 0;
		for (r = ROWS - 1; r >= 0; r--) {

			for (c = 0; c < COLS; c++) {
				if (boardCurr[c][r] == 2) // 채워져있으면 검색할필요가 없다.
					break;
			}
			if (c == COLS) { // 모두가 다 공백이면, 그전행이 최고높이
				break;
			}
		}

		for (c = 0; c < COLS; c++) {
			if (boardCurr[c][r + 1] == 0 && c == pieceCurr[1].getX()) {
				// Tetris4Android.P_soundsearch.start();
				//
				String str = "여기";
				Log.v(TAG, str + "빈칸 출력");
				// vie(mContext);// 빈칸 진동
				break;
			}
		}
		// Log.v(Tetris4Android.TAG, "height:"+
		// Integer.toString(r+1)+",col:"+Integer.toString(col));

	}

	// public void vie2()
	// {
	// Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	// //long[] pattern = {1000, 200, 1000, 2000, 1200}; // 진동, 무진동, 진동 무진동 숫으로
	// 시간을 설정한다.
	// //vibe.vibrate(pattern, 0); // 패턴을 지정하고 반복횟수를 지정
	// vibe.vibrate(1000);
	// }

	/* 현재의 상태를 저장할 수 있도록 한다. 이것은 restoreState() 와 쌍을 이루어야 한다. */
	public Bundle saveState() {

		Log.v(Tetris4Android.TAG, "saveState()");

		Bundle map = new Bundle();

		byte board[] = new byte[COLS * ROWS];
		for (int c = 0; c < COLS; c++) {
			for (int r = 0; r < ROWS; r++) {
				board[r * COLS + c] = boardCurr[c][r];
			}
		}

		map.putByte("mode", mode);
		map.putByteArray("boardCurr", board);

		return map;
	}

	/* 현재의 상태를 복원한다. 이것은 saveState() 와 쌍을 이루어야 한다. */
	public void restoreState(Bundle map) {

		Log.v(Tetris4Android.TAG, "restoreState()");

		setMode(PAUSE);

		byte board[];
		board = map.getByteArray("boardCurr");

		for (int c = 0; c < COLS; c++) {
			for (int r = 0; r < ROWS; r++) {
				boardCurr[c][r] = board[r * COLS + c];
			}
		}

		setMode(map.getByte("mode"));
	}

	public void errorClear() {
		int sum;
		// boardCurr[c][r] = 0;
		for (int i = 0; i < ROWS; i++) {
			sum = 0;
			for (int j = 0; j < COLS; j++) {
				if (boardCurr[j][i] == 0)
					sum++;
			}
			if (sum > 1) {
				for (int j = 0; j < COLS; j++)
					boardCurr[j][i] = 0;
			}
		}
	}
}
