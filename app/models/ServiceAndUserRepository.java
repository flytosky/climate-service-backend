package models;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public interface ServiceAndUserRepository extends CrudRepository<ServiceAndUser, Long> {

	List<ServiceAndUser> findByUserOrderByCountDesc(User user);
	List<ServiceAndUser> findByClimateServiceOrderByCountDesc(ClimateService service);
	List<ServiceAndUser> findAll(Sort sort);
	List<ServiceAndUser> findByUserAndClimateService(User user, ClimateService climateService);
	
	@Query(value = "select * from ServiceAndUser group by userId", nativeQuery = true)
	List<ServiceAndUser> findDinstinctUsers();
}

