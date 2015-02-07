package controllers;

import java.text.SimpleDateFormat;
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
import models.Dataset;
import models.DatasetRepository;
import models.Instrument;
import models.InstrumentRepository;
import play.mvc.*;

@Named
@Singleton
public class DatasetController extends Controller {
	
	private final ClimateServiceRepository climateServiceRepository;
	private final InstrumentRepository instrumentRepository;
	private final DatasetRepository datasetRepository;
	
	@Inject
	public DatasetController(ClimateServiceRepository climateServiceRepository, InstrumentRepository instrumentRepository, DatasetRepository datasetRepository) {
		this.climateServiceRepository = climateServiceRepository;
		this.instrumentRepository = instrumentRepository;
		this.datasetRepository = datasetRepository;
	}
	
	public Result addDataset() {
		JsonNode json = request().body().asJson();
    	if (json == null) {
    		System.out.println("Dataset not saved, expecting Json data");
			return badRequest("Dataset not saved, expecting Json data");
    	}
    	String description = json.findPath("description").asText();
    	long instrumentId = json.findPath("instrumentId").asLong();
    	String url = json.findPath("url").asText();
    	String publishTimeStampString = json.findPath("publishTimeStamp").asText();
    	
    	Date publishTimeStamp = new Date();
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(util.Common.DATE_PATTERN);
    	JsonNode ClimateServices = json.findPath("ServiesId");
    	List<Long> climateServicesId = new ArrayList<Long>();
    	for(int i = 0; i < ClimateServices.size(); i++) {
    		climateServicesId.add(ClimateServices.get(i).asLong());
    	}
    	try {
    		publishTimeStamp = simpleDateFormat.parse(publishTimeStampString);
    	} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Wrong Date Format :" + publishTimeStampString);
			return badRequest("Wrong Date Format :" + publishTimeStampString);
		}
    	try {
			Instrument instrument = instrumentRepository.findOne(instrumentId);
			List<ClimateService>climateServiceSet = new ArrayList<ClimateService>();
			for(int i=0;i<climateServicesId.size();i++) {
				climateServiceSet.add(climateServiceRepository.findOne(climateServicesId.get(i)));
			}
			Dataset dataset = new Dataset(description, instrument,climateServiceSet , publishTimeStamp, url);
			Dataset savedServiceConfiguration = datasetRepository.save(dataset);
			System.out.println("Service Configuration saved: "+ savedServiceConfiguration.getId());
			return created(new Gson().toJson(dataset.getId()));
		} catch (PersistenceException pe) {
			pe.printStackTrace();
			System.out.println("Dataset not created");
			return badRequest("Dataset Configuration not created");
		}
    	
	}
	
    public Result updateDatasetById(long id) {
		JsonNode json = request().body().asJson();
		if (json == null) {
			System.out.println("Dataset not saved, expecting Json data");
			return badRequest("Dataset Configuration not saved, expecting Json data");
		}
		String description = json.findPath("description").asText();
		long instrumentId = json.findPath("instrumentId").asLong();
		String url = json.findPath("url").asText();
		String publishTimeStampString = json.findPath("publishTimeStamp").asText();
		
		Date publishTimeStamp = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yyyy");
		JsonNode ClimateServices = json.findPath("ServiesId");
		List<Long> climateServicesId = new ArrayList<Long>();
		for(int i = 0; i < ClimateServices.size(); i++) {
			climateServicesId.add(ClimateServices.get(i).asLong());
		}
		try {
			publishTimeStamp = simpleDateFormat.parse(publishTimeStampString);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		try {
			Dataset dataset = datasetRepository.findOne(id);
			dataset.setId(id);
			Instrument instrument = instrumentRepository.findOne(instrumentId);
			dataset.setInstrument(instrument);
			dataset.setDescription(description);
			dataset.setUrl(url);
			dataset.setPublishTimeStamp(publishTimeStamp);
			List<ClimateService>climateServiceSet = new ArrayList<ClimateService>();
			for(int i=0;i<climateServicesId.size();i++) {
				climateServiceSet.add(climateServiceRepository.findOne(climateServicesId.get(i)));
			}
			dataset.setClimateServiceSet(climateServiceSet);
			Dataset savedDataset = datasetRepository.save(dataset);
			
			System.out.println("Dataset updated: "+ savedDataset.getId());
			return created("Dataset updated: "+ savedDataset.getId());
		} catch (PersistenceException pe) {
			pe.printStackTrace();
			System.out.println("Dataset not saved: "+id);
			return badRequest("Dataset not saved: "+id);
		}			
    }

	
    public Result deleteDataset(long id) {
    	Dataset dataset = datasetRepository.findOne(id);
    	if (dataset == null) {
    		System.out.println("Dataset not found with id: " + id);
			return notFound("Dataset not found with id: " + id);
    	}
    	
    	datasetRepository.delete(dataset);
    	System.out.println("Dataset is deleted: " + id);
		return ok("Dataset is deleted: " + id);
    }
    public Result getDataset(long id, String format) {
    	Dataset dataset = datasetRepository.findOne(id);
    	if (dataset == null) {
    		System.out.println("Dataset not found with name: " + id);
			return notFound("Dataset not found with name: " + id);
    	}
    	
    	String result = new String();
    	if (format.equals("json")) {
    		result = new Gson().toJson(dataset);
    	}
    	
    	return ok(result);
    }

    
    public Result getAllDataset() {
    	try {
    		Iterable<Dataset>datasets =  datasetRepository.findAll();
    		String result = new String();
    		result = new Gson().toJson(datasets);
    		return ok(result);
    	} catch (Exception e) {
    		return badRequest("Service Configurations not found");
    	}
    }
	
}