package models;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public interface ClimateServiceRepository extends CrudRepository<ClimateService, Long> {
	List<ClimateService> findAllByName(String name);

	ClimateService findFirstByName(String oldName);
}
