package com.decattech;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.functions.FunctionsFX;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class ModifyScenes {
	
	// List para listar todos os stages
	public static List<Stage> listStages = new ArrayList<>();

	public static boolean maximezed;

	// Funcao para abrir um novo stage/janela.
	/**
	 * @apiNote 0 - Localização do Arquivo FXML* (String)
	 * @apiNote 1 - Título da Tela* (String)
	 * @apiNote 2 - é Maximizavél?* (boolean)
	 * @apiNote 3 - está em tela cheia? (boolean)
	 * @param parametros
	 */
	public static void modify(Object[] parametros){
		Platform.setImplicitExit(true);
		Stage stage = new Stage();
		
		try{
			Scene root = new Scene(Main.loadFXML((String)parametros[0]));
			root.setOnKeyPressed(evento -> {
				if (evento.getCode() == KeyCode.ESCAPE) {
					Object[] Confirm = {"Fechando a tela","Tem certeza disso?"};
					if (FunctionsFX.ConfirmationDialog(Confirm) == ButtonType.OK) {
						close(stage);
					}
				}
			});
			
			stage.setScene(root);
			
		}catch(IOException e){
			e.printStackTrace();
		}

		stage.getIcons().add(new Image(Main.icon));

		stage.setOnCloseRequest(e->{

				e.consume();
				close(stage);

		});

		

		stage.setTitle((String)parametros[1]);


		if((boolean)parametros[2]){
			stage.setMaximized(maximezed);
		}

		if(parametros.length > 3 && parametros[parametros.length-1].getClass().equals(Boolean.class)){
			stage.setMaximized((boolean)parametros[3]);
		}

		

		stage.widthProperty().addListener((obs,was,is)->{
			maximezed = stage.isMaximized();
		});

		stage.setResizable((boolean)parametros[2]);
		
		stage.show();
		stage.toFront();

		listStages.add(stage);

	}

	//Fecha a tela passada como parametro e remove do listStages
	public static void close(Stage stage) {
		if (stage != null) {

			
			if (listStages.lastIndexOf(stage) != -1) {
				listStages.remove(listStages.indexOf(stage));
			}
		
			stage.close();
			
		}
	}

	//Fecha todas as telas que estão na listStages
	public static void CloseAllStages(){
		if(listStages.isEmpty() == false){
			for (int i = 0; i < listStages.size(); i++) {
				
				if(listStages.get(i).isShowing()){
					listStages.get(i).close();
				}

				listStages.remove(listStages.get(i));
			}
		}
	}
}
