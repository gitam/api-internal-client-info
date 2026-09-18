package com.internal.clientinfo.client;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ClientControllerTests {

	@Autowired
	private MockMvc mvc;

	@Test
	void returnsClientById() throws Exception {
		mvc.perform(get("/api/v1/clients/C-1001"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.firstName").value("Alice"))
			.andExpect(jsonPath("$.dateOfBirth").value("1985-03-14"))
			.andExpect(jsonPath("$.address.city").value("London"))
			.andExpect(jsonPath("$.locked").value(false));
	}

	@Test
	void reportsLockedClient() throws Exception {
		mvc.perform(get("/api/v1/clients/C-1002"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("ACTIVE"))
			.andExpect(jsonPath("$.locked").value(true));
	}

	@Test
	void returnsProblemDetailForUnknownClient() throws Exception {
		mvc.perform(get("/api/v1/clients/C-9999"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.detail").value("Client 'C-9999' not found"));
	}

	@Test
	void listsAllClients() throws Exception {
		mvc.perform(get("/api/v1/clients")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(5));
	}

	@Test
	void filtersByStatusAndSegment() throws Exception {
		mvc.perform(get("/api/v1/clients").param("status", "ACTIVE").param("segment", "premium"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[*].id").value(org.hamcrest.Matchers.contains("C-1001", "C-1005")));
	}

	@Test
	void filtersByEmailCaseInsensitively() throws Exception {
		mvc.perform(get("/api/v1/clients").param("email", "BILAL.KHAN@example.com"))
			.andExpect(jsonPath("$[0].id").value("C-1002"))
			.andExpect(jsonPath("$.length()").value(1));
	}

	@Test
	void rejectsInvalidStatus() throws Exception {
		mvc.perform(get("/api/v1/clients").param("status", "BOGUS")).andExpect(status().isBadRequest());
	}

}
