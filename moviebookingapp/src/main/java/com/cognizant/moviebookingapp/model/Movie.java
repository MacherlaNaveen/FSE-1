package com.cognizant.moviebookingapp.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name="movies")
public class Movie {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Schema(hidden = true)
	private Long movieId;

	@NotBlank
	private String movieName;

	@NotBlank
	private String theaterName;

	@NotNull
	@Min(value = 1, message = "numberOfTickets must be at least 1")
	@Max(value = 100, message = "numberOfTickets must be at most 100")
	private int totalTickets;

	@NotBlank
	@Schema(hidden = true)
	private String ticketStatus;



	@OneToMany(cascade= CascadeType.ALL,fetch=FetchType.LAZY)
	@JoinColumn(name="movie_id")
	@Schema(hidden = true)
	private List<Ticket> tickets = new ArrayList<Ticket>();

	public Movie() {
	}

	public Movie(Long movieId, @NotBlank String movieName, @NotBlank String theaterName, @NotNull int totalTickets,
			@NotBlank String ticketStatus, List<Ticket> tickets) {
		this.movieId = movieId;
		this.movieName = movieName;
		this.theaterName = theaterName;
		this.totalTickets = totalTickets;
		this.ticketStatus = ticketStatus;
		this.tickets = tickets;
	}

	public Long getMovieId() {
		return movieId;
	}

	public void setMovieId(Long movieId) {
		this.movieId = movieId;
	}

	public String getMovieName() {
		return movieName;
	}

	public void setMovieName(String movieName) {
		this.movieName = movieName;
	}

	public String getTheaterName() {
		return theaterName;
	}

	public void setTheaterName(String theaterName) {
		this.theaterName = theaterName;
	}

	public int getTotalTickets() {
		return totalTickets;
	}

	public void setTotalTickets(int totalTickets) {
		this.totalTickets = totalTickets;
	}

	public String getTicketStatus() {
		return ticketStatus;
	}

	public void setTicketStatus(String ticketStatus) {
		this.ticketStatus = ticketStatus;
	}

	

	public List<Ticket> getTickets() {
		return tickets;
	}

	public void setTickets(List<Ticket> tickets) {
		this.tickets = tickets;
	}

}
