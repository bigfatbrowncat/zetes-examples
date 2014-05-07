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
	private int TERMINAL_WIDTH = 100;
	private int TERMINAL_HEIGHT = 40;
	
	private char CHAR_NEWLINE = '\n';
	private char CHAR_SPACE = ' ';
	private char CHAR_TAB = '\t';
	
	private int TAB_LENGTH = 4;
	
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
	private int cursorFrame = 0;

	private Object screenLock = new Object();
	private Point cursorPosition = new Point(0, 0);
	private int foreColor = 0xFFAAAAAA;
	private int backColor = 0xFF000000;
	private char[] screenTextBuffer = new char[TERMINAL_WIDTH * TERMINAL_HEIGHT];
	private int[] screenForeColorBuffer = new int[TERMINAL_WIDTH * TERMINAL_HEIGHT];
	private int[] screenBackColorBuffer = new int[TERMINAL_WIDTH * TERMINAL_HEIGHT];
	
	public void setForeColor(int foreColor) {
		this.foreColor = foreColor;
	}
	
	public void setBackColor(int backColor) {
		this.backColor = backColor;
	}
	
	public OldLandTerminalWindow()
	{
		super();
		
	}
	
	private void rollBuffersLineUp() {
		for (int j = 1; j < TERMINAL_HEIGHT; j++) {
			for (int i = 0; i < TERMINAL_WIDTH; i++) {
				screenTextBuffer[(j - 1) * TERMINAL_WIDTH + i] = screenTextBuffer[j * TERMINAL_WIDTH + i];
				screenForeColorBuffer[(j - 1) * TERMINAL_WIDTH + i] = screenForeColorBuffer[j * TERMINAL_WIDTH + i];
				screenBackColorBuffer[(j - 1) * TERMINAL_WIDTH + i] = screenBackColorBuffer[j * TERMINAL_WIDTH + i];
			}
		}
		for (int i = 0; i < TERMINAL_WIDTH; i++) {
			screenTextBuffer[(TERMINAL_HEIGHT - 1) * TERMINAL_WIDTH + i] = CHAR_SPACE;
			screenForeColorBuffer[(TERMINAL_HEIGHT - 1) * TERMINAL_WIDTH + i] = foreColor;
			screenBackColorBuffer[(TERMINAL_HEIGHT - 1) * TERMINAL_WIDTH + i] = backColor;
		}
	}
	
	private void invertCursor() {
		int pos = cursorPosition.y * TERMINAL_WIDTH + cursorPosition.x;
		int fc = screenForeColorBuffer[pos];
		int bc = screenBackColorBuffer[pos];
		screenForeColorBuffer[pos] = bc;
		screenBackColorBuffer[pos] = fc;
	}
	
	private void printTabulation() {
		for (int i = 0; i < TAB_LENGTH; i++) {
			print(" ");
		}
	}
	
	public void clearScreen() {
		synchronized (screenLock) {
			for (int j = 0; j < TERMINAL_HEIGHT; j++) {
				for (int i = 0; i < TERMINAL_WIDTH; i++) {
					screenTextBuffer[j * TERMINAL_WIDTH + i] = CHAR_SPACE;
					screenForeColorBuffer[j * TERMINAL_WIDTH + i] = foreColor;
					screenBackColorBuffer[j * TERMINAL_WIDTH + i] = backColor;
				}
			}
			cursorPosition = new Point(0, 0);
		}
	}
	
	public void print(String text) {
		if (text == null) throw new IllegalArgumentException("text shouldn't be null");

		synchronized (screenLock) {
			int x = cursorPosition.x, y = cursorPosition.y;
			for (int i = 0; i < text.length(); i++) {
				if (text.charAt(i) == CHAR_TAB) {
					printTabulation();
				} else if (text.charAt(i) != CHAR_NEWLINE) {
					screenTextBuffer[y * TERMINAL_WIDTH + x] = text.charAt(i);
					screenForeColorBuffer[y * TERMINAL_WIDTH + x] = foreColor;
					screenBackColorBuffer[y * TERMINAL_WIDTH + x] = backColor;
					
					x++;
					if (x >= TERMINAL_WIDTH) {
						x = 0; y++;
					}
				} else {
					x = 0;
					y++;
				}
				
				if (y >= TERMINAL_HEIGHT) {
					y--;
					rollBuffersLineUp();
				}
			}
	
			cursorPosition = new Point(x, y);
		}
	}
	
	public void println(String text) {
		print(text);
		print(Character.toString(CHAR_NEWLINE));
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
		
		shell.setSize(size.x - clientSize.x + TERMINAL_WIDTH * 10 + 2 * frame, size.y - clientSize.y + TERMINAL_HEIGHT * 19 + 2 * frame);
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
		createTerminal(100, 100, frame, TERMINAL_WIDTH, TERMINAL_HEIGHT, codepage, 70, 8);
		
		clearScreen();
		
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

				synchronized (screenLock) {
					cursorFrame = (cursorFrame + 1) % 4; 
					
					// Inverting the cursor
					if (cursorFrame > 1) {
						invertCursor();
					}
					
					setScreenBuffer(screenTextBuffer);
					setForegroundColors(screenForeColorBuffer);
					setBackgroundColors(screenBackColorBuffer);
	
					// Inverting the cursor back
					if (cursorFrame > 1) {
						invertCursor();
					}
				}
				
				drawScene(time);
				canvas.swapBuffers();
			}
		});

		// Printing William Shakespeare's Romeo And Juliet play
		new RomeoAndJulietPrinter(this).start();
		
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
