package com.example.account.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.account.domain.User;
import com.example.account.repository.UserRepository;

@AutoConfigureMockMvc
@SpringBootTest
class UserResourceIT {
	private static final String DEFAULT_EMAIL = "mahdi.dahmardah@gmail.com";

	private static final String DEFAULT_FIRSTNAME = "Mahdi";

	private static final String DEFAULT_LASTNAME = "Dahmarda";

	private static final LocalDate DEFAULT_DOB = LocalDate.ofEpochDay(0L);

	private User user;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MockMvc restUserMockMvc;

	@Autowired
	private EntityManager em;

	public static User createEntity(EntityManager em) {
		User user = new User();

		user.setFirstName(DEFAULT_FIRSTNAME);
		user.setLastName(DEFAULT_LASTNAME);
		user.setEmail(DEFAULT_EMAIL);
		user.setDob(DEFAULT_DOB);

		return user;
	}

	public static User initTestUser(UserRepository userRepository, EntityManager em) {

		User user = createEntity(em);

		return user;
	}

	@BeforeEach
	public void initTest() {
		user = initTestUser(userRepository, em);
	}

	@Test
	@Transactional
	void createUser() throws Exception {
		int databaseSizeBeforeCreate = userRepository.findAll().size();

		// Create the User
		User user = new User();
		user.setFirstName(DEFAULT_FIRSTNAME);
		user.setLastName(DEFAULT_LASTNAME);
		user.setEmail(DEFAULT_EMAIL);
		user.setDob(DEFAULT_DOB);

		restUserMockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
				.content(TestUtil.convertObjectToJsonBytes(user))).andExpect(status().isCreated());

		// Validate the User in the database
		assertPersistedUsers(users -> {
			assertThat(users).hasSize(databaseSizeBeforeCreate + 1);
			User testUser = users.get(users.size() - 1);
			assertThat(testUser.getFirstName()).isEqualTo(DEFAULT_FIRSTNAME);
			assertThat(testUser.getLastName()).isEqualTo(DEFAULT_LASTNAME);
			assertThat(testUser.getEmail()).isEqualTo(DEFAULT_EMAIL);
		});
	}

	@Test
	@Transactional
	void getAllUsers() throws Exception {
		// Initialize the database
		userRepository.saveAndFlush(user);

		// Get all the users
		restUserMockMvc.perform(get("/api/users").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

				.andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRSTNAME)))
				.andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LASTNAME)))
				.andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
				.andExpect(jsonPath("$.[*].dob").value(hasItem(DEFAULT_DOB.toString())));

	}

	private void assertPersistedUsers(Consumer<List<User>> userAssertion) {
		userAssertion.accept(userRepository.findAll());
	}
}
