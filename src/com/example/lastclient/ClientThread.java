package com.example.lastclient;

import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class ClientThread extends Thread{
   MainActivity mainActivity;
   
   private BufferedReader br;
   private BufferedWriter bw;
   
   private String strAllMsg = "";
   private String strMsg;
   
   Message msg = new Message();
   
   ArrayList<String> chatClientList = new ArrayList<String>();
   
   public ClientThread(MainActivity tmpMain){
      mainActivity = tmpMain;
   }
   
   public void run(){
      try{
         mainActivity.socket = new Socket("61.84.218.166", 6657);
         
         mainActivity.br = new BufferedReader(new InputStreamReader(mainActivity.socket.getInputStream(), "UTF-8"));
         mainActivity.bw = new BufferedWriter(new OutputStreamWriter(mainActivity.socket.getOutputStream(), "UTF-8"));
         
         br = mainActivity.br;
         bw = mainActivity.bw;
         
         // 메시지 받기
         while((strMsg = br.readLine()) != null){
            String[] tmpMsg = strMsg.split("\\|");
            
            if(tmpMsg[0].equals("CONN")){ // Server 연결성공
               strAllMsg = strAllMsg + tmpMsg[1] + "\n";
               msg = chatView.obtainMessage(0, strAllMsg); // 메시지 얻어오기
               chatView.sendMessage(msg);
               
               conn.sendEmptyMessage(0);
            }else if(tmpMsg[0].equals("CLIENTIDNO")){ // 닉네임이 중복될 시
               isNicNo.sendEmptyMessage(0);
            }else if(tmpMsg[0].equals("CLIENTID")){ // 모든 Client - 접속한 Client ID 받기
               if(mainActivity.strId.equals(tmpMsg[1])){ // 들어온 사람이 현재 자신이라면
                  chatOk.sendEmptyMessage(0); // 메시지 입력 Edit true      
               }else{ // 들어온 사람이 ㅐ가 아니라면 새로 추가
                  chatClientList.add(tmpMsg[1]);
               }
            }else if(tmpMsg[0].equals("CLIENTIDLIST")){ // Client List 받기
               for(int i = 1; i < tmpMsg.length ; i++){
                  chatClientList.add(tmpMsg[i]);
               }
            }else if(tmpMsg[0].equals("CLIENTCLOSE")){ // Client 종료
               strAllMsg = strAllMsg + tmpMsg[1] + "\n";
               msg = chatView.obtainMessage(0, strAllMsg); // 메시지 얻어오기
               chatView.sendMessage(msg);
            }else if(tmpMsg[0].equals("CHATTING")){ // 메시지 받기
               strAllMsg = strAllMsg + tmpMsg[1] + "\n";
               msg = chatView.obtainMessage(0, strAllMsg); // 메시지 얻어오기
               chatView.sendMessage(msg); 
            }else if(tmpMsg[0].equals("EXIT")){ // Client 종료
               chatClientList.remove(tmpMsg[1]);
            }
         }
      }catch(UnknownHostException e){
         e.printStackTrace();
      }catch(IOException e){
         e.printStackTrace();
      }
   }
   Handler isNicNo = new Handler(){
      @Override
      public void handleMessage(Message msg){
         super.handleMessage(msg);
         
         Toast.makeText(mainActivity, "닉네임이 중복됩니다.", Toast.LENGTH_LONG).show();
         
         mainActivity.editNic.setText("");
         mainActivity.editNic.setEnabled(true);
         mainActivity.btnNicSend.setEnabled(true);
      }
   };
   
   Handler chatView = new Handler(){ // 왼쪽정렬
      @Override
      public void handleMessage(Message msg){
         super.handleMessage(msg);
         mainActivity.tvChat.setText(msg.obj + "");
         mainActivity.scrollView.post(new Runnable(){
            @Override
            public void run(){
               mainActivity.scrollView.scrollTo(0, mainActivity.tvChat.getHeight());
            }
         });
      }
   };
   
   Handler conn = new Handler(){
      @Override
      public void handleMessage(Message msg){
         super.handleMessage(msg);
         mainActivity.editNic.setEnabled(true);
         mainActivity.btnNicSend.setEnabled(true);
      }
   };
   
   Handler chatOk = new Handler(){
      @Override
      public void handleMessage(Message msg){
         super.handleMessage(msg);
         mainActivity.editMsg.setEnabled(true);
         mainActivity.btnMsgSend.setEnabled(true);
      }
   };
}