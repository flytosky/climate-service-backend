package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import models.DatasetAndUser;
import models.DatasetAndUserRepository;
import models.ServiceAndDataset;
import models.ServiceAndDatasetRepository;
import models.ServiceAndUser;
import models.ServiceAndUserRepository;
import play.mvc.Controller;
import play.mvc.Result;

import com.google.gson.Gson;

@Named
@Singleton
public class AnalyticsController extends Controller{
	
	private final DatasetAndUserRepository datasetAndUserRepository;
	private final ServiceAndUserRepository serviceAndUserRepository;
	private final ServiceAndDatasetRepository serviceAndDatasetRepository;
	
	
	@Inject
	public AnalyticsController(DatasetAndUserRepository datasetAndUserRepository, 
			ServiceAndUserRepository serviceAndUserRepository, 
			ServiceAndDatasetRepository serviceAndDatasetRepository) {
		this.datasetAndUserRepository = datasetAndUserRepository;
		this.serviceAndUserRepository = serviceAndUserRepository;
		this.serviceAndDatasetRepository = serviceAndDatasetRepository;
	}
	
	public Result getAllServiceAndDatasetWithCount(String format) {

		try {
			Iterable<ServiceAndDataset> datasetAndServices = serviceAndDatasetRepository.findAll();

			if (datasetAndServices == null) {
				System.out.println("Dataset and Service: cannot be found!");
				return notFound("Dataset and Service: cannot be found!");
			}  

			Map<String, Object> map = jsonFormatSerivceAndDataset(datasetAndServices);

			String result = new String();
			if (format.equals("json")) {
				result = new Gson().toJson(map);
			}

			return ok(result);
		} catch (Exception e) {
			return badRequest("Service and Dataset not found");
		}
	}
	
	public Result getAllDatasetAndUserWithCount(String format) {

		try {
			Iterable<DatasetAndUser> datasetAndUsers = datasetAndUserRepository.findAll();

			if (datasetAndUsers == null) {
				System.out.println("User and Dataset: cannot be found!");
				return notFound("User and Dataset: cannot be found!");
			}  

			Map<String, Object> map = jsonFormat(datasetAndUsers);

			String result = new String();
			if (format.equals("json")) {
				result = new Gson().toJson(map);
			}

			return ok(result);
		} catch (Exception e) {
			return badRequest("DatasetLog not found");
		}
	}
	
	public Result getAllServiceAndUserWithCount(String format) {

		try {
			Iterable<ServiceAndUser> serviceAndUsers = serviceAndUserRepository.findAll();

			if (serviceAndUsers == null) {
				System.out.println("User and Service: cannot be found!");
				return notFound("User and Service: cannot be found!");
			}  

			Map<String, Object> map = jsonFormatServiceAndUser(serviceAndUsers);

			String result = new String();
			if (format.equals("json")) {
				result = new Gson().toJson(map);
			}

			return ok(result);
		} catch (Exception e) {
			return badRequest("Service and user not found");
		}
	}

	private Map<String, Object> jsonFormat(Iterable<DatasetAndUser> userDatasets) {

		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> rels = new ArrayList<Map<String, Object>>();

		int i = 1;
		for (DatasetAndUser userDataset : userDatasets) {
			int source = 0;
			int target = 0;
			// Check whether the current user has already existed
			for (int j = 0; j < nodes.size(); j++) {
				if (nodes.get(j).get("title")
						.equals(userDataset.getUser().getUserName())) {
					source = (int) nodes.get(j).get("id");
					break;
				}
			}
			if (source == 0) {
				nodes.add(map6("id", i, "title", userDataset.getUser()
						.getUserName(), "label", "user", "cluster", "1",
						"value", 1, "group", "user"));
				source = i;
				i++;
			}
			// Check whether the current dataset has already existed
			for (int j = 0; j < nodes.size(); j++) {
				if (nodes.get(j).get("title")
						.equals(userDataset.getDataset().getName())) {
					target = (int) nodes.get(j).get("id");
					break;
				}
			}
			if (target == 0) {
				nodes.add(map6("id", i, "title", userDataset.getDataset()
						.getName(), "label", "dataset", "cluster", "2",
						"value", 2, "group", "dataset"));
				target = i;
				i++;
			}

			rels.add(map3("from", source, "to", target, "title", "USE"));

		}

		return map("nodes", nodes, "edges", rels);
	}
	
	private Map<String, Object> jsonFormatSerivceAndDataset(Iterable<ServiceAndDataset> serviceDatasets) {

		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> rels = new ArrayList<Map<String, Object>>();

		int i = 1;
		for (ServiceAndDataset serviceDataset : serviceDatasets) {
			int source = 0;
			int target = 0;
			// Check whether the current user has already existed
			for (int j = 0; j < nodes.size(); j++) {
				if (nodes.get(j).get("title")
						.equals(serviceDataset.getClimateService().getName())) {
					source = (int) nodes.get(j).get("id");
					break;
				}
			}
			if (source == 0) {
				nodes.add(map6("id", i, "title", serviceDataset.getClimateService()
						.getName(), "label", "service", "cluster", "3",
						"value", 1, "group", "service"));
				source = i;
				i++;
			}
			// Check whether the current dataset has already existed
			for (int j = 0; j < nodes.size(); j++) {
				if (nodes.get(j).get("title")
						.equals(serviceDataset.getDataset().getName())) {
					target = (int) nodes.get(j).get("id");
					break;
				}
			}
			if (target == 0) {
				nodes.add(map6("id", i, "title", serviceDataset.getDataset()
						.getName(), "label", "dataset", "cluster", "2",
						"value", 2, "group", "dataset"));
				target = i;
				i++;
			}

			rels.add(map3("from", source, "to", target, "title", "Utilize"));

		}

		return map("nodes", nodes, "edges", rels);
	}
	
	private Map<String, Object> jsonFormatServiceAndUser(Iterable<ServiceAndUser> userServices) {

		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> rels = new ArrayList<Map<String, Object>>();

		int i = 1;
		for (ServiceAndUser userService : userServices) {
			int source = 0;
			int target = 0;
			// Check whether the current user has already existed
			for (int j = 0; j < nodes.size(); j++) {
				if (nodes.get(j).get("title")
						.equals(userService.getUser().getUserName())) {
					source = (int) nodes.get(j).get("id");
					break;
				}
			}
			if (source == 0) {
				nodes.add(map6("id", i, "title", userService.getUser()
						.getUserName(), "label", "user", "cluster", "1",
						"value", 1, "group", "user"));
				source = i;
				i++;
			}
			// Check whether the current dataset has already existed
			for (int j = 0; j < nodes.size(); j++) {
				if (nodes.get(j).get("title")
						.equals(userService.getClimateService().getName())) {
					target = (int) nodes.get(j).get("id");
					break;
				}
			}
			if (target == 0) {
				nodes.add(map6("id", i, "title", userService.getClimateService()
						.getName(), "label", "service", "cluster", "3",
						"value", 2, "group", "service"));
				target = i;
				i++;
			}

			rels.add(map3("from", source, "to", target, "title", "USE"));

		}

		return map("nodes", nodes, "edges", rels);
	}
	
	private Map<String, Object> map(String key1, Object value1, String key2,
			Object value2) {
		Map<String, Object> result = new HashMap<String, Object>(2);
		result.put(key1, value1);
		result.put(key2, value2);
		return result;
	}

	private Map<String, Object> map3(String key1, Object value1, String key2,
			Object value2, String key3, Object value3) {
		Map<String, Object> result = new HashMap<String, Object>(3);
		result.put(key1, value1);
		result.put(key2, value2);
		result.put(key3, value3);
		return result;
	}

	private Map<String, Object> map6(String key1, Object value1, String key2,
			Object value2, String key3, Object value3, String key4,
			Object value4, String key5, Object value5, String key6,
			Object value6) {
		Map<String, Object> result = new HashMap<String, Object>(6);
		result.put(key1, value1);
		result.put(key2, value2);
		result.put(key3, value3);
		result.put(key4, value4);
		result.put(key5, value5);
		result.put(key6, value6);
		return result;
	}
}
