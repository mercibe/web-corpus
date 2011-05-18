SET storage_engine=INNODB;
CREATE database corpus;
ALTER DATABASE corpus DEFAULT CHARACTER SET utf8 COLLATE utf8_bin;

USE corpus;
CREATE USER 'corpus'@'localhost' IDENTIFIED BY 'corpus';
GRANT ALL ON corpus.* TO 'corpus'@'localhost';

CREATE INDEX mot_lemme  ON Mot ( lemme );
CREATE INDEX mot_mot  ON Mot ( mot );
CREATE INDEX mot_catgram  ON Mot ( catgram );
CREATE INDEX mot_genre  ON Mot ( genre );
CREATE INDEX mot_nombre  ON Mot ( nombre );