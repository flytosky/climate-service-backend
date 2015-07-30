import static org.junit.Assert.*;
import models.ClimateService;
import models.ServiceConfiguration;
import models.User;

import org.junit.Before;
import org.junit.Test;


public class ServiceConfigurationTest {

	private static long TEST_ID = 0;
	private static ClimateService climateService;
	private static User user;
	private static String TEST_RUNTIME = "runTime";
	
	private ServiceConfiguration serviceConfiguration;
	private ServiceConfiguration serviceConfiguration1;
	
	@Before
	public void setUp() throws Exception{
		serviceConfiguration = new ServiceConfiguration();
		climateService = new ClimateService();
		user = new User();
		serviceConfiguration1 = new ServiceConfiguration(climateService, user, TEST_RUNTIME);
	}
	
	@Test
	public void testId() {
		serviceConfiguration.setId(TEST_ID);
		assertEquals(TEST_ID, serviceConfiguration.getId());
	}
	
	@Test
	public void testClimateService() {
		serviceConfiguration.setClimateservice(climateService);
		assertEquals(climateService, serviceConfiguration.getClimateservice());
	}
	
	@Test
	public void testUser() {
		serviceConfiguration.setUser(user);
		assertEquals(user, serviceConfiguration.getUser());
	}
	
	@Test
	public void testRunTime() {
		serviceConfiguration.setRunTime(TEST_RUNTIME);
		assertEquals(TEST_RUNTIME, serviceConfiguration.getRunTime());
	}

}
