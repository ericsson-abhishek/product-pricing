package io.globomart.prodpricing;

import java.util.EnumSet;
import java.util.List;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Applications;

import io.globomart.prodpricing.dao.PersistanceUtil;

public class App {
	
	public static EurekaClient eurekaClient =null;

	public static void main(String[] args) throws Exception {

		
		int port = 0;
		int instanceId=0;
		if (args.length > 1) {
			port = Integer.valueOf(args[0]);
			instanceId = Integer.valueOf(args[1]);
		}

		System.out.println("PORT : " + port);
		Server server = createServer(port);


		try {
			
			registerServiceInEureka(port,instanceId);
			server.start();
			server.join();
		} finally {
			server.destroy();
		}

	}
	
	
	public static void registerServiceInEureka(int port, final int instanceId)  {
		MyDataCenterInstanceConfig myDataCenterInstanceConfig = new MyDataCenterInstanceConfig();
		//ApplicationInfoManager applicationInfoManager 
//		/if (applicationInfoManager == null) {
			//instanceConfig.getMetadataMap().put("eureka.instance.metadataMap.instanceId", "instance1");
		
			InstanceInfo instanceInfo = new EurekaConfigBasedInstanceInfoProvider(myDataCenterInstanceConfig).get();
			ApplicationInfoManager applicationInfoManager = new ApplicationInfoManager(myDataCenterInstanceConfig, instanceInfo);
			try{
			eurekaClient = new DiscoveryClient(applicationInfoManager, new DefaultEurekaClientConfig());
			
			}catch( Throwable e)
			{
				System.out.println("Exception occurred here");
				e.printStackTrace();
			}
			
			Applications apps = eurekaClient.getApplications();
			List<InstanceInfo> infos = apps.getInstancesByVirtualHostName("abhishek.com");
			for (InstanceInfo info : infos)
			{
				System.out.println("Appname for the instance is "+info.getAppName()+" id corresponding to that is "+info.getId()+" IP "+info.getIPAddr()+" port "+info.getPort()  );
			}
			
			applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.UP);
			
		//}
		//ApplicationInfoManager applicationInfoManager = initializeApplicationInfoManager(myDataCenterInstanceConfig);

		
	}
	
	public static Server createServer(int port) throws Exception, InterruptedException {
		String PORT;
		if (port == 0) {
			PORT = System.getenv("PORT");
			if (PORT == null || PORT.isEmpty()) {
				PORT = "3400";
			}
		} else {
			PORT = String.valueOf(port);
		}

		Server server = new Server(Integer.valueOf(PORT));
		ServletContextHandler context = new ServletContextHandler(server, "/*");

		// Configure Jersey resources
		// including the base package of JAX-RS resources
		// and some of the swagger packages
		ResourceConfig config = new ResourceConfig();
		config.packages("io.globomart.prodpricing", "io.swagger.jaxrs.json", "io.swagger.jaxrs.listing");

		// setup the jersey servlet
		ServletHolder jerseyServlet = new ServletHolder(new ServletContainer(config));
		context.addServlet(jerseyServlet, "/*");

		// add PersistanceUtil as a servletContexttlistener
		context.addEventListener(new PersistanceUtil());

		// Setup Swagger servlet.
		// Swagger is used for documenting the REST services being exposed by
		// Prodcat Service
		ServletHolder swaggerServ = new ServletHolder(new SwaggerBootstrap());
		swaggerServ.setInitOrder(2);
		context.addServlet(swaggerServ, "/swagger-core");

		// Purposefully allowing CORS to test the services from
		// http://editor.swagger.io/#/
		// MUST be commented for production
		FilterHolder cors = context.addFilter(CrossOriginFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
		cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
		cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
		cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD");
		cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");

		return server;
	}

	public static Server createServer() throws Exception, InterruptedException {
		String PORT = System.getenv("PORT");
		if (PORT == null || PORT.isEmpty()) {
			PORT = "3333";
		}

		Server server = new Server(Integer.valueOf(PORT));
		ServletContextHandler context = new ServletContextHandler(server, "/*");

		// Configure Jersey resources
		// including the base package of JAX-RS resources
		// and some of the swagger packages
		ResourceConfig config = new ResourceConfig();
		config.packages("io.globomart.prodpricing", "io.swagger.jaxrs.json", "io.swagger.jaxrs.listing");

		// setup the jersey servlet
		ServletHolder jerseyServlet = new ServletHolder(new ServletContainer(config));
		context.addServlet(jerseyServlet, "/*");

		// add PersistanceUtil as a servletContexttlistener
		context.addEventListener(new PersistanceUtil());

		// Swagger is used for documenting the REST services being exposed by
		// Prodcat Service
		ServletHolder swaggerServ = new ServletHolder(new SwaggerBootstrap());
		swaggerServ.setInitOrder(2);
		context.addServlet(swaggerServ, "/swagger-core");

		// Purposefully allowing CORS to test the services from
		// http://editor.swagger.io/#/
		// MUST be commented for production
		FilterHolder cors = context.addFilter(CrossOriginFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
		cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
		cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
		cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD");
		cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");

		return server;
	}
}
