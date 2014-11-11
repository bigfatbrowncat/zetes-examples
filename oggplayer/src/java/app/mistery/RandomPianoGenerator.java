package app.mistery;

import vam.MovedSound;
import vam.SoundSource;
import app.Meter;
import app.Note;
import app.NoteSoundPool;
import app.Note.Key;

public class RandomPianoGenerator
{
	private int bufferSize;
	private Meter meter;
	private int notesPerBeat;
	private RandomNotesShuffler randomNotesShuffler;
	
	public RandomPianoGenerator(NoteSoundPool noteSoundPool, Note[][] notesWithLevels, Meter meter, int notesPerBeat, int bufferSize) throws SoundSource.Error
	{
		this.bufferSize = bufferSize;
		this.meter = meter;
		this.notesPerBeat = notesPerBeat;
		
		randomNotesShuffler = new RandomNotesShuffler(bufferSize);
		randomNotesShuffler.setNoteSoundPool(noteSoundPool);

		for (int i = 0; i < 4; i++)
		{
			randomNotesShuffler.addNotes(i, notesWithLevels[i]);
		}
		
		randomNotesShuffler.setBeatsBetweenNotes(1.0 / notesPerBeat);
		randomNotesShuffler.setMeter(meter);
	}
	
	public SoundSource mix(double beats_delay, int[] scheme) throws SoundSource.Error
	{
		MovedSound ms = new MovedSound(bufferSize);
		ms.setDelay(meter.beatsToTimeSpan(beats_delay));
		
		ms.setSound(randomNotesShuffler.mix(scheme));
		return ms;
	}
}
