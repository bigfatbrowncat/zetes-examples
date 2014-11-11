package app.mistery;

import vam.SoundSource.Error;
import app.Meter;
import app.Note;
import app.NoteSoundPool;
import app.Note.Key;

public class RandomPianoGeneratorAsh extends RandomPianoGenerator
{

	public RandomPianoGeneratorAsh(NoteSoundPool noteSoundPool,
			Meter meter, int notesPerBeat, int bufferSize) throws Error
	{
		super(noteSoundPool,
		new Note[][] 
		{  
			// Level 0
			new Note[]
			{
				new Note(Key.Ash, 3),
				new Note(Key.Ash, 4),
			},
			// Level 1
			new Note[]
			{
					new Note(Key.F, 3),
					new Note(Key.F, 4),
			},
			// Level 2
			new Note[]
			{
					new Note(Key.Csh, 4),
			},
			// Level 3
			new Note[]
			{
					new Note(Key.G, 4),
					new Note(Key.Gsh, 4),
			}
		}, meter, notesPerBeat, bufferSize);
	}

}
