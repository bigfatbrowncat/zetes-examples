package oldland;

import java.util.Date;
import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;

import org.eclipse.swt.opengl.CrossBaseGLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import zetes.NullDocument;
import zetes.ui.ViewWindowBase;

public class OldLandTerminalWindow extends ViewWindowBase<NullDocument>
{
	public class Point {
		public final int x, y;
		public Point(int x, int y) { this.x = x; this.y = y; }
	}
	
	private static native boolean globalInit();
	private static native boolean createTerminal(int width, int height, int frame, int xLength, int yLength, String codepageSymbols, int codepageWidth, int codepageHeight);
	private static native boolean destroyTerminal();
	private static native boolean resizeView(int width, int height);
	private static native boolean drawScene(double angle);
	private static native void setScreenBuffer(char[] symbols);
	
	private static native void setForegroundColors(int[] forecolors);
	private static native void setBackgroundColors(int[] backcolors);
	
	private double time = 0;
	private CrossBaseGLCanvas canvas;
	private Date lastFrameMoment = new Date();
	private float framesPerSecond = 30;
	
	public OldLandTerminalWindow()
	{
		super();
		
	}
	
	protected Point cursorPos = new Point(0, 0);
	
	public void print(String text) {
		for (int i = 0; i < text.length(); i++) {
			
		}
	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
	protected Shell constructShell()
	{
		Shell shell = new Shell(SWT.CLOSE | SWT.TITLE | SWT.MIN);
		
		org.eclipse.swt.graphics.Point size = shell.getSize();
		org.eclipse.swt.graphics.Point clientSize = new org.eclipse.swt.graphics.Point(shell.getClientArea().width, shell.getClientArea().height);
		
		final int frame = 5;
		
		shell.setSize(size.x - clientSize.x + 80 * 10 + 2 * frame, size.y - clientSize.y + 30 * 19 + 2 * frame);
		shell.setImages(new Image[] { 
				SWTResourceManager.getImage(OldLandTerminalWindow.class, "/gltest/wingcube16.png"),
				SWTResourceManager.getImage(OldLandTerminalWindow.class, "/gltest/wingcube64.png")
		});

		shell.setLayout(new FillLayout());
		Composite comp = new Composite(shell, SWT.NO_BACKGROUND);
		comp.setLayout(new FillLayout());
		GLData data = new GLData ();
		data.doubleBuffer = true;
		data.depthSize = 1;
		data.samples = 1;
		data.sampleBuffers = 1;
		
		canvas = new CrossBaseGLCanvas(comp, SWT.NO_BACKGROUND, data);
		
		if (!canvas.isCurrent()) canvas.setCurrent();
		globalInit();
		
		String codepage = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz                  " +
                          "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя    " +
                          "1234567890-=!@#$%^&*()_+[]{},.<>/?\\|`~\"':;                            ";
		createTerminal(100, 100, frame, 80, 30, codepage, 70, 8);
		
		char[] buf = new char[80 * 30];
		int c = 0;
		for (int i = 0; i < 80 * 30; i++) {
			c = (c + 1) % 10;
			buf[i] = (char) (c + '0');
		}
		
		setScreenBuffer(buf);

		int[] fg = new int[80 * 30];
		int[] bg = new int[80 * 30];
		Random rnd = new Random();
		for (int i = 0; i < 80; i++)
			for (int j = 0; j < 30; j++)
			{
				fg[j * 80 + i] = rnd.nextInt();
				bg[j * 80 + i] = rnd.nextInt();
			}

		setForegroundColors(fg );
		setBackgroundColors(bg );
		
		canvas.addControlListener(new ControlListener()
		{
			
			@Override
			public void controlResized(ControlEvent arg0)
			{
				if (!canvas.isCurrent()) canvas.setCurrent();
				org.eclipse.swt.graphics.Point size = canvas.getSize();
				resizeView(size.x, size.y);
			}
			
			@Override
			public void controlMoved(ControlEvent arg0)
			{
			}
		});
		
		canvas.addPaintListener(new PaintListener()
		{
			@Override
			public void paintControl(PaintEvent arg0)
			{
				if (!canvas.isCurrent()) canvas.setCurrent();
				drawScene(time);
				canvas.swapBuffers();
			}
		});

		return shell;
	}
	
	public void updateFrame()
	{
		Date currentMoment;
		double deltaTimeSec;
		
		do
		{
			currentMoment = new Date();
			deltaTimeSec = currentMoment.getTime() - lastFrameMoment.getTime();
			try {
				Thread.sleep(Math.max(10, (long) (1000 / framesPerSecond)));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		while (deltaTimeSec < 1.0 / framesPerSecond);
	
			
		{
			time += deltaTimeSec;
			if (canvas != null && !canvas.isDisposed())
			{
				canvas.redraw();
			}
			lastFrameMoment = currentMoment;
		}
	}
	
	@Override
	public boolean supportsFullscreen()
	{
		return false;
	}
	
	@Override
	public boolean supportsMaximizing()
	{
		return false;
	}
}
