package com.decattech.controller;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import javafx.scene.image.ImageView;

public class GF_home {

    @FXML private ImageView logo_emp;
    
    @FXML private Label menu, menuback,lbuser;

    @FXML private VBox vbmenu,vbmenu_children;
    
    @FXML private MenuItem propriedades,liberar_telas,logout,relatorios, cadastro_setor,menu_login,cadastro_usuarios;

    @FXML private MenuButton menu_cadastros, menu_relatorio,menu_config,menu_processo;

    void initialize(){
        Load();
    }

    private void Load(){
        AnimationMenu();
    }
    
    private void AnimationMenu(){
		vbmenu_children.setTranslateX(-200);
		menu.setOnMouseClicked(e->{
			TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.5));
            slide.setNode(vbmenu_children);

            slide.setToX(0);
            slide.play();
            vbmenu_children.setTranslateX(-200);
            slide.setOnFinished(e1->{
                menu.setVisible(false);
                menuback.setVisible(true);
            });
		});

		menuback.setOnMouseClicked(e->{
			
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.5));
            slide.setNode(vbmenu_children);

            slide.setToX(-200);
            slide.play();

            vbmenu_children.setTranslateX(0);

            slide.setOnFinished(e1->{
                menu.setVisible(true);
                menuback.setVisible(false);
            });
		});
	}

}
