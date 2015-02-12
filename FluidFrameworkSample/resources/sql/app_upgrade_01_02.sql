alter table 		book
	add column 		price					real		default 9.99;
	
update book set price = 12.99 where id = 1;
update book set price = 0.99 where id = 2;
update book set price = 99.99 where id = 3;
