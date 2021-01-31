package com.grocery.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.grocery.dto.ShoppingItemDto;
import com.grocery.exception.ApplicationException;
import com.grocery.persistence.model.ShoppingItem;
import com.grocery.persistence.repository.ShoppingItemRepository;
import com.grocery.service.impl.ShoppingListServiceImpl;

@ExtendWith(SpringExtension.class)
public class ShoppingListServiceTest {

	@Mock
	private ShoppingItemRepository shoppingItemRepository;

	private ShoppingListService shoppingListService;

	private static final int ITEM_ID = 1;
	private static final String ITEM_NAME = "Sugar";
	private static final String COMMENT = "Try another brand";
	private static final int QUANTITY = 3;

	@BeforeEach
	public void setUp() {
		shoppingListService = new ShoppingListServiceImpl();
		ReflectionTestUtils.setField(shoppingListService, "shoppingItemRepository", shoppingItemRepository);
	}

	@Test
	void testGetShoppingList_OK() {

		List<ShoppingItem> expectedShoppingItems = new ArrayList<>();
		expectedShoppingItems.add(new ShoppingItem(ITEM_ID, ITEM_NAME, COMMENT, QUANTITY));

		Mockito.when(shoppingItemRepository.findAll()).thenReturn(expectedShoppingItems);
		List<ShoppingItemDto> actualShoppingItems = shoppingListService.getShoppingList();
		assertNotNull(actualShoppingItems);
		assertEquals(expectedShoppingItems.size(), actualShoppingItems.size());
		validateGetShoppingItem(expectedShoppingItems.get(0), actualShoppingItems.get(0));
	}

	@Test
	void testGetShoppingItemById_OK() {

		ShoppingItem expectedShoppingItem = new ShoppingItem(ITEM_ID, ITEM_NAME, COMMENT, QUANTITY);
		Mockito.when(shoppingItemRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(expectedShoppingItem));
		ShoppingItemDto actualShoppingItem = shoppingListService.getShoppingItemById(1);
		assertNotNull(actualShoppingItem);
		validateGetShoppingItem(expectedShoppingItem, actualShoppingItem);
	}

	@Test
	void testGetShoppingItemById_BAD_REQUEST_DOES_NOT_EXISTS() {

		Mockito.when(shoppingItemRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());

		Assertions.assertThrows(ApplicationException.class, () -> {
			shoppingListService.getShoppingItemById(1);
		});
	}

	@Test
	void testRemoveShoppingItemById_BAD_REQUEST_DOES_NOT_EXISTS() {

		Mockito.when(shoppingItemRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());

		Assertions.assertThrows(ApplicationException.class, () -> {
			shoppingListService.removeShoppingItem(1);
		});
	}

	@Test
	void testRemoveShoppingItemById_OK() {

		Mockito.when(shoppingItemRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(new ShoppingItem()));
		Mockito.doNothing().when(shoppingItemRepository).delete(Mockito.any(ShoppingItem.class));
		shoppingListService.removeShoppingItem(1);
	}

	@Test
	void testUpdateShoppingItemById_BAD_REQUEST_DOES_NOT_EXISTS() {

		Mockito.when(shoppingItemRepository.findById(Mockito.anyInt())).thenReturn(Optional.empty());

		Assertions.assertThrows(ApplicationException.class, () -> {
			shoppingListService.updateShoppingItem(new ShoppingItemDto(), 1);
		});
	}

	@Test
	void testUpdateShoppingItemById_ok() {

		Mockito.when(shoppingItemRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(new ShoppingItem()));
		ShoppingItem expectedShoppingItem = new ShoppingItem(ITEM_ID, ITEM_NAME, COMMENT, QUANTITY);
		Mockito.when(shoppingItemRepository.save(Mockito.any(ShoppingItem.class))).thenReturn(expectedShoppingItem);
		shoppingListService.updateShoppingItem(new ShoppingItemDto(), 1);
	}

	@Test
	void testAddShoppingItem_EMAIL_ALREADY_EXISTS() {

		Mockito.when(shoppingItemRepository.findByName(Mockito.anyString()))
				.thenReturn(Optional.of(new ShoppingItem()));

		Assertions.assertThrows(ApplicationException.class, () -> {
			shoppingListService.addShoppingItem(new ShoppingItemDto().name(ITEM_NAME));
		});
	}

	@Test
	void testAddShoppingItem_OK() {
		Mockito.when(shoppingItemRepository.findByName(Mockito.anyString())).thenReturn(Optional.empty());
		ShoppingItem expectedShoppingItem = new ShoppingItem(ITEM_ID, ITEM_NAME, COMMENT, QUANTITY);
		Mockito.when(shoppingItemRepository.save(Mockito.any(ShoppingItem.class))).thenReturn(expectedShoppingItem);
		ShoppingItemDto shoppingItemDto = new ShoppingItemDto().name(ITEM_NAME).comment(COMMENT).quantity(QUANTITY);

		ShoppingItemDto actualItem = shoppingListService.addShoppingItem(shoppingItemDto);

		assertNotNull(actualItem.getId());
		validateAddUpdateShoppingItem(shoppingItemDto, actualItem);
	}

	@Test
	void testAddShoppingItem_OK_withoutQuantity() {
		Mockito.when(shoppingItemRepository.findByName(Mockito.anyString())).thenReturn(Optional.empty());
		ShoppingItem expectedShoppingItem = new ShoppingItem(ITEM_ID, ITEM_NAME, COMMENT, 1);
		Mockito.when(shoppingItemRepository.save(Mockito.any(ShoppingItem.class))).thenReturn(expectedShoppingItem);
		ShoppingItemDto shoppingItemDto = new ShoppingItemDto().name(ITEM_NAME).comment(COMMENT);

		ShoppingItemDto actualItem = shoppingListService.addShoppingItem(shoppingItemDto);

		assertNotNull(actualItem.getId());
		// setting the default quantity in expected dto to validate
		shoppingItemDto.quantity(1);
		validateAddUpdateShoppingItem(shoppingItemDto, actualItem);
	}

	private void validateAddUpdateShoppingItem(ShoppingItemDto expected, ShoppingItemDto actual) {
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getComment(), actual.getComment());
		assertEquals(expected.getQuantity(), actual.getQuantity());
	}

	private void validateGetShoppingItem(ShoppingItem expected, ShoppingItemDto actual) {
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getComment(), actual.getComment());
		assertEquals(expected.getQuantity(), actual.getQuantity());
	}

}
