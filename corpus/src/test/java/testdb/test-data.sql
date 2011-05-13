INSERT INTO Corpus (id,description,nom) VALUES (1,'mels-cj blablbla','mels-cj');
INSERT INTO Liste (id,description,nom,corpus_id) VALUES (1,'Liste du premier cycle du primaire','1er cycle primaire',1);
INSERT INTO Mot (id, catgram,isLemme,lemme,mot,note,liste_id) VALUES (1,'n.f.',1,'pomme','pomme',null,1);
INSERT INTO Mot (id, catgram,isLemme,lemme,mot,note,liste_id) VALUES (2,'n.f.',0,'pomme','pommes',null,1);