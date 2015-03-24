package com.sponberg.fluid.android.layout;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ReplacementSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import com.sponberg.fluid.FluidApp;
import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.android.layout.CustomLayout.TappedOutsideWhileFocusedListener;
import com.sponberg.fluid.android.util.AndroidUtil;
import com.sponberg.fluid.android.util.DataChangeRunnable;
import com.sponberg.fluid.android.util.OnTouchListenerClick;
import com.sponberg.fluid.layout.AttributedText;
import com.sponberg.fluid.layout.AttributedText.Attribute;
import com.sponberg.fluid.layout.DataChangeListener;
import com.sponberg.fluid.layout.DataModelManager;
import com.sponberg.fluid.layout.FluidView;
import com.sponberg.fluid.layout.FluidViewFactory;
import com.sponberg.fluid.layout.FluidViewFactory.FluidViewBuilder;
import com.sponberg.fluid.layout.ModalView;
import com.sponberg.fluid.layout.View;
import com.sponberg.fluid.layout.ViewBehavior;
import com.sponberg.fluid.layout.ViewBehaviorBaseLabel;
import com.sponberg.fluid.layout.ViewBehaviorButton;
import com.sponberg.fluid.layout.ViewBehaviorImage;
import com.sponberg.fluid.layout.ViewBehaviorImage.ImageBounds;
import com.sponberg.fluid.layout.ViewBehaviorLabel;
import com.sponberg.fluid.layout.ViewBehaviorSegmentedControl;
import com.sponberg.fluid.layout.ViewBehaviorSpace;
import com.sponberg.fluid.layout.ViewBehaviorSubview;
import com.sponberg.fluid.layout.ViewBehaviorSubviewRepeat;
import com.sponberg.fluid.layout.ViewBehaviorTable;
import com.sponberg.fluid.layout.ViewBehaviorTextfield;
import com.sponberg.fluid.layout.ViewBehaviorURLWebView;
import com.sponberg.fluid.layout.ViewBehaviorWebView;
import com.sponberg.fluid.layout.ViewPosition;

class RoundedBackgroundSpan extends ReplacementSpan
{
  private final int _padding = 15;
  private int _backgroundColor;
  private int _textColor;
  private int _cornerRadius;

  public RoundedBackgroundSpan(int backgroundColor, int textColor, int cornerRadius) {
	  super();
	  _backgroundColor = backgroundColor;
	  _textColor = textColor;
	  _cornerRadius = cornerRadius;
  }

  @Override
  public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
	  return (int) (_padding + paint.measureText(text.subSequence(start, end).toString()) + _padding);
  }

  @Override
  public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
	  float width = paint.measureText(text.subSequence(start, end).toString());
	  RectF rect = new RectF(x - _padding, top, x + width + _padding, top + bottom);
	  paint.setColor(_backgroundColor);
	  canvas.drawRoundRect(rect, _cornerRadius, _cornerRadius, paint);
	  paint.setColor(_textColor);
	  canvas.drawText(text, start, end, x, y, paint);
  }
}

public class FluidViewFactoryRegistration {
	
	public static void registerViews(FluidApp fluidApp) {

		FluidViewFactory factory = fluidApp.getFluidViewFactory();
		factory.registerView(ViewBehavior.label, new LabelBuilder());
		factory.registerView(ViewBehavior.button, new ButtonBuilder());
		factory.registerView(ViewBehavior.image, new ImageBuilder());
		factory.registerView(ViewBehavior.space, new SpaceBuilder());
		factory.registerView(ViewBehavior.subview, new SubviewBuilder());
		factory.registerView(ViewBehavior.subviewRepeat, new SubviewRepeatBuilder());
		factory.registerView(ViewBehavior.table, new TableBuilder());
		factory.registerView(ViewBehavior.textfield, new TextfieldBuilder());
		factory.registerView(ViewBehavior.webview, new WebViewBuilder());
		factory.registerView(ViewBehavior.urlWebview, new URLWebViewBuilder());
		//factory.registerView(ViewBehavior.searchbar, new SearchbarBuilder()); Android should put the search bar in the action bar
		factory.registerView(ViewBehavior.segmentedControl, new SegmentedControlBuilder());
	}

	protected static void addDataChangeObserverFor(final View view,
			final FluidViewBuilderInfo info, final DataChangeRunnable r) {
		addDataChangeObserverFor(info.dataModelPrefix, view.getKey(), info.listenerId, info.context, false, r);
	}

	protected static void addDataChangeObserverFor(final String prefix, final String dataModelKey,
			final String listenerId, final Context context, boolean listenForChildren, final DataChangeRunnable r) {

		if (dataModelKey == null) {
			return;
		}

		GlobalState.fluidApp.getDataModelManager().addDataChangeListener(prefix, dataModelKey, listenerId, listenForChildren,
				new DataChangeListener() {
					@Override
					public void dataChanged(final String key, final String... subkeys) {
						((Activity) context).runOnUiThread(new Runnable() {
							@Override
							public void run() {
								r.run(key, subkeys);
							}
						});
					}
					@Override
					public void dataRemoved(String key) {
						r.runRemove(key);
					}
				}
		);
	}

	protected static void removeDataChangeObserver(String listenerId) {
		if (listenerId != null) {
			GlobalState.fluidApp.getDataModelManager().removeDataChangeListener(listenerId);
		}
	}

	protected static void styleBorder(android.view.View view, int borderSize, int color, Integer cornerRadius) {

		GradientDrawable bg = new GradientDrawable();

		if (cornerRadius != null) {
			int cr = cornerRadius * 2;
			bg.setCornerRadius(cr);
		}

		bg.setStroke(borderSize * 2, color);

		//view.setBackground(bg);
		view.setBackgroundDrawable(bg);
	}

	private static final class LabelBuilder implements FluidViewBuilder {

		@Override
		public FluidView createFluidView(final ViewPosition view, Object userInfo) {

			final FluidViewBuilderInfo info = (FluidViewBuilderInfo) userInfo;

			final CustomTextViewContainer vc;
			final CustomTextView label;

			final ViewBehaviorLabel viewBehavior = (ViewBehaviorLabel) view
					.getViewBehavior();

			label = new CustomTextView(info.context, viewBehavior, info.bounds, info.customLayout);
			vc = new CustomTextViewContainer(info.context, label, info.bounds);
			vc.setDataModelListenerId(info.listenerId);

			int clearColor = 0x00000000;
			label.setBackgroundColor(clearColor);
			vc.setBackgroundColor(clearColor);

			if (info.customLayout.isListenToDataModelChanges()) {
				final CustomTextViewContainer finalLabel = vc;
				addDataChangeObserverFor(info.dataModelPrefix, view.getKey(),
						info.listenerId, info.context, false,
						new DataChangeRunnable() {
							@Override
							public void run(String key, String... subkeys) {
								updateLabel(finalLabel, view, info);
							}
							@Override
							public void runRemove(String key) {
								// hstdbc what to do?
							}
						});
			}

			if (GlobalState.fluidApp.getEventsManager().isListeningForTapAt(info.viewPath)) {
				// Only add this if the app is listening for a touch,
				// because once you add an event listener, Android will stop sending
				// events to views underneath the label
				OnTouchListenerClick listener = new OnTouchListenerClick("labelBuilder" + info.viewPath) {

					@Override
					public void tap() {

						if (!info.customLayout.isUserActivityEnabled()) {
							return;
						}

						label.userTapped(info.viewPath, info.dataModelPrefix, view.getKey());
					}

					@Override
					public void touchDown() {
						if (viewBehavior.getTextColorPressed() != null) {
							label.setTextColor(CustomLayout.getColor(viewBehavior
									.getTextColorPressed()));
						}
						if (viewBehavior.getBackgroundColorPressed() != null) {
							vc.setBackgroundColor(CustomLayout.getColor(viewBehavior.getBackgroundColorPressed()));
						}
					}

					@Override
					public void touchMove() {
						touchUp();
					}

					@Override
					public void touchUp() {
						if (viewBehavior.getTextColorPressed() != null && viewBehavior.getTextColor() != null) {
							label.setTextColor(CustomLayout.getColor(viewBehavior
									.getTextColor()));
						}
						if (viewBehavior.getBackgroundColorPressed() != null) { // does this prefix key need to add fluidView key ?
							com.sponberg.fluid.layout.Color color = viewBehavior.getBackgroundColor(info.dataModelPrefix);
							if (color == null) {
								vc.setBackgroundColor(Color.TRANSPARENT);
							} else {
								vc.setBackgroundColor(CustomLayout.getColor(viewBehavior.getBackgroundColor(info.dataModelPrefix)));
							}
						}
					}
				};

				vc.setOnTouchListener(listener);
			}

			updateFluidView(vc, view, userInfo);

			if (viewBehavior.getBorderSize() != null && viewBehavior.getBorderSize().intValue() > 0 && viewBehavior.getBorderColor() != null) {
				int borderColor = 0;
				borderColor = CustomLayout.getColor(viewBehavior.getBorderColor());
				styleBorder(vc, viewBehavior.getBorderSize().intValue(), borderColor, viewBehavior.getCornerRadius());
			}

			return vc;
		}

		@Override
		public void updateFluidView(final Object fluidView, final ViewPosition view,
				Object userInfo) {

			final FluidViewBuilderInfo info = (FluidViewBuilderInfo) userInfo;
			((FluidViewAndroid) fluidView).setBounds(info.bounds);

			final ViewBehaviorLabel viewBehavior = (ViewBehaviorLabel) view
					.getViewBehavior();

			CustomTextViewContainer vc = (CustomTextViewContainer) fluidView;
			CustomTextView label = (CustomTextView) vc.getView();

			if (viewBehavior.getAlign() == null
					|| viewBehavior.getAlign().equals(
							ViewBehaviorLabel.kAlignCenter)) {
				label.setGravity(Gravity.CENTER_HORIZONTAL);
			} else if (viewBehavior.getAlign().equals(
					ViewBehaviorLabel.kAlignRight)) {
				label.setGravity(Gravity.RIGHT);
			} else {
				label.setGravity(Gravity.LEFT);
			}

			if (viewBehavior.getVerticalAlign() == null
					|| viewBehavior.getVerticalAlign().equals(
							ViewBehaviorLabel.kVerticalAlignTop)) {
				label.setGravity(label.getGravity() | Gravity.TOP);
			} else if (viewBehavior.getVerticalAlign().equals(
					ViewBehaviorLabel.kVerticalAlignMiddle)) {
				label.setGravity(label.getGravity() | Gravity.CENTER_VERTICAL);
			} else {
				label.setGravity(label.getGravity() | Gravity.BOTTOM);
			}

			updateLabel(vc, view, info);
		}

		protected static void updateLabel(CustomTextViewContainer viewContainer, ViewPosition view,
				FluidViewBuilderInfo info) {

			CustomTextView label = viewContainer.view;

			final ViewBehaviorLabel viewBehavior = (ViewBehaviorLabel) view
					.getViewBehavior();

			String text = CustomLayout.getValueFor(view,
					viewBehavior.getText(), info.dataModelPrefix);

			// hstdbc fix the issue, hack for now
			if (text == null) {
				text = "";
			}

			int hashCode = text.hashCode();

			if (hashCode == label.getLabelHashCode()) {
				return;
			}

			label.setLabelHashCode(hashCode);

			SpannableString spanString = createAttributedText(text);

			label.setCustomText(spanString);

			if (viewBehavior.getUnknownText() != null && viewBehavior.getUnknownText().equals(text) && viewBehavior.getUnknownTextColor() != null) {
				label.setTextColor(CustomLayout.getColor(viewBehavior
						.getUnknownTextColor()));
			} else if (viewBehavior.getTextColor() != null) {
				label.setTextColor(CustomLayout.getColor(viewBehavior
						.getTextColor()));
			} else {
				label.setTextColor(Color.BLACK);
			}

			if (viewBehavior.isEllipsize()) {
				label.setEllipsize(TruncateAt.END);
				label.setHorizontallyScrolling(true);
			}
				
			if (viewBehavior.getFontFamilyName() != null) {
				String familyName = viewBehavior.getFontFamilyName();
				int style = Typeface.NORMAL;
				if (viewBehavior.getFontStyle() != null) {
					if (viewBehavior.getFontStyle().equals(ViewBehaviorBaseLabel.kFontStyleBold)) {
						style = Typeface.BOLD;
					} else if (viewBehavior.getFontStyle().equals(ViewBehaviorBaseLabel.kFontStyleItalic)) {
						style = Typeface.ITALIC;
					} else if (viewBehavior.getFontStyle().equals(ViewBehaviorBaseLabel.kFontStyleBoldItaclic)) {
						style = Typeface.BOLD_ITALIC;
					}
				}
				
				Typeface typeface = Typeface.create(familyName, style);
				label.setTypeface(typeface);
			}
		}

		@Override
		public void cleanupFluidView(Object view) {

			CustomTextViewContainer vc = (CustomTextViewContainer) view;
			FluidViewFactoryRegistration.removeDataChangeObserver(vc.getDataModelListenerId());
		}
	}

	private static final class ButtonBuilder implements FluidViewBuilder {

		@Override
		public FluidView createFluidView(final ViewPosition view, Object userInfo) {

			final FluidViewBuilderInfo info = (FluidViewBuilderInfo) userInfo;

			final ButtonFluid button = new ButtonFluid(info.context,
					(ViewBehaviorButton) view.getViewBehavior(), info.viewPath,
					info.dataModelPrefix, view.getKey(), info.customLayout);
			button.bounds = info.bounds;

			button.setBackgroundColor(Color.TRANSPARENT);

			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(android.view.View androidView) {

					if (info.modalView != null) {
						info.modalView.setUserSelection(view.getId());
						((Dialog) info.modalView.getFluidData()).dismiss();
					} else {
						button.userTapped();
					}
				}
			});

			updateFluidView(button, view, userInfo);

			return button;
		}

		@Override
		public void updateFluidView(final Object fluidView, final ViewPosition view,
				Object userInfo) {

			ButtonFluid button = (ButtonFluid) fluidView;

			final FluidViewBuilderInfo info = (FluidViewBuilderInfo) userInfo;
			((FluidViewAndroid) fluidView).setBounds(info.bounds);

			ViewBehaviorButton viewBehavior = (ViewBehaviorButton) view
					.getViewBehavior();

			updateText(button, view, viewBehavior.getText(), info.dataModelPrefix);

			if (viewBehavior.getTextColor() != null) {
				button.setTextColor(CustomLayout.getColor(viewBehavior
						.getTextColor()));
			}

			if (viewBehavior.getImage() != null) {
				styleButtonWithImage(button, info, viewBehavior);
			}

			if (viewBehavior.getBackgroundColor(info.dataModelPrefix) != null) {
				button.setBackgroundColor(CustomLayout.getColor(viewBehavior.getBackgroundColor(info.dataModelPrefix)));
			}

			if (viewBehavior.getBorderSize() > 0 || viewBehavior.getBackgroundColorPressed() != null) {
				styleButtonWithColors(button, info, viewBehavior);
			}

		}

		private void updateText(ButtonFluid button, ViewPosition view, String baseText, String dataModelPrefix) {

			String text = CustomLayout.getValueFor(view, baseText, dataModelPrefix);

			// hstdbc fix the issue, hack for now
			if (text == null) {
				text = "";
			}

			int hashCode = text.hashCode();

			if (hashCode == button.getLabelHashCode()) {
				return;
			}

			button.setLabelHashCode(hashCode);

			SpannableString spanString = createAttributedText(text);

			button.setCustomText(spanString);
		}

		private void styleButtonWithImage(ButtonFluid button,
				final FluidViewBuilderInfo info, ViewBehaviorButton viewBehavior) {

			String imageNameKey = viewBehavior.getImage();

			Bounds bounds = info.bounds;

			if (button.getImageName() == null
					|| !button.getImageName().equals(imageNameKey)
					|| bounds.width != button.getImageBounds().getWidth()
					|| bounds.height != button.getImageBounds().getHeight()) {
				Bitmap bm;
				ImageBounds imageBounds = new ImageBounds(0, 0, bounds.width, bounds.height);
				bm = getBitmapFor(imageNameKey, imageBounds.getWidth(), imageBounds.getHeight(), info.context.getResources());
				BitmapDrawable bmDrawable = new BitmapDrawable(info.context.getResources(), bm);
				button.setBackgroundDrawable(bmDrawable);
				button.setImageName(imageNameKey);
				button.setImageBounds(imageBounds);
			}
		}

		private void styleButtonWithColors(ButtonFluid button,
				final FluidViewBuilderInfo info, ViewBehaviorButton viewBehavior) {

			StateListDrawable sld = new StateListDrawable();

			GradientDrawable gd = new GradientDrawable();
			GradientDrawable gdp;

			if (viewBehavior.getBackgroundColor(info.dataModelPrefix) != null) {
				gd.setColor(CustomLayout.getColor(viewBehavior.getBackgroundColor(info.dataModelPrefix)));
			} else {
				gd.setColor(Color.TRANSPARENT);
			}
	        gd.setCornerRadius(0);

	        if (viewBehavior.getBorderSize() > 0) {
				gd.setStroke((int) Math.round(viewBehavior.getBorderSize()), CustomLayout.getColor(viewBehavior.getBorderColor()));
			}

			if (viewBehavior.getBackgroundColorPressed() != null) {
				gdp = new GradientDrawable();
				gdp.setColor(CustomLayout.getColor(viewBehavior.getBackgroundColorPressed()));
		        gdp.setCornerRadius(0);
		        if (viewBehavior.getBorderSize() > 0) {
					gdp.setStroke((int) Math.round(viewBehavior.getBorderSize()), CustomLayout.getColor(viewBehavior.getBorderColor()));
				}
		        sld.addState(new int[] { android.R.attr.state_pressed }, gdp);
			}

	        sld.addState(new int[] { 0 }, gd);

	        //if (android.os.Build.VERSION.SDK_INT >= 16) {
	        //	button.setBackground(sld);
	        //} else {
	        	button.setBackgroundDrawable(sld);
	        //}
		}

		@Override
		public void cleanupFluidView(Object view) {
		}
	}

	private static final class ImageBuilder implements FluidViewBuilder {

		@Override
		public FluidView createFluidView(final ViewPosition view, Object userInfo) {

			final FluidViewBuilderInfo info = (FluidViewBuilderInfo) userInfo;

			ViewBehaviorImage viewBehavior = (ViewBehaviorImage) view
					.getViewBehavior();

			Bounds bounds = info.bounds;

			String imageNameKey = viewBehavior.getImageWith(info.dataModelPrefix);

			Bitmap bm;
			ImageBounds imageBounds;
			Double aspectRatio = null;
			if (imageNameKey == null) {
				bm = Bitmap.createBitmap(bounds.width, bounds.height, Config.ARGB_8888);
				imageBounds = new ImageBounds(0, 0, bounds.width, bounds.height);
			} else if (imageNameKey.startsWith("system:")) {
				bm = getSystemBitmapFor(imageNameKey, info.context.getResources());
				aspectRatio = bm.getWidth() * 1.0 / bm.getHeight();
				imageBounds = viewBehavior.getImageWithBounds(bounds.width, bounds.height, aspectRatio);
			} else {
				imageBounds = viewBehavior.getImageBounds(imageNameKey, bounds.width, bounds.height);
				bm = FluidViewFactoryRegistration.getBitmapFor(imageNameKey, imageBounds.getWidth(), imageBounds.getHeight(), info.context.getResources());
			}

			com.sponberg.fluid.layout.Color tintColor = null;
			if (viewBehavior.getTintColor() != null) {
				tintColor = viewBehavior.getTintColor();
			} else if (viewBehavior.getTintColorKey() != null) {
				String colorString = GlobalState.fluidApp.getDataModelManager().getValue(info.dataModelPrefix, viewBehavior.getTintColorKey(), "{0}", null);
				tintColor = GlobalState.fluidApp.getViewManager().getColor(colorString);
			}
			
			FluidImageView image = new FluidImageView(info.context, bm, imageNameKey, imageBounds,
					info.viewPath, info.dataModelPrefix, view.getKey(), info.customLayout,
					tintColor);
			image.bounds = info.bounds;

			if (aspectRatio != null) {
				image.setAspectRatio(aspectRatio);
			}

			updateFluidView(image, view, info);

			return image;
		}

		protected Bitmap getSystemBitmapFor(String imageName, Resources resources) {
			if (imageName.startsWith("system:")) {
				String systemName = imageName.substring(7);
				if (systemName.equals("radiobutton_on_background")) {
					return BitmapFactory.decodeResource(resources, android.R.drawable.radiobutton_on_background);
				} else if (systemName.equals("radiobutton_off_background")) {
					return BitmapFactory.decodeResource(resources, android.R.drawable.radiobutton_off_background);
				} else {
					throw new RuntimeException("Unsupported system resource " + imageName);
				}
			} else {
				throw new RuntimeException("Not a system image " + imageName);
			}
		}

		@Override
		public void updateFluidView(final Object fluidView, final ViewPosition view,
				Object userInfo) {

			FluidImageView image = (FluidImageView) fluidView;

			final FluidViewBuilderInfo info = (FluidViewBuilderInfo) userInfo;
			((FluidViewAndroid) fluidView).setBounds(info.bounds);

			ViewBehaviorImage viewBehavior = (ViewBehaviorImage) view
					.getViewBehavior();

			Bounds bounds = info.bounds;

			String imageNameKey = viewBehavior.getImageWith(info.dataModelPrefix);

			if (imageNameKey != null) {
				if (image.getImageName() == null || !image.getImageName().equals(imageNameKey)) {
					Bitmap bm;
					ImageBounds imageBounds;
					if (imageNameKey.startsWith("system:")) {
						bm = getSystemBitmapFor(imageNameKey, info.context.getResources());
						Double aspectRatio = bm.getWidth() * 1.0 / bm.getHeight();
						imageBounds = viewBehavior.getImageWithBounds(bounds.width, bounds.height, aspectRatio);
						image.setAspectRatio(aspectRatio);
					} else {
						imageBounds = viewBehavior.getImageBounds(imageNameKey, bounds.width, bounds.height);
						bm = FluidViewFactoryRegistration.getBitmapFor(imageNameKey, imageBounds.getWidth(), imageBounds.getHeight(), info.context.getResources());
					}
					image.setBitmap(bm);
					image.setImageName(imageNameKey);
					image.setImageBounds(imageBounds);
				} else {
					ImageBounds imageBounds;
					if (imageNameKey.startsWith("system:")) {
						imageBounds = viewBehavior.getImageWithBounds(bounds.width, bounds.height, image.getAspectRatio());
					} else {
						imageBounds = viewBehavior.getImageBounds(imageNameKey, bounds.width, bounds.height);
					}
					if (!image.getImageBounds().equals(imageBounds)) {
						image.setImageBounds(imageBounds);
					}
				}
				image.setVisibility(android.view.View.VISIBLE);
			} else {
				image.setImageName(null);
				image.setVisibility(android.view.View.INVISIBLE);
			}
		}

		@Override
		public void cleanupFluidView(Object view) {
		}
	}

	private static final class SpaceBuilder implements FluidViewBuilder {

		@Override
		public FluidView createFluidView(final ViewPosition view, Object userInfo) {

			final FluidViewBuilderInfo info = (FluidViewBuilderInfo) userInfo;

			ViewFluidSpace space = new ViewFluidSpace(info.context);
			space.bounds = info.bounds;

			updateFluidView(space, view, info);

			ViewBehaviorSpace viewBehavior = (ViewBehaviorSpace) view.getViewBehavior();

			if (viewBehavior.getBorderSize() != null && viewBehavior.getBorderSize().intValue() > 0 && viewBehavior.getBorderColor() != null) {
				int borderColor = 0;
				borderColor = CustomLayout.getColor(viewBehavior.getBorderColor());
				styleBorder(space, viewBehavior.getBorderSize().intValue(), borderColor, viewBehavior.getCornerRadius());
			}
			return space;
		}

		@Override
		public void updateFluidView(final Object fluidView, final ViewPosition view,
				Object userInfo) {

			final FluidViewBuilderInfo info = (FluidViewBuilderInfo) userInfo;
			((FluidViewAndroid) fluidView).setBounds(info.bounds);
		}

		@Override
		public void cleanupFluidView(Object view) {
		}
	}

	private static final class SubviewBuilder implements FluidViewBuilder {

		@Override
		public FluidView createFluidView(final ViewPosition view, Object userInfo) {

			final FluidViewBuilderInfo info = (FluidViewBuilderInfo) userInfo;

			ViewBehaviorSubview viewBehavior = (ViewBehaviorSubview) view
					.getViewBehavior();

			CustomLayout layout = new CustomLayout(info.context, null,
					GlobalState.fluidApp.getLayout(viewBehavior.getSubview()),
					info.bounds, info.dataModelPrefix, null, info.viewPath,
					true, info.customLayout, true, null, null, false, false,
					info.insideTableView);

			updateFluidView(layout, view, info);

			return layout;
		}

		@Override
		public void updateFluidView(final Object fluidView, final ViewPosition view,
				Object userInfo) {

			final FluidViewBuilderInfo info = (FluidViewBuilderInfo) userInfo;
			((FluidViewAndroid) fluidView).setBounds(info.bounds);
		}

		@Override
		public void cleanupFluidView(Object view) {

			CustomLayout layout = (CustomLayout) view;
			layout.cleanup();
		}
	}

	private static final class SubviewRepeatBuilder implements FluidViewBuilder {

		@Override
		public FluidView createFluidView(final ViewPosition view, Object userInfo) {

			final FluidViewBuilderInfo info = (FluidViewBuilderInfo) userInfo;

			ViewBehaviorSubviewRepeat viewBehavior = (ViewBehaviorSubviewRepeat) view
					.getViewBehavior();

			SubviewRepeatView subviewRepeat = new SubviewRepeatView(info,
					viewBehavior);
			subviewRepeat.bounds = info.bounds;

			updateFluidView(subviewRepeat, view, info);

			return subviewRepeat;
		}

		@Override
		public void updateFluidView(final Object fluidView, final ViewPosition view,
				Object userInfo) {

			final FluidViewBuilderInfo info = (FluidViewBuilderInfo) userInfo;
			((FluidViewAndroid) fluidView).setBounds(info.bounds);

			SubviewRepeatView subviewRepeat = (SubviewRepeatView) fluidView;
			subviewRepeat.bounds = info.bounds;
		}

		@Override
		public void cleanupFluidView(Object fluidView) {
			SubviewRepeatView subviewRepeat = (SubviewRepeatView) fluidView;
			subviewRepeat.cleanup();
		}
	}

	private static final class TableBuilder implements FluidViewBuilder {

		@Override
		public ListViewFluid createFluidView(final ViewPosition view, Object userInfo) {

			final FluidViewBuilderInfo info = (FluidViewBuilderInfo) userInfo;

			ViewBehaviorTable vb = (ViewBehaviorTable) view.getViewBehavior();

			//FluidTableOrListView table;
			ListViewFluid table;
			//if (vb.getTableLayoutId() != null) {
			//	table = createFluidTable(view, userInfo);
			//} else {
				table = createListView(view, userInfo);
			//}

			if (info.customLayout.isListenToDataModelChanges()) {
				final ListViewFluid finalTable = table;

				String key = view.getKey();
				if (vb.getTableLayoutId() != null) {
					key = vb.getTableLayoutId();
				}

				addDataChangeObserverFor(info.dataModelPrefix, key,
						info.listenerId, info.context, true,
						new DataChangeRunnable() {
							@Override
							public void run(final String key, final String... subkeys) {
								finalTable.reloadData();
							}
							@Override
							public void runRemove(final String key) {

								int i = key.lastIndexOf(".");
								String objectId;
								if (i != -1) {
									objectId = key.substring(i + 1, key.length());
								} else {
									objectId = key;
								}

								finalTable.hideViewWithId(Long.parseLong(objectId));

								//new Thread() {
									//public void run() {
										// Reload table after the remove listeners are processed - hstdbc runOnUiThread already does this now
										Runnable r = new Runnable() {
											@Override
											public void run() {
												String key = DataModelManager.getFullKey(info.dataModelPrefix, view.getKey());
												GlobalState.fluidApp.getDataModelManager().dataDidChange(key);
											}
										};
										GlobalState.fluidApp.getSystemService().runOnUiThread(r);
									//}
								//}.start();
							}
						});
			}

			updateFluidView(table, view, info);

			return table;
		}

		/*
		public FluidTableOrListView createFluidTable(final ViewPosition view, Object userInfo) {

			final FluidViewBuilderInfo info = (FluidViewBuilderInfo) userInfo;

			ViewBehaviorTable vb = (ViewBehaviorTable) view.getViewBehavior();

			FluidTable table = new FluidTable(info.context, view, info.bounds, info.viewPath, vb.isShowRowDivider());
			table.setDataModelListenerId(info.listenerId);

			return table;
		}*/

		public ListViewFluid createListView(final ViewPosition view, Object userInfo) {

			final FluidViewBuilderInfo info = (FluidViewBuilderInfo) userInfo;

			ListViewFluid table = new ListViewFluid(info.context, view,
					info.bounds, info.viewPath, info.customLayout);
			ViewBehaviorTable vb = (ViewBehaviorTable) view.getViewBehavior();

			table.setDataModelListenerId(info.listenerId);

			if (!vb.isShowRowDivider()) {
				table.setDivider(null);
			}

			if (vb.getPaddingBottom() != null && vb.getPaddingBottom() > 0) {
				table.setPadding(0, 0, 0, vb.getPaddingBottom().intValue());
				table.setClipToPadding(false);
			}

			return table;
		}

		@Override
		public void updateFluidView(final Object fluidView, final ViewPosition view,
				Object userInfo) {

			final FluidViewBuilderInfo info = (FluidViewBuilderInfo) userInfo;
			((ListViewFluid) fluidView).setBounds(info.bounds);

			ViewBehaviorTable vb = (ViewBehaviorTable) view.getViewBehavior();

			android.view.View table = (android.view.View) fluidView;
			if (vb.getBackgroundColor(info.dataModelPrefix) != null) {
				table.setBackgroundColor(CustomLayout.getColor(vb
						.getBackgroundColor(info.dataModelPrefix)));
			} else {
				table.setBackgroundColor(Color.TRANSPARENT);
			}
		}

		@Override
		public void cleanupFluidView(Object view) {
			ListViewFluid table = (ListViewFluid) view;
			FluidViewFactoryRegistration.removeDataChangeObserver(table.getDataModelListenerId());
		}
	}

	private static final class TextfieldBuilder implements FluidViewBuilder {

		@Override
		public FluidView createFluidView(final ViewPosition view, Object userInfo) {

			final FluidViewBuilderInfo info = (FluidViewBuilderInfo) userInfo;

			final ViewBehaviorTextfield viewBehavior = (ViewBehaviorTextfield) view
					.getViewBehavior();

			final EditTextFluid textfield = new EditTextFluid(info.context, view);
			textfield.bounds = info.bounds;
			textfield.setDataModelListenerId(info.listenerId);

			if (viewBehavior.getKeyboard().equals(
					ViewBehaviorTextfield.kKeyboardEmail)) {
				textfield.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
			} else if (viewBehavior.getKeyboard().equals(
					ViewBehaviorTextfield.kKeyboardNumber)) {
				textfield.setRawInputType(InputType.TYPE_CLASS_NUMBER);
				//textfield.setInputType(InputType.TYPE_CLASS_NUMBER);
			} else if (viewBehavior.getKeyboard().equals(
					ViewBehaviorTextfield.kKeyboardPhone)) {
				//textfield.setInputType(InputType.TYPE_CLASS_PHONE);
				textfield.setRawInputType(InputType.TYPE_CLASS_PHONE);
			} else if (viewBehavior.getKeyboard().equals(
					ViewBehaviorTextfield.kKeyboardUrl)) {
				textfield.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_URI);
			} else {
				textfield.setInputType(InputType.TYPE_CLASS_TEXT);
			}

			if (!viewBehavior.isAutoCorrect()) {
				textfield.setInputType(textfield.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
			}

			if (viewBehavior.isPassword()) {
				textfield.setInputType(textfield.getInputType() |
						InputType.TYPE_TEXT_VARIATION_PASSWORD);
			}

			String capitalize = viewBehavior.getCapitalize();
			if (capitalize != null && capitalize.equalsIgnoreCase("words")) {
				textfield.setInputType(textfield.getInputType() | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
			}

			textfield.setEnabled(true);

			String borderStyle = viewBehavior.getBorderStyle();
			if (borderStyle != null) {
				if (borderStyle.equals(ViewBehaviorTextfield.kBorderStyleNone)) {
					textfield.setBackgroundDrawable(null);
				}
			}

			if (viewBehavior.isMultiLine()) {
				textfield.setSingleLine(false);
				textfield.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
				textfield.setGravity(Gravity.TOP);
			}

			if (info.customLayout.isListenToDataModelChanges()) {
				addDataChangeObserverFor(info.dataModelPrefix, view.getKey(), info.listenerId, info.context, false,
					new DataChangeRunnable() {
						@Override
						public void run(String key, String... subkeys) {
							textfield.setText(CustomLayout.getValueFor(view, null, info.dataModelPrefix));
						}
						@Override
						public void runRemove(String key) {
							// hstdbc what to do?
						}
				});
			}

			textfield.addTextChangedListener(new TextWatcher() {

				String last = "";

				@Override
				public void afterTextChanged(Editable s) {

					if (last.equals(s.toString())) {
						return;
					}

					last = s.toString();

					GlobalState.fluidApp.getEventsManager().userChangedValueTo(view.getViewPathKey(), null, s.toString());
					CustomLayout.setValueFor(view, info.dataModelPrefix, s.toString());
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}
			});

			info.customLayout.addTappedOutsideWhileFocusedListener(textfield,
					new TappedOutsideWhileFocusedListener() {
						@Override
						public void tappedOutsideWhileFocused() {
							if (viewBehavior.isDismissKeyboardWithTap()) {
								InputMethodManager imm = (InputMethodManager) ((Activity) info.context).getSystemService(Context.INPUT_METHOD_SERVICE);
								imm.hideSoftInputFromWindow(textfield.getWindowToken(), 0);
							}
						}
					});

			textfield.setText(CustomLayout.getValueFor(view, null, info.dataModelPrefix));

			updateFluidView(textfield, view, info);

			styleTextView(textfield, viewBehavior, info);

			return textfield;
		}

		@Override
		public void updateFluidView(final Object fluidView, final ViewPosition view,
				Object userInfo) {

			final FluidViewBuilderInfo info = (FluidViewBuilderInfo) userInfo;
			((FluidViewAndroid) fluidView).setBounds(info.bounds);

			ViewBehaviorTextfield viewBehavior = (ViewBehaviorTextfield) view
					.getViewBehavior();

			EditTextFluid textfield = (EditTextFluid) fluidView;

			if (viewBehavior.getFormattedPlaceholder() != null) {

				SpannableString spanString = createAttributedText(viewBehavior.getFormattedPlaceholder());
				textfield.setHint(spanString);
			} else {

				textfield.setHint(viewBehavior.getLabel());
			}

			textfield.setEnabled(viewBehavior.isEnabled(info.dataModelPrefix));
		}

		protected void styleTextView(EditTextFluid textfield, ViewBehaviorTextfield viewBehavior, FluidViewBuilderInfo info) {

			boolean hasStyle = false;

	        StateListDrawable sld = new StateListDrawable();

			GradientDrawable gd = new GradientDrawable();
			gd.setColor(0x00000000);

			if (viewBehavior.getBackgroundColor(info.dataModelPrefix) != null) {
				hasStyle = true;
				gd.setColor(CustomLayout.getColor(viewBehavior.getBackgroundColor(info.dataModelPrefix)));
			}
	        gd.setCornerRadius(0);

	        if (viewBehavior.getBorderSize() > 0) {
	        	hasStyle = true;
	        	gd.setStroke((int) Math.round(viewBehavior.getBorderSize()), CustomLayout.getColor(viewBehavior.getBorderColor()));
	        }

	        sld.addState(new int[] { 0 }, gd);

	        if (viewBehavior.getAndroidLineColor() != null && textfield.getBackground() != null) {
	        	int color = CustomLayout.getColor(viewBehavior.getAndroidLineColor());
	        	textfield.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
	        }

	        if (hasStyle) {

	        	if (textfield.getBackground() == null) {
	        		textfield.setBackgroundDrawable(sld);
	        		//textfield.setBackground(sld);
	        	} else {
	    	        Drawable[] layers = new Drawable[2];
	    	        layers[0] = sld;
	    	        layers[1] = textfield.getBackground();
	    	        textfield.setBackgroundDrawable(new LayerDrawable(layers));
	    	        //textfield.setBackground(new LayerDrawable(layers));
	        	}
	        }

	        int enabledColor = Color.BLACK;
	        int disabledColor = Color.BLACK;

	        if(viewBehavior.getTextDisabledColor() != null) {
	        	disabledColor = CustomLayout.getColor(viewBehavior.getTextDisabledColor());
	        }
	        if(viewBehavior.getTextEnabledColor() != null) {
	        	enabledColor = CustomLayout.getColor(viewBehavior.getTextEnabledColor());
	        }

	        ColorStateList colorStateList = new ColorStateList(
		            new int[][]{
		                    new int[]{android.R.attr.state_enabled},
		                    new int[]{-android.R.attr.state_enabled},
		            },
		            new int[]{
		            		enabledColor,
		            		disabledColor});
			textfield.setTextColor(colorStateList);
		}

		@Override
		public void cleanupFluidView(Object fluidView) {
			EditTextFluid view = (EditTextFluid) fluidView;
			FluidViewFactoryRegistration.removeDataChangeObserver(view.getDataModelListenerId());
		}
	}

	private static final class WebViewBuilder implements FluidViewBuilder {

		@Override
		public FluidView createFluidView(final ViewPosition view, Object userInfo) {

			final FluidViewBuilderInfo info = (FluidViewBuilderInfo) userInfo;

			ViewBehaviorWebView viewBehavior = (ViewBehaviorWebView) view
					.getViewBehavior();
			String html = null;
			try {
				html = viewBehavior.getHtml();
			} catch (Exception e) {
				e.printStackTrace();
			}

			WebViewFluid webview = new WebViewFluid(info.context, view,
					info.bounds);

			webview.loadDataWithBaseURL("file:///android_asset/fluid/webview/",
					html, "text/html", "utf-8", "");

			updateFluidView(webview, view, info);

			return webview;
		}

		@Override
		public void updateFluidView(final Object fluidView, final ViewPosition view,
				Object userInfo) {

			final FluidViewBuilderInfo info = (FluidViewBuilderInfo) userInfo;
			((FluidViewAndroid) fluidView).setBounds(info.bounds);

			WebViewFluid webview = (WebViewFluid) fluidView;

			//if (!info.measurePass)
			//	webview.setViewBounds(info.bounds);
		}

		@Override
		public void cleanupFluidView(Object view) {
		}
	}

	private static final class URLWebViewBuilder implements FluidViewBuilder {

		@Override
		public FluidView createFluidView(final ViewPosition view, Object userInfo) {

			final FluidViewBuilderInfo info = (FluidViewBuilderInfo) userInfo;

			ViewBehaviorURLWebView viewBehavior = (ViewBehaviorURLWebView) view
					.getViewBehavior();

			WebViewFluid webview = new WebViewFluid(info.context, view,
					info.bounds);

			String url = viewBehavior.getUrl();
			String urlKey = viewBehavior.getUrlKey();
			if (urlKey != null) {
	            url = view.getValue(info.dataModelPrefix, urlKey, url);
	        }

			webview.loadUrl(url);

			updateFluidView(webview, view, info);

			return webview;
		}

		@Override
		public void updateFluidView(final Object fluidView, final ViewPosition view,
				Object userInfo) {

			final FluidViewBuilderInfo info = (FluidViewBuilderInfo) userInfo;
			((FluidViewAndroid) fluidView).setBounds(info.bounds);

			WebViewFluid webview = (WebViewFluid) fluidView;

			//if (!info.measurePass)
			//	webview.setViewBounds(info.bounds);
		}

		@Override
		public void cleanupFluidView(Object view) {
		}
	}

	private static final class SegmentedControlBuilder implements FluidViewBuilder {

		@Override
		public FluidView createFluidView(final ViewPosition view, Object userInfo) {

			final FluidViewBuilderInfo info = (FluidViewBuilderInfo) userInfo;

			ViewBehaviorSegmentedControl viewBehavior = (ViewBehaviorSegmentedControl) view.getViewBehavior();

			final FluidRadioGroup radioGroup = new FluidRadioGroup(info.context,
					(ViewBehaviorSegmentedControl) view.getViewBehavior(), info.viewPath, viewBehavior.getAndroidPadding(), info.customLayout);
			radioGroup.bounds = info.bounds;

			updateFluidView(radioGroup, view, info);

			return radioGroup;
		}

		@Override
		public void updateFluidView(final Object fluidView, final ViewPosition view,
				Object userInfo) {

			final FluidViewBuilderInfo info = (FluidViewBuilderInfo) userInfo;
			((FluidViewAndroid) fluidView).setBounds(info.bounds);

			final FluidRadioGroup radioGroup = (FluidRadioGroup) fluidView;

			ViewBehaviorSegmentedControl viewBehavior = (ViewBehaviorSegmentedControl) view.getViewBehavior();
		    if (viewBehavior.getSelectedIndexKey() != null) {

		    	String indexString = GlobalState.fluidApp.getDataModelManager().getValue(null, viewBehavior.getSelectedIndexKey(), "{0}", null);
		        radioGroup.setSelectdIndex(Integer.parseInt(indexString));
		    }

		}

		@Override
		public void cleanupFluidView(Object view) {
		}
	}

	public static class FluidViewBuilderInfo {

		final public Bounds bounds;
		final public Context context;
		final public String dataModelPrefix;
		final public String listenerId;
		final public boolean measurePass;
		final public CustomLayout customLayout;
		final public String viewPath;
		final public ModalView modalView;
		final public ListViewFluid insideTableView;

		FluidViewBuilderInfo(final Bounds bounds, final Context context,
				final String dataModelPrefix, final String listenerId,
				final boolean measurePass, CustomLayout customLayout,
				String viewPath, ModalView modalView, ListViewFluid insideTableView) {

			this.bounds = bounds;
			this.context = context;
			this.dataModelPrefix = dataModelPrefix;
			this.listenerId = listenerId;
			this.measurePass = measurePass;
			this.customLayout = customLayout;
			this.viewPath = viewPath;
			this.modalView = modalView;
			this.insideTableView = insideTableView;
		}

		public static class FluidViewBuilderInfoBuilder {
			private Bounds bounds;
			private Context context;
			private String dataModelPrefix;
			private String listenerId;
			private boolean measurePass;
			private CustomLayout customLayout;
			private String viewPath;
			private ModalView modalView;
			private ListViewFluid insideTableView;

			FluidViewBuilderInfoBuilder() {

			}

			public FluidViewBuilderInfoBuilder bounds(final Bounds bounds) {
				this.bounds = bounds;
				return this;
			}

			public FluidViewBuilderInfoBuilder context(final Context context) {
				this.context = context;
				return this;
			}

			public FluidViewBuilderInfoBuilder dataModelPrefix(
					final String dataModelPrefix) {
				this.dataModelPrefix = dataModelPrefix;
				return this;
			}

			public FluidViewBuilderInfoBuilder listenerId(
					final String listenerId) {
				this.listenerId = listenerId;
				return this;
			}

			public FluidViewBuilderInfoBuilder measurePass(
					final boolean measurePass) {
				this.measurePass = measurePass;
				return this;
			}

			public FluidViewBuilderInfoBuilder customLayout(
					final CustomLayout customLayout) {
				this.customLayout = customLayout;
				return this;
			}

			public FluidViewBuilderInfoBuilder viewPath(
					final String viewPath) {
				this.viewPath = viewPath;
				return this;
			}

			public FluidViewBuilderInfoBuilder modalView(final ModalView modalView) {
				this.modalView = modalView;
				return this;
			}

			public FluidViewBuilderInfoBuilder insideTableView(final ListViewFluid listViewFluid) {
				this.insideTableView = listViewFluid;
				return this;
			}

			public FluidViewBuilderInfo build() {
				return new FluidViewBuilderInfo(bounds, context,
						dataModelPrefix, listenerId, measurePass, customLayout,
						viewPath, modalView, insideTableView);
			}
		}

		public static FluidViewBuilderInfoBuilder builder() {
			return new FluidViewBuilderInfoBuilder();
		}
	}

	public static SpannableString createAttributedText(String text) {

		AttributedText attributedText = new AttributedText(text);

		SpannableString spanString = new SpannableString(attributedText.getText());
		for (Attribute attribute : attributedText.getAttributes()) {
			if (attribute.isBold() && attribute.isItalic()) {
				spanString.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), attribute.getStartIndex(), attribute.getEndIndex(), 0);
			} else if (attribute.isBold()) {
				spanString.setSpan(new StyleSpan(Typeface.BOLD), attribute.getStartIndex(), attribute.getEndIndex(), 0);
			} else if (attribute.isItalic()) {
				spanString.setSpan(new StyleSpan(Typeface.ITALIC), attribute.getStartIndex(), attribute.getEndIndex(), 0);
			}
			if (attribute.isUnderline()) {
				spanString.setSpan(new UnderlineSpan(), attribute.getStartIndex(), attribute.getEndIndex(), 0);
			}
			
			if (attribute.getCornerRadius() > 0 && attribute.getBackgroundColor() != null && attribute.getColor() != null) {
				int backgroundColor = CustomLayout.getColor(attribute.getBackgroundColor());;
				int textColor = CustomLayout.getColor(attribute.getColor());
				int cornerRadius = attribute.getCornerRadius();
				
				spanString.setSpan(new RoundedBackgroundSpan(backgroundColor, textColor, cornerRadius), attribute.getStartIndex(), attribute.getEndIndex(), 0);
			} else {
				if (attribute.getBackgroundColor() != null) {
					int color = CustomLayout.getColor(attribute.getBackgroundColor());
					spanString.setSpan(new BackgroundColorSpan(color), attribute.getStartIndex(), attribute.getEndIndex(), 0);
				}
				if (attribute.getColor() != null) {
					int color = CustomLayout.getColor(attribute.getColor());
					spanString.setSpan(new ForegroundColorSpan(color), attribute.getStartIndex(), attribute.getEndIndex(), 0);
				}
			}
		}
		return spanString;
	}
	
	

	public static Bitmap getBitmapFor(String imageName, int boundsWidth, int boundsHeight, Resources resources) {
		imageName = GlobalState.fluidApp.getImageManager().getImageName(imageName, boundsWidth,
				boundsHeight);
		return AndroidUtil.getBitmapFromAssets("images", imageName);
	}
}
