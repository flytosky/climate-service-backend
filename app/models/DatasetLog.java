package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class DatasetLog {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@ManyToOne(optional = false)
	@JoinColumn(name = "serviceExecutionLogId", referencedColumnName = "id")
	private ServiceExecutionLog serviceExecutionLog;
	@ManyToOne(optional = false)
	@JoinColumn(name = "dataSetId", referencedColumnName = "id")
	private Dataset dataset;
	private String plotUrl;
	private String dataUrl;
	@ManyToOne(optional = false)
	@JoinColumn(name = "originalDatasetId", referencedColumnName = "id")
	private Dataset originalDataset;
	@ManyToOne(optional = false)
	@JoinColumn(name = "outputDatasetId", referencedColumnName = "id")
	private Dataset outputDataset;
	
	public DatasetLog() {
		
	}
	
	public DatasetLog(ServiceExecutionLog serviceExecutionLog, Dataset dataset,
			String plotUrl, String dataUrl, Dataset originalDataset,
			Dataset outputDataset) {
		super();
		this.serviceExecutionLog = serviceExecutionLog;
		this.dataset = dataset;
		this.plotUrl = plotUrl;
		this.dataUrl = dataUrl;
		this.originalDataset = originalDataset;
		this.outputDataset = outputDataset;
	}

	public ServiceExecutionLog getServiceExecutionLog() {
		return serviceExecutionLog;
	}

	public void setServiceExecutionLog(ServiceExecutionLog serviceExecutionLog) {
		this.serviceExecutionLog = serviceExecutionLog;
	}

	public Dataset getDataset() {
		return dataset;
	}

	public void setDataSet(Dataset dataset) {
		this.dataset = dataset;
	}

	public String getPlotUrl() {
		return plotUrl;
	}

	public void setPlotUrl(String plotUrl) {
		this.plotUrl = plotUrl;
	}

	public String getDataUrl() {
		return dataUrl;
	}

	public void setDataUrl(String dataUrl) {
		this.dataUrl = dataUrl;
	}

	public Dataset getOriginalDataset() {
		return originalDataset;
	}

	public void setOriginalDataset(Dataset originalDataset) {
		this.originalDataset = originalDataset;
	}

	public Dataset getOutputDataset() {
		return outputDataset;
	}

	public void setOutputDataset(Dataset outputDataset) {
		this.outputDataset = outputDataset;
	}

	public long getId() {
		return id;
	}

	@Override
	public String toString() {
		return "DatasetLog [id=" + id + ", serviceExecutionLog="
				+ serviceExecutionLog + ", dataSet=" + dataset + ", plotUrl="
				+ plotUrl + ", dataUrl=" + dataUrl + ", originalDataSet="
				+ originalDataset + ", outputDataSet=" + outputDataset + "]";
	}
	
	
}
