package com.example.lastclient;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;


public class ChatClientList extends ListActivity
{
	
	private ArrayList<String> list;
	private ArrayAdapter<String> adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_client_list);
		
		list=new ArrayList<String>();
		
		Intent intent=getIntent();
		list=intent.getExtras().getStringArrayList("chatClientList");
		adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,list);
		setListAdapter(adapter);
	}
}