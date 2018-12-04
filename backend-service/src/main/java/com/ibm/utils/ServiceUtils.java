package com.ibm.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

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
	
	/**
	 * @return
	 */
	public static String formatInputDate(String date){
		DateFormat dateFormat = null;
		Date srcDate = null;
		String formattedDate = null;
		try {
			dateFormat = new SimpleDateFormat("mm/dd/yyyy");
			srcDate = dateFormat.parse(date);
			
			dateFormat = new SimpleDateFormat("yyyy-mm-dd");
			formattedDate =dateFormat.format(srcDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return	formattedDate;
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
	
	/**
	 * Sample Json :
	 * "{\"cartNo\":\"PR10063\",\"upstreamTrasactionId\":\"\",\"todate\":\"\",\"fromdate\":\"\",\"status\":\"Success\",\"upstreamAppType\":\"CSATM\"}"
	 * 
	 * @param param
	 * @return
	 */
	public static String prepareParams(String param) {
		
		
		
		JSONObject jsonObject = null;
		jsonObject = new JSONObject(param);
		String paramToSearch  = null;
		if(jsonObject.has("cartNo") && !ServiceUtils.isNullOrEmpty(jsonObject.getString("cartNo"))){
			
			paramToSearch ="cartID:'"+jsonObject.getString("cartNo")+"'";
		}
		if(jsonObject.has("status") && !ServiceUtils.isNullOrEmpty(jsonObject.getString("status"))){
			if(ServiceUtils.isNullOrEmpty(paramToSearch))
				paramToSearch ="status:'"+jsonObject.getString("status")+"'";
			else
				paramToSearch =paramToSearch +" AND status:'"+jsonObject.getString("status")+"'";
			
		}
		if(jsonObject.has("upstreamAppType") && !ServiceUtils.isNullOrEmpty(jsonObject.getString("upstreamAppType"))){
			
			String upstreamId = null;
			upstreamId = jsonObject.getString("upstreamAppType");
			
			if(upstreamId.contains("(")){
				
				if(ServiceUtils.isNullOrEmpty(paramToSearch))
					paramToSearch ="applicationType:"+jsonObject.getString("upstreamAppType");
				else
					paramToSearch =paramToSearch +" AND applicationType:"+jsonObject.getString("upstreamAppType");
			} else {
				
				if(ServiceUtils.isNullOrEmpty(paramToSearch))
					paramToSearch ="applicationType:'"+jsonObject.getString("upstreamAppType")+"'";
				else
					paramToSearch =paramToSearch +" AND applicationType:'"+jsonObject.getString("upstreamAppType")+"'";
			}
		}
		if(jsonObject.has("upstreamTrasactionId") && !ServiceUtils.isNullOrEmpty(jsonObject.getString("upstreamTrasactionId"))){
					
			if(ServiceUtils.isNullOrEmpty(paramToSearch))
				paramToSearch ="applicationTransactionNumber:'"+jsonObject.getString("upstreamTrasactionId")+"'";
			else
				paramToSearch =paramToSearch +" AND applicationTransactionNumber:'"+jsonObject.getString("upstreamTrasactionId")+"'";
			
		}
		if(jsonObject.has("fromdate") && !ServiceUtils.isNullOrEmpty(jsonObject.getString("fromdate"))){
			
			if(ServiceUtils.isNullOrEmpty(paramToSearch))
				paramToSearch ="createdTs:[\""+jsonObject.getString("fromdate")+"\" TO \""+jsonObject.getString("todate")+"\"]";
			else
				paramToSearch =paramToSearch +" AND createdTs:[\""+jsonObject.getString("fromdate")+"\" TO \""+jsonObject.getString("todate")+"\"]";
		}

		return paramToSearch;
	}
	
	
	public static String prepareBatchParams(String param) {
		
		JSONObject jsonObject = null;
		jsonObject = new JSONObject(param);
		String paramToSearch  = null;
		
		if(jsonObject.has("fromdate") && !ServiceUtils.isNullOrEmpty(jsonObject.getString("fromdate"))){
			
			if(ServiceUtils.isNullOrEmpty(paramToSearch))
				paramToSearch ="date:[\""+jsonObject.getString("fromdate")+"\" TO \""+jsonObject.getString("todate")+"\"]";
			else
				paramToSearch =paramToSearch +" AND date:[\""+jsonObject.getString("fromdate")+"\" TO \""+jsonObject.getString("todate")+"\"]";
		}

		return paramToSearch;
	}
	
	/**
	 * @param param
	 * @return
	 */
	public static String getParamsForSupplierSearch(String param) {
		String paramforSearch = null;
		String paramStr = null;
		if(!ServiceUtils.isNullOrEmpty(param)){
			if(param.contains(",")){
				String [] suppIds = param.split(",");

				for(String suppId : suppIds){
					if(!ServiceUtils.isNullOrEmpty(suppId)){
						if(ServiceUtils.isNullOrEmpty(paramStr)){
							paramStr = "LocationID:('"+suppId+"'";
						}else {
							paramStr = paramStr + " OR '"+suppId+"'";
						}	
					}
				}
				
				if(!ServiceUtils.isNullOrEmpty(paramStr)){
					paramforSearch = paramStr + ")";
				}
			}else {
				paramforSearch = "LocationID:'"+param+"'";
			}
		}
		return paramforSearch;
	}
	
	/**
	 * @return
	 */
	public static  List<String> getDays(){
	List<String> currentDtaes = null;
	  currentDtaes = new ArrayList<String>();
	  Calendar cal = Calendar.getInstance();
	  cal.set(Calendar.DAY_OF_MONTH, 1);
	    /*cal.set(Calendar.MONTH, 1);
	    
	    int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);*/
	    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	    for (int i = 0; i < 31; i++) {
	        cal.set(Calendar.DAY_OF_MONTH, i + 1);
	        currentDtaes.add(df.format(cal.getTime()));
	    }
	    
	    return currentDtaes;
	}
}
