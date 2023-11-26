package com.example.sms;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class AttendanceMarkingController implements Initializable {

    ArrayList<String> eventslist = new ArrayList<>();

    @FXML
    private Button absentbtn;

    @FXML
    private TableColumn<Attendance, String> attendance;

    @FXML
    private Button presentbtn;

    @FXML
    private ComboBox<String> selecteventdropdown;

    @FXML
    private TableView<Attendance> stdNameTbl;

    @FXML
    private TableColumn<Attendance, String> studentname;

    @FXML
    private Button backbutton;

    @FXML
    private Button submitattendance;

    private Stage stage; //create variables for scene, stage and root
    private Scene scene;
    private Parent root;
    private DatabaseConnection connectEvent;

   String clubName; //getting the selected club for the table in club attendance.

    ObservableList<Attendance> displayStudent = FXCollections.observableArrayList();
    ObservableList<Attendance> markAttendance = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            allEvents();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ObservableList<String> events = selecteventdropdown.getItems();
        int index = 0;
        while (index < eventslist.size()) {
            events.add(eventslist.get(index));
            index = index+2;
        }

        stdNameTbl.setItems(displayStudent);
    }
    private void allEvents() throws SQLException {
        String selectQuery = "SELECT * FROM `events`;";
        Connection comm = connectEvent.connect();
        try (PreparedStatement statement = comm.prepareStatement(selectQuery)) {
            ResultSet results = statement.executeQuery();
            while (results.next()) {
                for (int i = 0; i < OOPCoursework.clublist.size(); i++) {
                    if (stafflogincontroller.username1 == OOPCoursework.clublist.get(i).getAdvisorID()){
                        clubName = OOPCoursework.clublist.get(i).getName();
                        if (clubName.equals(results.getString(2))){
                            eventslist.add(results.getString(1));
                            eventslist.add(results.getString(4));
                        }
                    }
                }
            }
        }
        try {
            clubMembers();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        studentname.setCellValueFactory(new PropertyValueFactory<Attendance, String>("username1"));
       attendance.setCellValueFactory(new PropertyValueFactory<Attendance, String>("attendence"));


    }
    @FXML
    void back(ActionEvent event)throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("advisor.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    String name;
    String blank;

    public void clubMembers() throws SQLException {
        String selectQuery = "SELECT * FROM `students`;";
        Connection comm = connectEvent.connect();
        int i = 0;
        try (PreparedStatement statement = comm.prepareStatement(selectQuery)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                // Loop through the result set
                while (resultSet.next()) {
                    String columnValue = resultSet.getString("clubs");
                    if (columnValue == null) {
                        i++;
                        continue;
                    }
                    String[] clubs = columnValue.split(",");
                    List<String> arrayList = new ArrayList<>(Arrays.asList(clubs));
                    if (arrayList.contains(ClubAttendanceController.club1)) {
                        name = resultSet.getString("Firstname");
                        blank = "Absent";

                        // Check if the student is already in the displayStudent list
                        boolean studentFound = false;
                        for (Attendance attendance : displayStudent) {
                            if (attendance.getAttendence().equals(name)) {
                                // Update the existing row to present
                                attendance.setAttendence("Present");
                                studentFound = true;
                                break;
                            }
                        }

                        // If the student is not found in the list, add a new row
                        if (!studentFound) {
                            displayStudent.addAll(new Attendance(name, blank));
                        }
                    }
                }
            }
        }
    }

    @FXML
    void markPresent(ActionEvent event) throws IOException {
        Attendance selectedAttendance = stdNameTbl.getSelectionModel().getSelectedItem();
        if (selectedAttendance == null) {
            return;
        }

        // Update the status to "Present"
        selectedAttendance.setAttendence("Present");

        // Refresh the TableView
        stdNameTbl.refresh();
    }

    @FXML
    void markAbsent(ActionEvent event) throws IOException {
        Attendance selectedAttendance = stdNameTbl.getSelectionModel().getSelectedItem();
        if (selectedAttendance == null) {
            return;
        }

        // Update the status to "Present"
        selectedAttendance.setAttendence("Absent");

        // Refresh the TableView
        stdNameTbl.refresh();
    }

    @FXML
    public String submitattendance(ActionEvent event) throws IOException, SQLException {
        String eventName = selecteventdropdown.getValue();
        String club = scheduleController.userClub;
        String date = null;
        for (int i = 0; i < eventslist.size(); i++) {
            if (eventName.equals(eventslist.get(i))) {
                date = eventslist.get(i + 2);
                return date;
            }
        }
        String studentName = stdNameTbl.getSelectionModel().getSelectedItem().getUsername1();
        String attendance = "present";
        Attendance attendance2 = new Attendance(eventName, club, date, studentName ,attendance);
        String insertQuery =
                "INSERT INTO attendanc(`Event Name`, `Club Name`, `Date`, `Student Name`, `Attendance`) VALUES ('?','?','?','?','?')";

        Connection connection = connectEvent.connect();
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, attendance2.getEventName());
            preparedStatement.setString(2, attendance2.getClubName1());
            preparedStatement.setString(3,attendance2.getDate());
            preparedStatement.setString(4, attendance2.getUsername1());
            preparedStatement.setString(5, attendance2.getAttendence());

            int rowsInserted = preparedStatement.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Data inserted successfully!");
            } else {
                System.out.println("Data insertion failed.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("advisor.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        return eventName;
    }


}