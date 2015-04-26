package snake;

public class Field implements Cloneable {
	public enum Cell {
		EMPTY,
		HEAD_LEFT,
		HEAD_RIGHT,
		HEAD_UP,
		HEAD_DOWN,
		TAIL_LEFT,
		TAIL_RIGHT,
		TAIL_UP,
		TAIL_DOWN,
		BODY_HORIZONTAL,
		BODY_VERTICAL,
		BODY_LEFT_DOWN,
		BODY_RIGHT_DOWN,
		BODY_LEFT_UP,
		BODY_RIGHT_UP,
		FOOD,
		BRICK;
		
		public boolean isObstacle() {
			switch (this) {
			case HEAD_LEFT:
			case HEAD_RIGHT:
			case HEAD_UP:
			case HEAD_DOWN:
			case BODY_HORIZONTAL:
			case BODY_VERTICAL:
			case BODY_LEFT_DOWN:
			case BODY_RIGHT_DOWN:
			case BODY_LEFT_UP:
			case BODY_RIGHT_UP:
			case BRICK:
				return true;
			default:
				return false;
			}
		}
	}
	
	private int width, height;
	private Cell[] cells;
	
	public Field(int width, int height) {
		this.width = width;
		this.height = height;
		this.cells = new Cell[width * height];
		for (int i = 0; i < width * height; i++) {
			cells[i] = Cell.EMPTY;
		}
	}
	
	protected Field(int width, int height, Cell[] cells) {
		this.width = width;
		this.height = height;
		this.cells = cells.clone();
	}
	
	public void setCell(int x, int y, Cell c) {
		if (x < 0 || x >= width || y < 0 || y >= height) throw new IllegalArgumentException("x or y is out of bounds");
		cells[y * width + x] = c;
	}
	
	public Cell getCell(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height) throw new IllegalArgumentException("x or y is out of bounds");
		return cells[y * width + x];
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	@Override
	protected Field clone() {
		return new Field(width, height, cells);
	}
}
