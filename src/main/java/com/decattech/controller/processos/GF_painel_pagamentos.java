package com.decattech.controller.processos;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import com.decattech.App;
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
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

public class GF_painel_pagamentos {
    
    @FXML private TableView<Object> tbPagamentos;

    @FXML private TableColumn<Object, String> colValorPago, colVencimento, colCliente, colLiquidacao, colSituacao, colValorTitulo, colFormaPagamento,colButton;

    @FXML private MenuButton mbOutros;
    
    @FXML private ProgressBar pbLoader;

    @FXML private ComboBox<Combobox> cbStatus, cbMes, cbFormaPagamento, cbPesquisa;

    @FXML private Button btnLimpar, btnPesquisa, btnFiltrar;

    @FXML private TextField txtNumeroBs2, txtValorTitulo, txtValorPago, txtPesquisa, txtAno;

    @FXML private DatePicker dtVencimento_Fim, dtVencimento_Inicio, dtLiquidacao_Inicio, dtLiquidacao_Fim;
    
    @FXML private MenuItem miImportarCsv;
    
    @FXML private ComboBox<Object> cbCliente;
    
    @FXML private Label lbLoading;

    private Loading load;

    private List<Combobox> listaFormasPag = new ArrayList<>(), listaSituacao = new ArrayList<>();

    @FXML void initialize(){
        Load();
    }

    private void Load(){
       
        
        Keys();
        Connection.isOpen(true);
        listaFormasPag = Connection.ListCB("SELECT id, nome FROM tb_tipo_pagamentos WHERE status = 1");
        listaSituacao = Connection.ListCB("SELECT id, nome FROM tb_status WHERE id_grupo = 2");
        
        cbFormaPagamento.setItems(FXCollections.observableArrayList(listaFormasPag));
        cbStatus.setItems(FXCollections.observableArrayList(listaSituacao));

        DatePicker[] toFormatDate = {dtVencimento_Fim, dtVencimento_Inicio, dtLiquidacao_Inicio, dtLiquidacao_Fim};
        FunctionsFX.formatDatePicker(toFormatDate);

        Object[] toFormatDouble = {1,2,txtValorPago,txtValorTitulo};

        FunctionsFX.formatRelased(toFormatDouble);

        Object[] toFormatNumber = {1,1,txtNumeroBs2};

        FunctionsFX.formatRelased(toFormatNumber);

       

        List<Combobox> meses = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {
			Object[] format = {i+"","M","format string", "MMMMM"};
         
            meses.add(new Combobox(i, (String)Functions.parseDate(format)));
        }

        cbMes.setItems(FXCollections.observableArrayList(meses));

        cbMes.getSelectionModel().select(FunctionsFX.selectIDCB(LocalDate.now().getMonthValue(), cbMes));

        txtAno.setText(LocalDate.now().getYear()+"");

        List<Combobox> colunas = new ArrayList<>();
        for (int i = 0; i < tbPagamentos.getColumns().size()-1; i++) {
            colunas.add(new Combobox(i+1, tbPagamentos.getColumns().get(i).getText()));
        }
        cbPesquisa.setItems(FXCollections.observableArrayList(colunas));

        DefineValues();
        DefineColumns();

        Object[] psql = {"select id, nome, cpf_cnpj from tb_cliente where status = 1","objeto combobox","nome","id","nome"};
        cbCliente.setItems(FXCollections.observableArrayList(Connection.query.query(psql)));
        Connection.isOpen(false);
        Object[] toEdit = {cbCliente};
        FunctionsFX.formatComboBoxObject(toEdit);

        Platform.runLater(()->{
            Object[] toBlock = {tbPagamentos,btnFiltrar,btnLimpar,btnPesquisa,mbOutros,txtPesquisa};
            load = new Loading((Stage)cbCliente.getScene().getWindow(), lbLoading, pbLoader, toBlock);
            load.setClose(((Stage)cbCliente.getScene().getWindow()).getOnCloseRequest());
            Search();
        });
    }

    private void Keys(){
        miImportarCsv.setOnAction(e->{
            ImportCSV();
        });

        btnFiltrar.setOnAction(e->{
            Search();
        });

        btnPesquisa.setOnAction(e->{
            Search();
        });

        txtPesquisa.setOnKeyPressed(e->{
            if(e.getCode() == KeyCode.ENTER){
                Search();
            }
        });

        btnLimpar.setOnAction(e->{
            Clear();
        });
    }

    private void InitTable(String where){
        String sql = """
            SELECT 
                rp.id,
                rp.mes,
                rp.ano,
                rp.id_cliente,
                cli.nome as cliente,
                DATE_FORMAT(rp.dt_emissao,'%d/%m/%Y %H:%i:%S') as dt_emissao,
                DATE_FORMAT(rp.dt_liquidacao,'%d/%m/%Y %H:%i:%S') as dt_liquidacao,
                DATE_FORMAT(rp.dt_vencimento,'%d/%m/%Y') as dt_vencimento,
                rp.decat_numero,
                rp.bs2_numero,
                rp.valor_titulo,
                rp.valor_liquidado,
                CONCAT('R$',rp.valor_titulo) as valor_titulo_f,
                CONCAT('R$',rp.valor_liquidado) as valor_liquidado_f,
                rp.tipo_pagamento,
                tp.nome as nmtipo,
                rp.dt_cadastro,
                rp.status,
                sts.nome as situacao,
                rp.iduser
            FROM
                tb_registro_pagamentos rp
                    LEFT JOIN
                tb_cliente cli ON rp.id_cliente = cli.id
                    LEFT JOIN
                tb_status sts ON rp.status = sts.id
                    LEFT JOIN
                tb_tipo_pagamentos tp ON rp.tipo_pagamento = tp.id        
                """+" WHERE mes = "+cbMes.getValue().getId()+" AND ano = "+txtAno.getText()+"";
        
        if(!Functions.isNull(where)){
            sql += where;
        }

     

        Object[] psql = {sql,"objeto combobox","cliente","cliente"};
        load.startThread(()->{
            Connection.isOpen(true);
            tbPagamentos.setItems(FXCollections.observableArrayList(Connection.query.query(psql)));
            Connection.isOpen(false);
            Platform.runLater(()->{
                tbPagamentos.refresh();
            });
        });
    }

    @SuppressWarnings("unchecked")
    private void DefineValues(){
        String[] values = {"cliente","dt_vencimento","dt_liquidacao","valor_titulo_f","valor_liquidado_f"};
        TableColumn[] cols = {colCliente,colVencimento,colLiquidacao,colValorTitulo,colValorPago};
        FunctionsFX.definevalues(values, cols,null);
    }

    private void DefineColumns(){
  
        colFormaPagamento.setCellFactory(new Callback<TableColumn<Object, String>, TableCell<Object, String>>() {
			public TableCell<Object, String> call(final TableColumn<Object, String> param) {
				final TableCell<Object, String> cell = new TableCell<Object, String>() {

                    private final ComboBox<Combobox> cbFormas = new ComboBox<>();
                    {
                       
                            
                            cbFormas.setItems(FXCollections.observableArrayList(listaFormasPag));
                            cbFormas.setMaxWidth(Double.POSITIVE_INFINITY);
                            cbFormas.setOnAction(e->{
                                Objeto pagamento = (Objeto)getTableView().getItems().get(getIndex());
                                if(cbFormas.getValue() != null){
                                    var l = new ArrayList<Object>();
                                    l.add(cbFormas.getValue().getId()+"");
                                    pagamento.set("tipo_pagamento", l);
                                }
                            });
                        
                       
                    }

					public void updateItem(String item, boolean empty) {

						super.updateItem(item, empty);

						if (empty) {

							setGraphic(null);

						}

						else {
                            
                            Objeto pagamento = (Objeto)getTableView().getItems().get(getIndex());
                            if(!Functions.isNull(pagamento.getsFirst("tipo_pagamento"))){
                                cbFormas.getSelectionModel().select(FunctionsFX.selectIDCB(Integer.parseInt(pagamento.getsFirst("tipo_pagamento")), cbFormas));
                            }
							setGraphic(cbFormas);

						}
					}
				};
				return cell;
			}
		});
       

        colSituacao.setCellFactory(new Callback<TableColumn<Object, String>, TableCell<Object, String>>() {
			public TableCell<Object, String> call(final TableColumn<Object, String> param) {
				final TableCell<Object, String> cell = new TableCell<Object, String>() {

                    private final ComboBox<Combobox> cbSituacao = new ComboBox<>();
                    {
                       
                            cbSituacao.setItems(FXCollections.observableArrayList(listaSituacao));
                            cbSituacao.setMaxWidth(Double.POSITIVE_INFINITY);
                            cbSituacao.setOnAction(e->{
                                Objeto pagamento = (Objeto)getTableView().getItems().get(getIndex());
                                if(pagamento != null && cbSituacao.getValue() != null){
                                    var l = new ArrayList<Object>();
                                    l.add(cbSituacao.getValue().getId()+"");
                                    pagamento.set("status", l);
                                }
                            });
                        
                    }

					public void updateItem(String item, boolean empty) {

						super.updateItem(item, empty);

						if (empty) {

							setGraphic(null);

						}

						else {
                          
                            Objeto pagamento = (Objeto)getTableView().getItems().get(getIndex());
                            if(!Functions.isNull(pagamento.getsFirst("status"))){
                                cbSituacao.getSelectionModel().select(FunctionsFX.selectIDCB(Integer.parseInt(pagamento.getsFirst("status")), cbSituacao));
                            }
                            
							setGraphic(cbSituacao);

						}
					}
				};
				return cell;
			}
		});
        
        colButton.setCellFactory(new Callback<TableColumn<Object, String>, TableCell<Object, String>>() {
			public TableCell<Object, String> call(final TableColumn<Object, String> param) {
				final TableCell<Object, String> cell = new TableCell<Object, String>() {

                    private final Button btnSave = new Button();
                    {
                       
                            
                            btnSave.setGraphic(FunctionsD.getImage("Icons/confirme.png"));
                            btnSave.setOnAction(e->{
                                
                        
                                if(FunctionsD.ConfirmationDialog("Atualizando um pagamento", "Tem certeza disso?") == ButtonType.OK){
                                    Objeto pagamento = (Objeto)getTableView().getItems().get(getIndex());
                                    Connection.isOpen(true);
                                    boolean noErrors = true;

                                  
                                    if(!Functions.isNull(pagamento.getsFirst("status"))){
                                    
                                        noErrors = Connection.CED("UPDATE tb_registro_pagamentos SET status = "+pagamento.getsFirst("status")+" WHERE id = "+pagamento.getsFirst("id")+" ");
                                        
                                    }
                                   
                                    if(!Functions.isNull(pagamento.getsFirst("tipo_pagamento"))){
                                        
                                        noErrors = Connection.CED("UPDATE tb_registro_pagamentos SET tipo_pagamento = "+pagamento.getsFirst("tipo_pagamento")+" WHERE id = "+pagamento.getsFirst("id")+" ");
                                        
                                    }

                                    if(noErrors){
                                        FunctionsD.DialogBox("Pagamento Atualizado com sucesso!", 2);
                                    }

                                    Connection.isOpen(false);

                                }
                                

                               
                            });
                        
                       
                    }

					public void updateItem(String item, boolean empty) {

						super.updateItem(item, empty);

						if (empty) {

							setGraphic(null);

						}

						else {
                          
                           setGraphic(btnSave);

						}
					}
				};
				return cell;
			}
		});
    }

    private void Search(){
        String where = "";
        
        if(cbMes.getValue() != null && !FunctionsFX.isNull(txtAno)){
            if(cbCliente.getValue() != null){
                Objeto cliente = (Objeto)cbCliente.getValue();
                where += " AND id_cliente = '"+cliente.getsFirst("id")+"'";
            }
    
            if(dtVencimento_Inicio.getValue() != null || dtVencimento_Fim.getValue() != null){
    
                if(dtVencimento_Fim.getValue() == null){
                    dtVencimento_Fim.setValue(LocalDate.now());
                }else if(dtVencimento_Inicio.getValue() == null){
                    dtVencimento_Inicio.setValue(dtLiquidacao_Fim
                                                .getConverter()
                                                .fromString("01/"+dtVencimento_Fim.getValue().getMonth()+"/"+dtVencimento_Fim.getValue().getYear())
                                                );
                }
    
                where += " AND (rp.dt_vencimento BETWEEN '"+dtVencimento_Inicio.getValue().toString()+" 00:00:00' AND '"
                +dtVencimento_Fim.getValue().toEpochDay()+" 23:59:59')";
            }
    
            if(dtLiquidacao_Inicio.getValue() != null || dtLiquidacao_Fim.getValue() != null){
    
                if(dtLiquidacao_Fim.getValue() == null){
                    dtLiquidacao_Fim.setValue(LocalDate.now());
                }else if(dtLiquidacao_Inicio.getValue() == null){
                    dtLiquidacao_Inicio.setValue(dtLiquidacao_Fim
                                                .getConverter()
                                                .fromString("01/"+dtLiquidacao_Fim.getValue().getMonth()+"/"+dtLiquidacao_Fim.getValue().getYear())
                                                );
                }
    
                where += " AND (rp.dt_liquidacao BETWEEN '"+dtLiquidacao_Inicio.getValue().toString()+" 00:00:00' AND '"
                +dtLiquidacao_Fim.getValue().toEpochDay()+" 23:59:59')";
            }
    
            if(!FunctionsFX.isNull(txtNumeroBs2)){
                where += " AND rp.bs2_numero = '"+txtNumeroBs2.getText()+"'"; 
            }
    
            if(!FunctionsFX.isNull(txtValorTitulo)){
                where += " AND CONCAT('R$',rp.valor_titulo) like '%"+txtValorTitulo.getText()+"%'"; 
            }
    
            if(!FunctionsFX.isNull(txtValorPago)){
                where += " AND CONCAT('R$',rp.valor_liquidado) like '%"+txtValorPago.getText()+"%'"; 
            }
    
            if(cbFormaPagamento.getValue() != null){
                where += " AND rp.tipo_pagamento = "+cbFormaPagamento.getValue().getId()+" ";
            }
    
            if(cbStatus.getValue() != null){
                where += " AND rp.status = "+cbStatus.getValue().getId()+" ";
            }
    
            if(cbPesquisa.getValue() != null && !FunctionsFX.isNull(txtPesquisa)){
                String col = cbPesquisa.getValue().getNome();
    
                if(col.equals(colCliente.getText())){
                    where += " AND cli.nome like '%"+txtPesquisa.getText()+"%'";
                }else if(col.equals(colVencimento.getText())){
                    where += " AND  DATE_FORMAT(rp.dt_vencimento,'%d/%m/%Y %H:%i:%S') like '%"+txtPesquisa.getText()+"%'";
                }else if(col.equals(colLiquidacao.getText())){
                    where += " AND  DATE_FORMAT(rp.dt_liquidacao,'%d/%m/%Y %H:%i:%S') like '%"+txtPesquisa.getText()+"%'";
                }else if(col.equals(colValorTitulo.getText())){
                    where += " AND CONCAT('R$',rp.valor_titulo) like '%"+txtPesquisa.getText()+"%'";
                }else if(col.equals(colValorPago.getText())){
                    where += " AND CONCAT('R$',rp.valor_liquidado) like '%"+txtPesquisa.getText()+"%'";
                }else if(col.equals(colFormaPagamento.getText())){
                    where += " AND tp.nome like '%"+txtPesquisa.getText()+"%'";
                }else if(col.equals(colSituacao.getText())){
                    where += " AND sts.nome like '"+txtPesquisa.getText()+"%'";
                }
            }
    
            InitTable(where);
        }else{
            FunctionsD.DialogBox("Preencha os campos obrigátorios!", 1);
            cbMes.requestFocus();
        }
    }

    private void ImportCSV(){
      
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
                                if(RegistrationClientFromCSV(items)){
                                    if(RecordPayments(items)){
                                        Search();
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

    private boolean RegistrationClientFromCSV(List<Object> csv){
        boolean allRight = true;
        JSONObject colunas = FunctionsD.getJSON("config/import.json");
        if(colunas == null){
            allRight = false;
        }else{
            for (int i = 0; i < csv.size(); i++) {
                
                Objeto cliente = (Objeto)csv.get(i);
             
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
                  
                    if(!Connection.CED(sql)){
                        allRight = false;
                    }

                

            }
        }

        return allRight;
    }

    private boolean RecordPayments(List<Object> csv){

        boolean allRight = true;
        JSONObject colunas = FunctionsD.getJSON("config/import.json");
        if(colunas == null){
            allRight = false;
        }else{
            for (int i = 0; i < csv.size(); i++) {
                Objeto cliente = (Objeto)csv.get(i);
               
                boolean ExistsClient = !Connection.Count("SELECT COUNT(*) FROM tb_cliente WHERE cpf_cnpj = '"+cliente.getsFirst(colunas.getString("cpf_cnpj"))+"'").equals("0");
                String type = "INSERT INTO ";
                String where = "";
                
                if(ExistsClient){
                    String idCliente = Connection.Search("SELECT id FROM tb_cliente WHERE cpf_cnpj = '"+cliente.getsFirst(colunas.getString("cpf_cnpj"))+"'").get(0);
                    String vencimento = cliente.getsFirst(colunas.getString("dt_vencimento"));
                    int ano = Integer.parseInt(vencimento.substring(vencimento.lastIndexOf("/")+1));
                    int mes = Integer.parseInt(vencimento.substring(vencimento.indexOf("/")+1, vencimento.lastIndexOf("/")));
                    
                    boolean ExistsPayment = !Connection.Count("SELECT COUNT(*) FROM tb_registro_pagamentos WHERE id_cliente = '"+idCliente+"' AND mes = "+mes+" AND ano = "+ano+"").equals("0");
                   
                    if(ExistsPayment){
                        type = "UPDATE ";
                        where = " WHERE id = '"+Connection.Search("SELECT id FROM tb_registro_pagamentos WHERE id_cliente = '"+idCliente+"' AND mes = "+mes+" AND ano = "+ano+"").get(0)+"'";
                    }
                    
                    Object[] parseDat = {cliente.getsFirst(colunas.getString("dt_emissao")),colunas.getString("dt_emissao_Formato"),"format string","yyyy-MM-dd HH:mm:ss"};
                    String dtEmissao_Formatado = (String) Functions.parseDate(parseDat);
                    
                    boolean hasPagamento = !Functions.isNull(cliente.getsFirst(colunas.getString("dt_liquidacao")));
                    String tipo_pagamento = "";
                    String Valor_Liquido = "";
                    String Liquidacao = "";
                    
                    if(hasPagamento){

                        parseDat[0] = cliente.getsFirst(colunas.getString("dt_liquidacao"));
                        parseDat[1] = colunas.getString("dt_liquidacao_Formato");
                        tipo_pagamento = ", tipo_pagamento = 1";
                        Valor_Liquido = cliente.getsFirst(colunas.getString("valor_liquidado")).replace("R$", "").replace(",", ".").trim();
                        Liquidacao = ", valor_liquidado = '"+Valor_Liquido+"', dt_liquidacao = '"+Functions.parseDate(parseDat)+"'";

                    }
                    

                    parseDat[0] = vencimento;
                    parseDat[1] = colunas.getString("dt_vencimento_Formato");;
                    parseDat[3] = "yyyy-MM-dd";
                    String dtVencimento_Formatado = (String) Functions.parseDate(parseDat);
                   
                    double Valor_Titulo = Double.parseDouble(cliente.getsFirst(colunas.getString("valor_titulo")).replace("R$", "").replace(",", ".").trim());
                    String status = "";

                    boolean wasCanceled = !Functions.isNull(cliente.getsFirst(colunas.getString("dt_cancelado")));
                    
                    if(ExistsPayment == false && wasCanceled == false){
                        status = ", status = '"+verifyStatus(cliente, colunas)+"'";
                    }else if(wasCanceled){
                        Object[] parseDate = {cliente.getsFirst(colunas.getString("dt_cancelado")),colunas.getString("dt_cancelado_Formato"),"format string","yyyy-MM-dd HH:mm:ss"};
                        status = ", status = 6, dt_cancelado = '"+Functions.parseDate(parseDate)+"'";
                    }
                   
                    

                    String sql = type+" tb_registro_pagamentos SET mes = "+mes+", ano = "+ano+", id_cliente = '"+idCliente+"', dt_emissao = '"+dtEmissao_Formatado+"', dt_vencimento = '"+dtVencimento_Formatado+"'"
                    +", decat_numero = '"+cliente.getsFirst(colunas.getString("decat_numero"))+"', bs2_numero = '"+cliente.getsFirst(colunas.getString("bs2_numero"))+"'"
                    +", valor_titulo = '"+Valor_Titulo+"' "+Liquidacao + tipo_pagamento
                    +", iduser = '"+Main.user.getsFirst("id")+"' "+ status + where;
                    

                    //Se der algo errado ele adiciona false ao allRight
                    if(!Connection.CED(sql)){
                        allRight = false;
                    }
                }
            }
        }

        return allRight;
    }

    //Verifica a situação do cliente e retorna o valor do status
    private String verifyStatus(Objeto cliente, JSONObject colunas){
        String dt_vencimento = cliente.getsFirst(colunas.getString("dt_vencimento"));
        String dt_liquidacao = cliente.getsFirst(colunas.getString("dt_liquidacao"));
        String dt_cancelado = cliente.getsFirst(colunas.getString("dt_cancelado"));
       
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate vencimento = LocalDate.parse(dt_vencimento,formatter);
        LocalDate hoje = LocalDate.now();
        String status = "";
        boolean notHaveLiquidation = Functions.isNull(dt_liquidacao);
        boolean notCanceled = Functions.isNull(dt_cancelado);
        
        if(notHaveLiquidation){
            if(vencimento.isBefore(hoje)){
                status = "4";
            }else if(vencimento.isAfter(hoje) || vencimento.isEqual(hoje)){
                status = "3";
            }
        }else if(notCanceled){
            status = "5";
        }else{
            status = "6";
        }

        return status;
        
    }

    private void Clear(){
        Object[] toClear = {cbCliente,dtVencimento_Fim, dtVencimento_Inicio, dtLiquidacao_Inicio, dtLiquidacao_Fim,txtNumeroBs2, txtValorTitulo, txtValorPago,
            cbFormaPagamento,cbStatus};
        FunctionsFX.ClearObjects(toClear);
        tbPagamentos.getSelectionModel().clearSelection();
    }
}
