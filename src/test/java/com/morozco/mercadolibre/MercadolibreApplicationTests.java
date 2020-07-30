package com.morozco.mercadolibre;

import com.morozco.mercadolibre.services.ClimaService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MercadolibreApplicationTests {

	@Autowired
	private ClimaService service;

	@Test
	void contextLoads() {
		assertThat(service).isNotNull();
	}
	
	@Test
	public void whenDaysBelowOne_thenExceptionThrown() {
		assertThrows(IllegalArgumentException.class,() -> {
			service.simular(
					ClimaService.getFerengi(),
					ClimaService.getBetasoide(),
					ClimaService.getVulcano(),
					1,
					-5
			);
		});
	}
	
	@Test
	public void whenSimulationDaysExceedsMaximum_thenExceptionThrown() {
		assertThrows(IllegalArgumentException.class,() -> {
			service.simular(
					ClimaService.getFerengi(),
					ClimaService.getBetasoide(),
					ClimaService.getVulcano(),
					1,
					ClimaService.MAXIMOS_DIAS_SIMULACION + 2
			);
		});
	}

	@Test
	public void whenDaysCorrect_thenComputes() {
		service.simular(
				ClimaService.getFerengi(),
				ClimaService.getBetasoide(),
				ClimaService.getVulcano(),
				1,
				3652
		);
		
		System.out.println("Hay (" + service.getPeriodosSequia() +
				") periodos de sequia en un año");
		System.out.println("Hay (" + service.getPeriodosLluvia() +
				") periodos de lluvia en un año");
		System.out.println("Hay (" + service.getPeriodosCondicionesOptimas() +
				") periodos de condiciones óptimas en un año");
		System.out.println("El día de pico máximo de lluvia es " + service.getDiaPicoLluvia());
		
		assertTrue(service.getPeriodosSequia() > 0);
		assertTrue(service.getPeriodosLluvia() > 0);
		assertTrue(service.getPeriodosCondicionesOptimas() > 0);
	}

}
