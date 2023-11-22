package com.example.sms;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class InchargeclubController  implements Initializable {
    @FXML
    private TableView<Students> studtable;
    @FXML
    private TableColumn<Students, String> usernamecol;
    @FXML
    private TableColumn<Students, String> namecol;
    private DatabaseConnection connectRegister;
    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        usernamecol.setCellValueFactory(new PropertyValueFactory<>("username"));
        namecol.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        club AdvisorClub = null;
        for(int x=0; x < OOPCoursework.clublist.size();x++){
            if(OOPCoursework.clublist.get(x).getAdvisorID().equals(stafflogincontroller.username1)){
                AdvisorClub = OOPCoursework.clublist.get(x);
                break;
            }
        }
        for(int z=0;z<OOPCoursework.studentList.size();z++){
            if(OOPCoursework.studentList.get(z).getClubs().contains(AdvisorClub)){
                studtable.getItems().add(OOPCoursework.studentList.get(z));
            }
        }
    }
    private club getAdvisorClub() {
        club advisorClub = null;
        for (club club : OOPCoursework.clublist) {
            if (club.getAdvisorID().equals(stafflogincontroller.username1)) {
                advisorClub = club;
                break;
            }
        }
        return advisorClub;
    }
    @FXML
    void back (ActionEvent event)throws IOException{
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("advisor.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
     void remove() throws SQLException {
        Students  selectStudent = studtable.getSelectionModel().getSelectedItem();
        studtable.getItems().remove(selectStudent);
        System.out.println(selectStudent);
        club advisorClub = getAdvisorClub();
        int i;
        for( i=0;i<OOPCoursework.studentList.size();i++){
            if(OOPCoursework.studentList.get(i).getUsername().equals(selectStudent.getUsername())){
                ArrayList<club> studentClubs = selectStudent.getClubs();
                studentClubs.remove(advisorClub);
                break;
            }
        }
        System.out.println(advisorClub);
        String insertQuery =
                "UPDATE students SET clubs = ? WHERE Username = ?";
        Connection connection = connectRegister.connect();

        try(PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)){
            preparedStatement.setString(2,OOPCoursework.studentList.get(i).getUsername());
            preparedStatement.setString(1,OOPCoursework.studentList.get(i).clubString());
            int affectedRow = preparedStatement.executeUpdate();
            if (affectedRow >0){
                System.out.println("Updated");
            }else{
                System.out.println("Not updated");
            }
        }
        catch (SQLException e){
            e.printStackTrace();}
        }
     }
