package controllers;

import java.util.ArrayList;
import java.util.List;

import model.Cajero;
import model.Empleado;
import model.Panaderia;
import model.PanaderoArtesanal;
import model.PanaderoIndustrial;
import model.Pedido;
import model.TipoPan;

/**
 * Controlador: único punto de acceso del viewController al modelo.
 * Las excepciones del modelo se propagan hacia la vista, que las muestra.
 */
public class PanaderiaController {

    public static final String ARTESANAL = "Panadero artesanal";
    public static final String INDUSTRIAL = "Panadero industrial";
    public static final String CAJERO = "Cajero";

    private final Panaderia panaderia;

    public PanaderiaController(Panaderia panaderia) {
        this.panaderia = panaderia;
    }

    public void agregarEmpleado(String tipo, String nombre, String documento,
                                String codigo, int minutos, String numCaja) {
        if (nombre == null || nombre.isBlank() || documento == null || documento.isBlank()
                || codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("Nombre, documento y código son obligatorios.");
        }
        if (minutos < 0) {
            throw new IllegalArgumentException("Los minutos disponibles no pueden ser negativos.");
        }
        Empleado empleado;
        switch (tipo) {
            case ARTESANAL:
                empleado = new PanaderoArtesanal(nombre, documento, codigo, minutos);
                break;
            case INDUSTRIAL:
                empleado = new PanaderoIndustrial(nombre, documento, codigo, minutos);
                break;
            case CAJERO:
                if (numCaja == null || numCaja.isBlank()) {
                    throw new IllegalArgumentException("El cajero necesita un número de caja.");
                }
                empleado = new Cajero(nombre, documento, codigo, minutos, numCaja);
                break;
            default:
                throw new IllegalArgumentException("Seleccione el tipo de empleado.");
        }
        panaderia.agregarEmpleado(empleado);
    }

    public void registrarPedido(String codigo, String cliente, TipoPan tipo, int cantidad) {
        if (codigo == null || codigo.isBlank() || cliente == null || cliente.isBlank()) {
            throw new IllegalArgumentException("Código y cliente son obligatorios.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("Seleccione el tipo de pan.");
        }
        panaderia.registrarPedido(codigo, cliente, tipo, cantidad);
    }

    public void asignarPedido(String codigoPedido) {
        panaderia.asignarPedido(codigoPedido);
    }

    public void cancelarPedido(String codigoPedido) {
        panaderia.cancelarPedido(codigoPedido);
    }

    public List<Empleado> getEmpleados() {
        return new ArrayList<>(panaderia.getListaEmpleados());
    }

    public List<Pedido> getPedidos() {
        return new ArrayList<>(panaderia.getListaPedidos());
    }
}
