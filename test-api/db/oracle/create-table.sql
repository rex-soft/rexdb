-- Create table
create table R_STUDENT
(
  student_id      NUMBER(11) not null,
  name            VARCHAR2(30) not null,
  sex             NUMBER(1) not null,
  birthday        DATE not null,
  birth_time      DATE not null,
  enrollment_time DATE not null,
  major           NUMBER(6) not null,
  photo           BLOB,
  remark          CLOB,
  readonly        NUMBER(1) not null
);

alter table R_STUDENT add constraint PK_R_STUDENT primary key (STUDENT_ID) using index ;

-- Create sequence 
create sequence SQ_STUDENT_ID
minvalue 1
maxvalue 99999999999
start with 11
increment by 1
cache 20;

-- data
insert into R_STUDENT (STUDENT_ID, NAME, SEX, BIRTHDAY, BIRTH_TIME, ENROLLMENT_TIME, MAJOR, PHOTO, REMARK, READONLY)
values (1, 'Alice', 1, to_date('12-06-1988', 'dd-mm-yyyy'), to_date('06:23:15', 'hh24:mi:ss'), to_date('01-01-2000 13:05:01', 'dd-mm-yyyy hh24:mi:ss'), 10000, null, null, 1);
insert into R_STUDENT (STUDENT_ID, NAME, SEX, BIRTHDAY, BIRTH_TIME, ENROLLMENT_TIME, MAJOR, PHOTO, REMARK, READONLY)
values (2, 'Amber', 1, to_date('18-03-1988', 'dd-mm-yyyy'), to_date('01:04:01', 'hh24:mi:ss'), to_date('01-01-2000 16:10:01', 'dd-mm-yyyy hh24:mi:ss'), 10000, null, null, 1);
insert into R_STUDENT (STUDENT_ID, NAME, SEX, BIRTHDAY, BIRTH_TIME, ENROLLMENT_TIME, MAJOR, PHOTO, REMARK, READONLY)
values (3, 'Cherry', 1, to_date('24-08-1989', 'dd-mm-yyyy'), to_date('07:45:01', 'hh24:mi:ss'), to_date('01-01-2000 16:01:01', 'dd-mm-yyyy hh24:mi:ss'), 10000, null, null, 1);
insert into R_STUDENT (STUDENT_ID, NAME, SEX, BIRTHDAY, BIRTH_TIME, ENROLLMENT_TIME, MAJOR, PHOTO, REMARK, READONLY)
values (4, 'Corrine', 1, to_date('23-09-1990', 'dd-mm-yyyy'), to_date('12:53:23', 'hh24:mi:ss'), to_date('01-01-2000 13:12:01', 'dd-mm-yyyy hh24:mi:ss'), 10000, null, null, 1);
insert into R_STUDENT (STUDENT_ID, NAME, SEX, BIRTHDAY, BIRTH_TIME, ENROLLMENT_TIME, MAJOR, PHOTO, REMARK, READONLY)
values (5, 'Editha', 1, to_date('11-10-1990', 'dd-mm-yyyy'), to_date('01:53:30', 'hh24:mi:ss'), to_date('01-01-2000 13:20:01', 'dd-mm-yyyy hh24:mi:ss'), 10000, null, null, 1);
insert into R_STUDENT (STUDENT_ID, NAME, SEX, BIRTHDAY, BIRTH_TIME, ENROLLMENT_TIME, MAJOR, PHOTO, REMARK, READONLY)
values (6, 'Molly', 1, to_date('01-08-1991', 'dd-mm-yyyy'), to_date('02:12:01', 'hh24:mi:ss'), to_date('01-01-2000 17:23:01', 'dd-mm-yyyy hh24:mi:ss'), 10000, null, null, 1);
insert into R_STUDENT (STUDENT_ID, NAME, SEX, BIRTHDAY, BIRTH_TIME, ENROLLMENT_TIME, MAJOR, PHOTO, REMARK, READONLY)
values (7, 'Nina', 1, to_date('07-02-1991', 'dd-mm-yyyy'), to_date('01:32:01', 'hh24:mi:ss'), to_date('01-01-2000 15:01:01', 'dd-mm-yyyy hh24:mi:ss'), 10000, null, null, 1);
insert into R_STUDENT (STUDENT_ID, NAME, SEX, BIRTHDAY, BIRTH_TIME, ENROLLMENT_TIME, MAJOR, PHOTO, REMARK, READONLY)
values (8, 'Sherry', 1, to_date('07-11-1991', 'dd-mm-yyyy'), to_date('17:41:12', 'hh24:mi:ss'), to_date('01-01-2000 15:28:01', 'dd-mm-yyyy hh24:mi:ss'), 10000, null, null, 1);
insert into R_STUDENT (STUDENT_ID, NAME, SEX, BIRTHDAY, BIRTH_TIME, ENROLLMENT_TIME, MAJOR, PHOTO, REMARK, READONLY)
values (9, 'Kent', 1, to_date('15-01-1991', 'dd-mm-yyyy'), to_date('22:50:01', 'hh24:mi:ss'), to_date('01-01-2000 15:31:01', 'dd-mm-yyyy hh24:mi:ss'), 10000, null, null, 1);
insert into R_STUDENT (STUDENT_ID, NAME, SEX, BIRTHDAY, BIRTH_TIME, ENROLLMENT_TIME, MAJOR, PHOTO, REMARK, READONLY)
values (10, 'Kern', 1, to_date('11-05-1992', 'dd-mm-yyyy'), to_date('20:20:10', 'hh24:mi:ss'), to_date('01-01-2000 14:35:01', 'dd-mm-yyyy hh24:mi:ss'), 10000, null, null, 1);
