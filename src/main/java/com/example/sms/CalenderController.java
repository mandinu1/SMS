package com.example.sms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CalenderController implements Initializable {
    static ArrayList<event> eventList = new ArrayList<>();
    private DatabaseConnection connect;
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    public TableView<event> tableView;
    @FXML
    public TableColumn<event, String> ClubName;
    @FXML
    public TableColumn<event, String> EventName;
    @FXML
    public TableColumn<event, String> Date;

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            data();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ObservableList<event> EventObserver = FXCollections.observableArrayList(eventList);
        ClubName.setCellValueFactory(new PropertyValueFactory<>("ClubName"));
        EventName.setCellValueFactory(new PropertyValueFactory<>("EventName"));
        Date.setCellValueFactory(new PropertyValueFactory<>("Date"));
        tableView.setItems(EventObserver);
    }

    private void data() throws SQLException {

        String selectQuery = "SELECT * FROM `events`;";
        Connection comm = connect.connect();
        try (PreparedStatement statement = comm.prepareStatement(selectQuery)) {
            ResultSet results = statement.executeQuery();
            while (results.next()) {
                event PastEvent = new event(results.getString(3), results.getString(2), results.getString(1));
                eventList.add(PastEvent);
                System.out.println(eventList);


            }
        }
    }
}