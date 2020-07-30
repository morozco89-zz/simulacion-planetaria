package com.morozco.mercadolibre.services;

import org.springframework.stereotype.Service;

import com.morozco.mercadolibre.models.Planeta;
import com.morozco.mercadolibre.utils.Matrix;

@Service
public class ClimaService {
	
	public static final int MAXIMOS_DIAS_SIMULACION = 20000;
	private int periodosSequia;
	private int periodosLluvia;
	private int periodosCondicionesOptimas;
	private int diaPicoLluvia;
	private double areaTriangulo;
	private double tolerancia = 0.01;
	
	public ClimaService() {
		
	}
	
	public String getClimaGalaxia(int dia) {
		String clima;
		
		if (dia < 1) {
			throw new IllegalArgumentException("El dia debe ser mayor a cero");
		}
		
		Planeta ferengi = ClimaService.getFerengi();
		Planeta betasoide = ClimaService.getBetasoide();
		Planeta vulcano = ClimaService.getVulcano();
		
		ferengi.setPosicion(dia == 1 ? 0 : (dia - 1) * ferengi.getVelocidad());
		betasoide.setPosicion(dia == 1 ? 0 : (dia - 1) * betasoide.getVelocidad());
		vulcano.setPosicion(dia == 1 ? 0 : (dia - 1) * vulcano.getVelocidad());
		
		if (esPeriodoSequia(ferengi, betasoide, vulcano)) {
			clima = "sequía";
		} else if (esPeriodoOptimo(ferengi, betasoide, vulcano, tolerancia)) {
			clima = "óptimo";
		} else if (esPeriodoLluvia(ferengi, betasoide, vulcano, tolerancia)) {
			clima = "lluvia";
		} else {
			clima = "normal";
		}
		
		return clima;
	}
    
    public void simular(
    		Planeta planeta1,
    		Planeta planeta2,
    		Planeta planeta3,
    		int desdeDia,
    		int hastaDia
    ) {
    	if (desdeDia < 1) {
    		throw new IllegalArgumentException("El dia inicial debe ser mayor a 0");
    	}
    	
    	if (desdeDia > hastaDia) {
    		throw new IllegalArgumentException("El día inicial no puede ser mayor al día final");
    	}
    	
    	if ((hastaDia - desdeDia) > MAXIMOS_DIAS_SIMULACION) {
    		throw new IllegalArgumentException("La cantidad máxima permitida de días a simular es " + MAXIMOS_DIAS_SIMULACION + " dias");
    	}
    	
    	System.out.println("==========");
    	System.out.println("Iniciando simulacion...");
    	System.out.println("==========\n");
    	System.out.println("Posición inicial de los planetas:");
    	System.out.println("\t- " + planeta1.getNombre() + ":\t" + planeta1.getPosicion() + "º");
    	System.out.println("\t- " + planeta2.getNombre() + ":\t" + planeta1.getPosicion() + "º");
    	System.out.println("\t- " + planeta3.getNombre() + ":\t" + planeta1.getPosicion() + "º\n");
    	
    	do {
    		if (esPeriodoSequia(planeta1, planeta2, planeta3)) {
    			System.out.println("* El dia " + desdeDia + " es de sequia");
    			periodosSequia++;
    		}
    		
    		if (esPeriodoLluvia(planeta1, planeta2, planeta3, tolerancia)) {
    			System.out.println("* El dia " + desdeDia + " es de lluvia");
    			periodosLluvia++;
    			
    			double area = calcularArea(planeta1, planeta2, planeta3);
    			
    			if (area > areaTriangulo) {
    				diaPicoLluvia = desdeDia;
    				areaTriangulo = area;
    				System.out.println("!! Nuevo pico de lluvia dia "
    						+ desdeDia + " (Área " + areaTriangulo + ")");
    			}
    		}

    		if (esPeriodoOptimo(planeta1, planeta2, planeta3, tolerancia)) {
    			System.out.println("* El dia " + desdeDia + " es de condiciones óptimas");
    			periodosCondicionesOptimas++;
    		}
    		
    		desdeDia++;
    		actualizarPosicion(planeta1);
    		actualizarPosicion(planeta2);
    		actualizarPosicion(planeta3);
    	} while (desdeDia <= hastaDia);
    }
    
    public int getPeriodosSequia() {
    	return periodosSequia;
    }
    
    public int getPeriodosLluvia() {
    	return periodosLluvia;
    }
    
    public int getPeriodosCondicionesOptimas() {
    	return periodosCondicionesOptimas;
    }
    
    public int getDiaPicoLluvia() {
    	return diaPicoLluvia;
    }
    
    public void setTolerancia(double tolerancia)
    {
    	this.tolerancia = tolerancia;
    }
    
    /***** PREDICADOS *****/
    
    
    private static final boolean esPeriodoSequia(Planeta p1, Planeta p2, Planeta p3) {
    	return grados180(p1.getPosicion()) == grados180(p2.getPosicion()) &&
    			grados180(p2.getPosicion()) == grados180(p3.getPosicion());
    }
    
    private static final boolean esPeriodoLluvia(Planeta p1, Planeta p2, Planeta p3, double tolerancia) {
    	final double d1_2 = distance(p1.getPosicion(), p2.getPosicion());
    	final double d1_3 = distance(p1.getPosicion(), p3.getPosicion());
    	
    	return !esPeriodoOptimo(p1, p2, p3, tolerancia) &&
    				90 < d1_2 && d1_2 < 180 && 0 < d1_3 && d1_3 <= 90;
    }
    
    private static final boolean esPeriodoOptimo(Planeta p1, Planeta p2, Planeta p3, double tolerancia) {
    	return !esPeriodoSequia(p1, p2, p3) &&
    				Math.abs(calcularPendiente(p1, p2) - calcularPendiente(p2, p3)) < tolerancia;
    }
    
    /***** HELPERS *****/
    
    
    private static final void actualizarPosicion(Planeta planeta) {
    	final int po = grados(planeta.getPosicion());
    	final int delta = grados(planeta.getVelocidad());
    	final int pf = grados(po + delta);
    	
    	planeta.setPosicion(pf);
    }
    
    private static final double calcularPendiente(Planeta p1, Planeta p2) {
    	final double x1 = p1.getX();
    	final double y1 = p1.getY();
    	final double x2 = p2.getX();
    	final double y2 = p2.getY();
    	
    	return (y2 - y1)/(x2 - x1);
    } 
    
    private static int distance(int alpha, int beta) {
    	final int phi = Math.abs(beta - alpha) % 360;
    	final int distance = phi > 180 ? 360 - phi : phi;
    	
        return distance;
    }
    
    private static final int grados(int posicion) {
    	posicion = posicion % 360;
    	
    	return posicion < 0 ? 360 + posicion : posicion;
    }
    
    private static final int grados180(int posicion) {
    	posicion = posicion % 180;
    	
    	return posicion < 0 ? 180 + posicion : posicion;
    }
    
    private static final double calcularArea(Planeta p1, Planeta p2, Planeta p3) {
    	final double x1 = p1.getX();
    	final double y1 = p1.getY();
    	final double x2 = p2.getX();
    	final double y2 = p2.getY();
    	final double x3 = p3.getX();
    	final double y3 = p3.getY();
    	
    	final double determinant;
    	final double x[][] = {
				{x1, y1, 1,},
				{x2, y2, 1,},
				{x3, y3, 1,},
		};
		
		determinant = Matrix.matrixDeterminant(x);
		
		return Math.abs(determinant);
    }
    
    public static Planeta getFerengi() {
		final Planeta planeta = new Planeta();
		planeta.setRadio(500);
		planeta.setVelocidad(1);
		planeta.setNombre("Ferengi");
		planeta.setPosicion(0);

		return planeta;
	}

	public static Planeta getBetasoide() {
		final Planeta planeta = new Planeta();
		planeta.setRadio(2000);
		planeta.setVelocidad(3);
		planeta.setNombre("Betasoide");
		planeta.setPosicion(0);

		return planeta;
	}

	public static Planeta getVulcano() {
		final Planeta planeta = new Planeta();
		planeta.setRadio(1000);
		planeta.setVelocidad(-5);
		planeta.setNombre("Vulcano");
		planeta.setPosicion(0);

		return planeta;
	}

}