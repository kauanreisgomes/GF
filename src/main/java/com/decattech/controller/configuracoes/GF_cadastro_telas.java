package com.decattech.controller.configuracoes;

import java.util.ArrayList;
import java.util.List;

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

public class GF_cadastro_telas {
    
    @FXML private ProgressBar pbLoader_telas;

    @FXML private TabPane tabpane;

    @FXML private Tab tabGrupos, tabTelas;

    @FXML private TableView<Object> tbTelas;
    @FXML private TableColumn<Object, String> colDescricao_telas, colTitulo_telas, colId_telas, colSituacao_telas, colGrupo_telas;

    @FXML private TableView<Object> tbGrupo;
    @FXML private TableColumn<Object, String> colTitulo_grupo, colDescricao_grupo, colSituacao_grupo, colId_grupo;
    
    @FXML private Button btnDesativar_grupo, btnLimpar_grupo, btnAdd_grupo, btnPesquisa_grupo;

    @FXML private Button btnLimpar_telas, btnAdd_telas, btnPesquisa_telas, btnDesativar_telas;

    @FXML private CheckBox ckInativos_grupo, ckInativos_telas;

    @FXML private ProgressBar pbLoader_grupo;

    @FXML private ComboBox<Combobox> cbSituacao_grupo, cbPesquisar_grupo;
    
    @FXML private ComboBox<Combobox> cbPesquisa_telas, cbGrupos_telas, cbSituacao_telas;
    
    @FXML private TextField txtId_grupo,txtPesquisa_grupo,txtDescricao_grupo,txtTitulo_grupo;
    
    @FXML private TextField txtPesquisa_telas, txtDescricao_telas, txtTitulo_telas, txtId_telas;

    @FXML private Label lbLoading_telas, lbLoading_grupo;

    private Loading load_grupo, load_telas;

    @FXML void initialize(){
        Load();
    }

    private void Load(){

        Connection connection = new Connection();
        connection.isOpen(true);
        
        Object[] psql = {"SELECT id, nome FROM tb_status WHERE id_grupo = 1"};
        var listStatus = connection.query.listCb(psql);

        cbSituacao_grupo.setItems(FXCollections.observableArrayList(listStatus));
        cbSituacao_telas.setItems(FXCollections.observableArrayList(listStatus));

        cbGrupos_telas.setItems(FXCollections.observableArrayList(connection.ListCB("SELECT id, titulo as nome FROM tb_telas_grupos WHERE status = 1")));

        connection.isOpen(false);

        Object[] format = {1, 9, txtDescricao_telas, txtTitulo_telas, txtId_telas, txtDescricao_grupo,txtTitulo_grupo, txtId_grupo};
        FunctionsFX.formatRelased(format);

        List<Combobox> colunas_telas = new ArrayList<>();
        List<Combobox> colunas_grupos = new ArrayList<>();

        for (int i = 0; i < tbTelas.getColumns().size(); i++) {
            colunas_telas.add(new Combobox(i+1, tbTelas.getColumns().get(i).getText()));
        }

        for (int i = 0; i < tbGrupo.getColumns().size(); i++) {
            colunas_grupos.add(new Combobox(i+1, tbGrupo.getColumns().get(i).getText()));
        }

        cbPesquisa_telas.setItems(FXCollections.observableArrayList(colunas_telas));
        cbPesquisar_grupo.setItems(FXCollections.observableArrayList(colunas_grupos));

        Keys();
        DefineValues();

        Platform.runLater(()->{
            cbSituacao_telas.getSelectionModel().selectFirst();
            cbSituacao_grupo.getSelectionModel().selectFirst();
            Object[] toBlock = {tabpane};
            load_telas = new Loading((Stage)txtId_telas.getScene().getWindow(), lbLoading_telas, pbLoader_telas, toBlock);
            load_grupo = new Loading((Stage)txtId_telas.getScene().getWindow(), lbLoading_grupo, pbLoader_grupo, toBlock);
            Search();
        });
    }

    private void Keys(){
        
        btnAdd_grupo.setOnAction(e->{
            Add_Update(); 
        });

        btnAdd_telas.setOnAction(e->{
            Add_Update();
        });

        btnDesativar_grupo.setOnAction(e->{
            Disable();
        });

        btnDesativar_telas.setOnAction(e->{
            Disable();
        });

        btnLimpar_grupo.setOnAction(e->{
            Clear();
        });

        btnLimpar_telas.setOnAction(e->{
            Clear();
        });

        ckInativos_grupo.setOnAction(e->{
            Search();
        });

        ckInativos_telas.setOnAction(e->{
            Search();
        });

        btnPesquisa_grupo.setOnAction(e->{
            Search();
        });

        btnPesquisa_telas.setOnAction(e->{
            Search();
        });

        txtPesquisa_grupo.setOnKeyPressed(e->{
            if(e.getCode() == KeyCode.ENTER){
                Search();
            }
        });

        txtPesquisa_telas.setOnKeyPressed(e->{
            if(e.getCode() == KeyCode.ENTER){
                Search();
            }
        });

        tbGrupo.setOnMouseClicked(e->{
            TableClick();
        });

        tbTelas.setOnMouseClicked(e->{
            TableClick();
        });

        tabGrupos.setOnSelectionChanged(e->{
            if(tabGrupos.isSelected()){
                Search();
            }
        });

        tabTelas.setOnSelectionChanged(e->{
            if(tabTelas.isSelected()){
                Search();
            }
        });

    }

    private void Add_Update(){

        if(tabTelas.isSelected()){
            Object[] verify = {"string","textfield", txtId_telas,txtTitulo_telas,txtDescricao_telas};
            Object[] verify2 = {"combobox",cbGrupos_telas,cbSituacao_telas};

            boolean allFilled = !(FunctionsFX.verify(verify) || FunctionsFX.verify(verify2));

            if(allFilled){
                String type = "INSERT INTO";
                String where = "";
                String confirmation = "Adicionando uma nova tela";
                String concluded = "Tela adicionada com sucesso!";

                if(tbTelas.getSelectionModel().getSelectedIndex() != -1){
                    Objeto tela = (Objeto)tbTelas.getSelectionModel().getSelectedItem();
                    where = " WHERE id = '"+tela.getsFirst("id")+"'";
                    confirmation = "Atualizando uma tela";
                    concluded = "Tela atualizada com sucesso!";
                }

                if(FunctionsD.ConfirmationDialog(confirmation, "Tem certeza disso?") == ButtonType.OK){
                    String sql = type+" tb_telas SET id_string = '"+txtId_telas.getText()+"', titulo = '"+txtTitulo_telas.getText()+"'"
                    +", descricao = '"+txtDescricao_telas.getText()+"', id_grupo = ("+cbGrupos_telas.getValue().getId()+"), status = ("+cbSituacao_telas.getValue().getId()+")"
                    +", iduser = ("+Main.user.getsFirst("id")+")"+where;

                    Connection connection = new Connection();
                    connection.isOpen(true);
                    if(connection.CED(sql)){
                        FunctionsD.DialogBox(concluded, 2);
                        Clear();
                        Search();
                    }
                    connection.isOpen(false);
                }
            }


        }else if(tabGrupos.isSelected()){
            Object[] verify = {"string","textfield", txtId_grupo,txtTitulo_grupo,txtDescricao_grupo};
            Object[] verify2 = {"combobox",cbSituacao_grupo};

            boolean allFilled = !(FunctionsFX.verify(verify) || FunctionsFX.verify(verify2));

            if(allFilled){
                String type = "INSERT INTO";
                String where = "";
                String confirmation = "Adicionando uma nova grupo";
                String concluded = "Grupo adicionada com sucesso!";

                if(tbTelas.getSelectionModel().getSelectedIndex() != -1){
                    Objeto tela = (Objeto)tbTelas.getSelectionModel().getSelectedItem();
                    where = " WHERE id = '"+tela.getsFirst("id")+"'";
                    confirmation = "Atualizando uma grupo";
                    concluded = "Grupo atualizada com sucesso!";
                }

                if(FunctionsD.ConfirmationDialog(confirmation, "Tem certeza disso?") == ButtonType.OK){
                    String sql = type+" tb_telas_grupos SET idstring = '"+txtId_grupo.getText()+"', titulo = '"+txtTitulo_grupo.getText()+"'"
                    +", descricao = '"+txtDescricao_grupo.getText()+"', status = ("+cbSituacao_grupo.getValue().getId()+")"
                    +", iduser = ("+Main.user.getsFirst("id")+")"+where;

                    Connection connection = new Connection();
                    connection.isOpen(true);
                    if(connection.CED(sql)){
                        FunctionsD.DialogBox(concluded, 2);
                        //Atualiza o Combobox cbGrupos da tabTelas.
                        cbGrupos_telas.setItems(FXCollections.observableArrayList(connection.ListCB("SELECT id, titulo as nome FROM tb_telas_grupos WHERE status = 1")));
                        Clear();
                        Search();
                    }
                    connection.isOpen(false);
                }
            }
        }

    }

    private void Disable(){
        if(tabTelas.isSelected()){
            
            if(tbTelas.getSelectionModel().getSelectedIndex() != -1){
                String value = "";
                String confirmation = "";
                String concluded = "";
                Objeto tela = (Objeto)tbTelas.getSelectionModel().getSelectedItem();

                if(tela.getsFirst("status").equals("1")){
                    confirmation = "Desativando uma tela";
                    concluded = "Tela desativada com sucesso!";
                    value = "2";
                }else{
                    confirmation = "Reativando uma tela";
                    concluded = "Tela reativada com sucesso!";
                    value = "1";
                }

                if(FunctionsD.ConfirmationDialog(confirmation, "Tem certeza disso?") == ButtonType.OK){
                    String sql = "UPDATE tb_telas SET status = "+value+" WHERE id = "+tela.getsFirst("id");
                    Connection connection = new Connection();
                    connection.isOpen(true);
                    if(connection.CED(sql)){
                        FunctionsD.DialogBox(concluded, 2);
                        Clear();
                        Search();
                    }
                    connection.isOpen(false);
                }

            }else{
                FunctionsD.DialogBox("Selecione um item na tabela!", 1);
            }         

        }else if(tabGrupos.isSelected()){
            if(tbGrupo.getSelectionModel().getSelectedIndex() != -1){
                String value = "";
                String confirmation = "";
                String concluded = "";
                Objeto grupo = (Objeto)tbGrupo.getSelectionModel().getSelectedItem();

                if(grupo.getsFirst("status").equals("1")){
                    confirmation = "Desativando uma tela";
                    concluded = "Tela desativada com sucesso!";
                    value = "2";
                }else{
                    confirmation = "Reativando uma tela";
                    concluded = "Tela reativada com sucesso!";
                    value = "1";
                }

                if(FunctionsD.ConfirmationDialog(confirmation, "Tem certeza disso?") == ButtonType.OK){
                    String sql = "UPDATE tb_telas_grupos SET status = "+value+" WHERE id = "+grupo.getsFirst("id");
                    Connection connection = new Connection();
                    connection.isOpen(true);
                    if(connection.CED(sql)){
                        FunctionsD.DialogBox(concluded, 2);
                        //atualiza o Combobox grupos da tabTelas
                        cbGrupos_telas.setItems(FXCollections.observableArrayList(connection.ListCB("SELECT id, titulo as nome FROM tb_telas_grupos WHERE status = 1")));
                        Clear();
                        Search();
                    }
                    connection.isOpen(false);
                }

            }else{
                FunctionsD.DialogBox("Selecione um item na tabela!", 1);
            }   
        }
    }

    private void InitTable(String where){
        if(tabTelas.isSelected()){
            String sql = """
                SELECT 
                    tela.id,
                    tela.id_string,
                    tela.id_grupo,
                    tg.titulo AS grupo,
                    tela.titulo,
                    tela.descricao,
                    DATE_FORMAT(tela.dt_cadastro, '%d/%m/%Y %H:%i:%S') AS dt_cadastro,
                    tela.status,
                    sts.nome AS situacao,
                    tela.iduser
                FROM
                    tb_telas tela
                        LEFT JOIN
                    tb_status sts ON tela.status = sts.id
                        LEFT JOIN
                    tb_telas_grupos tg ON tela.id_grupo = tg.id
                    """ + "WHERE if("+ckInativos_telas.isSelected()+"=true,tela.status in (1,2), tela.status = 1) ";
            
            if(!Functions.isNull(where)){
                sql += where;
            }

            Object[] psql = {sql, "objeto"};

            load_telas.startThread(()->{
                Connection connection = new Connection();
                connection.isOpen(true);
                final var items = FXCollections.observableArrayList(connection.query.query(psql));
                
                connection.isOpen(false);
                Platform.runLater(()->{
                    tbTelas.setItems(items);
                    TableClick();
                    tbTelas.refresh();
                });
            });


        }else if(tabGrupos.isSelected()){

            String sql = """
                SELECT 
                    tela.id,
                    tela.idstring,
                    tela.titulo,
                    tela.descricao,
                    DATE_FORMAT(tela.dt_cadastro, '%d/%m/%Y %H:%i:%S') AS dt_cadastro,
                    tela.status,
                    sts.nome AS situacao,
                    tela.iduser
                FROM
                    tb_telas_grupos tela
                        LEFT JOIN
                    tb_status sts ON tela.status = sts.id
                    """ + " WHERE if("+ckInativos_grupo.isSelected()+"=true,tela.status in (1,2), tela.status = 1)";

            if(!Functions.isNull(where)){
                sql += where;
            }

            Object[] psql = {sql,"objeto"};

            load_grupo.startThread(()->{
                Connection connection = new Connection();
                connection.isOpen(true);
                final var items = FXCollections.observableArrayList(connection.query.query(psql));
               
                connection.isOpen(false);
                Platform.runLater(()->{
                    tbGrupo.setItems(items);
                    TableClick();
                    tbGrupo.refresh();
                });
            });

        }
    }

    private void TableClick(){
        if(tabTelas.isSelected()){

            if(tbTelas.getSelectionModel().getSelectedIndex() != -1){
                btnAdd_telas.setText("Atualizar");
                Objeto tela = (Objeto)tbTelas.getSelectionModel().getSelectedItem();

                if(tela.getsFirst("status").equals("1")){
                    btnDesativar_telas.setText("Desativar");
                }else{
                    btnDesativar_telas.setText("Reativar");
                }

                btnDesativar_telas.setDisable(false);

                txtId_telas.setText(tela.getsFirst("id_string"));
                txtTitulo_telas.setText(tela.getsFirst("titulo"));
                txtDescricao_telas.setText(tela.getsFirst("descricao"));
                cbGrupos_telas.getSelectionModel().select(FunctionsFX.selectIDCB(Integer.parseInt(tela.getsFirst("id_grupo")), cbGrupos_telas));
                cbSituacao_telas.getSelectionModel().select(FunctionsFX.selectIDCB(Integer.parseInt(tela.getsFirst("status")), cbSituacao_telas));


            }else{
                btnAdd_telas.setText("Adicionar");
                btnDesativar_telas.setText("Desativar");
                btnDesativar_telas.setDisable(true);
            }
        
        }else if(tabGrupos.isSelected()){

            if(tbGrupo.getSelectionModel().getSelectedIndex() != -1){
                btnAdd_grupo.setText("Atualizar");
                Objeto grupo = (Objeto)tbGrupo.getSelectionModel().getSelectedItem();

                if(grupo.getsFirst("status").equals("1")){
                    btnDesativar_grupo.setText("Desativar");
                }else{
                    btnDesativar_grupo.setText("Reativar");
                }

                btnDesativar_grupo.setDisable(false);

                txtId_grupo.setText(grupo.getsFirst("idstring"));
                txtTitulo_grupo.setText(grupo.getsFirst("titulo"));
                txtDescricao_grupo.setText(grupo.getsFirst("descricao"));
                cbSituacao_grupo.getSelectionModel().select(FunctionsFX.selectIDCB(Integer.parseInt(grupo.getsFirst("status")), cbSituacao_grupo));
                

            }else{
                btnAdd_grupo.setText("Adicionar");
                btnDesativar_grupo.setText("Desativar");
                btnDesativar_grupo.setDisable(true);
            }

        }
    }

    @SuppressWarnings("unchecked")
    private void DefineValues(){
        String[] values = {"id_string", "titulo", "descricao", "grupo", "situacao"};
        TableColumn[] cols = {colId_telas, colTitulo_telas, colDescricao_telas, colGrupo_telas, colSituacao_telas};
        String[] style = {"-fx-opacity: 0.5","status","2"};
        FunctionsFX.definevalues(values, cols, style);

        String[] values_grupo = {"idstring", "titulo", "descricao", "situacao"};
        TableColumn[] cols_grupo = {colId_grupo, colTitulo_grupo, colDescricao_grupo, colSituacao_grupo};
        FunctionsFX.definevalues(values_grupo, cols_grupo, style);

    }

    private void Search(){
        String where = "";

        if(tabTelas.isSelected()){

            if(cbPesquisa_telas.getValue() != null && !FunctionsFX.isNull(txtPesquisa_telas)){

                String col = cbPesquisa_telas.getValue().getNome();

                if(col.equals(colId_telas.getText())){
                    where += " AND tela.id_string like '%"+txtPesquisa_telas.getText()+"%'";
                }else if(col.equals(colTitulo_telas.getText())){
                    where += " AND tela.titulo like '%"+txtPesquisa_telas.getText()+"%'";
                }else if(col.equals(colDescricao_telas.getText())){
                    where += " AND tela.descricao like '%"+txtPesquisa_telas.getText()+"%'";
                }else if(col.equals(colGrupo_telas.getText())){
                    where += " AND tg.titulo like '%"+txtPesquisa_telas.getText()+"%'";
                }else if(col.equals(colSituacao_telas.getText())){
                    where += " AND sts.nome like '"+txtPesquisa_telas.getText()+"%'";
                }

            }

        }else if(tabGrupos.isSelected()){

            if(cbPesquisar_grupo.getValue() != null && !FunctionsFX.isNull(txtPesquisa_grupo)){

                String col = cbPesquisar_grupo.getValue().getNome();

                if(col.equals(colId_grupo.getText())){
                    where += " AND tela.idstring like '%"+txtPesquisa_grupo.getText()+"%'";
                }else if(col.equals(colTitulo_grupo.getText())){
                    where += " AND tela.titulo like '%"+txtPesquisa_grupo.getText()+"%'";
                }else if(col.equals(colDescricao_grupo.getText())){
                    where += " AND tela.descricao like '%"+txtPesquisa_grupo.getText()+"%'";
                }else if(col.equals(colSituacao_grupo.getText())){
                    where += " AND sts.nome like '"+txtPesquisa_grupo.getText()+"%'";
                }

            }

        }

        InitTable(where);
        
    }

    private void Clear(){
        if(tabTelas.isSelected()){

            Object[] toClear = {txtDescricao_telas, txtTitulo_telas, txtId_telas, cbGrupos_telas};
            cbSituacao_telas.getSelectionModel().selectFirst();
            FunctionsFX.ClearObjects(toClear);
            tbTelas.getSelectionModel().clearSelection();

        }else if(tabGrupos.isSelected()){

            Object[] toClear = {txtId_grupo, txtDescricao_grupo,txtTitulo_grupo};
            cbSituacao_grupo.getSelectionModel().selectFirst();
            FunctionsFX.ClearObjects(toClear);
            tbGrupo.getSelectionModel().clearSelection();

        }
    }


}
