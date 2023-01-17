package com.reficulx.tms.models;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

@Document(collection = "roles")
public class Role {
  @Id
  private String id;
  private ERole name;

  public Role() {
  }

  public String getId() {
    return id;
  }

  public ERole getName() {
    return name;
  }

  public void setName(ERole name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "ERole: [id=" + id + ", role name=" + name + "]";
  }

}
