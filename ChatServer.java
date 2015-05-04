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
				System.out.println("-----------新しい接続を受け付けました---------------");//for debug
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
		try{
			System.out.println("新しいスレッドが走り始めました : "+this.name);//for debug
			Users users = new Users(in, out);

			// とりあえず今はデータベースに対応していないので、毎回ユーザを新規作成する。
			// とりあえず、ここでアカウント作成しないと、ログインするアカウントが無い。
			users.createUser();

			// ここで認証をかける.
			// 入力が空なら認証失敗するので、入力がnullかチェックする部分は削りました(´・ω・`)
			while(!flag) {
				out.println("ログインします。ログイン情報を入力してください。");
				out.flush();

				out.print("What's your name? : ");
				out.flush();
				name = in.readLine();

				out.print("What's your passwd? : ");
				out.flush();
				passwd = in.readLine();

				flag = users.authenticate(name, passwd);
			}

			//System.out.println("名前が設定されました : "+this.name);//for debug
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


