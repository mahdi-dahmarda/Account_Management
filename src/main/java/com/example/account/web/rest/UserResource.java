package com.example.account.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.account.domain.User;
import com.example.account.repository.UserRepository;
import com.example.account.web.rest.errors.BadRequestAlertException;
import com.example.account.web.rest.errors.EmailAlreadyUsedException;
import com.example.account.web.util.ResponseUtil;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class UserResource {

	@Autowired
	private UserRepository userRepository;

	@PostMapping("/users")
	public ResponseEntity<User> createUser(@Valid @RequestBody User user) throws URISyntaxException {

		log.debug("REST request to save User : {}", user);

		if (user.getId() != null) {
			throw new BadRequestAlertException("A new user cannot already have an ID");
			// Lower case the user login before comparing with database
		} else if (userRepository.findOneByEmailIgnoreCase(user.getEmail()).isPresent()) {
			throw new EmailAlreadyUsedException();
		} else {
			User newUser = userRepository.save(user);
			return ResponseEntity.created(new URI("/api/users/" + newUser.getId())).body(newUser);
		}

	}

	@GetMapping("/users")
	public List<User> getAllUsers() {
		log.debug("REST request to get all User");

		return userRepository.findAll();
	}

	@GetMapping("/users/{id}")
	public ResponseEntity<User> getUser(@PathVariable Long id) {
		log.debug("REST request to get User : {}", id);
		return ResponseUtil.wrapOrNotFound(userRepository.findById(id));
	}

	@PutMapping("/users")
	public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
		log.debug("REST request to update User : {}", user);

		Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(user.getEmail());

		if (existingUser.isPresent() && (!existingUser.get().getId().equals(user.getId()))) {
			throw new EmailAlreadyUsedException();
		}

		User result = userRepository.save(user);

		return ResponseEntity.ok().body(result);
	}

	@DeleteMapping("/users/{id}")
	public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
		log.debug("REST request to delete Person : {}", id);

		userRepository.deleteById(id);

		return ResponseEntity.noContent().build();
	}
}
