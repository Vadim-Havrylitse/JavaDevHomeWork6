CREATE TABLE developers (
	id bigint AUTO_INCREMENT,
    name varchar(20) NOT NULL,
    surname varchar(20) NOT NULL,
    age bigint CHECK (age>0),
    sex varchar(10),
    companies_id bigint,
    salary int,
    PRIMARY KEY(id)
    );

CREATE TABLE skills (
	id bigint AUTO_INCREMENT,
    industry varchar(20),
    degree varchar(20) DEFAULT 'Junior',
    PRIMARY KEY(id)
    );

CREATE TABLE projects (
	id bigint AUTO_INCREMENT,
    name varchar(50) UNIQUE,
    budget bigint NOT NULL,
    release_date date,
    companies_id bigint,
    customers_id bigint,
    PRIMARY KEY(id)
    );

CREATE TABLE companies (
	id bigint AUTO_INCREMENT,
    name varchar(50) NOT NULL,
    country varchar(20),
    city varchar(20),
    PRIMARY KEY(id)
    );

CREATE TABLE customers (
	id bigint AUTO_INCREMENT,
    name varchar(20),
    surname varchar(20),
    PRIMARY KEY(id)
    );

CREATE TABLE developers_projects (
	developers_id bigint,
    projects_id bigint,
    FOREIGN KEY (projects_id) REFERENCES projects(id) ON DELETE CASCADE,
    FOREIGN KEY (developers_id) REFERENCES developers(id) ON DELETE CASCADE
    );

CREATE TABLE developers_skills (
	developers_id bigint,
    skills_id bigint,
    FOREIGN KEY (skills_id) REFERENCES skills(id) ON DELETE CASCADE,
    FOREIGN KEY (developers_id) REFERENCES developers(id) ON DELETE CASCADE
    );

ALTER TABLE projects
    ADD FOREIGN KEY (customers_id) REFERENCES customers(id) ON DELETE SET NULL;

ALTER TABLE projects
    ADD FOREIGN KEY (companies_id) REFERENCES companies(id) ON DELETE SET NULL;

ALTER TABLE developers
    ADD FOREIGN KEY (companies_id) REFERENCES companies(id) ON DELETE SET NULL;

