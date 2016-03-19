package gem.day16.homework;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/*客户端：发送给服务器端一个字符串filename 表示服务器上的一个文件，然后从服务器
端读入文件内容，并起名叫server_filename 保存在当前目录。
   网上接收到的数据 ==》存到文件中*/
public class TCPClient {
	public static void main(String[] args) throws UnknownHostException, IOException {
		Socket socket = new Socket("localhost",9998);
		new Thread(new SendFile(socket)).start();
	}

}

class SendFile implements Runnable{
	Socket socket;
	public SendFile(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		PrintStream out = null;
		BufferedReader br = null;
		BufferedWriter bw = null;
		
		Scanner scanner = new Scanner(System.in);
		try {
			out = new PrintStream(socket.getOutputStream());
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bw = new BufferedWriter(new FileWriter("src/day16/server_filename.txt"));
			
			//发送给服务器端一个字符串filename
			System.out.println("请输入文件名：");
			String fileName = scanner.nextLine();
			out.println(fileName);
			out.flush();	
			//从服务器端读入文件内容
			//String message = br.readLine();
			String str=null;
			while((str=br.readLine())!=null) {
				bw.write(str);
				bw.newLine();
				bw.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			//close
			try {
				if (out != null)
					out.close();
				if (br != null)
					br.close();
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		
	}
	
}