package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;


@Entity
public class ServiceConfiguration {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@ManyToOne(optional = false)
	@JoinColumn(name = "serviceId", referencedColumnName = "id")
	private ClimateService climateservice;
	@ManyToOne(optional = false)
	@JoinColumn(name = "userId", referencedColumnName = "id")
	private User user;
	private Date runTime;
	
//	@OneToMany(mappedBy="serviceConfiguration")
//	private List<ServiceConfigurationItem> serviceConfigurationItems;
	
	public ServiceConfiguration() {
	}
	
	public ServiceConfiguration(ClimateService climateservice,
			User user,Date runtime){
		super();
		this.climateservice = climateservice;
		this.user = user;
		this.runTime = runtime;
	}

	public long getId() {
		return id;
	}

	public ClimateService getClimateservice() {
		return climateservice;
	}

	public User getUser() {
		return user;
	}

	public Date getRunTime() {
		return runTime;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setClimateservice(ClimateService climateservice) {
		this.climateservice = climateservice;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setRunTime(Date runTime) {
		this.runTime = runTime;
	}

//	public List<ServiceConfigurationItem> getServiceConfigurationItems() {
//		return serviceConfigurationItems;
//	}
//
//	public void setServiceConfigurationItems(
//			List<ServiceConfigurationItem> serviceConfigurationItems) {
//		this.serviceConfigurationItems = serviceConfigurationItems;
//	}

	@Override
	public String toString() {
		return "ServiceConfiguration [id=" + id + ", climateservice="
				+ climateservice + ", user=" + user + ", runTime=" + runTime
				+ "]";
	}

	
}