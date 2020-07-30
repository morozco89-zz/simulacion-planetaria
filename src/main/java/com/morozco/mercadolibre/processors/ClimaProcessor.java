package com.morozco.mercadolibre.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.morozco.mercadolibre.http.ClimaResponse;
import com.morozco.mercadolibre.services.ClimaService;

@Component
public class ClimaProcessor implements Processor {
	
	@Autowired
    private ClimaService climaService;

	@Override
	public void process(Exchange exchange) throws Exception {
		ClimaResponse response = new ClimaResponse();
		int dia = exchange.getIn().getHeader("dia", Integer.class);
		String clima = climaService.getClimaGalaxia(dia);
		response.setDia(dia);
		response.setClima(clima);
		
		exchange.getIn().setBody(response);
	}

}
