// This class is AutoGenerated code by the MakeDatastoreConstants tool.
// DO NOT EDIT.

package com.sponberg.fluid.datastore;


public class DSBook extends DSBase implements SQLTable {

	public static final String _table = "book";

	public static final String id = "id";
	public static final String libraryId = "library_id";
	public static final String name = "name";
	public static final String numPages = "num_pages";
	public static final String price = "price";

	public void setId(Integer id) {
		this._data.put(DSBook.id, id);
	}

	public Integer getId() {
		 return (Integer) _data.get(DSBook.id);
	}

	public void setLibraryId(Integer libraryId) {
		this._data.put(DSBook.libraryId, libraryId);
	}

	public Integer getLibraryId() {
		 return (Integer) _data.get(DSBook.libraryId);
	}

	public void setName(String name) {
		this._data.put(DSBook.name, name);
	}

	public String getName() {
		 return (String) _data.get(DSBook.name);
	}

	public void setNumPages(Integer numPages) {
		this._data.put(DSBook.numPages, numPages);
	}

	public Integer getNumPages() {
		 return (Integer) _data.get(DSBook.numPages);
	}

	public void setPrice(Double price) {
		this._data.put(DSBook.price, price);
	}

	public Double getPrice() {
		 return (Double) _data.get(DSBook.price);
	}

	public String _getTableName() {
		return DSBook._table;
	}

}