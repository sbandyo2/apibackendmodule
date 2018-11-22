package com.ibm.cloudant.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.ibm.consants.BackendConstants;
import com.ibm.exception.ServiceException;
import com.ibm.utils.ServiceUtils;

public class CloudantDBUtil {
 
	
	/**
	 * Get the cloudant client
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public CloudantClient getCloudantContext() throws ServiceException {

		CloudantClient cloudantObj = null;
		try {
			cloudantObj = ClientBuilder.url(new URL(ServiceUtils.getConnectionConfig(BackendConstants.CLOUDANT_URI)))
					.username(ServiceUtils.getConnectionConfig(BackendConstants.CLOUDANT_USER))
					.password(ServiceUtils.getConnectionConfig(BackendConstants.CLOUDANT_PASSWORD)).build();

		} catch (MalformedURLException e) {
			throw new ServiceException(e.getMessage());
		} catch (IOException e) {
			throw new ServiceException(e.getMessage());
		}

		
		return cloudantObj;
	}

	
	/**
	 * Shut down the client builder
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public void closeConnection(CloudantClient cloudantClient) throws ServiceException {

		try {
			if(cloudantClient!=null)
				cloudantClient.shutdown();

		}  catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}

		
		return ;
	}
	
	/**
	 * Check if Cloudant DB exist
	 * @param dbName
	 * @return
	 */
	public boolean isCloudantDBExist( String dbName,CloudantClient cloudantObj) {
		boolean isExist = false;

		// Get a List of all the databases this Cloudant account
		List<String> databases = cloudantObj.getAllDbs();
		for (String db : databases) {
			if(dbName.equalsIgnoreCase(db)){
				isExist = true;
				break;
			}
		}

		return isExist;
	}
	
	/**
	 * @param dbName
	 * @return
	 */
	public Database getDB(String dbName,CloudantClient cloudantObj){
		
		return cloudantObj.database(dbName, false);
	}
	
	
	/**
	 * @param dbName
	 */
	public void createDB(String dbName,CloudantClient cloudantObj){
		
		cloudantObj.createDB(dbName);
	}
}
