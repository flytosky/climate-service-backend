package models;

import java.util.Enumeration;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


public class Parameter {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@ManyToOne(optional = false)
	@JoinColumn(name = "serviceId", referencedColumnName = "id")
	private ClimateService climateService;
	private String name;
	private Enumeration<String> dataType;
	private String dataRange;
	private String enumeration;
	private String rule;
	private String purpose;
}