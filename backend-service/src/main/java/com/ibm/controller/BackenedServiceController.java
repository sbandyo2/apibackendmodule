package com.ibm.controller;

import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.ibm.bean.VOWrapperDTO;
import com.ibm.consants.BackendConstants;
import com.ibm.dao.MonitorDAO;
import com.ibm.dao.SupplierDAO;
import com.ibm.daoImpl.MonitorDAOImpl;
import com.ibm.daoImpl.SupplierDAOImpl;
import com.ibm.exception.ServiceException;
import com.ibm.model.MonitorVO;
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
	
	@Autowired
	private SupplierDAO supplierDAO;

	@RequestMapping(value = "/fetchBatchtrackerDetails", method =RequestMethod.POST)
	public String gettBatchTrackerInfo(@RequestBody String param) {
		logger.info("Starting database transaction  ");
		
		JSONArray jsonArray = null;
		
		String paramToSearch = null;
		try {
			paramToSearch = ServiceUtils.prepareBatchParams(param);
			
			logger.info("Initiating search with param"+paramToSearch);
			
			
			if(!ServiceUtils.isNullOrEmpty(paramToSearch))
				jsonArray = supplierDAO.getBatchRecordsAsJson(paramToSearch);
			
		} catch (ServiceException | JSONException e) {
			logger.error(e.getMessage());
		}

		logger.info("Finishing database  transaction ");
		return jsonArray.toJSONString();
	}
	
	@RequestMapping(value = "/getCSVContent/{fileId}", method =RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<?> gettCSV(@PathVariable String fileId) {
		logger.info("Starting database transaction ");

		InputStream content = null;
		StringBuffer  xmlContent = null;
		try {
			content = supplierDAO.getSuppAttachmentForDownload(fileId);
			xmlContent = ServiceUtils.getStringBuffer(content);
		} catch (ServiceException e) {
			logger.error(e.getMessage());
		}

		logger.info("Finishing database  transaction ");
		return ResponseEntity.status(HttpStatus.OK).body(xmlContent.toString());
	}
	
	@RequestMapping(value = "/getAttachment/{fileId}", method =RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<?> getContent(@PathVariable String fileId) {
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
		return ResponseEntity.status(HttpStatus.OK).body(xmlContent.toString());
	}
	
	@RequestMapping(value = "/getJSONAttachment/{fileId}", method =RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getJSONCOntentt(@PathVariable String fileId) {
		logger.info("Starting database transaction ");

		InputStream content = null;
		StringBuffer  xmlContent = null;
		try {
			content = monitorDAO.getJSONAttachmentForDownload(fileId);
			xmlContent = ServiceUtils.getStringBuffer(content);
		} catch (ServiceException e) {
			logger.error(e.getMessage());
		}

		logger.info("Finishing database  transaction ");
		return ResponseEntity.status(HttpStatus.OK).body(xmlContent.toString());
	}
	
	
	@RequestMapping(value = "/getSuppPartneringInfo", method =RequestMethod.POST)
	public String getSupplierPrtneringInfo(@RequestBody String param) {
		logger.info("Starting database transaction for partnering ");
		JSONObject jsonObject = null;
		String paramforSearch = null;
		try {
			
			paramforSearch = ServiceUtils.getParamsForSupplierSearch(param);
			
			logger.info("Initiating search with param "+paramforSearch);
			jsonObject = supplierDAO.getSupplierPartnering(paramforSearch);
			
		} catch (ServiceException | JSONException e) {
			logger.error(e.getMessage());
		}

		logger.info("Finishing database  transaction for pertnering info ");
		
		return jsonObject.toString();
	}
	
	@RequestMapping(value = "/getSuppPartners", method =RequestMethod.POST)
	public String getSupplierPartners(@RequestBody String param) {
		logger.info("Starting database transaction for partnering ");
		JSONArray jsonArray = null;
		String paramforSearch = null;
		try {
			
			paramforSearch = ServiceUtils.prepareParamsForLocationId(param);
			
			logger.info("Initiating search with param "+paramforSearch);
			jsonArray = supplierDAO.getSuppPartnerInfo(paramforSearch);
			
		} catch (ServiceException | JSONException e) {
			logger.error(e.getMessage());
		}

		logger.info("Finishing database  transaction for pertnering info ");
		
		return jsonArray.toString();
	}
	
	
	@RequestMapping(value = "/getAribaSuppId", method =RequestMethod.POST)
	public String getSupplierPrtneringID(@RequestBody String suppId) {
		logger.info("Starting database transaction for partnering  ID");
		String suppPartneringID = null;
		try {
			
			suppPartneringID = supplierDAO.getAribaSupplierId(suppId);
			
		} catch (ServiceException | JSONException e) {
			logger.error(e.getMessage());
		}

		logger.info("Finishing database  transaction for pertnering ID");
		
		return suppPartneringID;
	}


	@RequestMapping(value = "/fetchAppsCount", method =RequestMethod.POST)
	public String gettAppWiseCount(@RequestBody String param) {
		logger.info("Starting database transaction  ");
		String appCountStr = null;
		try {
			
			logger.info("Initiating count string search with "+param);
			appCountStr = monitorDAO.getAppWiseCount(param);
			
		} catch (ServiceException | JSONException e) {
			logger.error(e.getMessage());
		}

		logger.info("Finishing database  transaction ");
		
		return appCountStr;
	}
	
	@RequestMapping(value = "/fetchMonthlyCount", method =RequestMethod.POST)
	public String getAppWiseMonthlyCount(@RequestBody String param) {
		logger.info("Starting database transaction  ");
		String appCountStr = null;
		try {
			
			logger.info("Initiating count string search with "+param);
			appCountStr = monitorDAO.getAppWiseMonthlyCount(param);
			
		} catch (ServiceException | JSONException e) {
			logger.error(e.getMessage());
		}

		logger.info("Finishing database  transaction ");
		
		return appCountStr;
	}
	
	@RequestMapping(value = "/fetchAppsStatusCount", method =RequestMethod.POST)
	public String gettAppWiseStatusCount(@RequestBody String param) {
		logger.info("Starting database transaction for application "+param);
		String appCountStr = null;
		try {
			
			appCountStr = monitorDAO.getAppWiseStatusCount(param);
			
		} catch (ServiceException | JSONException e) {
			logger.error(e.getMessage());
		}

		logger.info("Finishing database  transaction ");
		
		return appCountStr;
	}
	
	@RequestMapping(value = "/fetchResult", method =RequestMethod.POST)
	public String gettTransactions(@RequestBody String param) {
		logger.info("Starting database transaction  ");
		
		JSONArray jsonArray = null;
		
		String paramToSearch = null;
		try {
			paramToSearch = ServiceUtils.prepareParams(param);
			
			logger.info("Initiating search with param"+paramToSearch);
			
			
			if(!ServiceUtils.isNullOrEmpty(paramToSearch))
				jsonArray = monitorDAO.getMonitorRecordsAsJson(paramToSearch);
			
		} catch (ServiceException | JSONException e) {
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
		logger.info("Starting database transaction for attachment saving");

		String msg = BackendConstants.SUCCESS;
	
		try {
			if(BackendConstants.XML.equalsIgnoreCase(voWrapperDTO.getFileType())){
				if(voWrapperDTO.getRequestXml()!= null && !ServiceUtils.isNullOrEmpty(voWrapperDTO.getRequestXml().toString())){
					monitorDAO.saveAsAttachment(voWrapperDTO.getFileName(), voWrapperDTO.getRequestXml(),voWrapperDTO.getFileType());	
				}else if(voWrapperDTO.getResponseXml()!= null && !ServiceUtils.isNullOrEmpty(voWrapperDTO.getResponseXml().toString())){
					monitorDAO.saveAsAttachment(voWrapperDTO.getFileName(), voWrapperDTO.getResponseXml(),voWrapperDTO.getFileType());
				}else if(voWrapperDTO.getRecievedData()!= null && !ServiceUtils.isNullOrEmpty(voWrapperDTO.getRecievedData().toString())){
					monitorDAO.saveAsAttachment(voWrapperDTO.getFileName(), voWrapperDTO.getRecievedData(),voWrapperDTO.getFileType());
				}
			}else{
				if(voWrapperDTO.getRecievedData()!= null && !ServiceUtils.isNullOrEmpty(voWrapperDTO.getRecievedData().toString())){
					monitorDAO.saveAsAttachment(voWrapperDTO.getFileName(), voWrapperDTO.getRecievedData(),voWrapperDTO.getFileType());
				}
			}
			
		} catch (ServiceException e) {
			logger.error(e.getMessage());
			msg = BackendConstants.ERROR;
		}

		logger.info("Finishing database  transaction for attachment saving");
		return msg;
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
	
	@Bean
	SupplierDAO supplierDAO(){
		return new SupplierDAOImpl();
	}
}
