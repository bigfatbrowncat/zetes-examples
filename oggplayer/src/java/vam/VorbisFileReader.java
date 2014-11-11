package vam;

public class VorbisFileReader extends SoundSource
{
	public enum ErrorType
	{
		etCantOpen(0), 
		etNotAnOgg(1), 
		etCantRead(2),
		etVersionIncorrect(3),
		etBadHeader(4),
		etMemoryFault(5),
		etHole(6),
		etBadLink(7),
		etInvalidArgument(8),
		etStrangeError(9),
		etCantSeek(10);
		
		@SuppressWarnings("unused")	// used in native code
		private int value;
		
		ErrorType(int value) { this.value = value; }
		static ErrorType fromValue(int i)
		{
			switch (i)
			{
			case 0: return etCantOpen;
			case 1: return etNotAnOgg;
			case 2: return etCantRead;
			case 3: return etVersionIncorrect;
			case 4: return etBadHeader;
			case 5: return etMemoryFault;
			case 6: return etHole;
			case 7: return etBadLink;
			case 8: return etInvalidArgument;
			case 9: return etStrangeError;
			case 10: return etCantSeek;
			default:
				throw new RuntimeException("Strange value");
			}
		}
	}
	
	public static class Error extends SoundSource.Error
	{
		private static final long serialVersionUID = 334769002082148556L;
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
	
	// *** All the private fields and methods are implemented in C++ class ***
	

	/**
	 * Calls constructor of the native object. Sets the <code>nativeInstance</code> value
	 * @param file_name Vorbis file name to open
	 * @param buffer_size_request Buffer size to request for reading
	 * @return address to the native object
	 * @throws Error In an exception case
	 */
	private native static long createNativeInstance(String file_name, int buffer_size_request) throws Error;
	
	/**
	 * Frees the native allocated object
	 */
	private native void destroyNativeInstance();
	
	/**
	 * Creates the new <code>VorbisFileReader</code> object, 
	 * opens the selected file and prepares it to read Vorbis sound.
	 * @param file_name Vorbis file name to open
	 * @param buffer_size_request Buffer size to request for reading
	 * @throws Error In an exception case
	 */
	public VorbisFileReader(String file_name, int buffer_size_request) throws Error
	{
		super(createNativeInstance(file_name, buffer_size_request));
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

	public native int getBitsPerSecond() throws Error;
	public native String getVendor() throws Error;
	public native String[] getComments() throws Error;
}
