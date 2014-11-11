package app.mistery;

import vam.VorbisFileReader;
import vam.SoundSource;
import app.Note;
import app.NoteSoundPool;
import app.Note.Key;

public class PianoNoteSoundPool extends NoteSoundPool
{
	public PianoNoteSoundPool(int bufferSize) throws SoundSource.Error
	{
		addSoundForNote(new Note(Key.F,   3), new VorbisFileReader("../New/arp2/F3.ogg",  bufferSize));
		addSoundForNote(new Note(Key.A,   3), new VorbisFileReader("../New/arp1/A3.ogg",  bufferSize));
		addSoundForNote(new Note(Key.Ash, 3), new VorbisFileReader("../New/arp2/A#3.ogg", bufferSize));
		addSoundForNote(new Note(Key.A,   4), new VorbisFileReader("../New/arp1/A4.ogg",  bufferSize));
		addSoundForNote(new Note(Key.Ash, 4), new VorbisFileReader("../New/arp2/A#4.ogg", bufferSize));
		addSoundForNote(new Note(Key.B,   4), new VorbisFileReader("../New/arp1/B4.ogg",  bufferSize));
		addSoundForNote(new Note(Key.Csh, 4), new VorbisFileReader("../New/arp2/C#4.ogg", bufferSize));
		addSoundForNote(new Note(Key.E,   4), new VorbisFileReader("../New/arp1/E4.ogg",  bufferSize));
		addSoundForNote(new Note(Key.F,   4), new VorbisFileReader("../New/arp2/F4.ogg",  bufferSize));
		addSoundForNote(new Note(Key.G,   4), new VorbisFileReader("../New/arp1/G4.ogg",  bufferSize));
		addSoundForNote(new Note(Key.Gsh, 4), new VorbisFileReader("../New/arp2/G#4.ogg",  bufferSize));
		addSoundForNote(new Note(Key.C,   5), new VorbisFileReader("../New/arp1/C5.ogg",  bufferSize));		
	}
}
