package com.sponberg.fluid;

import java.util.HashMap;

import com.sponberg.fluid.HttpServiceWrapper.MapMode;

import lombok.Getter;

public interface HttpService {

	public enum PostBodyType {
		JsonString,
		FormData
	}
	
	public void get(String URL, HashMap<String, Object> parameters, HttpAuthorization auth, HttpServiceCallback callback);

	public void getBinary(String URL, HashMap<String, Object> parameters, HttpAuthorization auth, HttpServiceCallback callback);

	/**
	 * This post will use json string as http body 
	 * 
	 * @param URL
	 * @param parameters
	 * @param auth
	 * @param callback
	 */
	public void post(String URL, HashMap<String, Object> parameters, HttpAuthorization auth, HttpServiceCallback callback);

	/**
	 * This port will use multipart form data as a http body 
	 * 
	 * @param URL
	 * @param parameters
	 * @param postBodyType
	 * @param auth
	 * @param callback
	 */
	public void post(String URL, HashMap<String, Object> parameters, PostBodyType postBodyType, HttpAuthorization auth, HttpServiceCallback callback);
	
	public void post(String URL, HashMap<String, Object> parameters, PostBodyType postBodyType, MapMode mapMode, HttpAuthorization auth, HttpServiceCallback callback);
	
	public void put(String URL, HashMap<String, Object> parameters, HttpAuthorization auth, HttpServiceCallback callback);
	
	public void postRaw(String URL, String rawMessage, HttpAuthorization auth, HttpServiceCallback callback);

	@Getter
	public static class HttpAuthorization {
		
		final String username;
		
		final String password;
		
		public HttpAuthorization(String username, String password) {
			this.username = username;
			this.password = password;
		}
				
	}
	
}
