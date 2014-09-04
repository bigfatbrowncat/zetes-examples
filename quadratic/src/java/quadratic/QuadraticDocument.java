package quadratic;

import java.io.IOException;

import zetes.wings.abstracts.Document;

public class QuadraticDocument implements Document
{
	public enum State { unsolved, validRoots, unsolvable, tooManyRoots } 
	
	/* User data (DON'T MODIFY - ACCESSED FROM NATIVE CODE) */
	private double a, b, c;
	
	/* Calculation results (DON'T MODIFY - ACCESSED FROM NATIVE CODE) */
	private double x1, x2;
	private State state = State.unsolved;
	
	public QuadraticDocument() {
	}

	public QuadraticDocument(String fileName) throws IOException
	{
	}
	
	@Override
	public void dispose() {
	}

	@Override
	public String getTitle() {
		return null;
	}
	
	public void setA(double a) {
		this.a = a;
		state = State.unsolved;
	}
	
	public void setB(double b) {
		this.b = b;
		state = State.unsolved;
	}
	
	public void setC(double c) {
		this.c = c;
		state = State.unsolved;
	}
	
	public double getA() {
		return a;
	}
	
	public double getB() {
		return b;
	}
	
	public double getC() {
		return c;
	}
	
	public double getX1() {
		return x1;
	}
	
	public double getX2() {
		return x2;
	}
	
	public State getState() {
		return state;
	}
	
	// Implemented in FlyerDocument.cpp
	public native void solve();
	
	/*public void solve() {
		if (a == 0.0) {
			if (b != 0.0) {
				x1 = -c / b;
				x2 = -c / b;
				state = State.validRoots; 
			} else {
				if (c == 0) {
					state = State.tooManyRoots;
				} else {
					state = State.unsolvable;
				}
			}
		} else if (c == 0.0) {
			x1 = 0.0;
			x2 = -b / a;
			state = State.validRoots; 
		} else { 
			double D = b * b - 4 * a * c;
			if (D >= 0.0) {
				x1 = (-b + Math.sqrt(D)) / (4 * a * c); 
				x2 = (-b - Math.sqrt(D)) / (4 * a * c);
				state = State.validRoots;
			} else {
				state = State.unsolvable;
			}
		}
	}*/
}
