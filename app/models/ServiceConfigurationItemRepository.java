package models;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public interface ServiceConfigurationItemRepository extends CrudRepository<ServiceConfigurationItem, Long> {
	
	List<ServiceConfigurationItem> findByServiceConfiguration_Id (long serviceConfigurationId);
    
	ServiceConfigurationItem findByParameter (Parameter parameter);
    
	List<ServiceConfigurationItem> findByParameterAndValue (Parameter parameter, String value);

}
