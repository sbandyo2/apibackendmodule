package com.ibm.dao;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.JsonObject;
import com.ibm.exception.ServiceException;
import com.ibm.model.BaseVO;

public interface BaseDAO {

	
	/**
	 * @param dataStore
	 * @param docId
	 * @return
	 * @throws ServiceException
	 */
	public Object findById(String dataStore,String docId) throws ServiceException;
	/**
	 * @param dataStore
	 * @param obj
	 * @throws ServiceException
	 */
	public void save(String dataStore, Object obj) throws ServiceException;

	
	/**
	 * @param docId
	 * @param dataStore
	 * @param baseVO
	 * @return
	 * @throws ServiceException
	 */
	public JsonObject doUpdate(String docId, String dataStore,BaseVO baseVO) throws ServiceException;

	
	/**
	 * @param docId
	 * @param dataStore
	 * @param baseVO
	 * @throws ServiceException
	 */
	public void delete(String docId, String dataStore,BaseVO baseVO) throws ServiceException;

	
	/**
	 * @param datastore
	 * @param param
	 * @throws ServiceException
	 */
	public void getSearchResults(Object datastore, String param) throws ServiceException;
	
	/**
	 * @param datastore
	 * @param viewName
	 * @param viewIndex
	 * @return
	 * @throws ServiceException
	 */
	public BigDecimal getMaxValue(String datastore, String viewName, String viewIndex) throws ServiceException;
	
	
	/**
	 * @param datastore
	 * @param viewName
	 * @param viewIndex
	 * @return
	 * @throws ServiceException
	 */
	public BigDecimal getSum(String datastore, String viewName, String viewIndex) throws ServiceException;
	
	
	/**
	 * @param datastore
	 * @param viewName
	 * @param viewIndex
	 * @return
	 * @throws ServiceException
	 */
	public BigDecimal getCount(String datastore, String viewName, String viewIndex) throws ServiceException;
	
	/**
	 * @param datastore
	 * @return
	 * @throws ServiceException
	 */
	public List<? extends Object> getAllRecords(String datastore) throws ServiceException;
	
	
	
	/**
	 * @param datastore
	 * @param viewName
	 * @param viewIndex
	 * @param param
	 * @return
	 * @throws ServiceException
	 */
	public BigDecimal getMaxWithParam(String datastore, String viewName, String viewIndex,Object[] param) throws ServiceException;
	
	
	/**
	 * @param datastore
	 * @param viewName
	 * @param viewIndex
	 * @param params
	 * @return
	 */
	public BigDecimal getCountWithParam(String datastore, String viewName, String viewIndex, Object[] params) throws ServiceException;
	
	/**
	 * @param datastore
	 * @param param
	 * @param list
	 * @throws ServiceException
	 */
	public  void getSearchWithoutScan(Object datastore, String param, List<Object> list) throws ServiceException;
	
	
	/**
	 * @param datastore
	 * @param param
	 * @param colNames
	 * @throws ServiceException
	 */
	public  void getSearchResultsWithOutPojo(Object datastore, String param) throws ServiceException;
	
	
}
