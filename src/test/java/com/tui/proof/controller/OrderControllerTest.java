package com.tui.proof.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


import com.tui.proof.repository.OrderRepository;
import com.tui.proof.resource.request.OrderResource;
import com.tui.proof.service.OrderServiceImpl;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.InjectMocks;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)

public class OrderControllerTest {
	
	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	OrderServiceImpl service;
	
	@Autowired
	OrderRepository orderRepository;


	private static Duration SIX_MINUTES = Duration.of(6, ChronoUnit.MINUTES);

	private static final String ORDER_API_URL_BASE = "/api/pilotes/orders";
	private static final String ORDER_URl_UPDATE = "/update";
	private static final String ORDER_URl_CREATE = "/place";
	private static final String ORDER_URl_DELETE = "/delete";


	MockMvc mockMvc;
	OrderResource orderResource1, orderResource2, orderResource3, orderResource4, orderResource5;

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		orderResource1 = new OrderResource("1", 5, new Date(), 10, "mario", "rossi", "+91 934 8376473","rossi@gmail.com", "via Luglio 1", "13300", "roma", "italy");
		orderResource2 = new OrderResource("2", 10, new Date(), 20,"antonio","bianchi", "+91 946 8376473", "bianchimail.com","via Agosto 2", "32200", "milano", "italy");
		orderResource3 = new OrderResource("3", 30, new Date(), 30,"luca", "verdi", "+91 945 8376473", "verdi@gmail.com", "via Settembre 3", "34567", "Venezia", "italy");
		orderResource4 = new OrderResource("3", 5, new Date(), 30,"luca", "verdi", "+91 945 8376473", "verdi@gmail.com", "via Settembre 3", "34567", "Venezia", "italy");
		orderResource5 = new OrderResource("3", 5, new Date(), 10,"luca", "verdi", "+91 945 8376473", "verdi@gmail.com", "via Settembre 3", "34567", "Venezia", "italy");

	}

	@Test
	public void testCreateOrder() throws Exception {
		 mockMvc.perform(executeRequest(orderResource1, ORDER_API_URL_BASE+ORDER_URl_CREATE)).andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();	
	}
	

	@Test
	public void testCreateOrderInvalidInputEmail() throws Exception{
		mockMvc.perform(executeRequest(orderResource2, ORDER_API_URL_BASE+ORDER_URl_CREATE)).
				andExpect(MockMvcResultMatchers.status().is4xxClientError()).andReturn();
	}
	
	@Test
	public void testCreateOrderInvalidInputPilotesNumber() throws Exception{
		mockMvc.perform(executeRequest(orderResource3, ORDER_API_URL_BASE+ORDER_URl_CREATE)).
				andExpect(MockMvcResultMatchers.status().is4xxClientError()).andReturn();
	}
	
	
	@Test
	public void testUpdateOrderInFiveMin() throws Exception {
		 mockMvc.perform(executeRequest(orderResource4, ORDER_URl_UPDATE+ORDER_URl_UPDATE)).andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();	
	}
	
	@Test
	public void testUpdateOrderInFiveMinInputInvalid() throws Exception {
		mockMvc.perform(executeRequest(orderResource2, ORDER_URl_UPDATE+ORDER_URl_UPDATE)).andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();
	}
	
	@Test
	public void testUpdateOrderOverFiveMin() throws Exception {
		LocalDateTime dateTime = LocalDateTime.now().minus(SIX_MINUTES);
		Date tmfn = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
		orderResource5 = new OrderResource("3", 5, tmfn, 10,"luca", "verdi", "+91 945 8376473", "verdi@gmail.com", "via Settembre 3", "34567", "Venezia", "italy");
		orderResource5 = new OrderResource("3", 5, new Date(), 10,"luca", "verdi", "+91 945 8376473", "verdi@gmail.com", "via Settembre 3", "34567", "Venezia", "italy");
		
		MvcResult res1= mockMvc.perform(executeRequest(orderResource4,  ORDER_URl_UPDATE+ORDER_URl_UPDATE)).andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();
				
	}
	
	protected MockHttpServletRequestBuilder executeRequest(OrderResource request,
			String urlTemplate) throws Exception {
		final MockHttpServletRequestBuilder requestBuilder = buildRequestBuilder(request, urlTemplate);
		return requestBuilder;
	}

	protected MockHttpServletRequestBuilder buildRequestBuilder(OrderResource orderResouce, String urlTemplate) throws IOException {
		final MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(urlTemplate);
		request.contentType(MediaType.APPLICATION_JSON);
		request.content(invokeMarshaller(orderResouce));
		return request;
	}

	private byte[]  invokeMarshaller(Object object)throws IOException {
			        ObjectMapper mapper = new ObjectMapper();
		        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		        return mapper.writeValueAsBytes(object);
		    }

	

}