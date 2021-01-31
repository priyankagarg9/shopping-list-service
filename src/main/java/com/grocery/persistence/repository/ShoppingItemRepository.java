package com.grocery.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grocery.persistence.model.ShoppingItem;

/**
 * Interface representing repository methods on ShoppingItem table
 */
public interface ShoppingItemRepository extends JpaRepository<ShoppingItem, Integer> {

    /**
     * Method to find the item by name
     * 
     * @param name - the name of the item
     * @return - optional of the shopping item
     */
    public Optional<ShoppingItem> findByName(String name);

}
