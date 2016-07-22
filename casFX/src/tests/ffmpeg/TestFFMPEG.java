package tests.ffmpeg;

import java.io.IOException;

class TestFFMPEG {
	
	
	static Thread thread;
	
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		
		
		thread = new Thread(){
			
			String ffmpegPath = "D:\\Securety\\Programms\\ffmpeg\\bin\\";
			String[] ffmpeg = new String[] {ffmpegPath + "ffmpeg", "-i", "D:\\Users\\Videos\\BINGO.mp4", "-ss", "00:00:20.1",
					"-to", "00:00:30", "-y", "-async", "1", "D:\\Users\\Videos\\BINGO_20-30.mp4"};
			
			Runtime rt = Runtime.getRuntime();
			
			Process p = rt.exec(ffmpeg);
		};
		
		
		thread.start();
		
	
		
		// if you want to wait for the process to finish
		//p.waitFor();
		


	}
	
	
}