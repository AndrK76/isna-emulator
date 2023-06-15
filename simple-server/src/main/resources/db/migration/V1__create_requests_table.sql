create table requests(
 id bigserial not null primary key,
 message_id UUID,
 service_id varchar(50),
 message_date TIMESTAMP WITH TIME ZONE,
 data varchar(32000)
);