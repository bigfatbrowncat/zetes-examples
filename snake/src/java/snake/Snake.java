package snake;

import java.util.LinkedList;
import java.util.List;

public class Snake {
	public enum Direction {
		RIGHT, LEFT, UP, DOWN;
		
		public Direction opposite() {
			switch (this) {
			case RIGHT: return LEFT;
			case LEFT: return RIGHT;
			case UP: return DOWN;
			case DOWN: return UP;
			default: throw new RuntimeException("Impossible value");
			}
		}
	}
	
	private int headX, headY;
	
	/**
	 * Directions from head to tail
	 */
	private List<Direction> directions;
	
	public Snake(int headX, int headY) {
		this.headX = headX;
		this.headY = headY;
		directions = new LinkedList<Snake.Direction>();
	}
	
	public void move(Direction dir, boolean grow) {
		if (directions.size() > 0 && directions.get(0) == dir) throw new RuntimeException("Can't move in that direction");
		switch (dir) {
		case RIGHT:	headX++; break;
		case LEFT:	headX--; break;
		case UP:	headY--; break;
		case DOWN:	headY++; break;
		}
		directions.add(0, dir.opposite());
		if (!grow) directions.remove(directions.size() - 1);
	}
	
	public Direction[] getDirections() {
		return directions.toArray(new Direction[] {});
	}
	public int getHeadX() {
		return headX;
	}
	public int getHeadY() {
		return headY;
	}
}
