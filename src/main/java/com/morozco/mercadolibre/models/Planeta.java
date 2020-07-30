package com.morozco.mercadolibre.models;

public class Planeta {
	
    private String nombre;
    private long radio;
    private int velocidad;
    private int posicion;

    public String getNombre() {
        return nombre;
    }
    
    public double getX() {
    	return radio * Math.cos(posicion);
    }
    
    public double getY() {
    	return radio * Math.sin(posicion);
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public long getRadio() {
        return radio;
    }

    public void setRadio(long radio) {
        this.radio = radio;
    }

    public int getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(int velocidad) {
        this.velocidad = velocidad;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
    	int normalizada = (posicion % 360);
    	this.posicion = normalizada < 0 ? 360 + normalizada : normalizada;
    }
}

