package controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
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
	private final DatasetRepository datasetRepository;
	
	@Inject
	public DatasetAndServiceAndUserController(
			final DatasetAndUserRepository datasetAndUserRepository,
			final DatasetRepository datasetRepository) {
		this.datasetAndUserRepository = datasetAndUserRepository;
		this.datasetRepository = datasetRepository;
	}
	
	public Result getAllDatasets(long userId1, long userId2, String format) {
		List<BigInteger> datasets1 = datasetAndUserRepository.findByUserId(userId1);
		List<BigInteger> datasets2 = datasetAndUserRepository.findByUserId(userId2);
		List<BigInteger> datasetIds = new ArrayList<BigInteger>();
		
		for (BigInteger datasetId1 : datasets1) {
			for (BigInteger datasetId2 : datasets2) {
				if (datasetId1 == datasetId2) {
					datasetIds.add(datasetId1);
				}
			}
		}
		
		if (datasetIds.size() == 0) {
			System.out.println("No datasets found");
		}
		
		List<Dataset> datasets = new ArrayList<Dataset>();
		for (BigInteger id : datasetIds) {
			datasets.add(datasetRepository.findOne(id.longValue()));
		}

		String result = new String();
		if (format.equals("json")) {
			result = new Gson().toJson(datasets);
		}

		return ok(result);
	}
	
}
