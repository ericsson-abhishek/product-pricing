package io.globomart.prodpricing.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

import io.globomart.prodpricing.App;
import io.globomart.prodpricing.dao.PersistanceUtil;
import io.globomart.prodpricing.dao.ProductCatalogueDao;
import io.globomart.prodpricing.entities.PricingEntity;

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = {ProductCatalogueDao.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestAll {
	private static Server server;
	private static Client client;


	@BeforeClass
	public static void setUp() throws Exception {
		server = App.createServer();
		server.start();
		client = new Client();


	}
	
	@After
	public void cleanAll()
	{
		EntityManager em = PersistanceUtil.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.createNativeQuery("delete from Product_Pricing").executeUpdate();
		
		em.flush();
		tx.commit();
		em.getEntityManagerFactory().getCache().evictAll();
		em.close();
		
	}

	@AfterClass
	public static void tearDown() throws InterruptedException, Exception {
		server.stop();

	}

	@Test
	public void testCreateSuccess1() {
		PowerMockito.mockStatic(ProductCatalogueDao.class);
		when(ProductCatalogueDao.validateProductId(1)).thenReturn(true);

		String body = "{ \"productId\":1,\"supplierId\":111,\"price\":100}";
		WebResource webResource = client.resource("http://localhost:3333/prodprice/prices");
		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, body);
		
		PricingEntity resEntity = response.getEntity(PricingEntity.class);
		assertNotNull(resEntity.getPricingId());
		//System.out.println(resEntity.getPricingId());
		assertEquals(resEntity.getProductId().intValue(), 1);
		assertEquals(resEntity.getSupplierId(), 111);
		assertEquals(resEntity.getPrice(), 100, 0.0);
		
		//return resEntity.getPricingId();

	}

	@Test
	public void testCreateSuccess2() {
		
		PowerMockito.mockStatic(ProductCatalogueDao.class);
		when(ProductCatalogueDao.validateProductId(2)).thenReturn(true);
		
		String body = "{ \"productId\":2,\"supplierId\":222,\"price\":200}";
		WebResource webResource = client.resource("http://localhost:3333/prodprice/prices");
		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, body);
		//System.out.println("Create Status : " + response.getStatus());
		PricingEntity resEntity = response.getEntity(PricingEntity.class);
		assertNotNull(resEntity.getPricingId());
		assertEquals(resEntity.getProductId().intValue(), 2);
		assertEquals(resEntity.getSupplierId(), 222);
		assertEquals(resEntity.getPrice(), 200, 0.0);

	}
	
	@Test
	public void testCreateFailure() {
		
		PowerMockito.mockStatic(ProductCatalogueDao.class);
		when(ProductCatalogueDao.validateProductId(3)).thenReturn(false);
		
		String body = "{ \"productId\":3,\"supplierId\":222,\"price\":200}";
		WebResource webResource = client.resource("http://localhost:3333/prodprice/prices");
		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, body);
		assertEquals(response.getStatus(),Status.BAD_REQUEST.getStatusCode());

	}

	@Test
	public void testGetPrices() {
		testCreateSuccess1();
		testCreateSuccess2();
		
		WebResource webResource = client.resource("http://localhost:3333/prodprice/prices");
		ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		//System.out.println("Get Status : " + response.getStatus());
		List<PricingEntity> pricingEntityList = response.getEntity(new GenericType<List<PricingEntity>>() {
		});
		
		assertEquals(pricingEntityList.size(), 2);
		PricingEntity resEntity = pricingEntityList.get(0);
		assertNotNull(resEntity.getPricingId());
		assertNotNull(resEntity.getProductId());
		assertNotNull(resEntity.getSupplierId());
		assertNotNull(resEntity.getPrice());

		PricingEntity resEntity1 = pricingEntityList.get(1);
		assertNotNull(resEntity1.getPricingId());
		assertNotNull(resEntity1.getProductId());
		assertNotNull(resEntity1.getSupplierId());
		assertNotNull(resEntity1.getPrice());
	}

	
	@Test
	public void testGetPriceById() {
		int id = createPricing();
		WebResource webResource = client.resource("http://localhost:3333/prodprice/prices/"+id);
		ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		//System.out.println("Get Status testGetPriceById: " + response.getStatus());

		PricingEntity resEntity1 = response.getEntity(PricingEntity.class);
		assertEquals(resEntity1.getPricingId(), id);
		assertNotNull(resEntity1.getProductId());
		assertNotNull(resEntity1.getSupplierId());
		assertNotNull(resEntity1.getPrice());
	}
	
	@Test
	public void testGetPriceByFilter() {
		testCreateSuccess2();
		WebResource webResource = client.resource("http://localhost:3333/prodprice/prices");
		ClientResponse response = webResource.queryParam("productId", "2").accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		//System.out.println("Get Status : " + response.getStatus());
		List<PricingEntity> pricingEntityList = response.getEntity(new GenericType<List<PricingEntity>>() {
		});
		//System.out.println("getPriceByFilter response PricingEntity size : "+pricingEntityList.size());
		assertTrue(pricingEntityList.size()>0);
		
		PricingEntity resEntity1 = pricingEntityList.get(0);
		assertNotNull(resEntity1.getPricingId());
		assertEquals(resEntity1.getProductId().intValue(), 2);
		assertNotNull(resEntity1.getSupplierId());
		assertNotNull(resEntity1.getPrice());
	}


	public int createPricing() {
		PowerMockito.mockStatic(ProductCatalogueDao.class);
		when(ProductCatalogueDao.validateProductId(1)).thenReturn(true);

		String body = "{ \"productId\":1,\"supplierId\":111,\"price\":100}";
		WebResource webResource = client.resource("http://localhost:3333/prodprice/prices");
		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, body);
		//System.out.println("Create Status : " + response.getStatus());
		
		PricingEntity resEntity = response.getEntity(PricingEntity.class);
		assertNotNull(resEntity.getPricingId());
		//System.out.println(resEntity.getPricingId());
		return resEntity.getPricingId();
	}

}
