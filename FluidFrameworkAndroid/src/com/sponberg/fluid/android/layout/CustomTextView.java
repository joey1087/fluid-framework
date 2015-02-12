package com.sponberg.fluid.android.layout;

import android.content.Context;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.TypedValue;
import android.widget.TextView;

import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.layout.ViewBehaviorLabel;
import com.sponberg.fluid.layout.ActionListener.EventInfo;

public class CustomTextView extends TextView {

	private static float kLineSpacingMultiplier = 1.0f;
    
	private static float kLineAdditionalVerticalPadding = 0.0f;
	
	int maxLines = -1;
	
	private Bounds bounds;
	
	private final ViewBehaviorLabel viewBehavior;
	
	double adjustY = 0;
	
	boolean needsSizeAndPosition = true;
	
	int labelHashCode;
	
	int cachedWidth;
	int cachedHeight;
	
	CustomLayout rootCustomLayout;
	
	public int getLabelHashCode() {
		return labelHashCode;
	}

	public void setLabelHashCode(int labelHashCode) {
		this.labelHashCode = labelHashCode;
	}

	public double getAdjustY() {
		return adjustY;
	}

	public Bounds getBounds() {
		return bounds;
	}

	public void setBounds(Bounds bounds) {
		if (!this.bounds.equals(bounds))
			this.needsSizeAndPosition = true;
		this.bounds = bounds;
	}

	public CustomTextView(Context context, ViewBehaviorLabel viewBehavior, Bounds bounds, CustomLayout rootCustomLayout) {
		super(context);
		setIncludeFontPadding(false);
		this.viewBehavior = viewBehavior;
		this.bounds = bounds;
		this.rootCustomLayout = rootCustomLayout;
	}

	public void userTapped(String viewPath, String dataModelKeyParent, String dataModelKey) {
		
		if (!rootCustomLayout.isUserActivityEnabled()) {
			return;
		}

		EventInfo eventInfo = new EventInfo();
		eventInfo.setDataModelKeyParent(dataModelKeyParent);
		eventInfo.setDataModelKey(dataModelKey);
		GlobalState.fluidApp.getEventsManager().userTapped(viewPath, eventInfo);
	}
	
	public int getMaxLines() {
		return maxLines;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (needsSizeAndPosition)
			sizeAndPositionLabel();
		this.setMeasuredDimension(cachedWidth, cachedHeight);
	}

	protected void forceMeasure() {
		sizeAndPositionLabel();
		this.setMeasuredDimension(cachedWidth, cachedHeight);
	}
	
	public void setCustomText(CharSequence text) {
		super.setText(text);
		needsSizeAndPosition = true;
	}
	
	public void sizeAndPositionLabel() {
		
		// Just measure, position will happen later during layout
		
		Double fontSize = this.viewBehavior.getFontSize();
		Double minFontSize = this.viewBehavior.getMinFontSize();
		Double maxFontSize = this.viewBehavior.getMaxFontSize();
		
		String verticalAlign = this.viewBehavior.getVerticalAlign();
		
		CharSequence cs = this.getText();
	    
		BoundsWithFontSize boundsWithFontSize = computeHeightOfText(cs,
				bounds.width, bounds.height, null, fontSize, minFontSize,
				maxFontSize, this.getContext());
	    
	    setTextSize(TypedValue.COMPLEX_UNIT_PX, boundsWithFontSize.fontPixels);
	    int computedHeight = boundsWithFontSize.computedHeight;
	    
	    adjustY = 0;
	    if (verticalAlign != null) {
		    if (verticalAlign.equals("middle")) {
		    	adjustY = bounds.height / 2 - computedHeight / 2; 
		    } else if (verticalAlign.equals("bottom")) {
		    	adjustY = bounds.height - computedHeight;
		    }
	    }
	    
	    /*
	     * This is because the the computedHeight might be bigger 
	     * than the bounds.height due to minFontSize is not small
	     * enough to fit the text within the bounds without increasing
	     * the height. When that happens the computedHeight will be 
	     * bigger than the bounds height as text is wrapped, which will
	     * make the adjustY be less than 0 if the verticalALign is set
	     * to "middle"
	     */
	    if (adjustY < 0) {
	    	adjustY = 0;
	    }
	    
	    int width = Math.max(bounds.width, 1);
	    
	    cachedWidth = width;
	    cachedHeight = Math.min(bounds.height, computedHeight);
		needsSizeAndPosition = false;
	}
	
	private static Layout createWorkingLayout(CharSequence workingText, int width, TextPaint paint) {
        return new StaticLayout(workingText, paint, width,
                Alignment.ALIGN_NORMAL, kLineSpacingMultiplier, kLineAdditionalVerticalPadding, false);
    }

	public static class BoundsWithFontSize {
		
		float fontPixels;
		
		int computedHeight;

		public float getFontPixels() {
			return fontPixels;
		}

		public int getComputedHeight() {
			return computedHeight;
		}
		
	}
	
	public static BoundsWithFontSize computeHeightOfText(CharSequence spannableText, int maxWidth, double maxHeight, String fontName, Double fixedFontSizeInUnits, 
			Double minFontSizeInUnits, Double maxFontSizeInUnits, Context context) {

	    double kFontSizeExploreStepDown = .5;
	    
	    boolean changeFontSize;
	    double fontSizeInUnits;
	    if (fixedFontSizeInUnits == null) {
	        changeFontSize = true;
	        fontSizeInUnits = Double.parseDouble(GlobalState.fluidApp.getDefault("font", "size"));
	    } else {
	        // Don't allow font to resize. Instead, this will adjust the width to be more narrow and let the height grow.
	        // This means that the text could clip at the end, because the actual height allowed could be less than the
	        // height needed.
	        // For heights that are computed, this should find the exact same size found during the computation phase.
	        changeFontSize = false;
	        fontSizeInUnits = fixedFontSizeInUnits;
	    }

	    double minimumSizeUnits;
	    if (minFontSizeInUnits == null) {
	        minimumSizeUnits = Double.parseDouble(GlobalState.fluidApp.getDefault("font", "minimum-size"));
	    } else {
	        minimumSizeUnits = minFontSizeInUnits;
	    }
	    
	    if (maxFontSizeInUnits != null && fontSizeInUnits > maxFontSizeInUnits) {
	        fontSizeInUnits = maxFontSizeInUnits;
	    }
	    
	    if (fontSizeInUnits < minimumSizeUnits) {
	        fontSizeInUnits = minimumSizeUnits;
	    }
	    
	    float fontPixels = -1;
	    Layout layout = null;
	    TextView textView = new TextView(context);
	    while (fontSizeInUnits >= minimumSizeUnits) {
	        
	    	fontPixels = (float) GlobalState.fluidApp.unitsToPixels(fontSizeInUnits);
	    	textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontPixels);
	    	
	        layout = createWorkingLayout(spannableText, maxWidth, textView.getPaint());
	        
	        if (!changeFontSize || layout.getHeight() <= maxHeight) {
	        	break;
	        }
	        
	        fontSizeInUnits -= kFontSizeExploreStepDown;
	    }
	    
	    BoundsWithFontSize boundsWithFontSize = new BoundsWithFontSize();
	    boundsWithFontSize.fontPixels = fontPixels;
	    boundsWithFontSize.computedHeight = layout.getHeight();
	    return boundsWithFontSize;
	}
	
}
