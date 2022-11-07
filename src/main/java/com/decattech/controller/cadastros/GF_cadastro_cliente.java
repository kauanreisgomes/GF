package com.decattech.controller.cadastros;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.decattech.Main;
import com.decattech.model.Connection;
import com.decattech.model.CsvReader;
import com.decattech.model.FunctionsD;
import com.functions.Functions;
import com.functions.FunctionsFX;
import com.functions.models.Combobox;
import com.functions.models.FileChoose;
import com.functions.models.Loading;
import com.functions.models.Objeto;
import com.functions.models.Relatorios;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class GF_cadastro_cliente implements Interface_Cadastro {
    
    @FXML private Button btnAdicionar, btnDesativar, btnLimpar, btnPesquisar;

    @FXML private ComboBox<Combobox> cbPesquisar, cbSituacao, cbTipo;

    @FXML private MenuButton mbOutros;

    @FXML private MenuItem miImportar;
    
    @FXML private CheckBox ckInativos;

    @FXML private TableView<Object> tbCliente;

    @FXML private TableColumn<Object, String> colCpfCnpj, colEmail, colNome, colSituacao, colTel01, colTel02, colTipo;

    @FXML private Label lbLoading;
    
    @FXML private ProgressBar pbLoader;

    @FXML private TextField txtBairro, txtCep, txtCidade, txtCpfCnpj, txtEmail, txtEstado, txtLogradouro, txtNome, txtPesquisar, txtTelefone01, txtTelefone02;

    private Loading load;

    @FXML void initialize(){
        Load();
    }

    public void Load(){

        List<Combobox> Colunas = new ArrayList<>();

        for (int i = 0; i < tbCliente.getColumns().size(); i++) {
            Colunas.add(new Combobox(i+1, tbCliente.getColumns().get(i).getText()));
        }

        Object[] toFomartString = {1, 9, txtBairro, txtCep, txtCidade, txtCpfCnpj, txtEmail, txtEstado, txtLogradouro, txtNome, txtTelefone01, txtTelefone02};
        FunctionsFX.formatRelased(toFomartString);

        cbPesquisar.setItems(FXCollections.observableArrayList(Colunas));
        Connection.isOpen(true);
        cbSituacao.setItems(FXCollections.observableArrayList(Connection.ListCB("SELECT id, nome FROM tb_status WHERE id_grupo = 1")));
        Connection.isOpen(false);

        List<Combobox> tipos = new ArrayList<>();
        tipos.add(new Combobox(1,"Fisica"));
        tipos.add(new Combobox(2,"Juridica"));
        cbTipo.setItems(FXCollections.observableArrayList(tipos));

        btnPesquisar.setGraphic(FunctionsD.getImage("icons/lupa.png"));

        Keys();
        DefineValues();

        Platform.runLater(()->{
            Object[] toBlock = {txtPesquisar, btnPesquisar, btnAdicionar, btnDesativar, btnLimpar, tbCliente, ckInativos, mbOutros};
            load = new Loading((Stage)txtCep.getScene().getWindow(), lbLoading, pbLoader, toBlock);
            Search();
        });
    }

    @Override
    public void Keys() {
        btnAdicionar.setOnAction(e->{
            Add_Update();
        });

        btnDesativar.setOnAction(e->{
            Disable();
        });

        btnLimpar.setOnAction(e->{
            Clear();
        });

        ckInativos.setOnAction(e->{
            Search();
        });

        btnPesquisar.setOnAction(e->{
            Search();
        });

        txtPesquisar.setOnKeyPressed(e->{
            if(e.getCode() == KeyCode.ENTER){
                Search();
            }
        });

        tbCliente.setOnMouseClicked(e->{
            TableClick();
        });

        miImportar.setOnAction(e->{
            ImportClient();
        });
        
    }

    @Override
    public void Add_Update() {
        Object[] toVerify = {"String", "textfield", txtBairro, txtCep, txtCidade, txtEmail, txtEstado, txtLogradouro, txtNome, txtTelefone01};
        Object[] toVerify2 = {"combobox", cbSituacao, cbTipo};
        
        boolean allRight = !(FunctionsFX.verify(toVerify) && FunctionsFX.verify(toVerify2));

        if(allRight){
         
            if(cbTipo.getValue().getId() == 1){
                Object[] verifyCpf = {"cpf","",txtCpfCnpj};
                allRight = !FunctionsFX.verify(verifyCpf);
              
            }else{
                Object[] verifyCnpj = {"cnpj","",txtCpfCnpj};
                allRight = !FunctionsFX.verify(verifyCnpj);
            }

            if(allRight){

                String type = "";
                String where = "";
                String msgConfirmation = "";
                String msgConcluded = "";
                if(tbCliente.getSelectionModel().getSelectedIndex() == -1){
                    type = "INSERT INTO";
                    msgConfirmation = "Adicionando um cliente";
                    msgConcluded = "Cliente adicionado com sucesso!";
                }else{
                    type = "UPDATE";
                    msgConfirmation = "Atualizando um cliente";
                    msgConcluded = "Cliente atualizado com sucesso!";
                    Objeto cliente = (Objeto)tbCliente.getSelectionModel().getSelectedItem();
                    where = " WHERE id = '"+cliente.getsFirst("id")+"'";
                }

                if(FunctionsD.ConfirmationDialog(msgConfirmation, "Tem certeza disso?") == ButtonType.OK){
                    
                    String sql = type+" tb_cliente SET nome = '"+Relatorios.Limited(txtNome.getText(), 255)+"', cpf_cnpj = '"+txtCpfCnpj.getText()+"'"
                    +", tipo_pessoa = '"+cbTipo.getValue().getNome().substring(0,1)+"', email = '"+Relatorios.Limited(txtEmail.getText(), 255)+"'"
                    +", telefone01 = '"+Relatorios.Limited(txtTelefone01.getText(),20)+"', telefone02 = '"+Relatorios.Limited(txtTelefone02.getText(),20)+"'"
                    +", logradouro = '"+Relatorios.Limited(txtLogradouro.getText(), 255)+"', bairro = '"+Relatorios.Limited(txtBairro.getText(), 255)+"'"
                    +", cidade = '"+Relatorios.Limited(txtCidade.getText(), 255)+"', estado = '"+Relatorios.Limited(txtEstado.getText(), 255)+"'"
                    +", cep = '"+Relatorios.Limited(txtCidade.getText(), 255)+"', iduser = '"+Main.user.getsFirst("id")+"', status = "+cbSituacao.getValue().getId()+"" + where;
                    
                    Connection.isOpen(true);
                    if(Connection.CED(sql)){
                        FunctionsD.DialogBox(msgConcluded, 2);
                        Clear();
                        Search();
                    }
                    Connection.isOpen(false);
                }

            }
        }
    }

    @Override
    public void Disable() {
        
        if(tbCliente.getSelectionModel().getSelectedIndex() != -1){
            String value = "";
            String msgConfirmation = "";
            String msgConcluded = "";
            Objeto cliente = (Objeto)tbCliente.getSelectionModel().getSelectedItem();
            
            if(cliente.getsFirst("status").equals("1")){
                value = "2";
                msgConfirmation = "Desativando um cliente";
                msgConcluded = "Cliente desativado com sucesso!";
            }else{
                value = "1";
                msgConfirmation = "Reativando um cliente";
                msgConcluded = "Cliente reativado com sucesso!";
            }

            if(FunctionsD.ConfirmationDialog(msgConfirmation, "Tem certeza disso?") == ButtonType.OK){
                String sql = "UPDATE tb_cliente SET status = "+value+" WHERE id = '"+cliente.getsFirst("id")+"'";

                Connection.isOpen(true);
                if(Connection.CED(sql)){
                    FunctionsD.DialogBox(msgConcluded, 2);
                    Clear();
                    Search();
                }
                Connection.isOpen(false);
            }

        }else{
            FunctionsD.DialogBox("Selecione um cliente para desabilitar!", 1);
        }
        
    }

    @Override
    public void InitTable(String where) {
        String sql = """
            SELECT 
                cli.id,
                cli.nome,
                cli.cpf_cnpj,
                cli.tipo_pessoa,
                if(cli.tipo_pessoa = 'F','Fisica', 'Juridica') as tipo,
                IF(cli.cpf_cnpj != 0,IF(cli.tipo_pessoa = 'J',
                            CONCAT(SUBSTRING(cli.cpf_cnpj, 1, 2),
                                    '.',
                                    SUBSTRING(cli.cpf_cnpj, 3, 3),
                                    '.',
                                    SUBSTRING(cli.cpf_cnpj, 6, 3),
                                    '/',
                                    SUBSTRING(cli.cpf_cnpj, 9, 4),
                                    '-',
                                    SUBSTRING(cli.cpf_cnpj, 13, 2)),
                            CONCAT(SUBSTR(cli.cpf_cnpj, 1, 3),
                                    '.',
                                    SUBSTR(cli.cpf_cnpj, 4, 3),
                                    '.',
                                    SUBSTR(cli.cpf_cnpj, 7, 3),
                                    '-',
                                    SUBSTR(cli.cpf_cnpj, 10, 2))),'') as cpf_cnpj_f,
                cli.email,
                cli.telefone01,
                cli.telefone02,
                cli.logradouro,
                cli.bairro,
                cli.cidade,
                cli.estado,
                cli.cep,
                cli.dt_cadastro,
                cli.status,
                sts.nome as situacao,
                cli.iduser
            FROM
                tb_cliente cli
                LEFT JOIN tb_status sts
                ON cli.status = sts.id
                """+" WHERE if("+ckInativos.isSelected()+" = true, cli.status in (1,2),cli.status = 1) ";
        
        
        if(!Functions.isNull(where)){
            sql += where;
        }

        Object[] psql = {sql,"objeto"};

        load.startThread(()->{
            Connection.isOpen(true);
            tbCliente.setItems(FXCollections.observableArrayList(Connection.query.query(psql)));
            Connection.isOpen(false);
            Platform.runLater(()->{
                TableClick();
                tbCliente.refresh();
            });
        });

    }

    public void TableClick(){
        if(tbCliente.getSelectionModel().getSelectedIndex() != -1){
            btnAdicionar.setText("Atualizar");
            var cliente = (Objeto)tbCliente.getSelectionModel().getSelectedItem();
            if(cliente.getsFirst("status").equals("1")){
                btnDesativar.setText("Desativar");
            }else{
                btnDesativar.setText("Reativar");
            }
            btnDesativar.setDisable(false);

            txtNome.setText(cliente.getsFirst("nome"));
            txtCpfCnpj.setText(cliente.getsFirst("cpf_cnpj_f"));
            txtEmail.setText(cliente.getsFirst("email"));
            cbTipo.getSelectionModel().select(FunctionsFX.selectCB(cliente.getsFirst("tipo"), cbTipo));
            txtTelefone01.setText(cliente.getsFirst("telefone01"));
            txtTelefone02.setText(cliente.getsFirst("telefone02"));
            txtLogradouro.setText(cliente.getsFirst("logradouro"));
            txtBairro.setText(cliente.getsFirst("bairro"));
            txtCidade.setText(cliente.getsFirst("cidade"));
            txtEstado.setText(cliente.getsFirst("estado"));
            txtCep.setText(cliente.getsFirst("cep"));
            cbSituacao.getSelectionModel().select(FunctionsFX.selectIDCB(Integer.parseInt(cliente.getsFirst("status")), cbSituacao));

        }else{
            btnAdicionar.setText("Adicionar");
            btnDesativar.setText("Desativar");
            btnDesativar.setDisable(true);
        }
    }

    @Override
    public void Search() {
        String where = "";

        if(cbPesquisar.getValue() != null && !FunctionsFX.isNull(txtPesquisar)){
            String col = cbPesquisar.getValue().getNome();

            if(col.equals(colNome.getText())){
                where += " AND cli.nome like '%"+txtPesquisar.getText()+"%'";
            }else if(col.equals(colCpfCnpj.getText())){
                where += " AND  cli.cpf_cnpj like '%"+txtPesquisar.getText().replace(".", "").replace("/", "").replace("-", "")+"%'";
            }else if(col.equals(colTipo.getText())){
                where += " AND if(cli.tipo_pessoa = 'F','Fisica', 'Jurídica' like '%"+txtPesquisar.getText()+"%'";
            }else if(col.equals(colEmail.getText())){
                where += " AND cli.email like '%"+txtPesquisar.getText()+"%'";
            }else if(col.equals(colTel01.getText())){
                where += " AND cli.telefone01 like '%"+txtPesquisar.getText()+"%'";
            }else if(col.equals(colTel02.getText())){
                where += " AND cli.telefone02 like '%"+txtPesquisar.getText()+"%'";
            }else if(col.equals(colSituacao.getText())){
                where += " AND sts.nome like '"+txtPesquisar.getText()+"%'";
            }
        }

        InitTable(where);
        
    }

    @Override
    @SuppressWarnings("unchecked")
    public void DefineValues() {
        String[] values = {"nome","cpf_cnpj_f","tipo","email","telefone01","telefone02","situacao"};
        TableColumn[] cols = {colNome,colCpfCnpj,colTipo,colEmail,colTel01,colTel02,colSituacao};
        String[] style = {"-fx-opacity: 0.5","status","2"};
        FunctionsFX.definevalues(values, cols, style);
        
    }

    @Override
    public void Clear() {
        Object[] toClear = {txtBairro, txtCep, txtCidade, txtCpfCnpj, txtEmail, txtEstado, txtLogradouro, txtNome, txtTelefone01, txtTelefone02, cbSituacao, cbTipo};
        FunctionsFX.ClearObjects(toClear);
        tbCliente.getSelectionModel().clearSelection();
        TableClick();
        
    }

    public void ImportClient(){
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        FileChoose choose = new FileChoose("Selecione o arquivo CSV", extFilter, (Stage)lbLoading.getScene().getWindow());
        try {
            boolean hasFile = choose.getFile() != null;
            if(hasFile){
                final FileReader fr = new FileReader(choose.getFile());
                
                if(FunctionsD.ConfirmationDialog("Você está importando um arquivo", "Tem certeza disso?") == ButtonType.OK){
                    load.startThread(()->{

                        Connection.isOpen(true);
                        CsvReader csv = new CsvReader();
                        char separtor = FunctionsD.getJSON("config/import.json").getString("separator").toCharArray()[0];
                        Object[] parametros = {separtor};
                        csv.setSeparator(parametros);
                        
                        Object[] toList = {"objeto",choose.getFile().toPath().toString()};
                        List<Object> items = csv.getListFromCSV(toList);
                        
                        if(!items.isEmpty()){
                            Boolean allRight = true;
                            JSONObject colunas = FunctionsD.getJSON("config/import.json");
                            if(colunas == null){
                               
                            }else{
                                for (int i = 0; i < items.size(); i++) {
                                    
                                    Objeto cliente = (Objeto)items.get(i);
                                 
                                    boolean ExistsClient = !Connection.Count("SELECT COUNT(*) FROM tb_cliente WHERE cpf_cnpj = '"+cliente.getsFirst(colunas.getString("cpf_cnpj"))+"'").equals("0");
                                    String type = "INSERT INTO ";
                                    String where = "";
                                    if(ExistsClient){
                                        type = "UPDATE ";
                                        String idcliente = Connection.Search("SELECT id FROM tb_cliente WHERE cpf_cnpj = '"+cliente.getsFirst(colunas.getString("cpf_cnpj"))+"'").get(0);
                                        where = " WHERE id = '"+idcliente+"'";
                                    }
                                        String sql = type+" tb_cliente SET nome = upper('"+cliente.getsFirst(colunas.getString("nome"))+"'), cpf_cnpj = '"+cliente.getsFirst(colunas.getString("cpf_cnpj"))+"'"
                                        +", tipo_pessoa = '"+cliente.getsFirst(colunas.getString("tipo_pessoa")).substring(0, 1).toUpperCase()+"', email = '"+cliente.getsFirst(colunas.getString("email"))+"'"
                                        +", telefone01 = '"+cliente.getsFirst(colunas.getString("telefone"))+"', logradouro = '"+cliente.getsFirst(colunas.getString("logradouro"))+"'"
                                        +", bairro = '"+cliente.getsFirst(colunas.getString("bairro"))+"' , cidade = '"+cliente.getsFirst(colunas.getString("cidade"))+"'"
                                        +", estado = '"+cliente.getsFirst(colunas.getString("estado"))+"', cep = '"+cliente.getsFirst(colunas.getString("cep"))+"', iduser = '"+Main.user.getsFirst("id")+"'"+where;
                                      
                                        allRight = Connection.CED(sql);
                    
                                    
                    
                                }
                                if(allRight){
                                    FunctionsD.DialogBox("Clientes Importados com sucesso!", 2);
                                }
                            }
                        }

                        Connection.isOpen(false);

                    });
                }
                fr.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
