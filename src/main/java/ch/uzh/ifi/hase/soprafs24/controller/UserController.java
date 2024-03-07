package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

  private final UserService userService;

  UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getAllUsers() {
    // fetch all users in the internal representation
    List<User> users = userService.getUsers();
    List<UserGetDTO> userGetDTOs = new ArrayList<>();

    // convert each user to the API representation
    for (User user : users) {
      userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
    }
    return userGetDTOs;
  }

  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
    // create user
    User createdUser = userService.createUser(userInput);
    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
  }

  @PostMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO loginUser(@RequestBody UserPostDTO userPostDTO) {
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
    User createdUser = userService.loginUser(userInput);
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
  }

  @GetMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO getUser(@PathVariable Long userId) {
    User user = userService.getUser(userId);
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
  }

  // @PostMapping("/status")
  // @ResponseStatus(HttpStatus.OK)
  // @ResponseBody
  // public void setStatus(@PathVariable Long userId, @RequestBody UserPostDTO
  // userPostDTO) {
  // User userToUpdate = new User();
  // userToUpdate.setId(userId);
  // userService.setStatus(userToUpdate);
  // }

  @PutMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void editProfile(@PathVariable Long userId, @RequestBody UserPutDTO userPutDTO) {
    User userInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
    User userToUpdate = userService.getUser(userId);
    userService.updateProfile(userToUpdate, userInput);

  }

  @PutMapping("/logout")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void logout(@RequestBody UserPutDTO userPutDTO) {
    User userInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
    userService.logoutUser(userInput);
  }
}
