package com.javacodegeeks.xuggler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.xuggle.xuggler.Converter;

public class Transcoder {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//String inputStream = "rtmp://194.219.97.78/live/streamLive3H.263";
		//String outputStream = "rtmp://194.219.97.78/live/streamLive3H.264";
		File file_in = new File("D:\\Users\\Videos\\Test\\TheSimpsonsMovie-1080pTrailer.mp4");
		File file_out = new File("D:\\Users\\Videos\\Test\\xugglerOutStream.mp4");
		
		
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			inputStream = new FileInputStream(file_in);
			outputStream = new FileOutputStream(file_out);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		String[] parameters = new String[] { "--acodec", "libfaac", "--vcodec",
				"libx264", "--vpreset",
				"Xuggle/share/ffmpeg/libx264-ultrafast.ffpreset",
				inputStream.toString(), outputStream.toString() };

		Converter converter = new Converter();
		
		System.out.println(converter);

		Options options = converter.defineOptions();
		System.out.println(converter);
		
		CommandLine cmdLine;
		try {
			cmdLine = converter.parseOptions(options, parameters);
			converter.run(cmdLine);
			System.out.println("Finish!!!");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
