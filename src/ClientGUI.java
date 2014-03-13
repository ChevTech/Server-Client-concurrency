// Anton Stoytchev
// 05/10/2013

import java.awt.*;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/*
 * This Class creates and formats the Client User Interface
 */

public class ClientGUI extends JPanel {
	
	JTextArea input, output, connect;
	
	public ClientGUI() throws IOException {
		//create the window
		this.setPreferredSize(new Dimension(500, 600));
		this.add(new ControlPanel(this));
		
		//create and format a connection JTextArea
		connect = new JTextArea("Enter an IP Address", 1, 10);
		Font f = new Font("Monospace", Font.PLAIN, 18);
		connect.setFont(f);
		connect.setEditable(true);
		connect.setBackground(new Color(220, 230, 220));
		connect.setBorder(new TitledBorder(new EtchedBorder(), "IP-Address"));
		this.add(connect);
		
		//create and format an input JTextArea
		input = new JTextArea("Enter a bit-length for the Big Integer", 5, 20);
		input.setFont(f);
		input.setEditable(true);
		input.setLineWrap(true);
		input.setBackground(new Color(220, 230, 220));
		input.setBorder(new TitledBorder(new EtchedBorder(), "Bit-Length"));
		this.add(input);
		
		//create and format an output JTextArea
		output = new JTextArea(5, 20);
		output.setFont(f);
		output.setEditable(false);
		output.setLineWrap(true);
		output.setBackground(new Color(220, 230, 220));
		output.setBorder(new TitledBorder(new EtchedBorder(), "Generated Prime Number"));
		this.add(output);
	}
}
