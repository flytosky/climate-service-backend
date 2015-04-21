package models;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

/**
 * Created by xing on 4/15/15.
 */

@Named
@Singleton
public interface ServiceEntryRepository extends CrudRepository<ServiceEntry, Long> {

    // select c.*, sum(s.count) as totalcount from ClimateService c, ServiceEntry s where c.id=s.serviceId group by s.serviceId order by totalcount desc;
    @Query(value = "select c.* from ClimateService c, ServiceEntry s where c.id=s.serviceId group by s.serviceId order by sum(s.count) desc", nativeQuery = true)
    public List<ClimateService> getClimateServiceOrderByCount();

    @Query(value = "select c.* from ClimateService c, ServiceEntry s where c.id=s.serviceId group by s.serviceId order by s.latestAccessTimeStamp desc", nativeQuery = true)
    public List<ClimateService> getClimateServiceOrderByLatestAccessTime();


}




