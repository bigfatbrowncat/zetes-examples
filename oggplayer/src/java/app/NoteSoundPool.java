package app;

import java.util.HashMap;

import vam.SoundSource;

public class NoteSoundPool
{
	private HashMap<Note, SoundSource> soundsForNotes;
	
	public NoteSoundPool()
	{
		soundsForNotes = new HashMap<Note, SoundSource>();
	}
	
	public void addSoundForNote(Note note, SoundSource soundSource)
	{
		soundsForNotes.put(note, soundSource);
	}
	
	public SoundSource getSoundForNote(Note note)
	{
		return soundsForNotes.get(note);
	}
}
