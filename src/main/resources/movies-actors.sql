DROP SCHEMA IF EXISTS movies_actors;
CREATE SCHEMA movies_actors;
USE movies_actors;

DROP USER IF EXISTS 'movies_actors'@'%';
CREATE USER 'movies_actors'@'%' IDENTIFIED BY 'movies_actors';
GRANT ALL PRIVILEGES ON movies_actors.* TO 'movies_actors'@'%';
