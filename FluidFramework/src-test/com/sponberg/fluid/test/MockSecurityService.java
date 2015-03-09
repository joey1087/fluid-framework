package com.sponberg.fluid.test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.sponberg.fluid.SecurityService;
import com.sponberg.fluid.util.Base64;
import com.sponberg.fluid.util.Logger;

public class MockSecurityService implements SecurityService {

	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	
	@Override
	public String hmacBase64(String data, String key, String salt) {
		
		try {

			SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(),
					HMAC_SHA1_ALGORITHM);

			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			mac.init(signingKey);

			byte[] rawHmac = mac.doFinal((salt + "_" + data).getBytes());
			
			return Base64.encodeBytes(rawHmac);

		} catch (Exception e) {
			Logger.error(this, e);
			return null;
		}
	}
	
	@Override
	public String getUserSalt() {
		return "test";
	}

	@Override
	public PasswordProvider getPasswordProvider() {
		return new PasswordProvider() {
			@Override
			public String getHmacKeyFluidDatastoreParameters() {
				return "testKey";
			}			
		};
	}

	@Override
	public boolean hasUserSalt() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
