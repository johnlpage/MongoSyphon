drop database if exists sdemo;
create database sdemo;
use sdemo;

create table owner ( ownerid int primary key, name VARCHAR(20), address VARCHAR(60));
CREATE TABLE species ( speciesid int primary key ,  species VARCHAR(20));
CREATE TABLE pet ( petid int primary key, name VARCHAR(20), owner int, species int);

insert into species values(1,"Dog");
insert into species values(2,"Cat");

insert into owner values (1,"John","22 Accacia Avenue");
insert into owner values (2,"Sarah","19 Main Street");

insert into pet values (1,"Brea",1,1);
insert into pet values (2,"Harvest",1,1);
insert into pet values (3,"Mittens",2,2);




