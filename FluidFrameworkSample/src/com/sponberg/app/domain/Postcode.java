package com.sponberg.app.domain;

import com.sponberg.app.datastore.postcodes.DSPostcode;
import com.sponberg.fluid.layout.TableRowWithId;

public class Postcode extends DSPostcode implements TableRowWithId {

	@Override
	public Long getFluidTableRowObjectId() {
		return new Long(getId());
	}

}
