package app;

import vam.MixedSounds;
import vam.PortAudioPlayer;
import vam.SoundSource;
import vam.VorbisFileReader;
import app.mistery.PianoNoteSoundPool;
import app.mistery.RandomPianoGenerator;
import app.mistery.RandomPianoGeneratorA;
import app.mistery.RandomPianoGeneratorAsh;

public class Application
{
	public static final int RESULT_SUCCESS				= 0;
	public static final int RESULT_NO_INPUT_FILE		= 1;
	public static final int RESULT_SOUNDSOURCE_ERROR			= 2;

	private static String twoDig(int value)
	{
		if (value >= 10) 
			return String.valueOf(value);
		else 
			return "0" + String.valueOf(value);
	}
	
	private static String timeToString(double time)
	{
		double atime = Math.abs(time);
		int min = (int)(atime) / 60;
		int sec = (int)(atime) % 60;
		int sp10 = (int)(atime * 100) % 100;
		
		return (time < 0 ? "-" : " ") + twoDig(min) + ":" + twoDig(sec) + "." + twoDig(sp10);
	}
	
	private static final int INPUT_BUFFER = 1024;
		
	public static void main0(String[] args)
	{
		//System.load("/Users/il/Projects/ogg-vorbis/patry/bin/avian-embed");
		
		
		try
		{
			PianoNoteSoundPool pianoNoteSoundPool = new PianoNoteSoundPool(INPUT_BUFFER);
			
			Meter meter = new Meter(9, 4, 90);
			RandomPianoGenerator rpgA = new RandomPianoGeneratorA(pianoNoteSoundPool, meter, 2, INPUT_BUFFER);
			RandomPianoGenerator rpgAsh = new RandomPianoGeneratorAsh(pianoNoteSoundPool, meter, 2, INPUT_BUFFER);

			SoundSource theme_ss = new VorbisFileReader("../New/Theme.ogg", INPUT_BUFFER);
			
			// Scheme for intro piano arpeggio
			int[] scheme = new int[] { 1, 2, 3, 4, 2, 4, 4, 3, 1, 3, 4, 3, 4, 2, 1, 4, 3, 2 };
			
			SoundSource rpgA_1_ss = rpgA.mix(9, scheme);
			SoundSource rpgA_2_ss = rpgA.mix(18, scheme);
			SoundSource rpgAsh_3_ss = rpgAsh.mix(27, scheme);
			SoundSource rpgA_4_ss = rpgA.mix(36, scheme);

			MixedSounds resultSource = new MixedSounds(INPUT_BUFFER);
			resultSource.addSound(theme_ss);
			resultSource.addSound(rpgA_1_ss);
			resultSource.addSound(rpgA_2_ss);
			resultSource.addSound(rpgAsh_3_ss);
			resultSource.addSound(rpgA_4_ss);
			
			resultSource.rewind(-1);
			
			PortAudioPlayer pap = new PortAudioPlayer(resultSource.getChannels(), resultSource.getRate(), 1024);

			pap.setSoundSource(resultSource);
			
			pap.play();
			
			while (resultSource.getPlayhead() < resultSource.getEndTime())
			{
				try
				{
					Thread.sleep(10);
					
					System.out.print("\rPlaying the file... [ " + timeToString(resultSource.getStartTime()) + " / " + timeToString(resultSource.getPlayhead()) + " / " + timeToString(resultSource.getEndTime()) + "  ]");
					
				} 
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			
			pap.close();
			System.gc();
			
			System.out.println("\nBye.");
			System.exit(RESULT_SUCCESS);
		} 
		catch (SoundSource.Error e)
		{
			e.printStackTrace();
			System.exit(RESULT_SOUNDSOURCE_ERROR);
		} 
	}	
	
	public static void main(String[] args)
	{
		try
		{
			if (args.length == 0)
			{
				System.err.println("No input file");
				System.exit(RESULT_NO_INPUT_FILE);
				return;
			}

			System.out.println("Input file: " + args[0]);

			VorbisFileReader vfr = new VorbisFileReader(args[0], 64);

			System.out.println("Bitstream has " + vfr.getChannels() + " channels, " + vfr.getRate() + "Hz, quality is " + (int)(Math.round((float)vfr.getBitsPerSecond() / 1000)) + "Kbps (on average)");
			System.out.println("Encoded by " + vfr.getVendor());

			String[] comments = vfr.getComments(); 
			if (comments.length > 0) System.out.println("Comments:");
			for (int i = 0; i < comments.length; i++)
			{
				System.out.println("  " + comments[i]);
			}
			
			PortAudioPlayer pap = new PortAudioPlayer(vfr.getChannels(), vfr.getRate(), 1024);

			pap.setSoundSource(vfr);
			pap.play();
			
			System.out.println();
			while (vfr.getPlayhead() < vfr.getEndTime())
			{
				try
				{
					Thread.sleep(10);
					
					int min = (int)(vfr.getPlayhead()) / 60;
					int sec = (int)(vfr.getPlayhead()) % 60;
					int sp10 = (int)(vfr.getPlayhead() * 100) % 100;
					
					int lmin = (int)(vfr.getEndTime()) / 60;
					int lsec = (int)(vfr.getEndTime()) % 60;
					int lsp10 = (int)(vfr.getEndTime() * 100) % 100;
					
					System.out.print("\rPlaying the file... [ " + twoDig(min) + ":" + twoDig(sec) + "." + twoDig(sp10) + " / " + twoDig(lmin) + ":" + twoDig(lsec) + "." + twoDig(lsp10) + " ]");
					
				} 
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			
			pap.close();
			vfr.close();
			System.gc();
			
			System.out.println("\nBye.");
			System.exit(RESULT_SUCCESS);
		} 
		catch (VorbisFileReader.Error e)
		{
			e.printStackTrace();
			System.exit(RESULT_SOUNDSOURCE_ERROR);
		}
	}
	
	
	public static void main1(String[] args)
	{
		try
		{
			if (args.length == 0)
			{
				System.err.println("No input files");
				System.exit(RESULT_NO_INPUT_FILE);
				return;
			}

			MixedSounds ms = new MixedSounds(INPUT_BUFFER);

			for (int argIndex = 0; argIndex < args.length; argIndex++)
			{
				
				System.out.println("Input file: " + args[argIndex]);
	
				VorbisFileReader vfr = new VorbisFileReader(args[argIndex], 64);
	
				System.out.println("Bitstream has " + vfr.getChannels() + " channels, " + vfr.getRate() + "Hz, quality is " + (int)(Math.round((float)vfr.getBitsPerSecond() / 1000)) + "Kbps (on average)");
				System.out.println("Encoded by " + vfr.getVendor());
	
				String[] comments = vfr.getComments(); 
				if (comments.length > 0) System.out.println("Comments:");
				for (int i = 0; i < comments.length; i++)
				{
					System.out.println("  " + comments[i]);
				}
				System.out.println();
				
				ms.addSound(vfr);
			}
			
			ms.rewind(-3);
			
			PortAudioPlayer pap = new PortAudioPlayer(ms.getChannels(), ms.getRate(), 1024);

			pap.setSoundSource(ms);
			pap.play();
			
			while (ms.getPlayhead() < ms.getEndTime())
			{
				try
				{
					Thread.sleep(10);
					
					double playhead = Math.abs(ms.getPlayhead());
					
					int min = (int)(playhead) / 60;
					int sec = (int)(playhead) % 60;
					int sp10 = (int)(playhead * 100) % 100;
					
					int lmin = (int)(ms.getEndTime()) / 60;
					int lsec = (int)(ms.getEndTime()) % 60;
					int lsp10 = (int)(ms.getEndTime() * 100) % 100;
					
					System.out.print("\rPlaying the file... [ " + (ms.getPlayhead() < 0 ? "-" : " ") + twoDig(min) + ":" + twoDig(sec) + "." + twoDig(sp10) + " / " + twoDig(lmin) + ":" + twoDig(lsec) + "." + twoDig(lsp10) + "  ]");
					
				} 
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			
			pap.close();
			ms.close();
			System.gc();
			
			System.out.println("\nBye.");
			System.exit(RESULT_SUCCESS);
		} 
		catch (SoundSource.Error e)
		{
			e.printStackTrace();
			System.exit(RESULT_SOUNDSOURCE_ERROR);
		} 
	}
}