package model;

public class Pedido {
    private String codigoPedido;
    private String nombreCliente;
    private TipoPan tipo;
    private int cantidad;
    private EstadoPedido estado;
    private String codigoEmpleadoAsig;
    private int minutosReserva;

    public Pedido(String codigoPedido, String nombreCliente, TipoPan tipo, int cantidad) {
        this.codigoPedido = codigoPedido;
        this.nombreCliente = nombreCliente;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.estado = EstadoPedido.PENDIENTE;
        this.codigoEmpleadoAsig = null;
        this.minutosReserva = 0;
    }

    public String getCodigoPedido() { return codigoPedido; }
    public String getNombreCliente() { return nombreCliente; }
    public TipoPan getTipo() { return tipo; }
    public int getCantidad() { return cantidad; }
    public EstadoPedido getEstado() { return estado; }
    public String getCodigoEmpleadoAsig() { return codigoEmpleadoAsig; }
    public int getMinutosReserva() { return minutosReserva; }

    public void setEstado(EstadoPedido estado) { this.estado = estado; }
    public void setCodigoEmpleadoAsig(String codigoEmpleadoAsig) { this.codigoEmpleadoAsig = codigoEmpleadoAsig; }
    public void setMinutosReserva(int minutosReserva) { this.minutosReserva = minutosReserva; }

    @Override
    public String toString() {
        return codigoPedido + " | " + nombreCliente + " | " + cantidad + " " + tipo + " | " + estado
                + (codigoEmpleadoAsig != null
                    ? " | " + codigoEmpleadoAsig + " (" + minutosReserva + " min)"
                    : "");
    }
}
