package Client.View;

import Client.Model.VinylModel;
import Client.ViewModel.MainViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class MainViewController {
    @FXML
    private TableView<VinylModel> vinylTable;

    @FXML
    private TableColumn<VinylModel, String> titleColumn;

    @FXML
    private TableColumn<VinylModel, String> artistColumn;

    @FXML
    private TableColumn<VinylModel, String> statusColumn;

    @FXML
    private Label statusLabel;

    @FXML
    private Label clientIdLabel;

    @FXML
    private Button reserveButton;

    @FXML
    private Button borrowButton;

    @FXML
    private Button returnButton;

    @FXML
    private Button removeButton;

    private MainViewModel viewModel;

    public void initialize() {
        // Set up the table columns
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        artistColumn.setCellValueFactory(new PropertyValueFactory<>("artist"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Connect to server when view is loaded
        String serverAddress = "localhost"; // Can be changed or made configurable
        int serverPort = 8888;

        viewModel = new MainViewModel(serverAddress, serverPort);

        // Bind the table items to the viewModel's vinyls list
        vinylTable.setItems(viewModel.getVinyls());

        // Bind status message
        statusLabel.textProperty().bind(viewModel.statusMessageProperty());

        // Bind client ID
        clientIdLabel.textProperty().bind(Bindings.concat("Client ID: ", viewModel.clientIdProperty()));


        // Connect to server
        if (!viewModel.connect()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to connect to server.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    @FXML
    private void onReserve() {
        VinylModel selectedVinyl = vinylTable.getSelectionModel().getSelectedItem();
        if (selectedVinyl != null) {
            viewModel.reserveVinyl(selectedVinyl);
        }
    }

    @FXML
    private void onBorrow() {
        VinylModel selectedVinyl = vinylTable.getSelectionModel().getSelectedItem();
        if (selectedVinyl != null) {
            viewModel.borrowVinyl(selectedVinyl);
        }
    }

    @FXML
    private void onReturn() {
        VinylModel selectedVinyl = vinylTable.getSelectionModel().getSelectedItem();
        if (selectedVinyl != null) {
            viewModel.returnVinyl(selectedVinyl);
        }
    }

    @FXML
    private void onRemove() {
        VinylModel selectedVinyl = vinylTable.getSelectionModel().getSelectedItem();
        if (selectedVinyl != null) {
            viewModel.removeVinyl(selectedVinyl);
        }
    }



    @FXML
    private void refreshVinyls() {
        viewModel.refreshVinyls();
    }

    public void shutdown() {
        viewModel.disconnect();
    }
}