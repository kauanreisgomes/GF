package com.decattech.controller;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import javafx.scene.image.ImageView;

public class GF_home {

    @FXML private ImageView logo_emp;
    
    @FXML private Label menu, lbuser,lbversion;

    @FXML private VBox vbmenu,vbmenu_children;
    
    @FXML private MenuItem propriedades,liberar_telas,logout,relatorios, cadastro_setor,cadastro_usuarios;

    @FXML private MenuButton menu_cadastros, menu_relatorio,menu_config,menu_processo,menu_login;

    @FXML void initialize(){
        Load();
    }

    private void Load(){
        AnimationMenu();
    }
    
    private void AnimationMenu(){
		vbmenu.setTranslateX(-500);
		menu.setOnMouseEntered(e->{
			TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.3));
            slide.setNode(vbmenu);

            slide.setToX(0);
            slide.play();
            vbmenu.setTranslateX(-500);
           
		});

		menu.setOnMouseClicked(e->{
			if(vbmenu.getTranslateX() != -500){
                TranslateTransition slide = new TranslateTransition();
                slide.setDuration(Duration.seconds(0.3));
                slide.setNode(vbmenu);

                slide.setToX(-500);
                slide.play();

                vbmenu.setTranslateX(0);
            }else{
                TranslateTransition slide = new TranslateTransition();
                slide.setDuration(Duration.seconds(0.3));
                slide.setNode(vbmenu);

                slide.setToX(0);
                slide.play();
                vbmenu.setTranslateX(-500);
            }
		});

        MenuButton[] p = {menu_cadastros, menu_relatorio,menu_config,menu_processo,menu_login};
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

}
