package com.decattech.model;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.json.JSONObject;

import com.decattech.App;
import com.functions.Functions;
import com.functions.FunctionsFX;

import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;

public class FunctionsD {
    
    public static void DialogBox(String text, int type){
        Object[] dialog = {"Atenção!",text,type};
        FunctionsFX.dialogBox(dialog);
    }

    public static ButtonType ConfirmationDialog(String header,String text){
        Object[] dialog = {header,text};
        return FunctionsFX.ConfirmationDialog(dialog);
    }

    public static JSONObject getJSON(String local){
        try {

            return Functions.JsonReader(App.class.getResourceAsStream(local).readAllBytes());
           
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public static ImageView getImage(String local){
      
        try {
            return FunctionsFX.tratamentodeIcon(new File(App.class.getResource(local).toURI()).toPath().toString());
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
}
