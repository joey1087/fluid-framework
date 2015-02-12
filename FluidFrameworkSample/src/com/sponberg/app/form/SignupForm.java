package com.sponberg.app.form;

import lombok.Data;

import com.sponberg.app.datastore.app.DSUser;
import com.sponberg.app.domain.Postcode;
import com.sponberg.fluid.layout.TableList;

@Data
public class SignupForm {

	public static final TableList<WhenOption> whenOptions;
	
	static {
		whenOptions = new TableList<>();
		whenOptions.add(new WhenOption("Within a week", 0));
		whenOptions.add(new WhenOption("Within a month", 1));
		whenOptions.add(new WhenOption("Within a quarter", 2));
		whenOptions.add(new WhenOption("Within the next 6 months", 3));
		whenOptions.add(new WhenOption("Within a year", 4));
	}
	
	protected Postcode where;
	
	protected TableList<Postcode> whereSearchResults = new TableList<>();
	
	protected DSUser user = new DSUser();

	protected WhenOption when;
	
	protected String description;
	
	public TableList<WhenOption> getWhenOptions() {
		return whenOptions;
	}
	
}
