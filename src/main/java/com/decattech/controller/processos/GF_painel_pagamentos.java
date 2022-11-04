package com.decattech.controller.processos;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.decattech.model.CsvReader;
import com.functions.FunctionsFX;
import com.functions.models.Combobox;
import com.functions.models.FileChoose;
import com.functions.models.Loading;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class GF_painel_pagamentos {
    
    @FXML private TableView<Object> tbPagamentos;

    @FXML private TableColumn<Object, String> colValorPago, colVencimento, colCliente, colLiquidacao, colSituacao, colValorTitulo, colFormaPagamento;

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

    @FXML void initialize(){
        Load();
    }

    private void Load(){
        DefineValues();
        Keys();
        Platform.runLater(()->{
            Object[] toBlock = {tbPagamentos,btnFiltrar,btnLimpar,btnPesquisa,mbOutros,txtPesquisa};
            load = new Loading((Stage)cbCliente.getScene().getWindow(), lbLoading, pbLoader, toBlock);
            Search();
        });
    }

    private void Keys(){
        miImportarCsv.setOnAction(e->{
            ImportCSV();
        });
    }

    private void InitTable(String where){

    }

    @SuppressWarnings("unchecked")
    private void DefineValues(){
        String[] values = {"Valor Boleto", "ValorPago", "Sacado_Nome"};
        TableColumn[] cols = {colValorTitulo,colValorPago,colCliente};
        FunctionsFX.definevalues(values, cols,null);
    }

    private void Search(){

    }

    private void ImportCSV(){
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        FileChoose choose = new FileChoose("Selecione o arquivo CSV", extFilter, (Stage)lbLoading.getScene().getWindow());
        try {
            System.out.println(choose.getFile().toPath());
            final FileReader fr = new FileReader(choose.getFile());
           
            load.startThread(()->{
                CsvReader csv = new CsvReader();
                Object[] parametros = {(char)';'};
                csv.setSeparator(parametros);
                
                Object[] list = {"objeto",choose.getFile().toPath().toString()};
                List<Object> items = csv.getListFromCSV(list);
            
                tbPagamentos.setItems(FXCollections.observableArrayList(items));
                Platform.runLater(()->{
                    tbPagamentos.refresh();
                });
            });
            fr.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
