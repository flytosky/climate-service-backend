package models;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public interface ServiceConfigurationItemRepository extends CrudRepository<ServiceConfigurationItem, Long> {
	List<ServiceConfigurationItem> findAllByServiceConfiguration (ServiceConfiguration serviceConfiguration);
	ServiceConfigurationItem findByParameter(Parameter parameter);
}
