package snake;

import java.util.HashMap;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;

import snake.Field.Cell;

public class FieldView extends Canvas {

	private Image boardImage = new Image(getDisplay(), this.getClass().getResourceAsStream("board.png"));
	private Image grassImage = new Image(getDisplay(), this.getClass().getResourceAsStream("grass.png"));

	private Image bgImage = new Image(getDisplay(), this.getClass().getResourceAsStream("bg.png"));
	private Image boomImage = new Image(getDisplay(), this.getClass().getResourceAsStream("boom.png"));
	private Point boomPosition;
	
	@SuppressWarnings("unchecked")
	private HashMap<Field.Cell, Image>[] images = (HashMap<Field.Cell,Image>[]) new HashMap[2];
	
	private Field field;
	
	private int frame = 0;
	private PaintListener listener = new PaintListener() {
		
		@Override
		public void paintControl(PaintEvent e) {
			int d = 2;
			GC gc = e.gc;
			
			//Font fnt = new Fon

			// Drawing grass
			int centerX = getSize().x / 2, centerY = getSize().y / 2;
			int grassWidth = 550, grassHeight = 550;
			for (int i = centerX; i < getSize().x; i += grassWidth / d) {
				for (int j = centerY; j < getSize().y; j += grassHeight / d) {
					gc.drawImage(grassImage, 0, 0, grassWidth, grassHeight, i, j, grassWidth / d, grassHeight / d);
				}
			}
			for (int i = centerX - grassWidth / d; i >= -grassWidth / d; i -= grassWidth / d) {
				for (int j = centerY; j < getSize().y; j += grassHeight / d) {
					gc.drawImage(grassImage, 0, 0, grassWidth, grassHeight, i, j, grassWidth / d, grassHeight / d);
				}
			}
			for (int i = centerX; i < getSize().x; i += grassWidth / d) {
				for (int j = centerY - grassHeight / d; j >= -grassHeight / d; j -= grassHeight / d) {
					gc.drawImage(grassImage, 0, 0, grassWidth, grassHeight, i, j, grassWidth / d, grassHeight / d);
				}
			}

			for (int i = centerX - grassWidth / d; i >= -grassWidth / d; i -= grassWidth / d) {
				for (int j = centerY - grassHeight / d; j >= -grassHeight / d; j -= grassHeight / d) {
					gc.drawImage(grassImage, 0, 0, grassWidth, grassHeight, i, j, grassWidth / d, grassHeight / d);
				}
			}

			
			int boardWidth = 440, boardHeight = 848;
			int boardDelta = 10;
			int imw = 60, imh = 60;
			
			if (field != null) {
				int fieldX0 = getSize().x / 2 - field.getWidth() * imw / d / 2 + (boardWidth / d + boardDelta / d) / 2;
				int fieldY0 = getSize().y / 2 - field.getHeight() * imh / d / 2;
				for (int i = 0; i < field.getWidth(); i++) {
					for (int j = 0; j < field.getHeight(); j++) {
						gc.drawImage(bgImage, 0, 0, imw, imh, imw * i / d + fieldX0, imh * j / d + fieldY0, imw / d, imh / d);
						if (field.getCell(i, j) != Cell.EMPTY) {
							Image img = images[frame].get(field.getCell(i, j));
							gc.drawImage(img, 0, 0, imw, imh, imw * i / d + fieldX0, imh * j / d + fieldY0, imw / d, imh / d); 
						}
					}
				}
				if (boomPosition != null) {
					int bw = 140, bh = 140;
					gc.drawImage(boomImage, 0, 0, bw, bh, 
							(int)(imw * (boomPosition.x + 0.5) / d + fieldX0 - bw / d / 2), 
							(int)(imh * (boomPosition.y + 0.5) / d + fieldY0 - bh / d / 2), 
					bw / d, bh / d);
				}
				gc.drawImage(boardImage, 0, 0, boardWidth, boardHeight, fieldX0 - (boardWidth + boardDelta) / 2, fieldY0, boardWidth / d, boardHeight / d);
			}
			
			
		}
	};
	
	public FieldView(Composite arg0, int arg1) {
		super(arg0, arg1 | SWT.DOUBLE_BUFFERED);

		for (int frame = 1; frame <= 2; frame++) {
			images[frame - 1] = new HashMap<Field.Cell, Image>();
			images[frame - 1].put(Field.Cell.EMPTY, null);
			
			images[frame - 1].put(Field.Cell.HEAD_LEFT, new Image(getDisplay(), this.getClass().getResourceAsStream("h-l-" + frame + ".png")));
			images[frame - 1].put(Field.Cell.HEAD_RIGHT, new Image(getDisplay(), this.getClass().getResourceAsStream("h-r-" + frame + ".png")));
			images[frame - 1].put(Field.Cell.HEAD_UP, new Image(getDisplay(), this.getClass().getResourceAsStream("h-u-" + frame + ".png")));
			images[frame - 1].put(Field.Cell.HEAD_DOWN, new Image(getDisplay(), this.getClass().getResourceAsStream("h-d-" + frame + ".png")));
			
			images[frame - 1].put(Field.Cell.TAIL_LEFT, new Image(getDisplay(), this.getClass().getResourceAsStream("t-l-" + frame + ".png")));
			images[frame - 1].put(Field.Cell.TAIL_RIGHT, new Image(getDisplay(), this.getClass().getResourceAsStream("t-r-" + frame + ".png")));
			images[frame - 1].put(Field.Cell.TAIL_UP, new Image(getDisplay(), this.getClass().getResourceAsStream("t-u-" + frame + ".png")));
			images[frame - 1].put(Field.Cell.TAIL_DOWN, new Image(getDisplay(), this.getClass().getResourceAsStream("t-d-" + frame + ".png")));
	
			images[frame - 1].put(Field.Cell.BODY_HORIZONTAL, new Image(getDisplay(), this.getClass().getResourceAsStream("sn-h-" + frame + ".png")));
			images[frame - 1].put(Field.Cell.BODY_VERTICAL, new Image(getDisplay(), this.getClass().getResourceAsStream("sn-v-" + frame + ".png")));
			
			images[frame - 1].put(Field.Cell.BODY_LEFT_DOWN, new Image(getDisplay(), this.getClass().getResourceAsStream("sn-ld-" + frame + ".png")));
			images[frame - 1].put(Field.Cell.BODY_RIGHT_DOWN, new Image(getDisplay(), this.getClass().getResourceAsStream("sn-rd-" + frame + ".png")));
			images[frame - 1].put(Field.Cell.BODY_LEFT_UP, new Image(getDisplay(), this.getClass().getResourceAsStream("sn-lu-" + frame + ".png")));
			images[frame - 1].put(Field.Cell.BODY_RIGHT_UP, new Image(getDisplay(), this.getClass().getResourceAsStream("sn-ru-" + frame + ".png")));

			images[frame - 1].put(Field.Cell.FOOD, new Image(getDisplay(), this.getClass().getResourceAsStream("food-" + frame + ".png")));
			images[frame - 1].put(Field.Cell.BRICK, new Image(getDisplay(), this.getClass().getResourceAsStream("brick.png")));
		}
		addPaintListener(listener);
	}
	
	public void setField(Field field) {
		this.field = field.clone();
		redraw();
	}
	
	public void setFrame(int frame) {
		this.frame = frame % 2;
		redraw();
	}
	
	public void setBoomPosition(Point boomPosition) {
		this.boomPosition = boomPosition;
		redraw();
	}
	
}
