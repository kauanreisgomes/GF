package com.decattech;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

import javax.swing.JOptionPane;

import org.json.JSONArray;
import org.json.JSONObject;

import com.functions.Functions;

public class App {
    public static void main(String[] args) {
		Update();
		//if(VerifyVersion()){
			Main.main(args);
		//}
    }

    public static boolean VerifyVersion(){
		try {

			JSONObject gf = Functions.JsonReader(Files.readAllBytes(new File(Main.class.getResource("config/version.json").toURI()).toPath()));
			Object[] parametrosweb = {"http://localhost:8080/versao/"+gf.getString("id_prog"),1};
			var l = Functions.getJSONfromweb(parametrosweb);
			if(!l.isEmpty()){
				if((boolean) l.get(0)){
					System.out.println(gf.getString("versao"));
					JSONObject jsonweb = (JSONObject)l.get(1);

					if(!jsonweb.getString("versao").equals(gf.getString("versao"))){
						Update();
						return false;
					}
					
					
					Main.version = jsonweb.getString("versao");
					return true;	
					
				}
			}else{
				
				System.out.println("Erro ao verificar a versão!");
				JOptionPane.showMessageDialog(null, "Erro ao verificar versão!\r\nVerifique sua conexão com a internet!", "Erro de Verificação", JOptionPane.ERROR_MESSAGE);
				
			}

		} catch (URISyntaxException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
     
    }

    public static void Update(){
        Main.version = "0.0.1";
    }
}
