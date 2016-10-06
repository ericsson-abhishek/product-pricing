package io.globomart.prodpricing.test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.eclipse.jetty.server.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

import io.globomart.prodpricing.App;
import io.globomart.prodpricing.entities.PricingEntity;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestAll {
	private static Server server;
	private static Client client;

	public TestAll() {
	}
	

    

	@BeforeClass
	public static void setUp() throws Exception {
		server = App.createServer();
		server.start();
		client = new Client();
		//PowerMockito.mockStatic(ProductPricingDao.class);
		//when(ProductPricingDao.createPricing(new Pricing(1, 1, 100)).thenReturn(new PricingEntity(1, 1, 100));
		//when(ProductPricingDao.createPricing(any(Pricing.class))).thenReturn(new PricingEntity(1, 1, 100));
		
		//Ogiven(ProductPricingDao.createPricing(any(Pricing.class))).willReturn(expectedObject);


	}

	@AfterClass
	public static void tearDown() throws InterruptedException, Exception {
		server.stop();

	}

	@Test
	public void createPrice1() {
		String body = "{ \"productId\":1,\"supplierId\":111,\"price\":100}";
		WebResource webResource = client.resource("http://localhost:2222/prodprice/prices");
		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, body);
		System.out.println("Create Status : " + response.getStatus());
		PricingEntity resEntity = response.getEntity(PricingEntity.class);
		assertEquals(resEntity.getPricingId().intValue(), 1);
		assertEquals(resEntity.getProductId().intValue(), 1);
		assertEquals(resEntity.getSupplierId(), 111);
		assertEquals(resEntity.getPrice(), 100, 0.0);

	}

	@Test
	public void createPrice2() {
		String body = "{ \"productId\":2,\"supplierId\":222,\"price\":200}";
		WebResource webResource = client.resource("http://localhost:2222/prodprice/prices");
		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, body);
		System.out.println("Create Status : " + response.getStatus());
		PricingEntity resEntity = response.getEntity(PricingEntity.class);
		assertEquals(resEntity.getPricingId().intValue(), 2);
		assertEquals(resEntity.getProductId().intValue(), 2);
		assertEquals(resEntity.getSupplierId(), 222);
		assertEquals(resEntity.getPrice(), 200, 0.0);

	}

	@Test
	public void getPrices() {
		WebResource webResource = client.resource("http://localhost:2222/prodprice/prices");
		ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		System.out.println("Get Status : " + response.getStatus());
		List<PricingEntity> pricingEntityList = response.getEntity(new GenericType<List<PricingEntity>>() {
		});
		PricingEntity resEntity = pricingEntityList.get(0);
		assertEquals(resEntity.getPricingId().intValue(), 1);
		assertEquals(resEntity.getProductId().intValue(), 1);
		assertEquals(resEntity.getSupplierId(), 111);
		assertEquals(resEntity.getPrice(), 100, 0.0);

		PricingEntity resEntity1 = pricingEntityList.get(1);
		assertEquals(resEntity1.getPricingId().intValue(), 2);
		assertEquals(resEntity1.getProductId().intValue(), 2);
		assertEquals(resEntity1.getSupplierId(), 222);
		assertEquals(resEntity1.getPrice(), 200, 0.0);
	}

	
	@Test
	public void getPriceById() {
		WebResource webResource = client.resource("http://localhost:2222/prodprice/prices/2");
		ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		System.out.println("Get Status : " + response.getStatus());

		PricingEntity resEntity1 = response.getEntity(PricingEntity.class);
		assertEquals(resEntity1.getPricingId().intValue(), 2);
		assertEquals(resEntity1.getProductId().intValue(), 2);
		assertEquals(resEntity1.getSupplierId(), 222);
		assertEquals(resEntity1.getPrice(), 200, 0.0);
	}
	
	@Test
	public void getPriceByFilter() {
		WebResource webResource = client.resource("http://localhost:2222/prodprice/prices");
		ClientResponse response = webResource.queryParam("productId", "2").accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		System.out.println("Get Status : " + response.getStatus());
		List<PricingEntity> pricingEntityList = response.getEntity(new GenericType<List<PricingEntity>>() {
		});
		System.out.println("getPriceByFilter response PricingEntity size : "+pricingEntityList.size());
		PricingEntity resEntity1 = pricingEntityList.get(0);
		assertEquals(resEntity1.getPricingId().intValue(), 2);
		assertEquals(resEntity1.getProductId().intValue(), 2);
		assertEquals(resEntity1.getSupplierId(), 222);
		assertEquals(resEntity1.getPrice(), 200, 0.0);
	}


}
