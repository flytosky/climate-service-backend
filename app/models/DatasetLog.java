package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class DatasetLog {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String plotUrl;
	private String serviceExecutionLog;
	@ManyToOne(optional = false)
	@JoinColumn(name = "instrumentId", referencedColumnName = "id")
	private Instrument instrument;
	@ManyToOne(optional = false)
	@JoinColumn(name = "originalDataSetId", referencedColumnName = "id")
	private Dataset originalDataSet;
	@ManyToOne(optional = false)
	@JoinColumn(name = "outputDataSetId", referencedColumnName = "id")
	private Dataset outputDataSet;
	
	public DatasetLog(long id, Dataset originalDataSet, Dataset outputDataSet,
			String plotUrl, String serviceExecutionLog, Instrument instrument,
			Dataset dataset) {
		super();
		this.id = id;
		this.originalDataSet = originalDataSet;
		this.outputDataSet = outputDataSet;
		this.plotUrl = plotUrl;
		this.serviceExecutionLog = serviceExecutionLog;
		this.instrument = instrument;
	}
	public long getId() {
		return id;
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

	public Dataset getOriginalDataSet() {
		return originalDataSet;
	}
	public Dataset getOutputDataSet() {
		return outputDataSet;
	}
	public void setOriginalDataSet(Dataset originalDataSet) {
		this.originalDataSet = originalDataSet;
	}
	public void setOutputDataSet(Dataset outputDataSet) {
		this.outputDataSet = outputDataSet;
	}
	public void setId(long id) {
		this.id = id;
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

	
	
}
