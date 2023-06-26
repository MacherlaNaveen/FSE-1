package com.cognizant.moviebookingapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cognizant.moviebookingapp.model.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

	Optional<Movie> findByMovieId(Long movieId);

	boolean existsByMovieId(String movieId);

	boolean existsByMovieName(String movieName);

	Optional<Movie> findByMovieName(String movieName);

}
