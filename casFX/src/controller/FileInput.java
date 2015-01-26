package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;

public class FileInput {

	//public static void main(String[] args) {
	
	public static void FileInputLoader() {

		File file_in = new File("E:\\Users\\Videos\\Test\\TheSimpsonsMovie-1080pTrailer.mp4");
		File file_out = new File("E:\\Users\\Videos\\Test\\stream.mp4");

		try {
			InputStream inputStream = new FileInputStream(file_in);
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferReader = new BufferedReader(inputStreamReader);
			
			
			OutputStream outputStream = new FileOutputStream(file_out);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
			BufferedWriter bufferWriter = new BufferedWriter(outputStreamWriter);
				

			int data = bufferReader.read();
			bufferWriter.write(data);
			while(data != -1){
			    //char theChar = (char) data;
			    
				data = bufferReader.read();
			    
			    bufferWriter.write(data);
			    			    
			    //System.out.println(data);
			}

			bufferWriter.close();
			bufferReader.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Liest den Inhalt einer Datei aus.
	 *
	 * @param file_in
	 *            Dateiname
	 * @return Dateiinhalt (erste Zeile)
	 * @throws Exception
	 */
	public static String readFile(File file_in) throws Exception {
		InputStream is = new FileInputStream(file_in);
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader isrb = new BufferedReader(isr);
		return isrb.readLine();
	}

	
}
