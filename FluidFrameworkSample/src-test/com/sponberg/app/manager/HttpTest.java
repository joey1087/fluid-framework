package com.sponberg.app.manager;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sponberg.app.MockApp;
import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.HttpServiceCallback;
import com.sponberg.fluid.layout.DataModelManager;
import com.sponberg.fluid.test.MockRealHttpService;

public class HttpTest {

	@Before
	public void setUp() throws Exception {
		GlobalState.fluidApp = new MockApp();
		GlobalState.fluidApp.setHttpService(new MockRealHttpService());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		String url = "http://httpbin.org/status/202";
		GlobalState.fluidApp.getHttpService().get(url, null, null, new HttpServiceCallback() {
			@Override
			public void success(HttpResponse response) {
				assertEquals(202, response.getCode());
			}
			@Override
			public void fail(HttpResponse response) {
				Assert.fail(response.getData());
			}			
		});
		url = "http://httpbin.org/status/404";
		GlobalState.fluidApp.getHttpService().get(url, null, null, new HttpServiceCallback() {
			@Override
			public void success(HttpResponse response) {
				Assert.fail(response.getCode() + " " + response.getData());
			}
			@Override
			public void fail(HttpResponse response) {
				assertEquals(404, response.getCode());
			}			
		});
	}

}
