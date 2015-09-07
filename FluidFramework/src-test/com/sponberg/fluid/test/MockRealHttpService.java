package com.sponberg.fluid.test;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import com.sponberg.fluid.HttpService;
import com.sponberg.fluid.HttpServiceCallback;
import com.sponberg.fluid.HttpServiceCallback.HttpResponse;
import com.sponberg.fluid.util.Base64;
import com.sponberg.fluid.util.Logger;
import com.sponberg.fluid.util.StreamUtil;

public class MockRealHttpService implements HttpService {

	boolean kUseTestFile = false;
	
	@Override
	public void get(String URL, HashMap<String, Object> parameters,
			HttpService.HttpAuthorization auth, HttpServiceCallback callback) {
		
		HttpURLConnection connection = null;
		int responseCode = -1;
		try {

			InputStream content = null;
			
			if (kUseTestFile) {
				content = new FileInputStream("resources/testDataPoints.txt");
				responseCode = 200;
			} else {
			
				URL url = new URL(URL);
	
	            connection = (HttpURLConnection) url.openConnection();
	            connection.setRequestMethod("GET");
	            connection.setDoOutput(true);
	            
	            if (auth != null) {
		            String authString = auth.getUsername() + ":" + auth.getPassword();
		            String encoding = Base64.encodeBytes(authString.getBytes());
		            connection.setRequestProperty("Authorization", "Basic " + encoding);
	            }
	
	            responseCode = connection.getResponseCode();
	            content = connection.getInputStream();
			}
            
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            
            StreamUtil.copyInputStream(content, out);

            String nonBinary = new String(out.toByteArray(), "UTF-8");
            callback.success(new HttpResponse(nonBinary, responseCode));
		
		} catch (Exception e) {
			Logger.info(this, e);
			
            String message = e.getMessage();
            if (connection != null) {
            	InputStream errorStream = connection.getErrorStream();
            	if (errorStream != null) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    try {
	                    StreamUtil.copyInputStream(errorStream, out);
	                    message = new String(out.toByteArray(), "UTF-8");
                    } catch (Exception e2) {
                    	// Ignore
                    }
            	}
            }
            
			callback.fail(new HttpResponse(message, responseCode));
        }
	}

	@Override
	public void post(String URL, HashMap<String, Object> parameters,
			HttpService.HttpAuthorization auth, HttpServiceCallback callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void put(String URL, HashMap<String, Object> parameters,
			HttpService.HttpAuthorization auth, HttpServiceCallback callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getBinary(String URL, HashMap<String, Object> parameters,
			HttpAuthorization auth, HttpServiceCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postRaw(String URL, String rawMessage, HttpAuthorization auth,
			HttpServiceCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void post(String URL, HashMap<String, Object> parameters,
			PostBodyType postBodyType, HttpAuthorization auth,
			HttpServiceCallback callback) {
		// TODO Auto-generated method stub
		
	}

}
