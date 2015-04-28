package snake;

import java.util.Random;

import org.eclipse.swt.graphics.Point;

import snake.Field.Cell;
import snake.Snake.Direction;

public class Controller {
	private static Random random = new Random();
	
	private Field initialField, field;
	private Snake snake;
	private Direction thisStepDirection = Direction.RIGHT;
	private Direction nextStepDirection = Direction.RIGHT;
	
	private Food food;
	
	private Point boomPosition;
	
	public Controller(int snakeLength, Field initialField) {
		this.initialField = initialField.clone();
		field = initialField.clone();
		
		int width = initialField.getWidth();
		int height = initialField.getHeight();
		
		this.snake = new Snake(width / 2 - snakeLength / 2, height / 2);
		for (int i = 0; i < snakeLength; i++) {
			snake.move(Direction.RIGHT, true);
		}
		
		updateField();
		generateFood();
	}
	
	private int torusIncX(int x) {
		while (x < 0) {
			x += initialField.getWidth();
		}
		return (x + 1) % initialField.getWidth();
	}
	private int torusIncY(int y) {
		while (y < 0) {
			y += initialField.getHeight();
		}
		return (y + 1) % initialField.getHeight();
	}
	private int torusDecX(int x) {
		x = (x - 1) % initialField.getWidth();
		while (x < 0) {
			x += initialField.getWidth();
		}
		return x;
	}
	private int torusDecY(int y) {
		y = (y - 1) % initialField.getHeight();
		while (y < 0) {
			y += initialField.getHeight();
		}
		return y;
	}
	private int torusX(int x) {
		while (x < 0) {
			x += initialField.getWidth();
		}
		return x % initialField.getWidth();
	}
	private int torusY(int y) {
		while (y < 0) {
			y += initialField.getHeight();
		}
		return y % initialField.getHeight();
	}
	
	private void updateField() {
		field = initialField.clone();
		
		// Setting snake body
		int x = torusX(snake.getHeadX()), y = torusY(snake.getHeadY());
		Direction[] dir = snake.getDirections(); 
		switch (dir[0]) {
		case LEFT:
			field.setCell(x, y, Cell.HEAD_LEFT);
			x = torusDecX(x);
			break;
		case RIGHT:
			field.setCell(x, y, Cell.HEAD_RIGHT);
			x = torusIncX(x);
			break;
		case UP:
			field.setCell(x, y, Cell.HEAD_UP);
			y = torusDecY(y);
			break;
		case DOWN:
			field.setCell(x, y, Cell.HEAD_DOWN);
			y = torusIncY(y);
			break;
		}
		
		
		for (int i = 1; i < dir.length; i++) {
			if (dir[i] == Direction.LEFT) {
				switch (dir[i - 1]) {
				case LEFT:
					field.setCell(x, y, Cell.BODY_HORIZONTAL);
					break;
				case UP:
					field.setCell(x, y, Cell.BODY_LEFT_DOWN);
					break;
				case DOWN:
					field.setCell(x, y, Cell.BODY_LEFT_UP);
					break;
				default:
					throw new RuntimeException("Invalid snake data");
				}
				x = torusDecX(x);
			} else if (dir[i] == Direction.RIGHT) {
				switch (dir[i - 1]) {
				case RIGHT:
					field.setCell(x, y, Cell.BODY_HORIZONTAL);
					break;
				case UP:
					field.setCell(x, y, Cell.BODY_RIGHT_DOWN);
					break;
				case DOWN:
					field.setCell(x, y, Cell.BODY_RIGHT_UP);
					break;
				default:
					throw new RuntimeException("Invalid snake data");
				}
				x = torusIncX(x);
			} else if (dir[i] == Direction.UP) {
				switch (dir[i - 1]) {
				case UP:
					field.setCell(x, y, Cell.BODY_VERTICAL);
					break;
				case LEFT:
					field.setCell(x, y, Cell.BODY_RIGHT_UP);
					break;
				case RIGHT:
					field.setCell(x, y, Cell.BODY_LEFT_UP);
					break;
				default:
					throw new RuntimeException("Invalid snake data");
				}
				y = torusDecY(y);
			} else if (dir[i] == Direction.DOWN) {
				switch (dir[i - 1]) {
				case DOWN:
					field.setCell(x, y, Cell.BODY_VERTICAL);
					break;
				case LEFT:
					field.setCell(x, y, Cell.BODY_RIGHT_DOWN);
					break;
				case RIGHT:
					field.setCell(x, y, Cell.BODY_LEFT_DOWN);
					break;
				default:
					throw new RuntimeException("Invalid snake data");
				}
				y = torusIncY(y);
			} 
		}
		
		int i = dir.length - 1; // Tail
		switch (dir[i]) {
		case LEFT:
			field.setCell(x, y, Cell.TAIL_RIGHT);
			break;
		case RIGHT:
			field.setCell(x, y, Cell.TAIL_LEFT);
			break;
		case UP:
			field.setCell(x, y, Cell.TAIL_DOWN);
			break;
		case DOWN:
			field.setCell(x, y, Cell.TAIL_UP);
			break;
		}
		
		if (food != null) {
			placeFood();
		}
	}
	
	private void generateFood() {
		int x, y;
		do {
			x = random.nextInt(field.getWidth()); 
			y = random.nextInt(field.getHeight());
		} while (field.getCell(x, y) != Cell.EMPTY);
		food = new Food(x, y);
		placeFood();
	}
	
	private void placeFood() {
		field.setCell(food.getX(), food.getY(), Cell.FOOD);
	}
	
	/**
	 * @return <code>false</code> if the game is over
	 */
	public boolean doStep() {
		boolean grow = false;
		int newX, newY;
		
		switch (nextStepDirection) {
		case RIGHT:
			newX = torusIncX(snake.getHeadX()); newY = torusY(snake.getHeadY());
			break;
		case LEFT:
			newX = torusDecX(snake.getHeadX()); newY = torusY(snake.getHeadY());
			break;
		case UP:
			newY = torusDecY(snake.getHeadY()); newX = torusX(snake.getHeadX());
			break;
		case DOWN:
			newY = torusIncY(snake.getHeadY()); newX = torusX(snake.getHeadX());
			break;
		default:
			throw new RuntimeException("Impossible case");
		}
		
		boolean setNewFood = false;
		
		if (newX == food.getX() && newY == food.getY()) {
			grow = true;
			setNewFood = true;
			food = null;
		}
		
		boolean gameOver = field.getCell(newX, newY).isObstacle();
		if (gameOver) {
			boomPosition = new Point(newX, newY);
		}
		
		snake.move(nextStepDirection, grow);

		updateField();
		if (setNewFood) {
			generateFood();
			setNewFood = false;
		}
		
		thisStepDirection = nextStepDirection;
		
		return !gameOver;
	}
	
	public void moveRight() {
		if (thisStepDirection != Direction.LEFT)
			nextStepDirection = Direction.RIGHT;
	}
	public void moveLeft() {
		if (thisStepDirection != Direction.RIGHT)
			nextStepDirection = Direction.LEFT;
	}
	public void moveUp() {
		if (thisStepDirection != Direction.DOWN)
			nextStepDirection = Direction.UP;
	}
	public void moveDown() {
		if (thisStepDirection != Direction.UP)
			nextStepDirection = Direction.DOWN;
	}
	public Field getField() {
		return field;
	}
	
	public Point getBoomPosition() {
		return boomPosition;
	}
}
