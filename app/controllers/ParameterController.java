package controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.persistence.PersistenceException;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

import models.ClimateService;
import models.ClimateServiceRepository;
import models.Parameter;
import models.ParameterRepositiry;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * The main set of web services.
 */
@Named
@Singleton
public class ParameterController extends Controller {
	private final ParameterRepositiry parameterRepositiry;
    private final ClimateServiceRepository climateServiceRepository;
	
	// We are using constructor injection to receive a repository to support our desire for immutability.
    @Inject
    public ParameterController(final ParameterRepositiry parameterRepositiry,
    		final ClimateServiceRepository climateServiceRepository) {
        this.parameterRepositiry = parameterRepositiry;
        this.climateServiceRepository = climateServiceRepository;
    }
    
    public Result addParameter() {
    	System.out.println("here");
    	JsonNode json = request().body().asJson();
    	if (json == null) {
    		System.out.println("Parameter not saved, expecting Json data");
			return badRequest("Parameter not saved, expecting Json data");
    	}

    	//Parse JSON file
    	long serviceId = json.findPath("serviceId").asLong();
    	long indexInService = json.findPath("indexInService").asLong();
		String name = json.findPath("name").asText();
		String dataRange = json.findPath("dataRange").asText();
		String enumeration = json.findPath("enumeration").asText();
		String rule = json.findPath("rule").asText();
		String purpose = json.findPath("purpose").asText();
		
		try {
			ClimateService climateService = climateServiceRepository.findOne(serviceId);
			Parameter parameter = new Parameter(climateService, indexInService, name, 
					dataRange, enumeration, rule, purpose);
			Parameter savedParameter = parameterRepositiry.save(parameter);
			
			System.out.println("Parameter saved: " + savedParameter.getName());
			return created("Parameter saved: " + savedParameter.getName());
		} catch (PersistenceException pe) {
			pe.printStackTrace();
			System.out.println(serviceId);
			System.out.println(pe.getClass().toString());
			System.out.println("Parameter not saved: " + name);
			return badRequest("Parameter not saved: " + name);
		}			
    }
    
    public Result deleteParameter(String name) {
    	Parameter parameter = parameterRepositiry.findByName(name);
    	if (parameter == null) {
    		System.out.println("Parameter not found with name: " + name);
			return notFound("Parameter not found with name: " + name);
    	}
    	
    	parameterRepositiry.delete(parameter);
    	System.out.println("Parameter is deleted: " + name);
		return ok("Parameter is deleted: " + name);
    }
    
    public Result updateParameter() {
    	JsonNode json = request().body().asJson();
    	if (json == null) {
    		System.out.println("Parameter not updated, expecting Json data");
			return badRequest("Parameter not updated, expecting Json data");
    	}

    	//Parse JSON file
    	long serviceId = json.findPath("serviceId").asLong();
    	long indexInService = json.findPath("indexInService").asLong();
		String name = json.findPath("name").asText();
		String dataRange = json.findPath("dataRange").asText();
		String enumeration = json.findPath("enumeration").asText();
		String rule = json.findPath("rule").asText();
		String purpose = json.findPath("purpose").asText();
		
		if (name == null || name.length() == 0) {
    		System.out.println("Parameter Name is null or empty!");
			return badRequest("Parameter Name is null or empty!");
    	}
		
		try {
			ClimateService climateService = climateServiceRepository.findOne(serviceId);
			
			Parameter parameter = parameterRepositiry.findByName(name);
			parameter.setClimateService(climateService);
			parameter.setIndexInService(indexInService);
			parameter.setName(name);
			parameter.setDataRange(dataRange);
			parameter.setEnumeration(enumeration);
			parameter.setRule(rule);
			parameter.setPurpose(purpose);
			
			Parameter savedParameter = parameterRepositiry.save(parameter);
			
			System.out.println("Parameter updated: " + savedParameter.getName());
			return created("Parameter updated: " + savedParameter.getName());
		} catch (PersistenceException pe) {
			pe.printStackTrace();
			System.out.println("Parameter not updated: " + name);
			return badRequest("Parameter not updated: " + name);
		}			
    }
    
    public Result getParameter(String name, String format) {
    	if (name == null || name.length() == 0) {
    		System.out.println("Parameter Name is null or empty!");
			return badRequest("Parameter Name is null or empty!");
    	}
    	
    	Parameter parameter = parameterRepositiry.findByName(name);
    	if (parameter == null) {
    		System.out.println("Parameter not found with name: " + name);
			return notFound("Parameter not found with name: " + name);
    	}
    	
    	String result = new String();
    	if (format.equals("json")) {
    		result = new Gson().toJson(parameter);
    	}
    	
    	return ok(result);
    }
}
