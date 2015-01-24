
import java.io.IOException;

import com.fasterxml.jackson.databind.node.NullNode;
import netscape.javascript.JSObject;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import play.mvc.*;
import play.test.*;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;


public class ServiceConfigurationTest extends WithApplication {
	@Override
	protected FakeApplication provideFakeApplication() {
		FakeApplication fakeAppWithMemoryDb = fakeApplication(inMemoryDatabase("test"));
		return fakeAppWithMemoryDb;
	};
	
//	@Test
//	public void testBadRoute() {
//	    Result result = route(fakeRequest(GET, "/xx/Kiki"));
//	    assertThat(result).isNull();
//	}
	
	@Test
	public void testUser(){
		Gson gson = new Gson();
		ObjectMapper mapper = new ObjectMapper();		
		
		//Create a new user. /users/add returns the newly created user id as a Long
		JsonNode postJson = mapper.createObjectNode();
		try {
			postJson = mapper.readTree("{\"firstName\":\"John\",\"lastName\":\"Watson\"}");
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Result userCreation = route(fakeRequest(POST, "/users/add").withJsonBody(postJson));
		assertThat(userCreation).isNotNull();
		
		long userID = gson.fromJson(contentAsString(userCreation), Long.class);
		System.out.println(userID);
		
		//Create a new Climate Service 
		try {
			postJson = mapper.readTree("{\"creatorId\":"+userID+",\"name\":\"NightVale\"}");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Result serviceCreation = route(fakeRequest(POST, "/climate/addClimateService").withJsonBody(postJson));
		long serviceID = gson.fromJson(contentAsString(serviceCreation), Long.class);
		System.out.println(serviceID);
	}
}
