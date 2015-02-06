package controllers;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.persistence.PersistenceException;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

import models.Parameter;
import models.ParameterOption;
import models.ParameterRepository;
import models.ParameterOptionRepository;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * The main set of web services.
 */
@Named
@Singleton
public class ParameterOptionController extends Controller {
	private final ParameterOptionRepository parameterOptionRepository;
    private final ParameterRepository parameterRepository;
	
	// We are using constructor injection to receive a repository to support our desire for immutability.
    @Inject
    public ParameterOptionController(final ParameterOptionRepository parameterOptionRepository,
    		final ParameterRepository parameterRepository) {
        this.parameterOptionRepository = parameterOptionRepository;
        this.parameterRepository = parameterRepository;
    }
    
    public Result addParameterOption() {
    	System.out.println("here");
    	JsonNode json = request().body().asJson();
    	if (json == null) {
    		System.out.println("Parameter not saved, expecting Json data");
			return badRequest("Parameter not saved, expecting Json data");
    	}

    	//Parse JSON file
    	Long parameterId = json.findPath("parameterId").asLong();
		String parameterValue = json.findPath("parameterValue").asText();
		
		try {
			Parameter parameter = parameterRepository.findOne(parameterId);
			ParameterOption parameterOption = new ParameterOption(parameter, parameterValue);
			ParameterOption savedParameterOption = parameterOptionRepository.save(parameterOption);
			
			System.out.println("ParameterOption saved: " + savedParameterOption.getParameterValue());
			return created(new Gson().toJson(savedParameterOption.getId()));
		} catch (PersistenceException pe) {
			pe.printStackTrace();
			System.out.println(parameterId);
			System.out.println(pe.getClass().toString());
			System.out.println("ParameterOption not saved: " + parameterValue);
			return badRequest("ParameterOption not saved: " + parameterValue);
		}			
    }
    
    public Result deleteParameterOptionById(Long id) {
    	ParameterOption parameterOption = parameterOptionRepository.findOne(id);
    	if (parameterOption == null) {
    		System.out.println("ParameterOption not found with id: " + id);
			return notFound("Parameter not found with id: " + id);
    	}
    	
    	parameterOptionRepository.delete(parameterOption);
    	System.out.println("Parameter is deleted: " + id);
		return ok("Parameter is deleted: " + id);
    }
    
    public Result updateParameterOptionById(long id) {
    	JsonNode json = request().body().asJson();
    	if (json == null) {
    		System.out.println("Parameter not updated, expecting Json data");
			return badRequest("Parameter not updated, expecting Json data");
    	}

    	//Parse JSON file
    	Long parameterId = json.findPath("parameterId").asLong();
		String parameterValue = json.findPath("parameterValue").asText();
		
		try {
			Parameter parameter = parameterRepository.findOne(parameterId);
			ParameterOption parameterOption = parameterOptionRepository.findOne(id);
			parameterOption.setParameter(parameter);
			parameterOption.setParameterValue(parameterValue);
			
			ParameterOption savedParameterOption = parameterOptionRepository.save(parameterOption);
			
			System.out.println("ParameterOption updated: " + savedParameterOption.getId());
			return created("ParameterOption updated: " + savedParameterOption.getId());
		} catch (PersistenceException pe) {
			pe.printStackTrace();
			System.out.println("ParameterOption not updated: " + id);
			return badRequest("ParameterOption not updated: " + id);
		}			
    }
    
    public Result getParameterOptionById(Long id, String format) {
    	if (id < 0) {
    		System.out.println("id is negative!");
			return badRequest("id is negative!");
    	}
    	
    	ParameterOption parameterOption = parameterOptionRepository.findOne(id);
    	if (parameterOption == null) {
    		System.out.println("ParameterOption not found with id: " + id);
			return notFound("ParameterOption not found with id: " + id);
    	}
    	
    	String result = new String();
    	if (format.equals("json")) {
    		result = new Gson().toJson(parameterOption);
    	}
    	
    	return ok(result);
    }
    
    public Result getAllParameterOptions(String format) {
    	
    	String result = new String();
    	
    	if (format.equals("json")) {
    		result = new Gson().toJson(parameterOptionRepository.findAll());
    	}
    			
    	return ok(result);
    }
}
