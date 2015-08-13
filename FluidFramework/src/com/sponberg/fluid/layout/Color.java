package com.sponberg.fluid.layout;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@EqualsAndHashCode
public class Color {

	final double red, green, blue, alpha;
			
	public Color(double red, double green, double blue, double alpha) {
		
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}
	
	public Color(int red, int green, int blue, int alpha) {
		
		this.red = red * 1.0 / 255;
		this.green = green * 1.0 / 255;
		this.blue = blue * 1.0 / 255;
		this.alpha = alpha * 1.0 / 255;
	}
	
	protected Color(String[] rgb) {
		
		this(Integer.parseInt(rgb[0].trim()), Integer
				.parseInt(rgb[1].trim()), Integer.parseInt(rgb[2].trim()), getAlpha(rgb));
	}
	
	public static int getAlpha(String[] rgb) {
		
		if (rgb == null) {
			return 255;
		}
		
		if (rgb.length == 4) {
			return Integer.parseInt(rgb[3].trim());
		} else {
			return 255;
		}
	}
	
	// in case of errors, return the default color (white color)
	public static Color colorFromString(String colorAsString) {
		
		try {
			boolean useHtml = !colorAsString.contains(",") || colorAsString.startsWith("#");
			if (colorAsString.startsWith("#")) {
				colorAsString = colorAsString.substring(1);
			}
			if (useHtml) {
				int red, green, blue;
				int alpha = 255;
				switch (colorAsString.length()) {
				case 6:
					red = Integer.parseInt(colorAsString.substring(0, 2), 16);
					green = Integer.parseInt(colorAsString.substring(2, 4), 16);
					blue = Integer.parseInt(colorAsString.substring(4, 6), 16);
					break;
				case 3:
					red = Integer.parseInt(colorAsString.substring(0, 1), 16);
					green = Integer.parseInt(colorAsString.substring(1, 2), 16);
					blue = Integer.parseInt(colorAsString.substring(2, 3), 16);
					break;
				case 8:
					red = Integer.parseInt(colorAsString.substring(0, 2), 16);
					green = Integer.parseInt(colorAsString.substring(2, 4), 16);
					blue = Integer.parseInt(colorAsString.substring(4, 6), 16);
					alpha = Integer.parseInt(colorAsString.substring(6, 8), 16);
					break;
				case 4:
					red = Integer.parseInt(colorAsString.substring(0, 1), 16);
					green = Integer.parseInt(colorAsString.substring(1, 2), 16);
					blue = Integer.parseInt(colorAsString.substring(2, 3), 16);
					alpha = Integer.parseInt(colorAsString.substring(3, 4), 16);
					break;
				default:
					return getDefaultColor();
//					throw new IllegalArgumentException("Invalid color: "
//							+ colorAsString);
				}
				return new Color(red, green, blue, alpha);
			} else {
				String[] ca = colorAsString.split(",");
				if (ca.length != 3 && ca.length != 4) {
//					throw new IllegalArgumentException("Invalid color: " + colorAsString);
					return getDefaultColor();
				} else {
					return new Color(colorAsString.split(","));
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
//			throw new IllegalArgumentException("Invalid color: " + colorAsString);
			return getDefaultColor();
		}
	}
	
	public static Color getDefaultColor() {
		
		// default color is white
		Color defaultColor = new Color(255, 255, 255, 255);
		
		return defaultColor;
	}
	
	public String getHtml() {
		
		return String.format("#%02x%02x%02x", (int) (red * 255), (int) (green * 255), (int) (blue * 255));
	}
	
}
