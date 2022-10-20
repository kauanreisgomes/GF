package com.decattech.controller;

import com.decattech.Main;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

public class GF_login {
    
    @FXML private PasswordField txtpassword;

    
    @FXML private TextField txtlogin;

    
    @FXML private ImageView ivlogo;

    
    @FXML private Label lbversao;

    
    @FXML private Button btnlogin;

    @FXML
    void initialize(){
        Load();
    }

    private void Load(){
        Keys();
        lbversao.setText("Versão: "+Main.version);
    }

    private void Keys(){
        txtlogin.setOnKeyPressed(e->{
            if(e.getCode() == KeyCode.ENTER){
                Logar();
            }
        });

        txtpassword.setOnKeyPressed(e->{
            if(e.getCode() == KeyCode.ENTER){
                Logar();
            }
        });

        btnlogin.setOnAction(e->{
            Logar();
        });
    }

    private void Logar(){
        
    }

}
