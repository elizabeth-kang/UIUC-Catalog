package edu.illinois.cs.cs125.spring2021.mp.network;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.illinois.cs.cs125.spring2021.mp.application.CourseableApplication;
import edu.illinois.cs.cs125.spring2021.mp.models.Rating;
import edu.illinois.cs.cs125.spring2021.mp.models.Summary;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

/**
 * Development course API server.
 *
 * <p>Normally you would run this server on another machine, which the client would connect to over
 * the internet. For the sake of development, we're running the server right alongside the app on
 * the same device. However, all communication between the course API client and course API server
 * is still done using the HTTP protocol. Meaning that eventually it would be straightforward to
 * move this server to another machine where it could provide data for all course API clients.
 *
 * <p>You will need to add functionality to the server for MP1 and MP2.
 */
public final class Server extends Dispatcher {
  @SuppressWarnings({"unused", "RedundantSuppression"})
  private static final String TAG = Server.class.getSimpleName();

  private final Map<String, String> summaries = new HashMap<>();

  private static final int NUMBER_OF_PARTS_FOR_GET_COURSE = 4;
  private static final int NUMBER_OF_PARTS_FOR_RATING = 5;

  private MockResponse getSummary(@NonNull final String path) {
    String[] parts = path.split("/");
    if (parts.length != 2) {
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
    }

    String summary = summaries.get(parts[0] + "_" + parts[1]);
    if (summary == null) {
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
    }
    return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(summary);
  }

  private final Map<Summary, String> courses = new HashMap<>();

  private MockResponse getCourse(@NonNull final String path) {
    String[] parts = path.split("/");
    if (parts.length != NUMBER_OF_PARTS_FOR_GET_COURSE) {
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
    }
    Summary summary = new Summary(parts[0], parts[1], parts[2], parts[3], null);
    String course = courses.get(summary);
    if (course == null) {
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
    }
    return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(course);
  }

  //String is the UUID
  private final Map<String, Map<Summary, Rating>> ratingMap = new HashMap<>();

  private final ObjectMapper objectMapper = new ObjectMapper();

  private MockResponse handleRating(@NonNull final RecordedRequest request) throws JsonProcessingException {
    if (request.getMethod().equalsIgnoreCase("GET")) {
      String path = request.getPath();
      System.out.println(path);
      if (!path.contains("?client=")) {
        return new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
      }
      String uuid = request.getPath().substring(request.getPath().indexOf("=") + 1);
      System.out.println(uuid);
      Pattern pattern = Pattern.compile("([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})");
      Matcher matcher = pattern.matcher(uuid);
      if (!matcher.matches()) {
        return new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
      }
      String route = path.substring(0, path.indexOf("?"));
      System.out.println(route);

      String[] parts = route.split("/");
      for (String i: parts) {
        System.out.println(i);
      }
      System.out.println(parts[2] + parts[3] + parts[parts.length - 2] + parts[parts.length - 1]);

      Summary summary = new Summary(parts[2], parts[3], parts[parts.length - 2], parts[parts.length - 1], null);

      if (!courses.containsKey(summary)) {
        return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
      }

      Map<Summary, Rating> summaryToRatingMap = new HashMap<Summary, Rating>();
      summaryToRatingMap = ratingMap.get(uuid);


      Rating rating = null; //summaryToRatingMap.get(summary);

      if (rating ==  null) {
        Rating newRating = new Rating(uuid, Rating.NOT_RATED);
        String ratingAsString = objectMapper.writeValueAsString(newRating);
        return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(ratingAsString);
      } else {
        String ratingAsString = objectMapper.writeValueAsString(rating);
        return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(ratingAsString);
      }

    } else if (request.getMethod().equalsIgnoreCase("POST")) {
      Rating rating = objectMapper.readValue(request.getBody().readUtf8(), Rating.class);
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody("");
    }
    return new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
  }

  @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")

  @NonNull
  @Override
  public MockResponse dispatch(@NonNull final RecordedRequest request) {
    try {
      String path = request.getPath();
      if (path == null || request.getMethod() == null) {
        return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
      } else if (path.equals("/") && request.getMethod().equalsIgnoreCase("GET")) {
        return new MockResponse().setBody("CS125").setResponseCode(HttpURLConnection.HTTP_OK);
      } else if (path.startsWith("/summary/")) {
        return getSummary(path.replaceFirst("/summary/", ""));
      } else if (path.startsWith("/course/")) {
        return getCourse(path.replaceFirst("/course/", ""));
      } else if (path.startsWith("/rating/")) {
        return handleRating(request);
      }
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
    } catch (Exception e) {
      e.printStackTrace();
      return new MockResponse().setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
    }
  }

  /**
   * Start the server if has not already been started.
   *
   * <p>We start the server in a new thread so that it operates separately from and does not
   * interfere with the rest of the app.
   */
  public static void start() {
    if (!isRunning(false)) {
      new Thread(Server::new).start();
    }
    if (!isRunning(true)) {
      throw new IllegalStateException("Server should be running");
    }
  }

  /** Number of times to check the server before failing. */
  private static final int RETRY_COUNT = 8;

  /** Delay between retries. */
  private static final int RETRY_DELAY = 512;

  /**
   * Determine if the server is currently running.
   *
   * @param wait whether to wait or not
   * @return whether the server is running or not
   * @throws IllegalStateException if something else is running on our port
   */
  public static boolean isRunning(final boolean wait) {
    for (int i = 0; i < RETRY_COUNT; i++) {
      OkHttpClient client = new OkHttpClient();
      Request request = new Request.Builder().url(CourseableApplication.SERVER_URL).get().build();
      try {
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
          if (Objects.requireNonNull(response.body()).string().equals("CS125")) {
            return true;
          } else {
            throw new IllegalStateException(
                "Another server is running on port " + CourseableApplication.DEFAULT_SERVER_PORT);
          }
        }
      } catch (IOException ignored) {
        if (!wait) {
          break;
        }
        try {
          Thread.sleep(RETRY_DELAY);
        } catch (InterruptedException ignored1) {
        }
      }
    }
    return false;
  }

  private final ObjectMapper mapper = new ObjectMapper();

  private Server() {
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    loadSummary("2021", "spring");
    loadCourses("2021", "spring");

    try {
      MockWebServer server = new MockWebServer();
      server.setDispatcher(this);
      server.start(CourseableApplication.DEFAULT_SERVER_PORT);
    } catch (IOException e) {
      e.printStackTrace();
      throw new IllegalStateException(e.getMessage());
    }
  }

  @SuppressWarnings("SameParameterValue")
  private void loadSummary(@NonNull final String year, @NonNull final String semester) {
    String filename = "/" + year + "_" + semester + "_summary.json";
    String json =
        new Scanner(Server.class.getResourceAsStream(filename), "UTF-8").useDelimiter("\\A").next();
    summaries.put(year + "_" + semester, json);
  }

  @SuppressWarnings("SameParameterValue")
  private void loadCourses(@NonNull final String year, @NonNull final String semester) {
    String filename = "/" + year + "_" + semester + ".json";
    String json =
        new Scanner(Server.class.getResourceAsStream(filename), "UTF-8").useDelimiter("\\A").next();
    try {
      JsonNode nodes = mapper.readTree(json);
      for (Iterator<JsonNode> it = nodes.elements(); it.hasNext(); ) {
        JsonNode node = it.next();
        Summary course = mapper.readValue(node.toString(), Summary.class);
        courses.put(course, node.toPrettyString());
      }
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }
}
