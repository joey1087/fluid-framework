package com.sponberg.fluid.android;

import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import android.content.Context;
import android.content.SharedPreferences;

import com.sponberg.fluid.SecurityService;
import com.sponberg.fluid.util.Base64;
import com.sponberg.fluid.util.Logger;

public class DefaultSecurityService implements SecurityService {

	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	
	private final Context context;
	
	private final PasswordProvider passwordProvider;
	
	public DefaultSecurityService(Context context, PasswordProvider passwordProvider) {
		this.context = context;
		if (passwordProvider == null) {
			this.passwordProvider = new PasswordProvider() {
				@Override
				public String getHmacKeyFluidDatastoreParameters() {
					return "9823[]zxcvoiuyHSfoiuweh823#$2asdpf)...";
				}				
			};
		} else {
			this.passwordProvider = passwordProvider;
		}
	}
	
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
		
		SharedPreferences preferences = context.getSharedPreferences("FluidAppPreferences", Context.MODE_PRIVATE);
		String salt = preferences.getString("fluidUS", null);
		if (salt == null) {
			salt = UUID.randomUUID().toString();
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("fluidUS", salt);
			editor.commit();
		}
		return salt;
	}

	@Override
	public PasswordProvider getPasswordProvider() {
		return passwordProvider;
	}

	@Override
	public boolean hasUserSalt() {
		SharedPreferences preferences = context.getSharedPreferences("FluidAppPreferences", Context.MODE_PRIVATE);
		String salt = preferences.getString("fluidUS", null);
		return salt != null;
	}
	
}
