package com.ibm.dao;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

import org.json.simple.JSONArray;

import com.ibm.exception.ServiceException;
import com.ibm.model.MonitorVO;

public interface MonitorDAO extends BaseDAO {

	/**
	 * @param param
	 * @return
	 * @throws ServiceException
	 */
	public List<MonitorVO> getMonitorRecords (String param) throws ServiceException;
	
	/**
	 * @param monitorVO
	 * @throws ServiceException
	 */
	public void updateMonitorRecords (MonitorVO monitorVO) throws ServiceException;
	
	/**
	 * @param monitorVO
	 * @throws ServiceException
	 */
	public void saveMonitorRecord (MonitorVO monitorVO) throws ServiceException;
	
		
	/**
	 * @param fileName
	 * @return
	 * @throws CDAException
	 */
	public InputStream getAttachmentForDownload(String fileName) throws ServiceException;
	
	/**
	 * @param fileName
	 * @param data
	 * @throws CDAException
	 */
	public void saveAsAttachment(String fileName, StringBuffer data,String fType) throws ServiceException ;
	
	
	/**
	 * @param param
	 * @return
	 * @throws ServiceException
	 */
	public JSONArray getMonitorRecordsAsJson(String param)throws ServiceException;
	
	/**
	 * @return
	 * @throws ServiceException
	 */
	public BigDecimal getNextSeq() throws ServiceException;
	
	/**
	 * @param appType
	 * @return
	 * @throws ServiceException
	 */
	public String getAppWiseCount(String appType) throws ServiceException ;
}
