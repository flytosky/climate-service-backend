package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.persistence.PersistenceException;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

import models.ClimateService;
import models.ClimateServiceRepository;
import models.User;
import models.UserRepository;
import models.Workflow;
import models.WorkflowRepository;
import play.mvc.*;

@Named
@Singleton
public class WorkflowController extends Controller {
	
	private final ClimateServiceRepository climateServiceRepository;
	private final UserRepository userRepository;
	private final WorkflowRepository workflowRepository;
	
	@Inject
	public WorkflowController(ClimateServiceRepository climateServiceRepository, UserRepository userRepository, WorkflowRepository workflowRepository) {
		this.climateServiceRepository = climateServiceRepository;
		this.userRepository = userRepository;
		this.workflowRepository = workflowRepository;
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
	
}