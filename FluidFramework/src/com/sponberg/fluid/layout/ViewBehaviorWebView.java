package com.sponberg.fluid.layout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.util.KeyValueList;

@ToString
@Getter
@Setter
public class ViewBehaviorWebView extends ViewBehavior {

	private ArrayList<FilePart> internalHtml;

	static final boolean useInline = false;

	static boolean useCache = true;

	private static ArrayList<FilePart> topSection;
	private static ArrayList<FilePart> bottomSection;
	private static HashMap<String, String> cachedResources = new HashMap<>();

	static {
		topSection = generateTopSection();
		bottomSection = generateBottomSection();
		preloadAssets();
	}

	static void preloadAssets() {
		if (useCache) {
			getFile("jquery-2.1.0.min.js");
			getFile("fastclick.js");
			getFile("fluid.js");
			getFile("html5-doctor-reset-stylesheet.css");
			getFile("lesswebby.css");
		}
	}

	public ViewBehaviorWebView(KeyValueList properties) {
		super(ViewBehavior.webview, properties);

		if (!properties.contains("html")) {
			throw new RuntimeException("WebView must contain 'html' that references the html file for the view");
		}

		this.internalHtml = generateInternalHtml(getStringProperty("html", null, properties));
	}

	public String getHtml() {
		StringBuilder buf = new StringBuilder();
		for (FilePart p : topSection) {
			buf.append(p.getHtml());
		}
		for (FilePart p : internalHtml) {
			buf.append(p.getHtml());
		}
		for (FilePart p : bottomSection) {
			buf.append(p.getHtml());
		}
		return buf.toString();
	}

	private static ArrayList<FilePart> generateTopSection() {

		ArrayList<FilePart> list = new ArrayList<>();

		StringBuilder buf = new StringBuilder();

		buf.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");

		buf.append("<html>");

		buf.append("<head>");

		buf.append("<title>Fluid View</title>");

		buf.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, user-scalable=no\"></meta>");

		if (useInline) {

			buf.append("<script type=\"text/javascript\">");

			list.add(new StringFilePart(buf.toString()));
			buf = new StringBuilder();

			list.add(new InlineResourceFilePart("jquery-2.1.0.min.js"));
			list.add(new InlineResourceFilePart("fastclick.js"));
			list.add(new InlineResourceFilePart("fluid.js"));

			buf.append("</script>");
		} else {

			buf.append("<script type=\"text/javascript\" src=\"fluid://load/jquery-2.1.0.min.js\"></script>");
			buf.append("<script type=\"text/javascript\" src=\"fluid://load/fastclick.js\"></script>");
			buf.append("<script type=\"text/javascript\" src=\"fluid://load/fluid.js\"></script>");
		}

		if (useInline) {

			buf.append("<style type=\"text/css\">");

			list.add(new StringFilePart(buf.toString()));
			buf = new StringBuilder();

			list.add(new InlineResourceFilePart("html5-doctor-reset-stylesheet.css"));
			list.add(new InlineResourceFilePart("lesswebby.css"));

			buf.append("</style>");
		} else {

			buf.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"fluid://load/html5-doctor-reset-stylesheet.css\">");
			buf.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"fluid://load/lesswebby.css\">");
		}

		buf.append("</head>");

		buf.append("<body>");

		list.add(new StringFilePart(buf.toString()));

		return list;
	}

	private static ArrayList<FilePart> generateBottomSection() {

		ArrayList<FilePart> list = new ArrayList<>();

		StringBuilder buf = new StringBuilder();

		buf.append("</body>");

		buf.append("</html>");

		list.add(new StringFilePart(buf.toString()));

		return list;
	}

	private static ArrayList<FilePart> generateInternalHtml(String fileName) {
		String html = GlobalState.fluidApp.getResourceService().getResourceAsString("webview", fileName + ".html");

		if (useInline) {

			try {
				return generateHtmlInlineResources(html);
			} catch (IOException e) {
				e.printStackTrace();
				return new ArrayList<FilePart>();
			}
		} else {

			ArrayList<FilePart> list = new ArrayList<>();
			list.add(new StringFilePart(html));
			return list;
		}
	}

	private static ArrayList<FilePart> generateHtmlInlineResources(String html) throws IOException {

		ArrayList<FilePart> internalHtml = new ArrayList<>();

		StringBuilder buf = new StringBuilder();
		BufferedReader in = new BufferedReader(new StringReader(html));
		String line;
		while ( (line = in.readLine()) != null) {
			boolean finishedWithLine;
			do {
				finishedWithLine = true;
				int i = line.indexOf("<script");
				if (i != -1) {
					int i2 = line.indexOf(">", i);
					int i3 = line.indexOf("src=", i);
					if (i3 != -1 && i3 < i2) {
						int i4 = line.indexOf("\"", i3);
						int i5 = line.indexOf("\"", i4 + 1);
						String filename = line.substring(i4 + 1, i5).trim();
						buf.append(line.substring(0,  i));
						buf.append("<script>");
						internalHtml.add(new StringFilePart(buf.toString()));
						buf = new StringBuilder();
						buf.append("</script>");

						String resource = getFile(filename);
						if (resource == null) {
							throw new RuntimeException("Unable to find file " + filename);
						}
						internalHtml.add(new InlineResourceFilePart(filename));

						int i6 = line.indexOf("</script>", i5);
						if (i6 == -1) {
							throw new RuntimeException("Expected </script>");
						}
						line = line.substring(i6 + 9);
						finishedWithLine = false;
					}
				}
			} while (!finishedWithLine);
			buf.append(line + "\n");
		}
		if (buf.length() > 0) {
			internalHtml.add(new StringFilePart(buf.toString()));
		}

		return internalHtml;
	}

	public static String getFile(String name) {

		if (!(name.endsWith(".js") || name.endsWith(".css"))) {
			return null;
		}

		if (useCache) {
			String data = cachedResources.get(name);
			if (data != null) {
				return data;
			}
		}

		String resource = GlobalState.fluidApp.getResourceService().getResourceAsString("webview", name);
		if (useCache) {
			cachedResources.put(name, resource);
		}
		return resource;
	}

	static interface FilePart {
		String getHtml();
	}

	static class StringFilePart implements FilePart {
		final String html;
		public StringFilePart(String html) {
			this.html = html;
		}
		@Override
		public String getHtml() {
			return html;
		}
	}

	static class InlineResourceFilePart implements FilePart {
		final String key;
		public InlineResourceFilePart(String key) {
			this.key = key;
			if (useCache) {
				if (!cachedResources.containsKey(key)) {
					getFile(key);
				}
			}
		}
		@Override
		public String getHtml() {
			String html = cachedResources.get(key);
			return html;
		}
	}

}
