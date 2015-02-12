package com.sponberg.app.manager;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import lombok.Getter;
import lombok.Setter;

import com.eclipsesource.json.JsonObject;
import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.util.JsonUtil;

@Getter
@Setter
public class MeasureManager {

	int numPointsToCollect = 50;
	
	int durationInSeconds = 4; 
	
	int initialDelayMillis = 500;
	
	int measureProgress = 0;
	
	Double mean = null;
	
	Double actual = null;
	
	ArrayList<Point> list1 = new ArrayList<Point>();
	
	double x = 0;
	
	final double xStep = 0.15;
	
	double total = 0;
	
	ScheduledExecutorService service = null;
	
	ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
	
	WriteLock writeLock = lock.writeLock();
	
	ReadLock readLock = lock.readLock();
	
	public void startMeasurement() {
		
		if (service != null) {
			service.shutdown();
		}
		service = Executors.newSingleThreadScheduledExecutor();
		
		list1 = new ArrayList<Point>();
		x = 0;
		total = 0;
		mean = null;
		actual = null;
		
		measureProgress = 0;
		
		GlobalState.fluidApp.getDataModelManager().dataDidChange("app.measureManager");
		
		Runnable r = new Runnable() {
			@Override
			public void run() {
				actual = getCurrentValue();

				measureProgress = (int) (getNumPoints() * 1.0 / numPointsToCollect * 100);

				writeLock.lock();
				list1.add(new Point(x, actual));
				writeLock.unlock();
				
				if (measureProgress == 100) {
					service.shutdown();
				}

				x += xStep;
				total += actual;

				if (getNumPoints() >= numPointsToCollect / 3) {
					mean = total / getNumPoints();
				}

				GlobalState.fluidApp.getSystemService().runOnUiThread(new Runnable() {
					@Override
					public void run() {						
						GlobalState.fluidApp.getDataModelManager().dataDidChange("app.measureManager", "actual");
						GlobalState.fluidApp.getDataModelManager().dataDidChange("app.measureManager", "mean");					
						GlobalState.fluidApp.getDataModelManager().dataDidChange("app.measureManager", "measureProgress");
						GlobalState.fluidApp.getDataModelManager().dataDidChange("app.measureManager", "graph");				
					}
				});
			}
		};
		
		int period = durationInSeconds * 1000 / numPointsToCollect;
		
		service.scheduleAtFixedRate(r, initialDelayMillis, period, TimeUnit.MILLISECONDS);
	}
	
	private double getCurrentValue() {
		return 7 + Math.sin(x);
	}
	
	private int getNumPoints() {
		readLock.lock();
		try {
			return list1.size();
		} finally {
			readLock.unlock();
		}
	}
	
	public Double getMean() {
		return mean;
	}
	
	public Double getActual() {
		return actual;
	}
	
	public Integer getMeasureProgress() {
		return measureProgress;
	}
	
	public String getGraphData() {
		
		try {
			
			readLock.lock();
			
			ArrayList<Point> list2 = new ArrayList<Point>();
			if (mean != null) {
				list2.add(new Point(0, mean));
				list2.add(new Point(list1.get(getNumPoints() - 1).x, mean));
			}

			double xMax = numPointsToCollect * xStep - xStep;
			
			JsonObject json = new JsonObject();
			json.add("plotActual", JsonUtil.listToJsonArray(list1));
			json.add("plotMean", JsonUtil.listToJsonArray(list2));
			json.add("xMax", xMax);
			return json.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			readLock.unlock();
		}

	}
	
	public static class Point {
		double x, y;
		public Point(double x, double y) {
			this.x = x;
			this.y = y;
		}
		public Double getX() {
			return x;
		}
		public Double getY() {
			return y;
		}
	}

}
