package com.decattech.controller.configuracoes;

import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;

import com.decattech.Main;
import com.decattech.ModifyScenes;
import com.decattech.controller.GF_home;
import com.decattech.model.Connection;
import com.decattech.model.FunctionsD;
import com.functions.FunctionsFX;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class GF_alterar_senha {
    
    @FXML private Button btnSave;

    
    @FXML private PasswordField txtPassword_Atual, txtPassword_Confirm, txtPassword_Nova;

    @FXML void initialize(){
        Load();
        
    }

    private void Load(){
        Keys();
    }

    private void Keys(){
        btnSave.setOnAction(e->{
            Update_Password();
        });
    }

    private void Update_Password(){
       
        Object[] verify = {"String","textfield",txtPassword_Atual, txtPassword_Confirm, txtPassword_Nova};
        
        if(FunctionsFX.verify(verify) == false){
            PasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
            if(passwordEncryptor.checkPassword(txtPassword_Atual.getText(), Main.user.getsFirst("password"))){
                if(txtPassword_Confirm.getText().equals(txtPassword_Nova.getText())){
                    Connection.isOpen(true);
                    if(Connection.CED("UPDATE tb_usuarios SET password = '"
                    +passwordEncryptor.encryptPassword(txtPassword_Nova.getText())+"' WHERE id = '"+Main.user.getFirst("id")+"'")){
                        Connection.isOpen(false);
                        FunctionsD.DialogBox("Senha atualizada com sucesso!", 2);
                        //ModifyScenes.close((Stage)txtPassword_Atual.getScene().getWindow());
                        GF_home.gf.Logout();
                    }else{
                        Connection.isOpen(false);
                    }
                   
                }else{
                    FunctionsD.DialogBox("As novas senhas não são iguais!", 1);
                }
            }else{
                FunctionsD.DialogBox("Senha atual incorreta!", 1);
            }
        }
        
    }
}
