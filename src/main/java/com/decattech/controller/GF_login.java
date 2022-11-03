package com.decattech.controller;

import com.decattech.Main;
import com.decattech.ModifyScenes;
import com.functions.FunctionsFX;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class GF_login {
    
    @FXML private PasswordField txtPassword;

    
    @FXML private TextField txtLogin;

    
    @FXML private ImageView ivLogo;

    
    @FXML private Label lbVersao;

    
    @FXML private Button btnLogin;

    @FXML
    void initialize(){
        Load();
    }

    private void Load(){
        Keys();
        lbVersao.setText("Versão: "+Main.version);
    }

    private void Keys(){
        txtLogin.setOnKeyPressed(e->{
            if(e.getCode() == KeyCode.ENTER){
                Logar();
            }
        });

        txtPassword.setOnKeyPressed(e->{
            if(e.getCode() == KeyCode.ENTER){
                Logar();
            }
        });

        btnLogin.setOnAction(e->{
            Logar();
        });
    }

    private void Logar(){
        Object[] verify = {"String","Textfield",txtLogin,txtPassword};

        if(FunctionsFX.verify(verify) == false){

            if(txtLogin.getText().equals("kauan") && txtPassword.getText().equals("123")){
                Object[] parametros = {"view/GF_home","Gestor Financeiro - GF", true};
                ModifyScenes.modify(parametros);
                ModifyScenes.close((Stage)txtLogin.getScene().getWindow());
            }else{
                Object[] textDialog = {"Atenção!","Usuário ou Senha incorreto!",1};
                FunctionsFX.dialogBox(textDialog);
            }

        }
    }

}
