package com.example.lastclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lastclient.R.id;

public class MainActivity extends Activity
{

	// layout
	/*EditText editNic;
	Button btnNicSend;
	View viewClientList, viewChatRoom;
	Button btnClientList, btnChatRoom;
	ListView IvClientList, IvChatRoomList;*/
	TextView tvChat;
	EditText editMsg,editNic;
	Button btnMsgSend,btnNicSend;
	ScrollView scrollView;
	

	// socket
	Socket socket;
	BufferedReader br;
	BufferedWriter bw;
	ClientThread clientThread;
	
	
	String strId="";
	ArrayList<String> clientList = new ArrayList<String>();
	ArrayList<String> adapter;
	
	//옵션메뉴
	private static final int MENU_ONE=0;
	private	static final int MENU_TWO=1;
	
	

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tvChat=(TextView)findViewById(R.id.chat);
		editMsg=(EditText)findViewById(R.id.msg);
		editNic=(EditText)findViewById(R.id.nicname);
		btnMsgSend=(Button)findViewById(R.id.msgsend);
		btnNicSend=(Button)findViewById(R.id.nicsend);
		scrollView=(ScrollView)findViewById(R.id.scrollview);
		
		editMsg.setEnabled(true);
		editNic.setEnabled(true);
		btnMsgSend.setEnabled(true);
		btnNicSend.setEnabled(true);
		
		// socket
		try
		{
			
			clientThread=new ClientThread(this);
			clientThread.start();
			
			// br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

		} catch (Exception e)
		{
			tvChat.setText(e.getMessage());
			scrollView.scrollTo(0, tvChat.getHeight());
			scrollView.post(new Runnable()
			{
				
				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					scrollView.scrollTo(0, tvChat.getHeight());
					
				}
			});
		}
		// NicName 전송
		btnNicSend.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v){
				if(editNic.getText().toString().equals("")){
					Toast.makeText(MainActivity.this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
				}else{
					sendNicMsg(editNic.getText().toString());
			}
				
			
			}
		});
		
		//msg 전송
		btnMsgSend.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				/*sendMsg(editMsg.getText().toString());*///민완
				sendMsg(String.valueOf(BoardView.count));
				
				//입력 후 키보드 숨기기
		//		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		//		imm.hideSoftInputFromWindow(editMsg.getWindowToken(), 0);
			}
		});
		
		//엔터키막기
		editMsg.setOnKeyListener(new OnKeyListener()
		{
			
			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2)
			{
				// TODO Auto-generated method stub
				if(arg1==arg2.KEYCODE_ENTER){
					return true;
				}
				return false;
			}
		});
		
		
		editNic.setOnKeyListener(new OnKeyListener()
		{
			
			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2)
			{
				// TODO Auto-generated method stub
				if(arg1==arg2.KEYCODE_ENTER){
					return true;
				}
				
				return false;
			}
		});
	}

	// 닉네임 전송
	private void sendNicMsg(String tmpMsg)
	{
		if (bw != null)
		{
			// Toast.makeText(this, "sendMessage ok", Toast.LENGTH_SHORT).show();
			strId=tmpMsg;
			
			try
			{
				bw.write(tmpMsg + "\n");
				bw.flush();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			//입력 후 키보드 숨기기
			InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(editNic.getWindowToken(),0);
			
			//닉네임 수정 불가
			editNic.setText(tmpMsg);
			editNic.setEnabled(false);
			btnNicSend.setEnabled(false);
			
			//메세지 사용 가능
			//editMsg.setEnabled(true);
			//btnMsgSend.setEnabled(true);
		} else{
			Toast.makeText(this, "sendNicMsg error", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	
	//메시지전송
	public void sendMsg(String tmpMsg){
		if(bw != null){
			//Toast.makeText(this, "sendMsg ok", Toast.LENGTH_SHORT).show();
			try{
				bw.write("CHATTING|" + tmpMsg + "\n");
				bw.flush();
			}catch(IOException e){
				e.printStackTrace();
			}
			
			editMsg.setText("");
		}else{
			Toast.makeText(this, "sendMsg error", Toast.LENGTH_SHORT).show();
		}
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		
		try{
			bw.write("EXIT|" + strId + "\n");
			bw.flush();
			
			br.close();
			bw.close();
			socket.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(0,MENU_ONE,menu.NONE,"참여중인 대화상대");
		menu.add(0,MENU_TWO,menu.NONE,"테트리스넘어가기");
		return true;	
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case MENU_ONE:
			Intent intent=new Intent(MainActivity.this,Tetris4Android.class);
			intent.putExtra("chatClientList",clientThread.chatClientList);
			startActivity(intent);
			break;
		case MENU_TWO:
			Intent gotent=new Intent(this,StartActivity.class);
			startActivity(gotent);
		}
		return false;
		
	}
	
}