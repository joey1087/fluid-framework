package com.sponberg.fluid.android;

import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class OnTouchListenerClick implements OnTouchListener {

	public static final int kEventDownTimeForTap = 100; // millis
	public static final int kEventMoveDistanceForTap = 50;
	
	boolean moving = false;
	
	boolean tap = false;
	
	private final String id;
	
	Point touchEventStartedPoint = new Point(0, 0);

	public OnTouchListenerClick(String id) {
		this.id = id;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		final int action = event.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			moving = false;
			tap = false;
			touchEventStartedPoint = new Point((int) event.getX(), (int) event.getY());
			touchDown();
		} else if (action == MotionEvent.ACTION_MOVE) {
			moving = true;
			touchMove();
		} else if (action == MotionEvent.ACTION_UP) {
			touchUp();
			Point end = new Point((int) event.getX(), (int) event.getY());
			if (!moving || 
					event.getDownTime() < kEventDownTimeForTap || 
					getDistance(touchEventStartedPoint, end) < kEventMoveDistanceForTap) {
				tap = true;
			}
		}
				
		if (tap) {
			tap();
			return true;
		} else {
			return true;
		}
	}	
	
	public void touchDown() {}
	public void touchUp() {}
	public void touchMove() {}
	public void tap() {}
	
	public static double getDistance(Point p1, Point p2) {
		return Math.sqrt(Math.pow(p1.x-p2.x, 2) + Math.pow(p1.y-p2.y, 2));
	}

}
