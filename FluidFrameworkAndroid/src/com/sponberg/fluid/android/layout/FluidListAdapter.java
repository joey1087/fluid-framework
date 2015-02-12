package com.sponberg.fluid.android.layout;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.sponberg.fluid.FluidApp;
import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.layout.ActionListener.EventInfo;
import com.sponberg.fluid.layout.DataChangeListener;
import com.sponberg.fluid.layout.Layout;
import com.sponberg.fluid.layout.TableLayout;
import com.sponberg.fluid.layout.TableLayout.TableSection;
import com.sponberg.fluid.layout.TableRow;
import com.sponberg.fluid.layout.ViewBehaviorTable;
import com.sponberg.fluid.util.LRUCache;
import com.sponberg.fluid.util.Logger;

public class FluidListAdapter extends BaseAdapter {

	protected final com.sponberg.fluid.layout.ViewPosition view;
	
	final static int kNumHeightsToPrecompute = 50;
	
	Bounds bounds;
	
	ViewBehaviorTable viewBehavior;
	
	private static ScheduledExecutorService precomputeService = Executors.newSingleThreadScheduledExecutor();
	private static PrecomputeTask precomputeTask;
	
	private final LRUCache<Long, CustomLayout> viewsById = new LRUCache<>(100);
	
	private static final boolean viewCaching = true; 
	
	CustomLayout rootCustomLayout;
	
	public FluidListAdapter(com.sponberg.fluid.layout.ViewPosition view, CustomLayout rootCustomLayout) {
		this.view = view;
		this.rootCustomLayout = rootCustomLayout;
		viewBehavior = (ViewBehaviorTable) view.getViewBehavior();
	}
	
	public void hideViewWithId(long id) {
		CustomLayout layout = viewsById.get(id);
		if (layout != null) {
			layout.setVisibility(View.INVISIBLE);
		}
	}
	
	@Override
	public int getCount() {
		ViewBehaviorTable viewBehavior = (ViewBehaviorTable) view.getViewBehavior();
		return viewBehavior.getCount();
	}

	@Override
	public Object getItem(int index) {
		ViewBehaviorTable viewBehavior = (ViewBehaviorTable) view.getViewBehavior();
		return viewBehavior.getRowOrSectionAt(index);
	}

	@Override
	public long getItemId(int index) {
		ViewBehaviorTable viewBehavior = (ViewBehaviorTable) view.getViewBehavior();
		return viewBehavior.getItemId(index);
	}

	/*
	public long getItemIdFor(Object o) {
		if (o instanceof TableSection)
			return ((TableSection) o).getSectionId().hashCode();
		else if (o instanceof TableRow)
			return ((TableRow) o).getId();
		else
			return -1;		
	}*/
	
	@Override
	public View getView(final int index, android.view.View androidView,
			ViewGroup parent) {
		
		boolean isRowLayout = viewBehavior.getRowProvider().getRowLayout() != null;
		
		final ListViewFluid parentLayout = (ListViewFluid) parent;

		Layout layout;
		Object o = getItem(index);
		String dataPrefixKey = null;
		int height;
		TableRow tableRow = null;
		if (o instanceof TableSection) {
			layout = GlobalState.fluidApp.getLayout(((TableSection) o).getSectionHeaderLayout());
			height = getHeightForSectionHeaderLayout(layout, viewBehavior.getSectionHeaderHeight(), bounds.width, null);
			
			if (layout == null) {
				ViewFluidSpace space = new ViewFluidSpace(parent.getContext());
				space.bounds = new Bounds(0, 0, bounds.width, height);
				return space;
			}
		} else if (o instanceof TableRow) {
			tableRow = (TableRow) o;
			layout = GlobalState.fluidApp.getLayout(((TableRow) o).getLayout());
			dataPrefixKey = tableRow.getKey();
			height = getHeightForRowLayout(layout, bounds.width, dataPrefixKey, tableRow.getId());
		} else {
			throw new RuntimeException("Invalid index " + index);
		}
		
		int width = bounds.width;
	    
	    Bounds rowBounds = new Bounds(0, 0, width, height);
	    
	    final long itemId = viewBehavior.getItemId(index);
	    
		String dataModelListenerId = parentLayout.getDataModelListenerId() + itemId;

	    // hstdbc update customlayout so that the parameters can change, this will give better memory as 
	    // we can reuse cells
		CustomLayout customLayout = (viewCaching || FluidApp.useCaching) ? viewsById.get(itemId) : null;
		if (customLayout == null || !isRowLayout) {

			if (customLayout != null && !isRowLayout) {
				customLayout.cleanup();
			}
			
			String tableLayoutId = viewBehavior.getTableLayoutId();
			com.sponberg.fluid.layout.Color defaultBackgroundColor = null;
            if (tableLayoutId != null) {
            	TableLayout tableLayout = GlobalState.fluidApp.getViewManager().getTableLayout(tableLayoutId);
            	defaultBackgroundColor = tableLayout.getBackgroundColor();
            }
			
            String viewPath = layout.getId();
            int i = viewPath.lastIndexOf(".");
            if (i != -1) {
            	viewPath = viewPath.substring(i + 1);
            }
            viewPath = parentLayout.getViewPath() + "." + viewPath;
            viewPath += "|" + itemId;
            
            boolean listenForTouchEvent = false;
            CustomLayout root = null;
            if (tableRow != null) {
	            listenForTouchEvent = true;
	            root = CustomLayout.getRootCustomLayoutHelper(parent);
            }
            
            boolean listenToDataModelChanges = false;
			//if (tableRow != null) {
			//	listenToDataModelChanges = tableRow.isListenToDataModelChanges();
			//}
            
            //if (androidView != null) {
            	// todo: implement view reuse
            //	customLayout = (CustomLayout) androidView;
            //	customLayout.reset(parent.getContext(), null, layout,
			//			rowBounds, dataPrefixKey, defaultBackgroundColor, viewPath,
			//			listenForTouchEvent, root, listenToDataModelChanges, dataModelListenerId);
            //} else {
            
	            customLayout = new CustomLayout(parent.getContext(), null, layout,
						rowBounds, dataPrefixKey, defaultBackgroundColor, viewPath,
						listenForTouchEvent, root, listenToDataModelChanges, dataModelListenerId, null, false, false, parentLayout);
            //}
			customLayout.setRoot(false);
			if ((viewCaching || FluidApp.useCaching)) {
				
				if (isRowLayout) {					
					addViewToCache(dataPrefixKey, itemId, dataModelListenerId,
							customLayout);
				} else {
					viewsById.put(itemId, customLayout);
				}
			}
		} else {
			customLayout.createOrUpdateViews(rowBounds);
		}
		
		final String dataPrefixKeyFinal = dataPrefixKey;
		if (tableRow != null) {
			final TableRow tableRowFinal = tableRow;
			customLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					if (!rootCustomLayout.isUserActivityEnabled()) {
						return;
					}
					
					EventInfo eventInfo = new EventInfo();
					eventInfo.setDataModelKeyParent(dataPrefixKeyFinal);
					eventInfo.setDataModelKey(tableRowFinal.getId() + "");
					
					if (FluidListAdapter.this.viewBehavior.getTableLayoutId() != null) {
						// Table Layout, use name of row
						String[] comps = tableRowFinal.getLayout().split("\\.");
				        String rowLayout = comps[comps.length - 1];
				        eventInfo.setUserInfo(rowLayout);
					} else {
						// Use index of row
						eventInfo.setUserInfo(tableRowFinal.getId());
					}
					
					GlobalState.fluidApp.getEventsManager().userTapped(parentLayout.getViewPath(), eventInfo);
				}
			});
		}
		
		return customLayout;
	}

	private void addViewToCache(final String dataPrefixKey, final long itemId,
			String dataModelListenerId, CustomLayout customLayout) {
		
		viewsById.put(itemId, customLayout);
		final String listenerId = dataModelListenerId + "-table-cache-" + dataPrefixKey + "-" + itemId;
		
		GlobalState.fluidApp.getDataModelManager().addDataChangeListener(null, dataPrefixKey, listenerId, 
				new DataChangeListener() {
					@Override
					public void dataChanged(String key, String... subKeys) {
						viewsById.remove(itemId);
						GlobalState.fluidApp.getDataModelManager().removeDataChangeListener(listenerId);
					}
					@Override
					public void dataRemoved(String key) {
						if (precomputeTask != null) {
							precomputeTask.cancelled = true;
							precomputeTask = null;
						}
						CustomLayout l = viewsById.remove(itemId);
						GlobalState.fluidApp.getDataModelManager().removeDataChangeListener(listenerId);
						l.cleanup();
					}
				});
	}

	int getHeightForRowLayout(Layout layout, double width, String dataModelPrefix, long objectId) {
		
		double height;
		
		String heightString = layout.getProperty("tablerow", "height");
		if (heightString != null && heightString.equals("compute")) {
			boolean landscape = GlobalState.fluidApp.getUiService().isOrientationLandscape();
			height = layout.calculateHeight(landscape, (float) width, dataModelPrefix);
		} else if (heightString != null && heightString.equals("from-object")) {
			height = getHeightFromObjectWith(objectId);
		} else {
			height = Float.parseFloat(heightString);
			height = GlobalState.fluidApp.unitsToPixels(height);
		}
		return Math.round((float) height);
	}	

	double getHeightFromObjectWith(long id) {
		return viewBehavior.getHeightFromObjectWith(id);
	}
	
	int getHeightForSectionHeaderLayout(Layout layout, Double sectionHeaderHeight, float width, String dataModelPrefix) {
		if (layout == null) {
			if (sectionHeaderHeight == null) {
				return 0;
			} else {
				return sectionHeaderHeight.intValue();
			}
		} else {

			double height;
			String heightString = layout.getProperty("tablerow", "height");
			if (heightString != null && heightString.equals("compute")) {
				boolean landscape = GlobalState.fluidApp.getUiService().isOrientationLandscape();
				height = layout.calculateHeight(landscape, width, dataModelPrefix);
			} else {
				height = Float.parseFloat(heightString);
				height = GlobalState.fluidApp.unitsToPixels(height);				
			}
			return Math.round((float) height);
		}
	}	
	
	@Override
	public int getItemViewType(int index) {
		return 0;
		/* hstdbc
		if (viewBehavior.getRowProvider().getRowLayout() != null)
			return 0;
		else
			return index - 1;
			*/
	}
	
	@Override
	public int getViewTypeCount() {
		return 1;
		/*
		if (viewBehavior.getRowProvider().getRowLayout() != null)
			return 1;
		else
			return getCount();*/
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return getCount() == 0;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public boolean isEnabled(int index) {
		return true;
	}

	public void cleanup() {
		
		for (CustomLayout l : viewsById.values()) {
			
			l.cleanup();
		}
	}
	
	protected void precomputeHeightsAsync(final ViewGroup parent, boolean interrupt) {
		
		boolean isRowLayout = viewBehavior.getRowProvider().getRowLayout() != null;
		
		if (!isRowLayout) {
			return;
		}
		
		if (precomputeTask != null && !interrupt && precomputeTask.running) {
			return;
		} else if (precomputeTask != null) {
			precomputeTask.setCancelled(true);
		}
		
		precomputeTask = new PrecomputeTask(parent);
		precomputeService.schedule(precomputeTask, 2, TimeUnit.SECONDS); // don't start immediately
	}
	
	protected void cancelPrecomputeHeightsAsync() {

		if (precomputeTask != null) {
			precomputeTask.setCancelled(true);
			precomputeTask = null;
		}
	}
	
	public class PrecomputeTask implements Runnable {
		
		boolean cancelled = false;
		
		boolean running = true;
		
		final ListViewFluid parentLayout;
		
		final ViewGroup parent;
		
		public PrecomputeTask(final ViewGroup parent) {
			this.parent = parent;
			this.parentLayout = (ListViewFluid) parent;
		}
		
    	public void setCancelled(boolean cancelled) {
			this.cancelled = cancelled;
		}

		@Override
		public void run() {
			try {
				runHelper();
			} catch (Exception e) {
				Logger.error(this, e);
			}
			running = false;
		}
		
		public void runHelper() {
			
			ViewBehaviorTable viewBehavior = (ViewBehaviorTable) view.getViewBehavior();
    		int num = Math.min(kNumHeightsToPrecompute, getCount());
    		for (int index = 0; index < num; index++) {
    			
    			try {
    				// Sleep to allow user interaction to cancel
    				Thread.sleep(200);
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
    			
    			if (cancelled) {
					return;
				}
    			
    			Object o = getItem(index);
    			final long itemId = getItemId(index);
    			if (!(o instanceof TableRow)) {
    				continue;
    			}

    			final String dataModelListenerId = parentLayout.getDataModelListenerId() + itemId;
    			
				TableRow tableRow = (TableRow) o;
				final Layout layout = GlobalState.fluidApp.getLayout(((TableRow) o).getLayout());
				final String dataPrefixKey = tableRow.getKey();
				
				float height = getHeightForRowLayout(layout, bounds.width, dataPrefixKey, itemId); // this will be cached
				
    			if (cancelled) {
					return;
				}
				
	            String viewPath = layout.getId();
	            int i = viewPath.lastIndexOf(".");
	            if (i != -1) {
	            	viewPath = viewPath.substring(i + 1);
	            }
	            viewPath = parentLayout.getViewPath() + "." + viewPath;
	            viewPath += "|" + itemId;
	            
	    		boolean landscape = GlobalState.fluidApp.getUiService().isOrientationLandscape();
				layout.getViews(landscape, bounds.width, height, viewPath);
				
    			if (cancelled) {
					return;
				}

				//if (!FluidApp.useCaching)
				//	continue;
				
			    final Bounds rowBounds = new Bounds(0, 0, bounds.width, (int) height);

				String tableLayoutId = viewBehavior.getTableLayoutId();
				com.sponberg.fluid.layout.Color defaultBackgroundColor = null;
	            if (tableLayoutId != null) {
	            	TableLayout tableLayout = GlobalState.fluidApp.getViewManager().getTableLayout(tableLayoutId);
	            	defaultBackgroundColor = tableLayout.getBackgroundColor();
	            }
				
	            boolean listenForTouchEvent = false;
	            CustomLayout root = null;
	            if (tableRow != null) {
		            listenForTouchEvent = true;
		            root = CustomLayout.getRootCustomLayout(parent);
	            }
	            
	            final boolean listenToDataModelChanges = false;
				    	            
    			if (cancelled) {
					return;
				}

    			if (viewCaching && !viewsById.containsKey(itemId)) {
        			final CountDownLatch countdownLatch = new CountDownLatch(1);
    				final com.sponberg.fluid.layout.Color defaultBackgroundColorFinal = defaultBackgroundColor;
    				final String viewPathFinal = viewPath;
    				final boolean listenForTouchEventFinal = listenForTouchEvent;
    				final CustomLayout rootFinal = root;
    				Runnable r = new Runnable() {
    					@Override
						public void run() {
    	    	            CustomLayout customLayout = new CustomLayout(parent.getContext(), null, layout,
    	    						rowBounds, dataPrefixKey, defaultBackgroundColorFinal, viewPathFinal,
    	    						listenForTouchEventFinal, rootFinal, listenToDataModelChanges, dataModelListenerId, null, false, false, parentLayout);
    		    			customLayout.setRoot(false);
    		    			
    		    			if (!viewsById.containsKey(itemId)) {
	    						addViewToCache(dataPrefixKey, itemId, dataModelListenerId,
	    								customLayout);
    		    			}
    						
	    					countdownLatch.countDown();
    					}
    				};
    				GlobalState.fluidApp.getSystemService().runOnUiThread(r); // todo: safe to run on ui thread?
    				try {
						countdownLatch.await();
					} catch (InterruptedException e) {
					}
    				
    			}

    		}
        }

    }
	
}
