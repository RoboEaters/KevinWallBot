package com.example.newrobot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.util.Log;


public class RosBridgeTCP extends Thread {

	String IP_add;
	int port_TCP;
	Socket the_socket;
	RoboEaterMain main_acti;
	private static final String TAG = "TCP_thread";
	String message_TCP;
	InetSocketAddress addr;
	BufferedWriter out;
	BufferedReader input;
	boolean CLOSED_SOCKET=false;
	boolean STOP = false;
	
	public RosBridgeTCP(RoboEaterMain the_activity, String IP, int port)
	{
		IP_add = IP.toString();
		port_TCP = port;
		main_acti = the_activity;
		the_socket = new Socket();
		message_TCP = new String();

		message_TCP = "raw\r\n\r\n";
    

		addr = new InetSocketAddress(IP_add,port_TCP);
		try 
		{				
			the_socket.connect(addr, 1000);				//timeout 100 ms
			the_socket.setSoTimeout(500);
			out = new BufferedWriter(new OutputStreamWriter(the_socket.getOutputStream()));
			input = new BufferedReader(new InputStreamReader(the_socket.getInputStream()));

			out.write(message_TCP);
			out.flush();
			Log.i("tcp","send msg " + message_TCP);
		}
		catch(java.io.IOException e) 
		{
			STOP = true;
			Log.e("tcp","error connect: ", e);
		}
	}
	
	public void run()
	{
		while(STOP != true)
		{			
			String inputString=null;
			try
			{					
				inputString = input.readLine();
			}
			catch (java.net.SocketTimeoutException e) {}
			catch (IOException e) 
			{
				STOP = true;
				Log.e("tcp","error read: ", e);
			}

			if(inputString != null)
			{	        	
				final String[]inputArray= inputString.split("/");
				
				//HERE WE CAN DO THINGS DEPENDING ON THE DATA PASSED BACK BY ROSBRIDGE
				if(inputArray[0].matches("CAMERA_ON") == true)
				{	        		
					main_acti.runOnUiThread(new Runnable() {
						@Override
						public void run() 
						{
							                      
						}
					}); 
				}
				else if(inputArray[0].matches("CAMERA_OFF") == true)
				{
					main_acti.runOnUiThread(new Runnable() {
						@Override
						public void run() 
						{
							                          
						}
					}); 
				}
								
			}

			try {Thread.sleep(10);	} 
			catch (InterruptedException e) {Log.e("tcp","sleep error ", e);	}
		}

		close_socket();
	}

	public synchronized void stop_thread()
	{
		STOP = true;
		Log.i("tcp","stopping...");
	}	

	private void close_socket()
	{
			try 
			{	
				if(out != null)out.close();
				if(input != null)input.close();
				the_socket.close();				//Close connection
			} 
			catch (IOException e) {
				Log.e("tcp","error close: ", e);
			}

			main_acti.runOnUiThread(new Runnable() {
				@Override
				public void run() 
				{
//					if(main_acti.button_connect.isChecked()) main_acti.button_connect.setChecked(false);   
//					main_acti.stop_all();
				}
			}); 
			Log.i("tcp","tcp client stopped ");
		}

}
