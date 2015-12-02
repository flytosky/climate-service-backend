package models;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Provides CRUD functionality for accessing people. Spring Data auto-magically takes care of many standard
 * operations here.
 */
@Named
@Singleton
public interface UserRepository extends CrudRepository<User, Long> {
	List<User> findByUserName(String userName);
	User findByEmail(String email);
	
	@Query(value = "select u.* from User u where u.username = ?1", nativeQuery = true)
	User findByUsername(String username);
    @Query(value = "select u.userName from User u where u.email = ?1", nativeQuery = true)
	String getUserNameByEmail(String email);
    @Query(value = "select u.id from User u where u.email = ?1", nativeQuery = true)
	Long getUserIdByEmail(String email);
    @Query(value = "select u.userName from User u where u.id = ?1", nativeQuery = true)
	String getUserNameById(Long id);
    @Query(value = "select u.userName from User u", nativeQuery = true)
	List<String> getAllUserName();
    @Query(value = "select u.unreadMention from User u where u.email = ?1", nativeQuery = true)
	boolean getHasUnreadMentionByEmail(String email);
}
