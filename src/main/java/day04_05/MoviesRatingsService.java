package day04_05;

import javax.sql.DataSource;
import java.util.List;

public class MoviesRatingsService {

    private RatingsRepository ratingsRepository;
    private MoviesRepository moviesRepository;

    public MoviesRatingsService(DataSource dataSource) {
        ratingsRepository = new RatingsRepository(dataSource);
        moviesRepository = new MoviesRepository(dataSource);
    }


    public void addRatings(Movie movie, List<Integer> ratings) {
        ratingsRepository.insertRating(validated(movie).getId(), ratings);
    }

    public List<Integer> getRatings(Movie movie) {
        return ratingsRepository.loadRatings(validated(movie))
                .orElseThrow(() -> new IllegalArgumentException("cant find rating for movie " + movie));
    }

    public double getAverageRating(Movie movie) {
        return ratingsRepository.loadAverage(validated(movie).getId());
    }


    private Movie validated(Movie movie) {
        return moviesRepository.findMovie(movie)
                .orElseThrow(() -> new IllegalArgumentException("cant find movie"));
    }

}
