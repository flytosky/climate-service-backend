package controllers;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.persistence.PersistenceException;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

import models.Dataset;
import models.DatasetLog;
import models.DatasetLogRepository;
import models.DatasetRepository;
import models.Instrument;
import models.InstrumentRepository;
import play.mvc.*;

@Named
@Singleton
public class DatasetLogController extends Controller {
	
	private final InstrumentRepository instrumentRepository;
	private final DatasetLogRepository datasetLogRepository;
	private final DatasetRepository datasetRepository;
	
	@Inject
	public DatasetLogController(DatasetRepository datasetRepository, 
			InstrumentRepository instrumentRepository, DatasetLogRepository datasetLogRepository) {
		this.instrumentRepository = instrumentRepository;
		this.datasetLogRepository = datasetLogRepository;
		this.datasetRepository = datasetRepository;
	}
	
	public Result addDatasetLog() {
		JsonNode json = request().body().asJson();
    	if (json == null) {
    		System.out.println("DatasetLog not saved, expecting Json data");
			return badRequest("DatasetLog not saved, expecting Json data");
    	}
    	String plotUrl = json.findPath("plotUrl").asText();
    	long instrumentId = json.findPath("instrumentId").asLong();
    	long originalDatasetId = json.findPath("originalDatasetId").asLong();
    	long outputDatasetId = json.findPath("outputDatasetId").asLong();
    	String serviceExecutionLog = json.findPath("serviceExecutionLog").asText();
    	
    	try {
			Instrument instrument = instrumentRepository.findOne(instrumentId);
			Dataset originalDataset = datasetRepository.findOne(originalDatasetId);
			Dataset outputDataset = datasetRepository.findOne(outputDatasetId);
			DatasetLog datasetLog = new DatasetLog(originalDataset, outputDataset, plotUrl, serviceExecutionLog, instrument);
			DatasetLog saveddatasetLog = datasetLogRepository.save(datasetLog);
			System.out.println("DatasetLog saved: "+ saveddatasetLog.getId());
			return created(new Gson().toJson(datasetLog.getId()));
		} catch (PersistenceException pe) {
			pe.printStackTrace();
			System.out.println("DatasetLog not created");
			return badRequest("DatasetLog Configuration not created");
		}
    	
	}
	
    public Result updateDatasetLogById(long id) {
	    JsonNode json = request().body().asJson();
		if (json == null) {
			System.out.println("DatasetLog not saved, expecting Json data");
			return badRequest("DatasetLog Configuration not saved, expecting Json data");
		}
		String plotUrl = json.findPath("plotUrl").asText();
		long instrumentId = json.findPath("instrumentId").asLong();
		long originalDatasetId = json.findPath("originalDatasetId").asLong();
		long outputDatasetId = json.findPath("outputDatasetId").asLong();
		String serviceExecutionLog = json.findPath("serviceExecutionLog").asText();

		try {
			Instrument instrument = instrumentRepository.findOne(instrumentId);
			Dataset originalDataset = datasetRepository.findOne(originalDatasetId);
			Dataset outputDataset = datasetRepository.findOne(outputDatasetId);
			DatasetLog datasetLog = datasetLogRepository.findOne(id);
			datasetLog.setInstrument(instrument);
			datasetLog.setPlotUrl(plotUrl);
			datasetLog.setOriginalDataSet(originalDataset);
			datasetLog.setOutputDataSet(outputDataset);
			datasetLog.setServiceExecutionLog(serviceExecutionLog);
			DatasetLog savedDatasetLog = datasetLogRepository.save(datasetLog);
			
			System.out.println("DatasetLog updated: "+ savedDatasetLog.getId());
			return created("DatasetLog updated: "+ savedDatasetLog.getId());
		} catch (PersistenceException pe) {
			pe.printStackTrace();
			System.out.println("DatasetLog not saved: "+id);
			return badRequest("DatasetLog not saved: "+id);
		}			
    }

	
    public Result deleteDatasetLog(long id) {
    	DatasetLog datasetLog = datasetLogRepository.findOne(id);
    	if (datasetLog == null) {
    		System.out.println("DatasetLog not found with id: " + id);
			return notFound("DatasetLog not found with id: " + id);
    	}
    	
    	datasetLogRepository.delete(datasetLog);
    	System.out.println("DatasetLog is deleted: " + id);
		return ok("DatasetLog is deleted: " + id);
    }
    public Result getDatasetLog(long id, String format) {
    	DatasetLog datasetLog = datasetLogRepository.findOne(id);
    	if (datasetLog == null) {
    		System.out.println("DatasetLog not found with name: " + id);
			return notFound("DatasetLog not found with name: " + id);
    	}
    	
    	String result = new String();
    	if (format.equals("json")) {
    		result = new Gson().toJson(datasetLog);
    	}
    	
    	return ok(result);
    }

    
    public Result getAllDatasetLog() {
    	try {
    		Iterable<DatasetLog>datasetLogs =  datasetLogRepository.findAll();
    		String result = new String();
    		result = new Gson().toJson(datasetLogs);
    		return ok(result);
    	} catch (Exception e) {
    		return badRequest("Service Configurations not found");
    	}
    }
	
}