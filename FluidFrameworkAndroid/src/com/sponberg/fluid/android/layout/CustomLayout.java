package com.sponberg.fluid.android.layout;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.android.layout.FluidViewFactoryRegistration.FluidViewBuilderInfo;
import com.sponberg.fluid.android.util.OnTouchListenerClick;
import com.sponberg.fluid.layout.ActionListener.EventInfo;
import com.sponberg.fluid.layout.DataChangeListener;
import com.sponberg.fluid.layout.Layout;
import com.sponberg.fluid.layout.ModalView;
import com.sponberg.fluid.layout.Screen;
import com.sponberg.fluid.layout.View;
import com.sponberg.fluid.layout.ViewBehavior;
import com.sponberg.fluid.layout.ViewBehaviorSubview;
import com.sponberg.fluid.layout.ViewPosition;

public class CustomLayout extends ViewGroup implements FluidViewAndroid {

	private final HashMap<String, android.view.View> viewsById = new HashMap<>();

	private final String dataModelKeyPrefix;

	private final Screen screen; // null if this is a subview

	private final Layout layout;

	static int containerId = 1;

	public boolean isRoot;

	final protected Bounds bounds;

	boolean measurePass = true;

	private android.view.View currentViewFocused = null;

	private final CustomLayout rootCustomLayout; // screen

	private boolean listenToDataModelChanges = true;

	private String dataModelListenerId;

	private boolean listenForTouchEvent = false;

	private final String viewPath;

	private final ModalView modalView;

	protected final boolean lastViewPathTokenIsIndex;

	Bounds measuringBounds = new Bounds(0, 0, 0, 0);

	ScrollViewBounded scrollView = null;

	ViewBounded scrollViewView = null;

	boolean wrapInScrollView;

	boolean keyboardShowing = false;

	protected ListViewFluid insideTableView;

	protected boolean userActivityEnabled = true;

	boolean cleanedUp = false;

	boolean created = false;

	ArrayList<String> conditionalListenerIds = new ArrayList<>();

	public CustomLayout(Context context, Screen screen, Layout layout,
			Bounds bounds, String dataModelPrefix,
			com.sponberg.fluid.layout.Color defaultBackgroundColor,
			String viewPath, boolean listenForTouchEvent, CustomLayout rootCustomLayout,
			boolean listenToDataModelChanges, String dataModelListenerId, ModalView modalView,
			boolean wrapInScrollView, boolean lastViewPathTokenIsIndex, ListViewFluid insideTableView) {
        super(context);
        this.screen = screen;
        this.layout = layout;
        this.dataModelKeyPrefix = dataModelPrefix;
        setId(containerId++);
		this.bounds = new Bounds(bounds);
		this.viewPath = viewPath;
		this.listenForTouchEvent = listenForTouchEvent;
		this.rootCustomLayout = rootCustomLayout;
		this.listenToDataModelChanges = listenToDataModelChanges;
		this.dataModelListenerId = dataModelListenerId;
		if (this.dataModelListenerId == null) {
			this.dataModelListenerId = (rootCustomLayout != null) ? rootCustomLayout.getDataModelListenerId() : layout.getId();
		}
		this.modalView = modalView;
		this.wrapInScrollView = wrapInScrollView;
		this.lastViewPathTokenIsIndex = lastViewPathTokenIsIndex;
		this.insideTableView = insideTableView;
		
		if (layout.isBlockFocusViewOnLoad()) {
			this.setFocusable(true);
			this.setFocusableInTouchMode(true);
		}
		
		createOrUpdateViews(this.bounds);
		if (layout.getBackgroundColor() != null) {
			this.setBackgroundColor(CustomLayout.getColor(layout.getBackgroundColor()));
		} else if (defaultBackgroundColor != null) {
			this.setBackgroundColor(CustomLayout.getColor(defaultBackgroundColor));
		}

		final String updateViews = (dataModelPrefix == null) ? "default" : dataModelPrefix;

		for (final String conditionalKey : layout.getConditionalKeys()) {

			String conditionalListenerId = "customLayout-" + this.getId() + "-conditional-" + updateViews;
			conditionalListenerIds.add(conditionalListenerId);
			GlobalState.fluidApp.getDataModelManager().addDataChangeListener(conditionalKey,
					conditionalListenerId,
					new DataChangeListener() {
						@Override
						public void dataChanged(String key, String... subKeys) {
							GlobalState.fluidApp.getSystemService().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									if (CustomLayout.this.insideTableView != null) {
										CustomLayout.this.insideTableView.reloadData();
									}

									createOrUpdateViews(CustomLayout.this.bounds);
								}
							});
						}
						@Override
						public void dataRemoved(String arg0) {
							// Do nothing, hstdbc cleanup views?
						}
					});
		}
    }

	/* todo: implement
	public void reset(Context context, Screen screen, Layout layout,
			Bounds bounds, String dataModelPrefix,
			com.sponberg.fluid.layout.Color defaultBackgroundColor,
			String viewPath, boolean listenForTouchEvent, CustomLayout rootCustomLayout,
			boolean listenToDataModelChanges, String dataModelListenerId) {
        this.screen = screen;
        this.layout = layout;
        this.dataModelKeyPrefix = dataModelPrefix;
        setId(containerId++);
		this.bounds = bounds;
		this.viewPath = viewPath;
		this.listenForTouchEvent = listenForTouchEvent;
		this.rootCustomLayout = rootCustomLayout;
		this.listenToDataModelChanges = listenToDataModelChanges;
		this.dataModelListenerId = dataModelListenerId;
		if (this.dataModelListenerId == null)
			this.dataModelListenerId = (rootCustomLayout != null) ? rootCustomLayout.getDataModelListenerId() : layout.getId();
		createOrUpdateViews(bounds);
		createPass = false;
		if (layout.getBackgroundColor() != null)
			this.setBackgroundColor(CustomLayout.getColor(layout.getBackgroundColor()));
		else if (defaultBackgroundColor != null)
			this.setBackgroundColor(CustomLayout.getColor(defaultBackgroundColor));

		viewsById = new HashMap<>();

		removeAllViews();

		createPass = true;
		measurePass = true;
		moving = false;
		tap = false;

		currentViewFocused = null;

		listenersForTappingOutsideWhileFocused = new HashMap<>();
    }*/

	public String getDataModelListenerId() {
		return dataModelListenerId;
	}

	boolean moving = false;
	boolean tap = false;
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		boolean superResult = super.onTouchEvent(event);

		if (!listenForTouchEvent) {
			return superResult;
		}

		return true;

		/* TODO: evaluate functionality and remove this block

		final int action = event.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			moving = false;
			tap = false;
		} else if (action == MotionEvent.ACTION_MOVE) {
			moving = true;
		} else if (action == MotionEvent.ACTION_UP) {
			if (!moving) {
				tap = true;
			}
		}

		if (tap) {

			EventInfo eventInfo = new EventInfo();
			eventInfo.setDataModelKeyParent(dataModelKeyPrefix);

			String viewPath = this.viewPath;
            if (this.lastViewPathTokenIsIndex) {
                int index = this.viewPath.lastIndexOf(".");
                viewPath = this.viewPath.substring(0, index);
                String indexString = this.viewPath.substring(index + 1);
                eventInfo.setUserInfo(Integer.valueOf(indexString));
            }

			GlobalState.fluidApp.getEventsManager().userTapped(viewPath, eventInfo);
		}

		return true;*/
	}

	@Override
    protected void onMeasure(int width, int height) {

	   for (int index = 0; index < this.getChildCount(); index++) {
		   this.getChildAt(index).measure(width, height);
	   }

	   int measureWidth;
	   int measureHeight;

	   if (isRoot) {
		   measureWidth = MeasureSpec.getSize(width);
		   measureHeight = MeasureSpec.getSize(height);

		   super.onMeasure(width, height);
	   } else {
		   measureWidth = bounds.width;
		   measureHeight = bounds.height;

		   if (measureWidth == 0) {
			throw new RuntimeException("Not expected 34");
		}

		   this.setMeasuredDimension(bounds.width, bounds.height);
	   }

	   measurePass = true;
	   measuringBounds.setTo(0, 0, measureWidth, measureHeight);
	   createOrUpdateViews(measuringBounds);

    }

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		measurePass = false;
		measuringBounds.setTo(left, top, right - left, bottom - top);

		if (this.scrollView != null) {
		   	this.scrollView.layout(left, top, right, bottom);
		}

		createOrUpdateViews(measuringBounds);
	}

	public void createOrUpdateViews(Bounds parentBounds) {

		if (cleanedUp) {
			return;
		}

		this.bounds.setTo(parentBounds);

		checkAndCreateScrollView(parentBounds);

		if (!created) {
			getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			    @Override
			    public void onGlobalLayout() {
			    	if (currentViewFocused == null) {
						return;
					}

			        int heightDiff = getRootView().getHeight() - getHeight();
			        keyboardShowing = heightDiff > GlobalState.fluidApp.pixelsToPixels(150);
			     }
			});
		}

		if (this.scrollViewView != null && measurePass) {

			double height = layout.calculateHeight(false, parentBounds.width, this.dataModelKeyPrefix);
			height = Math.max(height, getHeight());
			if (!keyboardShowing) {
				scrollViewView.bounds.setTo(parentBounds.x, parentBounds.y, parentBounds.width, (int) Math.ceil(height));
			}
			scrollViewView.measure(0, 0);
		}

		if (!measurePass) {
			setLeft(parentBounds.x);
			setTop(parentBounds.y);
			setRight(parentBounds.x + parentBounds.width);
			setBottom(parentBounds.y + parentBounds.height);
		}

		int height = parentBounds.height;
		if (this.scrollViewView != null) {
			height = this.scrollViewView.getBounds().height;
		}

		boolean landscape = GlobalState.fluidApp.getUiService().isOrientationLandscape();
    	for (final ViewPosition view : layout.getViews(landscape, parentBounds.width, height, this.dataModelKeyPrefix, this.viewPath)) {

            int x = (int) Math.round(view.getX());
            int y = (int) Math.round(view.getY());
            int w = (int) Math.round(view.getWidth());
            int h = (int) Math.round(view.getHeight());

            String viewPath = this.viewPath + "." + view.getId();

            Bounds bounds = new Bounds(x, y, w, h);

			FluidViewBuilderInfo info = FluidViewBuilderInfo.builder()
					.bounds(bounds).context(getContext())
					.dataModelPrefix(dataModelKeyPrefix)
					.listenerId(dataModelListenerId)
					.measurePass(measurePass)
					.customLayout(this)
					.viewPath(viewPath)
					.modalView(modalView)
					.insideTableView(insideTableView)
					.build();

            ViewBehavior viewBehavior = view.getViewBehavior();

            FluidViewAndroid fluidView = (FluidViewAndroid) viewsById.get(view.getId());
            if (fluidView == null && view.isVisible()) {
            	if (Looper.myLooper() != Looper.getMainLooper() && viewBehavior.getType().equals(ViewBehavior.webview)) {
					// hstdbc don't reference webview like this, make more oo
            		continue;
				}

            	fluidView = (FluidViewAndroid) GlobalState.fluidApp.getFluidViewFactory().createView(viewBehavior.getType(), view, info);
            	addSubview((android.view.View) fluidView, view);

            	if (fluidView instanceof ViewGroup) {
            		addFocusListenerForChildViewInViewGroup((ViewGroup)fluidView);
            	} else {
            		final android.view.View androidViewFinal = (android.view.View) fluidView;
                	androidViewFinal.setOnFocusChangeListener(new OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(android.view.View v, boolean hasFocus) {
                        	if (hasFocus) {
                            	currentViewFocused = androidViewFinal;
                            }
                        }
                    });
            	}
            } else if (fluidView != null) {
	            setViewBoundsAndMeasureOrLayout(fluidView, bounds);
	            styleView((android.view.View) fluidView, view);
	            GlobalState.fluidApp.getFluidViewFactory().updateView(viewBehavior.getType(), fluidView, view, info);
            }
    	}

    	created = true;
	}

	private void addFocusListenerForChildViewInViewGroup(ViewGroup viewGroup) {
		
		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			final android.view.View subView = viewGroup.getChildAt(i);
			if (subView instanceof ViewGroup) {
				addFocusListenerForChildViewInViewGroup((ViewGroup)subView);
			} else {
				subView.setOnFocusChangeListener(new OnFocusChangeListener() {
	                @Override
	                public void onFocusChange(android.view.View v, boolean hasFocus) {
	                	if (hasFocus) {
	                    	currentViewFocused = subView;
	                    }
	                }
	            });
			}
		}
	}
	
	
	private void checkAndCreateScrollView(Bounds parentBounds) {
		if (this.scrollViewView == null && wrapInScrollView) {
			scrollView = new ScrollViewBounded(getContext());
			scrollView.setBackgroundColor(0x00000000);
			this.addView(scrollView);

			this.scrollViewView = new ViewBounded(getContext(), this);
			this.scrollViewView.setBackgroundColor(0x00000000);
			this.scrollViewView.setBounds(parentBounds);
			scrollView.addView(this.scrollViewView);
		}
	}

	@Override
	public void cleanup() {

		if (cleanedUp) {
			return;
		}

		cleanedUp = true;

		for (final View view : layout.getAllViewsToBePresentedToUI()) {
            ViewBehavior viewBehavior = view.getViewBehavior();
            FluidViewAndroid fluidView = (FluidViewAndroid) viewsById.get(view.getId());
            if (fluidView != null) {
            	GlobalState.fluidApp.getFluidViewFactory().cleanupView(viewBehavior.getType(), fluidView);
            	fluidView.cleanup();
            }
		}

		for (String listenerId : conditionalListenerIds) {
			GlobalState.fluidApp.getDataModelManager().removeDataChangeListener(listenerId);
		}
		conditionalListenerIds.clear();
	}

	protected void addSubview(android.view.View view, ViewPosition fluidView) {
		
		if (this.scrollViewView != null) {
			this.scrollViewView.addView(view);
		} else {
			this.addView(view);
		}
        styleView(view, fluidView);
        viewsById.put(fluidView.getId(), view);
	}

	protected void styleView(android.view.View view, ViewPosition fluidView) {

		if (!fluidView.getViewBehavior().isViewFactorySetsBackground()) {
			styleView(view, fluidView.getViewBehavior());
		}
	}

	protected void styleView(android.view.View view, ViewBehavior viewBehavior) {//int color, int cornerRadius) {

		com.sponberg.fluid.layout.Color backgroundColor = null;
		if (viewBehavior instanceof ViewBehaviorSubview) {
			backgroundColor = GlobalState.fluidApp.getLayout(((ViewBehaviorSubview) viewBehavior).getSubview()).getBackgroundColor();
		}

		GradientDrawable bg = new GradientDrawable();

		bg.setCornerRadii(setupCornerRadii(viewBehavior));

		if (backgroundColor == null) {
			backgroundColor = viewBehavior.getBackgroundColor(getDataModelKeyPrefix());
		}
		if (backgroundColor != null) {
			bg.setColor(getColor(backgroundColor));
		} else {
			bg.setColor(0x00000000);
		}

		Double borderSize = viewBehavior.getBorderSize();
		com.sponberg.fluid.layout.Color borderColor = viewBehavior.getBorderColor();

		if (borderSize != null && borderSize.intValue() > 0) {
			bg.setStroke(borderSize.intValue(), getColor(borderColor));
		}

		//view.setBackground(bg); next version of ADK
		view.setBackgroundDrawable(bg);
	}
	
	protected static float[] setupCornerRadii(ViewBehavior vb) {
		
		int radius = vb.getCornerRadius();
		int topLeft = vb.getCornerTopLeftRadius();
		int topRight = vb.getCornerTopRightRadius();
		int botRight = vb.getCornerBottomRightRadius();
		int botLeft = vb.getCornerBottomLeftRadius();
		
		if(radius > 0) {
			topLeft = topLeft <= 0 ? radius : topLeft;
			topRight = topRight <= 0 ? radius : topRight;
			botRight = botRight <= 0 ? radius : botRight;
			botLeft = botLeft <= 0 ? radius : botLeft;
		}
		
		float[] cornerRadii = new float[] {topLeft, topLeft, topRight, topRight, botRight, botRight, botLeft, botLeft};
		
		return cornerRadii;
	}

	public void setViewBoundsAndMeasureOrLayout(FluidViewAndroid view, Bounds bounds) {
		if (measurePass) {
			view.setBounds(bounds);
			view.measure(0, 0);
		} else {
			view.layout(bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height);
		}
	}

	protected static String getValueFor(ViewPosition view, String baseText, String dataModelKeyPrefix) {
		if (view.getKey() == null) {
			return baseText;
		} else {
			return view.getValue(dataModelKeyPrefix, view.getKey(), baseText);
		}
	}

	protected static void setValueFor(ViewPosition view, String dataModelKeyPrefix, Object value) {
		if (view.getKey() == null) {
			return;
		} else {
			view.setValue(dataModelKeyPrefix, view.getKey(), value);
		}
	}

	public static int getColor(com.sponberg.fluid.layout.Color color) {
		return Color.argb((int) Math.round(color.getAlpha() * 255), (int) Math.round(color.getRed() * 255), (int) Math.round(color.getGreen() * 255), (int) Math.round(color.getBlue() * 255));
	}

	HashMap<android.view.View, TappedOutsideWhileFocusedListener> listenersForTappingOutsideWhileFocused = new HashMap<>();

	public static interface TappedOutsideWhileFocusedListener {
		public void tappedOutsideWhileFocused();
	}

	public void addTappedOutsideWhileFocusedListener(android.view.View view, TappedOutsideWhileFocusedListener listener) {

		listenersForTappingOutsideWhileFocused.put(view, listener);
	}

	boolean dispatchMoving = false;
	boolean dispatchTap = false;
	Point touchEventStartedPoint = new Point(0, 0);

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {

		android.view.View view = ((Activity) getContext()).getCurrentFocus();

		boolean ret = super.dispatchTouchEvent(event);

		boolean override = isRoot && keyboardShowing;

		final int action = event.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			dispatchMoving = false;
			dispatchTap = false;
			touchEventStartedPoint = new Point((int) event.getX(), (int) event.getY());
		} else if (action == MotionEvent.ACTION_MOVE) {
			dispatchMoving = true;
		} else if (action == MotionEvent.ACTION_UP) {
			Point end = new Point((int) event.getX(), (int) event.getY());
			if (!dispatchMoving ||
					event.getDownTime() < OnTouchListenerClick.kEventDownTimeForTap ||
					OnTouchListenerClick.getDistance(touchEventStartedPoint, end) < OnTouchListenerClick.kEventMoveDistanceForTap) {
				dispatchTap = true;
			}
		}

		if (dispatchTap) {
			userTappedEvent();
			
			if ((!ret || override) && view != null && view == currentViewFocused) {
		
				int[] location = {0,0};
				currentViewFocused.getLocationOnScreen(location);

				boolean outside =
						event.getRawX() < location[0] ||
						event.getRawX() > location[0] + currentViewFocused.getWidth() ||
						event.getRawY() < location[1] ||
						event.getRawY() > location[1] + currentViewFocused.getHeight();

				if (outside) {
					TappedOutsideWhileFocusedListener listener = listenersForTappingOutsideWhileFocused.get(view);
					if (listener != null) {
						listener.tappedOutsideWhileFocused();
					}
				}
			}
		}

		return ret;
	}

	private void userTappedEvent() {

		if ((rootCustomLayout != null && !rootCustomLayout.isUserActivityEnabled()) ||
				!userActivityEnabled) {
			return;
		}

		EventInfo eventInfo = new EventInfo();
		eventInfo.setDataModelKeyParent(dataModelKeyPrefix);

		String viewPath = this.viewPath;
		if (this.lastViewPathTokenIsIndex) {
		    int index = this.viewPath.lastIndexOf(".");
		    viewPath = this.viewPath.substring(0, index);
		    String indexString = this.viewPath.substring(index + 1);
		    eventInfo.setUserInfo(Integer.valueOf(indexString));
		}

		GlobalState.fluidApp.getEventsManager().userTapped(viewPath, eventInfo);
	}

	@Override
	public void setBounds(Bounds bounds) {
		this.bounds.setTo(bounds);
		if (this.scrollViewView != null) {
			this.scrollView.bounds.setTo(bounds.x, bounds.y, bounds.width, bounds.height);
		}
	}

	public void grabFocusForView(final String viewId) {

		final android.view.View view = viewsById.get(viewId);
		
		if (view == null) {
			return;
		}
		
		// Without the delay, the keyboard won't show
		view.postDelayed(new Runnable()
		{
		    @Override
		    public void run()
		    {
				InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		        view.requestFocus();
		        imm.showSoftInput(view, 0);
		    }
		}, 10);
	}

	public void scrollToBottom(final String viewPath, final String viewId) {

		if (!this.viewPath.equals(viewPath)) {
			return;
		}

		if (viewId == null) {
			if (scrollView != null) {
				scrollView.post(new Runnable() {
			        @Override
			        public void run() {
			        	scrollView.fullScroll(ScrollView.FOCUS_DOWN);
			        }
			    });
			}
			return;
		}

		final android.view.View view = viewsById.get(viewId);
		if (view instanceof ListViewFluid) {
			((ListViewFluid) view).scrollToBottom();
		} else {
			throw new RuntimeException("Unsupported operation: scrollToBottom for view " + viewId);
		}
	}

	public boolean isRoot() {
		return isRoot;
	}

	public void setRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}

	public Screen getScreen() {
		return screen;
	}

	public static CustomLayout getRootCustomLayout(ViewGroup layout) {
		return getRootCustomLayoutHelper(layout.getParent());
	}

	public static CustomLayout getRootCustomLayoutHelper(ViewParent parent) {
		CustomLayout parentView = null;
		if (parent.getParent() != null) {
			parentView = getRootCustomLayoutHelper(parent.getParent());
		}
		if (parentView == null && parent instanceof CustomLayout) {
			parentView = (CustomLayout) parent;
		}
	    return parentView;
	}

	public Layout getLayout() {
		return layout;
	}

	boolean screenAppeared = false;

	@Override
	protected void onDetachedFromWindow() {
	    super.onDetachedFromWindow();
	    if (this.listenToDataModelChanges) {
	    	GlobalState.fluidApp.getDataModelManager().disableDataChangeListener(this.dataModelListenerId);
	    }
	    for (String listenerId : conditionalListenerIds) {
	    	GlobalState.fluidApp.getDataModelManager().disableDataChangeListener(listenerId);
	    }
	    viewDidDisappear();
	    screenAppeared = false;
	}

	public void viewDidDisappear() {
	    if (this.screen != null && screenAppeared) {
	    	this.screen.screenDidDisappear();
	    }
	    screenAppeared = false;
	}

	public void screenWasRemoved() {

		if (this.screen != null) {
			viewDidDisappear();
			this.screen.screenWasRemoved();
			this.cleanup();
		}
	}

	public void viewWillAppear() {
	    if (this.screen != null && !screenAppeared) {
	    	this.screen.screenWillAppear();
	    }
	}

	public void viewDidAppear() {
	    if (this.screen != null && !screenAppeared) {
	    	this.screen.screenDidAppear();
	    }
	    screenAppeared = true;
	}

	@Override
	protected void onAttachedToWindow() {
	    super.onAttachedToWindow();
	    if (this.listenToDataModelChanges) {
		    GlobalState.fluidApp.getDataModelManager().enableDataChangeListener(this.dataModelListenerId);
	    }
	    for (String listenerId : conditionalListenerIds) {
	    	GlobalState.fluidApp.getDataModelManager().enableDataChangeListener(listenerId);
	    }
    	viewWillAppear();
    	viewDidAppear();
	}

	public boolean isListenToDataModelChanges() {
		return listenToDataModelChanges;
	}

	public String getViewPath() {
		return viewPath;
	}

	public String getDataModelKeyPrefix() {
		return dataModelKeyPrefix;
	}

	public Bounds getBounds() {
		return bounds;
	}

	public ModalView getModalView() {
		return modalView;
	}

	public boolean isUserActivityEnabled() {

		if (rootCustomLayout != null) {
			return rootCustomLayout.isUserActivityEnabled();
		}

		return userActivityEnabled;
	}

	public void setUserActivityEnabled(boolean userActivityEnabled) {
		this.userActivityEnabled = userActivityEnabled;
	}

	@Override
	public void requestLayout() {
		if (layout != null) {
			layout.invalidateCache(this.dataModelKeyPrefix);
		}
		super.requestLayout();
	}

	protected OnClickListener onClickListener;
	
	@Override
	public void setOnClickListener(OnClickListener l) {
		super.setOnClickListener(l);
		this.onClickListener = l;
	}

	public OnClickListener getOnClickListener() {
		return onClickListener;
	}
	
}
