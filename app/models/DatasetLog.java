package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class DatasetLog {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private long originalDataSetId;
	private long outputDataSetId;
	private String plotUrl;
	private String serviceExecutionLog;
	@ManyToOne(optional = false)
	@JoinColumn(name = "instrumentId", referencedColumnName = "id")
	private Instrument instrument;
	@ManyToOne(optional = false)
	@JoinColumn(name = "datasetId", referencedColumnName = "id")
	private Dataset dataset;
	public DatasetLog(long id, long originalDataSetId, long outputDataSetId,
			String plotUrl, String serviceExecutionLog, Instrument instrument,
			Dataset dataset) {
		super();
		this.id = id;
		this.originalDataSetId = originalDataSetId;
		this.outputDataSetId = outputDataSetId;
		this.plotUrl = plotUrl;
		this.serviceExecutionLog = serviceExecutionLog;
		this.instrument = instrument;
		this.dataset = dataset;
	}
	public long getId() {
		return id;
	}
	public long getOriginalDataSetId() {
		return originalDataSetId;
	}
	public long getOutputDataSetId() {
		return outputDataSetId;
	}
	public String getPlotUrl() {
		return plotUrl;
	}
	public String getServiceExecutionLog() {
		return serviceExecutionLog;
	}
	public Instrument getInstrument() {
		return instrument;
	}
	public Dataset getDataset() {
		return dataset;
	}
	public void setId(long id) {
		this.id = id;
	}
	public void setOriginalDataSetId(long originalDataSetId) {
		this.originalDataSetId = originalDataSetId;
	}
	public void setOutputDataSetId(long outputDataSetId) {
		this.outputDataSetId = outputDataSetId;
	}
	public void setPlotUrl(String plotUrl) {
		this.plotUrl = plotUrl;
	}
	public void setServiceExecutionLog(String serviceExecutionLog) {
		this.serviceExecutionLog = serviceExecutionLog;
	}
	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}
	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}
	
	
}
