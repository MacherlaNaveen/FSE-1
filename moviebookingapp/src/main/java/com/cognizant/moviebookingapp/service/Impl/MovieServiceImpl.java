package com.cognizant.moviebookingapp.service.Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cognizant.moviebookingapp.exception.InvalidInputException;
import com.cognizant.moviebookingapp.model.Movie;
import com.cognizant.moviebookingapp.repository.MovieRepository;
import com.cognizant.moviebookingapp.service.MovieService;

@Service
public class MovieServiceImpl implements MovieService {

	@Autowired
	MovieRepository movieRepo;
	
	Map<String,String> resObj = new HashMap<>();

	@Override
	public ResponseEntity<?> addMovie(Movie movie) {
		movie.setTickets(new ArrayList<>()); 
		if (!movieRepo.existsByMovieName(movie.getMovieName())) {
			if (movie.getTotalTickets() > 0) {
				movie.setTicketStatus("BOOK ASAP");
				return new ResponseEntity<>(movieRepo.save(movie), HttpStatus.CREATED);
			}
			movie.setTicketStatus("SOLD OUT");
			return new ResponseEntity<>(movieRepo.save(movie), HttpStatus.CREATED);
		}
		return new ResponseEntity<>("movie already exists", HttpStatus.CONFLICT);
	}

	@Override
	public ResponseEntity<List<Movie>> getAllMovies() {

		return new ResponseEntity<>(movieRepo.findAll(), HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<?> searchMovieById(Long movieId) {
		Optional<Movie> movie = movieRepo.findByMovieId(movieId);
		if (movie.isPresent()) {
			return new ResponseEntity<>(movie.get(), HttpStatus.CREATED);
		}
		return new ResponseEntity<>("movie not found", HttpStatus.NOT_FOUND);
	}

	@Override
	public ResponseEntity<?> deleteMovie(Long movieId) {
		Optional<Movie> movie = movieRepo.findByMovieId(movieId);
		if (movie.isPresent()) {
			movieRepo.deleteById(movie.get().getMovieId());
			return new ResponseEntity<>("movie deleted successfully!", HttpStatus.OK);
		}
		return new ResponseEntity<>("movie not found", HttpStatus.NOT_FOUND);
	}

	@Override
	public ResponseEntity<?> updateMovie(String movieName, int sumTickets) {


			Optional<Movie> movie = movieRepo.findByMovieName(movieName);
			if (movie.isPresent()) {

				movie.get().setTotalTickets(sumTickets);// update total count of tickets
				if(sumTickets==0) {
				movie.get().setTicketStatus("SOLD OUT");
				}else {
				movie.get().setTicketStatus("BOOK ASAP");
				}
				movieRepo.save(movie.get());
				resObj.put("msg", "updated");
				
				return new ResponseEntity<>(resObj, HttpStatus.OK);
			}

			return  new ResponseEntity<>(new InvalidInputException("not found movie"),HttpStatus.BAD_REQUEST);
	}

	@Override
	public ResponseEntity<?> getBookedTicketList(String movieName) {
		Optional<Movie> movie = movieRepo.findByMovieName(movieName);
		if (movie.isEmpty()) {
			return new ResponseEntity<>("movie not found", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(movie.get().getTickets(), HttpStatus.NOT_FOUND);
	}

}
