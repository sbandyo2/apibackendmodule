package com.ibm.dao;

import java.io.InputStream;

import org.json.JSONObject;
import org.json.simple.JSONArray;

import com.ibm.exception.ServiceException;

public interface SupplierDAO  extends BaseDAO{

	/**
	 * @param params
	 * @return
	 */
	public JSONObject getSupplierPartnering(String params) throws ServiceException ;
	
	
	/**
	 * @param suppId
	 * @return
	 * @throws ServiceException
	 */
	public String getAribaSupplierId(String suppId) throws ServiceException ;
	
	
	/**
	 * @param fileName
	 * @return
	 * @throws ServiceException
	 */
	public InputStream getSuppAttachmentForDownload(String fileName) throws ServiceException; 
	
	/**
	 * @param param
	 * @return
	 * @throws ServiceException
	 */
	public JSONArray getBatchRecordsAsJson(String param)throws ServiceException;
	
	
	
}
