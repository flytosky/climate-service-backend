package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ServiceExecutionLog {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@ManyToOne(optional = false)
	@JoinColumn(name = "serviceId", referencedColumnName = "id")
	private ClimateService climateService;
	@ManyToOne(optional = false)
	@JoinColumn(name = "userId", referencedColumnName = "id")
	private User user;
	@ManyToOne(optional = false)
	@JoinColumn(name = "serviceConfigurationId", referencedColumnName = "id")
	private ServiceConfiguration serviceConfiguration;
	@ManyToOne(optional = false)
	@JoinColumn(name = "datasetLogId", referencedColumnName = "id")
	private DatasetLog datasetLog;
	private String purpose;
	private Date executionStartTime;
	private Date executionEndTime;
	public ServiceExecutionLog(ClimateService climateService,
			User user, ServiceConfiguration serviceConfiguration, DatasetLog datasetLog,
			String purpose, Date executionStartTime, Date executionEndTime) {
		this.climateService = climateService;
		this.user = user;
		this.serviceConfiguration = serviceConfiguration;
		this.datasetLog = datasetLog;
		this.purpose = purpose;
		this.executionStartTime = executionStartTime;
		this.executionEndTime = executionEndTime;
	}
	public long getId() {
		return id;
	}
	public ClimateService getClimateService() {
		return climateService;
	}
	public User getUser() {
		return user;
	}
	public ServiceConfiguration getServiceConfiguration() {
		return serviceConfiguration;
	}
	public DatasetLog getDatasetLog() {
		return datasetLog;
	}
	public String getPurpose() {
		return purpose;
	}
	public Date getExecutionStartTime() {
		return executionStartTime;
	}
	public Date getExecutionEndTime() {
		return executionEndTime;
	}
	public void setId(long id) {
		this.id = id;
	}
	public void setClimateService(ClimateService climateService) {
		this.climateService = climateService;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public void setServiceConfiguration(ServiceConfiguration serviceConfiguration) {
		this.serviceConfiguration = serviceConfiguration;
	}
	public void setDatasetLog(DatasetLog datasetLog) {
		this.datasetLog = datasetLog;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	public void setExecutionStartTime(Date executionStartTime) {
		this.executionStartTime = executionStartTime;
	}
	public void setExecutionEndTime(Date executionEndTime) {
		this.executionEndTime = executionEndTime;
	}
	@Override
	public String toString() {
		return "ServiceExecutionLog [id=" + id + ", climateService="
				+ climateService + ", user=" + user
				+ ", serviceConfiguration=" + serviceConfiguration
				+ ", datasetLog=" + datasetLog + ", purpose=" + purpose
				+ ", executionStartTime=" + executionStartTime
				+ ", executionEndTime=" + executionEndTime + "]";
	}
	
}
