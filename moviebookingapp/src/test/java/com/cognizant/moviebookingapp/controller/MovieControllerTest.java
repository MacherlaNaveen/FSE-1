package com.cognizant.moviebookingapp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import com.cognizant.moviebookingapp.exception.AuthorizationException;
import com.cognizant.moviebookingapp.service.MovieService;
import com.cognizant.moviebookingapp.model.Movie;

public class MovieControllerTest {
	 @Test
	  public void testAddMovies_Admin_Success() throws AuthorizationException {
	    // Mock dependencies
	    MovieService movieService = mock(MovieService.class);
	    AuthService authService = mock(AuthService.class);
	    
	    // Create test objects
	    Movie movie = new Movie();
	    String token = "admin_token";
	    
	    // Set up the mock behavior
	    when(authService.validateToken(token)).thenReturn(Collections.singletonMap("role", "admin"));
	    when(movieService.addMovie(movie)).thenReturn(ResponseEntity.ok().build());
	    
	    // Create the controller instance
	    MovieController movieController = new MovieController();
	    
	    // Call the function under test
	    ResponseEntity<?> response = movieController.addMovies(movie, token);
	    
	    // Verify the expected behavior
	    assertThat(response.getStatusCodeValue()).isEqualTo(200); // Assuming 200 is the success status code
	    
	    // Verify that the dependencies were called with the expected arguments
	    verify(authService).validateToken(token);
	    verify(movieService).addMovie(movie);
	  }
	  
	  @Test
	  public void testAddMovies_NonAdmin_AuthorizationException() throws AuthorizationException {
	    // Mock dependencies
	    MovieService movieService = mock(MovieService.class);
	    AuthService authService = mock(AuthService.class);
	    
	    // Create test objects
	    Movie movie = new Movie();
	    String token = "non_admin_token";
	    
	    // Set up the mock behavior
	    when(authService.validateToken(token)).thenReturn(Collections.singletonMap("role", "user"));
	    
	    // Create the controller instance
	    MovieController movieController = new MovieController();
	    
	    // Call the function under test and expect an AuthorizationException
	    assertThrows(AuthorizationException.class, () -> movieController.addMovies(movie, token));
	    
	    // Verify that the dependencies were called with the expected arguments
	    verify(authService).validateToken(token);
	    verifyNoInteractions(movieService); // Ensure that movieService.addMovie() was not called
	  }
}
