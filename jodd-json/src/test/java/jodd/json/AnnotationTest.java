// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.json;

import jodd.json.mock.Location;
import jodd.json.model.App;
import jodd.json.model.User;
import jodd.json.model.UserHolder;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class AnnotationTest {

	@Test
	public void testAnnName() {
		Location location = new Location();

		location.setLatitude(65);
		location.setLongitude(12);

		String json = new JsonSerializer().serialize(location);

		assertEquals("{\"lat\":65,\"lng\":12}", json);

		Location jsonLocation = new JsonParser().parse(json, Location.class);

		assertEquals(location.getLatitude(), jsonLocation.getLatitude());
		assertEquals(location.getLongitude(), jsonLocation.getLongitude());
	}

	@Test
	public void testAnnNameWithClass() {
		Location location = new Location();

		location.setLatitude(65);
		location.setLongitude(12);

		String json = new JsonSerializer().setClassMetadataName("class").serialize(location);

		assertEquals("{\"class\":\"jodd.json.mock.Location\",\"lat\":65,\"lng\":12}", json);

		Location jsonLocation = new JsonParser().setClassMetadataName("class").parse(json, Location.class);

		assertEquals(location.getLatitude(), jsonLocation.getLatitude());
		assertEquals(location.getLongitude(), jsonLocation.getLongitude());
	}

	@Test
	public void testAnnIncludeOfCollection() {
		App app = new App();

		String json = new JsonSerializer().serialize(app);

		assertTrue(json.contains("\"apis\":{}"));
		assertTrue(json.contains("\"name\":\"Hello\""));
	}

	@Test
	public void testClassInArraySerialize() {
		User user = new User();
		user.setId(123);
		user.setName("joe");

		String json = JsonSerializer.create().serialize(user);

		assertTrue(json.contains("123"));
		assertTrue(json.contains("userId"));
		assertFalse(json.contains("joe"));
		assertFalse(json.contains("name"));

		User[] users = new User[] {user};

		json = JsonSerializer.create().serialize(users);

		assertTrue(json.contains("123"));
		assertTrue(json.contains("userId"));
		assertFalse(json.contains("joe"));
		assertFalse(json.contains("name"));

		List<User> usersList = new ArrayList<>();
		usersList.add(user);

		json = JsonSerializer.create().serialize(usersList);

		assertTrue(json.contains("123"));
		assertTrue(json.contains("userId"));
		assertFalse(json.contains("joe"));
		assertFalse(json.contains("name"));
	}

	@Test
	public void testClassInArrayOrMapParse() {
		String json = "{\"userId\" : 123, \"name\":\"Joe\"}";

		User user = JsonParser.create().parse(json, User.class);

		assertEquals(123, user.getId());
		assertNull(user.getName());

		List<User> users = JsonParser.create().map(JsonParser.VALUES, User.class).parse("[" + json + "]");

		assertEquals(1, users.size());
		user = users.get(0);
		assertEquals(123, user.getId());
		assertNull(user.getName());

		Map<String, Object> map = JsonParser.create().map(JsonParser.VALUES, User.class).parse("{ \"user\":" + json + "}");

		assertEquals(1, map.size());
		user = (User) map.get("user");
		assertEquals(123, user.getId());
		assertNull(user.getName());

		UserHolder userHolder = JsonParser.create().parse("{ \"user\":" + json + "}", UserHolder.class);
		assertNotNull(userHolder);
		user = userHolder.getUser();
		assertEquals(123, user.getId());
		assertNull(user.getName());
	}

}