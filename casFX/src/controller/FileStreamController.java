package controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileStreamController {

	static FileInputStream fis; // video file
	static int frame_nb; // current frame nb

	static File file_in = new File("D:\\Users\\Videos\\Test\\TheSimpsonsMovie1080pTrailer.mp4");
	static File file_out = new File("D:\\Users\\Videos\\Test\\stream.mp4");

	public static void main(String[] args) throws Exception {

		fis = new FileInputStream(file_in);
		frame_nb = 0;

		int offset = 0;
		int length = 1048576; // 1 MB

		byte[] byteOdd = new byte[length];
		byte[] byteEven = new byte[length];

		byte[] buffer = new byte[length];
		
		// if 0 = EOF
//		while (fis.available() != 0) {
//			fis.read(byteOdd, offset, length);
//			
//			offset = offset + length;
//			System.out.println(byteOdd);
//		}

		BufferedInputStream bis = new BufferedInputStream(fis);
		
		BufferedOutputStream bos = null;
		
		FileOutputStream out = new FileOutputStream(file_out);
		
		int tmp = 0;
        //while ((tmp = bis.read(buffer)) > 0) {
		while (fis.available() != 0) {
			bis.read(buffer);
			
			out.write(buffer);
        	
        	
        	
        	
        	
        	
        }
		


	}

}
