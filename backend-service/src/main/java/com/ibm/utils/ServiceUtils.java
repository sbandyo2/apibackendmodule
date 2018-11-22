package com.ibm.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import com.ibm.consants.BackendConstants;
import com.ibm.exception.ServiceException;

public final class ServiceUtils {
	private static final String CONFIG_FILE = "conn_config.properties";
	
	private static final String[] specialCharacters = {"/",":",".","|"};
	private static final String escapeSlash= "\\";
	
	
	public static String getFormattedCurrentTimestampToString(){
		String timestamp =  null;
		String formattedTs = null;
		timestamp = getCurrentTimestampToString();
		System.out.println(timestamp);
		formattedTs = timestamp.replace(" ", "T");
		formattedTs = formattedTs.replace("-", "T");
		formattedTs = formattedTs.replace(".", "T");
		formattedTs = formattedTs.replaceFirst(":", "-");
		System.out.println(formattedTs);
		
		return formattedTs;
	}
	
	
	public static String replaceBlanksWithUnderScore(String value){
		return value.replaceAll(" ", "_");
	}
	
	/**
	 * @param inputStream
	 * @return
	 * @throws ServiceException
	 */
	public static StringBuffer getStringBuffer(InputStream inputStream) throws ServiceException{
		StringWriter writer = null;
		StringBuffer buffer = null;
		try {
			writer = new StringWriter();
			buffer = new StringBuffer();
			IOUtils.copy(inputStream, writer);
		} catch (IOException e) {
			throw new ServiceException(e.getMessage());
		}
		buffer.append(writer.toString());
		
		return buffer;
	}
	/**
	 * @param emailAddreess
	 * @return
	 */
	public static String getIDFromEmail(String emailAddreess){
		String id = "";
		if(emailAddreess.contains(BackendConstants.EMAIL_AT)){
			id=emailAddreess.split(BackendConstants.EMAIL_AT)[BackendConstants.INDEX_ZERO];
		}
		
		return id;
	}
	
	
	public static String escapeSpecialCharachtersForHelpString(String val) {

		String escapeChar = "\"";
		String escapedSearchparams = null;
		
		if (!isNullOrEmpty(val) && val.contains(escapeChar)) {
			escapedSearchparams = val.replace(escapeChar, "\\'");
			val = escapedSearchparams;
		}
		
		// escape 's with s for example Owner's to Owners
		escapeChar = "'s";
		if (!isNullOrEmpty(val) && val.contains(escapeChar)) {
			escapedSearchparams = val.replace(escapeChar, "s");
			val = escapedSearchparams;
		}

		if (ServiceUtils.isNullOrEmpty(escapedSearchparams))
			return val;
		else
			return escapedSearchparams;
	}
	
	/**
	 * @param searchParam
	 * @return
	 */
	public static String escapeSpecialCharachters(String val) {

		String escapeChar = null;
		String escapedSearchparams = null;

		for (int charCount = 0; charCount < specialCharacters.length; charCount++) {
			escapeChar = specialCharacters[charCount];
			if (val.contains(escapeChar)) {
				escapedSearchparams = val.replace(escapeChar, escapeSlash+ escapeChar);
				val = escapedSearchparams;
			}

		}

		
		if (ServiceUtils.isNullOrEmpty(escapedSearchparams))
			return val;
		else
			return escapedSearchparams;
	}
	
	
	/**
	 * @param string
	 * @return
	 */
	public static String parseDateforCloudantTimeStamp(String string) {

		String value;
		try {
			int index = string.lastIndexOf("T");

			value = string.substring(0, index) + "."
					+ string.substring((index + 1));

			value = value.replace("-", ":");

			value = value.replace("T", "-");
			
			value= value.substring(0, 10) + " "
					+ value.substring((12));
		} catch (Exception ex) {
			return null;
		}
		return value;

	}
	
	/**
	 * @return
	 */
	public static String getCurrentTimestampToString(){
		 return new Timestamp(System.currentTimeMillis()).toString();
	}
	
	

	/**
	 * Convert an exception stacktrace into a string output
	 * 
	 * @param excep
	 * @return
	 */
	public static String getExceptionAsString(Throwable excep) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		excep.printStackTrace(pw);

		// stack trace as a string
		return sw.toString();
	}
	
	
	/**
	 * @param str
	 * @return
	 */
	public static boolean isNullOrEmpty(String str) {
		if (str == null) {
			return true;
		} else if (str.trim().equals("")) {
			return true;
		} else if (str.trim().equals(BackendConstants.nullData)) {
			return true;
		}
		return false;
	}
	
	/**
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public static String getConnectionConfig(String key) throws IOException {
		Properties properties = null;
		String value = "";
		properties = new Properties();
		InputStream inputStream = null;

		inputStream = ServiceUtils.class.getClassLoader().getResourceAsStream(CONFIG_FILE);

		if (inputStream != null)
			properties.load(inputStream);
		if (properties != null)
			value = properties.getProperty(key);

		return value;
	}
}
