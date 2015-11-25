package controllers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import models.User;
import models.UserRepository;
import play.mvc.*;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.JDBCDataModel;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import com.google.gson.Gson;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * The main set of web services.
 */
@Named
@Singleton
public class UserRecommendationController extends Controller {
	
	class UserWrapper {
		public User user;
		public double sim;
		
		public UserWrapper() {
			
		}
		
		public UserWrapper(User user, double sim) {
			this.user = user;
			this.sim = sim;
		}
	}

	private final UserRepository userRepository;
	
	@Inject
	public UserRecommendationController(final UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
 	public Result getKSimilarUsers(Long id, int k, String format) {
 		if (id == null) {
 			System.out.println("User id is null or empty!");
 			return badRequest("User id is null or empty!");
 		}
		
 		MysqlDataSource dataSource = new MysqlDataSource();
 		dataSource.setServerName("einstein.sv.cmu.edu");
 		dataSource.setUser("root");
 		dataSource.setPassword("root");
 		dataSource.setDatabaseName("climateService");

 		JDBCDataModel model = new MySQLJDBCDataModel(
 		    dataSource, "ServiceAndUser", "userId",
 		    "serviceId", "count", "");
 		
 		String result = new String();
 		
 		try {
 			UserSimilarity similarity = new PearsonCorrelationSimilarity(model);

 	 		Queue<UserWrapper> queue = new PriorityQueue<UserWrapper>(k, new Comparator<UserWrapper>() {
 	 			@Override
 	 			public int compare(UserWrapper uw1, UserWrapper uw2) {
 	 				return (int) (uw1.sim - uw2.sim);
 	 			}
 	 		});
 	 		for (User user : userRepository.findAll()) {
 	 			if (user.getId() == id) {
 	 				continue;
 	 			}
 	 			UserWrapper uw = new UserWrapper(user, similarity.userSimilarity(id, user.getId()));
 	 			if (queue.size() < k) {
 	 				queue.add(uw);
 	 			} else {
 	 				if (uw.sim >= queue.peek().sim) {
 	 	 				queue.poll();
 	 	 				queue.add(uw);
 	 	 			}
 	 			}
 	 		}

 	 		List<User> ret = new ArrayList<User>();
 	 		while (!queue.isEmpty()) {
 	 			UserWrapper temp = queue.poll();
 	 			ret.add(temp.user);
 	 		}

 	 		if (format.equals("json")) {
 	 			result = new Gson().toJson(ret);
 	 		}

 		} catch (Exception e) {
 			return badRequest("User similiarity exception!");
 		}

 		return ok(result);
 	}
}
