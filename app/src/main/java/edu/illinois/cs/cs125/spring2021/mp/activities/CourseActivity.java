package edu.illinois.cs.cs125.spring2021.mp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.illinois.cs.cs125.spring2021.mp.R;
import edu.illinois.cs.cs125.spring2021.mp.application.CourseableApplication;
import edu.illinois.cs.cs125.spring2021.mp.databinding.ActivityCourseBinding;
import edu.illinois.cs.cs125.spring2021.mp.models.Course;
import edu.illinois.cs.cs125.spring2021.mp.models.Summary;
import edu.illinois.cs.cs125.spring2021.mp.network.Client;

/** CourseActivity showing the course summary list. */
public class CourseActivity extends AppCompatActivity implements Client.CourseClientCallbacks {
  @SuppressWarnings({"unused", "RedundantSuppression"})
  private static final String TAG = CourseActivity.class.getSimpleName();

   // Binding to the layout in activity_main.xml
  private ActivityCourseBinding binding;

  /**
  * Called when this activity is created.
  *
  * <p>Because this is the main activity for this app, this method is called when the app is
  * started, and any time that this view is shown.
  *
  * @param savedInstanceState saved instance state, currently unused and always empty or null
  */
  @Override
  protected void onCreate(@Nullable final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.i(TAG, "CourseActivity launched");

    binding = DataBindingUtil.setContentView(this, R.layout.activity_course);

    Intent intent = getIntent();
    String course = intent.getStringExtra("COURSE");
    ObjectMapper obj = new ObjectMapper();
    Summary summary = null;
    try {
      summary = obj.readValue(course, Summary.class);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    Log.i(TAG, course);

    //TODO: Use getCourse to retrieve information about the course passed into the intent
    CourseableApplication application = (CourseableApplication) getApplication();
    application.getCourseClient().getCourse(summary, this);



  }

  /**
   * Callback called when the client has retrieved the list of courses for this component to
   * display.
   *
   * @param summary the year that was retrieved
   * @param course the semester that was retrieved
   */
  @Override
  public void courseResponse(final Summary summary, final Course course) {
    //Bind to the layout in activity_course.xml
    binding.title.setText(course.getTitle());
    binding.description.setText(course.getDescription());
  }
}
