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
	List<ServiceExecutionLog> findAllByPurposeAndClimateService(String purpose, ClimateService climateServ);


	//List<ServiceExecutionLog> findByServiceConfiguration_serviceConfigurationItems_Parameter(Parameter param);
}
