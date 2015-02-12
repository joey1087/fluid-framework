package com.sponberg.fluid.android.layout;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.eclipsesource.json.JsonObject;
import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.android.FluidFrameworkAndroidApp;
import com.sponberg.fluid.layout.DataChangeListener;
import com.sponberg.fluid.layout.ViewBehaviorWebView;
import com.sponberg.fluid.util.HtmlUtil;
import com.sponberg.fluid.util.Logger;

public class WebViewFluid extends WebView implements FluidViewAndroid {

	com.sponberg.fluid.layout.ViewPosition view;
	Bounds bounds;
	
	public WebViewFluid(Context context, com.sponberg.fluid.layout.ViewPosition view, Bounds bounds) {
		super(context);
		this.view = view;
		this.bounds = bounds;
    			
		setBackgroundColor(Color.TRANSPARENT);
		
		WebSettings webSettings = getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setUseWideViewPort(true);
		
		setWebViewClient(new MyWebViewClient());
		
		setWebChromeClient(new WebChromeClient());
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		this.setMeasuredDimension(bounds.width, bounds.height);
	}

	public void setViewBounds(Bounds bounds) {
		
		this.bounds = bounds;
		
		int width = bounds.width;
		int height = bounds.height;
		
		DisplayMetrics metrics = new DisplayMetrics();
		FluidFrameworkAndroidApp.getFluidAndroidApp().getCurrentActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		float density = metrics.density;
		
		width /= density;
		height /= density;
		
    	String script = "resizeLayout(" + width + "," + height + ");";
    	loadUrl("javascript:" + script);

    	script = "fluidViewWasUpdated(" + width + "," + height + ");";
    	loadUrl("javascript:" + script);
	}
	
	private class MyWebViewClient extends WebViewClient {
		
		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			
			Logger.error(this, description);
			
			super.onReceivedError(view, errorCode, description, failingUrl);
		}

		@Override
		public WebResourceResponse shouldInterceptRequest(WebView view,
				String url) {

			String data = null;
	    	Uri uri = Uri.parse(url);
	    	String scheme = uri.getScheme();
			if (scheme.equalsIgnoreCase("fluid")) {
				String command = uri.getHost();
				if (command.equalsIgnoreCase("load")) {
					data = ViewBehaviorWebView.getFile(uri.getPathSegments().get(0));
				}
			} else {
				data = ViewBehaviorWebView.getFile(url);
			}
			
			if (data == null) {
				return super.shouldInterceptRequest(view, url);
			} else {
				String mimeType;
				if (url.endsWith(".js")) {
					mimeType = "text/javascript";
				} else if (url.endsWith(".css")) {
					mimeType = "text/css";
				} else {
					return super.shouldInterceptRequest(view, url);
				}
					
				try {
					return new WebResourceResponse(mimeType, "UTF-8", new ByteArrayInputStream(data.getBytes("UTF-8")));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					return super.shouldInterceptRequest(view, url);
				}
			}
		}

		@Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	    	
	    	Uri uri = Uri.parse(url);
	    	String scheme = uri.getScheme();
	    	
	    	if (scheme.equalsIgnoreCase("file")) {
	            return false;
	        } else {
	            if (scheme.equalsIgnoreCase("fluid")) {
	            	
	            	String command = uri.getHost();
	            	
	            	if (command.equals("data")) {
	            		
	                    // If success, then return a json object, where each dataKey will be paired with its data
	                    // If not success, then return an error message string
	                    
	            		String dataKeyString = uri.getPathSegments().get(0);
	            		String[] dataKeys = dataKeyString.split(",");

	            		String[] callbackTokens = uri.getQuery().split("=");

	            		boolean success = true;

	            		JsonObject json = new JsonObject();

	            		String data = null;
	            		if (!callbackTokens[0].equalsIgnoreCase("callback")) {
	            			data = "Invalid query " + uri.getQuery();
	            			success = false;
	            		} else {

	            			for (String dataKey : dataKeys) {
	            				String safeDataKey = dataKey.trim();
	            				data = GlobalState.fluidApp.getDataModelManager().getValue(null, safeDataKey, "{0}", null);
	            				if (data == null) {
	            					success = false;
	            					data = "No data for " + safeDataKey;
	            				} else {
	            					json.add(safeDataKey, data);
	            				}
	            			}
	            		}
	            		
	            		 String dataToReturn = data;
	                     
	                     if (success) {
	                         dataToReturn = json.toString();
	                         
	                         dataToReturn = HtmlUtil.escapeSingleQuote(dataToReturn);
	                         
	                         // dataToReturn may have escapes, but that will trip up javascript. We need to double escape those.
	                         dataToReturn = HtmlUtil.escapeBackslashes(dataToReturn);
	                     }

	                     String callbackId = callbackTokens[1];
	                     
	                     int successInt = success ? 1 : 0;
	                     
				    	String script = "fluidDataCallback('" + callbackId + "','" + successInt + "','" + dataToReturn + "');";
				    	loadUrl("javascript:" + script);
	            		
	            	} else if (command.equals("addDataChangeListener")) {
	            		
	            		List<String> pathComponents = uri.getPathSegments();
	            		String key = pathComponents.get(0);
	            		
	            		String[] callbackTokens = uri.getQuery().split("=");
	            		
	            		boolean success = true;
	            		
	            		if (!callbackTokens[0].equalsIgnoreCase("callback")) {
	            			success = false;
	            		}
	            		
	            		if (success) {
	            			final String callbackId = callbackTokens[1];
	            			GlobalState.fluidApp.getDataModelManager().addDataChangeListener(null, key, callbackId, new DataChangeListener() {
								@Override
								public void dataChanged(final String key,
										final String... subkeys) {
									
									Activity currentActivity = FluidFrameworkAndroidApp.getFluidAndroidApp().getCurrentActivity();
									currentActivity.runOnUiThread(new Runnable() {
									    @Override
									    public void run() {
									    	String script = "dataDidChangeFor('" + callbackId + "','" + key + "','');";
									    	loadUrl("javascript:" + script);
									    }
									});
								}
								@Override
								public void dataRemoved(String arg0) {
									// hstdbc what do do
								}	            				
	            			});
	            		}
	            	}
	            	
	            	loadUrl("javascript:commandFinished();");
	            }
	            return true;
	        }
	    }
	}

	@Override
	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}

	@Override
	public void cleanup() {
	}

}
