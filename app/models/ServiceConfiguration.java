package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


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

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("ServiceConfiguration [id=%d, serviceid=%d, user=%s, runtime=%s]"
				, id, climateservice.getId(), user.getId(), runTime);
	}
	
}