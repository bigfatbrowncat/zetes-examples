package snake;

import java.util.Random;

import snake.Field.Cell;

public class Food {
	private static Random random = new Random();

	private int x, y;
	
	protected Food(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	public static Food generate(Field field) {
		int x, y;
		do {
			x = random.nextInt(field.getWidth()); 
			y = random.nextInt(field.getHeight());
		} while (field.getCell(x, y) != Cell.EMPTY);
		return new Food(x, y);
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
}
