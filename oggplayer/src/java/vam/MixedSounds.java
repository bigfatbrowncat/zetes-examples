package vam;

import java.util.ArrayList;
import java.util.List;

public class MixedSounds extends SoundSource
{
	public enum ErrorType
	{
		etUnsupportedChannelsNumber(0),
		etInequalRate(1);
		
		@SuppressWarnings("unused")	// used in native code
		private int value;
		
		ErrorType(int value) { this.value = value; }
		static ErrorType fromValue(int i)
		{
			switch (i)
			{
			case 0: return etUnsupportedChannelsNumber;
			case 1: return etInequalRate;
			default:
				throw new RuntimeException("Strange value");
			}
		}
	}

	public static class Error extends SoundSource.Error
	{
		private static final long serialVersionUID = 6886145280554167342L;
		private ErrorType type;
		private String caller;
		protected Error(ErrorType type, String caller)
		{
			super("error type " + type.toString() + ", caller is " + caller);
			this.type = type;
			this.caller = caller;
		}
		public ErrorType getType() { return type; }
		public String getCaller() { return caller; }		
	}

	private List<SoundSource> sounds;
	
	private native static long createNativeInstance(int bufferSize);
	
	/**
	 * Frees the native allocated object
	 */
	private native void destroyNativeInstance();
	
	public MixedSounds(int bufferSize)
	{
		super(createNativeInstance(bufferSize));
		sounds = new ArrayList<SoundSource>();
	}

	/**
	 * Closes the file and releases all native resources.
	 */
	public void close()
	{
		if (nativeInstance != 0)
		{
			destroyNativeInstance();
			nativeInstance = 0;
		}
	}
	
	@Override
	protected void finalize() throws Throwable
	{
		try
		{
			close();
		}
		finally
		{
			super.finalize();
		}
	}
		
	
	@Override
	public native float[] readSample() throws Error;

	@Override
	public native int getChannels();

	@Override
	public native void rewind(double position) throws Error;

	@Override
	public native double getPlayhead();
	
	@Override
	public native double getStartTime();

	@Override
	public native double getEndTime();

	@Override
	public native int getRate();
	
	private native void addSoundNative(SoundSource sound) throws Error;
	private native void removeSoundNative(SoundSource sound);

	public void addSound(SoundSource sound) throws Error
	{
		addSoundNative(sound);
		sounds.add(sound);
	}
	
	public void removeSound(SoundSource sound)
	{
		removeSoundNative(sound);
		sounds.remove(sound);
	}

}
