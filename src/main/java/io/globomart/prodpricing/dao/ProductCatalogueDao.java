package io.globomart.prodpricing.dao;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ProductCatalogueDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductCatalogueDao.class);

	public static boolean validateProductId(int prodId) {
		boolean result = false;
		Client client = ClientBuilder.newClient();
		try {
			//TODO the URL must be replaced with LB URL of Prodcat Service.
			Response res = client.target("http://localhost:2222/prodcat/products/" + prodId)
					.request(MediaType.APPLICATION_JSON).get();
			if (res.getStatus() == Status.OK.getStatusCode()) {
				LOGGER.info(" Product with id ={} exists in Product Catalogue", prodId);
				result = true;
			}
		} catch (Exception e) {
			LOGGER.warn("Error received while querying Product Catalogue with Product id ={} ", prodId);
		}

		return result;
	}
}
