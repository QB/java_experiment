import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer{
	static ServerSocket serversocket;
	static List<Socket> connections;
	static final int PORT = 8080;

	public static void sendAll(String message){
		if(connections!=null){
			for(Socket socket : connections){
				try{
					PrintWriter pw = new PrintWriter(socket.getOutputStream());
					//System.out.println(connections.size());
					pw.println(message);
					pw.flush();
					System.out.println(socket.toString()+"is opened.");
				}catch(IOException error){
					System.out.println(socket.toString()+"is closed. : "+socket.isClosed());
					error.printStackTrace();
				}
			}
		}
		System.out.println(message);
	}

	public static void addConnections(Socket socket){
		if(connections == null){
			connections = new LinkedList<Socket>();
		}
		connections.add(socket);
	}

	public static void deleteConnection(Socket socket){
		if(connections != null){
			connections.remove(socket);
		}
	}

	public static void main(String[] args) {
		try{
			serversocket = new ServerSocket(PORT);
		}catch(IOException error){
			error.printStackTrace();
			System.exit(1);
		}

		while(true){
			try{
				System.out.println(serversocket);
				Socket socket = serversocket.accept();
				System.out.println("test2");
				System.out.println("-----------新しい接続を受け付けました---------------");
				addConnections(socket);
				(new Thread(new Client(socket))).start();
			}catch(IOException error){
				System.err.println(error);
				error.printStackTrace();
			}
		}
	}
}

class Client implements Runnable{
	Socket socket;
	BufferedReader in;
	PrintWriter out;
	String name = null, passwd = null;
	ChatServer server = null;
	int num;
	boolean flag = true;
	String userName = "";

	public Client(Socket socket) throws IOException{ 
		System.out.println("新しいClientからの接続 : " + this.name);//for debug
		this.socket = socket;
		in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream());
	}

	public void run(){
		System.out.println("新しいスレッドが走り始めました : "+this.name);//for debug
		Rooms rooms = new Rooms(in, out);
		Users users = new Users(in, out);
		users.readUserData();

		// for debug
		rooms.createRoom("ROOM1");
		rooms.createRoom("ROOM2");
		rooms.createRoom("ROOM3");

		try{
			while (flag) {
				// メニュー画面
				out.println("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
				out.println("1: アカウントを新規作成する");
				out.println("2: 既存のアカウントでログインする");
				out.println("3: アカウントを削除する");
				out.println("4: 終了する");
				out.print(">> ");
				out.flush();
				int selection = Integer.parseInt(in.readLine());

				switch(selection) {
				case 1:
					users.createUser();
					break;
				case 2:
					while(userName=="") userName = users.authenticate();

					out.println("現在、チャット場には、次の部屋が作られています。");
					out.println("部屋での会話を終了したくなったときは、quitと打ってください。");

					List<Room> roomList = rooms.getRooms();
					int n = roomList.size(); // 現在の部屋の数

					// 部屋の一覧を出力
					for (int i=0; i<n; i++)
						out.println(i + ": " + roomList.get(i).getName());
					out.println(n + ": [新規作成]");

					// どの部屋に入るか選択させる
					selection = -1;
					while(true) {
						out.print(">> ");
						out.flush();
						selection = Integer.parseInt(in.readLine());
						if(selection <= n && selection >= 0) break;
						else out.println("正しい部屋番号を入力してください。");
					}
					roomList.get(selection).talk(in, out, userName, socket);

					out.print("Roomクラスへ飛ぶ");

					break;
				case 3:
					users.deleteUser();
					break;
				case 4:
					flag = false;
					out.println("終了します。");
					out.flush();
					break;
				default:
					out.println("選択肢の数字を入力してください。");
					out.flush();
				}

			} // end while
			ChatServer.deleteConnection(socket);
			socket.close();

		}catch(IOException error){
			try{
				socket.close();
			}catch(IOException error2){
				error2.printStackTrace();
			}
		}
	}
}


