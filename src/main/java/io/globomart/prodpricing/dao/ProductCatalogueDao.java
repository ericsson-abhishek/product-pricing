package io.globomart.prodpricing.dao;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eureka2.shading.reactivex.netty.protocol.http.client.HttpClientResponse;
import com.netflix.client.AbstractLoadBalancerAwareClient;
import com.netflix.client.ClientFactory;
import com.netflix.client.ClientRequest;
import com.netflix.client.IClient;
import com.netflix.client.IResponse;
import com.netflix.client.http.HttpRequest;
import com.netflix.client.http.HttpResponse;

import io.netty.handler.codec.http.HttpResponseStatus;

public abstract class ProductCatalogueDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductCatalogueDao.class);
	private static AbstractLoadBalancerAwareClient  ribbonClient = null;

	public static boolean validateProductId(int prodId) {
		boolean result = false;
		Client client = ClientBuilder.newClient();
		try {
			
			if(ribbonClient==null)
			{
				ribbonClient =(AbstractLoadBalancerAwareClient) ClientFactory.getNamedClient("productCatalogue");
			}
			//TODO the URL must be replaced with LB URL of Prodcat Service.
			HttpRequest request = HttpRequest.newBuilder().uri("/prodcat/products/" + prodId).build();
			System.out.println(request.getUri());
			HttpResponse res = (HttpResponse)ribbonClient.executeWithLoadBalancer(request);
			
			
				
			if (res.getStatus() == HttpStatus.SC_OK) {
				LOGGER.info(" Product with id ={} exists in Product Catalogue", prodId);
				result = true;
			}
			
			res.close();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.warn("Error received while querying Product Catalogue with Product id ={} ", prodId);
		}
		
		

		return result;
	}
}
