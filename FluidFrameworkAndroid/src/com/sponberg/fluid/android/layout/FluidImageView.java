package com.sponberg.fluid.android.layout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.android.util.OnTouchListenerClick;
import com.sponberg.fluid.layout.ActionListener;
import com.sponberg.fluid.layout.ViewBehaviorImage.ImageBounds;

public class FluidImageView extends View implements FluidViewAndroid {

	protected Bitmap bitmap;
	
	protected Bounds bounds;
	
	protected String imageName;
	
	Rect source;
	
	Rect dest;

	protected ImageBounds imageBounds;
	
	boolean needToComputeRects = true;
	
	Double aspectRatio;
	
	Paint paint = new Paint();
	
	CustomLayout rootCustomLayout;
	
	boolean cleanedUp = false;
	
	public FluidImageView(Context context, Bitmap bitmap, String imageName, ImageBounds imageBounds, 
			final String viewPath, final String dataModelKeyParent, final String dataModelKey,
			final CustomLayout rootCustomLayout) {
		super(context);
		this.bitmap = bitmap;
		this.imageName = imageName;
		this.imageBounds = imageBounds;
		this.rootCustomLayout = rootCustomLayout;

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		
		this.setOnTouchListener(new OnTouchListenerClick("FluidImageView" + imageName) {

			@Override
			public void tap() {
				
				if (!rootCustomLayout.isUserActivityEnabled()) {
					return;
				}
				
				ActionListener.EventInfo eventInfo = new ActionListener.EventInfo();
				eventInfo.setDataModelKeyParent(dataModelKeyParent);
				eventInfo.setDataModelKey(dataModelKey);
				GlobalState.fluidApp.getEventsManager().userTapped(viewPath, eventInfo);				
			}
		});
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(bounds.width, bounds.height);
	}

	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		
		if (bitmap == null || cleanedUp) {
			return;
		}

		if (needToComputeRects) {
			computeRects();
			needToComputeRects = false;
		}
		super.onDraw(canvas);
		
		canvas.drawBitmap(bitmap, source, dest, paint);
	}

	@Override
	public void cleanup() {
		
		this.cleanedUp = true;
		this.bitmap.recycle();
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
		this.needToComputeRects = true;
	}
	
	public ImageBounds getImageBounds() {
		return imageBounds;
	}

	public void setImageBounds(ImageBounds imageBounds) {
		this.imageBounds = imageBounds;
		this.needToComputeRects = true;
	}

	protected void computeRects() {
		
		if (bitmap == null) {
			return;
		}
		
		source = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		dest = new Rect(imageBounds.getX(), 
						imageBounds.getY(),
						imageBounds.getX() + imageBounds.getWidth(), 
						imageBounds.getY() + imageBounds.getHeight());
	}

	public Bounds getBounds() {
		return bounds;
	}

	public Double getAspectRatio() {
		return aspectRatio;
	}

	public void setAspectRatio(Double aspectRatio) {
		this.aspectRatio = aspectRatio;
	}
	
}
