package models;

import org.springframework.data.repository.CrudRepository;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public interface ServiceAndDatasetRepository extends CrudRepository<ServiceAndDataset, Long> {

	Iterable<ServiceAndDataset> findByClimateService(ClimateService service);
		
}

