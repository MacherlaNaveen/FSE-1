package com.cognizant.moviebookingapp.service.Impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cognizant.moviebookingapp.model.Movie;
import com.cognizant.moviebookingapp.model.Ticket;
import com.cognizant.moviebookingapp.repository.MovieRepository;
import com.cognizant.moviebookingapp.repository.TicketRepository;
import com.cognizant.moviebookingapp.service.TicketService;


@Service
public class TicketServiceImpl implements TicketService {

	@Autowired
	TicketRepository ticketRepo;

	@Autowired
	MovieRepository movieRepo;

	@Override
	public ResponseEntity<?> bookMovie(String movieName, Ticket ticket) {
		Optional<Movie> movie = movieRepo.findByMovieName(movieName);
		if (movie.isEmpty()) {
			return new ResponseEntity<>("Movie not found", HttpStatus.NOT_FOUND);
		}

		int availableTickets = movie.get().getTotalTickets();// from movie document
		int totalTicketsBuy = ticket.getNumberOfTickets();// from ticket object
		if (availableTickets <= 0) {
			return new ResponseEntity<>("All tickets sold out", HttpStatus.BAD_REQUEST);
		}
		if (availableTickets < totalTicketsBuy) {
			return new ResponseEntity<>("Insufficient tickets available", HttpStatus.BAD_REQUEST);
		}

		// Update final ticket count and movie data
		int finalTicketCount = availableTickets - totalTicketsBuy;
		Ticket newTicket = new Ticket();
		newTicket.setNumberOfTickets(ticket.getNumberOfTickets());

		newTicket.setTransactionId(generateTransactionId());
		newTicket.setMovieName(movieName);
		newTicket.setTheaterName(movie.get().getTheaterName());
		newTicket.setUserId(ticket.getUserId());// --fix-me planned

		movie.get().getTickets().add(newTicket);
		movie.get().setTotalTickets(finalTicketCount);// updating final count of ticket in movie db
		movieRepo.save(movie.get());
		ticketRepo.save(newTicket);

		return new ResponseEntity<>("Successfully booked movie " + movieName, HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<?> getAllTickets() {
		try {
			List<Ticket> tickets = ticketRepo.findAll();
			return new ResponseEntity<>(tickets, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>("Error occurred while fetching tickets: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<?> getTicketsUser(String userId) {
		
		List<Ticket> userTickets = ticketRepo.findByUserId(userId);
		return new ResponseEntity<>(userTickets, HttpStatus.OK);
	}

	
	public static String generateTransactionId() {
		final int RANDOM_BOUND = 100;
		
		Random random = new Random();
		int randomNumber = random.nextInt(RANDOM_BOUND);
	
		LocalDateTime now = LocalDateTime.now();
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
		String timestamp = now.format(formatter);
		String transactionId = timestamp + randomNumber;
		return transactionId;
	}

}
