package com.decattech.controller.cadastros;

import com.functions.Functions;
import com.functions.models.Combobox;
import com.functions.models.Loading;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class GF_cadastro_usuario implements Interface_Cadastro {
    
    
    @FXML private Button btnLiberarTelas, btnPesquisa, btnAdd, btnLimpar;

    @FXML private ComboBox<Combobox> cbPesquisa,cbSituacao;
    
    @FXML private TextField txtUsuario,txtNivel;

    @FXML private CheckBox ckInativos;

    @FXML private TableView<Object> tbUsuarios;

    @FXML private TableColumn<?, ?> colId, colLogin, colNome, colNivel, colSituacao;

    @FXML private TextField txtPesquisa,txtNome;

    @FXML private PasswordField txtSenha;

    @FXML private Label lbLoading;

    @FXML private ProgressBar pgLoader_Bar;

    private Loading load;

    @FXML void initialize(){
        Load();
    }

    @Override
    public void Load() {
        Keys();

        Platform.runLater(()->{
            Object[] toBlock = {txtPesquisa,btnPesquisa,btnAdd,btnLiberarTelas,btnLiberarTelas,btnLimpar,ckInativos,tbUsuarios};
            load = new Loading((Stage)txtSenha.getScene().getWindow(),lbLoading,pgLoader_Bar,toBlock);
        });
    }

    @Override
    public void Keys() {
        
        
    }

    @Override
    public void Add_Update() {
        
        
    }

    @Override
    public void Disable() {
        
        
    }

    @Override
    public void InitTable(String where) {
        String sql = """
                
                """;

        if(!Functions.isNull(where)){
            sql += where;
        }

        Object[] psql = {sql,"objeto"};

        /*load.startThread(()->{
            tbUsuarios.setItems(FXCollections.observableArrayList(Query.query));
        });*/
        
    }

    @Override
    public void Search() {
        
        
    }

    @Override
    public void DefineValues() {
        
        
    }

    @Override
    public void Clear() {
        
        
    }



}
