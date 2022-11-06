package com.decattech.model;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.json.JSONObject;

import com.decattech.App;
import com.decattech.Main;
import com.functions.Functions;
import com.functions.dao.Query;
import com.functions.models.Combobox;
import com.functions.models.Objeto;

public class Connection {
    public static Query query;
    public Connection(){
        try{
            
            byte[] fileFontes = App.class.getResourceAsStream("config/config.json").readAllBytes();
            byte[] fileServer = App.class.getResourceAsStream("config/server.json").readAllBytes();
            JSONObject fontes = Functions.JsonReader(fileFontes);
            JSONObject server = Functions.JsonReader(fileServer);

            for (int i = 0; i < fontes.getJSONArray("fontes").length(); i++) {
                JSONObject fonte = fontes.getJSONArray("fontes").getJSONObject(i);
                if(server.getString("server").equals(fonte.getString("server"))){
                    query = new Query(fonte.getString("url"), fonte.getString("user"), fonte.getString("password"));
                    break;
                }
            }

        }catch(IOException e){
            e.printStackTrace();
            System.out.println("Erro ao pegar o arquivo json\r\nFunção: Connection.Connection()");
        }
    
    }

    public static boolean verifyUser(String user, String password){
        
        query.isOpen(true);
      
        Object[] psql = {"SELECT id, nome_user, login, password, nivel_acesso FROM tb_usuarios WHERE status = 1 AND login='"+user+"'","objeto"};
      
        Objeto usuario = (Objeto)query.query(psql).get(0);
        
        query.isOpen(false);

        PasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
        
        if(passwordEncryptor.checkPassword(password, usuario.getsFirst("password"))){
            Main.user = usuario;
            return true;
        }else{
            return false;
        }
  
    }

    public static void isOpen(boolean open){
        query.isOpen(open);
    }

    public static List<Object> query(String sql, String type){
        Object[] psql = {sql,type};
        return query.query(psql);
    }

    public static boolean CED(String sql){
        Object[] psql = {sql};
        return query.CED(psql);
    }

    public static String Count(String sql){
        Object[] psql = {sql};
        return query.Count(psql);
    }

    public static List<Combobox> ListCB(String sql){
        Object[] psql = {sql};
        return query.listCb(psql);
    }

    public static List<String> Search(String sql){
        Object[] psql = {sql};
        return query.search(psql);
    }
}
