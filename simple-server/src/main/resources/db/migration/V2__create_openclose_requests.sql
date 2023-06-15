create table open_close_requests
(
    id          bigserial   not null primary key,
    message_id  UUID        not null,
    reference   varchar(50) not null,
    code_form   varchar(5)  not null,
    notify_date TIMESTAMP   not null,
    data        varchar(32000)
);

create table open_close_request_accounts
(
    id         bigserial not null primary key,
    request_id long      not null,
    sort       int       not null,
    account    varchar(50),
    oper_type  int,
    bic        varchar(50),
    oper_date  TIMESTAMP,
    rnn        varchar(50),
    dog        varchar(50)
);

alter table open_close_requests add
    constraint open_close_requests_uk unique (message_id)
;

alter table open_close_request_accounts add
    constraint open_close_request_accounts_fk
        foreign key (request_id) references  open_close_requests(id)
;

create index open_close_request_accounts_request_id_idx
    on open_close_request_accounts(request_id)
;
