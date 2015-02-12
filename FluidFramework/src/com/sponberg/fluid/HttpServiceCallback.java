package com.sponberg.fluid;

import lombok.Getter;


public interface HttpServiceCallback {

	public void success(HttpResponse response);
	
	public void fail(HttpResponse response);

	@Getter
	public static class HttpResponse {
		
		// If binary, this will be base 64 encoded
		// If error, this will be an error message
		// Otherwise, this is the text of the response body
		String data; 
		
		int code;
		
		public HttpResponse(String data, int code) {
			this.data = data;
			this.code = code;
		}
		
	}
	
}
