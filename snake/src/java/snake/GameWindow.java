package snake;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;

import snake.Field.Cell;
import zetes.wings.NullDocument;
import zetes.wings.actions.Handler;
import zetes.wings.base.ViewWindowBase;

public class GameWindow extends ViewWindowBase<NullDocument>{

	private int frame = 0;
	private int initialSnakeLength = 5;

	private GameController controller;
	private Field initialField;
	private FieldView snakeFieldView;
	
	private boolean gameOver = false;
	
	void frame() {
		if (!getShell().isDisposed() ) {
			if (!gameOver) {
				if (frame == 0) {
					gameOver = !controller.doStep();
				}

				frame = (frame + 1) % 2;
				snakeFieldView.setFrame(frame);
			}
			snakeFieldView.setBoomPosition(controller.getBoomPosition());
			snakeFieldView.setField(controller.getField());
		}
	}

	
	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	protected Shell constructShell() {
		Shell shell = new Shell();
		shell.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		shell.setSize(690, 490);
		shell.setMinimumSize(new Point(690, 490));
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
		initialField = new Field(s, s);
		
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
		
		
		controller = new GameController(initialSnakeLength, initialField);
		
		snakeFieldView = new FieldView(shell, SWT.NONE);
		snakeFieldView.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		controller.doStep();
		snakeFieldView.setField(controller.getField());
		snakeFieldView.setFrame(0);
		snakeFieldView.setLayout(new RowLayout(SWT.HORIZONTAL));
		//composite.setLayoutData(new RowData(100, 100));

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

	private Handler<GameWindow> newGameHandler = new Handler<GameWindow>() {
		
		@Override
		public void execute(GameWindow window) {
			controller = new GameController(initialSnakeLength, initialField);
			gameOver = false;
		}
	};

	public Handler<GameWindow> getNewGameHandler() {
		return newGameHandler;
	}
}
