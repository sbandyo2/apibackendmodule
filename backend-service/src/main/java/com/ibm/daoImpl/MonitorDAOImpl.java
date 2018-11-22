package com.ibm.daoImpl;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

import com.ibm.consants.BackendConstants;
import com.ibm.dao.MonitorDAO;
import com.ibm.exception.ServiceException;
import com.ibm.model.MonitorVO;

public class MonitorDAOImpl extends BaseDAOImpl implements MonitorDAO {

	@Override
	public List<MonitorVO> getMonitorRecords(String param)throws ServiceException {
		List<MonitorVO> monitorVOs = null;
		getSearchResults(BackendConstants.MONITOR_DATASTORE, param);
		
		monitorVOs = (List<MonitorVO>) getDataList();
		return monitorVOs;
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
		return getAttachment(BackendConstants.DOWNLOAD_ATTACHMENT, fileName);
	}

	@Override
	public void saveAsAttachment(String fileName, StringBuffer data)
			throws ServiceException {
		//remove the attachment if any
		removeAttachment(BackendConstants.DOWNLOAD_ATTACHMENT, fileName);
		
		pauseProcess();
		//insert the record
		saveAttachment(BackendConstants.DOWNLOAD_ATTACHMENT, fileName, data);
		
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

}
