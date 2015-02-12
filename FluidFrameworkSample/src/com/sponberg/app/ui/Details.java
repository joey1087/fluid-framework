package com.sponberg.app.ui;

import java.util.HashMap;

import com.sponberg.app.datastore.Datastore;
import com.sponberg.app.datastore.app.DSPhoto;
import com.sponberg.app.ui.Screen.ScreenDetails;
import com.sponberg.fluid.ApplicationLoader;
import com.sponberg.fluid.FluidApp;
import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.datastore.DatastoreException;
import com.sponberg.fluid.layout.ActionListenerAdapter;
import com.sponberg.fluid.layout.MenuButtonItem;
import com.sponberg.fluid.layout.ModalView;
import com.sponberg.fluid.layout.ModalView.ModalActionListener;
import com.sponberg.fluid.util.Logger;

public class Details implements ApplicationLoader {

	@Override
	public void load(FluidApp app) {
		
		initializeMenu(app);
		
		app.addActionListener(Screen.Details, ScreenDetails.B).listener(new ActionListenerAdapter() {
			@Override
			public void userTapped(EventInfo info) {				
				GlobalState.fluidApp.getUiService().popLayout();
			}
		});
		
		app.addActionListener(Screen.Details, ScreenDetails.E).listener(new ActionListenerAdapter() {
			@Override
			public void userTapped(EventInfo info) {
				GlobalState.fluidApp.getUiService().pushLayout(Screen.Measure);
			}
		});
		
		app.addActionListener(Screen.Details, ScreenDetails.G).listener(new ActionListenerAdapter() {
			@Override
			public void userTapped(EventInfo info) {
				GlobalState.fluidApp.getUiService().pushLayout(Screen.Animate);
			}
		});
		
		app.addActionListener(Screen.Details, ScreenDetails.N).listener(new ActionListenerAdapter() {
			@Override
			public void userTapped(EventInfo info) {
				
				ModalView modal = new ModalView(ModalView.FluidLayoutFullScreen);
				modal.setUserData("SeparateActivity");
				
				GlobalState.fluidApp.getUiService().showModalView(modal);
			}
		});
		
		app.addActionListener(Screen.Details, ScreenDetails.H).listener(new ActionListenerAdapter() {
			@Override
			public void userTapped(EventInfo info) {
				GlobalState.fluidApp.getUiService().pushLayout(Screen.TableForm);
			}
		});
		
		app.addActionListener(Screen.Details, ScreenDetails.I).listener(new ActionListenerAdapter() {
			@Override
			public void userTapped(EventInfo info) {
				GlobalState.fluidApp.getUiService().pushLayout(Screen.DynamicTable);
			}
		});
	}

	private void initializeMenu(final FluidApp app) {
		
		MenuButtonItem item = new MenuButtonItem(MenuButtonItem.SystemItemCamera);
		item.addActionListener(new ActionListenerAdapter() {
			@Override
			public void userTapped(EventInfo info) {
				ModalView modalView = new ModalView(ModalView.ImagePicker);
				HashMap<String, String> settings = new HashMap<>();
				settings.put("format", "jpg");
				settings.put("quality", "90");
				settings.put("maxWidth", "1024");
				modalView.setUserData(settings);
				modalView.addActionListener(new ModalActionListener() {
					@Override
					public void modalComplete(Object userDataObject) {
						byte[] userData2 = (byte[]) userDataObject;
						Logger.debug(this, "received byte array of length {}", userData2.length);

						DSPhoto photo = new DSPhoto();
						photo.setData(userData2);

						try {
							Datastore.insertPhoto(photo);
						} catch (DatastoreException e) {
							Logger.error(this, e);
						}
					}
					@Override
					public void modalCanceled() {
					}
				});

				app.getUiService().showModalView(modalView);
			}
		});
		app.getScreen(Screen.Details).getNavigationMenuItems().add(item);
		item = new MenuButtonItem(MenuButtonItem.SystemItemAction);
		app.getScreen(Screen.Details).getNavigationMenuItems().add(item);
	}
	
	@Override
	public String[] getSupportedPlatforms() {
		return null;
	}

}
