package com.risevision.risecache.externallogger;

import com.risevision.risecache.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ExternalLogger {
  private static final String logUrl = "https://www.googleapis.com/bigquery/v2/" +
  "projects/client-side-events/datasets/Rise_Cache/tables/events/insertAll";

  public static void logExternal(InsertSchema schema) {
    try {
      Token.update();
    } catch (IOException e) {
      Log.error("External logger token update: " + e.getMessage());
    }

    if (schema.getEvent() == null || schema.getEvent().equals("")) {
      throw new RuntimeException("No event specified");
    }

    
    byte[] json = schema.setInsertId().setTimestamp().getJson().getBytes();

    try {
      HttpURLConnection conn = (HttpURLConnection) getUrl().openConnection();
      conn.setRequestMethod("POST");
      conn.setDoOutput(true);
      conn.setRequestProperty("Content-Type", "application/json"); 
      conn.setRequestProperty("Content-Length", String.valueOf(json.length));
      conn.setRequestProperty("Authorization", "Bearer " + Token.token);
      try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
        out.write(json);
      }
      conn.getInputStream().close();
    } catch (IOException e) {
      Log.error("External logger event save: " + e.getMessage());
    }
  }

  private static URL getUrl() throws IOException {
    return new URL(logUrl);
  }
}
