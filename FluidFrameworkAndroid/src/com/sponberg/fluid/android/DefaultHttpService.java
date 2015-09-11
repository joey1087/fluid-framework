package com.sponberg.fluid.android;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.HttpService;
import com.sponberg.fluid.HttpServiceCallback;
import com.sponberg.fluid.util.Base64;
import com.sponberg.fluid.util.Logger;

public class DefaultHttpService implements HttpService {

	enum HttpMethod { get, post, put };
	
	public DefaultHttpService() {
		
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
		HttpConnectionParams.setSoTimeout(httpParameters, 10000);
	}
	
	@Override
	public void get(String url, HashMap<String, Object> properties,
			HttpAuthorization auth, HttpServiceCallback callback) {

		HttpRequestConfiguration config = new HttpRequestConfiguration(auth, callback, HttpMethod.get, url);
		config.properties = properties;
		startHttpRequest(config);
	}

	@Override
	public void getBinary(String url, HashMap<String, Object> properties,
			HttpAuthorization auth, HttpServiceCallback callback) {

		HttpRequestConfiguration config = new HttpRequestConfiguration(auth, callback, HttpMethod.get, url);
		config.properties = properties;
		config.binary = true;
		startHttpRequest(config);
	}

	@Override
	public void post(String url, HashMap<String, Object> properties,
			HttpAuthorization auth, HttpServiceCallback callback) {
		
		HttpRequestConfiguration config = new HttpRequestConfiguration(auth, callback, HttpMethod.post, url);
		config.properties = properties;
		startHttpRequest(config);
	}

	@Override
	public void post(String url, HashMap<String, Object> properties, PostBodyType postBodyType, HttpAuthorization auth, HttpServiceCallback callback) {
		
		HttpRequestConfiguration config = new HttpRequestConfiguration(auth, callback, HttpMethod.post, url);
		config.properties = properties;
		config.isMultipart = true;
		startHttpRequest(config);
	}

	@Override
	public void postRaw(String url, String rawPost, HttpAuthorization auth,
			HttpServiceCallback callback) {
		
		HttpRequestConfiguration config = new HttpRequestConfiguration(auth, callback, HttpMethod.post, url);
		config.rawPost = rawPost;
		startHttpRequest(config);
	}
	
	@Override
	public void put(String url, HashMap<String, Object> properties,
			HttpAuthorization auth, HttpServiceCallback callback) {
		
		HttpRequestConfiguration config = new HttpRequestConfiguration(auth, callback, HttpMethod.put, url);
		config.properties = properties;
		startHttpRequest(config);
	}
	
	protected void startHttpRequest(final HttpRequestConfiguration config) {

		if (GlobalState.fluidApp.getSystemService().isOnUiThread()) {
			
			new RequestTask(config.rawPost, config.properties, config.auth, config.callback, config.httpMethod, config.isMultipart, config.binary).execute(config.url);
		} else {
			
			Runnable r = new Runnable() {
				public void run() {
					new RequestTask(config.rawPost, config.properties, config.auth, config.callback, config.httpMethod, config.isMultipart, config.binary).execute(config.url);
				}
			};
			GlobalState.fluidApp.getSystemService().runOnUiThread(r);
		}
		
	}
	
	class RequestTask extends AsyncTask<String, String, String> {

		final String rawPost;
		
		final HashMap<String, Object> properties;

		final HttpAuthorization auth;

		final HttpServiceCallback callback;

		final HttpMethod httpMethod;
		
		final boolean isMultipart;
		
		final boolean binary;
		
		boolean success = false;

		int statusCode = -1;
		
		public RequestTask(String rawPost, HashMap<String, Object> properties,
				HttpAuthorization auth, HttpServiceCallback callback, HttpMethod httpMethod, boolean isMultipart, boolean binary) {

			this.rawPost = rawPost;
			this.properties = properties;
			this.auth = auth;
			this.callback = callback;
			this.httpMethod = httpMethod;
			this.isMultipart = isMultipart;
			this.binary = binary;
		}

		@Override
		protected String doInBackground(String... uri) {
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = null;
			String responseString = null;
			try {
				if (httpMethod == HttpMethod.post) {
					if (isMultipart) {
						try {
							HttpPost post = new HttpPost(uri[0]);
							MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
							entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		
							if (properties != null) {
								for (String key : properties.keySet()) {
									Object value = properties.get(key);
									if (value instanceof String || value instanceof Number) {
										entityBuilder.addTextBody(key, String.valueOf(value));
									} else if (value instanceof Bitmap) {
										ByteArrayOutputStream baos = new ByteArrayOutputStream();
							            ((Bitmap)value).compress(Bitmap.CompressFormat.JPEG, 100, baos);
							            byte[] imageBytes = baos.toByteArray();
										entityBuilder.addBinaryBody(key, imageBytes, ContentType.create("image/jpeg"), "temp");
										//entityBuilder.addBinaryBody(name, file, contentType, filename)
									}
								}
								HttpEntity entity = entityBuilder.build();
								post.setEntity(entity);
							}
							
						    response = httpclient.execute(post);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						HttpPost post = new HttpPost(uri[0]);
						if (rawPost != null) {
							post.setEntity(new StringEntity(rawPost));	
						} else if (properties != null) {
							ArrayList<NameValuePair> pairs = new ArrayList<>();
							for (String key : properties.keySet())
								pairs.add(new BasicNameValuePair(key, properties.get(key).toString()));
							post.setEntity(new UrlEncodedFormEntity(pairs));
						}
						
						response = httpclient.execute(post);
					}
				} else if (httpMethod == HttpMethod.put) {
					HttpPut put = new HttpPut(uri[0]);
					if (properties != null) {
						ArrayList<NameValuePair> pairs = new ArrayList<>();
						for (String key : properties.keySet())
							pairs.add(new BasicNameValuePair(key, properties.get(key).toString()));
						put.setEntity(new UrlEncodedFormEntity(pairs));
					}
					
					response = httpclient.execute(put);					
				} else {
					String paramString = uri[0];
					if (properties != null) {
						ArrayList<NameValuePair> pairs = new ArrayList<>();
						for (String key : properties.keySet())
							pairs.add(new BasicNameValuePair(key, properties.get(key).toString()));
						paramString += "?" + URLEncodedUtils.format(pairs, "utf-8");
					}
					
					response = httpclient.execute(new HttpGet(paramString));					
				}
				
				StatusLine statusLine = response.getStatusLine();
				statusCode = statusLine.getStatusCode();
				
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				if (binary) {
					responseString = Base64.encodeBytes(out.toByteArray());
				} else {
					responseString = out.toString();
				}
				success = true;
			} catch (ClientProtocolException e) {
				Logger.error(this, e);
				responseString = "Error getting data from server";
			} catch (IOException e) {
				Logger.error(this, e);
				responseString = "Error getting data from server";
			}
			return responseString;
		}

		@Override
		protected void onPostExecute(String result) {
			
			if (success)
				callback.success(new HttpServiceCallback.HttpResponse(result, statusCode));
			else
				callback.fail(new HttpServiceCallback.HttpResponse(result, statusCode));
		}
	}
	
	private class HttpRequestConfiguration {
		
		public String rawPost = null;
		public HashMap<String, Object> properties = null;
		public HttpAuthorization auth = null;
		public HttpServiceCallback callback = null;
		public HttpMethod httpMethod = HttpMethod.get;
		public boolean isMultipart = false;
		public String url = null;
		public boolean binary = false;
		
		public HttpRequestConfiguration (HttpAuthorization auth, HttpServiceCallback callback, HttpMethod httpMethod, String url) {
			
			this.auth = auth;
			this.callback = callback;
			this.httpMethod = httpMethod;
			this.url = url;
		}
	}
}
