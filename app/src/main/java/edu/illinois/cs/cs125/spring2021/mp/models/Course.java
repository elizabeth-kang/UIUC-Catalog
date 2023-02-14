package edu.illinois.cs.cs125.spring2021.mp.models;

/**
 * Model for course information shown in the course list.
 */
public class Course extends Summary {
  private String description;

  /**
  * Get the description for this Course.
  *
  * @return the description for this Course
  */
  public String getDescription() {
    return description;
  }

  /** Create an empty Course. */
  public Course() {}

  /**
  * Create a Course with the provided field.
  *
  * @param setDescription the description for this Course
  */
  public Course(final String setDescription) {
    description = setDescription;
  }
}
