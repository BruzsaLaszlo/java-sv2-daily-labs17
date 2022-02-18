CREATE TABLE movies_actors
(
    id BIGINT AUTO_INCREMENT,
    movie_id BIGINT,
    actor_id BIGINT,
    CONSTRAINT pk_movies_actors PRIMARY KEY (id)
)