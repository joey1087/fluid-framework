package com.sponberg.fluid;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.eclipsesource.json.JsonObject;
import com.sponberg.fluid.util.Logger;
import com.sponberg.fluid.util.PrettyPrint;

import lombok.Data;

@Data
public class HttpServiceWrapper implements HttpService {

	final HttpService httpService;

	public enum MapMode {
		Jsonify,
		Bracketify,
		JsonifyNative,
	}
	
	private MapMode mapMode = MapMode.Jsonify;
	
	public HttpServiceWrapper(HttpService httpService) {
		this.httpService = httpService;
	}

	@Override
	public void get(String URL, HashMap<String, Object> parameters,
			HttpAuthorization auth, HttpServiceCallback callback) {
		
		Logger.debug(this, "Http Get {} {}", URL, PrettyPrint.toString(parameters));

		if (parameters != null) {
			if (mapMode == MapMode.Jsonify) {
				parameters = jsonifyMaps(parameters);
			} else if (mapMode == MapMode.Bracketify) {
				parameters = bracketifyMaps(parameters);
			}
		}

		httpService.get(URL, parameters, auth, callback);
	}

	@Override
	public void getBinary(String URL, HashMap<String, Object> parameters,
			HttpAuthorization auth, HttpServiceCallback callback) {
		
		Logger.debug(this, "Http Get Binary {} {}", URL, PrettyPrint.toString(parameters));

		if (parameters != null) {
			if (mapMode == MapMode.Jsonify) {
				parameters = jsonifyMaps(parameters);
			} else if (mapMode == MapMode.Bracketify) {
				parameters = bracketifyMaps(parameters);
			} else if (mapMode == MapMode.JsonifyNative) {
				parameters = jsonifyNativeMaps(parameters);
			}
		}

		httpService.getBinary(URL, parameters, auth, callback);
	}	
	
	@Override
	public void post(String URL, HashMap<String, Object> parameters, HttpAuthorization auth,
			HttpServiceCallback callback) {

		Logger.debug(this, "Http Post {} {}", URL, PrettyPrint.toString(parameters));

		if (mapMode == MapMode.Jsonify) {
			parameters = jsonifyMaps(parameters);
		} else if (mapMode == MapMode.Bracketify) {
			parameters = bracketifyMaps(parameters);
		} else if (mapMode == MapMode.JsonifyNative) {
			parameters = jsonifyNativeMaps(parameters);
		}
		
		httpService.post(URL, parameters, auth, callback);
	}
	
	@Override
	public void post(String URL, HashMap<String, Object> parameters,
			PostBodyType postBodyType, HttpAuthorization auth,
			HttpServiceCallback callback) {
		Logger.debug(this, "Http Post {} {}", URL, PrettyPrint.toString(parameters));

		if (mapMode == MapMode.Jsonify) {
			parameters = jsonifyMaps(parameters);
		} else if (mapMode == MapMode.Bracketify) {
			parameters = bracketifyMaps(parameters);
		} else if (mapMode == MapMode.JsonifyNative) {
			parameters = jsonifyNativeMaps(parameters);
		}
		
		httpService.post(URL, parameters, postBodyType, auth, callback);
	}
	
	@Override
	public void post(String URL, HashMap<String, Object> parameters, PostBodyType postBodyType, 
			MapMode mapMode, HttpAuthorization auth, HttpServiceCallback callback) {
		Logger.debug(this, "Http Post {} {}", URL, PrettyPrint.toString(parameters));
		if (mapMode == MapMode.Jsonify) {
			parameters = jsonifyMaps(parameters);
		}
		httpService.post(URL, parameters, postBodyType, auth, callback);		
	}	
	
	@Override
	public void postRaw(String URL, String rawPost, HttpAuthorization auth,
			HttpServiceCallback callback) {

		Logger.debug(this, "Http Post {} {}", URL, rawPost);

		httpService.postRaw(URL, rawPost, auth, callback);
	}	
	
	@Override
	public void put(String URL, HashMap<String, Object> parameters, HttpAuthorization auth,
			HttpServiceCallback callback) {
		
		Logger.debug(this, "Http Put {} {}", URL, PrettyPrint.toString(parameters));

		if (mapMode == MapMode.Jsonify) {
			parameters = jsonifyMaps(parameters);
		} else if (mapMode == MapMode.Bracketify) {
			parameters = bracketifyMaps(parameters);
		}
		
		httpService.put(URL, parameters, auth, callback);
	}
	
	@SuppressWarnings("unchecked")
	protected static HashMap<String, Object> jsonifyNativeMaps(Map<String, Object> parameters) {
		HashMap<String, Object> map = new HashMap<>();
		for (Entry<String, Object> entry : parameters.entrySet()) {
			Object value = entry.getValue();
			map.put(entry.getKey(), value);
		}
		return map;
	}
	
	@SuppressWarnings("unchecked")
	protected static HashMap<String, Object> jsonifyMaps(Map<String, Object> parameters) {
		HashMap<String, Object> map = new HashMap<>();
		for (Entry<String, Object> entry : parameters.entrySet()) {
			Object value = entry.getValue();
			
			if (value instanceof Map) {				
				map.put(entry.getKey(), jsonifyMapsHelper((Map<String, Object>) value));
			} else {
				map.put(entry.getKey(), value);
			}
		}
		return map;
	}	
	
	@SuppressWarnings("unchecked")
	protected static JsonObject jsonifyMapsHelper(Map<String, Object> parameters) {
		JsonObject json = new JsonObject();
		for (Entry<String, Object> entry : parameters.entrySet()) {
			if (entry.getValue() instanceof Map) {
				json.add(entry.getKey(), jsonifyMapsHelper((Map<String, Object>) entry.getValue()));
			} else {
				json.add(entry.getKey(), entry.getValue().toString());
			}
		}
		return json;	
	}
	
	@SuppressWarnings("unchecked")
	protected static HashMap<String, Object> bracketifyMaps(Map<String, Object> parameters) {
		HashMap<String, Object> map = new HashMap<>();
		for (Entry<String, Object> entry : parameters.entrySet()) {
			Object value = entry.getValue();
			if (value instanceof Map) {
				bracketifyMapsHelper(map, entry.getKey(), (Map<String, Object>) value);
			} else {
				map.put(entry.getKey(), value);
			}
		}
		return map;
	}
	
	@SuppressWarnings("unchecked")
	protected static void bracketifyMapsHelper(HashMap<String, Object> map, String prefix, Map<String, Object> parameters) {
		for (Entry<String, Object> entry : parameters.entrySet()) {
			Object value = entry.getValue();
			if (value instanceof Map) {
				bracketifyMapsHelper(map, prefix + "[" + entry.getKey() + "]", (Map<String, Object>) value);
			} else {
				map.put(prefix + "[" + entry.getKey() + "]", value);
			}
		}
	}

	
}
