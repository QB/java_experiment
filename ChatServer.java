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
	boolean flag = false;

	public Client(Socket socket) throws IOException{ 
		System.out.println("新しいClientからの接続 : " + this.name);//for debug
		this.socket = socket;
		in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream());
	}

	public void run(){
		System.out.println("新しいスレッドが走り始めました : "+this.name);//for debug
		Users users = new Users(in, out);
		users.readUserData();
		boolean bigFlag = true;

		try{
			while (bigFlag) {
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
					while(!flag) flag = users.authenticate();

					out.println("ようこそ！\n終了したくなったときは、quitと打ってください。");
					out.println("いつでもメニュー画面に戻ることができます。");

					out.print("Input message : ");
					out.flush();
					String line = in.readLine();
					while(!"quit".equals(line)){
						System.out.println(socket.toString()+": のClientが動いています");
						ChatServer.sendAll(name + " : " + line);
						out.print("Input message : ");
						out.flush();
						line = in.readLine();
					}
					break;
				case 3:
					users.deleteUser();
					break;
				case 4:
					bigFlag = false;
					out.println("じゃあね～");
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


