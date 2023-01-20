package com.reficulx.tms.payload.response;

import java.util.List;

public class JwtResponse implements Response {
  private String accessToken;
  private String tokenType = "Bearer";
  private String id;
  private String username;
  private String email;
  private List<String> roles;

  private String message;

  public JwtResponse(String accessToken, String id, String username, String email, List<String> roles, String message) {
    this.accessToken = accessToken;
    this.id = id;
    this.username = username;
    this.email = email;
    this.roles = roles;
    this.message = message;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getTokenType() {
    return tokenType;
  }

  public void setTokenType(String tokenType) {
    this.tokenType = tokenType;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public List<String> getRoles() {
    return roles;
  }

  @Override
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}

