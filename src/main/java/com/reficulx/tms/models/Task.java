package com.reficulx.tms.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "tasks")
public class Task {

  @Id
  private String id;

  private String username; // TODO: this should be userId
  private String title;
  private String description;
  private Date creationTime;
  private Date startTime;
  private Date deadline;
  private Date completionTime;
  private ETaskStatus status;

  public Task() {
  }

  public Task(String username, String title, String description, Date creationTime, Date startTime, Date deadline, ETaskStatus status) {
    this.username = username;
    this.title = title;
    this.description = description;
    this.creationTime = creationTime;
    this.startTime = startTime;
    this.deadline = deadline;
    this.completionTime = null;
    this.status = status;
  }

  public String getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Date getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(Date creationTime) {
    this.creationTime = creationTime;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public Date getDeadline() {
    return deadline;
  }

  public void setDeadline(Date deadline) {
    this.deadline = deadline;
  }

  public Date getCompletionTime() {
    return completionTime;
  }

  public void setCompletionTime(Date completionTime) {
    this.completionTime = completionTime;
  }

  public ETaskStatus getStatus() {
    return status;
  }

  public void setStatus(ETaskStatus status) {
    this.status = status;
  }

  @Override
  public String toString() {
    String base = "Task [id=" + id + ", username=" + username + ", title=" + title + ", description=" + description + ", "
            + "creationTime=" + creationTime.toString() + ", startTime=" + startTime.toString() + ", deadline=" + deadline.toString()
            + ", status=" + status;
    if (completionTime != null) {
      return base + ", completionTime" + completionTime.toString();
    }
    return base;
  }
}
