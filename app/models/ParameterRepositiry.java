package models;

import org.springframework.data.repository.CrudRepository;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public interface ParameterRepositiry extends CrudRepository<Parameter, Long> {
	Parameter findByName(String name);
}