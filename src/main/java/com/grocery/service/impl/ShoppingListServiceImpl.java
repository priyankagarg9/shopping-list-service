package com.grocery.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grocery.dto.ShoppingItemDto;
import com.grocery.exception.ApplicationException;
import com.grocery.persistence.model.ShoppingItem;
import com.grocery.persistence.repository.ShoppingItemRepository;
import com.grocery.service.ShoppingListService;

/**
 * Implementation of {@link ShoppingListService}.
 */
@Service
public class ShoppingListServiceImpl implements ShoppingListService {

    @Autowired
    private ShoppingItemRepository shoppingItemRepository;

    private static final String ITEM_NOT_FOUND = "Shopping item with given id does not exists: ";
    private static final String ITEM_ALREADY_EXISTS = "Shopping item already exists. Try updating the quantity for the item ";

    @Override
    public List<ShoppingItemDto> getShoppingList() {
        return Optional.ofNullable(shoppingItemRepository.findAll())
                       .orElseGet(Collections::emptyList)
                       .stream()
                       .map(item -> mapToShoppingItemDto(item))
                       .collect(Collectors.toList());
    }

    @Override
    public ShoppingItemDto getShoppingItemById(Integer id) {
        ShoppingItem shoppingItem = shoppingItemRepository.findById(id)
                                              .orElseThrow(() -> new ApplicationException(ITEM_NOT_FOUND + id));
        return mapToShoppingItemDto(shoppingItem);
    }

    @Override
    public ShoppingItemDto addShoppingItem(ShoppingItemDto item) {
    	/* Not checking the case here, will consider Sugar and sugar different objects */
        boolean itemExists = shoppingItemRepository.findByName(item.getName()).isPresent();

        if (itemExists) {
            throw new ApplicationException(ITEM_ALREADY_EXISTS + item.getName());
        }

        ShoppingItem newItem = new ShoppingItem();
        newItem.setName(item.getName());
        newItem.setComment(item.getComment());
        newItem.setQuantity(item.getQuantity());
        ShoppingItem createdItem = shoppingItemRepository.save(newItem);
        return mapToShoppingItemDto(createdItem);
    }

    @Override
    public ShoppingItemDto updateShoppingItem(ShoppingItemDto item, Integer id) {
        ShoppingItem shoppingItem = shoppingItemRepository.findById(id)
                                              .orElseThrow(() -> new ApplicationException(ITEM_NOT_FOUND + id));
        
        Optional<ShoppingItem> shoppingItemByName = shoppingItemRepository.findByName(item.getName());

        // if another item with the same name already exists, then throw an exception
        if (shoppingItemByName.isPresent() && shoppingItemByName.get().getId() != id) {
            throw new ApplicationException(ITEM_ALREADY_EXISTS + item.getName());
        }

        shoppingItem.setName(item.getName());
        shoppingItem.setComment(item.getComment());
        shoppingItem.setQuantity(item.getQuantity());
        ShoppingItem updatedItem = shoppingItemRepository.save(shoppingItem);
        return mapToShoppingItemDto(updatedItem);
    }

    @Override
    public void removeShoppingItem(Integer id) {
        ShoppingItem shoppingItem = shoppingItemRepository.findById(id)
                                              .orElseThrow(() -> new ApplicationException(ITEM_NOT_FOUND + id));
        shoppingItemRepository.delete(shoppingItem);

    }

    /* This logic would ideally be present in another layer - mapper */
    private ShoppingItemDto mapToShoppingItemDto(ShoppingItem item) {
        return new ShoppingItemDto().name(item.getName())
        							.comment(item.getComment())
        							.quantity(item.getQuantity())
        							.id(item.getId());
    }

}
