package vam;

public class ResourcesDeallocatedException extends RuntimeException
{
	private static final long serialVersionUID = -5760496286413720144L;
	public ResourcesDeallocatedException(String message)
	{
		super(message);
	}
}