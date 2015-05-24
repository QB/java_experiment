import java.io.*;

class PassSample{

public void test(){

Console console = System.console();
          char[] pw = console.readPassword("パスワード: ");

          console.printf("確認: ");

String pass = String.valueOf(pw);
	System.out.println(pass);

          console.printf("\n");
}


public static void main(String[] args) {
	PassSample ps = new PassSample();
	ps.test();
}

}

