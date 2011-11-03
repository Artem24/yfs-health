package com.varun.yfs.server.common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.criterion.Restrictions;

import com.varun.yfs.server.common.data.DataUtil;
import com.varun.yfs.server.models.ChapterName;
import com.varun.yfs.server.models.City;
import com.varun.yfs.server.models.Country;
import com.varun.yfs.server.models.Doctor;
import com.varun.yfs.server.models.Entities;
import com.varun.yfs.server.models.Locality;
import com.varun.yfs.server.models.ProcessType;
import com.varun.yfs.server.models.ReferralType;
import com.varun.yfs.server.models.State;
import com.varun.yfs.server.models.Town;
import com.varun.yfs.server.models.TypeOfLocation;
import com.varun.yfs.server.models.Users;
import com.varun.yfs.server.models.Village;
import com.varun.yfs.server.models.Volunteer;

public class HibernateUtil
{
	private static final SessionFactory sessionFactory;
	private static DozerBeanMapper mapper;
	private static final Logger log = Logger.getLogger(HibernateUtil.class);

	static
	{
		try
		{
			PropertyConfigurator.configure("log4j.properties");

			sessionFactory = new AnnotationConfiguration().configure().buildSessionFactory();
			log.debug("The application is booting...");

			mapper = new DozerBeanMapper();

			insertReferenceData();
			
			log.debug("The application has finished booting.The reference data insertion is complete.");

		} catch (Throwable ex)
		{
			log.error("Initial SessionFactory creation failed. No database connections available. " + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory()
	{
		return sessionFactory;
	}

	public static Mapper getDozerMapper()
	{
		return mapper;
	}

	public static void main(String[] args)
	{

		Session session = HibernateUtil.getSessionFactory().openSession();
		try
		{

		} catch (HibernateException e)
		{

		} finally
		{
			session.close();
		}
	}

	private static void insertReferenceData()
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();

		insertEntities(session);
		insertChapterNames(session);
		insertCountry(session);
		insertDoctor(session);
		insertProcessType(session);
		insertTypeOfLocation(session);
		insertVolunteer(session);
		insertUsers(session);
		
		insertReferralTypes(session);

		transaction.commit();
		session.close();
	}

	private static void insertReferralTypes(Session session)
	{
		session.save(new ReferralType("Paediatric"));
		session.save(new ReferralType("Paediatrician"));
		session.save(new ReferralType("Others"));
		session.save(new ReferralType("Eye"));
		session.save(new ReferralType("Gynec"));
		session.save(new ReferralType("Skin"));
		session.save(new ReferralType("ENT"));
		session.save(new ReferralType("Dental"));
		session.save(new ReferralType("Orthopaedic"));
	}

	private static void insertUsers(Session session)
	{
		Criteria criteria = session.createCriteria(Village.class);
		criteria.add(Restrictions.eq("deleted", "N"));
		List<Village> lstEntities = criteria.list();

		Users users = new Users("Rama", "pass");
		users.setVillages(lstEntities);
		session.save(users);

		session.save(new Users("Krishna", "pass"));
	}

	private static void insertVolunteer(Session session)
	{
		session.save(new Volunteer("Rama"));
		session.save(new Volunteer("Krishna"));
	}

	private static void insertTypeOfLocation(Session session)
	{
		session.save(new TypeOfLocation("School"));
		session.save(new TypeOfLocation("College"));
	}

	private static void insertProcessType(Session session)
	{
		session.save(new ProcessType("Normal"));
		session.save(new ProcessType("Standard"));
	}

	private static void insertDoctor(Session session)
	{
		session.save(new Doctor("Rama"));
		session.flush();
		session.save(new Doctor("Krishna"));
		session.flush();
	}

	private static void insertCountry(Session session)
	{
		Country country = new Country("India");
		session.save(country);
		session.flush();

		Set<State> lstStates = new HashSet<State>();

		State state1 = new State("Karnataka", country);
		lstStates.add(state1);

		Village village = new Village("Thayur");
		village.setState(state1);

		Town town = new Town("Kolar");
		town.setState(state1);

		City city = new City("B'lore");
		city.setState(state1);

		Locality locality = new Locality("Jayanagar");
		locality.setCity(city);

		State state2 = new State("Andhra Pradesh", country);
		lstStates.add(state2);

		session.save(state1);
		session.save(state2);
		session.flush();

		session.save(city);
		session.flush();

		session.save(locality);
		session.flush();

		session.save(village);
		session.flush();

		session.save(town);
		session.flush();

		state1.setCities(new HashSet<City>(Arrays.asList(city)));
		state1.setTowns(new HashSet<Town>(Arrays.asList(town)));
		state1.setVillages(new HashSet<Village>(Arrays.asList(village)));
		session.saveOrUpdate(state1);

		country.setStates(lstStates);
		session.saveOrUpdate(country);
		session.flush();
	}

	private static void insertChapterNames(Session session)
	{
		session.save(new ChapterName("B'lore"));
		session.save(new ChapterName("Mysore"));
	}

	private static void insertEntities(Session session)
	{
		List<String> subList = DataUtil.lstEntities.subList(1, DataUtil.lstEntities.size());
		for (String entityName : subList)
		{
			session.save(new Entities(entityName));
		}
	}
}