package com.grocery.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.grocery.dto.ShoppingItemDto;
import com.grocery.service.ShoppingListService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = {"v1/shopping-list"})
@Api(tags = {"shopping-list"})
public class ShoppingListController {

    @Autowired
    private ShoppingListService shoppingListService;

    @GetMapping()
    @ResponseStatus(code = HttpStatus.OK)
    @ApiOperation(value = "Gets a list of all shopping items",
                  response = ShoppingItemDto.class,
                  responseContainer = "List",
                  produces = "application/json")
    public List<ShoppingItemDto> getShoppingList() {
        return shoppingListService.getShoppingList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    @ApiOperation(value = "Gets shopping item by id",
                  response = ShoppingItemDto.class,
                  produces = "application/json")
    public ShoppingItemDto getShoppingItemById(@ApiParam(value = "Shopping item ID", required = true) @PathVariable(value = "id") Integer id) {
        return shoppingListService.getShoppingItemById(id);
    }

    @PostMapping()
    @ResponseStatus(code = HttpStatus.CREATED)
    @ApiOperation(value = "Adds a shopping item to the existing list",
                  consumes = "application/json", 
                  response = ShoppingItemDto.class,
                  produces = "application/json")
    public ShoppingItemDto addShoppingItem(@ApiParam(value = "ShoppingItem data") @Valid @RequestBody ShoppingItemDto item) {
        item.trim();
        return shoppingListService.addShoppingItem(item);
    }

    @PutMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    @ApiOperation(value = "Updates shopping item information",
                  consumes = "application/json",
                  response = ShoppingItemDto.class,
                  produces = "application/json")
    public ShoppingItemDto updateShoppingItem(@ApiParam(value = "Shopping Item data") @Valid @RequestBody ShoppingItemDto item,
                               @ApiParam(value = "ShoppingItem ID", required = true) @PathVariable(value = "id") Integer id) {
        item.trim();
        return shoppingListService.updateShoppingItem(item, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @ApiOperation(value = "Deletes an item from the shopping list")
    public void removeShoppingItem(@ApiParam(value = "Shopping item ID", required = true) @PathVariable(value = "id") Integer id) {
        shoppingListService.removeShoppingItem(id);
    }
}
