create table blog_user(
user_id int  auto_increment not null comment 'user id',
user_name varchar(50) null comment 'user name',
user_sex tinyint(2) not null comment 'female 1 male 2',
user_address varchar(200) null comment 'address',
user_nation varchar(100) null comment 'nation',
add_time  timestamp not null default current_timestamp comment 'add time',
update_time timestamp not null default current_timestamp on update current_timestamp comment 'update time',
primary key (user_id),
key `ix_addtime` (`add_time`),
key `ix_updatetime` (`update_time`)
)engine=innodb auto_increment=1 default charset=utf8 comment 'blog user';
