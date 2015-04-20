package controllers;

import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.persistence.PersistenceException;

import models.ClimateService;
import models.ClimateServiceRepository;
//import models.DatasetLog;
import models.DatasetLogRepository;
import models.Parameter;
import models.ParameterRepository;
import models.ServiceConfiguration;
import models.ServiceConfigurationItem;
import models.ServiceConfigurationItemRepository;
import models.ServiceConfigurationRepository;
import models.ServiceExecutionLog;
import models.ServiceExecutionLogRepository;
import models.User;
import models.UserRepository;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

/**
 * The main set of web services.
 */
@Named
@Singleton
public class ServiceExecutionLogController extends Controller {
	public static final String WILDCARD = "%";

	private final ServiceExecutionLogRepository serviceExecutionLogRepository;
	private final UserRepository userRepository;
	private final ClimateServiceRepository climateServiceRepository;
	private final ParameterRepository parameterRepository;
	private final ServiceConfigurationItemRepository serviceConfigurationItemRepository;
	//private final DatasetLogRepository datasetLogRepository;
	private final ServiceConfigurationRepository serviceConfigurationRepository;

	// We are using constructor injection to receive a repository to support our desire for immutability.
	@Inject
	public ServiceExecutionLogController(
			ServiceExecutionLogRepository serviceExecutionLogRepository,
			ParameterRepository parameterRepository,
			ServiceConfigurationItemRepository serviceConfigurationItemRepository,
			UserRepository userRepository,
			ClimateServiceRepository climateServiceRepository,
			DatasetLogRepository datasetLogRepository,
			ServiceConfigurationRepository serviceConfigurationRepository) {
		this.parameterRepository = parameterRepository;
		this.serviceExecutionLogRepository = serviceExecutionLogRepository;
		this.userRepository = userRepository;
		this.serviceConfigurationItemRepository = serviceConfigurationItemRepository;
		this.climateServiceRepository = climateServiceRepository;
		//this.datasetLogRepository = datasetLogRepository;
		this.serviceConfigurationRepository = serviceConfigurationRepository;
	}

	public Result queryServiceExecutionLogs() {
		JsonNode json = request().body().asJson();
		if (json == null) {
			System.out.println("ServiceExecutionLog cannot be queried, expecting Json data");
			return badRequest("ServiceExecutionLog cannot be queried, expecting Json data");
		}
		String result = new String();

		try {
			//Parse JSON file
			Long userId = json.findPath("userId").asLong();
			//long datasetLogId = json.findPath("datasetLogId").asLong();
			String purpose = json.findPath("purpose").asText();
			if (purpose.isEmpty()) {
				purpose = WILDCARD;
			}
			else {
				purpose = WILDCARD+purpose+WILDCARD;
			}

			Date start = new Date(0);
			Date end = new Date();
			long executionStartTimeNumber = json.findPath("executionStartTime").asLong();
			long executionEndTimeNumber = json.findPath("executionEndTime").asLong();

			if (executionStartTimeNumber > 0) {
				start = new Date(executionStartTimeNumber);
			}
			if (executionEndTimeNumber > 0) {
				end = new Date(executionEndTimeNumber);
			}

//          If we change the date format later, we can modify here.
//
//    		String executionStartTimeString = json.findPath("executionStartTime").asText();
//    		String executionEndTimeString = json.findPath("executionEndTime").asText();
//	    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(util.Common.DATE_PATTERN);
//
//	    	try {
//	    		executionStartTime = simpleDateFormat.parse(executionStartTimeString);
//	    	} catch (ParseException e) {
//	    		// TODO Auto-generated catch block
//	    		e.printStackTrace();
//	    		System.out.println("Wrong Date Format :" + executionStartTimeString);
//	    		return badRequest("Wrong Date Format :" + executionStartTimeString);
//	    	}
//	    	try {
//	    		executionEndTime = simpleDateFormat.parse(executionEndTimeString);
//	    	} catch (ParseException e) {
//	    		// TODO Auto-generated catch block
//	    		e.printStackTrace();
//	    		System.out.println("Wrong Date Format :" + executionEndTimeString);
//	    		return badRequest("Wrong Date Format :" + executionEndTimeString);
//	    	}

			JsonNode parameters = json.findPath("parameters");
			Iterator<String> iterator = parameters.fieldNames();
			List<ServiceExecutionLog> logs;
			if (!iterator.hasNext()) {
				if (userId != 0) {
					logs = serviceExecutionLogRepository.findByExecutionStartTimeGreaterThanEqualAndExecutionEndTimeLessThanEqualAndPurposeLikeAndUser_Id(start, end, purpose, userId);
				} else {
					logs = serviceExecutionLogRepository.findByExecutionStartTimeGreaterThanEqualAndExecutionEndTimeLessThanEqualAndPurposeLike(start, end, purpose);
				}
			} else {
				Set<ServiceConfiguration> configurationsSet = null;
				while (iterator.hasNext()) {
					String parameterName = iterator.next();
					String value = parameters.findPath(parameterName).asText();
					if (value != null && !value.isEmpty()) {
						List<Parameter> parameterList = parameterRepository.findByName(parameterName);
						//Find the serviceConfigurationItems that match the parameters
						//If parameter is not ranged
						List<ServiceConfigurationItem> serviceConfigurationItem = serviceConfigurationItemRepository.findByParameterInAndValue(parameterList, value);
						Set<ServiceConfiguration> tempConfigSet = new HashSet<ServiceConfiguration>();

						for (ServiceConfigurationItem items : serviceConfigurationItem) {
							tempConfigSet.add(items.getServiceConfiguration());
						}

						configurationsSet = intersectServiceConfiguration(configurationsSet, tempConfigSet);
					}
				}

//              DatasetLog datasetLog = datasetLogRepository.findOne(datasetLogId);

				if (configurationsSet == null || configurationsSet.isEmpty()) {
					// If no parameter matches, just return the empty set
					logs = new ArrayList<ServiceExecutionLog>();
				} else {
					if (userId != 0) {
						logs = serviceExecutionLogRepository.findByExecutionStartTimeGreaterThanEqualAndExecutionEndTimeLessThanEqualAndPurposeLikeAndUser_IdAndServiceConfigurationIn(start, end, purpose, userId, configurationsSet);
					} else {
						logs = serviceExecutionLogRepository.findByExecutionStartTimeGreaterThanEqualAndExecutionEndTimeLessThanEqualAndPurposeLikeAndServiceConfigurationIn(start, end, purpose, configurationsSet);
					}
				}
			}
			result = new Gson().toJson(logs);
		} catch (Exception e) {
			System.out.println("ServiceExecutionLog cannot be queried, query is corrupt");
			return badRequest("ServiceExecutionLog cannot be queried, query is corrupt");
		}

		return ok(result);
	}

	private Set<ServiceConfiguration> intersectServiceConfiguration(Set<ServiceConfiguration> configurationsSet, Set<ServiceConfiguration> tempConfigSet) {
		if (configurationsSet == null) {
			configurationsSet = tempConfigSet;
		} else {
			configurationsSet.retainAll(tempConfigSet);
		}
		return configurationsSet;
	}

	public Result addServiceExecutionLog() {
		JsonNode json = request().body().asJson();
		if (json == null) {
			System.out.println("ServiceExecutionLog not saved, expecting Json data");
			return badRequest("ServiceExecutionLog not saved, expecting Json data");
		}

		//Parse JSON file
		long serviceId = json.findPath("serviceId").asLong();
		long userId = json.findPath("userId").asLong();
		//long datasetLogId = json.findPath("datasetLogId").asLong();
		String purpose = json.findPath("purpose").asText();
		String plotUrl = json.findPath("url").asText();
		String dataUrl = json.findPath("dataUrl").asText();
//    	String executionStartTimeString = json.findPath("executionStartTime").asText();
//    	String executionEndTimeString = json.findPath("executionEndTime").asText();
		long executionStartTimeNumber = json.findPath("executionStartTime").asLong();
		long executionEndTimeNumber = json.findPath("executionEndTime").asLong();
		Date executionStartTime = new Date(executionStartTimeNumber);
		Date executionEndTime = new Date(executionEndTimeNumber);

//		If we change the date format later, we can modify here.
//
//    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(util.Common.DATE_PATTERN);
//
//    	try {
//    		executionStartTime = simpleDateFormat.parse(executionStartTimeString);
//    	} catch (ParseException e) {
//    		// TODO Auto-generated catch block
//    		e.printStackTrace();
//    		System.out.println("Wrong Date Format :" + executionStartTimeString);
//    		return badRequest("Wrong Date Format :" + executionStartTimeString);
//    	}
//    	try {
//    		executionEndTime = simpleDateFormat.parse(executionEndTimeString);
//    	} catch (ParseException e) {
//    		// TODO Auto-generated catch block
//    		e.printStackTrace();
//    		System.out.println("Wrong Date Format :" + executionEndTimeString);
//    		return badRequest("Wrong Date Format :" + executionEndTimeString);
//    	}

		try {
			User user = userRepository.findOne(userId);
			ClimateService climateService = climateServiceRepository.findOne(serviceId);
			long difference = executionEndTime.getTime() - executionStartTime.getTime();
			ServiceConfiguration serviceConfiguration = new ServiceConfiguration(climateService, user, difference+"ms");
			ServiceExecutionLog serviceExecutionLog = new ServiceExecutionLog(climateService, user, serviceConfiguration, purpose, executionStartTime, executionEndTime, dataUrl, plotUrl);
			ServiceExecutionLog savedServiceExecutionLog = serviceExecutionLogRepository.save(serviceExecutionLog);
			ServiceConfiguration savedServiceConfiguration = savedServiceExecutionLog.getServiceConfiguration();
			JsonNode parameters = json.findPath("parameters");
			Iterator<String> iterator = parameters.fieldNames();
			while (iterator.hasNext()) {
				String fieldName = iterator.next();
				String value = parameters.findPath(fieldName).asText();
				Parameter parameter = parameterRepository.findByNameAndClimateService(fieldName, climateService);
				ServiceConfigurationItem serviceConfigurationItem = new ServiceConfigurationItem(savedServiceConfiguration, parameter, value);
				ServiceConfigurationItem savedServiceConfigurationItem = serviceConfigurationItemRepository.save(serviceConfigurationItem);
				System.out.println("ServiceConfigurationItem saved: " + savedServiceConfigurationItem.getId());
			}

			System.out.println("ServiceExecutionLog saved: " + savedServiceExecutionLog.getId());
			return created(new Gson().toJson(savedServiceExecutionLog.getId()));
		} catch (PersistenceException pe) {
			pe.printStackTrace();
			System.out.println(pe.getClass().toString());
			System.out.println("ServiceExecutionLog not saved");
			return badRequest("ServiceExecutionLog not saved");
		}
	}


	public Result deleteServiceExecutionLog(Long id) {
		ServiceExecutionLog serviceExecutionLog = serviceExecutionLogRepository
				.findOne(id);
		if (serviceExecutionLog == null) {
			System.out.println("ServiceExecutionLog not found with id: " + id);
			return notFound("ServiceExecutionLog not found with id: " + id);
		}

		for (ServiceConfigurationItem items: serviceConfigurationItemRepository.findByServiceConfiguration_Id(serviceExecutionLog.getServiceConfiguration().getId()))
		{
			serviceConfigurationItemRepository.delete(items.getId());
		}
		serviceExecutionLogRepository.delete(serviceExecutionLog);
		System.out.println("ServiceExecutionLog is deleted: " + id);
		return ok("ServiceExecutionLog is deleted: " + id);
	}

	public Result updateServiceExecutionLog(long id) {
		JsonNode json = request().body().asJson();
		if (json == null) {
			System.out.println("ServiceExecutionLog not saved, expecting Json data");
			return badRequest("ServiceExecutionLog not saved, expecting Json data");
		}

		//Parse JSON file
		long serviceId = json.findPath("serviceId").asLong();
		long userId = json.findPath("userId").asLong();
		String purpose = json.findPath("purpose").asText();
		String plotUrl = json.findPath("url").asText();
		String dataUrl = json.findPath("dataUrl").asText();
		long executionStartTimeNumber = json.findPath("executionStartTime").asLong();
		long executionEndTimeNumber = json.findPath("executionEndTime").asLong();
		Date executionStartTime = new Date(executionStartTimeNumber);
		Date executionEndTime = new Date(executionEndTimeNumber);

		try {
			User user = userRepository.findOne(userId);
			ClimateService climateService = climateServiceRepository
					.findOne(serviceId);
			ServiceExecutionLog serviceExecutionLog = serviceExecutionLogRepository
					.findOne(id);
			ServiceConfiguration serviceConfiguration = serviceExecutionLog.getServiceConfiguration();
			serviceExecutionLog.setClimateService(climateService);
			serviceExecutionLog.setDataUrl(dataUrl);
			serviceExecutionLog.setPlotUrl(plotUrl);
			serviceExecutionLog.setExecutionEndTime(executionEndTime);
			serviceExecutionLog.setExecutionStartTime(executionStartTime);
			serviceExecutionLog.setPurpose(purpose);
			serviceExecutionLog.setUser(user);
			serviceExecutionLog.setServiceConfiguration(serviceConfiguration);
			JsonNode parameters = json.findPath("parameters");
			Iterator<String> iterator = parameters.fieldNames();
			while (iterator.hasNext()) {
				String fieldName = iterator.next();
				String value = parameters.findPath(fieldName).asText();
				Parameter parameter = parameterRepository.findByNameAndClimateService(fieldName, climateService);
				ServiceConfigurationItem serviceConfigurationItem = serviceConfigurationItemRepository.findFirstByParameterAndServiceConfiguration(parameter, serviceConfiguration);
				serviceConfigurationItem.setValue(value);
				//if NULL?
				ServiceConfigurationItem savedServiceConfigurationItem = serviceConfigurationItemRepository.save(serviceConfigurationItem);
				System.out.println("ServiceConfigurationItem saved: " + savedServiceConfigurationItem.getId());
			}

			ServiceExecutionLog savedServiceExecutionLog = serviceExecutionLogRepository
					.save(serviceExecutionLog);

			System.out.println("ServiceExecutionLog updated: "
					+ savedServiceExecutionLog.getId());
			return created("ServiceExecutionLog updated: "
					+ savedServiceExecutionLog.getId());
		} catch (PersistenceException pe) {
			pe.printStackTrace();
			System.out.println("ServiceExecutionLog not updated: " + id);
			return badRequest("ServiceExecutionLog not updated: " + id);
		}
	}

	public Result getServiceExecutionLog(Long id, String format) {
		if (id < 0) {
			System.out.println("id is negative!");
			return badRequest("id is negative!");
		}

		ServiceExecutionLog ServiceExecutionLog = serviceExecutionLogRepository
				.findOne(id);
		if (ServiceExecutionLog == null) {
			System.out.println("ServiceExecutionLog not found with id: " + id);
			return notFound("ServiceExecutionLog not found with id: " + id);
		}

		String result = new String();
		if (format.equals("json")) {
			result = new Gson().toJson(ServiceExecutionLog);
		}

		return ok(result);
	}

	public Result getAllServiceExecutionLogs(String format) {
		String result = new String();

		if (format.equals("json")) {
			result = new Gson().toJson(serviceExecutionLogRepository.findAll());
		}

		return ok(result);
	}

	public Result replaceUserWithPurpose() {
		// Get all execution logs with userid = 1
		List<ServiceExecutionLog> executionLogs = serviceExecutionLogRepository.findByUser_Id(1);

		if(executionLogs == null || executionLogs.isEmpty()){
			System.out.println("No logs need to be updated");
			return notFound("No logs need to be updated");
		}

		// For each log, set its user to a new user with new name
		for(ServiceExecutionLog log: executionLogs){
			String userName = "";
			String purpose = log.getPurpose().trim();
			if(purpose.startsWith("CCS student")) {
				System.out.println(purpose);
				// Temporarily
				userName = purpose.substring(0, 11)+" ";
				for(int i = 12; i < purpose.length(); i++)
					if(Character.isDigit(purpose.charAt(i))){
						userName += purpose.charAt(i);
					}
					else break;
				User newUser = new User(userName,"", "", "", "", "", "", "", "", "", "");
				userRepository.save(newUser);
				log.setUser(newUser);
				serviceExecutionLogRepository.save(log);
			}
		}

		String result = new Gson().toJson(serviceExecutionLogRepository.findAll());


		return ok(result);
	}

}
