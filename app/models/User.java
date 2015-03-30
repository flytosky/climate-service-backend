package models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.Hibernate;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    private String firstName;
    private String lastName;
    
//    @OneToMany(orphanRemoval=true, mappedBy = "user", cascade={CascadeType.MERGE, CascadeType.REMOVE})
//    private Set<ClimateService> climateServices = new HashSet<ClimateService>();
    
    public User() {
//        Hibernate.initialize(climateServices);
    }

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
//        Hibernate.initialize(climateServices);
    }

    @Override
    public String toString() {
        return String.format(
                "Customer[id=%d, firstName='%s', lastName='%s']",
                id, firstName, lastName);
    }
    
    public long getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

//	public Set<ClimateService> getClimateServices() {
//		return climateServices;
//	}
//
//	public void setClimateServices(Set<ClimateService> climateServices) {
//		this.climateServices = climateServices;
//	}
//	
//	public void addClimateService(ClimateService climateService) {
//		climateService.setUser(this);
//		this.climateServices.add(climateService);
//	}
}

