package com.grocery.service;

import java.util.List;

import com.grocery.dto.ShoppingItemDto;

/**
 * Interface representing service methods for Shopping Items
 */
public interface ShoppingListService {

    /**
     * Gets all shopping items in the shopping list
     * 
     * @return list of shopping items
     */
    public List<ShoppingItemDto> getShoppingList();

    /**
     * Gets a shopping item by item ID
     * 
     * @param id - the id of the item
     * @return the shopping item
     */
    public ShoppingItemDto getShoppingItemById(Integer id);

    /**
     * Adds new item to the shopping list
     * 
     * @param item - the new item dto
     * @return the shopping item
     */
    public ShoppingItemDto addShoppingItem(ShoppingItemDto item);

    /**
     * Updates the shopping item with the new details
     * 
     * @param item - the updated item dto
     * @param id - the id of the item to be updated
     * @return the updated shopping item
     */
    public ShoppingItemDto updateShoppingItem(ShoppingItemDto item, Integer id);

    /**
     * Deletes the shopping item
     * 
     * @param id - The id of the item to be deleted
     */
    public void removeShoppingItem(Integer id);

}
