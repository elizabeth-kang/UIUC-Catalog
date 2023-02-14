package edu.illinois.cs.cs125.spring2021.mp.models;

/**
 * Model for rating information shown in the course list.
 */
public class Rating {
  /**
   * When the Course isn't rated.
   *
   */
  public static final double NOT_RATED = -1.0;
  private String id;
  private double rating;

  /**
   * Create a Rating with the provided field.
   *
   * @param setId the ID for this rating
   * @param setRating the raating for Course
   */
  public Rating(final String setId, final double setRating) {
    id = setId;
    rating = setRating;
  }

  /**
   * Empty rating constructor.
   *
   */
  public Rating() { }

  /**
   * Get the ID for this rating.
   *
   * @return the rating for this Raring
   */
  public String getId() {
    return id;
  }

  /**
   * Get the rating for this rating.
   *
   * @return the rating for this rating
   */
  public double getRating() {
    return rating;
  }
}
