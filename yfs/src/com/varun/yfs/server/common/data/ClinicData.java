package com.varun.yfs.server.common.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.dozer.Mapper;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.varun.yfs.client.common.RpcStatusEnum;
import com.varun.yfs.dto.UserClinicPermissionsDTO;
import com.varun.yfs.dto.UserDTO;
import com.varun.yfs.dto.YesNoDTO;
import com.varun.yfs.server.common.HibernateUtil;
import com.varun.yfs.server.models.City;
import com.varun.yfs.server.models.Clinic;

public class ClinicData extends AbstractData
{
	private static final Logger LOGGER = Logger.getLogger(ClinicData.class);

	public ModelData getModel(UserDTO userDto)
	{
		ModelData modelData = new BaseModelData();

		List<ModelData> list = DataUtil.<ModelData> getModelList("Clinic");
		modelData.set("data", this.applyPermission(userDto, list));
		modelData.set("parentStoreCity", DataUtil.<ModelData> getModelList("City"));

		modelData.set("configIds", Arrays.asList("clinicName", "cityName"));
		modelData.set("configCols", Arrays.asList("Clinic Name", "City"));
		modelData.set("configType", Arrays.asList("Text", "combo"));
		return modelData;
	}

	public RpcStatusEnum saveModel(ModelData model)
	{
		RpcStatusEnum status = RpcStatusEnum.FAILURE;
		Session session = HibernateUtil.getSessionFactory().openSession();
		Mapper dozerMapper = HibernateUtil.getDozerMapper();
		Transaction transact = session.beginTransaction();
		try
		{
			List<City> lstCities = DataUtil.<City> getRawList("City");
			List<ModelData> lstModels = model.get("data");

			for (ModelData modelData : lstModels)
			{
				Clinic hibObject = dozerMapper.map(modelData, Clinic.class);

				String cityName = modelData.get("cityName").toString();
				hibObject.setCity(findParent(lstCities, new City(cityName)));

				if (hibObject.getId() <= 0) // new state - find the parent
				{
					hibObject.setName(modelData.get("clinicName").toString());
					session.save(hibObject);
				} else
				{
					session.saveOrUpdate(hibObject);
				}
			}
			transact.commit();
			session.flush();
			session.close();
			status = RpcStatusEnum.SUCCESS;
		} catch (HibernateException ex)
		{
			LOGGER.error("Encountered error saving the model." + ex.getMessage());
			status = RpcStatusEnum.FAILURE;
			if (session != null)
			{
				transact.rollback();
				session.close();
			}
		}
		return status;
	}

	protected List<ModelData> applyPermission(UserDTO userDto, List<ModelData> modelList)
	{
		List<ModelData> lstModels = new ArrayList<ModelData>();
		if (!userDto.isAdmin())
		{
			List<UserClinicPermissionsDTO> lstChapterPermissions = userDto.getClinicPermissions();
			for (UserClinicPermissionsDTO userClinicPermissionsDTO : lstChapterPermissions)
			{
				if (userClinicPermissionsDTO.getRead().equalsIgnoreCase(YesNoDTO.YES.toString()))
				{
					String entityName = userClinicPermissionsDTO.getClinicName();

					ModelData tempModel = new BaseModelData();
					tempModel.set("name", entityName);

					int idx = modelList.indexOf(tempModel);
					if (idx >= 0)
						lstModels.add(modelList.get(idx));
				}
			}
		} else
			lstModels = modelList;
		return lstModels;
	}

}
