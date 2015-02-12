create table library (
	id 						integer 	primary key autoincrement,
	name					text		not null
);

create table book (
	id 						integer 	primary key autoincrement,
	library_id				integer		not null,
	name					text		not null,
	num_pages				integer		not null
);

insert into library (id, name) values (1, 'Default Library');

insert into book (id, library_id, name, num_pages) values (1, 1, 'A good story', 101);
insert into book (id, library_id, name, num_pages) values (2, 1, 'A bad story', 3);
insert into book (id, library_id, name, num_pages) values (3, 1, "Hans's book", 327);
