create table post (
id serial primary key,
name varchar(50),
text text unique,
link text,
created timestamp
);