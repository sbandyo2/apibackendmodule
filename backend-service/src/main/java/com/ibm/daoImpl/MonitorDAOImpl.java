package com.ibm.daoImpl;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

import org.json.simple.JSONArray;

import com.ibm.consants.BackendConstants;
import com.ibm.dao.MonitorDAO;
import com.ibm.exception.ServiceException;
import com.ibm.model.MonitorVO;
import com.ibm.utils.ServiceUtils;

public class MonitorDAOImpl extends BaseDAOImpl implements MonitorDAO {

	@Override
	public List<MonitorVO> getMonitorRecords(String param)throws ServiceException {
		List<MonitorVO> monitorVOs = null;
		
		getSearchResults(BackendConstants.MONITOR_DATASTORE, param);
		monitorVOs = (List<MonitorVO>) getDataList();
		
		return monitorVOs;
	}
	
	@Override
	public JSONArray getMonitorRecordsAsJson(String param)throws ServiceException {
		JSONArray array = null;
		
		getSearchResultsWithOutPojo(BackendConstants.MONITOR_DATASTORE,param);
		array = getRawDataLIst();
		
		
		
		return array;
	}

	@Override
	public void updateMonitorRecords(MonitorVO monitorVO)
			throws ServiceException {
		doUpdate(monitorVO.get_id(), BackendConstants.MONITOR_DATASTORE, monitorVO);
	}

	@Override
	public void saveMonitorRecord(MonitorVO monitorVO)throws ServiceException {
		
		BigDecimal seq =  getNextSeq();
		monitorVO.setTransactionID(seq.longValue());
		
		save(BackendConstants.MONITOR_DATASTORE, monitorVO);
		
	}


	@Override
	public InputStream getAttachmentForDownload(String fileName)
			throws ServiceException {
		return getAttachment(BackendConstants.DOWNLOAD_ATTACHMENT, fileName,BackendConstants.XML);
	}

	@Override
	public void saveAsAttachment(String fileName, StringBuffer data,String fType)
			throws ServiceException {
		//remove the attachment if any
		removeAttachment(BackendConstants.DOWNLOAD_ATTACHMENT, fileName);
		
		pauseProcess();
		//insert the record
		saveAttachment(BackendConstants.DOWNLOAD_ATTACHMENT, fileName, data,fType);
		
	}
	
	@Override
	public InputStream getJSONAttachmentForDownload(String fileName)throws ServiceException {
		return getAttachment(BackendConstants.DOWNLOAD_ATTACHMENT, fileName,BackendConstants.JSON);
	}
	

	@Override
	public Class<? extends Object> getPojoClass() {
		return MonitorVO.class;
	}
	
	@Override
	public BigDecimal getNextSeq() throws ServiceException {
		BigDecimal seq = null;
		
		seq = getMaxValue(BackendConstants.MONITOR_DATASTORE, BackendConstants.CLOUDANT_VIEW_01,
				BackendConstants.MONITOR_VIEW_INDEX);
		
		if(seq== null)
			seq= new BigDecimal(0);
			

		return seq.add(new BigDecimal(BackendConstants.SEQ_COUNTER));
	}
	
	@Override
	public String getAppWiseCount(String appType) throws ServiceException {
		BigDecimal count = null;
		Object[] paramsObj = null;
		String[] applications = null;
		String countParam = null;
		
		if(appType!=null && appType.contains("~")){
			applications	 = appType.split("~");
			
			for(String application : applications){
				
				paramsObj = new Object[] {application};
				count = getCountWithParam(BackendConstants.MONITOR_DATASTORE, BackendConstants.CLOUDANT_VIEW_01,
						BackendConstants.MONITOR_VIEWCOUNT_INDEX, paramsObj);
				if(count== null)
					count = new BigDecimal(0);
				
				if(ServiceUtils.isNullOrEmpty(countParam)){
					countParam = "["+count;
				}else {
					countParam = countParam + ","+  count;
				}
			}
		}
		
		
		countParam = countParam + "]";
		
		pauseProcess();
			

		return countParam;
	}
}
