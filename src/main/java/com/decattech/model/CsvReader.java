package com.decattech.model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.input.BOMInputStream;

import com.functions.models.Objeto;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

public class CsvReader {
    private CSVParser parser = null;
    private CSVReader reader;

    public static void main(String[] args) {
        try {
            // Create an object of file reader class with CSV file as a parameter.
            FileReader filereader = new FileReader("C:\\Users\\kauan.reis\\Downloads\\boletos_1667569017542.csv");
      
            // create csvParser object with
            // custom separator semi-colon
            CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
      
            // create csvReader object with parameter
            // filereader and parser
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                                      .withCSVParser(parser)
                                      .build();
      
            // Read all data at once
            List<String[]> allData = csvReader.readAll();
      
            // Print Data.
            for (String[] row : allData) {
                for (String cell : row) {
                    System.out.print(cell + "\t");
                }
                System.out.println();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSeparator(Object[] parametros){
        
        if(parametros[0].getClass().equals(CSVParser.class)){
            parser = (CSVParser) parametros[0];
        }else if(parametros[0].getClass().equals(Character.class) || parametros[0].getClass().equals(String.class)){
            parser = new CSVParserBuilder()
            .withSeparator((char)parametros[0])
            .withIgnoreQuotations(true)
            .build();
        }else{
            System.out.println("Sem parametros para o valor passado!\r\nFun????o: CsvReader.setSeparator();");
        }
    }

    public List<Object> getListFromCSV(Object[] parametros){
        String type = (String) parametros[0];
        List<Object> itens = new ArrayList<>();
        
        if(type.equals("objetos") || type.equals("relatorios")){
            String file = (String) parametros[1];
           
            if(parser != null){
                try {
                    var inputStreamReader = new InputStreamReader(new BOMInputStream(new FileInputStream(file)), StandardCharsets.UTF_8);
                    reader =  new CSVReaderBuilder(inputStreamReader).withSkipLines(0).withCSVParser(parser).build();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }else{
                try {
                    var inputStreamReader = new InputStreamReader(new BOMInputStream(new FileInputStream(file)), StandardCharsets.UTF_8);
                    reader =  new CSVReaderBuilder(inputStreamReader).withSkipLines(0).build();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            Objeto csv = new Objeto();
           

            try{
                //ler todo o arquivo e transforma em uma lista de array String
                List<String[]> linhas = reader.readAll();

                //Cria um for para passar para o objeto
                for (int i = 1; i < linhas.size(); i++) {
                    for (int j = 0; j < linhas.get(i).length; j++) {
                        
                        //seta os valores no objeto Objeto, a primeira linha sempre ?? o nome da coluna!
                        csv.set(linhas.get(0)[j].trim().replace("?", ""), linhas.get(i)[j]);
                    }
                    
                }
               
                itens.add(csv);
                return itens;
            }catch(IOException | CsvException e){
                e.printStackTrace();
                //Ser der algum error retorna nulo.
                return null;
            }

        }
        
        else if(type.equals("objeto")){
           
            String file = (String) parametros[1];
           
            if(parser != null){
                try {
                    var inputStreamReader = new InputStreamReader(new BOMInputStream(new FileInputStream(file)), StandardCharsets.UTF_8);
                    reader =  new CSVReaderBuilder(inputStreamReader).withCSVParser(parser).build();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }else{
                try {
                    var inputStreamReader = new InputStreamReader(new BOMInputStream(new FileInputStream(file)), StandardCharsets.UTF_8);
                    reader =  new CSVReaderBuilder(inputStreamReader).build();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
           

            try{
                //ler todo o arquivo e transforma em uma lista de array String
                List<String[]> linhas = reader.readAll();
                       

                //Cria um for para passar para o objeto
                for (int i = 1; i < linhas.size(); i++) {
                    Objeto csv = new Objeto();
                    for (int j = 0; j < linhas.get(i).length; j++) {
    
                        //seta os valores no objeto Objeto, a primeira linha sempre ?? o nome da coluna!
                        csv.set(linhas.get(0)[j].trim(), linhas.get(i)[j].replace("'", "").replace("\"", ""));
                        
                    }
                
                    itens.add(csv);
                }
                

                return itens;
            }catch(IOException | CsvException e){
                e.printStackTrace();
                //Ser der algum error retorna nulo.
                return null;
            }
        }else{
            System.out.println("N??o coube em nenhuma das condi????es existentes.\r\nFun????o: CsvReader.getHashFromCSV();");
            return null;
        }
        
    }

}
