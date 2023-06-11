create table requests(
 id bigserial not null primary key,
 message_id varchar(50),
 service_id varchar(50),
 message_date TIMESTAMP WITH TIME ZONE,
 data varchar(32000)
);