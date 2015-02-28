package models;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public interface ServiceExecutionLogRepository extends CrudRepository<ServiceExecutionLog, Long>, ServiceExecutionLogRepositoryCustom {
	List<ServiceExecutionLog> findByUser_Id(long userId);
	
	List<ServiceExecutionLog> findByExecutionStartTimeBetweenAndExecutionEndTimeBetween(Date startA, Date startB, Date endA, Date endB);
	List<ServiceExecutionLog> findByPurposeLikeAndClimateService_IdLike(String purpose, long serviceId);

	@Query("SELECT l FROM ServiceExecutionLog l INNER JOIN l.climateService s   WHERE s IN (:services)")
	List<ServiceExecutionLog> findByServiceConfigurationByParam(@Param("services") List<ClimateService> services);



}
