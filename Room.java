import java.io.*;
import java.net.*;
import java.util.*;

public class Room {
	private String name = null; 

	boolean setName(String name){
		this.name = name;
		return true;
	}
	String getName(){
		return this.name;
	}

	boolean talk(BufferedReader in, PrintWriter out, String userName, Socket socket){
		try {
			out.print("Input message : ");
			out.flush();
			String line = in.readLine();
			while(!"quit".equals(line)){
				ChatServer.sendAll(userName + "@" + name + " : " + line);
				out.print("Input message : ");
				out.flush();
				line = in.readLine();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return true;
	}
}

class Rooms{
	private List<Room> rooms = new ArrayList<Room>();

	private BufferedReader in;
	private PrintWriter out;

	public Rooms(BufferedReader in, PrintWriter out){
		this.in = in;
		this.out = out;
	}

	public List<Room> getRooms(){
		return rooms;
	}

	public boolean createRoom(String name){
		Room room = new Room();

		room.setName(name);
		rooms.add(room);

		return true;
	}


}


