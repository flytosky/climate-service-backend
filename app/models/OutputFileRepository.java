package models;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public interface OutputFileRepository extends CrudRepository<OutputFile, Long> {
	List<OutputFile> findAll();
}
