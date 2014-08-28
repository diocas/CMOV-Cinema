
CREATE TABLE Movie (
  id_Movie INTEGER  NOT NULL ,
  Name varchar(255) NOT NULL DEFAULT '',
  Duration INTEGER DEFAULT NULL,
  Sinopsis VARCHAR(255) ,
  Cover varchar(255) DEFAULT '',
  Trailer varchar(255) DEFAULT NULL,
  Date_From date NOT NULL,
  Date_Until date NOT NULL,
  Update_date date NOT NULL,
  PRIMARY KEY (id_Movie)
);


CREATE TABLE Session (
  id_Session INTEGER  NOT NULL ,
  id_Movie INTEGER  NOT NULL,
  Time time NOT NULL,
  Room varchar(15) NOT NULL DEFAULT '',
  PRIMARY KEY (id_Session),
  FOREIGN KEY (id_Movie) REFERENCES Movie (id_Movie)
);


CREATE TABLE Useracount (
  id_User INTEGER  NOT NULL ,
  email varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (id_User)
);


CREATE TABLE Reservation (
  id_Reservation INTEGER  NOT NULL ,
  id_Session INTEGER  NOT NULL,
  id_User INTEGER  NOT NULL,
  Places varchar(255) NOT NULL DEFAULT '',
  Date date NOT NULL,
  Update_date date NOT NULL,
  PRIMARY KEY (id_Reservation),
  FOREIGN KEY (id_User) REFERENCES Useracount (id_User),
  FOREIGN KEY (id_Session) REFERENCES Session (id_Session)
);


INSERT INTO Movie (id_Movie, Name, Duration, Sinopsis, Cover, Trailer, Date_From, Date_Until, Update_date)
VALUES
	(1,'Filme1',NULL,NULL,'',NULL,'2014-08-25','2014-09-20', '2014-08-26'),
	(2,'Filme2',NULL,NULL,'',NULL,'2014-08-25','2014-09-20', '2014-08-26');

INSERT INTO Session (id_Session, id_Movie, Time, Room)
VALUES
	(8,1,'18:00:00','1'),
	(9,1,'22:00:00','1'),
	(10,2,'21:00:00','2'),
	(11,2,'23:30:00','2');

INSERT INTO Useracount (id_User, email)
VALUES
	(1,'user@user.pt'),
	(2,'user@user.com');
