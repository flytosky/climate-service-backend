package models;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public interface ServiceExecutionLogRepository extends CrudRepository<ServiceExecutionLog, Long> {
	List<ServiceExecutionLog> findByUser_Id(long userId);
	
	List<ServiceExecutionLog> findByExecutionStartTimeBetweenAndExecutionEndTimeBetween(Date startA, Date startB, Date endA, Date endB);
	List<ServiceExecutionLog> findByPurposeAndClimateService_Id(String purpose, long serviceId);


	//List<ServiceExecutionLog> findByServiceConfiguration_serviceConfigurationItems_Parameter(Parameter param);
}
