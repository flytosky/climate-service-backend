package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ParameterOption {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	@ManyToOne(optional = false)
	@JoinColumn(name = "parameterId", referencedColumnName = "id")
	private Parameter parameter;
	private String parameterValue;
	 
	public ParameterOption() {
	}

	public ParameterOption(Parameter parameter, String parameterValue) {
		super();
		this.parameter = parameter;
		this.parameterValue = parameterValue;
	}

	public Parameter getParameter() {
		return parameter;
	}

	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}

	public String getParameterValue() {
		return parameterValue;
	}

	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}

	public long getId() {
		return id;
	}

	@Override
	public String toString() {
		return "ParameterOption [id=" + id + ", parameter=" + parameter
				+ ", parameterValue=" + parameterValue + "]";
	}
}
