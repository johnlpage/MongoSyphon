drop database phones;
create database phones;
use phones;

create table people ( id INT NOT NULL,
                      name VARCHAR(16),
                       PRIMARY KEY (id) );

create table phones ( digits INT NOT NULL,
                      make VARCHAR(16),
                      model VARCHAR(16),
                      owner INT NOT NULL,
                      FOREIGN KEY (owner) REFERENCES people(id) ON DELETE CASCADE,
                      PRIMARY KEY (digits));


insert into people (id,name) values (1,"Andy");
insert into people (id,name) values (2,"Bob");
insert into people (id,name) values (3,"Charles");
insert into people (id,name) values (4,"Damien");

insert into phones (digits,make,model,owner) values (2025551234,"iphone","6",1);
insert into phones (digits,make,model,owner) values (2025551239,"iphone","5",1);
insert into phones (digits,make,model,owner) values (2025552239,"samsung","galaxy",3);

create index ownidx on phones(owner);