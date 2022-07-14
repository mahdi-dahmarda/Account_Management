package com.example.account.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

	private static final Long DEFAULT_ID = 1L;

	private static final String DEFAULT_FIRSTNAME = "Mahdi";
	private static final String UPDATED_FIRSTNAME = "Ali";

	private static final String DEFAULT_LASTNAME = "Dahmarda";
	private static final String UPDATED_LASTNAME = "Ahmadi";

	private static final String DEFAULT_EMAIL = "mahdi.dahmardah@gmail.com";
	private static final String UPDATED_EMAIL = "ali.ahmadi@gmail.com";

	private static final LocalDate DEFAULT_DOB = LocalDate.of(1990, 2, 20);
	private static final LocalDate UPDATED_DOB = LocalDate.of(2000, 4, 12);

	private static final String ENTITY_API_URL = "/api/users";
	private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

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

		restUserMockMvc.perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
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
	void createUserWithExistingId() throws Exception {
		int databaseSizeBeforeCreate = userRepository.findAll().size();

		User user = new User();
		user.setId(DEFAULT_ID);
		user.setFirstName(DEFAULT_FIRSTNAME);
		user.setLastName(DEFAULT_LASTNAME);
		user.setEmail(DEFAULT_EMAIL);
		user.setDob(DEFAULT_DOB);

		// An entity with an existing ID cannot be created, so this API call must fail
		restUserMockMvc.perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
				.content(TestUtil.convertObjectToJsonBytes(user))).andExpect(status().isBadRequest());

		// Validate the User in the database
		assertPersistedUsers(users -> assertThat(users).hasSize(databaseSizeBeforeCreate));
	}

	@Test
	@Transactional
	void createUserWithExistingEmail() throws Exception {
		// Initialize the database
		userRepository.saveAndFlush(user);
		int databaseSizeBeforeCreate = userRepository.findAll().size();

		User user = new User();

		user.setFirstName(DEFAULT_FIRSTNAME);
		user.setLastName(DEFAULT_LASTNAME);
		user.setEmail(DEFAULT_EMAIL); // this email should already be used
		user.setDob(DEFAULT_DOB);

		// Create the User
		restUserMockMvc.perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
				.content(TestUtil.convertObjectToJsonBytes(user))).andExpect(status().isBadRequest());

		// Validate the User in the database
		assertPersistedUsers(users -> assertThat(users).hasSize(databaseSizeBeforeCreate));
	}

	@Test
	@Transactional
	void getAllUsers() throws Exception {
		// Initialize the database
		userRepository.saveAndFlush(user);

		// Get all the users
		restUserMockMvc.perform(get(ENTITY_API_URL).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

				.andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRSTNAME)))
				.andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LASTNAME)))
				.andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
				.andExpect(jsonPath("$.[*].dob").value(hasItem(DEFAULT_DOB.toString())));

	}

	@Test
	@Transactional
	void getUser() throws Exception {
		// Initialize the database
		userRepository.saveAndFlush(user);

		// Get the user
		restUserMockMvc.perform(get(ENTITY_API_URL_ID, user.getId())).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))

				.andExpect(jsonPath("$.firstName").value(DEFAULT_FIRSTNAME))
				.andExpect(jsonPath("$.lastName").value(DEFAULT_LASTNAME))
				.andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
				.andExpect(jsonPath("$.dob").value(DEFAULT_DOB.toString()));

	}

	@Test
	@Transactional
	void getNonExistingUser() throws Exception {
		restUserMockMvc.perform(get(ENTITY_API_URL + "/0")).andExpect(status().isNotFound());
	}

	@Test
	@Transactional
	void updateUser() throws Exception {
		// Initialize the database
		userRepository.saveAndFlush(user);

		int databaseSizeBeforeUpdate = userRepository.findAll().size();

		// Update the user
		User updatedUser = userRepository.findById(user.getId()).get();

		User user = new User();
		user.setId(updatedUser.getId());
		user.setFirstName(UPDATED_FIRSTNAME);
		user.setLastName(UPDATED_LASTNAME);
		user.setEmail(UPDATED_EMAIL);
		user.setDob(UPDATED_DOB);

		restUserMockMvc.perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
				.content(TestUtil.convertObjectToJsonBytes(user))).andExpect(status().isOk());

		// Validate the User in the database
		assertPersistedUsers(users -> {
			assertThat(users).hasSize(databaseSizeBeforeUpdate);
			User testUser = users.stream().filter(usr -> usr.getId().equals(updatedUser.getId())).findFirst().get();
			assertThat(testUser.getFirstName()).isEqualTo(UPDATED_FIRSTNAME);
			assertThat(testUser.getLastName()).isEqualTo(UPDATED_LASTNAME);
			assertThat(testUser.getEmail()).isEqualTo(UPDATED_EMAIL);
			assertThat(testUser.getDob()).isEqualTo(UPDATED_DOB);
		});
	}

	@Test
	@Transactional
	void updateUserExistingEmail() throws Exception {
		// Initialize the database with 2 users
		userRepository.saveAndFlush(user);

		User anotherUser = new User();
		anotherUser.setEmail("ahmad.mahmud@test.com");
		anotherUser.setFirstName("Ahmad");
		anotherUser.setLastName("Mahmud");
		anotherUser.setDob(LocalDate.of(1950, 4, 16));
		userRepository.saveAndFlush(anotherUser);

		// Update the user
		User updatedUser = userRepository.findById(user.getId()).get();

		User user = new User();
		user.setId(updatedUser.getId());
		user.setFirstName(updatedUser.getFirstName());
		user.setLastName(updatedUser.getLastName());
		user.setEmail("ahmad.mahmud@test.com"); // this email should already be used by anotherUser
		user.setDob(DEFAULT_DOB);

		restUserMockMvc.perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON)
				.content(TestUtil.convertObjectToJsonBytes(user))).andExpect(status().isBadRequest());
	}

	@Test
	@Transactional
	void deleteUser() throws Exception {
		// Initialize the database
		userRepository.saveAndFlush(user);

		int databaseSizeBeforeDelete = userRepository.findAll().size();

		// Delete the user
		restUserMockMvc.perform(delete(ENTITY_API_URL_ID, user.getId()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());

		// Validate the database is empty
		assertPersistedUsers(users -> assertThat(users).hasSize(databaseSizeBeforeDelete - 1));
	}

	private void assertPersistedUsers(Consumer<List<User>> userAssertion) {
		userAssertion.accept(userRepository.findAll());
	}
}
