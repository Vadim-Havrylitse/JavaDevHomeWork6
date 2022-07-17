INSERT INTO companies (name,country,city)
	VALUES
	('ABN inc', 'Makedonia', 'Tringo'),
	('FIFA corp', 'Italy', 'Rome'),
	('Spring4beavers', 'Holland', 'Amsterdam');

INSERT INTO customers (name, surname)
	VALUES
	('John', 'Makedon'),
	('Bob', 'Fridgh'),
	('Ivan', 'Petrenko');

INSERT INTO skills (industry, degree)
	VALUES
	('Java', 'Middle'),
    ('JS', 'Middle'),
    ('C++', 'Senior'),
    ('C++', 'Middle'),
    ('Java', 'Junior'),
    ('JS', 'Junior');
    
INSERT INTO projects
	VALUES
    (1, 'Loombook', 123000, '2022-12-22', 1, 2),
    (2, 'IDEA Workspace', 2340000500, null, 2, 2),
    (3, 'Git workbench', 450100500, null, 1, 3);
    
INSERT INTO developers
	VAlUES
    (1, 'Nenci', 'Ibragimovich', 31, 'female', 2, 12000),
    (2, 'Victor', 'Ronaldo', 29, 'male', null, 22000),
    (3, 'Imanuel', 'Mikron', 22, 'other', 1, 12000);
    
INSERT INTO developers_skills
	VALUES
    (2,3),
    (1,3),
    (1,6),
    (1,2),
    (2,2),
    (3,1);
    
    
INSERT INTO developers_projects
	VALUES 
    (1, 2),
    (2, 2),
    (3, 1),
    (3, 2),
    (1, 1),
	(1, 3);
