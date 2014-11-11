package app.mistery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import app.Meter;
import app.Note;
import app.NoteSoundPool;

import vam.MixedSounds;
import vam.MovedSound;
import vam.SoundSource;

public class RandomNotesShuffler
{
	private long randomSeed;

	private Meter meter;
	private double beatsBetweenNotes;
	private NoteSoundPool noteSoundPool;
	private HashSet<Note>[] notes;
	private int bufferSize;
	
	@SuppressWarnings("unchecked")
	public RandomNotesShuffler(int bufferSize)
	{
		this.bufferSize = bufferSize;
		randomSeed = System.currentTimeMillis();
		System.out.println(randomSeed);
		
		notes = new HashSet[4];
		for (int i = 0; i < 4; i++)
		{
			notes[i] = new HashSet<Note>();
		}
	}
	
	public void setMeter(Meter value) { meter = value; }
	public void setBeatsBetweenNotes(double value) { beatsBetweenNotes = value; }
	public void setNoteSoundPool(NoteSoundPool value) { noteSoundPool = value; }
	
	public void addNotes(int level, Note[] notes)
	{
		ArrayList<Note> notesList = new ArrayList<Note>();
		for (int i = 0; i < notes.length; i++)
		{
			notesList.add(notes[i]);
		}
		
		this.notes[level].addAll(notesList);
	}

	public void addNotes(int level, Collection<? extends Note> notes)
	{
		this.notes[level].addAll(notes);
	}
	
	public SoundSource mix(int[] scheme) throws MovedSound.Error, MixedSounds.Error
	{
		Random rnd = new Random(randomSeed);
		
		MixedSounds res = new MixedSounds(bufferSize);
		
		double time = 0;

		
		Note[/*level*/][/*note index*/] notesArray = new Note[4][];
		for (int i = 0; i < 4; i++)
		{				
			notesArray[i] = notes[i].toArray(new Note[] {});
			Arrays.sort(notesArray[i]);
		}
		
		List<Integer[]> indices = new ArrayList<Integer[]>();
		int level, noteIndex;
		for (int i = 0; i < scheme.length; i++)
		{
			MovedSound ms = new MovedSound(bufferSize);
			Note nextNote = null;
			
			// Generating the next note
			boolean ok;
			do
			{
				ok = true;
				
				// Generating the level of the note. Level should not be higher then the one specified in the scheme 
				level = rnd.nextInt(scheme[i]);
				
				// Generating the note index in the level
				noteIndex = rnd.nextInt(notesArray[level].length);

				// The probability of the note to appear should be smal if it's level is much lower than the one from the scheme
				if (level < scheme[i])
				{
					int delta = scheme[i] - level;
					
					// Delta = 1 -> probability of not ok is 1/2
					// Delta = 2 -> probability of not ok is 2/3
					
					if (rnd.nextInt(delta + 1) > 0) ok = false;
				}
				
				// Checking if the generated note is good in the context				
				if (indices.size() >= 1 && level == indices.get(indices.size() - 1)[0] && noteIndex == indices.get(indices.size() - 1)[1])
				{
					// The current note shouldn't be equal to the previous one
					ok = false;
				}
				
				if (indices.size() >= 2 && level == indices.get(indices.size() - 2)[0] && noteIndex == indices.get(indices.size() - 2)[1])
				{
					if (rnd.nextInt(3) > 0)
					{
						// If the next note is equal to the one that is 2 notes before,
						// the result is not ok with 2/3 chance
						ok = false;
					}
				}
			
			}
			while (!ok);
			
			indices.add(new Integer[] { level, noteIndex });
			
			int nextNoteLevel = indices.get(indices.size() - 1)[0];
			int nextNoteIndex = indices.get(indices.size() - 1)[1];
			nextNote = notesArray[nextNoteLevel][nextNoteIndex];

			ms.setSound(noteSoundPool.getSoundForNote(nextNote));
			ms.setDelay(time);
			res.addSound(ms);
			
			time += meter.beatsToTimeSpan(beatsBetweenNotes);
		}
		
		return res;
	}
}
