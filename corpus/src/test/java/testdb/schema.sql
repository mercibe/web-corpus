CREATE TABLE contexte
(
   Id int PRIMARY KEY,
   mot_id int NOT NULL
)
;
CREATE TABLE corpus
(
   id int PRIMARY KEY,
   description varchar(255),
   nom varchar(255)
)
;
CREATE TABLE liste
(
   id int PRIMARY KEY,
   description varchar(255),
   nom varchar(255),
   corpus_id int NOT NULL
)
;
CREATE TABLE mot
(
   id int PRIMARY KEY,
   catgram varchar(255),
   isLemme bit,
   lemme varchar(255),
   mot varchar(255) NOT NULL,
   note varchar(255),
   liste_id int NOT NULL
)
;
ALTER TABLE contexte
ADD CONSTRAINT FKE209E4B6BD7BEFCB
FOREIGN KEY (mot_id)
REFERENCES mot(id)
;

ALTER TABLE liste
ADD CONSTRAINT FK460736751D13FC9
FOREIGN KEY (corpus_id)
REFERENCES corpus(id)
;
ALTER TABLE mot
ADD CONSTRAINT FK12EF2A8590F6B
FOREIGN KEY (liste_id)
REFERENCES liste(id)
;
CREATE UNIQUE INDEX liste_id ON mot
(
  liste_id,
  mot
)
;

