package com.sponberg.fluid;

import java.util.HashMap;

import lombok.Getter;

public interface HttpService {

	public void get(String URL, HashMap<String, Object> parameters, HttpAuthorization auth, HttpServiceCallback callback);

	public void getBinary(String URL, HashMap<String, Object> parameters, HttpAuthorization auth, HttpServiceCallback callback);

	public void post(String URL, HashMap<String, Object> parameters, HttpAuthorization auth, HttpServiceCallback callback);

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
