
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
		System.out.println("REQUEST User create");
		System.out.println(postJson);
		assertThat(userCreation).isNotNull();
		System.out.println("RESULT");
		System.out.println(userCreation);
		long userID = gson.fromJson(contentAsString(userCreation), Long.class);

		JsonNode postJson2 = mapper.createObjectNode();
		//Create a new Climate Service 
		try {
			postJson = mapper.readTree("{\"creatorId\":\""+userID+"\",\"name\":\"NightVale\"}");
			postJson2 = mapper.readTree("{\"creatorId\":\""+userID+"\",\"name\":\"testName\",\"purpose\":\"For testing\",\"url\":\"http://einstein.sv.cmu.edu:9008/forTesting\",\"scenario\":\"Used only for testing\",\"versionNo\":\"1\",\"rootServiceId\":\"1\"}]");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("REQUEST Service Create ");
		System.out.println(postJson2);
	
		Result serviceCreation = route(fakeRequest(POST, "/climate/addClimateService").withJsonBody(postJson));
		
		System.out.println("RESULT");
		System.out.println(contentAsString(serviceCreation));
		
		Result serviceCreation2 = route(fakeRequest(POST, "/climate/addClimateService").withJsonBody(postJson2));
		System.out.println("RESULT Service Create2");
		System.out.println(contentAsString(serviceCreation2));
		
		long serviceID = gson.fromJson(contentAsString(serviceCreation2), Long.class);
			
		Result serviceGetname = route(fakeRequest(GET, "/climate/getClimateService/NightVale/json"));
		System.out.println("RESULT get service by name");
		System.out.println(contentAsString(serviceGetname));
		
		Result serviceGetAll = route(fakeRequest(GET, "/climate/getAllClimateServices/json"));
		System.out.println("RESULT get service (all)");
		System.out.println(contentAsString(serviceGetAll));

		Result serviceGetId = route(fakeRequest(GET, "/climate/getClimateService/id/"+serviceID));
		System.out.println("RESULT get service by name");
		System.out.println(contentAsString(serviceGetId));
		
	}
}
