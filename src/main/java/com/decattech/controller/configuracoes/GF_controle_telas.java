package com.decattech.controller.configuracoes;

import com.decattech.Main;
import com.decattech.model.Connection;
import com.decattech.model.FunctionsD;
import com.functions.Functions;
import com.functions.FunctionsFX;
import com.functions.models.Combobox;
import com.functions.models.Loading;
import com.functions.models.Objeto;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
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

        Connection.isOpen(true);
        cbGrupo.setItems(FXCollections.observableArrayList(
        Connection.ListCB("""
                SELECT 
                    tg.id, tg.titulo as grupo
                FROM
                    tb_telas_grupos tg
                        LEFT JOIN
                    tb_telas t ON tg.id = t.id_grupo
                WHERE
                    t.status = 1
                group by tg.id""")));

        Object[] psql = {"""
            SELECT 
                user.id,
                user.nome_user as nome,
                user.login,
                user.nivel_acesso
            FROM
                tb_usuarios user
            WHERE
                user.status = 1
                ""","objeto combobox","nome","id","nome"};
        cbNome.setItems(FXCollections.observableArrayList(Connection.query.query(psql)));
        Connection.isOpen(false);
        
        Object[] toFormat = {cbNome};
        FunctionsFX.formatComboBoxObject(toFormat);

        Keys();
        DefineValues();

        Platform.runLater(()->{
            Object[] toBlock = {btnAdicionar,btnLimpar,btnPesquisar,txtPesquisar,tbMenus,tbMenusUser};
            load = new Loading((Stage)txtId.getScene().getWindow(), lbLoading, pbLoader, toBlock);
            Search();
        });
    }

    private void Keys(){
        btnLimpar.setOnAction(e->{
            Clear();
        });

        btnAdicionar.setOnAction(e->{
            Add_Remove();
        });

        tbMenusUser.setOnMouseClicked(e->{
            TableClick();
        });

        tbMenus.setOnMouseClicked(e->{
            TableClick();
        });

        txtId.setOnKeyPressed(e->{
            if(e.getCode() == KeyCode.ENTER){
                cbNome.getEditor().setText(txtId.getText());
                cbNome.requestFocus();
                txtId.requestFocus();
            }
        });

        cbGrupo.setOnMouseClicked(e->{
            if(e.getClickCount() == 2){
                cbGrupo.setValue(null);
            }
        });
    }

    private void Add_Remove(){
        if(cbNome.getValue() == null){
            FunctionsD.DialogBox("Informe o usuário!", 1);
            return;
        }

        if(tbMenus.getSelectionModel().getSelectedIndex() != -1){
            Objeto menu = (Objeto)tbMenus.getSelectionModel().getSelectedItem();
            Objeto user = (Objeto)cbNome.getValue();
            if(FunctionsD.ConfirmationDialog("Adicionando um menu ao usuário", "Tem certeza disso?") == ButtonType.OK){
                String sql = "INSERT INTO tb_permissao_telas SET id_tela = "+menu.getsFirst("id")+", id_user = "+user.getsFirst("id")+""
                +", iduser_cadastro = "+Main.user.getsFirst("id");

                Connection.isOpen(true);
                if(Connection.CED(sql)){
                    FunctionsD.DialogBox("Tela vinculada ao usuário com sucesso!", 2);
                    Search();
                }
                Connection.isOpen(false);
            }
        }else if(tbMenusUser.getSelectionModel().getSelectedIndex() != -1){
            Objeto tela = (Objeto)tbMenus.getSelectionModel().getSelectedItem();
            Objeto user = (Objeto)cbNome.getValue();

            if(FunctionsD.ConfirmationDialog("Removendo uma tela do usuário", "Tem certeza disso?") == ButtonType.OK){
                String sql = "DELETE FROM tb_permissao_telas WHERE (id_tela = "+tela.getsFirst("id")+" AND id_user = "+user.getsFirst("id")+")";

                Connection.isOpen(true);
                if(Connection.CED(sql)){
                    FunctionsD.DialogBox("Tela desvinculada do usuário com sucesso!", 2);
                    Search();
                }
                Connection.isOpen(false);
            }

        }else{
            FunctionsD.DialogBox("Selecione um item na tabela!", 1);
        }
    }

    private void InitTables(String where){
        
        String sql = """
            SELECT 
            t.id, t.id_grupo, t.titulo as nome, tg.titulo as grupo
        FROM
            tb_telas t
                LEFT JOIN
            tb_telas_grupos tg ON t.id_grupo = tg.id
        WHERE
            t.status = 1
                """;

        if(!Functions.isNull(where)){
            sql += where;
        }

        Object[] psql = {sql, "objeto"};

        load.startThread(()->{
            Connection.isOpen(true);
            tbMenus.setItems(FXCollections.observableArrayList(Connection.query.query(psql)));
            Platform.runLater(()->{
                TableClick();
                tbMenus.refresh();
            });
            Connection.isOpen(false);
        });

        if(cbNome.getValue() != null){
            Objeto user = (Objeto) cbNome.getValue();
            String sql_user = """
                SELECT 
                    pt.id,
                    pt.id_tela,
                    t.titulo as nome,
                    t.id_grupo,
                    tg.titulo as grupo,
                    pt.id_user,
                    DATE_FORMAT(pt.dt_cadastro, '%d/%m/%Y %H:%i:%S') AS dt_cadastro,
                    pt.status,
                    pt.iduser_cadastro
                FROM
                    tb_telas t
                        LEFT JOIN
                    tb_permissao_telas pt ON t.id = pt.id_tela
                        LEFT JOIN 
                    tb_telas_grupos tg on t.id_grupo = tg.id
                WHERE 
                    """+" (pt.id_user = "+user.getsFirst("id")+") AND (t.status = 1)";
                
            if(!Functions.isNull(where)){
                sql_user += where;
            }

            Object[] psql_user = {sql_user,"objeto"};

            load.startThread(()->{
                Connection.isOpen(true);
                tbMenusUser.setItems(FXCollections.observableArrayList(Connection.query.query(psql_user)));
                Platform.runLater(()->{
                    tbMenusUser.refresh();
                });
                Connection.isOpen(false);
            });
        }
    }

    private void TableClick(){
        if(tbMenusUser.getSelectionModel().getSelectedIndex() != -1 && tbMenusUser.isFocused()){
            tbMenus.getSelectionModel().clearSelection();
            btnAdicionar.setText("Remover");
        }else{
            tbMenusUser.getSelectionModel().clearSelection();
            btnAdicionar.setText("Adicionar");
        }
    }

    @SuppressWarnings("unchecked")
    private void DefineValues(){
        String[] values = {"nome","grupo","nome","grupo"};
        TableColumn[] cols = {colNome_Menus, colGrupo_Menus, colNome_User, colGrupo_User};
        FunctionsFX.definevalues(values, cols, null);
    }

    private void Search(){
        String where = "";

        if(cbGrupo.getValue() != null){
            where += " AND t.id_grupo = "+cbGrupo.getValue().getId();
        }

        if(cbPesquisar.getValue() != null && !FunctionsFX.isNull(txtPesquisar)){
            String col = cbPesquisar.getValue().getNome();
            
            if(col.equals(colNome_Menus.getText())){
                where += " AND t.titulo like '%"+txtPesquisar.getText()+"%'";
            }else if(col.equals(colGrupo_Menus.getText())){
                where += " AND tg.titulo like '%"+txtPesquisar.getText()+"%'";
            }
        }

        InitTables(where);
    }

    private void Clear(){
        Object[] toClear = {txtId,cbNome,cbGrupo,txtNivel,txtLogin};
        FunctionsFX.ClearObjects(toClear);
        tbMenus.getSelectionModel().clearSelection();
        tbMenusUser.getSelectionModel().clearSelection();
        TableClick();
    }

}
