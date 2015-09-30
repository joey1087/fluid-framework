package com.sponberg.fluid.android;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

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

		startHttpRequest(properties, auth, callback, HttpMethod.get, url, false);
	}

	@Override
	public void getBinary(String url, HashMap<String, Object> properties,
			HttpAuthorization auth, HttpServiceCallback callback) {

		startHttpRequest(properties, auth, callback, HttpMethod.get, url, true);
	}

	@Override
	public void post(String url, HashMap<String, Object> properties,
			HttpAuthorization auth, HttpServiceCallback callback) {
		
		startHttpRequest(properties, auth, callback, HttpMethod.post, url, false);
	}

	@Override
	public void postRaw(String url, String rawPost, HttpAuthorization auth,
			HttpServiceCallback callback) {
		
		startHttpRequest(rawPost, auth, callback, HttpMethod.post, url, false);
	}
	
	@Override
	public void put(String url, HashMap<String, Object> properties,
			HttpAuthorization auth, HttpServiceCallback callback) {
		
		startHttpRequest(properties, auth, callback, HttpMethod.put, url, false);
	}

	protected void startHttpRequest(final HashMap<String, Object> properties,
				final HttpAuthorization auth, final HttpServiceCallback callback, 
				final HttpMethod httpMethod, final String url, final boolean binary) {
		
		startHttpRequest(null, properties, auth, callback, httpMethod, url, binary);
	}
	protected void startHttpRequest(final String rawPost,
			final HttpAuthorization auth, final HttpServiceCallback callback, 
			final HttpMethod httpMethod, final String url, final boolean binary) {
		
		startHttpRequest(rawPost, null, auth, callback, httpMethod, url, binary);
	}

	protected void startHttpRequest(final String rawPost, final HashMap<String, Object> properties,
			final HttpAuthorization auth, final HttpServiceCallback callback, 
			final HttpMethod httpMethod, final String url, final boolean binary) {

		if (GlobalState.fluidApp.getSystemService().isOnUiThread()) {
			
			new RequestTask(rawPost, properties, auth, callback, httpMethod, binary).execute(url);
		} else {
			
			Runnable r = new Runnable() {
				public void run() {
					new RequestTask(rawPost, properties, auth, callback, httpMethod, binary).execute(url);
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
		
		final boolean binary;
		
		boolean success = false;

		int statusCode = -1;
		
		public RequestTask(String rawPost, HashMap<String, Object> properties,
				HttpAuthorization auth, HttpServiceCallback callback, HttpMethod httpMethod,
				boolean binary) {

			this.rawPost = rawPost;
			this.properties = properties;
			this.auth = auth;
			this.callback = callback;
			this.httpMethod = httpMethod;
			this.binary = binary;
		}

		@Override
		protected String doInBackground(String... uri) {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response;
			String responseString = null;
			try {
				
				if (httpMethod == HttpMethod.post) {
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
			} catch (NullPointerException e) {
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

}
