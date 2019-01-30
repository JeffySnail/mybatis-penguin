create table Blog_User(
user_id int  auto_increment not null comment 'user id',
user_name varchar(50) null comment 'user name',
user_sex tinyint(2) not null comment 'female 1 male 2',
user_address varchar(200) null comment 'address',
user_nation varchar(100) null comment 'nation',
add_time  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'add time',
update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
primary key (user_id),
KEY `IX_AddTime` (`add_time`),
KEY `IX_UpdateTime` (`update_time`)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT 'blog user';

create table Blog_Detail (
blog_id int  auto_increment not null comment 'blog id',
user_id int  not null comment 'user id',
blog_title varchar(50) null comment 'title',
blog_content varchar(200) not null comment 'content',
blog_tag varchar(200) null comment 'tag',
add_time  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'add time',
update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'update time',
primary key (blog_id),
KEY `IX_AddTime` (`add_time`),
KEY `IX_UpdateTime` (`update_time`)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT 'blog detail';
