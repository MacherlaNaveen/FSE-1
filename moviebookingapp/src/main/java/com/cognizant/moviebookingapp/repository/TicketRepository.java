package com.cognizant.moviebookingapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cognizant.moviebookingapp.model.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, String>{
	
	List<Ticket> findByUserId(String userId);

}
