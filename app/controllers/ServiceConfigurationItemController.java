package controllers;

import java.util.List;

import models.Parameter;
import models.ParameterRepository;
import models.ServiceConfiguration;
import models.ServiceConfigurationItem;
import models.ServiceConfigurationItemRepository;
import models.ServiceConfigurationRepository;
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
public class ServiceConfigurationItemController extends Controller {

	private final ServiceConfigurationItemRepository serviceConfigurationItemRepository;
	private final ServiceConfigurationRepository serviceConfigurationRepository;
	private final ParameterRepository parameterRepository;
	// We are using constructor injection to receive a repository to support our
	// desire for immutability.
	@Inject
	public ServiceConfigurationItemController(
			final ServiceConfigurationRepository serviceConfigurationRepository,
			final ParameterRepository parameterRepository,
			final ServiceConfigurationItemRepository serviceConfigurationItemRepository) {
		this.parameterRepository = parameterRepository;
		this.serviceConfigurationItemRepository = serviceConfigurationItemRepository;
		this.serviceConfigurationRepository = serviceConfigurationRepository;
	}

	public Result addServiceConfigItem() {
		JsonNode json = request().body().asJson();
		if (json == null) {
			System.out
					.println("ServiceConfigurationItem not saved, expecting Json data");
			return badRequest("ServiceConfigurationItem not saved, expecting Json data");
		}

		// Parse JSON file
		long serviceConfigId = json.findPath("serviceConfigId").asLong();
		long paramId = json.findPath("paramId").asLong();
		String value = json.findPath("value").asText();

		try {
			ServiceConfiguration serviceConfiguration = serviceConfigurationRepository
					.findOne(serviceConfigId);
			Parameter param = parameterRepository.findOne(paramId);
			ServiceConfigurationItem newConfigItem = new ServiceConfigurationItem(
					serviceConfiguration, param, value);
			serviceConfigurationItemRepository.save(newConfigItem);

			System.out.println("ServiceConfigurationItem saved: "
					+ newConfigItem.getValue());
			return created("ServiceConfigurationItem saved: "
					+ newConfigItem.getValue());
		} catch (PersistenceException pe) {
			pe.printStackTrace();
			System.out.println("ServiceConfigurationItem not saved: " + value);
			return badRequest("ServiceConfigurationItem not saved: " + value);
		}
	}

	public Result deleteClimateService(long id) {
		ServiceConfigurationItem delConfigItem = serviceConfigurationItemRepository
				.findOne(id);
		if (delConfigItem == null) {
			System.out.println("ServiceConfigurationItem not found with id: "
					+ id);
			return notFound("ServiceConfigurationItem not found with id: " + id);
		}

		serviceConfigurationItemRepository.delete(delConfigItem);
		System.out.println("ServiceConfigurationItem is deleted: " + id);
		return ok("ServiceConfigurationItem is deleted: " + id);
	}

	public Result updateServiceConfigItem(long id) {
		JsonNode json = request().body().asJson();
		if (json == null) {
			System.out
					.println("Climate service not saved, expecting Json data");
			return badRequest("Climate service not saved, expecting Json data");
		}

		// Parse JSON file
		long serviceConfigId = json.findPath("serviceConfigId").asLong();
		long paramId = json.findPath("paramId").asLong();
		String value = json.findPath("value").asText();

		try {

			ServiceConfiguration serviceConfiguration = serviceConfigurationRepository
					.findOne(serviceConfigId);
			Parameter param = parameterRepository.findOne(paramId);

			if (serviceConfiguration != null || param != null || value != null)
				return ok("Nothing to update, ServiceConfigItem unchanged");

			ServiceConfigurationItem configItem = new ServiceConfigurationItem(
					serviceConfiguration, param, value);
			configItem.setServiceConfiguration(serviceConfiguration);
			configItem.setParameter(param);
			configItem.setValue(value);

			serviceConfigurationItemRepository.save(configItem);

			System.out.println("ServiceConfigItem updated: "
					+ configItem.getValue());
			return ok("ServiceConfigItem updated: " + configItem.getValue());
		} catch (PersistenceException pe) {
			pe.printStackTrace();
			System.out.println("ServiceConfigItem not updated: " + value);
			return badRequest("ServiceConfigItem not updated: " + value);
		}
	}

	public Result getServiceConfigItemByParamName(String parameterName) {
		if (parameterName == null || parameterName.length() == 0) {
			System.out.println("Parameter Name is null or empty!");
			return badRequest("Parameter Name is null or empty!");
		}

		Parameter param = parameterRepository.findByName(parameterName);
		if (param == null) {
			System.out.println("Parameter not found with name: "
					+ parameterName);
			return notFound("Parameter not found with name: " + parameterName);
		}

		ServiceConfigurationItem serviceConfigItem = serviceConfigurationItemRepository
				.findByParameter(param);
		if (serviceConfigItem == null) {
			System.out.println("ServiceConfigurationItem not found with name: "
					+ parameterName);
			return notFound("ServiceConfigurationItem not found with name: "
					+ parameterName);
		}

		String result = new Gson().toJson(serviceConfigItem);

		return ok(result);
	}

	public Result getServiceConfigItemById(Long id) {
		if (id == null) {
			System.out.println("ServiceConfigItem id is null or empty!");
			return badRequest("ServiceConfigItem id is null or empty!");
		}

		ServiceConfigurationItem serviceConfigItem = serviceConfigurationItemRepository
				.findOne(id);
		if (serviceConfigItem == null) {
			System.out.println("ServiceConfigurationItem not found with id: "
					+ id);
			return notFound("ServiceConfigurationItem not found with id: " + id);
		}

		String result = new Gson().toJson(serviceConfigItem);

		return ok(result);
	}

	public Result getServiceConfigItemsInServiceConfig(Long serviceConfigId) {
		if (serviceConfigId == null) {
			System.out.println("ServiceConfig id is null or empty!");
			return badRequest("ServiceConfig id is null or empty!");
		}

		ServiceConfiguration serviceConfiguration = serviceConfigurationRepository
				.findOne(serviceConfigId);
		if (serviceConfiguration == null) {
			System.out.println("ServiceConfiguration not found with id: "
					+ serviceConfigId);
			return notFound("ServiceConfiguration not found with id: "
					+ serviceConfigId);
		}
		List<ServiceConfigurationItem> serviceConfigItems = serviceConfigurationItemRepository
				.findAllByServiceConfiguration(serviceConfiguration);

		String result = new Gson().toJson(serviceConfigItems);

		return ok(result);
	}

}
