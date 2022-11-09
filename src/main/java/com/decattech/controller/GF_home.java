package com.decattech.controller;

import java.net.URI;

import com.decattech.Main;
import com.decattech.ModifyScenes;
import com.decattech.model.FunctionsD;
import com.functions.models.Objeto;
import com.functions.models.Relatorios;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.awt.Desktop;

import javafx.scene.image.ImageView;

public class GF_home {

    @FXML private ImageView ivLogo_Emp;
    
    @FXML private MenuItem miCadastro_Usuarios,miPropriedades,miLogout,miRelatorios,miLiberar_Telas,miPainel_Pagamentos, miCadastro_Setor,miAlterarSenha, miCadastro_Cliente, miWebDecattech, miWebBS2;
    
    @FXML private VBox vbMenu;
    
    @FXML private MenuButton mbConfig,mbLogin;
    
    @FXML private Label lbUser, Menu;

    @FXML private VBox vbMenu_Children;
    
    @FXML private Label lbVersion;

    @FXML private MenuButton mbCadastros, mbProcesso, mbRelatorio;

    public static GF_home gf;


    @FXML void initialize(){
        Load();
    }

    private void Load(){
        var json = FunctionsD.getJSON("config/config.json");
        //ivLogo_Emp.setImage(FunctionsD.getImage(json.getJSONArray("logo_empresa").getString(1)).getImage());
        lbVersion.setText("Versão: "+Main.version);
        lbUser.setText("Usuário: "+Relatorios.Limited(Main.user.getsFirst("nome_user"),20));
        AnimationMenu();
        MenuAction();

        Platform.runLater(()->{
            gf = this;
        });
    }

    private void MenuAction(){
        //===============PROCESSOS=============== 

            miPainel_Pagamentos.setOnAction(e->{
                Object[] parametros = {"view/processos/GF_painel_pagamentos","Painel de Pagamentos",true};
                ModifyScenes.modify(parametros);
            });
            
            miWebDecattech.setOnAction(e->{
                try{
                    Desktop.getDesktop().browse(new URI("https://decattech.tracksys.com.br/"));
                }catch(Exception e1){
                    e1.printStackTrace();
                }
            });

            miWebBS2.setOnAction(e->{
                try{
                    Desktop.getDesktop().browse(new URI("https://app.empresas.bs2.com/bs2/autenticacao"));
                }catch(Exception e1){
                    e1.printStackTrace();
                }
            });

        //===============PROCESSOS=============== 

        //===============CONFIGURAÇÕES=============== 
            miCadastro_Usuarios.setOnAction(e->{
                Object[] parametros = {"view/cadastros/GF_cadastro_usuario","Cadastro de Usuários",true};
                ModifyScenes.modify(parametros);
            });
            miAlterarSenha.setOnAction(e->{
                Object[] parametros = {"view/configuracoes/GF_alterar_senha","Alterar Senha",false};
                ModifyScenes.modify(parametros);
            });
        //===============CONFIGURAÇÕES=============== 

        //===============CADASTRO===============
        
            miCadastro_Cliente.setOnAction(e->{
                Object[] parametros = {"view/cadastros/GF_cadastro_cliente","Cadastro de Clientes",true};
                ModifyScenes.modify(parametros);
            });

        //===============CADASTRO=============== 

        //===============LOGIN===============
            miLogout.setOnAction(e->{
                Logout();
            });
          
        //===============LOGIN=============== 
    }
    
    private void AnimationMenu(){
		vbMenu.setTranslateX(-500);
		Menu.setOnMouseEntered(e->{
			TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.3));
            slide.setNode(vbMenu);

            slide.setToX(0);
            slide.play();
            vbMenu.setTranslateX(-500);
           
		});

		Menu.setOnMouseClicked(e->{
			if(vbMenu.getTranslateX() == -500){
                TranslateTransition slide = new TranslateTransition();
                slide.setDuration(Duration.seconds(0.3));
                slide.setNode(vbMenu);

                slide.setToX(0);
                slide.play();
                vbMenu.setTranslateX(-500);
               
            }else{
                TranslateTransition slide = new TranslateTransition();
                slide.setDuration(Duration.seconds(0.3));
                slide.setNode(vbMenu);

                slide.setToX(-500);
                slide.play();

                vbMenu.setTranslateX(0);
            }
		});

        MenuButton[] p = {mbCadastros, mbRelatorio,mbConfig,mbProcesso,mbLogin};
        for (int i = 0; i < p.length; i++) {
            MenuButton mb = p[i];
            mb.setOnMouseEntered(e->{
                mb.show();
                for (int j = 0; j < p.length; j++) {
                    if(!p[j].equals(mb)){
                        p[j].hide();
                    }
                }
            });
        }
	}

    public void Logout(){
        Main.user = new Objeto();
        ModifyScenes.CloseAllStages();
        Object[] parametros = {"view/GF_login", Main.title_prog, false};
        ModifyScenes.modify(parametros);
       
    }

}
