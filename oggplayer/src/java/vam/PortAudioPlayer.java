package vam;

public class PortAudioPlayer
{
	public enum State
	{
		sStopped(0), 
		sPlaying(1), 
		sError(2); 

		@SuppressWarnings("unused")	// used in native code
		private int value;
		
		State(int value) { this.value = value; }
		static State fromValue(int i)
		{
			switch (i)
			{
			case 0: return sStopped;
			case 1: return sPlaying;
			case 2: return sError;
			default:
				throw new RuntimeException("Strange value");
			}
		}
	}
	
	public enum ErrorType
	{
		etPortAudioError(0), 
		etNoDevice(1), 
		etInvalidOperationInThisState(2);
		
		@SuppressWarnings("unused")	// used in native code
		private int value;
		
		ErrorType(int value) { this.value = value; }
		static ErrorType fromValue(int i)
		{
			switch (i)
			{
			case 0: return etPortAudioError;
			case 1: return etNoDevice;
			case 2: return etInvalidOperationInThisState;
			default:
				throw new RuntimeException("Strange value");
			}
		}
	}
	
	private long nativeInstance = 0;
	
	private SoundSource soundSource;

	private native static long createNativeInstance(int channels, int rate, int frames_per_buffer) throws Error;
	private native void destroyNativeInstance();

	private native void setNativeSoundSource(SoundSource value);

	
	public PortAudioPlayer(int channels, int rate, int frames_per_buffer) throws Error
	{
		nativeInstance = createNativeInstance(channels, rate, frames_per_buffer);
	}
	
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
	
	public native void play() throws Error;
	public native void stop() throws Error;
	
	public void setSoundSource(SoundSource value)
	{
		setNativeSoundSource(value);
		soundSource = value;
	}
	public SoundSource getSoundSource() { return soundSource; }
}
