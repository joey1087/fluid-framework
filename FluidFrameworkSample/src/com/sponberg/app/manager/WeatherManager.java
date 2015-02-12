package com.sponberg.app.manager;

import lombok.Getter;
import lombok.Setter;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonValue;
import com.sponberg.app.domain.WeatherDataPoint;
import com.sponberg.fluid.ApplicationLoader;
import com.sponberg.fluid.FluidApp;
import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.HttpServiceCallback;
import com.sponberg.fluid.layout.TableList;
import com.sponberg.fluid.util.JsonUtil;

@Getter
@Setter
public class WeatherManager implements ApplicationLoader {
	
	TableList<WeatherDataPoint> dataPoints = new TableList<>();
	
	boolean useFakeData = true;
	
	static final int kFetchDataTimeout = 3000;
	
	@Override
	public void load(FluidApp app) {
		downloadWeatherAsync(app);
	}

	public void downloadWeatherAsync(final FluidApp app) {
		
		if (useFakeData) {
			getDataFromTestFile();
			return;
		}
		
		HttpServiceCallback callback = new HttpServiceCallback() {

			@Override
			public void success(final HttpResponse response) {
				
				Runnable r =  new Runnable() {
					@Override
					public void run() {
						// hstdbc delay for testing Thread.sleep(3000);
						if (response.getCode() == 200) {
							parseData(response.getData());
						} else {
							System.out.println("unsuccessful " + response.getCode() + " " + response.getData());
						}
					}
				};
				app.getSystemService().runOnUiThread(r);
			}

			@Override
			public void fail(HttpResponse response) {
				System.out.println("fail " + response.getData());
			}			
		};
		
		//HashMap<String, Object> parameters = new HashMap<>();
		//parameters.put("limit", 140);
		//getHttpService().get(url, parameters, new HttpService.HttpAuthorization(username, password), callback);
		
		String url = "https://api.forecast.io/forecast/0eda8b99b6e311d2f3c62aa8eb77cad4/-33.86,151.2111";
		GlobalState.fluidApp.getHttpService().get(url, null, null, callback);	
	}
	
	private void getDataFromTestFile() {
		
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(kFetchDataTimeout);
					String data = GlobalState.fluidApp.getResourceService().getResourceAsString("", "testDataPoints.txt");
					parseData(data);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		GlobalState.fluidApp.getSystemService().runOnUiThread(r);
	}
	
	private void parseData(String data) {
		
		dataPoints = new TableList<>();
		
		JsonValue valueData = JsonValue.readFrom(data);
		
		JsonValue daily = valueData.asObject().get("daily");
		
		JsonArray array = daily.asObject().get("data").asArray();
		
		for (JsonValue value : array) {
			
			WeatherDataPoint dataPoint = new WeatherDataPoint();
			try {
				JsonUtil.setValuesTo(dataPoint, value.asObject());
				dataPoints.add(dataPoint);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		GlobalState.fluidApp.getDataModelManager().dataDidChange("app.weatherManager", "dataPoints");
		// or dataDidChange("app.weatherManager.dataPoints");
	}

	@Override
	public String[] getSupportedPlatforms() {
		return null;
	}
	
}
