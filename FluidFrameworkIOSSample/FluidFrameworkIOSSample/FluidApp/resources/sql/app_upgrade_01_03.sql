alter table 		library
	add column 		address					text;
	
update library set address = "538 Crown St, Surry Hills, NSW 2010" where id = 1;
