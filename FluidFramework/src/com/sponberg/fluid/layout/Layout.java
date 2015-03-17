package com.sponberg.fluid.layout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.sponberg.fluid.FluidApp;
import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.util.KeyValueList;
import com.sponberg.fluid.util.LRUCache;
import com.sponberg.fluid.util.Logger;

@ToString
@Getter
@Setter
@EqualsAndHashCode
public class Layout implements LRUCache.RemovedListener<String, Layout.LastLayout> {

	public enum Direction { RIGHT, LEFT }

	public enum Align { TOP, BOTTOM, LEFT, CENTER, RIGHT, UNASSIGNED }
	
	private final String name;
	
	private final String nameKey;
	
	private String subtitle;
	
	private String subtitleKey;
	
	private final String id;
	
	private Color backgroundColor;
	
	private boolean precomputedPositions = false;
	
	private boolean wrapInScrollView = false;
	
	private boolean blockFocusViewOnLoad = false;
	
	LastLayout lastLayout = new LastLayout();

	LRUCache<String, LastLayout> viewCache = new LRUCache<>(100); // Each layout can have 100 copies of its views for different datamodelprefix
	
	private int spacerId = 0;
	
	Orientation portrait = new Orientation(false);
	
	Orientation landscape = null;
	
	Orientation currentOrientation = portrait;
	
	LinkedHashSet<View> allViews = new LinkedHashSet<>();
	
	ArrayList<View> allViewsToBePresentedToUI = null;
	
	HashMap<String, View> viewMap = new HashMap<>();
		
	HashMap<String, KeyValueList> propertiesByCategory = new HashMap<>();
	
	View lastLeftEdge;
	
	View lastRightEdge;
	
	View last;
	
	double baseUnit;
	
	boolean isInLandscape;

	public Layout(String id, String name, String nameKey, double baseUnit) {
		this.name = name;
		this.nameKey = nameKey;
		this.id = id;
		this.baseUnit = baseUnit;
		this.viewCache.addRemovedListener(this);
	}

	public Collection<View> getAllViews() {
		return allViews;
	}

	public Collection<View> getAllViewsToBePresentedToUI() {
		
		if (allViewsToBePresentedToUI == null) {		
			allViewsToBePresentedToUI = new ArrayList<>();
			for (View view: allViews) {
				if (view.getViewBehavior().isShouldBePresentedToUI()) {
					allViewsToBePresentedToUI.add(view);
				}
			}
		}
		return allViewsToBePresentedToUI;
	}
	
	public void setAnchor(View anchor, Direction direction) {
		currentOrientation.currentLayer.hasAnchor = true;
		currentOrientation.setDirectionOf(anchor, direction);
		lastLeftEdge = anchor;
		lastRightEdge = anchor;
		addView(anchor);
		setupDynamicCoords(anchor);
	}
	
	public void setProperties(List<? extends KeyValueList> properties) {
		this.propertiesByCategory = new HashMap<>();
		if (properties != null) {
			for (KeyValueList kvl : properties) {
				propertiesByCategory.put(kvl.getValue(), kvl);
			}
		}
	}
	
	public String getProperty(String category, String property) {
		KeyValueList kvl = this.propertiesByCategory.get(category);
		if (kvl != null && kvl.contains(property)) {
			return kvl.getValue(property);
		} else {
			return null;
		}
	}
	
	void addView(final View view) {
		if (!currentOrientation.currentLayer.hasAnchor) {
			throw new RuntimeException("Anchor not set yet");
		}
		currentOrientation.setHorizontalChain(view);
		currentOrientation.currentLayer.views.add(view);
		if (currentOrientation.currentLayer.horizontalChain == currentOrientation.currentLayer.horizontalChains.size()) {
			currentOrientation.currentLayer.horizontalChains.add(new ArrayList<View>());
		}
		currentOrientation.currentLayer.horizontalChains.get(currentOrientation.currentLayer.horizontalChain).add(view);
		viewMap.put(view.id, view);
		allViews.add(view);
		last = view;
	}

	private void setupDynamicCoords(final View view) {
		// Relative coordinates must be listed in the layout after the reference layout
		// .. which should be ok since they are on a higher layer
		if (view.getGivenConstraints().getX() != null && view.getGivenConstraints().getX().isRelativeToView()) {
			final View source = viewMap.get(view.getGivenConstraints().getX().getRelativeId());
			if (view.getGivenConstraints().getX().getRelativeEdge().equalsIgnoreCase("left")) {
				LayoutAction action = new LayoutAction() {
					boolean registered = false;
					@Override
					public void run() {
						double x = source.getX();
						Double size = subtractorViewsWidth(view.getGivenConstraints().getX().subtractors, true);
						if (size == null) {
							if (!registered) {
								registerDynamicCoordRelativeHeight(view, this);
								registered = true;
							}
							return;
						}
						x -= size;
						view.setX(x);						
					}
				};
				source.addActionX(action);
			} else {
				LayoutAction action = new LayoutAction() {
					boolean registered = false;
					@Override
					public void run() {
						double x = source.getX2();
						Double size = subtractorViewsWidth(view.getGivenConstraints().getX().subtractors, true);
						if (size == null) {
							if (!registered) {
								registerDynamicCoordRelativeHeight(view, this);
								registered = true;
							}
							return;
						}
						x -= size;
						view.setX(x);						
					}
				};
				source.addActionX2(action);
			}
		}
		if (view.getGivenConstraints().getX2() != null && view.getGivenConstraints().getX2().isRelativeToView()) {
			final View source = viewMap.get(view.getGivenConstraints().getX2().getRelativeId());
			if (view.getGivenConstraints().getX2().getRelativeEdge().equalsIgnoreCase("left")) {
				LayoutAction action = new LayoutAction() {
					boolean registered = false;
					@Override
					public void run() {
						double x = source.getX();
						Double size = subtractorViewsWidth(view.getGivenConstraints().getX2().subtractors, true);
						if (size == null) {
							if (!registered) {
								registerDynamicCoordRelativeHeight(view, this);
								registered = true;
							}
							return;
						}
						x -= size;
						view.setX2(x);						
					}
				};
				source.addActionX(action);
			} else {
				LayoutAction action = new LayoutAction() {
					boolean registered = false;
					@Override
					public void run() {
						double x = source.getX2();
						
						Double size = subtractorViewsWidth(view.getGivenConstraints().getX2().subtractors, true);
						if (size == null) {
							if (!registered) {
								registerDynamicCoordRelativeHeight(view, this);
								registered = true;
							}
							return;
						}
						x -= size;
						view.setX2(x);						
					}
				};
				source.addActionX2(action);
			}
		}
		if (view.getGivenConstraints().getY() != null && view.getGivenConstraints().getY().isRelativeToView()) {
			final View source = viewMap.get(view.getGivenConstraints().getY().getRelativeId());
			if (view.getGivenConstraints().getY().getRelativeEdge().equalsIgnoreCase("top")) {
				LayoutAction action = new LayoutAction() {
					boolean registered = false;
					@Override
					public void run() {
						double y = source.getY();
						Double size = subtractorViewsHeight(view.getGivenConstraints().getY().subtractors, true);
						if (size == null) {
							if (!registered) {
								registerDynamicCoordRelativeHeight2(view, this);
								registered = true;
							}
							return;
						}
						y -= size;
						view.setY(y);
					}
				};
				source.addActionY(action);
			} else {
				LayoutAction action = new LayoutAction() {
					boolean registered = false;
					@Override
					public void run() {
						double y = source.getY2();
						Double size = subtractorViewsHeight(view.getGivenConstraints().getY().subtractors, true);
						if (size == null) {
							if (!registered) {
								registerDynamicCoordRelativeHeight2(view, this);
								registered = true;
							}
							return;
						}
						y -= size;
						view.setY(y);						
					}
				};
				source.addActionY2(action);
			}
		}
		if (view.getGivenConstraints().getY2() != null && view.getGivenConstraints().getY2().isRelativeToView()) {
			final View source = viewMap.get(view.getGivenConstraints().getY2().getRelativeId());
			if (view.getGivenConstraints().getY2().getRelativeEdge().equalsIgnoreCase("top")) {
				LayoutAction action = new LayoutAction() {
					boolean registered = false;
					@Override
					public void run() {
						double y = source.getY();
						Double size = subtractorViewsHeight(view.getGivenConstraints().getY2().subtractors, true);
						if (size == null) {
							if (!registered) {
								registerDynamicCoordRelativeHeight2(view, this);
								registered = true;
							}
							return;
						}
						y -= size;
						view.setY2(y);						
					}
				};
				source.addActionY(action);
			} else {
				LayoutAction action = new LayoutAction() {
					boolean registered = false;
					@Override
					public void run() {
						double y = source.getY2();
						Double size = subtractorViewsHeight(view.getGivenConstraints().getY2().subtractors, true);
						if (size == null) {
							if (!registered) {
								registerDynamicCoordRelativeHeight2(view, this);
								registered = true;
							}
							return;
						}
						y -= size;
						view.setY2(y);						
					}
				};
				source.addActionY2(action);
			}
		}
	}
	
	private void registerDynamicCoordRelativeHeight(View view, LayoutAction action) {
		// setupDynamicCoords is called during layout.reset, therefore the widths
		// and heights of relative views aren't known yet. When they are, then
		// apply the action to set the relative coord (x or y)
		for (Subtractor subtractor : view.getGivenConstraints().getX().subtractors) {
			if (subtractor.isRelativeToView()) {
				String viewId = subtractor.relativeToView;
				View v = viewMap.get(viewId);
				if (v == null || v.getWidth() == null) {
					v.addActionWidth(action);
				}
			}
		}
	}
	
	private void registerDynamicCoordRelativeHeight2(View view, LayoutAction action) {
		// setupDynamicCoords is called during layout.reset, therefore the widths
		// and heights of relative views aren't known yet. When they are, then
		// apply the action to set the relative coord (x or y)		
		for (Subtractor subtractor : view.getGivenConstraints().getY().subtractors) {
			if (subtractor.isRelativeToView()) {
				String viewId = subtractor.relativeToView;
				View v = viewMap.get(viewId);
				if (v == null || v.getHeight() == null) {
					v.addActionHeight(action);
				}
			}
		}
	}
	
	public void addNewLayer(String visibleCondition) {
		addNewLayer(visibleCondition, currentOrientation.layers.size());
	}

	public void addNewLayer(String visibleCondition, int zIndex) {
		for (Orientation.Layer layer : currentOrientation.layers) {
			if (layer.zIndex == zIndex) {
				throw new RuntimeException("There is already a layer with the z-index " + zIndex);
			}
		}
		currentOrientation.currentLayer = new Orientation.Layer(zIndex);
		currentOrientation.currentLayer.visibleCondition = visibleCondition;
		currentOrientation.layers.add(currentOrientation.currentLayer);
	}
		
	public void addRight(final View view, Align vAlign) {
		currentOrientation.setDirectionOf(view, Direction.RIGHT);
		
		final View source = last;
		source.addActionX2(new LayoutAction() {
			@Override
			public void run() {
				view.setX(source.getX2());
			}
		});
		
		if (vAlign == Align.TOP) {
			source.addActionY(new LayoutAction() {
				@Override
				public void run() {
					view.setY(source.getY());
				}
			});				
		} else {
			if (true) {
				throw new RuntimeException("Not implemented");
			}
			/*
			source.addActionY2(new LayoutAction() {
				public void run() {
					view.setY2(source.getY2());
				}
			});*/
		}
		
		lastRightEdge = view;
		addView(view);
	}
	
	public void addLeft(final View view, Align vAlign) {

		currentOrientation.setDirectionOf(view, Direction.LEFT);

		final View source = last;
		source.addActionX(new LayoutAction() {
			@Override
			public void run() {
				view.setX2(source.getX());
			}
		});

		if (vAlign == Align.TOP) {
			source.addActionY(new LayoutAction() {
				@Override
				public void run() {
					view.setY(source.getY());
				}
			});
		} else {
			if (true) {
				throw new RuntimeException("Not implemented");
			}
			/*
			source.addActionY2(new LayoutAction() {
				public void run() {
					view.setY2(source.getY2());
				}
			});*/
		}
		
		lastLeftEdge = view;
		addView(view);
	}
	
	public void addDown(final View view, Align align, Direction direction) {
		
		currentOrientation.currentLayer.horizontalChain++;
		
		currentOrientation.setDirectionOf(view, direction);
		
		final View source;
		if (direction == Direction.RIGHT) {
			source = lastLeftEdge;
		} else {
			source = lastRightEdge;
		}
		
		source.addActionY2(new LayoutAction() {
			@Override
			public void run() {
				view.setY(source.getY2());
			}
		});

		if (align == Align.LEFT) {
			source.addActionX(new LayoutAction() {
				@Override
				public void run() {
					view.setX(source.getX());
				}
			});
		} else if (align == Align.CENTER) {
			source.addActionX(new LayoutAction() {
				@Override
				public void run() {
					if (view.getWidth() != null && source.getWidth() != null) {
						double middle = source.x + source.getWidth() / 2;
						double viewX = middle - view.getWidth() / 2;
						view.setX(viewX);
					} else if (source.getWidth() != null) {
						double middle = source.x + source.getWidth() / 2;
						view.setMiddleX(middle);
					}
				}
			});
			source.addActionX2(new LayoutAction() {
				@Override
				public void run() {
					if (view.getWidth() != null && source.getWidth() != null) {
						double middle = source.x2 - source.getWidth() / 2;
						double viewX = middle - view.getWidth() / 2;
						view.setX(viewX);
					} else if (source.getWidth() != null) {
						double middle = source.x + source.getWidth() / 2;
						view.setMiddleX(middle);
					}
				}
			});
		} else {
			source.addActionX2(new LayoutAction() {
				@Override
				public void run() {
					view.setX2(source.getX2());
				}
			});
		}
		
		lastLeftEdge = view;
		lastRightEdge = view;
		addView(view);
	}
	
	public synchronized Collection<ViewPosition> getViews(boolean landscape, double width, double height, String viewPath) {
		return getViews(landscape, width, height, null, viewPath);
	}
	
	public synchronized Collection<ViewPosition> getViews(boolean landscape, double width, double height, String dataModelPrefix, String viewPath) {
		return getViews(landscape, width, height, dataModelPrefix, viewPath, false);
	}
	
	public synchronized void invalidateCache(String dataModelPrefix) {
		
		final String cacheId = (dataModelPrefix == null) ? "default" : dataModelPrefix;
		viewCache.remove(cacheId);
	}
	
	private synchronized Collection<ViewPosition> getViews(boolean landscape, 
			final double width, 
			final double height, 
			final String dataModelPrefix, 
			final String viewPath, 
			final boolean printTable) {
		
		this.isInLandscape = landscape;
		
		if (!FluidApp.useCaching && precomputedPositions) {
			ArrayList<ViewPosition> viewPositions = new ArrayList<>();
			for (View view : getAllViewsToBePresentedToUI()) {
				ViewPosition vp = GlobalState.fluidApp.getPrecomputeLayoutManager().getViewPosition(viewPath + "." + view.getId());
				if (vp == null) {
					throw new RuntimeException("view must be in PrecomputeLayoutManager if parent is precompute-positions: " + viewPath + " " + view.getId());
				}
				vp.setViewBehavior(view.getViewBehavior());
				viewPositions.add(vp);
			}
			return viewPositions;
		} else if (!FluidApp.useCaching) {
			return layout(landscape, width, height, dataModelPrefix, printTable, false, false, viewPath);
		} else {
			final String cacheId = (dataModelPrefix == null) ? "default" : dataModelPrefix;
			LastLayout lastLayout = viewCache.get(cacheId);
			boolean lastLayoutCreated = false;
			if (lastLayout == null) {
				lastLayout = new LastLayout();
				lastLayoutCreated = true;
				viewCache.put(cacheId, lastLayout);
			}
			if (lastLayout.width != width || lastLayout.height != height) {
				
				if (precomputedPositions) {
					lastLayout.views = new ArrayList<>();
					
					if (lastLayoutCreated && conditionalKeys == null) {
						reset(baseUnit, dataModelPrefix, width);
					}
					
					boolean precomputeNotAvailable = false;
					for (View view : getAllViewsToBePresentedToUI()) {
						ViewPosition vp = GlobalState.fluidApp.getPrecomputeLayoutManager().getViewPosition(viewPath + "." + view.getId());
						
						if (vp == null) {
							precomputeNotAvailable = true;
							break;
						}
							
						vp.setViewBehavior(view.getViewBehavior());
						
						// To populate conditionalKeys
						if (lastLayoutCreated && conditionalKeys == null) {
							checkViewCondition(vp.visibleCondition, dataModelPrefix);
						}
						
						lastLayout.views.add(vp);
					}
					
					if (precomputeNotAvailable) {

						Logger.debug(this, "View is using precompute, but it is not available {}", viewPath);
						lastLayout.views = layout(landscape, width, height, dataModelPrefix, printTable, false, true, viewPath);
						lastLayout.width = width;
						lastLayout.height = height;				
					}
						
				} else {
					lastLayout.views = layout(landscape, width, height, dataModelPrefix, printTable, false, true, viewPath);
					lastLayout.width = width;
					lastLayout.height = height;				
				}
				
				if (lastLayoutCreated) {
					for (final String conditionalKey : conditionalKeys) {
						GlobalState.fluidApp.getDataModelManager().addDataChangeListener(conditionalKey, 
								this.getId() + "-conditional-" + cacheId, 
								new DataChangeListener() {
									@Override
									public void dataChanged(String key, String... subKeys) {
										viewCache.remove(cacheId);
									}
									@Override
									public void dataRemoved(String key) {
										viewCache.remove(cacheId);
									}
								});
					}
				}
			}
			return lastLayout.views;
		}
	}
	
	public static synchronized Collection<ViewPosition> getViewPositions(String viewPath, Collection<View> views) {
		ArrayList<ViewPosition> viewPositions = new ArrayList<>();
		for (View view : views) {
			viewPositions.add(new ViewPosition(viewPath, view));
		}
		return viewPositions;
	}
	
	public synchronized void createOrientationLandscape() {
		landscape = new Orientation(true);		
	}
	
	public synchronized void setOrientationLandscape(boolean isLandscape) {
		
		// hstdbc only perform if it changed
		
		// if landscape is null, the current orientation will always be portrait
		if (landscape != null) {
			currentOrientation = (isLandscape) ? landscape : portrait;
		}
		sortAllViewsAccordingToLayerZIndex();
		currentOrientation.setViewsOrienation();
	}
	
	private void sortAllViewsAccordingToLayerZIndex() {
		
		ArrayList<Orientation.Layer> layers = new ArrayList<>();
		for (Orientation.Layer l : currentOrientation.layers) {
			layers.add(l);
		}
		
		Collections.sort(layers, new Comparator<Orientation.Layer>() {
			@Override
			public int compare(Orientation.Layer o1, Orientation.Layer o2) {
				return o1.zIndex - o2.zIndex;
			}				
		});
		
		allViews = new LinkedHashSet<View>(); 
		for (Orientation.Layer layer : layers) {
			for (View view : layer.views) {
				allViews.add(view);
			}
		}
		allViewsToBePresentedToUI = null;
		getAllViewsToBePresentedToUI();
	}
	
	// dataModelPrefix needed in the case of compute height
	protected synchronized ArrayList<ViewPosition> layout(boolean landscape, 
			double width, 
			double height, 
			String dataModelPrefix, 
			boolean printTable, 
			boolean computingHeight, 
			boolean useComputingHeightCache,
			String viewPath) {
		setOrientationLandscape(landscape);
		reset(baseUnit, dataModelPrefix, width);
		setWidths(width, dataModelPrefix);
		setHeights(height, computingHeight, dataModelPrefix, useComputingHeightCache);
		if (printTable) {
			printTable();
		}
		
		if (viewPath == null) {
			return null;
		}
		
		ArrayList<ViewPosition> viewPositions = new ArrayList<>();
		for (View view : getAllViewsToBePresentedToUI()) {
			viewPositions.add(new ViewPosition(viewPath, view));
		}
		return viewPositions;
	}
	
	private void setWidths(double width, String dataModelPrefix) {
		
		for (Orientation.Layer layer : currentOrientation.layers) {
			setWidthsHelper(width, layer, dataModelPrefix);
		}
	}
	
	private void setWidthsHelper(double width, Orientation.Layer layer, String dataModelPrefix) {
		
		// Some views width can be determined on the first pass, such as view width relative to other views
		// Other views, such as equals or fill, need all the other views in that horizontal chain to be
		// determined first. We'll determine the width of these views on the second pass.
		ArrayList<View> viewsForSecondPass = new ArrayList<>();
		
		int horizontalChain = 0;
		for (View view : layer.views) {
			
			if (currentOrientation.getHorizontalChainOf(view) != horizontalChain) {
				for (View v : viewsForSecondPass) {
					if (v.width == null) {
						computeWidthSecondPass(v, width, layer);
					}
				}
				viewsForSecondPass.clear();
				horizontalChain = currentOrientation.getHorizontalChainOf(view);
			}
			
			if (view.width != null) {
				continue;
			}
			
			if (!computeWidth(view, width, dataModelPrefix)) {
				viewsForSecondPass.add(view);
			}
		}

		for (View v : viewsForSecondPass) {
			if (v.width == null) {
				computeWidthSecondPass(v, width, layer);
			}
		}
	}

	private boolean computeWidth(View view, double width, String dataModelPrefix) {
	
		if (view.getGivenConstraints().width.relativeToView()) {
			View v = viewMap.get(view.getGivenConstraints().width.getRelativeId());
			if (v.getWidth() == null) {
				throw new RuntimeException("Width can't be resolved for " + view + " from " + v);
			}
			double w = v.getWidth() * view.getGivenConstraints().width.getRatio();
			w -= subtractorViewsWidth(view.getGivenConstraints().width.getSubtractors(), false);
			view.setWidth(w);
		} else if (view.getGivenConstraints().width.relativeToParent()) {
			double w = width * view.getGivenConstraints().width.getRatio();
			w -= subtractorViewsWidth(view.getGivenConstraints().width.getSubtractors(), false);			
			view.setWidth(w);
		} else if (view.getGivenConstraints().width.relativeToRow()) {
			throw new RuntimeException("Width relative to row is not supported");
		} else if (view.getGivenConstraints().width.summation()) {
			double sum = 0;
			for (String id : view.getGivenConstraints().width.getSummationOf()) {
				View v = viewMap.get(id);
				if (v == null || v.getWidth() == null) {
					throw new RuntimeException("Error computing width for " + view.getId() + ". Width unknown for " + v.getId());
				}
				sum += v.getWidth() * baseUnit;
			}
			view.setWidth(sum);
		} else if (view.getGivenConstraints().width.isFromDataModel()) {
			String widthString = GlobalState.fluidApp.getDataModelManager().getValue(dataModelPrefix, 
					view.getGivenConstraints().width.getDataModelKey(), "{0}", null);
			if (widthString.equals("nan")) {
				view.setWidth(0d);
			} else {
				view.setWidth(GlobalState.fluidApp.sizeToPixels(widthString));
			}
		} else {
			return false;
		}
		
		return true;
	}
		
	private void computeWidthSecondPass(View view, double width, Orientation.Layer layer) {
		
		int horizontalChain = currentOrientation.getHorizontalChainOf(view);
		
		ArrayList<View> views = new ArrayList<>(layer.horizontalChains.get(horizontalChain));
		
		double remaining = getRemainingWidthCheckingEarlierChain(views, horizontalChain, width, layer);
		
		Direction direction = currentOrientation.getDirectionOf(view);
		
		Double firstMaxWidth = null;
		boolean firstIsMiddleAligned = false;
		View first = views.get(0);
		if (direction == Direction.RIGHT) {
			if (first.getX() != null) {
				// left aligned
				remaining -= first.getX();
			} else if (first.getX2() != null) {
				// right aligned
				if (first.getWidth() != null) {
					remaining -= first.getX2();
				} else {
					firstMaxWidth = first.getX2();
				}
			} else {
				// center aligned
				if (first.getWidth() != null) {
					remaining -= first.getX2();
				} else {
					remaining -= first.getMiddleX(); // We'll claim the width to the middle now, and account for this offset below
					firstIsMiddleAligned = true;
					firstMaxWidth = first.getMiddleX() * 2;
				}
			}
		} else {
			// direction  == Direction.LEFT
			if (first.getX2() != null) {
				// right aligned
				remaining -= (width - first.getX2());
			} else if (first.getX() != null) {
				// left aligned
				if (first.getWidth() != null) {
					remaining -= (width - first.getX());
				} else {
					firstMaxWidth = width - first.getX();
				}
			} else {
				// center aligned
				if (first.getWidth() != null) {
					remaining -= (width - first.getX());
				} else {
					remaining -= (width - first.getMiddleX()); // We'll claim the width to the middle now, and account for this offset below
					firstIsMiddleAligned = true;
					firstMaxWidth = (width -  first.getMiddleX()) * 2;
				}
			}
		}
		
		for (Iterator<View> i = views.iterator(); i.hasNext();) {
			View v = i.next();
			if (v.width != null) {
				if (v == view) {
					throw new RuntimeException("Not expecting a view with a width passed in");
				}
				remaining -= v.width;
				i.remove();
			}
		}
		
		// If the remaining ones are equals or fill, then set them
		boolean allAreEqualsOrFill = true;
		for (View v : views) {
			if (!v.getGivenConstraints().width.equal() && !v.getGivenConstraints().width.fill()) {
				allAreEqualsOrFill = false;
				break;
			}
		}
		if (allAreEqualsOrFill) {
			double widthForEach;
			if (firstIsMiddleAligned) {
				widthForEach = remaining / (views.size() - .5);
			} else {
				widthForEach = remaining / views.size();
			}
			if (firstMaxWidth != null && widthForEach > firstMaxWidth) {
				widthForEach = firstMaxWidth;
			}				
				
			for (View v : views) {
				v.setWidth(widthForEach);
			}
			return;
		}
		
		// If the remaining ones are length fill ratio
		boolean allAreFillRatio = true;
		double totalRatio = 0;
		for (View v : views) {
			if (!v.getGivenConstraints().width.fillRatio()) {
				allAreFillRatio = false;
				break;
			}
			totalRatio += v.getGivenConstraints().width.getRatio();
		}
		if (allAreFillRatio) {
			if (totalRatio != 1) {
				StringBuilder buf = new StringBuilder("Fill ratio must add up to 1 for:\n ");
				for (View v : views) {
					buf.append(v + "\n ");
				}
				throw new RuntimeException(buf.toString());
			}
			double denom = 1;
			if (firstIsMiddleAligned) {
				denom = 1 - first.getGivenConstraints().width.getRatio() / 2;
				double newFirstRatio = first.getGivenConstraints().width.getRatio() / denom;
				double widthForFirst = remaining * newFirstRatio;
				if (widthForFirst > firstMaxWidth) {
					double applyRatio = firstMaxWidth / widthForFirst;
					denom /= applyRatio;
				}				
			}
			
			for (View v : views) {
				double ratio = v.getGivenConstraints().width.getRatio() / denom;
				v.setWidth(remaining * ratio);
			}
			return;
		}
		
		// If last element in chain, everything else in
		// chain has been counted, it can claim the remaining
		boolean last = views.get(views.size() - 1) == view;
		
		views.remove(view);
		
		if (last
				&& views.size() == 0
				&& view.getGivenConstraints().width.fill()) {
			view.setWidth(remaining);
			return;
		}		
		
		throw new RuntimeException("Can't resolve width for " + view);
	}
	
	private double getRemainingWidthCheckingEarlierChain(ArrayList<View> viewsInChain, int horizontalChain, double width, Orientation.Layer layer) {
		
		// TODO: perhaps this should have vertical slices instead of a wide slice
		Double minY = null;
		for (View view: viewsInChain) {
			if (view.getY() != null) {
				if (minY == null) {
					minY = view.getY();
				} else {
					minY = Math.min(minY.doubleValue(), view.getY());
				}
			}
		}
		
		if (minY == null) {
			// Since height in the earlier chain is unknown, it is dynamic. Therefore, let this chain
			// claim the whole space.
			return width;
		}
		
		double remaining = width;
		for (View v : layer.views) {
			if (currentOrientation.getHorizontalChainOf(v) == horizontalChain) {
				break;
			}
			if (v.getY2() != null && minY < v.getY2()) {
				// If y2 is null, then it is dynamic. So let this claim all the space.
				remaining -= v.getWidth();
			}
		}
		
		return remaining;
	}
	
	private void setHeights(double height, boolean computingHeight, String dataModelPrefix, boolean useComputingHeightCache) {
		for (Orientation.Layer layer : currentOrientation.layers) {
			setHeightsHelper(height, layer, computingHeight, dataModelPrefix, useComputingHeightCache);
		}
	}

	private void setHeightsHelper(double height, Orientation.Layer layer, boolean computingHeight, String dataModelPrefix, boolean useComputingHeightCache) {
	
		// Some views width can be determined on the first pass, such as view width relative to other views
		// Other views, such as equals or fill, need all the other views in that horizontal chain to be
		// determined first. We'll determine the width of these views on the second pass.
		ArrayList<View> viewsForSecondPass = new ArrayList<>();
		
		for (View view : layer.views) {
			if (view.height != null) {
				continue;
			}
			if (!computeHeight(view, height, dataModelPrefix, useComputingHeightCache)) {
				viewsForSecondPass.add(view);
			}
		}
		
		// If the remaining ones are equals or fill, then set them
		boolean allAreFill = true;
		for (View v : viewsForSecondPass) {
			if (!v.getGivenConstraints().height.fill()) {
				allAreFill = false;
				break;
			}
		}

		if (viewsForSecondPass.size() == 0) {
			return;
		}
		
		if (allAreFill) {
			
			if (computingHeight) {
				// If we are computing height, then anything dynamic gets zero
				// In the future, we could add a minimum height and let dynamic height get some of that
				for (View view : viewsForSecondPass) {
					view.setHeight(0d);
				}
				return;
			} 
			
			double remainingHeight = getRemainingHeight(viewsForSecondPass.get(0), height, layer);
			
			double heightForEach;
			heightForEach = remainingHeight / viewsForSecondPass.size();
				
			for (View v : viewsForSecondPass) {
				v.setHeight(heightForEach);
			}
		} else {
			for (View view : viewsForSecondPass) {
				computeHeightSecondPass(view, height, layer, computingHeight);
			}
		}
	}
	
	private boolean computeHeight(View view, double height, String dataModelPrefix, boolean useComputingHeightCache) {
		if (view.getGivenConstraints().height.relativeToView()) {
			View v = viewMap.get(view.getGivenConstraints().height.getRelativeId());
			if (v.getHeight() == null) {
				throw new RuntimeException("Height can't be resolved for " + view + " from " + v);
			}
			double calcHeight = v.getHeight() * view.getGivenConstraints().height.getRatio(); 
			calcHeight -= subtractorViewsHeight(view.getGivenConstraints().height.subtractors, false);
			view.setHeight(calcHeight);
		} else if (view.getGivenConstraints().height.relativeToParent()) {
			double calcHeight = (height * view.getGivenConstraints().height.getRatio());
			calcHeight -= subtractorViewsHeight(view.getGivenConstraints().height.subtractors, false);
			view.setHeight(calcHeight);
		} else if (view.getGivenConstraints().height.fill()) {
			return false;
		} else if (view.getGivenConstraints().height.relativeToRow()) {
			return false;
		} else if (view.getGivenConstraints().height.relativeToLayer()) {
			double layerHeight = heightForLayer(view.getGivenConstraints().height.getLayerIndex());
			layerHeight -= subtractorViewsHeight(view.getGivenConstraints().height.subtractors, false);
			view.setHeight(layerHeight * view.getGivenConstraints().height.getRatio());
		} else if (view.getGivenConstraints().height.compute()) {
			double computedHeight = view.getViewBehavior().computeHeight(isInLandscape, dataModelPrefix, view, useComputingHeightCache);
			computedHeight -= subtractorViewsHeight(view.getGivenConstraints().height.subtractors, false);
			view.setHeight(computedHeight);
		} else if (view.getGivenConstraints().height.isFromDataModel()) {
			String heightString = GlobalState.fluidApp.getDataModelManager().getValue(dataModelPrefix, 
					view.getGivenConstraints().height.getDataModelKey(), "{0}", null);
			if (heightString.equals("nan")) {
				view.setHeight(0d);
			} else {
				view.setHeight(GlobalState.fluidApp.sizeToPixels(heightString));
			}
		} else {
			throw new RuntimeException("Can't resolve height for " + view);
		}
		return true;
	}
	
	private Double subtractorViewsWidth(ArrayList<Subtractor> subtractors, boolean setingUpDynamicCoords) {
		double w = 0;
		for (Subtractor subtractor : subtractors) {
			if (!subtractor.isRelativeToView()) {
				w += subtractor.getFixed();
			} else {
				String viewId = subtractor.relativeToView;
				View v = viewMap.get(viewId);
				if (v == null || v.getWidth() == null) {
					if (setingUpDynamicCoords) {
						return null;
					} else {
						throw new RuntimeException("Error computing width for " + v.getId() + ". Width unknown for " + viewId);
					}
				}
				w += subtractor.ratioRelativeToView * v.getWidth();
			}
		}
		return w;
	}
	
	private Double subtractorViewsHeight(ArrayList<Subtractor> subtractors, boolean setingUpDynamicCoords) {
		double w = 0;
		for (Subtractor subtractor : subtractors) {
			if (!subtractor.isRelativeToView()) {
				w += subtractor.getFixed();
			} else {
				String viewId = subtractor.relativeToView;
				View v = viewMap.get(viewId);
				if (v == null || v.getHeight() == null) {
					if (setingUpDynamicCoords) {
						return null;
					} else {
						throw new RuntimeException("Error computing height for " + v.getId() + ". Height unknown for " + viewId);
					}
				}
				w += subtractor.ratioRelativeToView * v.getHeight();
			}
		}
		return w;
	}
	
	private double heightForLayer(int layerIndex) {
		Orientation.Layer layer = this.currentOrientation.layers.get(layerIndex);
		double maxHeight = 0;
		for (View v : layer.views) {
			double height = v.getY2();
			if (height > maxHeight) {
				maxHeight = height;
			}
		}
		return maxHeight;
	}
	
	private boolean computeHeightSecondPass(View view, double height, Orientation.Layer layer, boolean computingHeight) {
		if (view.getGivenConstraints().height.fill()) {
			if (computingHeight) {
				// If we are computing height, then anything dynamic gets zero
				// In the future, we could add a minimum height and let dynamic height get some of that
				view.setHeight(0d);
			} else {
				double remaining = getRemainingHeight(view, height, layer);
				view.setHeight(remaining);
			}
		} else if (view.getGivenConstraints().height.relativeToRow()) {
			double rowHeight = maxHeightForChain(currentOrientation.getHorizontalChainOf(view), layer);
			view.setHeight(rowHeight * view.getGivenConstraints().height.getRatio());
		} else {
			throw new RuntimeException("Can't resolve height for " + view);
		}
		return true;
	}
	
	private double getRemainingHeight(View view, double height, Orientation.Layer layer) {
		
		Double minX = view.getX();
		Double maxX = view.getX2();
		
		if (minX == null || maxX == null) {
			StringBuffer buf = new StringBuffer("Height can't be resolved for:\n ");
			buf.append(view + " ");
			throw new RuntimeException(buf.toString());
		}
		
		double remaining = height;
		
		for (ArrayList<View> chain : layer.horizontalChains) {
			double heightForChain = computeHeightForChainIntersectingView(chain, view);
			remaining -= heightForChain;
		}
		
		if (remaining < 0) {
			return 0;
		} else {
			return remaining;
		}
	}
	
	private double computeHeightForChainIntersectingView(ArrayList<View> chain, View view) {

		// This doesn't resolve if some are aligned with bottom, it assumes
		// all are aligned from top
		
		Double minX = view.getX();
		Double maxX = view.getX2();
		
		double maxHeight = 0;
		for (View v : chain) {
			if (currentOrientation.getHorizontalChainOf(v) == currentOrientation.getHorizontalChainOf(view)) {
				return 0;
			}
			if (!(minX >= v.getX2() || maxX <= v.getX())) {
				if (v.getHeight() != null) {
					maxHeight = Math.max(maxHeight, v.getHeight());
				}
			}
		}
		return maxHeight;
	}
	
	private double maxHeightForChain(int horizontalChain, Orientation.Layer layer) {
		ArrayList<View> chain = layer.horizontalChains.get(horizontalChain);
		double maxHeight = 0;
		for (View v : chain) {
			if (v.getHeight() != null) {
				maxHeight = Math.max(maxHeight, v.getHeight());
			}
		}
		return maxHeight;
	}
	
	private void printTable() {
		String sep = "\t";
		for (Orientation.Layer layer : currentOrientation.layers) {
			for (View view : layer.views) {
				
				System.out.print(view.id + sep);
				System.out.print(view.x + sep);
				System.out.print(view.y + sep);
				System.out.print(view.x2 + sep);
				System.out.print(view.y2 + sep);
				System.out.print(view.width + sep);
				System.out.println(view.height);
			}
		}
	}
	
	private HashSet<String> conditionalKeys;
	
	public synchronized Collection<String> getConditionalKeys() {
		return conditionalKeys;
	}
	
	public void reset(double baseUnit, String dataModelPrefix, double width) {
		for (View view : getAllViews()) {
			clearView(view);
		}
		final String cacheId = (dataModelPrefix == null) ? "default" : dataModelPrefix;
		conditionalKeys = new HashSet<>();
		for (View view : getAllViews()) {
			boolean visible = isViewVisible(currentOrientation, view, dataModelPrefix);
			if (view.isVisible() != visible) {
				viewCache.remove(cacheId);
			}
			resetView(view, baseUnit, visible, width);
		}
	}

	private boolean isViewVisible(Orientation o, View view, String dataModelPrefix) {
		
		for (Orientation.Layer layer : o.layers) {
			
			if (layer.views.contains(view)) {
				// The view is part of this orientation
				// Check the view's condition
				boolean layerVisible = checkViewCondition(layer.visibleCondition, dataModelPrefix);
				
				return layerVisible && checkViewCondition(view.visibleCondition, dataModelPrefix);
			}
		}
		return false;
	}

	private boolean checkViewCondition(String visibleCondition, String dataModelPrefix) {
		if (visibleCondition == null) {
			return true;
		} else {
			return GlobalState.fluidApp.getDataModelManager().checkCondition(visibleCondition, dataModelPrefix, conditionalKeys);
		}
	}
	
	private void clearView(View view) {
		view.x = null;
		view.y = null;
		view.x2 = null;
		view.y2 = null;
		view.width = null;
		view.height = null;
	}
	
	private void resetView(View view, double baseUnit, boolean isVisible, double width) {
		Constraints c = view.getGivenConstraints();
		if (c.x != null && !c.x.isDynamic()) {
			view.setX(c.x.getFixed());
		}
		if (c.y != null && !c.y.isDynamic()) {
			view.setY(c.y.getFixed());
		}
		if (c.x2 != null && !c.x2.isDynamic()) {
			view.setX2(c.x2.getFixed());
		}
		if (c.y2 != null && !c.y2.isDynamic()) {
			view.setY2(c.y2.getFixed());
		}
		if (!isVisible) {
			// Other rows need width set to layout
			view.setHeight(0d);
			view.setVisible(false);
		} else {
			view.setVisible(true);
		}
		if (!c.width.isDynamic()) {
			view.setWidth(c.width.getFixedLength());
		}
		if (!c.height.isDynamic() && isVisible) {
			// height should be left at 0 if the row is not visible
			view.setHeight(c.height.getFixedLength());
		}
		
		if (view.getGivenConstraints().getX2() != null && view.getGivenConstraints().getX2().isRelativeToParent()) {
			if (view.getGivenConstraints().getX2().getRelativeEdge().equalsIgnoreCase("right")) {
				view.setX2(width);
			}
		}
		
	}
	
	public synchronized Layout getLayout(String viewId, String layoutId) {
		return viewMap.get(viewId).getViewBehavior().getLayout(layoutId);
	}
	
	static class CalculatedHeight {
		
		float width;
		
		double calculatedHeight;
		
		public CalculatedHeight(float width, double calculatedHeight) {
			this.width = width;
			this.calculatedHeight = calculatedHeight;
		}
		
	}
	
	LRUCache<String, CalculatedHeight> cache = new LRUCache<>(32);
	
	public synchronized double calculateHeight(boolean landscape, float width, String dataModelPrefix) {
		return calculateHeight(landscape, width, dataModelPrefix, true);
	}		
	
	public synchronized double calculateHeight(boolean landscape, float width, String dataModelPrefix, boolean useCache) {
			
		if (dataModelPrefix == "") {
			dataModelPrefix = null;
		}
		
		// If the prefix is null, then we don't have any way to know when this layout's data changes
		// Therefore, do not cache the calculated height
		final String dataModelPrefixFinal = dataModelPrefix;
		if (dataModelPrefixFinal != null && FluidApp.useCaching && useCache) {
			CalculatedHeight calculatedHeight = cache.get(dataModelPrefixFinal);
			if (calculatedHeight != null && width == calculatedHeight.width) {
				return calculatedHeight.calculatedHeight;
			}
		}

		// If anything is relative to parent then it gets 0
		// If anything is fill then it gets 0
		layout(landscape, width, 0, dataModelPrefix, false, true, useCache, null);
		double maxHeight = 0;
		for (View v : getAllViews()) {
			double height = v.getY2();
			if (height > maxHeight) {
				maxHeight = height;
			}
		}
		
		if (dataModelPrefixFinal != null && FluidApp.useCaching && useCache) {
			
			cache.put(dataModelPrefixFinal, new CalculatedHeight(width, maxHeight));
			GlobalState.fluidApp.getDataModelManager().addDataChangeListener(null, dataModelPrefixFinal, getId() + dataModelPrefixFinal + "-calculateHeight", 
					new DataChangeListener() {
						@Override
						public void dataChanged(String key, String... subKeys) {
							cache.remove(dataModelPrefixFinal);
							GlobalState.fluidApp.getDataModelManager().removeDataChangeListener(getId() + dataModelPrefixFinal + "-calculateHeight");
						}
						@Override
						public void dataRemoved(String key) {
							cache.remove(dataModelPrefixFinal);
							GlobalState.fluidApp.getDataModelManager().removeDataChangeListener(getId() + dataModelPrefixFinal + "-calculateHeight");
						}
					});
		}
		
		return maxHeight;
	}
	
	static class LastLayout {
		double width = -1;
		double height = -1;
		Collection<ViewPosition> views;
	}

	@Override
	public void entryWasRemoved(String cacheId, LastLayout entry) {
		GlobalState.fluidApp.getDataModelManager().removeDataChangeListener(cacheId);		
	}

	static class Orientation {
		
		final boolean landscape;

		ArrayList<Layer> layers = new ArrayList<>();
		
		Layer currentLayer;
		
		public Orientation(boolean landscape) {
			this.landscape = landscape;
			currentLayer = new Orientation.Layer(0);
			layers.add(currentLayer);
		}
		
		public static class Layer {
			
			int horizontalChain = 0;			
			
			boolean hasAnchor = false;
			
			ArrayList<ArrayList<View>> horizontalChains = new ArrayList<>();	
			
			Collection<View> views = new LinkedHashSet<>();
			
			int zIndex;
			
			String visibleCondition;
			
			public Layer(int zIndex) {
				this.zIndex = zIndex;
			}
		}
		
		public int getHorizontalChainOf(View view) {
			if (landscape) {
				return view.landscape.horizontalChain;
			} else {
				return view.portrait.horizontalChain;
			}
		}
		
		public void setDirectionOf(View view, Direction direction) {
			if (landscape) {
				view.landscape.direction = direction;
			} else {
				view.portrait.direction = direction;
			}
		}
		
		public Direction getDirectionOf(View view) {
			if (landscape) {
				return view.landscape.direction;
			} else {
				return view.portrait.direction;
			}
		}
		
		public void setHorizontalChain(View view) {
			if (landscape) {
				view.landscape.horizontalChain = currentLayer.horizontalChain;
			} else {
				view.portrait.horizontalChain = currentLayer.horizontalChain;
			}
		}
		
		public void setViewsOrienation() {
			
			for (Layer l : layers) {
				for (View v : l.views) {
					if (landscape) {
						v.currentLayout = v.landscape;
					} else {
						v.currentLayout = v.portrait;
					}
				}
			}
		}
		
	}
		
}
