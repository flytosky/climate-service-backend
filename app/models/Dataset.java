package models;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;


@Entity
public class Dataset {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String description;
	@ManyToOne(optional = false)
	@JoinColumn(name = "instrumentId", referencedColumnName = "id")
	private Instrument instrument;
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(name = "DatasetAndService", joinColumns = { @JoinColumn(name ="datasetId", referencedColumnName = "id")}, inverseJoinColumns = { @JoinColumn(name = "climateServiceId", referencedColumnName = "id") })
	private List<ClimateService> climateServiceSet;
	private Date publishTimeStamp;
	private String url;

	public Dataset (String description, Instrument instrument, List<ClimateService> climateServiceSet, Date publishTimeStamp, String url) {
		super();
		this.description = description;
		this.instrument = instrument;
		this.climateServiceSet = climateServiceSet;
		this.publishTimeStamp = publishTimeStamp;
		this.url = url;
	}

	public long getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public Instrument getInstrument() {
		return instrument;
	}

	public List<ClimateService> getClimateServiceSet() {
		return climateServiceSet;
	}

	public Date getPublishTimeStamp() {
		return publishTimeStamp;
	}

	public String getUrl() {
		return url;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}

	public void setClimateServiceSet(List<ClimateService> climateServiceSet) {
		this.climateServiceSet = climateServiceSet;
	}

	public void setPublishTimeStamp(Date publishTimeStamp) {
		this.publishTimeStamp = publishTimeStamp;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "Dataset [id=" + id + ", description=" + description
				+ ", instrument=" + instrument + ", climateServiceSet="
				+ climateServiceSet + ", publishTimeStamp=" + publishTimeStamp
				+ ", url=" + url + "]";
	}

	
	
}