package com.grocery.controller.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.grocery.ShoppingListServiceApplication;
import com.grocery.dto.ShoppingItemDto;
import com.grocery.exception.ErrorMessageDto;
import com.grocery.persistence.model.ShoppingItem;
import com.grocery.persistence.repository.ShoppingItemRepository;

@SpringBootTest(classes = ShoppingListServiceApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class ShoppingListRestControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ShoppingItemRepository shoppingItemRepository;

	private ShoppingItem item1;

	private ShoppingItem item2;

	private static final String BASE_PATH = "/v1/shopping-list";
	private static final int ITEM_ID_1 = 1;
	private static final String ITEM_NAME_1 = "Sugar";
	private static final String COMMENT_1 = "Try another brand";
	private static final int QUANTITY_1 = 3;

	private static final int ITEM_ID_2 = 2;
	private static final String ITEM_NAME_2 = "Salt";
	private static final String COMMENT_2 = "Bring Iodized";
	private static final int QUANTITY_2 = 1;

	private static final int INVALID_ID = 100001;

	private final com.fasterxml.jackson.databind.ObjectMapper mapper = new ObjectMapper();

	@BeforeEach
	void setup() {
		mapper.registerModule(new JSR310Module());
		shoppingItemRepository.deleteAll();
		item1 = shoppingItemRepository.save(new ShoppingItem(ITEM_ID_1, ITEM_NAME_1, COMMENT_1, QUANTITY_1));
	}

	@Test
	public void test_getShoppingList_OK() throws Exception {
		String contentBody = mockMvc
				.perform(MockMvcRequestBuilders.get(BASE_PATH).accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Assertions.assertFalse(StringUtils.isEmpty(contentBody));
		List<ShoppingItemDto> actualShoppingItems = mapper.readValue(contentBody,
				new TypeReference<List<ShoppingItemDto>>() {
				});
		Assertions.assertNotNull(actualShoppingItems);
		Assertions.assertEquals(1, actualShoppingItems.size());
		validateShoppingItem(item1, actualShoppingItems.get(0));
	}

	@Test
	public void test_getShoppingItemById_OK() throws Exception {
		String contentBody = mockMvc
				.perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + item1.getId()).accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Assertions.assertFalse(StringUtils.isEmpty(contentBody));
		ShoppingItemDto actualShoppingItem = mapper.readValue(contentBody, ShoppingItemDto.class);
		Assertions.assertNotNull(actualShoppingItem);
		validateShoppingItem(item1, actualShoppingItem);
	}

	@Test
	public void test_getShoppingItemById_ERR_ITEM_NOT_FOUND() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/" + INVALID_ID).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	void test_deleteShoppingItemById_OK() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete(BASE_PATH + "/" + item1.getId())
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNoContent());
	}

	@Test
	void test_deleteShoppingItemById_ERR_ITEM_NOT_FOUND() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete(BASE_PATH + "/" + INVALID_ID).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	void test_createShoppingItem_BAD_REQUEST_ITEM_NAME_NULL() throws Exception {
		String contentBody = mockMvc
				.perform(MockMvcRequestBuilders.post(BASE_PATH).accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(createShoppingItemDto(null, COMMENT_2, QUANTITY_2))))
				.andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn().getResponse()
				.getContentAsString();
		Assertions.assertFalse(StringUtils.isEmpty(contentBody));
		List<ErrorMessageDto> errors = mapper.readValue(contentBody, new TypeReference<List<ErrorMessageDto>>() {
		});
		Assertions.assertNotNull(errors);
		Assertions.assertNotNull(errors.get(0).getField());
		Assertions.assertEquals("name", errors.get(0).getField());
	}

	@Test
	void test_createShoppingItem_BAD_REQUEST_ITEM_NAME_EMPTY() throws Exception {
		String contentBody = mockMvc
				.perform(MockMvcRequestBuilders.post(BASE_PATH).accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(createShoppingItemDto(" ", COMMENT_2, QUANTITY_2))))
				.andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn().getResponse()
				.getContentAsString();
		Assertions.assertFalse(StringUtils.isEmpty(contentBody));
		List<ErrorMessageDto> errors = mapper.readValue(contentBody, new TypeReference<List<ErrorMessageDto>>() {
		});
		Assertions.assertNotNull(errors);
		Assertions.assertNotNull(errors.get(0).getField());
		Assertions.assertEquals("name", errors.get(0).getField());
	}
	
	@Test
	void test_createShoppingItem_BAD_REQUEST_QUANTITY_THAN_1() throws Exception {
		String contentBody = mockMvc
				.perform(MockMvcRequestBuilders.post(BASE_PATH).accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(createShoppingItemDto(ITEM_NAME_2, COMMENT_2, 0))))
				.andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn().getResponse()
				.getContentAsString();
		Assertions.assertFalse(StringUtils.isEmpty(contentBody));
		List<ErrorMessageDto> errors = mapper.readValue(contentBody, new TypeReference<List<ErrorMessageDto>>() {
		});
		Assertions.assertNotNull(errors);
		Assertions.assertNotNull(errors.get(0).getField());
		Assertions.assertEquals("quantity", errors.get(0).getField());
	}

	@Test
	void test_createShoppingItem_OK() throws Exception {
		item2 = new ShoppingItem(ITEM_ID_2, ITEM_NAME_2, COMMENT_2, QUANTITY_1);
		String contentBody = mockMvc
				.perform(MockMvcRequestBuilders.post(BASE_PATH).accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(createShoppingItemDto(ITEM_NAME_2, COMMENT_2, QUANTITY_1))))
				.andExpect(MockMvcResultMatchers.status().isCreated()).andReturn().getResponse().getContentAsString();
		ShoppingItemDto actualShoppingItem = mapper.readValue(contentBody, ShoppingItemDto.class);
		Assertions.assertNotNull(actualShoppingItem);
		validateShoppingItem(item2, actualShoppingItem);
	}

	@Test
	void test_createShoppingItem_OK_withoutQuantity() throws Exception {
		item2 = new ShoppingItem(ITEM_ID_2, ITEM_NAME_2, COMMENT_2, QUANTITY_2);
		String contentBody = mockMvc
				.perform(MockMvcRequestBuilders.post(BASE_PATH).accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(new ShoppingItemDto().name(ITEM_NAME_2).comment(COMMENT_2))))
				.andExpect(MockMvcResultMatchers.status().isCreated()).andReturn().getResponse().getContentAsString();
		ShoppingItemDto actualShoppingItem = mapper.readValue(contentBody, ShoppingItemDto.class);
		Assertions.assertNotNull(actualShoppingItem);
		validateShoppingItem(item2, actualShoppingItem);
	}

	@Test
	void test_createShoppingItem_ERR_ITEM_ALREADY_EXISTS() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(createShoppingItemDto(ITEM_NAME_1, COMMENT_2, QUANTITY_1))))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	void test_updateShoppingItem_OK() throws Exception {
		item2 = new ShoppingItem(ITEM_ID_2, ITEM_NAME_2, COMMENT_2, QUANTITY_2);
		String contentBody = mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH + "/" + item1.getId()).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(createShoppingItemDto(ITEM_NAME_2, COMMENT_2, QUANTITY_2))))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse().getContentAsString();
		Assertions.assertFalse(StringUtils.isEmpty(contentBody));
		ShoppingItemDto actualShoppingItem = mapper.readValue(contentBody, ShoppingItemDto.class);
		Assertions.assertNotNull(actualShoppingItem);
		validateShoppingItem(item2, actualShoppingItem);
	}
	
	@Test
	void test_updateShoppingItem_BAD_REQUEST_ITEM_NAME_NULL() throws Exception {
		String contentBody =  mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH + "/" + item1.getId()).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(createShoppingItemDto(null, COMMENT_2, QUANTITY_2))))
				.andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn().getResponse()
				.getContentAsString();
		Assertions.assertFalse(StringUtils.isEmpty(contentBody));
		List<ErrorMessageDto> errors = mapper.readValue(contentBody, new TypeReference<List<ErrorMessageDto>>() {
		});
		Assertions.assertNotNull(errors);
		Assertions.assertNotNull(errors.get(0).getField());
		Assertions.assertEquals("name", errors.get(0).getField());
	}
	
	@Test
	void test_updateShoppingItem_BAD_REQUEST_QUANTITY_LESS_THAN_1() throws Exception {
		String contentBody =  mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH + "/" + item1.getId()).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(createShoppingItemDto(ITEM_NAME_2, COMMENT_2, 0))))
				.andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn().getResponse()
				.getContentAsString();
		Assertions.assertFalse(StringUtils.isEmpty(contentBody));
		List<ErrorMessageDto> errors = mapper.readValue(contentBody, new TypeReference<List<ErrorMessageDto>>() {
		});
		Assertions.assertNotNull(errors);
		Assertions.assertNotNull(errors.get(0).getField());
		Assertions.assertEquals("quantity", errors.get(0).getField());
	}
	
	@Test
	void test_updateShoppingItem_ERR_ITEM_DOES_NOT_EXISTS() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH + "/" + item1.getId()).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(createShoppingItemDto(null, COMMENT_2, QUANTITY_2))))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	void test_updateShoppingItem_ERR_ITEM_ALREADY_EXISTS() throws Exception {
		item2 = shoppingItemRepository.save(new ShoppingItem(ITEM_ID_2, ITEM_NAME_2, COMMENT_2, QUANTITY_2));
		mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH + "/" + item1.getId()).accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(createShoppingItemDto(ITEM_NAME_2, COMMENT_2, QUANTITY_2))))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	private ShoppingItemDto createShoppingItemDto(String itemName, String comment, int quantity) {
		return new ShoppingItemDto().name(itemName).comment(comment).quantity(quantity);
	}

	private void validateShoppingItem(ShoppingItem expected, ShoppingItemDto actual) {
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getComment(), actual.getComment());
		assertEquals(expected.getQuantity(), actual.getQuantity());
	}
}
