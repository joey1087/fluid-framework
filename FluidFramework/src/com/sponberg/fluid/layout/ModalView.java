package com.sponberg.fluid.layout;

import java.util.ArrayList;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@EqualsAndHashCode
public class ModalView {

	public static final String FluidLayout = "FluidLayout";
	public static final String FluidLayoutFullScreen = "FluidLayoutFullScreen";
	public static final String ImagePicker = "ImagePicker";
	public static final String Confirmation = "NativeConfirmation";
	public static final String WaitingDialog = "NativeWaitingDialog";
	public static final String Custom = "Custom";
	
	ArrayList<ModalActionListener> actionListeners = new ArrayList<>();

	final String systemId;

	Object userData; // In the case of FluidLayout, this should be the layout
	
	Object fluidData;
	
	Object userSelection;
	
	boolean userCancelable = true;
	
	String tag;
	
	public ModalView(String systemId) {
		this.systemId = systemId;
	}

	public void addActionListener(ModalActionListener al) {
		this.actionListeners.add(al);
	}

	public void modalCanceled() {
		for (ModalActionListener l : actionListeners) {
			l.modalCanceled();
		}
	}
	
	public void modalComplete(Object userDataObject) {
		for (ModalActionListener l : actionListeners) {
			l.modalComplete(userDataObject);
		}
	}
	
	public static interface ModalActionListener {
		
		public void modalComplete(Object userData);
		
		public void modalCanceled();

	}

	public static class ModalActionListenerAdapter implements ModalActionListener {

		@Override
		public void modalComplete(Object userData) {
		}

		@Override
		public void modalCanceled() {
		}
	}
	
	@ToString
	@Getter
	@Setter
	public static class ModalViewConfirmation {
		
		String title;

		String message;
		
		String ok;
		
		String cancel;
		
	}
	
	@ToString
	@Getter
	@Setter
	public static class ModalViewWaitingDialog {
		
		String title;

		String message;
		
	}
	
}
