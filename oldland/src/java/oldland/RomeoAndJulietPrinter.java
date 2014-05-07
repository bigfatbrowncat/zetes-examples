package oldland;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import zetes.WinLinMacApi;

public class RomeoAndJulietPrinter extends Thread {
	private int PAUSE_BETWEEN_CHARS = 40;
	private int PAUSE_BETWEEN_LINES = 100;
	
	private OldLandTerminalWindow terminalWindow;
	private BufferedReader br;
	
	public RomeoAndJulietPrinter(OldLandTerminalWindow terminalWindow) {
		this.terminalWindow = terminalWindow;
		try {
			System.out.println(WinLinMacApi.locateResource("data", "romeo.txt"));
			br = new BufferedReader(new InputStreamReader(new FileInputStream(WinLinMacApi.locateResource("data", "romeo.txt"))));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setDaemon(true);
	}
	
	private void typeln(String line) throws InterruptedException {
		for (int i = 0; i < line.length(); i++) {
			terminalWindow.print(((Character)line.charAt(i)).toString());
			Thread.sleep(PAUSE_BETWEEN_CHARS);
		}
		terminalWindow.println("");
	}
	
	@Override
	public void run() {
		
		// Reading the play to a string
		
		StringBuffer sb = new StringBuffer();
		
		try {
			for (int c = br.read(); c != -1; c = br.read()) {
				if (c == '\n') {
					// Here the line ends. Printing the text out.
					String s = sb.toString();

					if (s.length() > 0) {
					
						if (s.startsWith("        ")) {
							// Title
							terminalWindow.setForeColor(0xFFAAAAFF);
						} else if (s.charAt(0) != ' ') {
							if (s.startsWith("ACT")) {
								terminalWindow.setForeColor(0xFF55FF88);
							} else if (s.startsWith("PROLOGUE") || s.startsWith("SCENE")) {
								terminalWindow.setForeColor(0xFF33AA66);
							} else {
								terminalWindow.setForeColor(0xFF888888);
							}
						} else {
							terminalWindow.setForeColor(0xFFCCCCCC);
						}
					}
					
					typeln(sb.toString());
					Thread.sleep(PAUSE_BETWEEN_LINES);
					sb.setLength(0);
				}
				
				if (c != '\r' && c != '\n') {		// Ignoring \r to equalize Unix and Windows encodings
					sb.append((char)c);
				}
			}

			// Printing out the last line
			terminalWindow.println(sb.toString());

			
		} catch (IOException e) {
			// Do nothing cause it's impossible
			e.printStackTrace();
		} catch (InterruptedException e) {
			// Do nothing cause it's impossible
			e.printStackTrace();
		}
		
	}
}
