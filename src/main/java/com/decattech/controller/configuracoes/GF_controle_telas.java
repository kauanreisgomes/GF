package com.decattech.controller.configuracoes;

import com.functions.FunctionsFX;
import com.functions.models.Combobox;
import com.functions.models.Loading;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class GF_controle_telas {

    @FXML private ComboBox<Object> cbNome;
    
    @FXML private ProgressBar pbLoader;
    
    @FXML private ComboBox<Combobox> cbGrupo, cbPesquisar;

    @FXML private Button btnPesquisar, btnAdicionar, btnLimpar;

    @FXML private TextField txtLogin, txtNivel, txtId, txtPesquisar;

    @FXML private TableView<Object> tbMenus, tbMenusUser;

    @FXML private TableColumn<Object, String> colGrupo_Menus, colGrupo_User, colNome_Menus, colNome_User;

    @FXML private Label lbLoading;

    private Loading load;

    @FXML void initialize(){
        Load();
    }

    private void Load(){

        Keys();
        DefineValues();

        Platform.runLater(()->{
            Object[] toBlock = {btnAdicionar,btnLimpar,btnPesquisar,txtPesquisar,tbMenus,tbMenusUser};
            load = new Loading((Stage)txtId.getScene().getWindow(), lbLoading, pbLoader, toBlock);
            load.loader(false);
        });
    }

    private void Keys(){

    }

    private void Add_Remove(){
        
    }

    private void InitTables(int i, String where){

    }

    private void DefineValues(){

    }

    private void Search(){

    }

    private void Clear(){
        Object[] toClear = {txtId,cbNome,cbGrupo,txtNivel,txtLogin};
        FunctionsFX.ClearObjects(toClear);
        tbMenus.getSelectionModel().clearSelection();
        tbMenusUser.getSelectionModel().clearSelection();
    }

}
