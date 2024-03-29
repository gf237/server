package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User createUser(User newUser) {
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.ONLINE);
    newUser.setBirthday(null);
    newUser.setCreationDate(LocalDate.now());

    checkIfUserExists(newUser);
    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  /**
   * This is a helper method that will check the uniqueness criteria of the
   * username and the name
   * defined in the User entity. The method will do nothing if the input is unique
   * and throw an error otherwise.
   *
   * @param userToBeCreated
   * @throws org.springframework.web.server.ResponseStatusException
   * @see User
   */
  private void checkIfUserExists(User userToBeCreated) {
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

    String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
    if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "username", "is"));

    }
  }

  public User loginUser(User userToBeCreated) {
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());
    User userByName = userRepository.findByName(userToBeCreated.getName());
    if (userByUsername != null && userByName != null) {
      if (!userByUsername.getId().equals(userByName.getId())) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The provided username and password do not match.");
      }
      userByUsername.setStatus(UserStatus.ONLINE);
      userRepository.save(userByUsername);
      userRepository.flush();
      return userByUsername;
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found or invalid credentials.");
  }

  public User getUser(long userId) {
    User userById = userRepository.findById(userId);
    if (userById != null) {
      return userById;
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found or invalid credentials.");
  }

  public User updateProfile(User inputUser) {
    User userByToken = userRepository.findByToken(inputUser.getToken());

    if (userByToken.getId().equals(inputUser.getId())) {

      String username = inputUser.getUsername();
      User userByUsername = userRepository.findByUsername(username);

      if (username != null) {
        if (userByUsername != null) {
          String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not updated!";
          throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "username", "is"));
        }
        userByToken.setUsername(username);
      }

      LocalDate birthday = inputUser.getBirthday();
      if (birthday != null) {
        userByToken.setBirthday(birthday);
      }

      userRepository.save(userByToken);
      userRepository.flush();

    } else {
      new ResponseStatusException(HttpStatus.CONFLICT, "Id is not matching!");
    }
    return userByToken;
  }

  public void logoutUser(User user) {
    Long userId = user.getId();
    if (userId == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "UserId not found.");
    }
    User userToUpdate = userRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
    userToUpdate.setStatus(UserStatus.OFFLINE);
    userRepository.save(userToUpdate);
    userRepository.flush();
  }
}
