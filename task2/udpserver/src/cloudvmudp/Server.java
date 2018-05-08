package cloudvmudp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server {
	
	private static final int SERVER_PORT = 12345;
	private static final int BUFFER_LENGTH = 1024;
	
	public Server() {

	}

	public void start() {
		DatagramSocket socket = null;
		byte[] bufferReceive = new byte[BUFFER_LENGTH];

		try {
			socket = new DatagramSocket(SERVER_PORT);

			while (true) {
				DatagramPacket receivePacket = new DatagramPacket(bufferReceive, bufferReceive.length);
				System.out.println("Server waiting...");
				socket.receive(receivePacket);

				String message = unpack(receivePacket);
				receivePacket.getAddress();
				receivePacket.getPort();

				System.out.println(message);

				sendPacket(socket, message.getBytes(), receivePacket.getAddress(), receivePacket.getPort());
			}
		} catch (SocketException e) {
			System.out.println("Port is taken.");
			System.exit(-1);
		} catch (IOException e) {
			System.out.println("ERROR OMG FIX PLZ.");
		} finally {
			if (socket != null) {
				socket.close();
				System.out.println("Terminated server.");
			}
		}
	}

	private void sendPacket(DatagramSocket socket, byte[] bufferSend, InetAddress clientAddress, int port)
			throws IOException {
		DatagramPacket sendPacket = new DatagramPacket(bufferSend, bufferSend.length, clientAddress, port);
		socket.send(sendPacket);
	}

	private String unpack(DatagramPacket packet) {
		return new String(packet.getData(), 0, packet.getLength());
	}

}
