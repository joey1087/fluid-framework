package com.sponberg.fluid.layout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sponberg.fluid.ApplicationInitializer;
import com.sponberg.fluid.FluidApp;
import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.util.LRUCache;

public class PrecomputeLayoutManager implements ApplicationInitializer {

	public static final String dataModelId = "_fluid-precompute-manager"; 
	
	public static final String dataModelIdForPrecomputeList = dataModelId + ".precomputeList";
	
	private final LRUCache<String, ViewPosition> viewPositions = new LRUCache<>(10000);
	
	ExecutorService computeService = Executors.newSingleThreadExecutor();
	
	TableList<?> precomputeList;
	
	public void setViewPosition(String viewPath, ViewPosition viewPosition) {
		this.viewPositions.put(viewPath, viewPosition);
	}
	
	public ViewPosition getViewPosition(String viewPath) {
		
		if (viewPath == null || !this.viewPositions.containsKey(viewPath)) {
			return null;
		}
		
		return this.viewPositions.get(viewPath);
	}
	
	public TableList<?> getPrecomputeList() {
		return precomputeList;
	}

	public void setPrecomputeList(TableList<?> precomputeList) {
		this.precomputeList = precomputeList;
	}

	public void computeAsync(final TableList<? extends TableRowWithIdAndHeight> list, 
			final String screenId, 
			final String compId,
			final boolean landscape,
			final PrecomputeLayoutCallback callback) {

		Runnable r = new Runnable() {
			@Override
			public void run() {
				
				ArrayList<ViewPosition> viewPositions = new ArrayList<>();
				
				precomputeList = list;
				
				Screen screen = GlobalState.fluidApp.getScreen(screenId);
				
				final double width = GlobalState.fluidApp.getUiService().getScreenWidthInPixels();
				final double height = GlobalState.fluidApp.getUiService().getScreenHeightInPixels();

				String viewPathPrefix = screenId + "." + compId;
				
				Collection<ViewPosition> screenViews = screen.getLayout().getViews(landscape, width, height, viewPathPrefix);
				ViewPosition viewPosition = null;
				for (ViewPosition vp : screenViews) {
					if (vp.getId().equals(compId)) {
						viewPosition = vp;
						break;
					}
				}
				if (viewPosition == null) {
					throw new RuntimeException("Unable to find " + screenId + "." + compId);
				}
				
				ViewBehaviorTable vb = (ViewBehaviorTable) viewPosition.getViewBehavior();
				final Layout layout = GlobalState.fluidApp.getLayout(vb.getRowProvider().getRowLayout());				
				
				viewPathPrefix += "." + layout.getId();
				
				// TODO: decouple tablewithid from precomputewithid
				for (TableRowWithIdAndHeight rowWithId : list.getRows()) {
				
					String viewPathPrefixView = viewPathPrefix + "|" + rowWithId.getFluidTableRowObjectId();
					String precomputePrefix = dataModelIdForPrecomputeList + "." + rowWithId.getFluidTableRowObjectId();

					precomputeViewPositionsFor(layout, landscape, viewPathPrefixView, precomputePrefix,
							viewPosition, rowWithId, viewPositions);
				}
				if (callback != null) {
					callback.computeFinished(viewPositions);
				}
			}
		};
		computeService.execute(r);
		
	}
	
	public void setTableRowsToPrecomputedHeights(final TableList<? extends TableRowWithIdAndHeight> list, 
			final String screenId, 
			final String compId,
			final boolean landscape,
			final SetPrecomputedHeightsCallback callback) {

		Runnable r = new Runnable() {
			@Override
			public void run() {
				
				TableList<TableRowWithIdAndHeight> rowsWithoutPrecomputedHeights = new TableList<>();
				
				Screen screen = GlobalState.fluidApp.getScreen(screenId);
				
				final double width = GlobalState.fluidApp.getUiService().getScreenWidthInPixels();
				final double height = GlobalState.fluidApp.getUiService().getScreenHeightInPixels();
				
				String viewPathPrefix = screenId + "." + compId;
		
				Collection<ViewPosition> screenViews = screen.getLayout().getViews(landscape, width, height, viewPathPrefix);
				ViewPosition viewPosition = null;
				for (ViewPosition vp : screenViews) {
					if (vp.getId().equals(compId)) {
						viewPosition = vp;
						break;
					}
				}
				if (viewPosition == null) {
					throw new RuntimeException("Unable to find " + screenId + "." + compId);
				}
		
				ViewBehaviorTable vb = (ViewBehaviorTable) viewPosition.getViewBehavior();
				final Layout layout = GlobalState.fluidApp.getLayout(vb.getRowProvider().getRowLayout());				
		
				viewPathPrefix += "." + layout.getId();
				
				for (TableRowWithIdAndHeight rowWithId : list.getRows()) {
					
					String viewPathPrefixView = viewPathPrefix + "|" + rowWithId.getFluidTableRowObjectId();
		
					ViewPosition vp = getViewPosition(viewPathPrefixView);
					if (vp == null) {
						rowsWithoutPrecomputedHeights.add(rowWithId);
						//throw new RuntimeException("View path must be precomputed " + viewPathPrefixView);
						continue;
					}
					
					rowWithId.setFluidComputedHeight(vp.getHeight());
				}
			
				if (callback != null) {
					callback.computeFinished(rowsWithoutPrecomputedHeights);
				}
			}
		};
		computeService.execute(r);
	}
	
	public static interface PrecomputeLayoutCallback {
		
		public void computeFinished(Collection<ViewPosition> viewPositions);
		
	}

	public static interface SetPrecomputedHeightsCallback {
		
		public void computeFinished(TableList<? extends TableRowWithIdAndHeight> rowsWithoutPrecomputedHeights);
		
	}

	@Override
	public void initialize(FluidApp app) {
		app.getDataModelManager().setDataModel(dataModelId, this);
		app.getDataModelManager().cache.dontCacheChainStartingWith(dataModelId);
	}

	@Override
	public String[] getSupportedPlatforms() {
		return null;
	}

	public static double precomputeViewPositionsFor(Layout layout, 
			boolean landscape,
			String viewPathPrefix, 
			String precomputePrefix,
			ViewPosition viewPosition,
			TableRowWithIdAndHeight rowWithId,
			Collection<ViewPosition> newViewPositions) {
				
		if (!layout.isPrecomputedPositions()) {
			throw new RuntimeException("Expecting precompute positions to be true for layout " + layout.getId());
		}
		
		double calcHeight = layout.calculateHeight(false, (float) viewPosition.width, precomputePrefix, false);
		
		if (rowWithId != null) {
			// Only for the row that is passed into computeAsync
			rowWithId.setFluidComputedHeight(calcHeight);
			ViewPosition rowWithIdHeight = new ViewPosition(viewPathPrefix,
					(int) Math.round(calcHeight));
			GlobalState.fluidApp.getPrecomputeLayoutManager().setViewPosition(viewPathPrefix, rowWithIdHeight);
			newViewPositions.add(rowWithIdHeight);
		}
		
		Collection<ViewPosition> views = layout.layout(landscape, viewPosition.width, calcHeight, precomputePrefix, false, false, false, viewPathPrefix);
		for (ViewPosition view : views) {
			
			newViewPositions.add(view);
			
			String viewPathPrefixView = DataModelManager.getFullKey(viewPathPrefix, view.getId());
			
			GlobalState.fluidApp.getPrecomputeLayoutManager().setViewPosition(viewPathPrefixView, view);
			
			// If the ViewBehavior has something to compute - eg. Subview and SubviewRepeat
			view.getViewBehavior().precomputeViewPositions(landscape, precomputePrefix, view,
					viewPathPrefixView, newViewPositions);
		}
		
		return calcHeight;
	}
	
}
