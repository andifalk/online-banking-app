alter table if exists bank_transaction
    drop constraint if exists FK_bank_transaction;
alter table if exists bank_account
    drop constraint if exists FK_bank_account;
alter table if exists user_roles
    drop constraint if exists FK_user_roles;
drop table if exists bank_transaction cascade;
drop table if exists bank_account cascade;
drop table if exists banking_user cascade;
drop table if exists user_roles cascade;
drop sequence if exists bank_account_seq;
drop sequence if exists bank_transaction_seq;
drop sequence if exists banking_user_seq;
create sequence bank_account_seq start with 1 increment by 50;
create sequence bank_transaction_seq start with 1 increment by 50;
create sequence banking_user_seq start with 1 increment by 50;
create table bank_transaction
(
    amount             numeric(38, 2) not null,
    account_id         bigint         not null,
    id                 bigint         not null,
    timestamp          timestamp(6)   not null,
    transaction_type   varchar(255) check (transaction_type in ('DEPOSIT', 'WITHDRAW', 'TRANSFER')),
    transaction_status varchar(255) check (transaction_status in ('APPROVED', 'PENDING', 'REJECTED')),
    primary key (id)
);
create table bank_account
(
    balance        numeric(38, 2) not null,
    id             bigint         not null,
    user_id        bigint         not null,
    account_number varchar(50),
    account_type   varchar(255)   not null check (account_type in ('SAVINGS', 'CHECKING')),
    primary key (id)
);
create table banking_user
(
    id         bigint       not null,
    first_name varchar(50),
    last_name  varchar(50),
    username   varchar(50),
    email      varchar(255),
    identity   varchar(255) not null,
    primary key (id)
);
create table user_roles
(
    user_id bigint not null,
    roles   varchar(255)
);

alter table if exists bank_transaction
    add constraint FK_bank_transaction foreign key (account_id) references bank_account;
alter table if exists bank_account
    add constraint FK_bank_account foreign key (user_id) references banking_user;
alter table if exists user_roles
    add constraint FK_user_roles foreign key (user_id) references banking_user;