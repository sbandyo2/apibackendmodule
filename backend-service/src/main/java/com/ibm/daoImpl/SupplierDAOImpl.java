package com.ibm.daoImpl;

import java.io.InputStream;
import java.util.List;

import org.json.JSONObject;
import org.json.simple.JSONArray;

import com.ibm.consants.BackendConstants;
import com.ibm.dao.SupplierDAO;
import com.ibm.exception.ServiceException;
import com.ibm.model.SupplierPartneringVO;
import com.ibm.utils.ServiceUtils;

public class SupplierDAOImpl extends BaseDAOImpl implements SupplierDAO {

	@Override
	public JSONObject getSupplierPartnering(String params) throws ServiceException {
		List<SupplierPartneringVO> partneringVOs = null;
		JSONObject jsonObject = null;
		
		getSearchResults(BackendConstants.SUPPLIER_INFO_DATASTORE, params);
		partneringVOs = (List<SupplierPartneringVO>) getDataList();
		
		jsonObject = new JSONObject();
		for(SupplierPartneringVO partneringVO : partneringVOs){
			jsonObject.put( partneringVO.getLocationID(), partneringVO.getVendorID());
		}
		return jsonObject;
	}

	@Override
	public Class<? extends Object> getPojoClass() {
		
		return SupplierPartneringVO.class;
	}
	
	@Override
	public JSONArray getSuppPartnerInfo(String param)throws ServiceException {
		JSONArray array = null;
		List<SupplierPartneringVO> list = null;
		JSONObject jsonObject = null;
		
		if(!ServiceUtils.isNullOrEmpty(param)){
			getSearchResultsWithOutPojo(BackendConstants.SUPPLIER_INFO_DATASTORE,param);
			array = getRawDataLIst();	
		}else{
			list =  (List<SupplierPartneringVO>) getAllRecords(BackendConstants.SUPPLIER_INFO_DATASTORE);
			
			if(list!=null && !list.isEmpty()){
				array = new JSONArray();
				for(SupplierPartneringVO partneringVO : list){
					jsonObject = new JSONObject();
					jsonObject.put("VendorID", partneringVO.getVendorID());
					jsonObject.put("LocationID", partneringVO.getLocationID());
					jsonObject.put("Name", partneringVO.getName());
					jsonObject.put("City", partneringVO.getCity());
					jsonObject.put("Street", partneringVO.getStreet());
					jsonObject.put("PostalCode", partneringVO.getPostalCode());
					jsonObject.put("Region", partneringVO.getRegion());
					jsonObject.put("Country", partneringVO.getCountry());
					jsonObject.put("Phone", partneringVO.getPhone());
					jsonObject.put("Fax", partneringVO.getFax());
					jsonObject.put("EmailAddress", partneringVO.getEmailAddress());
					
					array.add(jsonObject);
				}
			}
		}
		
		
		
		
		return array;
	}
	
	@Override
	public JSONArray getBatchRecordsAsJson(String param)throws ServiceException {
		JSONArray array = null;
		
		getSearchResultsWithOutPojo(BackendConstants.BATCH_TRACKER_DATASTORE,param);
		array = getRawDataLIst();
		
		
		
		return array;
	}

	@Override
	public String getAribaSupplierId(String suppId) throws ServiceException {
		List<SupplierPartneringVO> partneringVOs = null;
		String params = null;
		
		params = "LocationID:'"+suppId+"'";
		getSearchResults(BackendConstants.SUPPLIER_INFO_DATASTORE, params);
		partneringVOs = (List<SupplierPartneringVO>) getDataList();
		
		if(partneringVOs!= null && !partneringVOs.isEmpty()){
			return partneringVOs.get(0).getVendorID();
		}else
			return "";
	}
	
	@Override
	public InputStream getSuppAttachmentForDownload(String fileName)
			throws ServiceException {
		return getAttachment(BackendConstants.BATCH_FILES_DATASTORE, fileName,BackendConstants.CSV);
	}
	
	public static void main(String[] args) {
		try {
			System.out.println(new SupplierDAOImpl().getSuppPartnerInfo(null));
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
