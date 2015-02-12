create table library2 (
	id 						integer 	primary key autoincrement,
	name					text		not null,
	address1				text		not null,
	address2				text		default null
);