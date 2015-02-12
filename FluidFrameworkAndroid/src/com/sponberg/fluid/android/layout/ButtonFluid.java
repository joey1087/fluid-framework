package com.sponberg.fluid.android.layout;

import android.content.Context;
import android.util.TypedValue;
import android.widget.Button;

import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.android.layout.CustomTextView.BoundsWithFontSize;
import com.sponberg.fluid.layout.ActionListener.EventInfo;
import com.sponberg.fluid.layout.ViewBehaviorImage.ImageBounds;
import com.sponberg.fluid.layout.ViewBehaviorButton;

public class ButtonFluid extends Button implements FluidViewAndroid {

	Bounds bounds;

	private float lineSpacingMultiplier = 1.0f;
    
	private float lineAdditionalVerticalPadding = 0.0f;
	
	int maxLines = 1;
	
	private final ViewBehaviorButton viewBehavior;
	
	private String viewPath;
	
	private String dataModelKeyParent;
	
	private String dataModelKey;
	
	boolean needsSizeAndPosition = true;
	
	private String imageName;
	
	private ImageBounds imageBounds = new ImageBounds(0, 0, 0, 0);
	
	int labelHashCode;
	
	int cachedWidth;
	int cachedHeight;
	
	CustomLayout rootCustomLayout;
	
	public ButtonFluid(Context context, ViewBehaviorButton viewBehavior, String viewPath, String dataModelKeyParent, String dataModelKey, CustomLayout rootCustomLayout) {
		super(context);
		this.viewBehavior = viewBehavior;
		this.viewPath = viewPath;
		this.dataModelKeyParent = dataModelKeyParent;
		this.dataModelKey = dataModelKey;
		this.rootCustomLayout = rootCustomLayout;
	}

	public int getLabelHashCode() {
		return labelHashCode;
	}

	public void setLabelHashCode(int labelHashCode) {
		this.labelHashCode = labelHashCode;
	}

    protected void onMeasure(int width, int height) {
		if (needsSizeAndPosition)
			sizeAndPositionLabel();
    	this.setMeasuredDimension(bounds.width, bounds.height);
    }

	public Bounds getBounds() {
		return bounds;
	}

	public void setBounds(Bounds bounds) {
		if (this.bounds == null || !this.bounds.equals(bounds))
			this.needsSizeAndPosition = true;
		this.bounds = bounds;
	}
    
	public void setCustomText(CharSequence text) {
		super.setText(text);
		needsSizeAndPosition = true;
	}
	
	public void sizeAndPositionLabel() {
		
		// Just measure, position will happen later during layout
		
		Double fontSize = this.viewBehavior.getFontSize();
		
		CharSequence cs = this.getText();
	    
		// Only take up 75% of the height of the button with text
		BoundsWithFontSize boundsWithFontSize = CustomTextView.computeHeightOfText(cs,
				(int) (bounds.width * .75f), bounds.height * .5f, null, fontSize, null,
				null, this.getContext());
	    
	    setTextSize(TypedValue.COMPLEX_UNIT_PX, boundsWithFontSize.fontPixels);
	    int computedHeight = boundsWithFontSize.computedHeight;
	    
	    int width = Math.max(bounds.width, 1);
	    
	    cachedWidth = width;
	    cachedHeight = Math.min(bounds.height, computedHeight);
		needsSizeAndPosition = false;
	}
	
	public void userTapped() {
		
		if (!rootCustomLayout.isUserActivityEnabled()) {
			return;
		}
		
		EventInfo eventInfo = new EventInfo();
		eventInfo.setDataModelKeyParent(dataModelKeyParent);
		eventInfo.setDataModelKey(dataModelKey);
		GlobalState.fluidApp.getEventsManager().userTapped(viewPath, eventInfo);
	}
	
	@Override
	public void cleanup() {
	}

	public String getViewPath() {
		return viewPath;
	}

	public void setViewPath(String viewPath) {
		this.viewPath = viewPath;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public ImageBounds getImageBounds() {
		return imageBounds;
	}

	public void setImageBounds(ImageBounds imageBounds) {
		this.imageBounds = imageBounds;
	}

}
