package models;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public interface DatasetAndUserRepository extends CrudRepository<DatasetAndUser, Long> {
	
	List<DatasetAndUser> findByUserAndDataset(User user, Dataset dataset);
	List<DatasetAndUser> findByUserOrderByCountDesc(User user);
	List<DatasetAndUser> findByDatasetOrderByCountDesc(Dataset dataset);
	List<DatasetAndUser> findAll(Sort sort);
	
	@Query(value = "select datasetId from DatasetAndUser where userId = ?1", nativeQuery = true)
    List<Long> findByUserId(long userId);
}
