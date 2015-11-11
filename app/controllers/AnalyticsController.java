package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import models.ClimateService;
import models.ClimateServiceRepository;
import models.Dataset;
import models.DatasetAndUser;
import models.DatasetAndUserRepository;
import models.DatasetRepository;
import models.ServiceAndDataset;
import models.ServiceAndDatasetRepository;
import models.ServiceAndUser;
import models.ServiceAndUserRepository;
import models.User;
import models.UserRepository;
import play.mvc.Controller;
import play.mvc.Result;
import util.Matrix;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

@Named
@Singleton
public class AnalyticsController extends Controller {

	private final DatasetAndUserRepository datasetAndUserRepository;
	private final ServiceAndUserRepository serviceAndUserRepository;
	private final ServiceAndDatasetRepository serviceAndDatasetRepository;
	private final UserRepository userRepository;
	private final DatasetRepository datasetRepository;
	private final ClimateServiceRepository serviceRepository;

	@Inject
	public AnalyticsController(
			DatasetAndUserRepository datasetAndUserRepository,
			ServiceAndUserRepository serviceAndUserRepository,
			ServiceAndDatasetRepository serviceAndDatasetRepository,
			UserRepository userRepository, DatasetRepository datasetRepository,
			ClimateServiceRepository serviceRepository) {
		this.datasetAndUserRepository = datasetAndUserRepository;
		this.serviceAndUserRepository = serviceAndUserRepository;
		this.serviceAndDatasetRepository = serviceAndDatasetRepository;
		this.userRepository = userRepository;
		this.datasetRepository = datasetRepository;
		this.serviceRepository = serviceRepository;
	}

	public long getResultCount(String param) {
		long count = 0;
		switch (param) {
		case "User":
			count = userRepository.count();
			break;
		case "Dataset":
			count = datasetRepository.count();
			break;
		case "Service":
			count = serviceRepository.count();
			break;
		default:
			break;
		}
		return count;
	}

	public Result getRelationalKnowledgeGraph(String format) {
		JsonNode json = request().body().asJson();

		if (json == null) {
			System.out
					.println("Cannot find relational knowledge graph, expecting Json data");
			return badRequest("Cannot find relational knowledge graph, expecting Json data");
		}
		String param1 = json.findPath("param1").asText();
		String param2 = json.findPath("param2").asText();
		String param3 = json.findPath("param3").asText();
		int count1 = (int) getResultCount(param1), count2 = (int) getResultCount(param2), count3 = (int) getResultCount(param3);

		int[][] relations = new int[count1][count3];
		try {
			Iterable<DatasetAndUser> datasetAndUsers = datasetAndUserRepository
					.findAll();

			if (datasetAndUsers == null) {
				System.out.println("User and Dataset: cannot be found!");
				return notFound("User and Dataset: cannot be found!");
			}

			for (DatasetAndUser one : datasetAndUsers) {
				int i = (int) one.getUser().getId() - 1;
				int j = (int) one.getDataset().getId() - 1;
				relations[i][j] = (int) one.getCount();
			}

			Matrix m1 = new Matrix(relations);
			Matrix m2 = m1.transpose();
			Matrix m3 = m1.times(m2);
			int[][] res = m3.getArray();

			Map<String, Object> map = jsonFormatUserAndUser(res);
			
			String result = new String();
			if (format.equals("json")) {
				result = new Gson().toJson(map);
			}
			return ok(result);
		} catch (Exception e) {
			return badRequest("User Relationship not found");
		}

	}

	public Result getAllServiceAndDatasetWithCount(String format) {

		try {
			Iterable<ServiceAndDataset> datasetAndServices = serviceAndDatasetRepository
					.findAll();

			if (datasetAndServices == null) {
				System.out.println("Dataset and Service: cannot be found!");
				return notFound("Dataset and Service: cannot be found!");
			}

			Map<String, Object> map = jsonFormatServiceAndDataset(datasetAndServices);

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
			Iterable<DatasetAndUser> datasetAndUsers = datasetAndUserRepository
					.findAll();

			if (datasetAndUsers == null) {
				System.out.println("User and Dataset: cannot be found!");
				return notFound("User and Dataset: cannot be found!");
			}

			Map<String, Object> map = jsonFormatUserAndDataset(datasetAndUsers);

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
			Iterable<ServiceAndUser> serviceAndUsers = serviceAndUserRepository
					.findAll();

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

	public Result getOneUserWithAllDatasetAndCount(long userId, String format) {

		try {
			User user = userRepository.findOne(userId);
			Iterable<DatasetAndUser> datasetAndUsers = datasetAndUserRepository
					.findByUser(user);

			if (datasetAndUsers == null) {
				System.out.println("User and Dataset: cannot be found!");
				return notFound("User and Dataset: cannot be found!");
			}

			Map<String, Object> map = jsonFormatUserAndDataset(datasetAndUsers);

			String result = new String();
			if (format.equals("json")) {
				result = new Gson().toJson(map);
			}

			return ok(result);
		} catch (Exception e) {
			return badRequest("User and Dataset not found");
		}
	}

	public Result getOneDatasetWithAllDatasetAndCount(long datasetId,
			String format) {

		try {
			Dataset dataset = datasetRepository.findOne(datasetId);
			Iterable<DatasetAndUser> datasetAndUsers = datasetAndUserRepository
					.findByDataset(dataset);

			if (datasetAndUsers == null) {
				System.out.println("User and Dataset: cannot be found!");
				return notFound("User and Dataset: cannot be found!");
			}

			Map<String, Object> map = jsonFormatUserAndDataset(datasetAndUsers);

			String result = new String();
			if (format.equals("json")) {
				result = new Gson().toJson(map);
			}

			return ok(result);
		} catch (Exception e) {
			return badRequest("User and Dataset not found");
		}
	}

	public Result getOneUserWithAllServiceAndCount(long userId, String format) {

		try {
			User user = userRepository.findOne(userId);
			Iterable<ServiceAndUser> serviceAndUsers = serviceAndUserRepository
					.findByUser(user);

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
			return badRequest("User and Service not found");
		}
	}

	public Result getOneServiceWithAllUserAndCount(long serviceId, String format) {

		try {
			ClimateService service = serviceRepository.findOne(serviceId);
			Iterable<ServiceAndUser> serviceAndUsers = serviceAndUserRepository
					.findByClimateService(service);

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
			return badRequest("User and Service not found");
		}
	}

	public Result getOneServiceWithAllDatasetAndCount(long serviceId,
			String format) {

		try {
			ClimateService service = serviceRepository.findOne(serviceId);
			Iterable<ServiceAndDataset> datasetAndServices = serviceAndDatasetRepository
					.findByClimateService(service);

			if (datasetAndServices == null) {
				System.out.println("Dataset and Service: cannot be found!");
				return notFound("Dataset and Service: cannot be found!");
			}

			Map<String, Object> map = jsonFormatServiceAndDataset(datasetAndServices);

			String result = new String();
			if (format.equals("json")) {
				result = new Gson().toJson(map);
			}

			return ok(result);
		} catch (Exception e) {
			return badRequest("Dataset and Service not found");
		}
	}

	public Result getOneDatasetWithAllServiceAndCount(long datasetId,
			String format) {

		try {
			Dataset dataset = datasetRepository.findOne(datasetId);
			Iterable<ServiceAndDataset> datasetAndServices = serviceAndDatasetRepository
					.findByDataset(dataset);

			if (datasetAndServices == null) {
				System.out.println("Dataset and Service: cannot be found!");
				return notFound("Dataset and Service: cannot be found!");
			}

			Map<String, Object> map = jsonFormatServiceAndDataset(datasetAndServices);

			String result = new String();
			if (format.equals("json")) {
				result = new Gson().toJson(map);
			}

			return ok(result);
		} catch (Exception e) {
			return badRequest("Dataset and Service not found");
		}
	}

	private Map<String, Object> jsonFormatUserAndDataset(
			Iterable<DatasetAndUser> userDatasets) {

		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> rels = new ArrayList<Map<String, Object>>();

		int i = 1;
		int edgeId = 1;
		for (DatasetAndUser userDataset : userDatasets) {
			int source = 0;
			int target = 0;
			// Check whether the current user has already existed
			for (int j = 0; j < nodes.size(); j++) {
				if (nodes.get(j).get("group").equals("user")
						&& (long) nodes.get(j).get("userId") == userDataset
								.getUser().getId()) {
					source = (int) nodes.get(j).get("id");
					break;
				}
			}
			if (source == 0) {
				String realName = userDataset.getUser().getFirstName() + " "
						+ userDataset.getUser().getLastName();
				nodes.add(map7("id", i, "title", realName, "label", userDataset
						.getUser().getUserName(), "cluster", "1", "value", 1,
						"group", "user", "userId", userDataset.getUser()
								.getId()));

				source = i;
				i++;
			}
			// Check whether the current dataset has already existed
			for (int j = 0; j < nodes.size(); j++) {
				if (nodes.get(j).get("group").equals("dataset")
						&& (long) nodes.get(j).get("datasetId") == userDataset
								.getDataset().getId()) {
					target = (int) nodes.get(j).get("id");
					break;
				}
			}
			if (target == 0) {
				nodes.add(map7("id", i, "title", userDataset.getDataset()
						.getName(), "label",
						userDataset.getDataset().getName(), "cluster", "2",
						"value", 2, "group", "dataset", "datasetId",
						userDataset.getDataset().getId()));
				target = i;
				i++;
			}
			rels.add(map5("from", source, "to", target, "title", "USE",
					"edgeId", edgeId, "weight", userDataset.getCount()));
			edgeId++;
		}

		return map("nodes", nodes, "edges", rels);
	}

	private Map<String, Object> jsonFormatServiceAndDataset(
			Iterable<ServiceAndDataset> serviceDatasets) {

		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> rels = new ArrayList<Map<String, Object>>();

		int i = 1;
		for (ServiceAndDataset serviceDataset : serviceDatasets) {
			int source = 0;
			int target = 0;
			// Check whether the current service has already existed
			for (int j = 0; j < nodes.size(); j++) {
				if (nodes.get(j).get("group").equals("service")
						&& (long) nodes.get(j).get("serviceId") == serviceDataset
								.getClimateService().getId()) {
					source = (int) nodes.get(j).get("id");
					break;
				}
			}
			if (source == 0) {
				nodes.add(map7("id", i, "title", serviceDataset
						.getClimateService().getName(), "label", serviceDataset
						.getClimateService().getName(), "cluster", "3",
						"value", 1, "group", "service", "serviceId",
						serviceDataset.getClimateService().getId()));
				source = i;
				i++;
			}
			// Check whether the current dataset has already existed
			for (int j = 0; j < nodes.size(); j++) {
				if (nodes.get(j).get("group").equals("dataset")
						&& (long) nodes.get(j).get("datasetId") == serviceDataset
								.getDataset().getId()) {
					target = (int) nodes.get(j).get("id");
					break;
				}
			}
			if (target == 0) {
				nodes.add(map7("id", i, "title", serviceDataset.getDataset()
						.getName(), "label", serviceDataset.getDataset()
						.getName(), "cluster", "2", "value", 2, "group",
						"dataset", "datasetId", serviceDataset.getDataset()
								.getId()));
				target = i;
				i++;
			}

			rels.add(map3("from", source, "to", target, "title", "Utilize"));

		}

		return map("nodes", nodes, "edges", rels);
	}

	private Map<String, Object> jsonFormatServiceAndUser(
			Iterable<ServiceAndUser> userServices) {

		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> rels = new ArrayList<Map<String, Object>>();

		int i = 1;
		for (ServiceAndUser userService : userServices) {
			int source = 0;
			int target = 0;
			// Check whether the current user has already existed
			for (int j = 0; j < nodes.size(); j++) {
				if (nodes.get(j).get("group").equals("user")
						&& (long) nodes.get(j).get("userId") == userService
								.getUser().getId()) {
					source = (int) nodes.get(j).get("id");
					break;
				}
			}
			if (source == 0) {
				String realName = userService.getUser().getFirstName() + " "
						+ userService.getUser().getMiddleInitial() + " "
						+ userService.getUser().getLastName();
				nodes.add(map7("id", i, "title", realName, "label", userService
						.getUser().getUserName(), "cluster", "1", "value", 1,
						"group", "user", "userId", userService.getUser()
								.getId()));
				source = i;
				i++;
			}
			// Check whether the current service has already existed
			for (int j = 0; j < nodes.size(); j++) {
				if (nodes.get(j).get("group").equals("service")
						&& (long) nodes.get(j).get("serviceId") == userService
								.getClimateService().getId()) {
					target = (int) nodes.get(j).get("id");
					break;
				}
			}
			if (target == 0) {
				nodes.add(map7("id", i, "title", userService
						.getClimateService().getName(), "label", userService
						.getClimateService().getName(), "cluster", "3",
						"value", 2, "group", "service", "serviceId",
						userService.getClimateService().getId()));
				target = i;
				i++;
			}

			rels.add(map3("from", source, "to", target, "title", "USE"));

		}

		return map("nodes", nodes, "edges", rels);
	}

	private Map<String, Object> jsonFormatUserAndUser(int[][] users) {

		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> rels = new ArrayList<Map<String, Object>>();

		int i = 1;
		for (int m = 0; m < users.length; m++) {
			for (int n = m + 1; n < users[0].length; n++) {
				if (users[m][n] > 0) {
					int source = 0;
					int target = 0;
					// Check whether the current user has already existed
					for (int j = 0; j < nodes.size(); j++) {
						if (nodes.get(j).get("group").equals("user")
								&& (long) nodes.get(j).get("userId") == (long)m + 1) {
							source = (int) nodes.get(j).get("id");
							break;
						}
					}
					if (source == 0) {
						String userName = userRepository.findOne((long)m+1).getUserName();
						nodes.add(map7("id", i, "title", userName, "label",
								userName, "cluster", "1", "value", 1, "group",
								"user", "userId", (long)m + 1));
						source = i;
						i++;
					}
					for (int j = 0; j < nodes.size(); j++) {
						if (nodes.get(j).get("group").equals("user")
								&& (long) nodes.get(j).get("userId") == (long)n + 1) {
							target = (int) nodes.get(j).get("id");
							break;
						}
					}
					if (target == 0) {
						String userName = userRepository.findOne((long)n+1).getUserName();;
						nodes.add(map7("id", i, "title", userName, "label",
								userName, "cluster", "1", "value", 1, "group",
								"user", "userId", (long)n + 1));
						target = i;
						i++;
					}
					rels.add(map3("from", source, "to", target, "title", "RELATE"));
				}
			}
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
	
	private Map<String, Object> map5(String key1, Object value1, String key2,
			Object value2, String key3, Object value3, String key4, Object value4,
			String key5, Object value5) {
		Map<String, Object> result = new HashMap<String, Object>(3);
		result.put(key1, value1);
		result.put(key2, value2);
		result.put(key3, value3);
		result.put(key4, value4);
		result.put(key5, value5);
		return result;
	}

	private Map<String, Object> map7(String key1, Object value1, String key2,
			Object value2, String key3, Object value3, String key4,
			Object value4, String key5, Object value5, String key6,
			Object value6, String key7, Object value7) {
		Map<String, Object> result = new HashMap<String, Object>(6);
		result.put(key1, value1);
		result.put(key2, value2);
		result.put(key3, value3);
		result.put(key4, value4);
		result.put(key5, value5);
		result.put(key6, value6);
		result.put(key7, value7);
		return result;
	}
}
