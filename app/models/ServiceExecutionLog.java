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
	private long serviceConfigurationId;
	@ManyToOne(optional = false)
	@JoinColumn(name = "datasetId", referencedColumnName = "id")
	private Dataset dataset;
	private String purpose;
	private Date executionStartTime;
	private Date executionEndTime;
	public ServiceExecutionLog(long id, ClimateService climateService,
			User user, long serviceConfigurationId, Dataset dataset,
			String purpose, Date executionStartTime, Date executionEndTime) {
		super();
		this.id = id;
		this.climateService = climateService;
		this.user = user;
		this.serviceConfigurationId = serviceConfigurationId;
		this.dataset = dataset;
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
	public long getServiceConfigurationId() {
		return serviceConfigurationId;
	}
	public Dataset getDataset() {
		return dataset;
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
	public void setServiceConfigurationId(long serviceConfigurationId) {
		this.serviceConfigurationId = serviceConfigurationId;
	}
	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
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
				+ ", serviceConfigurationId=" + serviceConfigurationId
				+ ", dataset=" + dataset + ", purpose=" + purpose
				+ ", executionStartTime=" + executionStartTime
				+ ", executionEndTime=" + executionEndTime + "]";
	}
	
}
