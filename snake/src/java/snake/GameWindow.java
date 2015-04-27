package snake;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;

import snake.Field.Cell;
import zetes.wings.NullDocument;
import zetes.wings.base.ViewWindowBase;

public class GameWindow extends ViewWindowBase<NullDocument>{

	private final int fps = 10;
	private int frame = 0;
	
	private Controller controller;
	private FieldView snakeFieldView;
	
	private Thread gameThread = new Thread(new Runnable() {
		
		@Override
		public void run() {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			while (!getShell().isDisposed()) {
				long currentTime = System.currentTimeMillis();
				
				boolean gameOver = false;
				if (frame == 0) {
					gameOver = !controller.doStep();
				}
				getShell().getDisplay().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						if (!snakeFieldView.isDisposed()) {
							snakeFieldView.setFrame(frame);
							snakeFieldView.setBoomPosition(controller.getBoomPosition());
							snakeFieldView.setField(controller.getField());
						}
					}
				});
				
				if (gameOver) return;

				frame = (frame + 1) % 2;

				long currentTime2 = System.currentTimeMillis();
				try {
					Thread.sleep(Math.max(0, 1000 / fps - (currentTime2 - currentTime)));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	});
	
	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	protected Shell constructShell() {
		Shell shell = new Shell();
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN));
		shell.setSize(600, 600);
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		/*Field fd = new Field(3, 3);
		fd.setCell(0, 0, Cell.BODY_RIGHT_DOWN);
		fd.setCell(1, 0, Cell.BODY_HORIZONTAL);
		fd.setCell(2, 0, Cell.BODY_LEFT_DOWN);
		fd.setCell(2, 1, Cell.TAIL_UP);
		fd.setCell(0, 1, Cell.BODY_VERTICAL);
		fd.setCell(0, 2, Cell.BODY_RIGHT_UP);
		fd.setCell(1, 2, Cell.BODY_LEFT_UP);
		fd.setCell(1, 1, Cell.HEAD_DOWN);*/
		
		int s = 15;
		Field initialField = new Field(s, s);
		
		// Drawing walls
		for (int i = 0; i < s / 3; i++) {
			initialField.setCell(0, i, Cell.BRICK);
			initialField.setCell(i, 0, Cell.BRICK);

			initialField.setCell(0, s - 1 - i, Cell.BRICK);
			initialField.setCell(s - 1 - i, 0, Cell.BRICK);

			initialField.setCell(s - 1, i, Cell.BRICK);
			initialField.setCell(i, s - 1, Cell.BRICK);
			
			initialField.setCell(s - 1, s - 1 - i, Cell.BRICK);
			initialField.setCell(s - 1 - i, s - 1, Cell.BRICK);
		}
		
		
		int snakeLength = 5;
		controller = new Controller(snakeLength, initialField);
		
		snakeFieldView = new FieldView(shell, SWT.NONE);
		snakeFieldView.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN));
		controller.doStep();
		snakeFieldView.setField(controller.getField());
		snakeFieldView.setFrame(0);
		snakeFieldView.setLayout(new RowLayout(SWT.HORIZONTAL));
		//composite.setLayoutData(new RowData(100, 100));

		gameThread.start();
		
		shell.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent arg0) { }
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.keyCode == SWT.ARROW_RIGHT) {
					controller.moveRight();
				} else if (arg0.keyCode == SWT.ARROW_LEFT) {
					controller.moveLeft();
				} else if (arg0.keyCode == SWT.ARROW_UP) {
					controller.moveUp();
				} else if (arg0.keyCode == SWT.ARROW_DOWN) {
					controller.moveDown();
				}
			}
		});
		return shell;
	}

	@Override
	public boolean supportsFullscreen() {
		return true;
	}

	@Override
	public boolean supportsMaximizing() {
		return true;
	}
}
