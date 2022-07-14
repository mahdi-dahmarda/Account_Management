package com.example.account.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.account.domain.User;
import com.example.account.repository.UserRepository;
import com.example.account.web.util.ResponseUtil;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class UserResource {

	@Autowired
	private UserRepository userRepository;

	@PostMapping("/users")
	public ResponseEntity<User> createUser(@RequestBody User user) throws URISyntaxException {

		log.debug("REST request to save User : {}", user);

		User newUser = userRepository.save(user);

		return ResponseEntity.created(new URI("/api/users/" + newUser.getId())).body(newUser);

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
	public ResponseEntity<User> updateUser(@RequestBody User user) {
		log.debug("REST request to update User : {}", user);

		User result = userRepository.save(user);

		return ResponseEntity.ok().body(result);
	}
}
