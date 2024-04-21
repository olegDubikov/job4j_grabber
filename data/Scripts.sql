CREATE TABLE company
(
    id integer NOT NULL,
    name character varying,
    CONSTRAINT company_pkey PRIMARY KEY (id)
);


CREATE TABLE person
(
    id integer NOT NULL,
    name character varying,
    company_id integer references company(id),
    CONSTRAINT person_pkey PRIMARY KEY (id)
);


select p.name, c.name  
from person p join company c  on p.company_id = c.id 
where p.company_id != 5;

select c.name, count(p.id) AS person_count  
from company c  
join person p  on c.id  = p.company_id 
group by c.name
having count(p.id) = (
select max(person_count) 
from (
select company_id, count(id) as person_count
from person 
group by company_id) as subquery
);