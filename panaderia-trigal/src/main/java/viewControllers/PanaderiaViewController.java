package viewControllers;

import controllers.PanaderiaController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import model.Cajero;
import model.Empleado;
import model.Panadero;
import model.PanaderoArtesanal;
import model.PanaderoIndustrial;
import model.Pedido;
import model.TipoPan;

public class PanaderiaViewController {

    private PanaderiaController controlador;

    // ----- Empleados -----
    @FXML private ComboBox<String> cmbTipoEmpleado;
    @FXML private TextField txtEmpNombre;
    @FXML private TextField txtEmpDocumento;
    @FXML private TextField txtEmpCodigo;
    @FXML private TextField txtEmpMinutos;
    @FXML private TextField txtEmpCaja;
    @FXML private TableView<Empleado> tblEmpleados;
    @FXML private TableColumn<Empleado, String> colEmpCodigo;
    @FXML private TableColumn<Empleado, String> colEmpNombre;
    @FXML private TableColumn<Empleado, String> colEmpDocumento;
    @FXML private TableColumn<Empleado, String> colEmpTipo;
    @FXML private TableColumn<Empleado, String> colEmpMinutos;

    // ----- Pedidos -----
    @FXML private TextField txtPedCodigo;
    @FXML private TextField txtPedCliente;
    @FXML private ComboBox<TipoPan> cmbTipoPan;
    @FXML private TextField txtPedCantidad;
    @FXML private TableView<Pedido> tblPedidos;
    @FXML private TableColumn<Pedido, String> colPedCodigo;
    @FXML private TableColumn<Pedido, String> colPedCliente;
    @FXML private TableColumn<Pedido, String> colPedTipo;
    @FXML private TableColumn<Pedido, String> colPedCantidad;
    @FXML private TableColumn<Pedido, String> colPedEstado;
    @FXML private TableColumn<Pedido, String> colPedEmpleado;
    @FXML private TableColumn<Pedido, String> colPedMinutos;

    @FXML private Label lblEstado;

    @FXML
    private void initialize() {
        cmbTipoEmpleado.setItems(FXCollections.observableArrayList(
                PanaderiaController.ARTESANAL, PanaderiaController.INDUSTRIAL, PanaderiaController.CAJERO));
        cmbTipoEmpleado.getSelectionModel().selectFirst();
        cmbTipoPan.setItems(FXCollections.observableArrayList(TipoPan.values()));
        cmbTipoPan.getSelectionModel().selectFirst();

        // El campo "caja" solo aplica a cajeros.
        txtEmpCaja.disableProperty().bind(
                cmbTipoEmpleado.valueProperty().isNotEqualTo(PanaderiaController.CAJERO));

        colEmpCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigoEmpleado()));
        colEmpNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colEmpDocumento.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDocumento()));
        colEmpTipo.setCellValueFactory(c -> new SimpleStringProperty(describirTipo(c.getValue())));
        colEmpMinutos.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getMinutosDisponibles())));

        colPedCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigoPedido()));
        colPedCliente.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombreCliente()));
        colPedTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo().name()));
        colPedCantidad.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getCantidad())));
        colPedEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado().name()));
        colPedEmpleado.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getCodigoEmpleadoAsig() == null ? "-" : c.getValue().getCodigoEmpleadoAsig()));
        colPedMinutos.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getMinutosReserva())));
    }

    /** Lo llama App después de cargar el FXML. */
    public void setControlador(PanaderiaController controlador) {
        this.controlador = controlador;
        refrescarTablas();
    }

    // =====================================================================
    // Eventos
    // =====================================================================

    @FXML
    private void onAgregarEmpleado() {
        try {
            int minutos = Integer.parseInt(txtEmpMinutos.getText().trim());
            controlador.agregarEmpleado(cmbTipoEmpleado.getValue(),
                    txtEmpNombre.getText().trim(), txtEmpDocumento.getText().trim(),
                    txtEmpCodigo.getText().trim(), minutos, txtEmpCaja.getText().trim());
            limpiarFormularioEmpleado();
            exito("Empleado registrado.");
        } catch (NumberFormatException e) {
            error("Los minutos disponibles deben ser un número entero.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            error(e.getMessage());
        }
        refrescarTablas();
    }

    @FXML
    private void onRegistrarPedido() {
        try {
            int cantidad = Integer.parseInt(txtPedCantidad.getText().trim());
            controlador.registrarPedido(txtPedCodigo.getText().trim(), txtPedCliente.getText().trim(),
                    cmbTipoPan.getValue(), cantidad);
            limpiarFormularioPedido();
            exito("Pedido registrado en estado PENDIENTE.");
        } catch (NumberFormatException e) {
            error("La cantidad debe ser un número entero.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            error(e.getMessage());
        }
        refrescarTablas();
    }

    @FXML
    private void onAsignarPedido() {
        Pedido seleccionado = tblPedidos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            error("Seleccione un pedido de la tabla.");
            return;
        }
        try {
            controlador.asignarPedido(seleccionado.getCodigoPedido());
            exito("Pedido " + seleccionado.getCodigoPedido() + " asignado al empleado "
                    + seleccionado.getCodigoEmpleadoAsig() + " (" + seleccionado.getMinutosReserva() + " min).");
        } catch (IllegalArgumentException | IllegalStateException e) {
            error(e.getMessage());
        }
        refrescarTablas();
    }

    @FXML
    private void onCancelarPedido() {
        Pedido seleccionado = tblPedidos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            error("Seleccione un pedido de la tabla.");
            return;
        }
        try {
            controlador.cancelarPedido(seleccionado.getCodigoPedido());
            exito("Pedido " + seleccionado.getCodigoPedido() + " cancelado.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            error(e.getMessage());
        }
        refrescarTablas();
    }

    // =====================================================================
    // Utilidades
    // =====================================================================

    private void refrescarTablas() {
        if (controlador == null) {
            return;
        }
        tblEmpleados.setItems(FXCollections.observableArrayList(controlador.getEmpleados()));
        tblPedidos.setItems(FXCollections.observableArrayList(controlador.getPedidos()));
        tblEmpleados.refresh();
        tblPedidos.refresh();
    }

    private String describirTipo(Empleado e) {
        if (e instanceof PanaderoArtesanal) {
            return "Panadero artesanal";
        }
        if (e instanceof PanaderoIndustrial) {
            return "Panadero industrial";
        }
        if (e instanceof Cajero) {
            return "Cajero (caja " + ((Cajero) e).getNumCaja() + ")";
        }
        return e instanceof Panadero ? "Panadero" : "Empleado";
    }

    private void limpiarFormularioEmpleado() {
        txtEmpNombre.clear();
        txtEmpDocumento.clear();
        txtEmpCodigo.clear();
        txtEmpMinutos.clear();
        txtEmpCaja.clear();
    }

    private void limpiarFormularioPedido() {
        txtPedCodigo.clear();
        txtPedCliente.clear();
        txtPedCantidad.clear();
    }

    private void exito(String mensaje) {
        lblEstado.setStyle("-fx-text-fill: #1b7f3b;");
        lblEstado.setText(mensaje);
    }

    private void error(String mensaje) {
        lblEstado.setStyle("-fx-text-fill: #b00020;");
        lblEstado.setText(mensaje);
        Alert alerta = new Alert(Alert.AlertType.WARNING, mensaje);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
