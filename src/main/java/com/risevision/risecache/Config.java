// Copyright - 2010 - May 2014 Rise Vision Incorporated.
// Use of this software is governed by the GPLv3 license
// (reproduced in the LICENSE file).

package com.risevision.risecache;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Properties;

public class Config {

	public static String appPath;
	public static String jarPath;
	public static String cachePath; //folder for downloaded files
	public static String downloadPath; //folder for downloading files
	public static String versionPath;

	public static int basePort = Globals.BASE_PORT;
	public static int maxPort = Globals.MAX_PORT;

	public static boolean useProxy;
	public static String proxyAddr;
	public static int proxyPort;
	public static String riseCacheVersion;
        public static boolean isWindows;

	public static String debugUrl = null;

	private static final String PROPERTY_DISPLAY_ID = "displayid";
	public static String displayId = "";


	private static Properties props;

	public static void init(Class<?> mainClass) {
                isWindows = System.getProperty("os.name").startsWith("Windows");

		appPath = System.getenv(isWindows ? "LOCALAPPDATA" : "HOME") + File.separator + "rvplayer";
                jarPath = mainClass.getProtectionDomain().getCodeSource().getLocation().getPath();
		if (isWindows) {
			try {
				jarPath = URLDecoder.decode(jarPath, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			jarPath = jarPath.substring(1).replace("/", File.separator);
		}
	
		if (jarPath.endsWith(".jar") || jarPath.endsWith(File.separator)) {
			jarPath = jarPath.substring(0, jarPath.lastIndexOf(File.separator));
		}
	
		cachePath = appPath + File.separator + "cache";
		downloadPath = appPath + File.separator + "download";
		versionPath = (new File(jarPath)).getParentFile().getPath();

		//create download folder if missing
		File downloadDir = new File(downloadPath);
		if (!downloadDir.exists()) {
			downloadDir.mkdir();
			//throw new Error(dataPath + " doesn't exist as server root");
		}
	
		//create cache folder if missing
		File cacheDir = new File(cachePath);
		if (!cacheDir.exists()) {
			cacheDir.mkdir();
			//throw new Error(cacheDir + " doesn't exist as server root");
		}

		riseCacheVersion = readVersion("RiseCache.ver");
	
	}
	
	static void loadProps() {

		String fileName = appPath + File.separator + Globals.APPLICATION_NAME + ".properties";
		try {
			File f = new File(fileName);
			if (f.exists()) {
				InputStream is = new BufferedInputStream(new FileInputStream(f));
				props = new Properties();
				props.load(is);
				is.close();
				Log.info("Loading application properties...");
				basePort = getPropertyInt("base.port", Globals.BASE_PORT);
				maxPort = getPropertyInt("max.port", Globals.MAX_PORT);
				if (maxPort <= basePort)
					maxPort = basePort + 20;
				debugUrl = getPropertyStr("debug.url", null);
				//proxy
				useProxy = "true".equalsIgnoreCase(getPropertyStr("proxy.enabled", "false"));
				proxyAddr = getPropertyStr("proxy.address", null);
				proxyPort = getPropertyInt("proxy.port", 0);
			} else {
				Log.info("Application properties file is not found. Using default setting. File name: " + fileName);
			}
			
		} catch (Exception e) {
			Log.warn("Error loading application properties", "File name: " + fileName + ". Error: " + e.getMessage());
		}
	}

	public static void loadDisplayProperties() {

		String fileName = null;
		try {
			fileName = URLDecoder.decode(appPath + File.separator + "RiseDisplayNetworkII.ini", "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try {
			File f = new File(fileName);

			if (f.exists()) {
				props = loadPropertiesFile(f);

				Log.info("Loading display properties...");
				displayId = getPropertyStr(PROPERTY_DISPLAY_ID, "");
			}

		} catch (Exception e) {
			Log.warn("Error loading display properties", "File name: " + fileName + ". Error: " + e.getMessage());
		}
	}

	/**
	 * Loads a properties file accumulating properties with the same name into a single key separated by spaces
	 * @param file The file reference
	 * @return The processed Properties object
	 */
	public static Properties loadPropertiesFile(File file) throws Exception {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		Properties properties = new Properties();
		String line;

		while((line = bufferedReader.readLine()) != null) {
			int idx = line.indexOf("=");

			if(idx >= 0) {
				String key = line.substring(0, idx);
				String value = line.substring(idx + 1);

				if(!properties.containsKey(key)) {
					properties.setProperty(key, value);
				}
				else {
					properties.setProperty(key, properties.getProperty(key) + " " + value);
				}
			}
		}

		bufferedReader.close();

		return properties;
	}

	private static int getPropertyInt(String name, int defaultValue) {
		String s = props.getProperty(name);
		int res = defaultValue;
		if (s != null) {
			try {
				res = Integer.parseInt(s);
			} catch (Exception e) {
				Log.warn("Property is not a number" , name);
			}
		}
		return res;
	}

	private static String getPropertyStr(String name, String defaultValue) {
		String s = props.getProperty(name);
		String res = defaultValue;
		if (s != null) {
			res = s;
		}
		return res;
	}


	private static String readVersion(String fileName) {
		String res = "";
		File file = new File(versionPath, fileName);
		if (file.exists()) {
			try {
				List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
				if (lines != null && lines.size() > 0)
					res = lines.get(0);
			} catch (IOException e) {
			}
		}
		return res;
	}


}
