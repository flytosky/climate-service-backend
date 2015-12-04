package models;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public interface ServiceAndDatasetRepository extends CrudRepository<ServiceAndDataset, Long> {

	List<ServiceAndDataset> findByClimateServiceOrderByCountDesc(ClimateService service);
	List<ServiceAndDataset> findByDatasetOrderByCountDesc(Dataset dataset);
	List<ServiceAndDataset> findAll(Sort sort);
	List<ServiceAndDataset> findByClimateServiceAndDataset(ClimateService climateService, Dataset dataset);
}

