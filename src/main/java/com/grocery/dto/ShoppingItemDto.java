package com.grocery.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModelProperty.AccessMode;

@ApiModel(value = "ShoppingItem")
public class ShoppingItemDto {

	@ApiModelProperty(value = "Shopping item ID.", accessMode = AccessMode.READ_ONLY)
	private Integer id;

	@NotBlank
	@ApiModelProperty(value = "Name of the item.", required = true, example = "Sugar")
	private String name;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value = "Comment for the item.", required = false, example = "Try another brand")
	private String comment;

	@Min(1)
	@ApiModelProperty(value = "Quantity for the item. Will be set to 1 by default if not provided", required = false, example = "1")
	private int quantity = 1;

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getComment() {
		return comment;
	}

	public int getQuantity() {
		return quantity;
	}

	public ShoppingItemDto id(Integer id) {
		this.id = id;
		return this;
	}

	public ShoppingItemDto name(String name) {
		this.name = name;
		return this;
	}

	public ShoppingItemDto comment(String comment) {
		this.comment = comment;
		return this;
	}

	public ShoppingItemDto quantity(int quantity) {
		this.quantity = quantity;
		return this;
	}

	public void trim() {
		name = StringUtils.trimWhitespace(name);
		comment = StringUtils.trimWhitespace(comment);
	}

	@Override
	public String toString() {
		return "ShoppingItemDto [id=" + id + ", name=" + name + ", comment=" + comment + ", quantity=" + quantity + "]";
	}

}
