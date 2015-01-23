package controllers;

import models.ClimateService;
import models.ClimateServiceRepository;
import models.User;
import models.UserRepository;
import play.mvc.*;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.persistence.PersistenceException;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

/**
 * The main set of web services.
 */
@Named
@Singleton
public class ClimateServiceController extends Controller {

    private final ClimateServiceRepository climateServiceRepository;
    private final UserRepository userRepository;

    // We are using constructor injection to receive a repository to support our desire for immutability.
    @Inject
    public ClimateServiceController(final ClimateServiceRepository climateServiceRepository, 
    		UserRepository userRepository) {
        this.climateServiceRepository = climateServiceRepository;
        this.userRepository = userRepository;
    }
    
    public Result addClimateService() {
    	JsonNode json = request().body().asJson();
    	if (json == null) {
    		System.out.println("Climate service not saved, expecting Json data");
			return badRequest("Climate service not saved, expecting Json data");
    	}

    	//Parse JSON file
    	long rootServiceId = json.findPath("rootServiceId").asLong();
    	long creatorId = json.findPath("creatorId").asLong();
		String name = json.findPath("name").asText();
		String purpose = json.findPath("purpose").asText();
		String url = json.findPath("url").asText();
		String scenario = json.findPath("scenario").asText();
		String createTime = json.findPath("createTime").asText();
		String versionNo = json.findPath("versionNo").asText();
		
		try {
			User user = userRepository.findOne(creatorId);
			ClimateService climateService = new ClimateService(rootServiceId, user, name, 
	        		purpose, url, scenario, createTime,
	        		versionNo);
			ClimateService savedClimateService = climateServiceRepository.save(climateService);
			
			System.out.println("Climate Service saved: " + savedClimateService.getName());
			return created("Climate Service saved: " + savedClimateService.getName());
		} catch (PersistenceException pe) {
			pe.printStackTrace();
			System.out.println("Climate Service not saved: " + name);
			return badRequest("Climate Service not saved: " + name);
		}			
    }
    
    public Result deleteClimateService(String name) {
    	ClimateService climateService = climateServiceRepository.findByName(name);
    	if (climateService == null) {
    		System.out.println("Climate service not found with name: " + name);
			return notFound("Climate service not found with name: " + name);
    	}
    	
    	climateServiceRepository.delete(climateService);
    	System.out.println("Climate service is deleted: " + name);
		return ok("Climate service is deleted: " + name);
    }
    
    public Result updateClimateService(long id) {
    	JsonNode json = request().body().asJson();
    	if (json == null) {
    		System.out.println("Climate service not saved, expecting Json data");
			return badRequest("Climate service not saved, expecting Json data");
    	}

    	//Parse JSON file
    	long rootServiceId = json.findPath("rootServiceId").asLong();
    	long creatorId = json.findPath("creatorId").asLong();
		String name = json.findPath("name").asText();
		String purpose = json.findPath("purpose").asText();
		String url = json.findPath("url").asText();
		String scenario = json.findPath("scenario").asText();
		String createTime = json.findPath("createTime").asText();
		String versionNo = json.findPath("versionNo").asText();

		try {
			ClimateService climateService = climateServiceRepository.findOne(id);
			
			climateService.setCreateTime(createTime);
			climateService.setName(name);
			climateService.setPurpose(purpose);
			climateService.setRootServiceId(rootServiceId);
			climateService.setScenario(scenario);
			climateService.setUrl(url);
			User user = userRepository.findOne(creatorId);
			climateService.setUser(user);
			climateService.setVersionNo(versionNo);
			
			ClimateService savedClimateService = climateServiceRepository.save(climateService);
			
			System.out.println("Climate Service updated: " + savedClimateService.getName());
			return created("Climate Service updated: " + savedClimateService.getName());
		} catch (PersistenceException pe) {
			pe.printStackTrace();
			System.out.println("Climate Service not updated: " + name);
			return badRequest("Climate Service not updated: " + name);
		}			
    }
    
    public Result updateClimateService(String oldName) {
    	JsonNode json = request().body().asJson();
    	if (json == null) {
    		System.out.println("Climate service not saved, expecting Json data");
			return badRequest("Climate service not saved, expecting Json data");
    	}

    	//Parse JSON file
    	long rootServiceId = json.findPath("rootServiceId").asLong();
    	long creatorId = json.findPath("creatorId").asLong();
		String name = json.findPath("name").asText();
		String purpose = json.findPath("purpose").asText();
		String url = json.findPath("url").asText();
		String scenario = json.findPath("scenario").asText();
		String createTime = json.findPath("createTime").asText();
		String versionNo = json.findPath("versionNo").asText();
		
		if (oldName == null || oldName.length() == 0) {
    		System.out.println("Old climate Service Name is null or empty!");
			return badRequest("Old climate Service Name is null or empty!");
    	}
		
		try {
			ClimateService climateService = climateServiceRepository.findByName(oldName);
			
			climateService.setCreateTime(createTime);
			climateService.setName(name);
			climateService.setPurpose(purpose);
			climateService.setRootServiceId(rootServiceId);
			climateService.setScenario(scenario);
			climateService.setUrl(url);
			User user = userRepository.findOne(creatorId);
			climateService.setUser(user);
			climateService.setVersionNo(versionNo);
			
			ClimateService savedClimateService = climateServiceRepository.save(climateService);
			
			System.out.println("Climate Service updated: " + savedClimateService.getName());
			return created("Climate Service updated: " + savedClimateService.getName());
		} catch (PersistenceException pe) {
			pe.printStackTrace();
			System.out.println("Climate Service not updated: " + name);
			return badRequest("Climate Service not updated: " + name);
		}			
    }
    
    public Result getClimateService(String name, String format) {
    	if (name == null || name.length() == 0) {
    		System.out.println("Climate Service Name is null or empty!");
			return badRequest("Climate Service Name is null or empty!");
    	}
    	
    	ClimateService climateService = climateServiceRepository.findByName(name);
    	if (climateService == null) {
    		System.out.println("Climate service not found with name: " + name);
			return notFound("Climate service not found with name: " + name);
    	}
    	
    	String result = new String();
    	if (format.equals("json")) {
    		result = new Gson().toJson(climateService);
    	}
    	
    	return ok(result);
    }
    
}


