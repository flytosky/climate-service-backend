package models;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public interface ServiceExecutionLogRepository extends CrudRepository<ServiceExecutionLog, Long> {
	List<ServiceExecutionLog> findAllByUserId(User user);
	
	List<ServiceExecutionLog> findAllByExecutionStartTimeBetweenAndExecutionEndTimeBetween(Date startA, Date startB, Date endA, Date endB);
	List<ServiceExecutionLog> findAllByPurposeAndClimateServiceAndDatasetLog(String purpose, ClimateService climateServ, DatasetLog dataset);
	
	List<ServiceExecutionLog> findAllByServiceConfiguration_ServiceConfigurationItemsParameter(Parameter param);
}
