// Anton Stoytchev
// 05/10/2013

import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import java.math.BigInteger;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

/*
 *  This class controls the GUI as requested by the user
 */

public class ControlPanel extends JPanel {
	JButton GenPrime, Cancel;
	ClientGUI client;
	BufferedReader fromServer;
	PrintWriter    toServer;
	Socket sock;
	Thread t1;
	
	//Pattern for a valid IP address
	private static final String PATTERN = "^(\\d+\\.\\d+\\.\\d+\\.\\d+)$";
	
	public ControlPanel(ClientGUI c) throws IOException{
		this.client = c;
		this.setBorder((new TitledBorder(new EtchedBorder(), "Gen Big Integer" )));

		// Set-up GenPrime button
		this.GenPrime = new JButton("GenPrime");
		this.GenPrime.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String IP = client.connect.getText(); 
						String bits = client.input.getText(); 
						
						//Check for a valid IP and Bits before going forward
						if (!ControlPanel.this.CheckIPErrors(IP)){
							client.connect.setBackground(new Color(255,0,0));
							client.input.setText("Error: Invalid IP-Address");
							return;
						}
						//IP is valid set the background to green
						client.connect.setBackground(new Color(102, 255, 0));
						
						if (!ControlPanel.this.CheckInputErrors(bits)){
							client.input.setBackground(new Color(255,0,0));
							return;
						}
						//Input is valid set the background to green
						client.input.setBackground(new Color(102,255,0));
						
						//connect to server
						try{
							sock = new Socket(IP, 12345);
							//set-up to and from server writer/reader
							fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));
							toServer = new PrintWriter(sock.getOutputStream(), true);
							
							
							ControlPanel.this.Cancel.setEnabled(true); //Enable the Cancel Button
							
							//start a thread to get the prime number
							t1 = new Thread(new getInt(bits));
							t1.start();
						}catch (IOException j){
							client.output.setText("Error: Can't establish connection to server");
							client.connect.setBackground(new Color(255,0,0));
						}
					}
				});
		this.add(GenPrime);
		
		// Set-up Cancel button
		this.Cancel = new JButton("Cancel");
		this.Cancel.addActionListener(
				new ActionListener(){
					@SuppressWarnings("deprecation")
					public void actionPerformed(ActionEvent e){
						//Send a signal to the server to cancel its thread for generating the prime
						toServer.println("cancle");
						t1.stop(); //stop the thread waiting for a return from the server
						ControlPanel.this.GenPrime.setEnabled(true); //Enable GenPrime Button
						client.output.setText("Big Integer Generation Canceled");
						ControlPanel.this.Cancel.setEnabled(false); //Disable Cancel Button
					}
				});
		this.add(Cancel);
		ControlPanel.this.Cancel.setEnabled(false); //Start state of cancel (disabled)
	}
	
	//Function to check the IP
	protected boolean CheckIPErrors(String iP) {
		Pattern pattern = Pattern.compile(PATTERN);
	    Matcher matcher = pattern.matcher(iP);
	    return matcher.matches();           
	}

	//Function to check the input
	public boolean CheckInputErrors(String inputText){
		try{ 
			int bits = Integer.parseInt(inputText);
			if (bits > 1){
				return true;
			}else{
				client.output.setText("Error: Invalid length of bits requested: " + inputText +
						"\nNote: Number of bits must be greater than 1.");
				return false;
			}
		}catch (NumberFormatException e) {
			client.output.setText("Error: Invalid Input Given: " + inputText);
			return false;
		}
	}
	
	//Creates a class runnable to get the prime
	class getInt implements Runnable{
		String bits;
		
		public getInt(String b){
			this.bits = b;
		}
		
		public void run() {
			try {
				toServer.println(bits);
				ControlPanel.this.GenPrime.setEnabled(false);
				String response = fromServer.readLine();
				ControlPanel.this.GenPrime.setEnabled(true);
				ControlPanel.this.Cancel.setEnabled(false);
				toServer.println("Close Connection");
				client.output.setText(response);
			} catch (IOException e) {
				System.out.println("Can't send or recieve from server");
			}
		}
	}
}
