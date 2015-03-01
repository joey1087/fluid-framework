package com.sponberg.fluid.layout;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.util.KeyValueList;

@ToString
@Getter
@Setter
public class ViewBehaviorImage extends ViewBehavior {

	private ArrayList<ImageWithCondition> images = new ArrayList<>();

	private boolean fill = false;

	private String align;

	private String verticalAlign;

	private double marginTop;

	private Double maxWidth;

	private Double maxHeight;
	
	private Color tintColor;

	private String tintColorKey;
	
	public ViewBehaviorImage(KeyValueList properties) {
		super(ViewBehavior.image, properties);
		makeImages(properties);
		this.fill = getBooleanProperty("fill", false, properties);
		this.align = getStringProperty("align", "c", properties);
		this.verticalAlign = getStringProperty("vertical-align", "m", properties);
		this.marginTop = GlobalState.fluidApp.unitsToPixels(getDoubleProperty("margin-top", 0d, properties));
		this.maxWidth = getSizeProperty("image-max-width", null, properties);
		this.maxHeight = getSizeProperty("image-max-height", null, properties);
		this.tintColor = getColorProperty("tint-color", null, properties);
		this.tintColorKey = getStringProperty("tint-color-key", null, properties);
	}

	private void makeImages(KeyValueList properties) {
		for (KeyValueList imageKvl : properties.get("image")) {
			ImageWithCondition image = new ImageWithCondition();
			images.add(image);
			image.image = imageKvl.getValue();
			if (imageKvl.contains("condition")) {
				image.condition = imageKvl.getValue("condition");
			}
		}
	}

	public String getImageWith(String dataModelPrefix) {
		for (ImageWithCondition i : images) {
			if (i.condition == null) {
				return i.image;
			} else if (GlobalState.fluidApp.getDataModelManager().checkCondition(i.condition, dataModelPrefix)) {
				return i.image;
			}
		}
		return null;
	}

	public ImageBounds getImageBounds(String imageName, double boundsWidth, double boundsHeight) {
		double imageAspectRatio = GlobalState.fluidApp.getImageManager().getImageAspectRatio(imageName);
		return getImageWithBounds(boundsWidth, boundsHeight, imageAspectRatio);
	}

	public ImageBounds getImageWithBounds(double boundsWidth, double boundsHeight, double imageAspectRatio) {

		double width = boundsWidth;
		double height = boundsHeight;

		height -= this.getMarginTop();

		double boundsAspectRatio = width / height;

		double x = 0;
		double y = 0;

		if (!fill) {

			if (imageAspectRatio > boundsAspectRatio) {
				// width is limiting factor
				height = boundsWidth / imageAspectRatio;
			} else {
				// height is limiting factor
				width = boundsHeight * imageAspectRatio;
			}

			if (maxWidth != null && width > maxWidth) {
				double ratio = maxWidth / width;
				width *= ratio;
				height *= ratio;
			}

			if (maxHeight != null && height > maxHeight) {
				double ratio = maxHeight / height;
				width *= ratio;
				height *= ratio;
			}

			String align = this.getAlign();
			if (align.equalsIgnoreCase("left")) {
				x = 0;
			} else if (align.equalsIgnoreCase("right")) {
				x = boundsWidth - width;
			} else {
				x = (boundsWidth - width) / 2;
			}

			String vAlign = this.getVerticalAlign();
			if (vAlign.equalsIgnoreCase("top")) {
				y = 0;
			} else if (vAlign.equalsIgnoreCase("bottom")) {
				y = boundsHeight - height;
			} else {
				y = (boundsHeight - height) / 2;
			}

			y += this.getMarginTop();
		}

		return new ImageBounds((int) x, (int) y, (int) width, (int) height);
	}

	@Getter
	public static class ImageWithCondition {

		String image;

		String condition;

	}

	@Getter
	@ToString
	public static class ImageBounds {

		final int x, y, width, height;

		public ImageBounds(int x, int y, int width, int height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

	}

}
