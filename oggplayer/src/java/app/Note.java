package app;

public class Note implements Comparable<Note>
{
	public enum Key
	{
		C   (0,  "C"),
		Csh (1,  "C#"),
		D   (2,  "D"),
		Dsh (3,  "D#"),
		E   (4,  "E"),
		F   (5,  "F"),
		Fsh (6,  "F#"),
		G   (7,  "G"),
		Gsh (8,  "G#"),
		A   (9,  "A"),
		Ash (10, "A#"),
		B   (11, "B");
		
		int index;
		String name;
		
		Key(int value, String name)
		{
			this.index = value;
			this.name = name;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
		
		public static Key fromIndex(int index)
		{
			switch (index)
			{
			case 0:  return C;
			case 1:  return Csh;
			case 2:  return D;
			case 3:  return Dsh;
			case 4:  return E;
			case 5:  return F;
			case 6:  return Fsh;
			case 7:  return G;
			case 8:  return Gsh;
			case 9:  return A;
			case 10: return Ash;
			case 11: return B;
			default: throw new RuntimeException("A strange value");
			}
		}
		
		public int getIndex() { return index; }
		
		/*public int hashCode() 
		{
			return index;
		};*/
	}
	public Key key;
	public int octave;
	
	public Note(Key tone, int octave)
	{
		this.key = tone;
		this.octave = octave;
	}
	
	Key getTone() { return key; }
	int getOctave() { return octave; }
	
	@Override
	public boolean equals(Object other)
	{
		if (other instanceof Note)
		{
			Note otherNote = (Note)other;
			if (otherNote.octave == octave && otherNote.key == key)
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		return key.toString() + octave;
	}
	
	@Override
	public int hashCode()
	{
		return key.getIndex() + octave * 12;
	}

	@Override
	public int compareTo(Note o)
	{
		return (octave * 12 + key.getIndex()) - (o.octave * 12 + key.getIndex());
	}
}
