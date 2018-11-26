package com.ibm.daoImpl;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.Search;
import com.cloudant.client.api.model.Attachment;
import com.cloudant.client.api.model.DesignDocument;
import com.cloudant.client.api.model.Document;
import com.cloudant.client.api.model.SearchResult;
import com.cloudant.client.api.views.AllDocsRequest;
import com.cloudant.client.api.views.AllDocsRequestBuilder;
import com.cloudant.client.api.views.AllDocsResponse;
import com.cloudant.client.api.views.Key;
import com.cloudant.client.api.views.Key.ComplexKey;
import com.cloudant.client.api.views.ViewRequest;
import com.cloudant.client.api.views.ViewRequestBuilder;
import com.cloudant.client.api.views.ViewResponse;
import com.cloudant.client.api.views.ViewResponse.Row;
import com.cloudant.client.org.lightcouch.DocumentConflictException;
import com.cloudant.client.org.lightcouch.NoDocumentException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.cloudant.utils.CloudantDBUtil;
import com.ibm.consants.BackendConstants;
import com.ibm.dao.BaseDAO;
import com.ibm.exception.ServiceException;
import com.ibm.model.BaseVO;
import com.ibm.utils.ServiceUtils;

public abstract class BaseDAOImpl implements BaseDAO {
	
	private List<Object> dataList = null;
	private JsonArray jsonArray  = null;
	
	
	/**
	 * @param dataStore
	 * @param fileName
	 * @throws DownloadException
	 */
	public InputStream removeAttachment(String dataStore,String fileName) throws ServiceException {
		CloudantDBUtil cloudantDBUtil = null;
		Database db = null;
		InputStream  is = null;
		
		
		// initialize
		cloudantDBUtil = new CloudantDBUtil();

		// get the context of CLoudant DB
		CloudantClient cloudantClient = cloudantDBUtil.getCloudantContext();

		// get the DB instance
		if(!cloudantDBUtil.isCloudantDBExist(dataStore, cloudantClient)){
			cloudantClient.createDB(dataStore);
		}
		
		db = cloudantDBUtil.getDB(dataStore,cloudantClient);
		Document document = null;
		try{
			document =db.find(Document.class, fileName);
		}catch(Exception e){
			document = null;
		}
		//delete only if document exist
		if(document!=null)
			db.remove(document);
		
		cloudantDBUtil.closeConnection(cloudantClient);
			
		return is;
	}
	
	/**
	 * @param dataStore
	 * @param fileName
	 * @throws DownloadException
	 */
	public Map<String, com.cloudant.client.org.lightcouch.Attachment> getAttachments(String dataStore,String fileName) throws ServiceException {
		CloudantDBUtil cloudantDBUtil = null;
		Database db = null;
		Map<String, com.cloudant.client.org.lightcouch.Attachment> attachments = null;
		
		// initialize
		cloudantDBUtil = new CloudantDBUtil();

		// get the context of CLoudant DB
		CloudantClient cloudantClient = cloudantDBUtil.getCloudantContext();

		// get the DB instance
		if(!cloudantDBUtil.isCloudantDBExist(dataStore, cloudantClient)){
			cloudantClient.createDB(dataStore);
		}
		
		db = cloudantDBUtil.getDB(dataStore,cloudantClient);

		Document document = db.find(Document.class, fileName);
		
		attachments = document.getAttachments();
		
		
		cloudantDBUtil.closeConnection(cloudantClient);
			
		return attachments;
	}
	/**
	 * @param dataStore
	 * @param fileName
	 * @throws DownloadException
	 */
	public InputStream getAttachment(String dataStore,String fileName) throws ServiceException {
		CloudantDBUtil cloudantDBUtil = null;
		Database db = null;
		InputStream  is = null;
		
		
		// initialize
		cloudantDBUtil = new CloudantDBUtil();

		// get the context of CLoudant DB
		CloudantClient cloudantClient = cloudantDBUtil.getCloudantContext();

		// get the DB instance
		if(!cloudantDBUtil.isCloudantDBExist(dataStore, cloudantClient)){
			cloudantClient.createDB(dataStore);
		}
		
		db = cloudantDBUtil.getDB(dataStore,cloudantClient);

		
		is = db.getAttachment(fileName, fileName+BackendConstants.XML);
		
		
		cloudantDBUtil.closeConnection(cloudantClient);
			
		return is;
	}
	
	/**
	 * @param dataStore
	 * @throws ServiceException
	 */
	public void deleteDB(String dataStore) throws ServiceException {
		
		CloudantDBUtil cloudantDBUtil = null;

		// initialize
		cloudantDBUtil = new CloudantDBUtil();

		// get the context of CLoudant DB
		CloudantClient cloudantClient = cloudantDBUtil.getCloudantContext();

		// check if DB exist and delete it before reinserting
		if(cloudantDBUtil.isCloudantDBExist(dataStore, cloudantClient)){
			cloudantClient.deleteDB(dataStore);
		}
		
	
	}
		
	
	/**
	 * @param dataStore
	 * @param indexitems
	 * @throws ServiceException
	 */
	public  void saveIndex(String dataStore, List<String> indexitems) throws ServiceException {
		pauseProcess();
		
		CloudantDBUtil cloudantDBUtil = null;
		Database db = null;
		// initialize
		cloudantDBUtil = new CloudantDBUtil();

		// get the context of CLoudant DB
		CloudantClient cloudantClient = cloudantDBUtil.getCloudantContext();

		// get the DB instance
		if(!cloudantDBUtil.isCloudantDBExist(dataStore, cloudantClient)){
			cloudantClient.createDB(dataStore);
		}
		
		db = cloudantDBUtil.getDB(dataStore,cloudantClient);

				
		DesignDocument designDocument = new DesignDocument();
		
		// Call setters on ddoc to populate document
		designDocument.setId("_design/_search");
		
		db.save(designDocument);
		
		
		DesignDocument ddoc = db.find(DesignDocument.class, "_design/_search");
		// Call setters to update values
		 
		 JsonObject analyzerObject = new JsonObject();
		 analyzerObject.addProperty("analyzer", "standard");
		 
		 JsonObject indexObject = new JsonObject();
		 
		 String indexStr = null;
		 for(String index : indexitems){
			 
			 if(ServiceUtils.isNullOrEmpty(indexStr))
				 indexStr = "index(\""+index+"\", doc."+index+");\n";
			 else
				 indexStr =indexStr+ "index(\""+index+"\", doc."+index+");\n";
		 }
		 
		 indexObject.addProperty("index", "function (doc) {\n "+indexStr+"  }");

		 JsonObject indexes = new JsonObject();
		 indexes.add("newSearch", analyzerObject);
		 indexes.add("newSearch", indexObject);
		 
		 ddoc.setIndexes(indexes);;
		 // Update the design document
		 db.update(ddoc);


		cloudantDBUtil.closeConnection(cloudantClient);
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.ariba.dataConfig.dao.BaseDAO#saveWithDBAndIndex(java.lang.String, java.util.Map, java.util.List)
	 */
	public  void saveAttachment(String dataStore,String fileName,StringBuffer data) throws ServiceException {
		
		CloudantDBUtil cloudantDBUtil = null;
		Database db = null;
		// initialize
		cloudantDBUtil = new CloudantDBUtil();

		// get the context of CLoudant DB
		CloudantClient cloudantClient = cloudantDBUtil.getCloudantContext();

		// get the DB instance
		if(!cloudantDBUtil.isCloudantDBExist(dataStore, cloudantClient)){
			cloudantClient.createDB(dataStore);
		}
		
		db = cloudantDBUtil.getDB(dataStore,cloudantClient);

			Attachment attachment = new Attachment(fileName,
	                "text/xml");

	        attachment.setData(Base64.encodeBase64String(data.toString().getBytes()));
		
	        Document document = new Document();
	        document.setId(fileName);
	        document.addAttachment(fileName+BackendConstants.XML, attachment);
	        
	
	        db.save(document);
		cloudantDBUtil.closeConnection(cloudantClient);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sp.dao.BaseDao#save(java.lang.String, java.lang.Object)
	 */
	public  void save(String dataStore, Object obj) throws ServiceException {
		pauseProcess();
		
		CloudantDBUtil cloudantDBUtil = null;
		Database db = null;
		// initialize
		cloudantDBUtil = new CloudantDBUtil();

		// get the context of CLoudant DB
		CloudantClient cloudantClient = cloudantDBUtil.getCloudantContext();

		// get the DB instance
		db = cloudantDBUtil.getDB(dataStore,cloudantClient);

		// insert the row
		db.save(obj);

		cloudantDBUtil.closeConnection(cloudantClient);
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.sp.dao.BaseDAO#findById(java.lang.String, java.lang.String)
	 */
	public Object findById(String dataStore,String docId) throws ServiceException {
		CloudantDBUtil cloudantDBUtil = null;
		Database db = null;
		// initialize
		cloudantDBUtil = new CloudantDBUtil();

		// get the context of CLoudant DB
		CloudantClient cloudantClient = cloudantDBUtil.getCloudantContext();

		// get the DB instance
		db = cloudantDBUtil.getDB(dataStore,cloudantClient);

		Object pojoObj = db.find(getPojoClass(),docId);

		pauseProcess();
		cloudantDBUtil.closeConnection(cloudantClient);
		
		return pojoObj;
	}
	
	/**
	 * @throws ServiceException
	 */
	public void pauseProcessForSearch() throws ServiceException{
		try {
			Thread.sleep(BackendConstants.THREAD__SEARCH_SLEEP_TIME);
		} catch (InterruptedException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	/**
	 * @throws ServiceException
	 */
	public void pauseProcess() throws ServiceException{
		try {
			Thread.sleep(BackendConstants.THREAD_SLEEP_TIME);
		} catch (InterruptedException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.sp.dao.BaseDAO#doUpdate(java.lang.String, java.lang.String)
	 */
	public  JsonObject doUpdate(String docId, String dataStore,BaseVO baseVO) throws ServiceException {

		pauseProcess();
		
		CloudantClient client = null;
		Database db = null;
		JsonObject output = null;

		output = new JsonObject();

		if (docId == null || docId.isEmpty()) {
			output.addProperty(BackendConstants.DB_RESULT_KEY, BackendConstants.DB_SEARCH_ERROR);
		} else {
			try {
				client = new CloudantDBUtil().getCloudantContext();
				db = client.database(dataStore, false);
				
				// update the record information
				db.update(baseVO);

				output.addProperty(BackendConstants.DB_RESULT_KEY, BackendConstants.DB_UPDATE_SUCCESS);

			} catch (NoDocumentException ex) {
				output.addProperty(BackendConstants.DB_RESULT_KEY, BackendConstants.DB_UPDATE_ERROR);
				throw new ServiceException(ex.getMessage());
			} catch (DocumentConflictException ex) {
				output.addProperty(BackendConstants.DB_RESULT_KEY, BackendConstants.DB_UPDATE_ERROR);
				throw new ServiceException(ex.getMessage());
			}finally{
				if(client!=null)
					client.shutdown();
			}
		}

		return output;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sp.dao.BaseDao#delete(java.lang.String, java.lang.String)
	 */
	public  void delete(String docId, String dataStore,BaseVO baseVO) throws ServiceException {
		CloudantClient client = null;
		JsonObject output = null;
		Database db = null;
		
		pauseProcess();
		
		output = new JsonObject();

		if (docId == null || docId.isEmpty()) {
			output.addProperty(BackendConstants.DB_RESULT_KEY, BackendConstants.DB_SEARCH_ERROR);
		} else {
			try {
				client = new CloudantDBUtil().getCloudantContext();
				db = client.database(dataStore, false);
				
				// delete the object from the document
				db.remove(baseVO);
			
				output.addProperty(BackendConstants.DB_RESULT_KEY, BackendConstants.DB_UPDATE_SUCCESS);

			} catch (NoDocumentException ex) {
				output.addProperty(BackendConstants.DB_RESULT_KEY, BackendConstants.DB_UPDATE_ERROR);
				throw new ServiceException(ex.getMessage());
			}
		}

	}

	/**
	 * @return Abstract method to return the Pojo instance before calling the
	 *         database operations methods
	 */
	public abstract Class<? extends Object> getPojoClass();


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sp.dao.BaseDAO#getSearchResults(java.lang.Object,
	 * java.lang.String)
	 */
	public  void getSearchResults(Object datastore, String param) throws ServiceException {
		// pause the process
		pauseProcessForSearch();
		CloudantClient client = null;
		client = new CloudantDBUtil().getCloudantContext();
		getSearchResults(client,datastore, param, null, null);
		
		//shut down the client builder
		client.shutdown();
	}

	
	/* (non-Javadoc)
	 * @see com.ibm.ariba.dataConfig.dao.BaseDAO#getSearchResultsWithOutPojo(java.lang.Object, java.lang.String, java.util.List)
	 */
	public void getSearchResultsWithOutPojo(Object datastore, String param) throws ServiceException {
		// pause the process
		//pauseProcessForSearch();
		CloudantClient client = null;
		client = new CloudantDBUtil().getCloudantContext();
		getSearchResultsWithoiutPojo(client,datastore, param, null, null);
		
		//shut down the client builder
		client.shutdown();
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.sp.dao.BaseDAO#getSearchWithoutScan(java.lang.Object, java.lang.String, java.util.List, com.cloudant.client.api.model.SearchResult)
	 */
	public  void getSearchWithoutScan(Object datastore, String param, List<Object> list) throws ServiceException {
		SearchResult<? extends Object> result = null;
		Database db = null;
		CloudantClient client = null;
		try {
			client = new CloudantDBUtil().getCloudantContext();
			dataList = new ArrayList<Object>();

			if (list != null && !list.isEmpty()) {
				dataList.addAll(list);
			}

			if (datastore instanceof String) {
				db = client.database(datastore.toString(), false);
			} else if (datastore instanceof Database) {
				db = (Database) datastore;
			}

			Search search = db.search(BackendConstants.DB_SEARCH_PATH);
			search.includeDocs(true);
			search.limit(BackendConstants.DB_SINGLE_LIMIT);
			

			result = search.querySearchResult(param, getPojoClass());
			//List<SearchResult<? extends Object>.SearchResultRow> rows = result.getRows();

			
			for (SearchResult<? extends Object>.SearchResultRow resultRow : result.getRows()) {
				Object application = resultRow.getDoc();
				dataList.add(application);
			}

		} catch (NoDocumentException ex) {
			throw new ServiceException(ex.getMessage());

		}

	}
	/**
	 * Recursive search method for scanning through each indexes
	 * 
	 * @param datastore
	 * @param param
	 * @param list
	 * @param searchResult
	 * @throws ServiceException
	 */
	private  void getSearchResults(CloudantClient client , Object datastore, String param, List<Object> list,
			SearchResult<? extends Object> searchResult) throws ServiceException {
		
		SearchResult<? extends Object> result = null;
		Database db = null;
		try {
			
			dataList = new ArrayList<Object>();

			if (list != null && !list.isEmpty()) {
				dataList.addAll(list);
			}

			if (datastore instanceof String) {
				db = client.database(datastore.toString(), false);
			} else if (datastore instanceof Database) {
				db = (Database) datastore;
			}

			Search search = db.search(BackendConstants.DB_SEARCH_PATH);
			search.includeDocs(true);
			search.limit(BackendConstants.DB_SEARCH_LIMIT);
			if (searchResult != null)
				search.bookmark(searchResult.getBookmark());

			result = search.querySearchResult(param, getPojoClass());

			//List<SearchResult<? extends Object>.SearchResultRow> rows = result.getRows();

			for (SearchResult<? extends Object>.SearchResultRow resultRow : result.getRows()) {
				Object application = resultRow.getDoc();
				dataList.add(application);
			}

			if (result.getRows() != null && !result.getRows().isEmpty()) {
				// pause process to avoid 409 exception, as the current limit is
				// 5 per second
				pauseProcessForSearch();
				//get the next book mark
				getSearchResults(client,db, param, dataList, result);
			}

		} catch (NoDocumentException ex) {
			throw new ServiceException(ex.getMessage());

		}

	}
	
	
	/**
	 * @param client
	 * @param datastore
	 * @param param
	 * @param list
	 * @param searchResult
	 * @param colNames
	 * @throws ServiceException
	 */
	private void getSearchResultsWithoiutPojo(CloudantClient client , Object datastore, String param, JsonArray list,
			SearchResult<? extends Object> searchResult) throws ServiceException {
		
		SearchResult<? extends Object> result = null;
		Database db = null;
		try {
			
			jsonArray = new JsonArray();

			if (list != null && list.size() >0) {
				jsonArray = list;
			}

			if (datastore instanceof String) {
				db = client.database(datastore.toString(), false);
			} else if (datastore instanceof Database) {
				db = (Database) datastore;
			}

			Search search = db.search(BackendConstants.DB_SEARCH_PATH);
			search.includeDocs(true);
			search.limit(BackendConstants.DB_SEARCH_LIMIT);
			if (searchResult != null)
				search.bookmark(searchResult.getBookmark());

			result = search.querySearchResult(param, Object.class);
			

			for (SearchResult<? extends Object>.SearchResultRow resultRow : result.getRows()) {
				Object application = resultRow.getDoc();
				JsonParser parser = new JsonParser(); 
				
				//String formattedString = application.toString().replaceAll("=", "\"=\"").replaceAll(", ", "\",\"").replace("{", "{\"").replace("}", "\"}");
				//JsonObject json = (JsonObject) parser.parse(application.toString());
				
				
				
				//adding the cols
				jsonArray.add(application.toString());;
				
			}

			if (result.getRows() != null && !result.getRows().isEmpty()) {
				// pause process to avoid 409 exception, as the current limit is
				// 5 per second
				pauseProcessForSearch();
				//get the next book mark
				getSearchResultsWithoiutPojo(client,db, param, jsonArray, result);
			}

		} catch (NoDocumentException ex) {
			throw new ServiceException(ex.getMessage());

		}

	}

	
	/**
	 * @param datastore
	 * @param param
	 * @param searchResult
	 * @param paginationLimit
	 * @return
	 * @throws ServiceException
	 */
	public  Map<String, Object> getSearchResultsForPagination(Object datastore, String param,
			SearchResult<? extends Object> searchResult,int paginationLimit) throws ServiceException {
		CloudantClient client = null;
		SearchResult<? extends Object> result = null;
		Database db = null;
		Map<String, Object> paginationMap = null;
		try {
			paginationMap = new HashMap<String, Object>();
			
			client = new CloudantDBUtil().getCloudantContext();
			dataList = new ArrayList<Object>();

			
			if (datastore instanceof String) {
				db = client.database(datastore.toString(), false);
			} else if (datastore instanceof Database) {
				db = (Database) datastore;
			}

			Search search = db.search(BackendConstants.DB_SEARCH_PATH);
			search.includeDocs(true);
			search.limit(paginationLimit);
			if (searchResult != null)
				search.bookmark(searchResult.getBookmark());

			result = search.querySearchResult(param, getPojoClass());

			//List<SearchResult<? extends Object>.SearchResultRow> rows = result.getRows();

			for (SearchResult<? extends Object>.SearchResultRow resultRow : result.getRows()) {
				Object application = resultRow.getDoc();
				dataList.add(application);
			}


		} catch (NoDocumentException ex) {
			throw new ServiceException(ex.getMessage());

		}
		
		//store search result and DB connection if record exist for this page
		if(dataList!=null && !dataList.isEmpty()){
			paginationMap.put(BackendConstants.SEARCH_RESULT_OBJECT, result);
			paginationMap.put(BackendConstants.DB_CONN_OBJ, db);	
		}
		
		
		return paginationMap;

	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sp.dao.BaseDAO#getMaxValue(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	public BigDecimal getMaxValue(String datastore, String viewName, String viewIndex) throws ServiceException {
		CloudantClient client = null;
		Database db = null;
		ViewRequestBuilder requestBuilder = null;
		ViewRequest<String, JsonObject> request = null;
		ViewResponse<String, JsonObject> response = null;
		List<Row<String, JsonObject>> rows = null;
		JsonObject jsonObject = null;
		BigDecimal maxValue = null;
		try {

			client = new CloudantDBUtil().getCloudantContext();
			db = client.database(datastore, false);
			requestBuilder = db.getViewRequestBuilder(viewName, viewIndex);
			request = requestBuilder.newRequest(Key.Type.STRING, JsonObject.class).reduce(true).returnThis().build();

			response = request.getResponse();

			rows = response.getRows();

			for (Row<String, JsonObject> resultRow : rows) {

				jsonObject = resultRow.getValue();
				maxValue = new BigDecimal(String.valueOf(jsonObject.get(BackendConstants.CLOUDANT_MAX_TYPE)));

			}

		} catch (NoDocumentException ex) {
			throw new ServiceException(ex.getMessage());

		} catch (IOException e) {
			throw new ServiceException(e.getMessage());
		}

		return maxValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sp.dao.BaseDAO#getSum(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	public BigDecimal getSum(String datastore, String viewName, String viewIndex) throws ServiceException {
		CloudantClient client = null;
		Database db = null;
		ViewRequestBuilder requestBuilder = null;
		ViewRequest<String, JsonObject> request = null;
		ViewResponse<String, JsonObject> response = null;
		List<Row<String, JsonObject>> rows = null;
		JsonObject jsonObject = null;
		BigDecimal sum = null;
		try {

			client = new CloudantDBUtil().getCloudantContext();
			db = client.database(datastore, false);
			requestBuilder = db.getViewRequestBuilder(viewName, viewIndex);
			request = requestBuilder.newRequest(Key.Type.STRING, JsonObject.class).reduce(true).returnThis().build();

			response = request.getResponse();

			rows = response.getRows();

			for (Row<String, JsonObject> resultRow : rows) {

				jsonObject = resultRow.getValue();
				sum = new BigDecimal(String.valueOf(jsonObject.get(BackendConstants.CLOUDANT_SUM_TYPE)));

			}

		} catch (NoDocumentException ex) {
			throw new ServiceException(ex.getMessage());

		} catch (IOException e) {
			throw new ServiceException(e.getMessage());
		}

		return sum;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sp.dao.BaseDAO#getCount(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	public BigDecimal getCount(String datastore, String viewName, String viewIndex) throws ServiceException {
		CloudantClient client = null;
		Database db = null;
		ViewRequestBuilder requestBuilder = null;
		ViewRequest<String, JsonObject> request = null;
		ViewResponse<String, JsonObject> response = null;
		List<Row<String, JsonObject>> rows = null;
		JsonObject jsonObject = null;
		BigDecimal count = null;
		try {

			client = new CloudantDBUtil().getCloudantContext();
			db = client.database(datastore, false);
			requestBuilder = db.getViewRequestBuilder(viewName, viewIndex);
			request = requestBuilder.newRequest(Key.Type.STRING, JsonObject.class).reduce(true).returnThis().build();

			response = request.getResponse();

			rows = response.getRows();

			for (Row<String, JsonObject> resultRow : rows) {

				jsonObject = resultRow.getValue();
				count = new BigDecimal(String.valueOf(jsonObject.get(BackendConstants.CLOUDANT_COUNT_TYPE)));

			}

		} catch (NoDocumentException ex) {
			throw new ServiceException(ex.getMessage());

		} catch (IOException e) {
			throw new ServiceException(e.getMessage());
		}

		return count;
	}
	
	/**
	 * @param datastore
	 * @param colNames
	 * @return
	 * @throws ServiceException
	 */
	public  List<List <Object>> getAllRecordsWithOutPojo(String datastore,List<String> colNames) throws ServiceException {
		CloudantClient client = null;
		Database db = null;
		AllDocsRequestBuilder requestBuilder = null;
		AllDocsRequest request = null;
		AllDocsResponse response = null;
		List<? extends Object> rows = null;
		List<List <Object>> tempDataList = null;
		try {

			client = new CloudantDBUtil().getCloudantContext();
			db = client.database(datastore, false);
			requestBuilder = db.getAllDocsRequestBuilder();
			request = requestBuilder.includeDocs(true).build();

			tempDataList = new ArrayList<List<Object>>();
			
			response = request.getResponse();

			rows = response.getDocsAs(Object.class);
			
			for(Object row : rows){
				
				
				JsonParser parser = new JsonParser(); 
			
				if(!row.toString().contains("_design/_search")){
					String formattedString = row.toString().replaceAll("=", "\"=\"").replaceAll(", ", "\",\"").replace("{", "{\"").replace("}", "\"}");
					JsonObject json = (JsonObject) parser.parse(formattedString);
					
					List<Object> cols = new ArrayList<Object>();
					
					for(String colString : colNames){
						if(json.has(colString)){
							JsonElement unspsc = json.get(colString);
							String val = unspsc.getAsString();
							//converting the booleans in the upper case
							if("true".equalsIgnoreCase(val) || "false".equalsIgnoreCase(val)){
								cols.add(val.toUpperCase());
							}else
								cols.add(val);
						}
					}
					
					//adding the cols
					tempDataList.add(cols);
				}
				
			}

		} catch (NoDocumentException ex) {
			throw new ServiceException(ex.getMessage());

		} catch (IOException e) {
			throw new ServiceException(e.getMessage());
		}finally{
			if(client!=null)
				client.shutdown();
		}

		return tempDataList;
	}
	
	/**
	 * @param datastore
	 * @param colNames
	 * @return
	 * @throws ServiceException
	 */
	public  List<List<Map<Object, Object>>> getAllRecordsForMerge(String datastore,List<String> colNames) throws ServiceException {
		CloudantClient client = null;
		Database db = null;
		AllDocsRequestBuilder requestBuilder = null;
		AllDocsRequest request = null;
		AllDocsResponse response = null;
		List<? extends Object> rows = null;
		List<List <Map<Object, Object>>> tempDataList = null;
		try {

			client = new CloudantDBUtil().getCloudantContext();
			db = client.database(datastore, false);
			requestBuilder = db.getAllDocsRequestBuilder();
			request = requestBuilder.includeDocs(true).build();

			tempDataList = new ArrayList<List<Map<Object,Object>>>();
			
			response = request.getResponse();

			rows = response.getDocsAs(Object.class);
			
			for(Object row : rows){
				
				JsonParser parser = new JsonParser(); 
			
				if(!row.toString().contains("_design/_search")){
					String formattedString = row.toString().replaceAll("=", "\"=\"").replaceAll(", ", "\",\"").replace("{", "{\"").replace("}", "\"}");
					JsonObject json = (JsonObject) parser.parse(formattedString);
					
					List<Map<Object, Object>> cols = new ArrayList<Map<Object,Object>>();
					
					for(String colString : colNames){
						if(json.has(colString)){
							Map<Object, Object> obj = new LinkedHashMap<Object, Object>();
							JsonElement unspsc = json.get(colString);
							String val = unspsc.getAsString();
							//converting the booleans in the upper case
							if("true".equalsIgnoreCase(val) || "false".equalsIgnoreCase(val)){
								obj.put(colString, val);
								cols.add(obj);
							}else{
								obj.put(colString, val);
								cols.add(obj);
							}
								
						}
					}
					
					//adding the cols
					tempDataList.add(cols);
				}
				
			}

		} catch (NoDocumentException ex) {
			throw new ServiceException(ex.getMessage());

		} catch (IOException e) {
			throw new ServiceException(e.getMessage());
		}finally{
			if(client!=null)
				client.shutdown();
		}

		return tempDataList;
	}

	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sp.dao.BaseDAO#getAllRecords(java.lang.String)
	 */
	public List<? extends Object> getAllRecords(String datastore) throws ServiceException {
		CloudantClient client = null;
		Database db = null;
		AllDocsRequestBuilder requestBuilder = null;
		AllDocsRequest request = null;
		AllDocsResponse response = null;
		List<? extends Object> rows = null;
		try {

			client = new CloudantDBUtil().getCloudantContext();
			db = client.database(datastore, false);
			requestBuilder = db.getAllDocsRequestBuilder();
			request = requestBuilder.includeDocs(true).build();

			response = request.getResponse();

			rows = response.getDocsAs(getPojoClass());

		} catch (NoDocumentException ex) {
			throw new ServiceException(ex.getMessage());

		} catch (IOException e) {
			throw new ServiceException(e.getMessage());
		}finally{
			if(client!=null)
				client.shutdown();
		}

		return rows;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sp.dao.BaseDAO#getMaxWithParam(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	public BigDecimal getMaxWithParam(String datastore, String viewName, String viewIndex, Object[] params)
			throws ServiceException {
		CloudantClient client = null;
		Database db = null;
		ViewRequestBuilder requestBuilder = null;
		ViewRequest<ComplexKey, JsonObject> request = null;
		ViewResponse<ComplexKey, JsonObject> response = null;
		List<Row<ComplexKey, JsonObject>> rows = null;
		JsonObject jsonObject = null;
		BigDecimal max = null;
		Key.ComplexKey keys = null;
		try {

			client = new CloudantDBUtil().getCloudantContext();
			db = client.database(datastore, false);

			requestBuilder = db.getViewRequestBuilder(viewName, viewIndex);

			// set the complex keys
			for (Object keyObj : params) {
				if (keyObj instanceof String) {
					if (keys == null)
						keys = Key.complex(String.valueOf(keyObj));
					else
						keys.add(String.valueOf(keyObj));
				} else if (keyObj instanceof Integer) {
					if (keys == null)
						keys = Key.complex(Integer.parseInt(String.valueOf(keyObj)));
					else
						keys.add(Integer.parseInt(String.valueOf(keyObj)));
				} else if (keyObj instanceof Boolean) {
					if (keys == null)
						keys = Key.complex(Boolean.valueOf(String.valueOf(keyObj)));
					else
						keys.add(Boolean.valueOf(String.valueOf(keyObj)));
				} else if (keyObj instanceof BigDecimal) {
					if (keys == null)
						keys = Key.complex(new BigDecimal(String.valueOf(keyObj)));
					else
						keys.add(new BigDecimal(String.valueOf(keyObj)));
				}

			}
			request = requestBuilder.newRequest(Key.Type.COMPLEX, JsonObject.class).reduce(true).keys(keys).build();

			response = request.getResponse();

			rows = response.getRows();

			for (Row<ComplexKey, JsonObject> resultRow : rows) {

				jsonObject = resultRow.getValue();
				max = new BigDecimal(String.valueOf(jsonObject.get(BackendConstants.CLOUDANT_MAX_TYPE)));

			}

		} catch (NoDocumentException ex) {
			throw new ServiceException(ex.getMessage());

		} catch (IOException e) {
			throw new ServiceException(e.getMessage());
		}

		return max;
	}

	
	/**
	 * @return the rawDataLIst
	 */
	public JsonArray getRawDataLIst() {
		return jsonArray;
	}

	/**
	 * @param rawDataLIst the rawDataLIst to set
	 */
	public void setRawDataLIst(JsonArray jsonArray) {
		this.jsonArray = jsonArray;
	}

	/**
	 * @return the dataList
	 */
	public List<? extends Object> getDataList() {
		return dataList;
	}

	/**
	 * @param dataList the dataList to set
	 */
	public void setDataList(List<Object> dataList) {
		this.dataList = dataList;
	}

	public BaseDAOImpl(){
		
	}

	

}
