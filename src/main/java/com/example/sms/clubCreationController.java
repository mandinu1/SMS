package com.example.sms;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class clubCreationController {
    @FXML
    private TextField name;
    @FXML
    private TextField description;
    @FXML
    private TextField adid;
    @FXML
    private Label namelabel;
    @FXML
    private Label descriplabel;
    @FXML
    private Label idlabel;

    private  DatabaseConnection connectclubcreation;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public clubCreationController() {
    }

    public void clubCreation (ActionEvent event) throws IOException, SQLException {
        String Clubname = name.getText();
        String Clubdescrip = description.getText();
        String advisorID = adid.getText();
        if (!clubcreation_validation(Clubname, Clubdescrip, advisorID)){
            return;
        }
        club Clubs = new club(Clubname,Clubdescrip,advisorID);
        String insertQuery =
                "INSERT INTO clubs(`Name` , `Advisor ID`, `Description`)VALUES(?, ?, ?)";
        Connection connection = connectclubcreation.connect();

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, Clubs.getName());
            preparedStatement.setString(2, Clubs.getAdvisorID());
            preparedStatement.setString(3, Clubs.getDescription());

            int rowInsert = preparedStatement.executeUpdate();

            if (rowInsert > 0) {
                System.out.println("Data inserted successfully");
            } else {
                System.out.println("Data insertion failed");
            }
        }catch (SQLException e){
                e.printStackTrace();
            }

        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("clubcreation.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public boolean clubcreation_validation(String Clubname, String Clubdescrip, String advisorID){
        boolean ResultClubName = Clubname.matches("[a-zA-Z]");//Checks if the club name contains only letters and stores the result of the checking in a boolean
        boolean ResultDescription = Clubdescrip.matches("[a-zA-Z]");//Checks if the club description contains only letters and stores the result of the checking in a boolean
        boolean ResultAdvisorID = advisorID.matches("[a-zA-Z0-9]");//Checks if the advisor ID  contains only letters and numbers and stores the result of the checking in a boolean
        if (ResultClubName == false){
            namelabel.setText("Input only letters");
            return false;
        }
        namelabel.setText(" ");
        if (ResultDescription == false){
            descriplabel.setText("Input only letters");
            return false;
        }
        descriplabel.setText(" ");
        if (ResultAdvisorID == false){
            idlabel.setText("Input only letters and numbers");
            return false;
        }
        idlabel.setText(" ");
        return true;
    }
}
