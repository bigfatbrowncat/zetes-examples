package vam;

public class MovedSound extends SoundSource
{
	public enum ErrorType
	{
		etSoundNotSet(0);
		
		@SuppressWarnings("unused")	// used in native code
		private int value;
		
		ErrorType(int value) { this.value = value; }
		static ErrorType fromValue(int i)
		{
			switch (i)
			{
			case 0: return etSoundNotSet;
			default:
				throw new RuntimeException("Strange value");
			}
		}
	}
	
	public static class Error extends SoundSource.Error
	{
		private static final long serialVersionUID = -850272977445772333L;
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
	
	
	private SoundSource sound;
	
	private native static long createNativeInstance(int buffer_size);
	
	/**
	 * Frees the native allocated object
	 */
	private native void destroyNativeInstance();
	
	public MovedSound(int bufferSize)
	{
		super(createNativeInstance(bufferSize));
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
	public native void rewind(double position) throws Error;
	@Override
	public native double getPlayhead() throws Error;
	@Override
	public native double getStartTime() throws Error;
	@Override
	public native double getEndTime() throws Error;
	@Override
	public native int getChannels() throws Error;
	@Override
	public native int getRate() throws Error;

	private native void setSoundNative(SoundSource sound) throws Error;

	public void setSound(SoundSource sound) throws Error
	{
		setSoundNative(sound);
		this.sound = sound; 
	}
	
	public native void setDelay(double value);
}
