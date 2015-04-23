package models;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public interface DatasetRepository extends CrudRepository<Dataset, Long> {
	List<Dataset> findByNameLikeAndAgencyIdLikeAndGridDimensionLikeAndInstrument_Id(String name, String agencyId, String gridDimension, long instrumentId);
	List<Dataset> findByNameLikeAndAgencyIdLikeAndGridDimensionLike(String name, String agencyId, String gridDimension);
}