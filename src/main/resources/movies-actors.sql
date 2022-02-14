DROP SCHEMA IF EXISTS movies_actors;
CREATE SCHEMA movies_actors;
USE movies_actors;

drop user if exists 'movies_actors'@'%';
CREATE USER 'movies_actors'@'%' IDENTIFIED BY 'movies_actors';
GRANT ALL PRIVILEGES ON movies_actors.* TO 'movies_actors'@'%';

CREATE TABLE actors
(
    id         BIGINT AUTO_INCREMENT,
    actor_name VARCHAR(255),
    CONSTRAINT pk_actors PRIMARY KEY (id)
);

