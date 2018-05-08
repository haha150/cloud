package cloudvm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {
	private Socket socket;
	private ObjectOutputStream sout;
	private ObjectInputStream sin;
	private boolean running = true;

	public ClientHandler(Socket socket, Server server) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			sin = new ObjectInputStream(socket.getInputStream());
			sout = new ObjectOutputStream(socket.getOutputStream());
			while (running) {
				String s = (String)sin.readObject();
				System.out.println("Client says: " + s);
				if(s == null) {
					running = false;
				}
				sout.writeObject(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				cleanUp();
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}

	private void cleanUp() throws IOException {
		if (socket != null) {
			socket.close();
		}
		if (sin != null) {
			sin.close();
		}
		if (sout != null) {
			sout.close();
		}
	}
}
