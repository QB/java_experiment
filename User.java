//memo:UserはUsersの内部クラスにするのはどうなんだろうか
/*
 * 1:ClientがUserを持つ
 * 2:UserはUsersで管理
 * 3:Usersを持つのはServer
 * 4:ClientはServerのUsersを通してUserを作成&取得(認証)
 * 5:Usersはデータベースとやりとりする
 */

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.security.*;

public class User{
	private String name = null; 
	private String password = null;

	//Clientから呼び出す　ここでバリデイトしてClientでtrueが返ってくるまで繰り返し呼び出せばよい
	//修正1：Clientではなくユーザを管理するUsersでユーザの追加、削除、認証を行う
	boolean setName(String name){
		this.name = name;
		return true;
	}
	boolean setPassword(String password){
		this.password = password;
		return true;
	}
	String getName(){
		return this.name;
	}
	String getPassword(){
		return this.password;
	}
}

class Users{
	private List<User> users = new ArrayList<User>();

	//ユーザで使う入出力
	private BufferedReader in;
	private PrintWriter out;

	private String filename = "users.dat";

	//users作成時にユーザで使う入出力を渡す
	public Users(BufferedReader in, PrintWriter out){
		this.in = in;
		this.out = out;
	}

	// ファイルから既存のユーザデータを読み込む
	public boolean readUserData(){
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
			String line;

			while ((line = br.readLine()) != null) {
				String text[] = line.split(",", 2);
				User user = new User();
				user.setName(text[0]);
				user.setPassword(text[1]);
				users.add(user);
			}
			br.close();

		} catch(Exception e) {
			System.err.println(e);
		}
		return true;
	}

	// ユーザ認証 
	public boolean authenticate() {

		out.println("=========================");
		out.println("■ ログインします。ログイン情報を入力してください。");
		out.println("=========================");
		out.flush();

		try {
			out.print("name : ");
			out.flush();
			String name = in.readLine();

			out.print("password : ");
			out.flush();
			String password = hash(in.readLine());

			for(User user : users){
				if(user.getName().equals(name)){
					if(user.getPassword().equals(password)){
						out.println("ユーザ認証に成功しました");
						out.flush();
						return true;
					}else{
						out.println("パスワードが間違っています");
						out.flush();
						return false;
					}
				}
			}
			out.println("ユーザ名が間違っています");
			out.flush();
			return false;

		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	//超簡易ユーザ認証(User)
	//引数passwordは、ハッシュ化済みのものが渡されることを想定している
	public User getUser(String name, String password){
		for(User user : users){
			if(user.getName().equals(name)){
				if(user.getPassword().equals(password)){
					return user;
				}else{
					return null;
				}
			}
		}
		return null;
	}

	public boolean createUser(){
		User user = new User();

		out.println("=========================");
		out.println("■ アカウントを作成します。");
		out.println("=========================");
		out.flush();

		try{
			String name, password;
			boolean flag1 = false, flag2 = true;

			//ユーザ名が半角英数+下線のみで構成されているか調べるための正規表現
			Pattern p = Pattern.compile("^[a-zA-Z0-9_]+$");

			//nameを登録
			while(true){
				out.print("name : ");
				out.flush();
				name = in.readLine();
				flag1 = p.matcher(name).find();

				for(User u : users)
					if(u.getName().equals(name)) flag2 = false;

				if(!flag1) {
					out.println("ユーザ名に使えるのは、半角英数字と下線のみです。");
					out.flush();
				} else if(!flag2) {
					out.println("そのユーザ名は既に使われています。");
					out.flush();
				} else {
					if(user.setName(name)) break;
				}
			}
			//passwordを登録
			//パスワードはどうせハッシュ化するので、どんな文字を使っても可
			while(true){
				out.print("password : ");
				out.flush();
				password = hash(in.readLine());
				if(user.setPassword(password)) break;
			}
			//userを追加
			users.add(user);

			//ユーザ情報を格納するファイルにも追加
			PrintWriter pw = new PrintWriter(new FileWriter(filename, true));
			pw.println(name + "," + password);
			pw.close();

		}catch(IOException error){
			error.printStackTrace();
		}
		return true;
	}

	public boolean deleteUser(){

		out.println("=========================");
		out.println("■ アカウントを削除します。");
		out.println("=========================");
		out.flush();

		try{
			out.print("Name : ");
			out.flush();
			String name = in.readLine();
			
			out.print("password : ");
			out.flush();
			String password = hash(in.readLine());

			User user = this.getUser(name, password);
			if(user!=null){
				this.users.remove(user);
				(new File(filename)).delete();

				PrintWriter pw = new PrintWriter(new FileWriter(filename, true));
				for(User u : users)
					pw.println(u.getName() + "," + u.getPassword());
				pw.close();

				out.println("ユーザを消去しました");
				out.flush();
			}else{
				out.println("ユーザ認証に失敗しました");
				out.flush();
			}
		}catch(IOException error){
			error.printStackTrace();
		}
		return true;
	}

	// パスワードをハッシュ化する関数
	public String hash(String password){
		String digest = "";
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(password.getBytes());
			byte[] hash = md.digest();

			StringBuilder sb = new StringBuilder();
			for(byte b : hash) sb.append(String.format("%02x", b));
			digest = sb.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return digest;
	}

	public static void main(String[] args) throws ClassNotFoundException {

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
		
		Users users = new Users(in, out);

		users.readUserData();
		users.authenticate();

		users.deleteUser();
	}
}


