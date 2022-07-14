package com.example.account.web.rest;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.account.domain.User;
import com.example.account.repository.UserRepository;

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
}
