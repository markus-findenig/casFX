package controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * 
 * http://stackoverflow.com/questions/10864317/how-to-break-a-file-into-pieces-using-java
 *
 */
public class FileStreamController {

	static FileInputStream fis; // video file
	static int frame_nb; // current frame nb

	static File file_in = new File("D:\\Users\\Videos\\Test\\TheSimpsonsMovie1080pTrailer.mp4");
	static File file_out = new File("D:\\Users\\Videos\\Test\\stream.mp4");

	public static void main(String[] args) throws Exception {

		fis = new FileInputStream(file_in);
		frame_nb = 0;

		int offset = 0;
		int length = 10485760; // 1024 * 1024 * 10 = 10 MB
		
		int partCounter = 1;//I like to name parts from 001, 002, 003, ...
        //you can change it to 0 if you want 000, 001, ...
		
		String name = file_in.getName();

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
		
		//FileOutputStream out = new FileOutputStream(file_out);
		
		FileOutputStream out = null;
		
		int tmp = 0;
        while ((tmp = bis.read(buffer)) > 0) {
		//while (fis.available() != 0) {
			//bis.read(buffer);
			
			File newFile = new File(file_in.getParent(), name + "." + String.format("%03d", partCounter++));
			out = new FileOutputStream(newFile);
			out.write(buffer, 0, tmp);//tmp is chunk size
        	
        	
        	
        	
        	
        	
        }
		


	}

}
