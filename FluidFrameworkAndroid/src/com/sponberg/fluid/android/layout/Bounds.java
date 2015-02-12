package com.sponberg.fluid.android.layout;

public class Bounds {
	
	int x, y, width, height;
	
	public Bounds(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public Bounds(Bounds bounds) {
		this.x = bounds.x;
		this.y = bounds.y;
		this.width = bounds.width;
		this.height = bounds.height;
	}
	
	public void setTo(Bounds bounds) {
		setTo(bounds.x, bounds.y, bounds.width, bounds.height);
	}
	
	public void setTo(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public boolean equals(Object o) {
		
		if (!(o instanceof Bounds))
			return false;
		
		Bounds b2 = (Bounds) o;
		
		return this.x == b2.x &&
				this.y == b2.y &&
				this.width == b2.width &&
				this.height == b2.height;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
}