package com.grocery.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "shopping_item")
public class ShoppingItem {

	@Id
	@Column(columnDefinition = "INT(11) UNSIGNED", name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(unique = true, nullable = false)
	private String name;

	@Column(name = "comment")
	private String comment;

	@Column(name = "quantity", nullable = false)
	private int quantity;

	public ShoppingItem() {
	}

	public ShoppingItem(Integer id, String name, String comment, int quantity) {
		super();
		this.id = id;
		this.name = name;
		this.comment = comment;
		this.quantity = quantity;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "ShoppingItem [id=" + id + ", name=" + name + ", comment=" + comment + ", quantity=" + quantity + "]";
	}

}
