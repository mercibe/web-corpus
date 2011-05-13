CREATE database corpus;
USE corpus;
CREATE USER 'corpus'@'localhost' IDENTIFIED BY 'corpus';
GRANT ALL ON corpus.* TO 'corpus'@'localhost';