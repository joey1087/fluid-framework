package com.sponberg.fluid.layout;

import lombok.Data;

public interface ActionListener {

	public void userTapped(EventInfo eventInfo);
	
	public void userChangedValueTo(EventInfo eventInfo, Object value);

	public void userCancelled();

	@Data
	public static class EventInfo {
		
		String dataModelKey;
		
		String dataModelKeyParent;
		
		Object userInfo;
		
	}
	
}
