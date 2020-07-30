package com.morozco.mercadolibre.resource;

import com.morozco.mercadolibre.processors.ClimaProcessor;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class ApplicationResource extends RouteBuilder {
	
	@Autowired
	private ClimaProcessor climaProcessor;

    @Override
    public void configure() throws Exception {
        restConfiguration()
        	.component("servlet")
        	.port(9090)
        	.host("localhost")
        	.bindingMode(RestBindingMode.json);

        rest()
        	.get("/clima")
            .produces(MediaType.APPLICATION_JSON_VALUE)
            .route()
            	.doTry()
		            .choice()
		            	.when(header("dia"))
		            		.process(climaProcessor)
		            	.otherwise()
		            		.removeHeaders("*")
		            		.setBody(constant(null))
		            		.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(422))
		            .endChoice()
		         .endDoTry()
		         .doCatch(Exception.class)
		         	.removeHeaders("*")
	         		.setBody(constant(null))
	         		.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
		         ;
    }
}