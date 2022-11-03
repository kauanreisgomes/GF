package com.decattech.controller.processos;

import com.functions.models.Combobox;
import com.functions.models.Loading;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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

        Platform.runLater(()->{
            Object[] toBlock = {tbPagamentos,btnFiltrar,btnLimpar,btnPesquisa,mbOutros,txtPesquisa};
            load = new Loading((Stage)cbCliente.getScene().getWindow(), lbLoading, pbLoader, toBlock);
            Search();
        });
    }

    private void InitTable(String where){

    }

    private void Search(){

    }
}
