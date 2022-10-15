package com.decattech;



public class App {
    public static void main(String[] args) {
		Main.main(args);
    }

   /*  public boolean VerifyVersion(){
        Object[] parametrosweb = {"http://192.168.254.216/version.json",1};
		var l = Functions.getJSONfromweb(parametrosweb);
		if(!l.isEmpty()){
			if((boolean) l.get(0)){
				
				JSONObject jsonweb = (JSONObject)l.get(1);
				var jsongpe = (JSONArray)jsonweb.get("java");
				JSONObject gpe = new JSONObject();
				
				for (int i = 0; i < jsongpe.length(); i++) {
					var variavel = jsongpe.getJSONObject(i);
					if(variavel.get("app").toString().toLowerCase().equals("gpe")){
						gpe = variavel;
					}
				}
				/*var jsonmain = Functions.JsonReader("version");
				//System.out.println(json.get("versao") + " - " + gpe.get("versao"));
				if(gpe.get("versao").toString().equals(jsonmain.get("versao"))){
					Main.version = jsonmain.get("versao").toString();
					return true;
				}else{
					return false;
				}
				
				
			}
		}else{
			System.out.println("Erro ao verificar a versão!");
			JOptionPane.showMessageDialog(null, "Erro ao verificar versão!\r\nVerifique sua conexão com a internet!", "Erro de Verificação", JOptionPane.ERROR_MESSAGE);
			
		}
		return false;
     
    }
	*/
    public void Update(){
        
    }
}
