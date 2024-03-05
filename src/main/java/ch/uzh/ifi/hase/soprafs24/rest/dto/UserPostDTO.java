package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.time.LocalDate;
import java.util.Date;

public class UserPostDTO {

  private String name;
  private String username;
  private LocalDate birthDate;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public LocalDate getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(LocalDate birthDate) {
    this.birthDate = birthDate;
  }

}
