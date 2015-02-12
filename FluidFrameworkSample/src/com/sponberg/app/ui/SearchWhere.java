package com.sponberg.app.ui;

import java.util.Comparator;

import com.sponberg.app.SampleApp;
import com.sponberg.app.datastore.DS;
import com.sponberg.app.datastore.postcodes.DSPostcode;
import com.sponberg.app.domain.Postcode;
import com.sponberg.app.ui.Screen.ScreenSearchWhere;
import com.sponberg.fluid.ApplicationLoader;
import com.sponberg.fluid.FluidApp;
import com.sponberg.fluid.GlobalState;
import com.sponberg.fluid.Platforms;
import com.sponberg.fluid.datastore.DatastoreTransaction;
import com.sponberg.fluid.datastore.DatastoreTransaction.QueryBuilder;
import com.sponberg.fluid.layout.ActionListenerAdapter;
import com.sponberg.fluid.layout.ScreenListener;
import com.sponberg.fluid.layout.TableList;
import com.sponberg.fluid.util.Logger;

public class SearchWhere implements ApplicationLoader {

	SampleApp app;
	
	@Override
	public void load(final FluidApp fApp) {

		this.app = (SampleApp) fApp;
		
		app.addActionListener(Screen.SearchWhere, ScreenSearchWhere.Search)
			.listener(new ActionListenerAdapter() {
				@Override
				public void userChangedValueTo(EventInfo info, Object value) {
					userSearchedFor(value);
				}
				@Override
				public void userCancelled() {
					userCanceledSearch();
				}
			});
			
		app.addActionListener(Screen.SearchWhere, ScreenSearchWhere.Results)
			.listener(new ActionListenerAdapter() {
				@Override
				public void userTapped(EventInfo info) {
					userTappedSearchResult(info.getUserInfo());
				}
			});
		
		app.addScreenListener(Screen.SearchWhere, new ScreenListener() {
			@Override
			public void screenWillAppear() {
			}			
			@Override
			public void screenDidDisappear() {
				app.getQuoteForm().setWhereSearchResults(new TableList<Postcode>());
			}
			@Override
			public void screenDidAppear() {
			}			
			@Override
			public void screenWasRemoved() {
			}			
		});

	}
	
	protected void userTappedSearchResult(Object userInfo) {
		Long id = (Long) userInfo;
		Postcode suburb = app.getQuoteForm().getWhereSearchResults().getById(id);
		app.getQuoteForm().setWhere(suburb);
		app.getQuoteForm().setWhereSearchResults(new TableList<Postcode>());
		app.getDataModelManager().dataDidChange("app.quoteForm.where");
		GlobalState.fluidApp.getUiService().popLayout();	
	}

	protected void userSearchedFor(Object value) {
		String search = value.toString();
		if (search.length() < 2) {
			app.getQuoteForm().setWhereSearchResults(new TableList<Postcode>());
			app.getDataModelManager().dataDidChange("app.quoteForm.whereSearchResults");
			return;
		}
		
		String[] searchTerms = search.split(" ");
		
		boolean firstIsDigit = Character.isDigit(searchTerms[0].charAt(0));
		String codeSearch = null;
		String titleSearch = null;
		int startTitleSearchAt = 0;
		if (firstIsDigit) {
			codeSearch = searchTerms[0]; 
			startTitleSearchAt  = codeSearch.length() + 1;
		}
		if (startTitleSearchAt < search.length()) {
			titleSearch = search.substring(startTitleSearchAt);
			if (titleSearch.startsWith("- ")) {
				titleSearch = titleSearch.substring(2);
			} else if (titleSearch.startsWith("-")) {
				titleSearch = titleSearch.substring(1);
			}
		}
		
		DatastoreTransaction txn = new DatastoreTransaction(DS.postcodes);
		txn.start();
		
		TableList<Postcode> list = new TableList<>();
		
		QueryBuilder<Postcode> query = txn.query(Postcode.class)
			.select(DSPostcode.id, DSPostcode.code, DSPostcode.title)
			.limit(2000)
			.allowRefresh(false);
		
		// If first is digit, then use code = 2094 and 
		
		// hstdbc num should be and, text should be or'ed
		StringBuilder where = new StringBuilder();
		if (codeSearch != null) {
			where.append("{} like ?");
			query = query
					.param(DSPostcode.code, codeSearch + "%");						
		}
		if (titleSearch != null && !titleSearch.equals("")) {
			if (codeSearch != null) {
				where.append(" and ");
			}
			where.append("{} like ?");
			query = query
					.param(DSPostcode.title, titleSearch + "%");
		}

		query = query.where(where.toString());
		
		for (Postcode suburb : query.execute()) {
			list.add(suburb);
		}
		
		final boolean digit = Character.isDigit(search.charAt(0));
		
		list.sort(new Comparator<Postcode>() {
			@Override
			public int compare(Postcode o1, Postcode o2) {
				if (digit) {
					return o1.getCode().compareTo(o2.getCode());
				} else {
					return o1.getTitle().compareTo(o2.getTitle());
				}
			}					
		});
		
		Logger.debug(this, "result size is " + list.size());
		
		app.getQuoteForm().setWhereSearchResults(list);
		app.getDataModelManager().dataDidChange("app.quoteForm.whereSearchResults");
		
		txn.rollback();
	}

	protected void userCanceledSearch() {
		app.getQuoteForm().setWhereSearchResults(new TableList<Postcode>());
		GlobalState.fluidApp.getUiService().popLayout();
	}
	
	@Override
	public String[] getSupportedPlatforms() {
		return new String[] { Platforms.IOS };
	}

}
