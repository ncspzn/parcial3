package application;

import controllers.PanaderiaController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Panaderia;
import viewControllers.PanaderiaViewController;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PanaderiaView.fxml"));
        Parent root = loader.load();

        PanaderiaViewController viewController = loader.getController();
        viewController.setControlador(new PanaderiaController(new Panaderia("Panadería El Trigal")));

        stage.setTitle("Panadería El Trigal");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
