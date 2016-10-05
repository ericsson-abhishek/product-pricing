package io.globomart.prodpricing.dto;

public class Pricing {
	private Integer pricingId;
	private int productId;
	private int supplierId;
	private double price;

	public Pricing() {
	}

	public Pricing(int productId, int supplierId, double price) {
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
}
