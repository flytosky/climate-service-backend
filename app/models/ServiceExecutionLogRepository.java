package models;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public interface ServiceExecutionLogRepository extends CrudRepository<ServiceExecutionLog, Long> {
    List<ServiceExecutionLog> findByUser_Id(long userId);

    List<ServiceExecutionLog> findByExecutionStartTimeBetweenAndExecutionEndTimeBetweenAndPurposeLikeAndUser_IdAndServiceConfigurationIn(Date startA, Date startB, Date endA, Date endB, String purpose, long userId, Set<ServiceConfiguration> serviceConfigurations);

    List<ServiceExecutionLog> findByExecutionStartTimeBetweenAndExecutionEndTimeBetweenAndPurposeLikeAndServiceConfigurationIn(Date startA, Date startB, Date endA, Date endB, String purpose, Set<ServiceConfiguration> serviceConfigurations);


    List<ServiceExecutionLog> findByExecutionStartTimeBetweenAndExecutionEndTimeBetweenAndPurposeLikeAndUser_Id(Date startA, Date startB, Date endA, Date endB, String purpose, long userId);

    List<ServiceExecutionLog> findByExecutionStartTimeBetweenAndExecutionEndTimeBetweenAndPurposeLike(Date startA, Date startB, Date endA, Date endB, String purpose);

    //	List<ServiceExecutionLog> findByPurposeLikeAndUser_IdLike(String purpose, String userId);
//    List<ServiceExecutionLog> findByPurposeLike()
//	@Query("SELECT l FROM ServiceExecutionLog l INNER JOIN l.ServiceConfiguration c   WHERE c IN (:serviceConfigurations)")  @Param("services")
//	List<ServiceExecutionLog> findByServiceConfigurationIn(List<ServiceConfiguration> serviceConfigurations);
    List<ServiceExecutionLog> findByServiceConfigurationIn(List<ServiceConfiguration> serviceConfigurations);


}
