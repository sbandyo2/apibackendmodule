package com.ibm.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.ibm.bean.VOWrapperDTO;
import com.ibm.consants.BackendConstants;
import com.ibm.dao.MonitorDAO;
import com.ibm.daoImpl.MonitorDAOImpl;
import com.ibm.exception.ServiceException;
import com.ibm.model.MonitorVO;
import com.ibm.model.QueryDetailsVO;
import com.ibm.utils.ServiceUtils;
import com.netflix.discovery.EurekaClient;

@RestController
public class BackenedServiceController {
	Logger logger = LoggerFactory.getLogger(BackenedServiceController.class);

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private EurekaClient eurekaClient;
	
	@Autowired
	private MonitorDAO monitorDAO;

	@RequestMapping(value = "/getAttachment/{fileId}", method =RequestMethod.GET)
	public Response gettContent(@PathVariable String fileId) {
		logger.info("Starting database transaction ");

		InputStream content = null;
		StringBuffer  xmlContent = null;
		try {
			content = monitorDAO.getAttachmentForDownload(fileId);
			xmlContent = ServiceUtils.getStringBuffer(content);
		} catch (ServiceException e) {
			logger.error(e.getMessage());
		}

		logger.info("Finishing database  transaction ");
		return Response.status(200).type(MediaType.TEXT_XML).entity(xmlContent.toString()).build();
	}
	
	@RequestMapping(value = "/fetchResult", method =RequestMethod.POST)
	public String gettTransactions(@RequestBody String param) {
		logger.info("Starting database transaction ");

		JSONArray jsonArray = null;
		try {
			jsonArray = monitorDAO.getMonitorRecordsAsJson(param);
		} catch (ServiceException e) {
			logger.error(e.getMessage());
		}

		logger.info("Finishing database  transaction ");
		return jsonArray.toJSONString();
	}
	
	@RequestMapping(value = "/dbinsert", method = RequestMethod.POST)
	public String insertTransaction(@RequestBody MonitorVO monitorVO) {
		logger.info("Starting database transaction ");

		String msg = BackendConstants.SUCCESS;
		try {
			monitorDAO.saveMonitorRecord(monitorVO);
		} catch (ServiceException e) {
			logger.error(e.getMessage());
			msg = BackendConstants.ERROR;
		}

		logger.info("Finishing database  transaction ");
		return msg;
	}
	
	@RequestMapping(value = "/dbattachinsert", method = RequestMethod.POST)
	public String insertAttachment(@RequestBody VOWrapperDTO voWrapperDTO) {
		logger.info("Starting database transaction ");

		String msg = BackendConstants.SUCCESS;
	
		try {
			if(voWrapperDTO.getRequestXml()!= null && !ServiceUtils.isNullOrEmpty(voWrapperDTO.getRequestXml().toString())){
				monitorDAO.saveAsAttachment(voWrapperDTO.getFileName(), voWrapperDTO.getRequestXml());	
			}else if(voWrapperDTO.getResponseXml()!= null && !ServiceUtils.isNullOrEmpty(voWrapperDTO.getResponseXml().toString())){
				monitorDAO.saveAsAttachment(voWrapperDTO.getFileName(), voWrapperDTO.getResponseXml());
			}
		} catch (ServiceException e) {
			logger.error(e.getMessage());
			msg = BackendConstants.ERROR;
		}

		logger.info("Finishing database  transaction ");
		return msg;
	}
	
	
	
	@RequestMapping(value = "/fetchDbRecord", method = RequestMethod.POST)
	public VOWrapperDTO fetchTransaction(@RequestBody QueryDetailsVO queryDetailsVO) {
		
		logger.info("Starting database fetch transaction ");
		
		String param = null;
		List<MonitorVO> monitorVOs = null;
		VOWrapperDTO transactionVO = null;
		
		try {
			
			transactionVO = new VOWrapperDTO();
			param = queryDetailsVO.getParam();
		
			monitorVOs = monitorDAO.getMonitorRecords(param);
			
			if(monitorVOs== null)
				monitorVOs = new ArrayList<>();
			
			transactionVO.setMonitorVOs(monitorVOs);
			
		} catch (ServiceException e) {
			logger.error("Exception happened during fetch "+e.getMessage() + " for query"+param);
		}	
		
		logger.info("Finishing database  fetch transaction ");
		
		return transactionVO;
	}
	
	@RequestMapping(value = "/updateDbRecord", method = RequestMethod.POST)
	public String updateTransaction(@RequestBody QueryDetailsVO queryDetailsVO) {
		
		logger.info("Starting database fetch transaction ");
		
		String cartNumber = null;
		String fileName = null;
		String status = null;
		MonitorVO monitorVO = null;
		
		try {
			monitorVO = queryDetailsVO.getMonitorVO();
			fileName = monitorVO.getApplicationTransactionNumber()+"_"+BackendConstants.RESPONSE;
			
			//save the attachment
			monitorDAO.saveAsAttachment(fileName, queryDetailsVO.getResponseXml());
			
			//set the response fileName
			monitorVO.setResponseXmlID(fileName);
			
			cartNumber = monitorVO.getCartID();
			
			if(ServiceUtils.isNullOrEmpty(cartNumber))
				status = BackendConstants.ERROR;
			else
				status = BackendConstants.SUCCESS;
			//set the status
			monitorVO.setStatus(status);
			
			//update the transaction
			monitorDAO.updateMonitorRecords(monitorVO);
			
		} catch (ServiceException e) {
			logger.error("Exception happened during update "+e.getMessage() + " for ID "+monitorVO.getApplicationTransactionNumber());
		}	
		
		logger.info("Finishing database  fetch transaction ");
		
		return String.valueOf(monitorVO.getTransactionID());
	}
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	
	@Bean
	MonitorDAO monitorDAO()
	{
	    return new MonitorDAOImpl();
	}
	
}
