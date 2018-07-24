package client;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.net.*;
import java.io.*;

public class Server extends JFrame 
{
	private JTextField userText;
	private JTextArea chatWindow;
	int number;
	int temp;
	ServerSocket ss1;
	Socket socket1;
	ObjectInputStream input;
	ObjectOutputStream output; 
	
	public Server()
	{
		super("Matt's IM server");
		
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener
		(
			new ActionListener() 
			{
				public void actionPerformed(ActionEvent event)
				{
					sendMessage(event.getActionCommand());
					userText.setText("");
				}
			}		
		);
		
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(300, 150);
		setVisible(true);
	}
	
	public void startRunning()
	{
		try
		{
			ss1 = new ServerSocket(6789, 100);
			
			while (true)
			{
			    try
			    {
					waitForConnection();
					setUpStreams();
					whileChatting();
			    } 
			    catch (EOFException eofException)
			     {
			    	showMessage("\n Server ended the connection! ");
			     }
			    finally
			    {
			    	closeCrap();
			    }
			}
		} catch (IOException ioException) 
		  {
			ioException.printStackTrace();
		  }
	}
	
	public void waitForConnection() throws IOException
	{
		showMessage("Waiting for someone to connect... ");
		socket1 = ss1.accept();
		showMessage("Now connected to: " + socket1.getInetAddress().getHostName());
	}
	
	private void setUpStreams() throws IOException
	{
		output = new ObjectOutputStream(socket1.getOutputStream());
		output.flush();
		input = new ObjectInputStream(socket1.getInputStream());
		showMessage("\n Your stream is now set up!!");
	}
	
	public void whileChatting() throws IOException
	{
		String message = "You are now connected!";
		sendMessage(message);
		ableToType(true);
		
		do
		{
			try
			{
				message = (String) input.readObject();
				showMessage("\n" + message);
			} 
			catch (ClassNotFoundException classNotFoundException) 
            {
				showMessage("idk what the user sent.");
            }
			
		} while (!message.equals("CLIENT - END"));
	}
	
	public void closeCrap()
	{
		showMessage("\n Closing connections... \n");
		ableToType(false);
		try
		{
			output.close();
			input.close();
			socket1.close();
		}
		catch (IOException iOException)
		{
			iOException.printStackTrace();
		}
	}
	
	public void sendMessage(String message)
	{
		try
		{
			output.writeObject("SERVER - " + message);
			output.flush();
			showMessage("\nServer: " + message);
		}
		catch (IOException iOException)
		{
			chatWindow.append("\n" + "something went wrong.");
		}
	}
	
	public void showMessage(final String text)
	{
		SwingUtilities.invokeLater
		(
				new Runnable()
				{
					public void run()
					{
						chatWindow.append(text);
					}
				}
		);
	}
	
	private void ableToType(final boolean tof)
	{
		userText.setEditable(tof);
	}
	
}
