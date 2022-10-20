package com.decattech;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

import org.json.JSONArray;
import org.json.JSONObject;

import com.functions.Functions;

public class App {
    public static void main(String[] args) {
		if(VerifyVersion()){
			Main.main(args);
		}
    }

    public static boolean VerifyVersion(){
        Object[] parametrosweb = {"http://localhost:8080/versao/2",1};
		var l = Functions.getJSONfromweb(parametrosweb);
		if(!l.isEmpty()){
			if((boolean) l.get(0)){
				
				JSONObject jsonweb = (JSONObject)l.get(1);
				//var jsongpe = (JSONArray)jsonweb.get("java");
			
				try {
					JSONObject gf = Functions.JsonReader(new File(Main.class.getResource("config/version.json").toURI()));
					if(!jsonweb.getString("versao").equals(gf.getString("versao"))){
						Update();
						return false;
					}
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Main.version = jsonweb.getString("versao");
				return true;	
				
			}
		}else{
			System.out.println("Erro ao verificar a versão!");
			JOptionPane.showMessageDialog(null, "Erro ao verificar versão!\r\nVerifique sua conexão com a internet!", "Erro de Verificação", JOptionPane.ERROR_MESSAGE);
			
		}
		return false;
     
    }

    public static void Update(){
        System.out.println("testeee");
    }
}
