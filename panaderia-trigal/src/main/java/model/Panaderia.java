package model;

import java.util.ArrayList;
import java.util.List;

public class Panaderia {
    private String nombre;
    private List<Empleado> listaEmpleados;
    private List<Pedido> listaPedidos;

    public Panaderia(String nombre) {
        this.nombre = nombre;
        this.listaEmpleados = new ArrayList<>();
        this.listaPedidos = new ArrayList<>();
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public List<Empleado> getListaEmpleados() { return listaEmpleados; }
    public List<Pedido> getListaPedidos() { return listaPedidos; }


    public void agregarEmpleado(Empleado empleado) {
        if (empleado == null) {
            throw new IllegalArgumentException("El empleado no puede ser nulo.");
        }
        if (buscarEmpleado(empleado.getCodigoEmpleado()) != null) {
            throw new IllegalArgumentException("Ya existe un empleado con el código "
                    + empleado.getCodigoEmpleado() + ".");
        }
        listaEmpleados.add(empleado);
    }

    public Pedido registrarPedido(String codigoPedido, String nombreCliente,
                                  TipoPan tipo, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        if (buscarPedido(codigoPedido) != null) {
            throw new IllegalArgumentException("Ya existe un pedido con el código "
                    + codigoPedido + ".");
        }
        Pedido pedido = new Pedido(codigoPedido, nombreCliente, tipo, cantidad);
        listaPedidos.add(pedido);
        return pedido;
    }

    public Pedido asignarPedido(String codigoPedido) {
        Pedido pedido = buscarPedido(codigoPedido);
        if (pedido == null) {
            throw new IllegalArgumentException("No existe el pedido " + codigoPedido + ".");
        }
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new IllegalStateException("El pedido " + codigoPedido
                    + " no está pendiente (estado: " + pedido.getEstado() + ").");
        }

        Panadero seleccionado = null;
        int menorTiempo = 0;
        for (Empleado empleado : listaEmpleados) {
            if (empleado instanceof Panadero) {
                Panadero panadero = (Panadero) empleado;
                int tiempo = panadero.calcularTiempo(pedido);
                boolean alcanza = panadero.getMinutosDisponibles() >= tiempo;
                if (alcanza && (seleccionado == null || tiempo < menorTiempo)) {
                    seleccionado = panadero;
                    menorTiempo = tiempo;
                }
            }
        }
        if (seleccionado == null) {
            throw new IllegalStateException("Ningún panadero tiene tiempo suficiente para el pedido "
                    + codigoPedido + ".");
        }

        seleccionado.setMinutosDisponibles(seleccionado.getMinutosDisponibles() - menorTiempo);
        pedido.setCodigoEmpleadoAsig(seleccionado.getCodigoEmpleado());
        pedido.setMinutosReserva(menorTiempo);
        pedido.setEstado(EstadoPedido.ASIGNADO);
        return pedido;
    }

    public Pedido cancelarPedido(String codigoPedido) {
        Pedido pedido = buscarPedido(codigoPedido);
        if (pedido == null) {
            throw new IllegalArgumentException("No existe el pedido " + codigoPedido + ".");
        }
        switch (pedido.getEstado()) {
            case CANCELADO:
                throw new IllegalStateException("El pedido " + codigoPedido + " ya está cancelado.");
            case ASIGNADO:
                Empleado empleado = buscarEmpleado(pedido.getCodigoEmpleadoAsig());
                if (empleado == null) {
                    throw new IllegalStateException("No se encontró el empleado asignado al pedido.");
                }
                empleado.setMinutosDisponibles(empleado.getMinutosDisponibles() + pedido.getMinutosReserva());
                pedido.setEstado(EstadoPedido.CANCELADO);
                break;
            default:
                pedido.setEstado(EstadoPedido.CANCELADO);
                break;
        }
        return pedido;
    }

    public Pedido buscarPedido(String codigoPedido) {
        for (Pedido p : listaPedidos) {
            if (p.getCodigoPedido().equals(codigoPedido)) {
                return p;
            }
        }
        return null;
    }

    public Empleado buscarEmpleado(String codigoEmpleado) {
        for (Empleado e : listaEmpleados) {
            if (e.getCodigoEmpleado().equals(codigoEmpleado)) {
                return e;
            }
        }
        return null;
    }
}
