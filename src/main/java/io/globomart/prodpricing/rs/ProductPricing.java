package io.globomart.prodpricing.rs;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;

import io.globomart.prodpricing.App;
import io.globomart.prodpricing.dao.ProductCatalogueDao;
import io.globomart.prodpricing.dao.ProductPricingDao;
import io.globomart.prodpricing.dto.Pricing;
import io.globomart.prodpricing.entities.PricingEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Path("prodprice")
@Api(value = "/prodprice")
public class ProductPricing {

	private static Logger LOGGER = LoggerFactory.getLogger(ProductPricing.class);

	@GET
	@Path("prices")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Return all Prices those match the specified all filter criterii (if provided)", response = Pricing.class, responseContainer = "Array")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pricingId", value = "Id of the pricing", required = false, dataType = "integer", paramType = "query"),
			@ApiImplicitParam(name = "productId", value = "product id", required = false, dataType = "integer", paramType = "query"),
			@ApiImplicitParam(name = "supplierId", value = "supplier id", required = false, dataType = "integer", paramType = "query"),
			@ApiImplicitParam(name = "price", value = "product price", required = false, dataType = "double", paramType = "query") })
	public Response getPricing(@Context UriInfo uriInfo) throws SQLException {

		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
		LOGGER.debug("the query params are{}", queryParams);

		Map<String, String> filter = null;
		if (queryParams != null) {
			filter = createFilterFromQueryParams(queryParams);
		}

		List<PricingEntity> productList = ProductPricingDao.getPricing(filter);
		return Response.ok().entity(productList).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").allow("OPTIONS").build();
	}

	private Map<String, String> createFilterFromQueryParams(MultivaluedMap<String, String> queryParams) {
		Map<String, String> filter = new HashMap<>();
		for (Entry<String, List<String>> queryParam : queryParams.entrySet()) {
			// TODO require NULL checks
			filter.put(queryParam.getKey(), queryParam.getValue().get(0));
		}

		return filter;
	}

	@GET
	@Path("prices/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Return a single price with specific id", response = Pricing.class)
	public Response getPricingById(
			@ApiParam(value = "ID of the pricing to be searched", required = true) @PathParam(value = "id") int pricingId) {
		PricingEntity pricing = ProductPricingDao.getPricingById(pricingId);
		Response res = null;
		if (pricing != null) {
			LOGGER.debug("Successfully fetched Pricing for id ={}", pricingId);
			res = Response.ok().entity(pricing).build();
		} else {
			LOGGER.warn("Could not find Pricing for id ={}", pricingId);
			res = Response.status(Status.NOT_FOUND).build();
		}
		return res;
	}

	@POST
	@Path("prices")
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Add a new Price for a specific product and supplier", response = Pricing.class)
	public Response createPricing(
			@ApiParam(value = "The JSON request for new Price to be added", required = true) Pricing pricing)
			throws JsonParseException, JsonMappingException, IOException {

		EurekaClient eClient = App.eurekaClient;
		Applications apps = eClient.getApplications();
		List<InstanceInfo> infos = apps.getInstancesByVirtualHostName("abhishek.com");
		for (InstanceInfo info : infos)
		{
			System.out.println("Appname for the instance is "+info.getAppName()+" id corresponding to that is "+info.getId()+" IP "+info.getIPAddr()+" port "+info.getPort()  );
		}
		
		Response res = null;
		boolean isvalidProduct = ProductCatalogueDao.validateProductId(pricing.getProductId());
		if (isvalidProduct) {
			PricingEntity pricingEn = ProductPricingDao.createPricing(pricing);
			res = Response.ok().entity(pricingEn).build();
		} else {
			res = Response.status(Status.BAD_REQUEST.getStatusCode()).entity("INVALID PRODUCT ID").build();
		}
		return res;

	}

}
