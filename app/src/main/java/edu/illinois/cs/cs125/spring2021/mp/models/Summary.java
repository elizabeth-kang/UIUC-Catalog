package edu.illinois.cs.cs125.spring2021.mp.models;

import  androidx.annotation.NonNull;
import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Model holding the course summary information shown in the course list.
 *
 * <p>You will need to complete this model for MP0.
 */
public class Summary implements SortedListAdapter.ViewModel {
  private String year;

  /**
   * Get the year for this Summary.
   *
   * @return the year for this Summary
   */
  public final String getYear() {
    return year;
  }

  private String semester;

  /**
   * Get the semester for this Summary.
   *
   * @return the semester for this Summary
   */
  public final String getSemester() {
    return semester;
  }

  private String department;

  /**
   * Get the department for this Summary.
   *
   * @return the department for this Summary
   */
  public final String getDepartment() {
    return department;
  }

  private String number;

  /**
   * Get the number for this Summary.
   *
   * @return the number for this Summary
   */
  public final String getNumber() {
    return number;
  }

  private String title;

  /**
   * Get the title for this Summary.
   *
   * @return the title for this Summary
   */
  public final String getTitle() {
    return title;
  }

  /** Create an empty Summary. */
  @SuppressWarnings({"unused", "RedundantSuppression"})
  public Summary() {}

  /**
   * Create a Summary with the provided fields.
   *
   * @param setYear the year for this Summary
   * @param setSemester the semester for this Summary
   * @param setDepartment the department for this Summary
   * @param setNumber the number for this Summary
   * @param setTitle the title for this Summary
   */
  public Summary(
      final String setYear,
      final String setSemester,
      final String setDepartment,
      final String setNumber,
      final String setTitle) {
    year = setYear;
    semester = setSemester;
    department = setDepartment;
    number = setNumber;
    title = setTitle;
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object o) {
    if (!(o instanceof Summary)) {
      return false;
    }
    Summary course = (Summary) o;
    return Objects.equals(year, course.year)
        && Objects.equals(semester, course.semester)
        && Objects.equals(department, course.department)
        && Objects.equals(number, course.number);
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return Objects.hash(year, semester, department, number);
  }

  /** {@inheritDoc} */
  @Override
  public <T> boolean isSameModelAs(@NonNull final T model) {
    return equals(model);
  }

  /** {@inheritDoc} */
  @Override
  public <T> boolean isContentTheSameAs(@NonNull final T model) {
    return equals(model);
  }

  /** Compare two courses. */
  public static final Comparator<Summary> COMPARATOR = (courseModel1, courseModel2) -> {
    if (courseModel1.getDepartment().compareTo(courseModel2.getDepartment()) != 0) {
      return courseModel1.getDepartment().compareTo(courseModel2.getDepartment());
    }
    if (courseModel1.getNumber().compareTo(courseModel2.getNumber()) != 0) {
      return courseModel1.getNumber().compareTo(courseModel2.getNumber());
    }
    // in the last case, just return the overall comparison (-1,0, or 1).
    return courseModel1.getTitle().compareTo(courseModel2.getTitle());
  };

  /**
   * Filter a list of courses.
   *
   * @param text the text to search for
   * @param courses the courses to filter
   * @return the filtered list of courses
   */
  public static List<Summary> filter(
          @NonNull final List<Summary> courses, @NonNull final String text) {
    List<Summary> found = new ArrayList<Summary>();
    String courseName;
    for (int i =  0; i < courses.size(); i++) {
      // example: cs 125: intro to computer science
      courseName = courses.get(i).getDepartment().toLowerCase()
              + " " + courses.get(i).getNumber().toLowerCase()
              + ": " + courses.get(i).getTitle().toLowerCase();
      if (courseName.contains(text.toLowerCase())) {
        found.add(courses.get(i));
      }
    }
    return found;
  }
}
