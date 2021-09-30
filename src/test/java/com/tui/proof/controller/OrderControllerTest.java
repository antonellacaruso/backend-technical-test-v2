package com.tui.proof.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tui.proof.MainApplication;
import com.tui.proof.controller.OrderController;
import com.tui.proof.enums.OrderStatus;
import com.tui.proof.model.Address;
import com.tui.proof.model.Client;
import com.tui.proof.model.Order;
//import com.tui.proof.domain.response.CommonResponse;
import com.tui.proof.repository.OrderRepository;
import com.tui.proof.resource.mapper.OrderMapper;
import com.tui.proof.resource.request.OrderResource;
import com.tui.proof.resource.response.OrderDetails;
import com.tui.proof.service.OrderService;
import com.tui.proof.service.OrderServiceImpl;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import com.fasterxml.jackson.annotation.JsonInclude;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.InjectMocks;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class OrderControllerTest {
	
	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	OrderServiceImpl service;
	
	@Autowired
	OrderRepository orderRepository;


	private static long SIX_MINUTES = Duration.ofMinutes(6).toMinutes();

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
		LocalDateTime dateTime = LocalDateTime.now().minus(Duration.of(6, ChronoUnit.MINUTES));
		Date tmfn = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
		//Date sixMinutesAgo = new Date(System.currentTimeMillis()-SIX_MINUTES);
		orderResource5 = new OrderResource("3", 5, tmfn, 10,"luca", "verdi", "+91 945 8376473", "verdi@gmail.com", "via Settembre 3", "34567", "Venezia", "italy");
		//MvcResult res2= mockMvc.perform(executeRequest(orderResource5, ORDER_API_URL, "4")).andReturn();
		//System.out.println("resssssssssssssssssssss11111111111"+res2.getResponse().getContentAsString());
		orderResource5 = new OrderResource("3", 5, new Date(), 10,"luca", "verdi", "+91 945 8376473", "verdi@gmail.com", "via Settembre 3", "34567", "Venezia", "italy");
		System.out.println("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqq");
		MvcResult res1= mockMvc.perform(executeRequest(orderResource4,  ORDER_URl_UPDATE+ORDER_URl_UPDATE)).andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();
		MockHttpServletResponse response=res1.getResponse();
		System.out.println("resssssssssssssssssssss222222222"+response.getContentAsString());
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

	/*
	 * @Test
	 * 
	 * @WithMockUser public void saveOrder() throws Exception {
	 * 
	 * when(clientRepository.findById(3)).thenReturn(Optional.of(client3)); Address
	 * deliveryAddress = new Address("street 1", "21100", "pavia", "italy"); Order
	 * order = new Order("jkl4r5", deliveryAddress, 5, 50, 3, null, null); String
	 * body = mapper.writeValueAsString(order); MvcResult result =
	 * mvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON_VALUE).
	 * content(body)) .andExpect(status().isOk()).andReturn();
	 * CommonResponse<Map<String, String>> apiResponse =
	 * mapper.readValue(result.getResponse().getContentAsString(), new
	 * TypeReference<CommonResponse<Map<String, String>>>(){});
	 * 
	 * assertTrue(apiResponse.getEntity().equals(Entity.ORDER));
	 * assertTrue(apiResponse.getData() != null &&
	 * !apiResponse.getData().isEmpty()); assertTrue(apiResponse.getData().size() ==
	 * 1); assertTrue(apiResponse.getData().get("orderNumber") != null);
	 * 
	 * }
	 * 
	 * 
	 * 
	 * @Test
	 * 
	 * @WithMockUser public void saveOrder_PilotesNotAllowedException() throws
	 * Exception {
	 * when(clientRepository.findById(3)).thenReturn(Optional.of(client3)); Address
	 * deliveryAddress = new Address("street 1", "21100", "pavia", "italy"); Order
	 * order = new Order("jkl4r5", deliveryAddress, 55, 50, 3, null, null); String
	 * body = mapper.writeValueAsString(order); MvcResult result =
	 * mvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON_VALUE).
	 * content(body)) .andExpect(status().isForbidden()).andReturn(); String
	 * expected = stringFromResource("orders/orderPilotesNotAllowed.json");
	 * JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(),
	 * false); }
	 * 
	 * 
	 * 
	 * @Test
	 * 
	 * @WithMockUser public void saveOrder_OrderTotalNotValid() throws Exception {
	 * when(clientRepository.findById(3)).thenReturn(Optional.of(client3)); Address
	 * deliveryAddress = new Address("street 1", "21100", "pavia", "italy"); Order
	 * order = new Order("jkl4r5", deliveryAddress, 15, -5, 3, null, null); String
	 * body = mapper.writeValueAsString(order); MvcResult result =
	 * mvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON_VALUE).
	 * content(body)) .andExpect(status().isBadRequest()).andReturn(); String
	 * expected = stringFromResource("orders/orderTotalNotValid.json");
	 * JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(),
	 * false); }
	 * 
	 * @Test
	 * 
	 * @WithMockUser public void updateOrder_OK() throws Exception {
	 * when(clientRepository.findById(3)).thenReturn(Optional.of(client3));
	 * when(orderRepository.findById("jkl4r5")).thenReturn(Optional.of(order4));
	 * Address deliveryAddress = new Address("street 1", "21100", "pavia", "italy");
	 * Order orderUpdate = new Order("jkl4r5", deliveryAddress, 15, 150, 3, null,
	 * null); String body = mapper.writeValueAsString(orderUpdate); MvcResult result
	 * = mvc.perform(put("/orders/jkl4r5").contentType(MediaType.
	 * APPLICATION_JSON_VALUE).content(body))
	 * .andExpect(status().isOk()).andReturn(); String expected =
	 * stringFromResource("orders/ordersUpdateResponse.json");
	 * JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(),
	 * false); }
	 * 
	 * @Test
	 * 
	 * @WithMockUser public void updateOrder_OrderNotFound() throws Exception {
	 * when(clientRepository.findById(3)).thenReturn(Optional.of(client3));
	 * when(orderRepository.findById("jkl4r5")).thenReturn(Optional.empty());
	 * Address deliveryAddress = new Address("street 1", "21100", "pavia", "italy");
	 * Order orderUpdate = new Order("jkl4r5", deliveryAddress, 15, 150, 3, null,
	 * null); String body = mapper.writeValueAsString(orderUpdate); MvcResult result
	 * = mvc.perform(put("/orders/jkl4r5").contentType(MediaType.
	 * APPLICATION_JSON_VALUE).content(body))
	 * .andExpect(status().isNotFound()).andReturn(); String expected =
	 * stringFromResource("orders/ordersNotFound.json");
	 * JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(),
	 * false); }
	 * 
	 * @Test
	 * 
	 * @WithMockUser public void updateOrder_UpdateNotAllowed() throws Exception {
	 * when(clientRepository.findById(2)).thenReturn(Optional.of(client2));
	 * when(orderRepository.findById("def321")).thenReturn(Optional.of(order2));
	 * Address deliveryAddress = new Address("street 1", "21100", "pavia", "italy");
	 * Order orderUpdate = new Order("def321", deliveryAddress, 15, 150, 2, null,
	 * null); String body = mapper.writeValueAsString(orderUpdate); MvcResult result
	 * = mvc.perform(put("/orders/def321").contentType(MediaType.
	 * APPLICATION_JSON_VALUE).content(body))
	 * .andExpect(status().isForbidden()).andReturn(); String expected =
	 * stringFromResource("orders/ordersCannotBeUpdated.json");
	 * JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(),
	 * false); }
	 * 
	 * @Test
	 * 
	 * @WithMockUser public void updateOrder_PilotesNotAllowedException() throws
	 * Exception {
	 * when(clientRepository.findById(3)).thenReturn(Optional.of(client3));
	 * when(orderRepository.findById("jkl4r5")).thenReturn(Optional.of(order4));
	 * Address deliveryAddress = new Address("street 1", "21100", "pavia", "italy");
	 * Order orderUpdate = new Order("jkl4r5", deliveryAddress, 13, 150, 3, null,
	 * null); String body = mapper.writeValueAsString(orderUpdate); MvcResult result
	 * = mvc.perform(put("/orders/jkl4r5").contentType(MediaType.
	 * APPLICATION_JSON_VALUE).content(body))
	 * .andExpect(status().isForbidden()).andReturn(); String expected =
	 * stringFromResource("orders/orderPilotesNotAllowed.json");
	 * JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(),
	 * false); }
	 * 
	 * @Test
	 * 
	 * @WithMockUser public void updateOrder_OrderTotalNotValid() throws Exception {
	 * when(clientRepository.findById(3)).thenReturn(Optional.of(client3));
	 * when(orderRepository.findById("pilotes234")).thenReturn(Optional.of(order4));
	 * Address deliveryAddress = new Address("street 1", "21100", "pavia", "italy");
	 * Order orderUpdate = new Order("pilotes234", deliveryAddress, 15, -150, 3,
	 * null, null); String body = mapper.writeValueAsString(orderUpdate); MvcResult
	 * result = mvc.perform(put("/orders/jkl4r5").contentType(MediaType.
	 * APPLICATION_JSON_VALUE).content(body))
	 * .andExpect(status().isBadRequest()).andReturn(); String expected =
	 * stringFromResource("orders/orderTotalNotValid.json");
	 * JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(),
	 * false); }
	 */

}