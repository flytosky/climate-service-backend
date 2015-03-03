package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

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
            System.out
                    .println("ServiceExecutionLog cannot be queried, expecting Json data");
            return badRequest("ServiceExecutionLog cannot be queried, expecting Json data");
        }
        String result = new String();


        try {
            //Parse JSON file
            Long userId = json.findPath("userId").asLong();

            //long datasetLogId = json.findPath("datasetLogId").asLong();
            String purpose = json.findPath("purpose").asText();
            if (purpose.isEmpty())
                purpose = WILDCARD;

//    	String executionStartTimeString = json.findPath("executionStartTime").asText();
//    	String executionEndTimeString = json.findPath("executionEndTime").asText();

            Date start = new Date(0);
            Date end = new Date();  //


            if (json.has("executionStartTime")) {
                long executionStartTimeNumber = json.findPath("executionStartTime").asLong();
                start = new Date(executionStartTimeNumber);
            }
            if (json.has("executionEndTime")) {
                long executionEndTimeNumber = json.findPath("executionEndTime").asLong();
                end = new Date(executionEndTimeNumber);
            }

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


            JsonNode parameters = json.findPath("parameters");
            Iterator<String> iterator = parameters.fieldNames();
            List<ServiceExecutionLog> logs;
            if (!iterator.hasNext())
            {
                if (userId != 0)
                    logs = serviceExecutionLogRepository.findByExecutionStartTimeBetweenAndExecutionEndTimeBetweenAndPurposeLikeAndUser_Id(start, end, start, end, purpose, userId);
                else
                    logs = serviceExecutionLogRepository.findByExecutionStartTimeBetweenAndExecutionEndTimeBetweenAndPurposeLike(start, end, start, end, purpose);

            }
            else {

                List<ServiceConfiguration> configurationsList = null;

                while (iterator.hasNext()) {
                    String fieldName = iterator.next();
                    String value = parameters.findPath(fieldName).asText();
                    Parameter parameter = parameterRepository.findByName(fieldName);
                    //Find the serviceConfigurationItems that match the parameters
                    //If parameter is not ranged
                    List<ServiceConfigurationItem> serviceConfigurationItem = serviceConfigurationItemRepository.findByParameterAndValue(parameter, value);
                    List<ServiceConfiguration> tempConfigList = new ArrayList<ServiceConfiguration>();

                    for (ServiceConfigurationItem items : serviceConfigurationItem) {
                        tempConfigList.add(items.getServiceConfiguration());
                    }

                    configurationsList = intersectServiceConfiguration(configurationsList, tempConfigList);
                }

                //Find the serviceConfigurations that match the serviceConfigurationItems


//            configurationsList = intersectServiceConfiguration(configurationsList, userConfigList);
                //DatasetLog datasetLog = datasetLogRepository.findOne(datasetLogId);


                if (userId != 0)
                    logs = serviceExecutionLogRepository.findByExecutionStartTimeBetweenAndExecutionEndTimeBetweenAndPurposeLikeAndUser_IdAndServiceConfigurationIn(start, end, start, end, purpose, userId, configurationsList);
                else
                    logs = serviceExecutionLogRepository.findByExecutionStartTimeBetweenAndExecutionEndTimeBetweenAndPurposeLikeAndServiceConfigurationIn(start, end, start, end, purpose, configurationsList);
            }
//            List<ServiceExecutionLog> logs = serviceExecutionLogRepository.findByServiceConfigurationIn(configurationsList);
            result = new Gson().toJson(logs);
        } catch (Exception e) {
            System.out
                    .println("ServiceExecutionLog cannot be queried, query is corrupt");
            return badRequest("ServiceExecutionLog cannot be queried, query is corrupt");
        }

        return ok(result);
    }

    private List<ServiceConfiguration> intersectServiceConfiguration(List<ServiceConfiguration> configurationsList, List<ServiceConfiguration> tempConfigList) {
        if (configurationsList == null)
            configurationsList = tempConfigList;
        else {
            configurationsList.retainAll(tempConfigList);
        }
        return configurationsList;
    }

    private List<ServiceConfiguration> intersectConfigurations(List<ServiceConfiguration> configurationsList, List<ServiceConfiguration> tempConfigList) {
        configurationsList.retainAll(tempConfigList);
        return configurationsList;
    }

    public Result addServiceExecutionLog() {
        JsonNode json = request().body().asJson();
        if (json == null) {
            System.out
                    .println("ServiceExecutionLog not saved, expecting Json data");
            return badRequest("ServiceExecutionLog not saved, expecting Json data");
        }

        //Parse JSON file
        long serviceId = json.findPath("serviceId").asLong();
        long userId = json.findPath("userId").asLong();
        long serviceConfigurationId = json.findPath("serviceConfigurationId").asLong();
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
            ServiceConfiguration serviceConfiguration = new ServiceConfiguration(climateService, user, executionStartTime);
            ServiceConfiguration savedServiceConfiguration = serviceConfigurationRepository.save(serviceConfiguration);
            JsonNode parameters = json.findPath("parameters");
            Iterator<String> iterator = parameters.fieldNames();
            while (iterator.hasNext()) {
                String fieldName = iterator.next();
                String value = parameters.findPath(fieldName).asText();
                Parameter parameter = parameterRepository.findByName(fieldName);
                ServiceConfigurationItem serviceConfigurationItem = new ServiceConfigurationItem(savedServiceConfiguration, parameter, value);
                ServiceConfigurationItem savedServiceConfigurationItem = serviceConfigurationItemRepository.save(serviceConfigurationItem);
                System.out.println("ServiceConfigurationItem saved: " + savedServiceConfigurationItem.getId());
            }

            //DatasetLog datasetLog = datasetLogRepository.findOne(datasetLogId);
            //ServiceExecutionLog ServiceExecutionLog = new ServiceExecutionLog(climateService, user, serviceConfiguration, datasetLog, purpose, executionStartTime, executionEndTime);
            ServiceExecutionLog ServiceExecutionLog = new ServiceExecutionLog(climateService, user, serviceConfiguration, purpose, executionStartTime, executionEndTime, dataUrl, plotUrl);
            ServiceExecutionLog savedServiceExecutionLog = serviceExecutionLogRepository.save(ServiceExecutionLog);

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

        serviceExecutionLogRepository.delete(serviceExecutionLog);
        System.out.println("ServiceExecutionLog is deleted: " + id);
        return ok("ServiceExecutionLog is deleted: " + id);
    }

    public Result updateServiceExecutionLog(long id) {
        JsonNode json = request().body().asJson();
        if (json == null) {
            System.out
                    .println("ServiceExecutionLog not saved, expecting Json data");
            return badRequest("ServiceExecutionLog not saved, expecting Json data");
        }

        // Parse JSON file
        long serviceId = json.findPath("serviceId").asLong();
        long userId = json.findPath("userId").asLong();
        long serviceConfigurationId = json.findPath("serviceConfigurationId")
                .asLong();
        // long datasetLogId = json.findPath("datasetLogId").asLong();
        String purpose = json.findPath("purpose").asText();
        String executionStartTimeString = json.findPath("executionStartTime")
                .asText();
        String executionEndTimeString = json.findPath("executionEndTime")
                .asText();
        String plotUrl = json.findPath("url").asText();
        String dataUrl = json.findPath("dataUrl").asText();
        Date executionStartTime = new Date();
        Date executionEndTime = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                util.Common.DATE_PATTERN);

        try {
            executionStartTime = simpleDateFormat
                    .parse(executionStartTimeString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out
                    .println("Wrong Date Format :" + executionStartTimeString);
            return badRequest("Wrong Date Format :" + executionStartTimeString);
        }
        try {
            executionEndTime = simpleDateFormat.parse(executionEndTimeString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("Wrong Date Format :" + executionEndTimeString);
            return badRequest("Wrong Date Format :" + executionEndTimeString);
        }

        try {
            User user = userRepository.findOne(userId);
            ClimateService climateService = climateServiceRepository
                    .findOne(serviceId);
            ServiceConfiguration serviceConfiguration = serviceConfigurationRepository
                    .findOne(serviceConfigurationId);
            // DatasetLog datasetLog =
            // datasetLogRepository.findOne(datasetLogId);

            ServiceExecutionLog serviceExecutionLog = serviceExecutionLogRepository
                    .findOne(id);

            serviceExecutionLog.setClimateService(climateService);
            serviceExecutionLog.setDataUrl(dataUrl);
            serviceExecutionLog.setPlotUrl(plotUrl);
            // serviceExecutionLog.setDatasetLog(datasetLog);
            serviceExecutionLog.setExecutionEndTime(executionEndTime);
            serviceExecutionLog.setExecutionStartTime(executionStartTime);
            serviceExecutionLog.setPurpose(purpose);
            serviceExecutionLog.setUser(user);
            serviceExecutionLog.setServiceConfiguration(serviceConfiguration);

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

    public Result getServiceExecutionLogs(long userId, String startTime,
                                          String endTime, String format) {
        String result = new String();

        if (format.equals("json")) {

            Date startMonth;
            Date endMonth;


            try {
                SimpleDateFormat yearMonth = new SimpleDateFormat("YYYYMM");

                startMonth = yearMonth.parse(startTime);
                endMonth = yearMonth.parse(endTime);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return notFound("Input Date format not correct ");
            }

            List<ServiceExecutionLog> logs = serviceExecutionLogRepository.findByUser_Id(userId);

//			List<ServiceExecutionLog> logs1 = serviceExecutionLogRepository.findByExecutionStartTimeBetweenAndExecutionEndTimeBetween(startMonth, endMonth, startMonth, endMonth);

            result = new Gson().toJson(logs);
        }

        return ok(result);
    }

    public Result getServiceExecutionLogsMultifield(String purpose,
                                                    Long serviceId, String format) {
        String result = new String();

        if (format.equals("json")) {

            if (purpose.isEmpty())
                purpose = "%";
            String serviceIdStr;
            if (serviceId == null)
                serviceIdStr = "%";
            else
                serviceIdStr = Long.toString(serviceId);

            result = new Gson().toJson(serviceExecutionLogRepository.findAll());
        }

        return ok(result);
    }
}