package com.sponberg.fluid.android.layout;

import android.content.Context;
import android.os.Parcelable;
import android.widget.ListView;

import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.layout.ViewBehaviorTable;
import com.sponberg.fluid.util.Logger;

public class ListViewFluid extends ListView implements FluidViewAndroid {

	private final FluidListAdapter adapter;
	
	Bounds bounds;
	
	private String dataModelListenerId;
	
	private final String viewPath;
	
	final static boolean kPrecomputeHeights = true;
	
	final ViewBehaviorTable viewBehavior;
	
	private boolean shouldScrollToBottom = false;
	
	private Parcelable mListState = null;

	public ListViewFluid(Context context,
			com.sponberg.fluid.layout.ViewPosition view, Bounds bounds,
			String viewPath, CustomLayout rootCustomLayout) {
		super(context);
		this.viewPath = viewPath;
		this.bounds = bounds;
		
		this.viewBehavior = (view.getViewBehavior() instanceof ViewBehaviorTable) ? (ViewBehaviorTable) (view.getViewBehavior()) : null;
		
		/*
		String rowLayout = ((ViewBehaviorTable) view.getViewBehavior()).getRowProvider().getRowLayout();
		if (rowLayout != null) {
			Layout layout = GlobalState.fluidApp.getLayout(rowLayout);
			String heightString = layout.getProperty("tablerow", "height");
			if (heightString != null && heightString.equals("compute")) {
				computeRowHeight = true;
			}
		}*/
		
		this.adapter = new FluidListAdapter(view, rootCustomLayout);
		adapter.bounds = bounds;
		setAdapter(adapter);
	}
	
	@Override
	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(bounds.width, bounds.height);
	}

	@Override
	protected void onLayout(final boolean changed, final int l, final int t, final int r, final int b) {
		adapter.bounds.width = r - l;

		try {
			super.onLayout(changed, l, t, r, b);
		} catch (java.lang.IllegalStateException e) {
			
			if (!GlobalState.fluidApp.isRecoverFromExceptions()) {
				throw e;
			}
			
			Logger.warn(this, e);
		}
		
		int count = getChildCount();
		for (int index = 0; index < count; index++) {
			android.view.View view = this.getChildAt(index);
			view.layout(view.getLeft(), view.getTop(), view.getLeft() + (r - l), view.getBottom());
		}
		
		checkAndScrollToBottom();
	}

	public void checkAndScrollToBottom() {
		
		if (shouldScrollToBottom) {
			if (this.viewBehavior != null && this.viewBehavior.isScrollToBottomOnLoad()) {
				scrollToBottom();
			}
			
			shouldScrollToBottom = false;
		}
	}

	public void hideViewWithId(long id) {
		adapter.hideViewWithId(id);
	}
	
	public void reloadData() {
		//if (kPrecomputeHeights && computeRowHeight) {
		if (kPrecomputeHeights) {
			adapter.precomputeHeightsAsync(this, true);
		}
		adapter.notifyDataSetChanged();
		checkAndScrollToBottom();
	}

	public void scrollToBottom() {
		this.post(new Runnable() {
	        @Override
	        public void run() {
	            // Select the last row so it will scroll into view...
	            setSelection(getCount() - 1);
	        }
	    });
	}
	
	public String getDataModelListenerId() {
		return dataModelListenerId;
	}

	public void setDataModelListenerId(String dataModelListenerId) {
		this.dataModelListenerId = dataModelListenerId;
	}

	@Override
	public void cleanup() {
		
		for (int index = 0; index < this.getChildCount(); index++) {
			
			FluidViewAndroid fluidView = (FluidViewAndroid) this.getChildAt(index);
			fluidView.cleanup();
		}
		
		this.adapter.cleanup();
	}

	public String getViewPath() {
		return viewPath;
	}
	
	@Override
	protected void onAttachedToWindow () {
		
		super.onAttachedToWindow();
		
		//if (kPrecomputeHeights && computeRowHeight) {
		if (kPrecomputeHeights) {
			adapter.precomputeHeightsAsync(this, false);
		}
		
		shouldScrollToBottom = true;
		
		if (mListState != null) {
			onRestoreInstanceState(mListState);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		
		//if (kPrecomputeHeights && computeRowHeight) {
		if (kPrecomputeHeights) {
			adapter.cancelPrecomputeHeightsAsync();
		}
		
		shouldScrollToBottom = true;
		
		mListState = onSaveInstanceState();
		
		super.onDetachedFromWindow();
	}
	
}
