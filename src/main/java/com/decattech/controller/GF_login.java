package com.decattech.controller;

import java.io.IOException;

import org.json.JSONObject;

import com.decattech.Main;
import com.decattech.ModifyScenes;
import com.decattech.model.Connection;
import com.decattech.model.FunctionsD;
import com.functions.FunctionsFX;
import com.functions.dao.Query;
import com.functions.models.Objeto;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
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
        var json = FunctionsD.getJSON("config/config.json").getJSONObject("logo_empresa");
        if(json.getBoolean("isvisible")){
            ivLogo.setImage(FunctionsD.getImage(json.getJSONArray("images").getString(0)).getImage());
        }

        Keys();
        //Inicia conexão
        new Connection();
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
        boolean allFilled = !FunctionsFX.verify(verify);
        
        if(allFilled){
            Connection con = new Connection();
            con.isOpen(true);

            boolean existsUser = !con.Count("SELECT count(*) FROM tb_usuarios WHERE status = 1 AND login='"+txtLogin.getText()+"'").equals("0");

            con.isOpen(false);
            
            if(existsUser){
                
                if(con.verifyUser(txtLogin.getText(), txtPassword.getText())){

                    startHome();
                    
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
        Platform.setImplicitExit(true);
		Stage stage = new Stage();
		
		try{
			Scene root = new Scene(Main.loadFXML("view/GF_home"));
			root.setOnKeyPressed(evento -> {
				if (evento.getCode() == KeyCode.ESCAPE) {
					Object[] Confirm = {"Fechando a tela","Tem certeza disso?"};
					if (FunctionsFX.ConfirmationDialog(Confirm) == ButtonType.OK) {
						Platform.exit();
					}
				}
			});
			
			stage.setScene(root);
			
		}catch(IOException e){
			e.printStackTrace();
		}

		stage.getIcons().add(new Image(Main.icon));

		stage.setOnCloseRequest(e->{

				e.consume();
				Platform.exit();

		});

		stage.setTitle(Main.title_prog);

        ModifyScenes.maximezed = stage.isMaximized();

        stage.widthProperty().addListener((obs,was,is)->{
            ModifyScenes.maximezed = stage.isMaximized();
        });
		
		stage.setMaximized(true);

		stage.setResizable(true);
		stage.show();
		stage.toFront();
        ModifyScenes.close((Stage)txtLogin.getScene().getWindow());

    }


}
