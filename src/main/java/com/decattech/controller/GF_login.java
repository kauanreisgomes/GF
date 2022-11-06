package com.decattech.controller;

import com.decattech.Main;
import com.decattech.ModifyScenes;
import com.decattech.model.Connection;
import com.functions.FunctionsFX;
import com.functions.dao.Query;
import com.functions.models.Objeto;

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
        //Inicia conexão
        new Connection();
        Main.query = Connection.query;
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
            Main.query.isOpen(true);
            Object[] count = {"SELECT count(*) FROM tb_usuarios WHERE status = 1 AND login='"+txtLogin.getText()+"'","objeto"};
            boolean existsUser = !Main.query.Count(count).equals("0");
            
            if(existsUser){
                
                if(Connection.verifyUser(txtLogin.getText(), txtPassword.getText())){
                    Object[] parametros = {"view/GF_home",Main.title_prog, true};
                    ModifyScenes.modify(parametros);
                    ModifyScenes.close((Stage)txtLogin.getScene().getWindow());
                 }else{
                    Object[] textDialog = {"Atenção!","Usuário ou Senha incorreto!",1};
                    FunctionsFX.dialogBox(textDialog);
                }

            }else{
                Object[] alertDialog = {"Atenção!","Usuário não existe ou está desativado!",1};
                FunctionsFX.dialogBox(alertDialog);
            }

        }
    }

    public void startHome(){
        
    }

   

    private void nivelAcesso(){

    }

}
