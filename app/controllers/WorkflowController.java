package controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.persistence.PersistenceException;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

import models.ClimateService;
import models.ClimateServiceRepository;
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
import models.Workflow;
import models.WorkflowRepository;
import play.mvc.*;
import workflow.VisTrailJson;

@Named
@Singleton
public class WorkflowController extends Controller {
	public static final String WILDCARD = "%";
	
	private final ClimateServiceRepository climateServiceRepository;
	private final UserRepository userRepository;
	private final WorkflowRepository workflowRepository;
	private final ParameterRepository parameterRepository;
	private final ServiceExecutionLogRepository serviceExecutionLogRepository;
	private final ServiceConfigurationItemRepository serviceConfigurationItemRepository;
	private final DatasetLogRepository datasetLogRepository;
	private final ServiceConfigurationRepository serviceConfigurationRepository;
	
	@Inject
	public WorkflowController(ClimateServiceRepository climateServiceRepository, UserRepository userRepository, WorkflowRepository workflowRepository, ParameterRepository parameterRepository, ServiceExecutionLogRepository serviceExecutionLogRepository, ServiceConfigurationItemRepository serviceConfigurationItemRepository, DatasetLogRepository datasetLogRepository, ServiceConfigurationRepository serviceConfigurationRepository) {
		this.climateServiceRepository = climateServiceRepository;
		this.userRepository = userRepository;
		this.workflowRepository = workflowRepository;
		this.parameterRepository = parameterRepository;
		this.serviceExecutionLogRepository = serviceExecutionLogRepository;
		this.serviceConfigurationItemRepository = serviceConfigurationItemRepository;
		this.datasetLogRepository = datasetLogRepository;
		this.serviceConfigurationRepository = serviceConfigurationRepository;
	}
	
	public Result addWorkflow() {
		JsonNode json = request().body().asJson();
    	if (json == null) {
    		System.out.println("Dataset not saved, expecting Json data");
			return badRequest("Dataset not saved, expecting Json data");
    	}
    	String name = json.findPath("name").asText();
    	String purpose = json.findPath("purpose").asText();
    	long createTimeNumber = json.findPath("createTime").asLong();
		Date createTime = new Date(createTimeNumber);
    	String versionNo = json.findPath("versionNo").asText();
    	long rootWorkflowId = json.findPath("rootWorkflowId").asLong();
    	
    	JsonNode users = json.findPath("userId");
    	List<User> userSet = new ArrayList<User>();
    	for (int i = 0; i < users.size(); i ++) {
    		userSet.add(userRepository.findOne(users.get(i).asLong()));
    	}
    	JsonNode climateServices = json.findPath("climateServiceId");
    	List<ClimateService> climateServiceSet = new ArrayList<ClimateService>();
    	for (int i = 0; i < climateServices.size(); i ++) {
    		climateServiceSet.add(climateServiceRepository.findOne(climateServices.get(i).asLong()));
    	}
    	try {
	    	Workflow workflow = new Workflow(name, purpose, createTime, versionNo, rootWorkflowId, userSet, climateServiceSet);
	    	Workflow savedWorkflow = workflowRepository.save(workflow);
	    	System.out.println("Workflow saved: "+ savedWorkflow.getId());
			return created(new Gson().toJson("Workflow saved: "+ savedWorkflow.getId()));
    	} catch (PersistenceException pe) {
			pe.printStackTrace();
			System.out.println("Workflow not created");
			return badRequest("Workflow not created");
		}
    	
	}
	
    public Result updateWorkflowById(long id) {
    	if (id < 0) {
    		System.out.println("id is negative!");
			return badRequest("id is negative!");
    	}
		JsonNode json = request().body().asJson();
		if (json == null) {
			System.out.println("Workflow not saved, expecting Json data");
			return badRequest("Workflow not saved, expecting Json data");
		}
		String name = json.findPath("name").asText();
    	String purpose = json.findPath("purpose").asText();
    	long createTimeNumber = json.findPath("createTime").asLong();
		Date createTime = new Date(createTimeNumber);
    	String versionNo = json.findPath("versionNo").asText();
    	long rootWorkflowId = json.findPath("rootWorkflowId").asLong();
    	
    	JsonNode users = json.findPath("userId");
    	List<User> userSet = new ArrayList<User>();
    	for (int i = 0; i < users.size(); i ++) {
    		userSet.add(userRepository.findOne(users.get(i).asLong()));
    	}
    	JsonNode climateServices = json.findPath("climateServiceId");
    	List<ClimateService> climateServiceSet = new ArrayList<ClimateService>();
    	for (int i = 0; i < climateServices.size(); i ++) {
    		climateServiceSet.add(climateServiceRepository.findOne(climateServices.get(i).asLong()));
    	}
	
		try {
			Workflow workflow = workflowRepository.findOne(id);
			
			workflow.setClimateServiceSet(climateServiceSet);
			workflow.setCreateTime(createTime);
			workflow.setName(name);
			workflow.setPurpose(purpose);
			workflow.setRootWorkflowId(rootWorkflowId);
			workflow.setUserSet(userSet);
			workflow.setVersionNo(versionNo);
			
			Workflow savedWorkflow = workflowRepository.save(workflow);
			
			System.out.println("Workflow updated: "+ savedWorkflow.getId());
			return created("Workflow updated: "+ savedWorkflow.getId());
		} catch (PersistenceException pe) {
			pe.printStackTrace();
			System.out.println("Workflow not saved: "+id);
			return badRequest("Workflow not saved: "+id);
		}			
    }

	
    public Result deleteWorkflowById(long id) {
    	if (id < 0) {
    		System.out.println("id is negative!");
			return badRequest("id is negative!");
    	}
    	Workflow workflow = workflowRepository.findOne(id);
    	if (workflow == null) {
    		System.out.println("Workflow not found with id: " + id);
			return notFound("Workflow not found with id: " + id);
    	}
    	workflowRepository.delete(workflow);
    	System.out.println("Workflow is deleted: " + id);
		return ok("Workflow is deleted: " + id);
    }
    
    public Result getWorkflowById(long id, String format) {
    	if (id < 0) {
    		System.out.println("id is negative!");
			return badRequest("id is negative!");
    	}
    	Workflow workflow = workflowRepository.findOne(id);
    	if (workflow == null) {
    		System.out.println("Workflow not found with name: " + id);
			return notFound("Workflow not found with name: " + id);
    	}
    	
    	String result = new String();
    	if (format.equals("json")) {
    		result = new Gson().toJson(workflow);
    	}
    	
    	return ok(result);
    }

    
    public Result getAllWorkflows(String format) {
    	try {
    		Iterable<Workflow> workflows =  workflowRepository.findAll();
    		String result = new String();
    		result = new Gson().toJson(workflows);
    		return ok(result);
    	} catch (Exception e) {
    		return badRequest("Workflows not found");
    	}
    }
	
    public Result generateWorkflowJson() {
    	List<ServiceExecutionLog> list = queryServiceExecutionLogsAsList();
    	String result = VisTrailJson.getVisTrailJson(list);
    	String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
    	String location = "workflowRepository/" + timeStamp+".log";
    	
    	File theDir = new File("/workflowRepository");

    	// if the directory does not exist, create it
    	if (!theDir.exists()) {
    	    System.out.println("creating directory: " + workflowRepository);
    	    boolean create = false;

    	    try{
    	        theDir.mkdir();
    	        create = true;
    	    } 
    	    catch(SecurityException se){
    	        //handle it
    	    }        
    	    if(create) {    
    	        System.out.println("DIR created");  
    	    }
    	}
    	
    	try {
			File file = new File(location);
            BufferedWriter output = new BufferedWriter(new FileWriter(file));
            output.write(result);
            output.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return ok(result);
    }
    
    public List<ServiceExecutionLog> queryServiceExecutionLogsAsList() {
		JsonNode json = request().body().asJson();
		List<ServiceExecutionLog> logs = null;
		if (json == null) {
			System.out.println("ServiceExecutionLog cannot be queried, expecting Json data");
			return logs;
		}

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

			JsonNode parameters = json.findPath("parameters");
			Iterator<String> iterator = parameters.fieldNames();
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
		} catch (Exception e) {
			System.out.println("ServiceExecutionLog cannot be queried, query is corrupt");
			return logs;
		}

		return logs;
	}
    
    private Set<ServiceConfiguration> intersectServiceConfiguration(Set<ServiceConfiguration> configurationsSet, Set<ServiceConfiguration> tempConfigSet) {
		if (configurationsSet == null) {
			configurationsSet = tempConfigSet;
		} else {
			configurationsSet.retainAll(tempConfigSet);
		}
		return configurationsSet;
	}
}