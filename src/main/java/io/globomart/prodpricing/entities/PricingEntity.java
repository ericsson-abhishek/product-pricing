package io.globomart.prodpricing.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Product_Pricing")
public class PricingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer pricingId;
	public Integer getPricingId() {
		return pricingId;
	}

	public void setPricingId(Integer pricingId) {
		this.pricingId = pricingId;
	}

	private int productId;
	private int supplierId;
	private double price;

	public PricingEntity() {
	}

	public PricingEntity(int productId, int supplierId, double price) {
		this.productId = productId;
		this.setSupplierId(supplierId);
		this.setPrice(price);
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public int getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	

//	@Override
//	public String toString() {
//		StringBuilder builder = new StringBuilder();
//		builder.append("ProductEntity [productId=");
//		builder.append(productId);
//		builder.append(", supplierId=");
//		builder.append(supplierId);
//		builder.append(", model=");
//		builder.append(model);
//		builder.append(", color=");
//		builder.append(color);
//		builder.append("]");
//		return builder.toString();
//	}

}