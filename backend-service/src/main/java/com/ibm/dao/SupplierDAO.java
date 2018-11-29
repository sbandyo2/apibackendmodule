package com.ibm.dao;

import org.json.JSONObject;

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
}
