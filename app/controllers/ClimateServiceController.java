package controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.ClimateService;
import models.ClimateServiceRepository;
import models.User;
import models.UserRepository;
import util.Common;
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

	// static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ssz";
	private final ClimateServiceRepository climateServiceRepository;
	private final UserRepository userRepository;

	// We are using constructor injection to receive a repository to support our
	// desire for immutability.
	@Inject
	public ClimateServiceController(
			final ClimateServiceRepository climateServiceRepository,
			UserRepository userRepository) {
		this.climateServiceRepository = climateServiceRepository;
		this.userRepository = userRepository;
	}

	public Result addClimateService() {
		JsonNode json = request().body().asJson();
		if (json == null) {
			System.out
					.println("Climate service not saved, expecting Json data");
			return badRequest("Climate service not saved, expecting Json data");
		}

		// Parse JSON file
		long rootServiceId = json.findPath("rootServiceId").asLong();
		long creatorId = json.findPath("creatorId").asLong();
		String name = json.findPath("name").asText();
		String purpose = json.findPath("purpose").asText();
		String url = json.findPath("url").asText();
		String scenario = json.findPath("scenario").asText();

		Date createTime = new Date();
		SimpleDateFormat format = new SimpleDateFormat(Common.DATE_PATTERN);
		try {
			createTime = format.parse(json.findPath("createTime").asText());
		} catch (ParseException e) {
			System.out
					.println("No creation date specified, set to current time");
		}
		String versionNo = json.findPath("versionNo").asText();

		try {
			User user = userRepository.findOne(creatorId);
			ClimateService climateService = new ClimateService(rootServiceId,
					user, name, purpose, url, scenario, createTime, versionNo);
			ClimateService savedClimateService = climateServiceRepository
					.save(climateService);

			System.out.println("Climate Service saved: "
					+ savedClimateService.getName());
			return created(new Gson().toJson(savedClimateService.getId()));
		} catch (PersistenceException pe) {
			pe.printStackTrace();
			System.out.println("Climate Service not saved: " + name);
			return badRequest("Climate Service not saved: " + name);
		}
	}

	public Result deleteClimateServiceById(long id) {
		ClimateService climateService = climateServiceRepository.findOne(id);
		if (climateService == null) {
			System.out.println("Climate service not found with id: " + id);
			return notFound("Climate service not found with id: " + id);
		}

		climateServiceRepository.delete(climateService);
		System.out.println("Climate service is deleted: " + id);
		return ok("Climate service is deleted: " + id);
	}
	
	public Result deleteClimateServiceByName(String name) {
		ClimateService climateService = climateServiceRepository.findFirstByName(name);
		if (climateService == null) {
			System.out.println("Climate service not found with name: " + name);
			return notFound("Climate service not found with name: " + name);
		}

		climateServiceRepository.delete(climateService);
		System.out.println("Climate service is deleted: " + name);
		return ok("Climate service is deleted: " + name);
	}

	public Result updateClimateServiceById(long id) {
		JsonNode json = request().body().asJson();
		if (json == null) {
			System.out
					.println("Climate service not saved, expecting Json data");
			return badRequest("Climate service not saved, expecting Json data");
		}

		// Parse JSON file
		long rootServiceId = json.findPath("rootServiceId").asLong();
		long creatorId = json.findPath("creatorId").asLong();
		String name = json.findPath("name").asText();
		String purpose = json.findPath("purpose").asText();
		String url = json.findPath("url").asText();
		String scenario = json.findPath("scenario").asText();
		String versionNo = json.findPath("versionNo").asText();
		// Creation time should be immutable and not updated.

		try {
			ClimateService climateService = climateServiceRepository
					.findOne(id);

			climateService.setName(name);
			climateService.setPurpose(purpose);
			climateService.setRootServiceId(rootServiceId);
			climateService.setScenario(scenario);
			climateService.setUrl(url);
			User user = userRepository.findOne(creatorId);
			climateService.setUser(user);
			climateService.setVersionNo(versionNo);

			ClimateService savedClimateService = climateServiceRepository
					.save(climateService);

			System.out.println("Climate Service updated: "
					+ savedClimateService.getName());
			return created("Climate Service updated: "
					+ savedClimateService.getName());
		} catch (PersistenceException pe) {
			pe.printStackTrace();
			System.out.println("Climate Service not updated: " + name);
			return badRequest("Climate Service not updated: " + name);
		}
	}

	public Result updateClimateServiceByName(String oldName) {
		JsonNode json = request().body().asJson();
		if (json == null) {
			System.out
					.println("Climate service not saved, expecting Json data");
			return badRequest("Climate service not saved, expecting Json data");
		}
		System.out.println(json);
		// Parse JSON file
		long rootServiceId = json.findPath("rootServiceId").asLong();
		long creatorId = json.findPath("creatorId").asLong();
		String name = json.findPath("name").asText();
		String purpose = json.findPath("purpose").asText();
		String url = json.findPath("url").asText();
		String scenario = json.findPath("scenario").asText();
		String versionNo = json.findPath("versionNo").asText();
		// Creation time is immutable and should not be updated

		if (oldName == null || oldName.length() == 0) {
			System.out.println("Old climate Service Name is null or empty!");
			return badRequest("Old climate Service Name is null or empty!");
		}

		try {
			ClimateService climateService = climateServiceRepository
					.findFirstByName(oldName);

			climateService.setName(name);
			climateService.setPurpose(purpose);
			climateService.setRootServiceId(rootServiceId);
			climateService.setScenario(scenario);
			climateService.setUrl(url);
			User user = userRepository.findOne(creatorId);
			climateService.setUser(user);
			climateService.setVersionNo(versionNo);

			ClimateService savedClimateService = climateServiceRepository
					.save(climateService);

			System.out.println("Climate Service updated: "
					+ savedClimateService.getName());
			return created("Climate Service updated: "
					+ savedClimateService.getName());
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

		List<ClimateService> climateService = climateServiceRepository
				.findAllByName(name);
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

	public Result getClimateServiceById(long id) {
		ClimateService climateService = climateServiceRepository.findOne(id);
		if (climateService == null) {
			System.out.println("Climate service not found with id: " + id);
			return notFound("Climate service not found with id: " + id);
		}

		String result = new Gson().toJson(climateService);

		return ok(result);
	}

	public Result getAllClimateServices(String format) {
		Iterable<ClimateService> climateServices = climateServiceRepository
				.findAll();
		if (climateServices == null) {
			System.out.println("No climate service found");
		}

		String result = new String();
		if (format.equals("json")) {
			result = new Gson().toJson(climateServices);
		}

		return ok(result);

	}

    public Result getAllClimateServicesOrderByCreateTime(String format){
        Iterable<ClimateService> climateServices = climateServiceRepository
                .findByOrderByCreateTimeDesc();
        if (climateServices == null) {
            System.out.println("No climate service found");
        }

        String result = new String();
        if (format.equals("json")) {
            result = new Gson().toJson(climateServices);
        }

        return ok(result);
    }
}
