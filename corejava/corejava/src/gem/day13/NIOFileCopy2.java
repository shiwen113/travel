package gem.day13;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOFileCopy2 {

	public static void main(String[] args) throws Exception {
		// 获得输入文件的FileChannel
		FileInputStream fin = new FileInputStream(
				"src\\gem\\day13\\NIOFileCopy2.java");
		FileChannel fc1 = fin.getChannel();
		
		// 获得输出文件的FileChannel
		FileOutputStream fout = new FileOutputStream(
				"src\\gem\\day13\\NIOFileCopy2.txt");
		FileChannel fc2 = fout.getChannel();
		//读一块，写一块，new byte[512]
		ByteBuffer buffer = ByteBuffer.allocate(512);
		//buffer:position,limit,capacity
		while(fc1.read(buffer) != -1) {
			//
			buffer.flip();
			fc2.write(buffer);
			//
			buffer.clear();
		}
		//关闭相应的资源
		fin.close();
		fout.close();
		fc1.close();
		fc2.close();
		
		
		
		
	}

}
