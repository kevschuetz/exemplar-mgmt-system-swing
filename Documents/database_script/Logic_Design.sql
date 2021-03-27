DROP TABLE CONTRIBUTED_TO_EXEMPLAR;
DROP TABLE COMMUNITY_EXEMPLARS;
DROP TABLE IS_MEMBER_OF;
DROP TABLE RATING;
DROP TABLE EXEMPLAR;
DROP TABLE USERS;
DROP TABLE IS_LABELED_AS; 
DROP TABLE LABEL;
DROP TABLE COMMUNITY;



CREATE TABLE USERS(
name varchar(50) PRIMARY KEY,
contributor bit NOT NULL,
/* CHECK (contributor IN ("yes", "no")) */
);


CREATE TABLE EXEMPLAR(
name varchar(50) PRIMARY KEY,
problem varchar(5000) NOT NULL,
solution varchar(5000) NOT NULL,
owner varchar(50),

CONSTRAINT exemplar_owner FOREIGN KEY(owner) references USERS(name) ON DELETE SET NULL
);

CREATE TABLE CONTRIBUTED_TO_EXEMPLAR(
user_name varchar(50) NOT NULL,
exemplar_name varchar(50) NOT NULL,

PRIMARY KEY(user_name, exemplar_name),
CONSTRAINT contributing_user FOREIGN KEY(user_name) references USERS(name) ON DELETE CASCADE,
CONSTRAINT referenced_exemplar FOREIGN KEY(exemplar_name) references EXEMPLAR(name) ON DELETE CASCADE,
);

CREATE TABLE LABEL(
name varchar(10) PRIMARY KEY
);

CREATE TABLE IS_LABELED_AS(
exemplar_name varchar(50),
label_name varchar(10),
PRIMARY KEY(exemplar_name, label_name)

); 

CREATE TABLE RATING(
user_name varchar(50),
rating integer,
exemplar_name varchar(50),
PRIMARY KEY(user_name, exemplar_name),
CONSTRAINT rating_fk1 FOREIGN KEY(user_name) REFERENCES USERS(name) ON DELETE CASCADE,
CONSTRAINT rating_fk2 FOREIGN KEY(exemplar_name) REFERENCES EXEMPLAR(name) ON DELETE CASCADE
);


CREATE TABLE COMMUNITY(
name varchar(20) PRIMARY KEY,
);

CREATE TABLE IS_MEMBER_OF(
user_n varchar(50),
community varchar(20),
PRIMARY KEY(user_n, community),

CONSTRAINT community_member_fk1 FOREIGN KEY (user_n) REFERENCES USERS(name) ON DELETE CASCADE,
CONSTRAINT community_member_fk2 FOREIGN KEY (community) REFERENCES COMMUNITY(name) ON DELETE CASCADE
);

CREATE TABLE COMMUNITY_EXEMPLARS(
community varchar(20),
exemplar varchar(50),
PRIMARY KEY (community, exemplar),

CONSTRAINT comm_exempl_fk1 FOREIGN KEY(community) REFERENCES COMMUNITY(name) ON DELETE CASCADE,
CONSTRAINT comm_exempl_fk2 FOREIGN KEY(exemplar) REFERENCES EXEMPLAR(name) ON DELETE CASCADE


);


