package com.sponberg.fluid;

public interface SecurityService {

	public String hmacBase64(String data, String key, String salt);
	
	public boolean hasUserSalt();
	
	public String getUserSalt();
	
	public PasswordProvider getPasswordProvider();
	
	public static interface PasswordProvider {

		public String getHmacKeyFluidDatastoreParameters();
				
	}
	
}
