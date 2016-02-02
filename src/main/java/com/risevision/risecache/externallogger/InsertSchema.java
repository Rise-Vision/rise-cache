package com.risevision.risecache.externallogger;

import com.google.gson.Gson;
import com.risevision.risecache.Config;

import java.util.*;

public class InsertSchema {
	String kind = "bigquery#tableDataInsertAllRequest";
	boolean skipInvalidRows = false;
	boolean ignoreUnknownValues = false;
	String templateSuffix = "";
	List<Row> rows;

	static class Row {
		String insertId;
		RowData json;
	}

	static class RowData {
		String event;
		String event_details;
		String error_details;
		String display_id;
		String cache_version;
		String os;
		String ts;
	}

	private InsertSchema(){}

	public static InsertSchema initialize() {
		InsertSchema schema = new InsertSchema();
		schema.rows = new ArrayList<Row>();
		Row row = new Row();
		row.json = new RowData();
		schema.rows.add(row);
		schema.setCacheVersion(Config.riseCacheVersion);
		schema.setOS(System.getProperty("os.name"));
		schema.setDisplayId(Config.displayId);
		schema.templateSuffix = getTemplateSuffix();
		return schema;
	}

	public static InsertSchema withEvent(String event) {
		return InsertSchema.initialize().setEvent(event);
	}

	public static InsertSchema withEvent(String event, String details) {
		return InsertSchema.initialize().setEvent(event).setEventDetails(details);
	}

	public static InsertSchema withEvent(String event, String details, String errorDetails) {
		return InsertSchema.initialize().setEvent(event).setEventDetails(details).setErrorDetails(errorDetails);
	}

	public InsertSchema setEvent(String event) {
		this.rows.get(0).json.event = event;
		return this;
	}

	public InsertSchema setEventDetails(String details) {
		this.rows.get(0).json.event_details = details;
		return this;
	}

	public InsertSchema setErrorDetails(String errorDetails) {
		this.rows.get(0).json.error_details = errorDetails;
		return this;
	}

	public InsertSchema setDisplayId(String id) {
		this.rows.get(0).json.display_id = id;
		return this;
	}

	public InsertSchema setCacheVersion(String version) {
		this.rows.get(0).json.cache_version = version;
		return this;
	}

	public InsertSchema setOS(String os) {
		this.rows.get(0).json.os = os;
		return this;
	}

	public InsertSchema setTimestamp() {
		java.text.SimpleDateFormat fmt;
		fmt = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		fmt.setCalendar(Calendar.getInstance(TimeZone.getTimeZone("GMT")));
		this.rows.get(0).json.ts = fmt.format(new Date());
		return this;
	}

	public InsertSchema setInsertId() {
		this.rows.get(0).insertId = String.valueOf(Math.random()).substring(2);
		return this;
	}

	public static String getTemplateSuffix() {
		java.text.SimpleDateFormat fmt;
		fmt = new java.text.SimpleDateFormat("yyyyMMdd");
		fmt.setCalendar(Calendar.getInstance(TimeZone.getTimeZone("GMT")));
		return fmt.format(new Date());
	}

	public String getEvent() {
		return this.rows.get(0).json.event;
	}

	public String getEventDetails() {
		return this.rows.get(0).json.event_details;
	}

	public String getDisplayId() {
		return this.rows.get(0).json.display_id;
	}

	public String getCacheVersion() {
		return this.rows.get(0).json.cache_version;
	}


	public String getOS() {
		return this.rows.get(0).json.os;
	}

	public String getJson() {
		return (new Gson()).toJson(this);
	}
}
