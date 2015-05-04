import java.io.*;
import java.net.*;

public class GenericTerminal{
	private Socket sock = null;
	private OutputStream sock_out = null;
	private InputStream sock_in = null;

	//接続を確立
	public void estConnetion(String hostname, int port) throws IOException, UnknownHostException{
		this.sock = new Socket(hostname,port);
		this.sock_in = new BufferedInputStream(this.sock.getInputStream());
		this.sock_out = this.sock.getOutputStream();
	}

	//ストリームを繋ぐ
	public void communicate() throws IOException{	
		StreamConnecter stdin_to_sock = new StreamConnecter(System.in, sock_out);
		StreamConnecter stdout_to_sock = new StreamConnecter(sock_in, System.out);

		(new Thread(stdin_to_sock)).start();
		(new Thread(stdout_to_sock)).start();
	}

	public static void main(String[] args) {
		GenericTerminal gt = null;
		try{
			gt = new GenericTerminal();
			gt.estConnetion(args[0], Integer.parseInt(args[1]));
			gt.communicate();
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}finally{
			try{
				if(gt.sock.isClosed()){
					gt.sock.close();
				}
			}catch(IOException e){
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}


class StreamConnecter implements Runnable{
	private InputStream in = null;
	private	OutputStream out = null;

	public StreamConnecter(InputStream in, OutputStream out){
		this.in = in;
		this.out = out;
	}

	public void run(){
		byte[] buff = new byte[1024];
		int len = 0;
		try{
			while(true){
				len = in.read(buff);
				if(len > 0) out.write(buff,0,len);
			}
		}catch(IOException e){
			e.printStackTrace();
			System.exit(1);
		}
	}
}
