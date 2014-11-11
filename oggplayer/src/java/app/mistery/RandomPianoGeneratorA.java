package app.mistery;

import vam.SoundSource.Error;
import app.Meter;
import app.Note;
import app.NoteSoundPool;
import app.Note.Key;

public class RandomPianoGeneratorA extends RandomPianoGenerator
{

	public RandomPianoGeneratorA(NoteSoundPool noteSoundPool,
			Meter meter, int notesPerBeat, int bufferSize) throws Error
	{
		super(noteSoundPool, new Note[][] 
		{  
			// Level 0
			new Note[]
			{
				new Note(Key.A, 3),
				new Note(Key.A, 4),
			},
			// Level 1
			new Note[]
			{
				new Note(Key.E, 4),
			},
			// Level 2
			new Note[]
			{
				new Note(Key.C, 5)
			},
			// Level 3
			new Note[]
			{
				new Note(Key.B, 4),
				new Note(Key.G, 4),
			}
		}, meter, notesPerBeat, bufferSize);
		
	}

}
