package controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.*;
import util.Common;
import util.Constants;
import workflow.VisTrailJson;
import play.mvc.*;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringEscapeUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

/**
 * The main set of web services.
 */
@Named
@Singleton
public class DatasetAndServiceAndUserController extends Controller {
	
	private final DatasetAndUserRepository datasetAndUserRepository;
	
	@Inject
	public DatasetAndServiceAndUserController(
			final DatasetAndUserRepository datasetAndUserRepository) {
		this.datasetAndUserRepository = datasetAndUserRepository;
	}
	
	public Result getAllDatasets(long userId1, long userId2, String format) {
		List<Dataset> datasets1 = datasetAndUserRepository.findByUserId(userId1);
		List<Dataset> datasets2 = datasetAndUserRepository.findByUserId(userId2);
		List<Dataset> datasets = new ArrayList<Dataset>();
		
		for (Dataset dataset1 : datasets1) {
			for (Dataset dataset2 : datasets2) {
				if (dataset1.equals(dataset2)) {
					datasets.add(dataset1);
				}
			}
		}
		
		if (datasets.size() == 0) {
			System.out.println("No datasets found");
		}

		String result = new String();
		if (format.equals("json")) {
			result = new Gson().toJson(datasets);
		}

		return ok(result);
	}
	
}
