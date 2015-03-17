package models;

import org.springframework.data.repository.CrudRepository;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public interface ParameterRepository extends CrudRepository<Parameter, Long> {
	Parameter findByName(String name);
	Parameter findByNameAndClimateService(String name, ClimateService climateService);
}