package com.decattech.controller.cadastros;

import java.util.ArrayList;
import java.util.List;

import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;

import com.decattech.Main;
import com.decattech.model.Connection;
import com.decattech.model.FunctionsD;
import com.functions.Functions;
import com.functions.FunctionsFX;
import com.functions.dao.Query;
import com.functions.models.Combobox;
import com.functions.models.FileDE;
import com.functions.models.Loading;
import com.functions.models.Objeto;
import com.functions.models.Relatorios;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class GF_cadastro_usuario implements Interface_Cadastro {
    
    
    @FXML private Button btnPesquisa, btnAdd, btnLimpar,btnDesativar;

    @FXML private MenuButton mbOutros;

    @FXML private MenuItem miLiberarTelas;

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

    private volatile Query query = new Connection().query;

    @FXML void initialize(){

        Load();

    }

    @Override
    public void Load() {
        query.isOpen(true);
        Keys();
        DefineValues();

        //Formata para receber somente números.
        Object[] formatReleased = {1,1,txtNivel};
        //Formata para não receber os caracter (') e (").
        Object[] formatReleased2 = {1,9,txtNome,txtUsuario,txtSenha};

        FunctionsFX.formatRelased(formatReleased);
        FunctionsFX.formatRelased(formatReleased2);
        
        List<Combobox> cols = new ArrayList<>();

        for (int i = 0; i < tbUsuarios.getColumns().size(); i++) {
            cols.add(new Combobox(i+1, tbUsuarios.getColumns().get(i).getText()));
        }

        cbPesquisa.setItems(FXCollections.observableArrayList(cols));


        Object[] psql = {"""
            SELECT 
            s.id,s.nome
        FROM
            tb_status s
                LEFT JOIN
            tb_status_grupo sg ON s.id_grupo = sg.id
            WHERE sg.nome = 'cadastro'
                """};

        cbSituacao.setItems(FXCollections.observableArrayList(query.listCb(psql)));
        cbSituacao.getSelectionModel().selectFirst();
        query.isOpen(false);
        Platform.runLater(()->{
            Object[] toBlock = {txtPesquisa,btnPesquisa,btnAdd,mbOutros,btnLimpar,ckInativos,tbUsuarios};
            load = new Loading((Stage)txtSenha.getScene().getWindow(),lbLoading,pgLoader_Bar,toBlock);
            Search();
        });
    }

    @Override
    public void Keys() {
        btnAdd.setOnAction(e->{
            Add_Update();
        });

        btnDesativar.setOnAction(e->{
            Disable();
        });

        btnLimpar.setOnAction(e->{
            Clear();
        });

        tbUsuarios.setOnMouseClicked(e->{
            TableClick();
        });

        btnPesquisa.setOnAction(e->{
            Search();
        });

        txtPesquisa.setOnKeyPressed(e->{
            if(e.getCode() == KeyCode.ENTER){
                Search();
            }
        });

        ckInativos.setOnAction(e->{
            Search();
        });
        
    }

    @Override
    public void Add_Update() {
        Object[] verify = {"string","textfield",txtNome,txtUsuario,txtNivel};
        Object[] verify2= {"combobox",cbSituacao};

        if(FunctionsFX.verify(verify) || FunctionsFX.verify(verify2)){
            return;
        }
        
        

        String type = "", msgConfirmation = "",where = "",msgConcluded = "";
        String sqlc = "SELECT COUNT(*) FROM tb_usuarios WHERE login = '"+txtUsuario.getText()+"'";
        if(tbUsuarios.getSelectionModel().getSelectedIndex() == -1){
            type = "INSERT INTO ";
            msgConfirmation = "Adicionando um novo usuário";
            msgConcluded = "Usuário Adicionado com sucesso!";
        }else{
            Objeto usuario = (Objeto)tbUsuarios.getSelectionModel().getSelectedItem();
            type = "UPDATE ";
            msgConfirmation = "Atualizando um usuário";
            msgConcluded = "Usuário Atualizado com sucesso!";
            where = " WHERE id = '"+usuario.getsFirst("id")+"'";
            sqlc += " AND id != "+usuario.getsFirst("id");
        }

        query.isOpen(true);
        Object[] count = {sqlc};
        boolean existLogin = !query.Count(count).equals("0");
        
        if(existLogin){
            query.isOpen(false);
            FunctionsD.DialogBox("Nome de Usuário já existe!", 1);
            return;
        }

        Object[] confirmDialog = {msgConfirmation,"Tem certeza disso?"};

        if(FunctionsFX.ConfirmationDialog(confirmDialog) == ButtonType.OK){
            PasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
            String senha = "";

            if(FunctionsFX.isNull(txtSenha.getText()) && type.contains("INSERT")){
                senha = ",password = '"+passwordEncryptor.encryptPassword("123")+"'";
            }else if(type.contains("INSERT")){
                senha = ",password = '"+passwordEncryptor.encryptPassword(txtSenha.getText())+"'";
            }

            String sql = type+" tb_usuarios SET nome_user = upper('"+Relatorios.Limited(txtNome.getText(),255)+"'), login = '"+Relatorios.Limited(txtUsuario.getText(),255)+"'"
            +", nivel_acesso = '"
            +txtNivel.getText()+"',status = '"+cbSituacao.getValue().getId()+"', iduser='"+Main.user.getsFirst("id")+"' "+senha+""+where;
            
            
            Object[] psql = {sql};
            
            if(query.CED(psql)){
                Object[] concludedDialog = {"Atenção!",msgConcluded, 2};
                FunctionsFX.dialogBox(concludedDialog);
                Clear();
                Search();
            }
            query.isOpen(false);
        }

        
    }

    @Override
    public void Disable() {
        if(tbUsuarios.getSelectionModel().getSelectedIndex() != -1){
            Objeto usuario = (Objeto)tbUsuarios.getSelectionModel().getSelectedItem();
            String value = "", msgConfirm = "",msgConcluded = "";
            if(usuario.getsFirst("status").equals("1")){
                value = "2";
                msgConfirm = "Desativando um usuário";
                msgConcluded = "Usuário desativado com sucesso!";
            }else{
                value = "1";
                msgConfirm = "Reativando um usuário";
                msgConcluded = "Usuário reativado com sucesso!";
            }
            Object[] confirmDialog = {msgConfirm,"Tem certeza disso?"};

            if(FunctionsFX.ConfirmationDialog(confirmDialog) == ButtonType.OK){
                String sql = "UPDATE tb_usuarios SET status = '"+value+"' WHERE id = '"+usuario.getsFirst("id")+"'";

                Object[] psql = {sql};
                query.isOpen(true);
                if(query.CED(psql)){
                    Object[] concludedDialog = {"Atenção!",msgConcluded,2};
                    FunctionsFX.dialogBox(concludedDialog);
                    Clear();
                    Search();
                }
                query.isOpen(false);

            }


        }else{
            Object[] alertDialog = {"Atenção!","Selecione um item na tabela!", 1};
            FunctionsFX.dialogBox(alertDialog);
        }
        
    }

    @Override
    public void InitTable(String where) {
        String sql = """
            SELECT 
                user.id,
                upper(user.nome_user) as nome_user,
                user.login,
                user.nivel_acesso,
                DATE_FORMAT(user.dt_cadastro,'%d/%m/%Y %H:%i:%S') as dt_cadastro,
                user.status,
                status.nome as situacao,
                user.iduser
            FROM
                tb_usuarios user
                LEFT JOIN tb_status status 
                ON user.status = status.id
                """+" WHERE if("+ckInativos.isSelected()+"=true, user.status in (1,2), user.status = 1)";

        if(!Functions.isNull(where)){
            sql += where;
        }

        Object[] psql = {sql,"objeto"};
        
        load.startThread(()->{
            query.isOpen(true);
            tbUsuarios.setItems(FXCollections.observableArrayList(query.query(psql)));
            Platform.runLater(()->{
                tbUsuarios.refresh();
                TableClick();
            });
            query.isOpen(false);
        });
        
        
    }

    public void TableClick(){
        if(tbUsuarios.getSelectionModel().getSelectedIndex() != -1){

            Objeto user = (Objeto)tbUsuarios.getSelectionModel().getSelectedItem();
            
            btnAdd.setText("Atualizar");
            if(user.getsFirst("status").equals("1")){
                btnDesativar.setText("Desativar");
            }else{
                btnDesativar.setText("Reativar");
            }
            btnDesativar.setDisable(false);

            txtNome.setText(user.getsFirst("nome_user"));
            txtUsuario.setText(user.getsFirst("login"));
            txtNivel.setText(user.getsFirst("nivel_acesso"));
            cbSituacao.getSelectionModel().select(FunctionsFX.selectIDCB(Integer.parseInt(user.getsFirst("status")), cbSituacao));


        }else{

            btnAdd.setText("Adicionar");
            btnDesativar.setText("Desativar");
            btnDesativar.setDisable(true);
        }
    }

    @Override
    public void Search() {
        if(cbPesquisa.getValue() != null && !Functions.isNull(txtPesquisa.getText())){

            String col = cbPesquisa.getValue().getNome();
            String where = "";

            if(col.equals(colId.getText())){
                where = " AND user.id = '"+txtPesquisa.getText()+"'";
            }else if(col.equals(colNivel.getText())){
                where = " AND user.nivel_acesso = '"+txtPesquisa.getText()+"'";
            }else if(col.equals(colNome.getText())){
                where = " AND user.nome_user like '%"+txtPesquisa.getText()+"%'";
            }else if(col.equals(colLogin.getText())){
                where = " AND user.login like '%"+txtPesquisa.getText()+"%'";
            }else if(col.equals(colSituacao.getText())){
                where = " AND  status.nome like '"+txtPesquisa.getText()+"%'";
            }

            InitTable(where);

        }else{
            InitTable(null);
        }
        
    }

    @Override
    @SuppressWarnings("unchecked")
    public void DefineValues() {
        String[] values = {"id","nome_user","login","nivel_acesso","situacao"};
        TableColumn[] cols = {colId,colNome,colLogin,colNivel,colSituacao};
        String[] style = {"-fx-opacity: 0.5","status","2"};
        FunctionsFX.definevalues(values, cols, style);
    }

    @Override
    public void Clear() {
        Object[] ObjectToClear = {txtNivel,txtNome,txtSenha,txtUsuario,cbSituacao};
        FunctionsFX.ClearObjects(ObjectToClear);
        cbSituacao.getSelectionModel().selectFirst();
        tbUsuarios.getSelectionModel().clearSelection();
        TableClick();
    }



}
