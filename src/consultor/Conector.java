/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package consultor;

/**
 *
 * @author ine031
 * agregando comentario para merge
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple Java Program to connect Oracle database by using Oracle JDBC thin driver
 * Make sure you have Oracle JDBC thin driver in your classpath before running this program
 * @author
 */
public class Conector {

       // JDBC driver name and database URL
   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
   static final String DB_URL = "jdbc:mysql://localhost/REPORTE_IPC";

   //  Database credentials
   static final String USER = "root";
   static final String PASS = "Ine$2020";

    private String urlCSVs = "";
    Connection conn;

    private int anioActual, mesActual;
    private int anioAnterior, mesAnterior;
    private boolean esEnero;
    private Double ipcRepMesAnterior, ipcRepAnioAnterior;
    private Double ipcReg1MesAnterior, ipcReg1AnioAnterior;
    private Double ipcReg2MesAnterior, ipcReg2AnioAnterior;
    private Double ipcReg3MesAnterior, ipcReg3AnioAnterior;
    private Double ipcReg4MesAnterior, ipcReg4AnioAnterior;
    private Double ipcReg5MesAnterior, ipcReg5AnioAnterior;
    private Double ipcReg6MesAnterior, ipcReg6AnioAnterior;
    private Double ipcReg7MesAnterior, ipcReg7AnioAnterior;
    private Double ipcReg8MesAnterior, ipcReg8AnioAnterior;
    private File f, texs;
    private Double varMensual = 0.0, varAnual = 0.0, ipcActual = 0.0, varAcumulada = 0.0;
    private Double varMensual1 = 0.0, varAnual1 = 0.0, ipcActual1 = 0.0;
    private Double varMensual2 = 0.0, varAnual2 = 0.0, ipcActual2 = 0.0;
    private Double varMensual3 = 0.0, varAnual3 = 0.0, ipcActual3 = 0.0;
    private Double varMensual4 = 0.0, varAnual4 = 0.0, ipcActual4 = 0.0;
    private Double varMensual5 = 0.0, varAnual5 = 0.0, ipcActual5 = 0.0;
    private Double varMensual6 = 0.0, varAnual6 = 0.0, ipcActual6 = 0.0;
    private Double varMensual7 = 0.0, varAnual7 = 0.0, ipcActual7 = 0.0;
    private Double varMensual8 = 0.0, varAnual8 = 0.0, ipcActual8 = 0.0;
    private int fecha_fecha;
    private String inflacionCA;
    private String rutaEntradas;

    public Double getVariacionAnual(){
        return this.varAnual;
    }

    public Double getVariacionMensual(){
        return this.varMensual;
    }

    public Double getVariacionAcumulada(){
        return this.varAcumulada;
    }

    public Conector(String urlCSV, String ruta, String urlTEX, String anio, String mes) throws SQLException {
        this.rutaEntradas = "/var/www/archivos/CSV/";
        System.out.println("antes del try");
        anioActual = Integer.parseInt(anio);
        mesActual = Integer.parseInt(mes);
        anioAnterior = anioActual - 1;
            mesAnterior = mesActual - 1;
            if(mesAnterior == 0){
                mesAnterior = 12;
                esEnero = true;
            }
        //his.inflacionCA = inflacionCentroAmerica;

        this.urlCSVs = ruta;
        f = new File(urlCSVs);
        texs = new File(urlTEX);

        conn = null;
        Statement stmt = null;
        System.out.println("antes del try");
        try{
           //STEP 2: Register JDBC driver
           Class.forName(JDBC_DRIVER);

           //STEP 3: Open a connection
           System.out.println("Connecting to database...");
           conn = DriverManager.getConnection(DB_URL,USER,PASS);

           //STEP 4: Execute a query
           System.out.println("Creating statement...");
           stmt = conn.createStatement();
           String sql;
           sql = "DELETE FROM AUXILIAR_DATOS";
           boolean rs = stmt.execute(sql);

           //STEP 6: Clean-up environment
           System.out.println("otra");
           sql = "LOAD DATA LOCAL INFILE '"+urlCSV+"' INTO TABLE AUXILIAR_DATOS\n" +
                "FIELDS TERMINATED BY ';' ENCLOSED BY '\"'\n" +
                "LINES TERMINATED BY '\\n'\n" +
                "IGNORE 1 LINES\n" +
                "(año, mes, código, descripción, rep, reg_I, reg_II, reg_III, reg_IV, reg_V, reg_VI, reg_VII, reg_VIII);";
           rs = stmt.execute(sql);

           stmt.close();
           conn.close();
        }catch(SQLException se){
           //Handle errors for JDBC
           se.printStackTrace();
        }catch(Exception e){
           //Handle errors for Class.forName
           e.printStackTrace();
        }finally{
           //finally block used to close resources
           try{
              if(stmt!=null)
                 stmt.close();
           }catch(SQLException se2){
           }// nothing we can do
           try{
              if(conn!=null)
                 conn.close();
           }catch(SQLException se){
              se.printStackTrace();
           }//end finally try
        }//end try
        try {
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
        }catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }

        setAnioMesActual();
        setIPCRepAnteriores();

        //agregarVariablesCondicionales(variablesCondicionales);
        generarCSVs();

        //cerrando la conexion con la base de datos
        conn.close();

    }

    /*private void agregarVariablesCondicionales(String variablesCondicionales){
        System.out.println("esto entra a agregarVariables: " + variablesCondicionales);
        String[] vars = variablesCondicionales.split("&");

            System.out.println("fao: " + fao);
            System.out.println("petroleo: " + petroleo);
            System.out.println("cambio: " + cambio);
            System.out.println("interes: " + interes);
            System.out.println("eeuu: " + eeuu);
            System.out.println("mexico: " + mexico);
            Statement stmt = null;

            //obteniendo las ultimas fechas para cada variable condicional
            int[] fechas = new int[6];
            for(int i=0; i<6; i++){
                String sql = "SELECT fecha"
                    + " FROM VARIABLE_CONDICIONAL"
                    + " WHERE tipo_variable_condicional = " + (i+1)
                    + " ORDER BY fecha DESC";
                        PreparedStatement preStatement;
                    try {
                        preStatement = conn.prepareStatement(sql);
                        ResultSet result = preStatement.executeQuery();
                        if(result.next()){
                            fechas[i] = Integer.parseInt(result.getString("fecha"));
                        }
                        result.close();
                        preStatement.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
                    }
            }


            for(int i=0; i<6; i++){
                try {
                    if(vars[i].equals("null")){
                        String insert1 = "INSERT INTO VARIABLE_CONDICIONAL VALUES(1,"+fechas[i]+","+vars[i]+");";

                    }
                    stmt = conn.createStatement();

                    boolean rs = stmt.execute(sql);
                } catch (SQLException ex) {
                    Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
                }
               try{
                  if(stmt!=null)
                     stmt.close();
               }catch(SQLException se2){
               }// nothing we can do
            }



    }
    */
    private String getNumero(Double n){
        DecimalFormat decim = new DecimalFormat("0.00");
        return (decim.format(n));
    }
    
    private String getNumeroDosDecimales(Double n){
        DecimalFormat decim = new DecimalFormat("0.00");
        return ("\\mbox{" + decim.format(n) + "}");
    }
    
    private String getNumeroUnDecimal(Double n){
        DecimalFormat decim = new DecimalFormat("0.0");
        return ("\\mbox{" + decim.format(n) + "}");
    }

    private String[] csv1_07(String inflacionCA){
        try{
            String[] vars = inflacionCA.split("&");
            String texto = "Guatemala&" + vars[0] + "&" + vars[5]
                + "El Salvador&" + vars[1] + "&" + vars[6]
                + "Honduras&" + vars[2] + "&" + vars[7]
                + "Nicaragua&" + vars[3] + "&" + vars[8]
                + "Costa Rica&" + vars[4] + "&" + vars[9];
            List<Intermensual> lista = new ArrayList<>();

            lista.add(new Intermensual("Guatemala", Double.parseDouble(vars[5])));
            lista.add(new Intermensual("El Salvador", Double.parseDouble(vars[6])));
            lista.add(new Intermensual("Honduras", Double.parseDouble(vars[7])));
            lista.add(new Intermensual("Nicaragua", Double.parseDouble(vars[8])));
            lista.add(new Intermensual("Costa Rica", Double.parseDouble(vars[9])));

            for(int i=0; i<lista.size(); i++){
                for(int j=0; j<lista.size(); j++){
                    if(lista.get(i).getValor() < lista.get(j).getValor()){
                        Intermensual temp = lista.get(i);
                        lista.set(i, lista.get(j));
                        lista.set(j, temp);
                    }
                }
            }

            String descripcion = "Para el mes de " + getMesCadenaMin(mesActual) + " " + anioActual
                    + ", en la región centroamericana, " + lista.get(0).getAlias()
                    + " presentó la mayor tasa de inflación interanual de " + getNumeroDosDecimales(lista.get(0).getValor()) + "\\%"
                    + ", mientras que " + lista.get(0).getAlias()
                    + " registró la tasa más baja con un nivel de " + getNumeroDosDecimales(lista.get(0).getValor()) + "\\%";
            String[] resultado = {texto, descripcion};
            return resultado;
        }catch(Exception e){
            System.out.println("Error en la cadena de entrada de inflación de Centro América");
        }
        String[] error = {"error", "error"};
        return error;
    }

    private int getMesAnterior(int mes){
        if(mes==1) return 12;
        else return mes-1;
    }

    private String[] csv1_x(int varC){
        String texto = "x;y\n";
         String descripcion = "";
            /*String sql = "SELECT f.anio, f.mes, v.valor "
                    + " FROM FECHA f, VARIABLE_CONDICIONAL v"
                    + " WHERE v.fecha = f.fecha"
                    + " AND ((f.anio = " + anioActual
                    + " AND f.mes <= " + mesActual + ")"
                    + " OR (f.anio = " + (anioActual - 1)
                    + " AND f.mes >= " + mesActual + "))"
                    + " AND v.tipo_variable_condicional = " + varC
                    + " ORDER BY f.anio, f.mes;";
            System.out.println("1_0"+varC+": " + sql);
            PreparedStatement preStatement;
            List<FactorExterno> lista = new ArrayList<>();*/
        try {
            /*preStatement = conn.prepareStatement(sql);
            ResultSet result = preStatement.executeQuery();
            while(result.next()){
                texto += getMesAbreviacion(Integer.parseInt(result.getString("mes"))) + "-" + result.getString("anio").substring(2,4) + ";" + result.getString("valor") + "\n";
                lista.add(new FactorExterno((Integer.parseInt(result.getString("anio"))), Integer.parseInt(result.getString("mes")), Double.parseDouble(result.getString("valor"))));
            }
            result.close();
            preStatement.close();*/
            List<Double> lista = new ArrayList();
            String line = "";
            String fileName = "arc";
                switch(varC){
                    case 1:
                        fileName = rutaEntradas + "1_01.csv";
                        break;
                    case 2:
                        fileName = rutaEntradas + "1_02.csv";
                        break;
                    case 3:
                        fileName = rutaEntradas + "1_03.csv";
                        break;
                    case 4:
                        fileName = rutaEntradas + "1_04.csv";
                        break;
                    case 5:
                        fileName = rutaEntradas + "1_06.csv";
                        break;
                    case 6:
                        fileName = rutaEntradas + "1_05.csv";
                        break;
                    case 7:
                        fileName = rutaEntradas + "1_07.csv";
                        break;
                }
                String ultimoMes = "";
            try {

                // FileReader reads text files in the default encoding.
                FileReader fileReader = new FileReader(fileName);

                // Always wrap FileReader in BufferedReader.
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                boolean encabezado = true;
                while((line = bufferedReader.readLine()) != null) {
                    if(encabezado){
                        encabezado = false;
                    } else{
                        String[] datos = line.split(";");
                        if(varC==7){
                            lista.add(new Double(datos[2]));
                        }
                        else{
                            lista.add(new Double(datos[1]));
                        }
                        ultimoMes = datos[0].substring(0, 3);
                    }
                }
                bufferedReader.close();
                }
                catch(FileNotFoundException ex) {
                    System.out.println(
                        "Unable to open file '" +
                        fileName + "'");
                }
                catch(IOException ex) {
                    System.out.println(
                        "Error reading file '"
                        + fileName + "'");
                }
            ultimoMes = getNombreMes(ultimoMes);
            for(int i=0; i<lista.size(); i++){
                System.out.println("VALOR " + i + ": " + lista.get(i));
            }
            int paisMayor = 0, paisMenor = 0;
            Double valMayor = -10000.0, valMenor = 10000.0;
            Double valorAnioAnterior = 0.0;
            Double valorMesAnterior = 0.0;
            Double valorActual = 0.0;
            if(varC==7){
                for(int i=0; i<lista.size(); i++){
                    if(lista.get(i) > valMayor){
                        valMayor = lista.get(i);
                        paisMayor = i;
                    }
                    if(lista.get(i) < valMenor){
                        valMenor = lista.get(i);
                        paisMenor = i;
                    }
                }
            }
            else{
                valorAnioAnterior = lista.get(lista.size()-13);
                valorMesAnterior = lista.get(lista.size()-2);
                valorActual = lista.get(lista.size()-1);
            }
                switch(varC){
                    case 1:
                        System.out.println("El ultimo mes de FAO es: " + ultimoMes);
                        descripcion = "El índice de precios de los alimentos\\footnote{El índice de precios de los "
                                + "alimentos de la FAO es una medida de la variación mensual de los precios "
                                + "internacionales de una canasta de productos alimenticios. Consiste en el "
                                + "promedio de los índices de precios de cinco grupos de productos "
                                + "básicos, ponderado con las cuotas medias de exportación de cada uno de los grupos para 2002-2004. }"
                                + " de la FAO\\footnote{Organización de las Naciones Unidas para la"
                                + " Alimentación y la Agricultura." +
                                "} registró en " + ultimoMes + " " + anioActual
                                + " un índice de " + getNumeroDosDecimales((Math.round((valorActual)*100.0)/100.0)) + ", lo que representa una variación de "
                                + getNumeroDosDecimales((Math.round(((valorActual/valorAnioAnterior - 1) * 100)*100.0)/100.0)) + "\\% respecto a "
                                + getMesCadenaMin(mesAnterior) + " " + anioAnterior + " y de "
                                + getNumeroDosDecimales((Math.round(((valorActual/valorMesAnterior - 1) * 100)*100.0)/100.0)) + "\\% respecto a "
                                + getMesCadenaMin(getMesAnterior(mesAnterior)) + " " + anioActual + ".";
                        break;
                    case 2:
                        descripcion = "El precio internacional del petróleo\\footnote{Se refiere al crudo West "
                                + "Texas Intermediate (WTI) producido en Texas y el sur de Oklahoma} "
                                + " registró en " + getMesCadenaMin(mesActual) + " " + anioActual
                                + " un precio medio de US\\$" + getNumeroDosDecimales((Math.round((valorActual)*100.0)/100.0)) + " por barril, "
                                + "lo que representa una variación de "
                                + getNumeroDosDecimales((Math.round(((valorActual/valorAnioAnterior - 1) * 100)*100.0)/100.0)) + "\\%"
                                + " (" + getFlechita(valorActual, valorAnioAnterior) + " US\\$"
                                + getNumero((Math.round(Math.abs(valorActual-valorAnioAnterior)*100.0)/100.0)) + ") respecto a "
                                + getMesCadenaMin(mesActual) + " " + anioAnterior + " y de "
                                + getNumeroDosDecimales((Math.round(((valorActual/valorMesAnterior - 1) * 100)*100.0)/100.0)) + "\\%"
                                + " (" + getFlechita(valorActual, valorMesAnterior) + " US\\$"
                                + getNumeroDosDecimales((Math.round((valorActual-valorMesAnterior)*100.0)/100.0)) + ") respecto a "
                                + getMesCadenaMin(mesAnterior) + " " + anioActual + ".";
                        break;
                    case 3:
                        descripcion = "El tipo de cambio de referencia\\footnote{El tipo de cambio de referencia "
                                + "lo calcula el Banco de Guatemala con la información que las instituciones que "
                                + "constituyen el Mercado Institucional de Divisas le proporcionan, relativa al "
                                + "monto de divisas compradas y al "
                                + "monto de divisas vendidas y sus respectivas equivalencias en moneda nacional.}"
                                + " del quetzal respecto al dólar de los Estados Unidos de América, "
                                + "registró en " + getMesCadenaMin(mesActual) + " " + anioActual
                                + " un precio medio de Q" + getNumeroDosDecimales((Math.round((valorActual)*100.0)/100.0)) + " por US\\$1.00, "
                                + "lo que representa una variación de "
                                + getNumeroDosDecimales((Math.round(((valorActual/valorAnioAnterior - 1) * 100)*100.0)/100.0)) + "\\% respecto a "
                                + getMesCadenaMin(mesActual) + " " + anioAnterior + " y de "
                                + getNumeroDosDecimales((Math.round(((valorActual/valorMesAnterior - 1) * 100)*100.0)/100.0)) + "\\% respecto a "
                                + getMesCadenaMin(mesAnterior) + " " + anioActual + ".";
                        break;
                    case 4:
                        descripcion = "El promedio ponderado preliminar de la tasa de interés activa\\footnote{Es el "
                                + "porcentaje que las instituciones bancarias, de acuerdo con las condiciones de "
                                + "mercado y las disposiciones del "
                                + "banco central, cobran por los diferentes tipos de servicios de crédito a"
                                + " los usuarios de los mismos.}"
                                + " en moneda nacional se "
                                + "ubicó en " + getMesCadenaMin(mesActual) + " " + anioActual
                                + " en " + getNumeroDosDecimales((Math.round((valorActual)*100.0)/100.0)) + "\\%, "
                                + "" + getAumento(valorActual, valorAnioAnterior, valorMesAnterior)

                                + " y de "
                                + getNumeroDosDecimales((Math.round(((valorActual/valorMesAnterior - 1) * 100)*100.0)/100.0)) + "\\% "
                                + "respecto a "
                                + getMesCadenaMin(mesAnterior) + " " + anioActual + ".";
                        break;
                    case 5:
                        descripcion = "El Índice de Precios al Consumidor en los Estados Unidos de "
                                + "América\\footnote{Para mayor información sobre el indice de precios al consumidor de los "
                                + "Estados Unidos, visite \\url{http://www.bls.gov/cpi}.} registró una variación "
                                + "interanual al mes de " + getMesCadenaMin(mesAnterior) + " " + anioActual
                                + " de " + getNumeroDosDecimales((Math.round((valorActual)*100.0)/100.0)) + "\\%. \\\\ \\\\ "
                                + "En " + getMesCadenaMin(mesAnterior) + " " + anioAnterior + " la variación "
                                + "interanual se ubicó en " + getPuntosEEUU(valorActual, valorAnioAnterior);
                        /*
                         y una variación mensual de "
                                + getNumeroDosDecimales((Math.round(((valorActual/valorMesAnterior - 1) * 100)*100.0)/100.0)) + " respecto a "
                                + getMesCadenaMin(getMesAnterior(mesAnterior)) + " " + anioActual + "
                        */
                        break;
                    case 6:
                        descripcion = "El Índice de Precios al Consumidor en México\\footnote{Para "
                                + "mayor información sobre el índice de precios al consumidor "
                                + "en México, visite \\url{http://www.inegi.org.mx}.} registró una "
                                + "variación interanual al mes de " + getMesCadenaMin(mesAnterior) + " " + anioActual
                                + " de " + getNumeroDosDecimales((Math.round((valorActual)*100.0)/100.0)) + "\\%. \\\\ \\\\"
                                + "En " + getMesCadenaMin(mesAnterior) + " " + anioAnterior + " la variación "
                                + "interanual se ubicó en " + getPuntosEEUU(valorActual, valorAnioAnterior);
                        
                        break;
                    case 7:
                        descripcion = "Para el mes de " + getMesCadenaMin(mesAnterior) + " " + anioActual
                            + ", en la región centroamericana, " + getPais(paisMayor)
                            + " presentó la mayor tasa de inflación interanual de " + valMayor + "\\%"
                            + ", mientras que " + getPais(paisMenor)
                            + " registró la tasa más baja con un nivel de " + valMenor + "\\%";
                        break;
                    default:
                        break;
                }

        } catch (Exception ex) {//esta era SQLException
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }
        String[] resultado = {texto, descripcion};
        return resultado;
    }
    
    private String getFlechita(Double actual, Double anterior){
        if(actual > anterior) return "";
        else return "-";
    }

    private String getNombreMes(String abreviatura){
        if(abreviatura.toLowerCase().equals("ene")) return "enero";
        else if(abreviatura.toLowerCase().equals("feb")) return "febrero";
        else if(abreviatura.toLowerCase().equals("mar")) return "marzo";
        else if(abreviatura.toLowerCase().equals("abr")) return "abril";
        else if(abreviatura.toLowerCase().equals("may")) return "mayo";
        else if(abreviatura.toLowerCase().equals("jun")) return "junio";
        else if(abreviatura.toLowerCase().equals("jul")) return "julio";
        else if(abreviatura.toLowerCase().equals("ago")) return "agosto";
        else if(abreviatura.toLowerCase().equals("sep")) return "septiembre";
        else if(abreviatura.toLowerCase().equals("oct")) return "octubre";
        else if(abreviatura.toLowerCase().equals("nov")) return "noviembre";
        else if(abreviatura.toLowerCase().equals("dic")) return "diciembre";
        else return getMesCadenaMin(mesActual);
    }
    
    private String getPais(int n){
        switch(n){
            case 0: return "Guatemala";
            case 1: return "El Salvador";
            case 2: return "Honduras";
            case 3: return "Nicaragua";
            case 4: return "Costa Rica";
            default: return "País";
        }
    }

    private String getDelta(int val1, int val2){
        int res = val1 - val2;
        if(res >= 0){
            return "incremento de " + res;
        }
        else{
            return "decremento de " + (res*-1);
        }
    }

     private void generarCSVs() throws SQLException{
        String region = "rep";
        File sub = new File(f, "");
        File tablas = new File(f, "tablas");

        for(int varC=1; varC<=7; varC++){
            String[] resultado = csv1_x(varC);
            escribirCSV(new File(sub, "1_0"+varC).getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File("1_0"+varC, "descripcion").getPath()).getAbsolutePath(), resultado[1]);
        }

        //String[] resultado = csv1_07(inflacionCA);
        //escribirTabla(new File(tablas, "1_07").getAbsolutePath(), resultado[0]);
        //escribirTEX(new File(texs, new File("1_07", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        String[] resultado = csv2_01(region);
        escribirTabla(new File(tablas, "2_01").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_01", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv2_02(region);
        escribirCSV(new File(sub, "2_02").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_02", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv2_03(region);
        escribirCSV(new File(sub, "2_03").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_03", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv1_M1(region);
        escribirCSV(new File(sub, "2_04").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_04", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv2_05();
        escribirTabla(new File(tablas, "2_05").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_05", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv_interMensual(region, 0);
        escribirCSV(new File(sub, "2_06").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_06", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv2_07(region);
        escribirCSV(new File(sub, "2_07").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_07", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv2_08(region);
        escribirCSV(new File(sub, "2_08").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_08", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv2_09(region);
        escribirCSV(new File(sub, "2_09").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_09", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv_interMensual(region, 1);
        escribirCSV(new File(sub, "2_10").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_10", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv_interMensual(region, 2);
        escribirCSV(new File(sub, "2_11").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_11", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado =  csv_interMensual(region, 3);
        escribirCSV(new File(sub, "2_12").getAbsolutePath(),resultado[0]);
        escribirTEX(new File(texs, new File("2_12", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv_interMensual(region, 4);
        escribirCSV(new File(sub, "2_13").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_13", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv_interMensual(region, 5);
        escribirCSV(new File(sub, "2_14").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_14", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv_interMensual(region, 6);
        escribirCSV(new File(sub, "2_15").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_15", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv_interMensual(region, 7);
        escribirCSV(new File(sub, "2_16").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_16", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv_interMensual(region, 8);
        escribirCSV(new File(sub, "2_17").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_17", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv_interMensual(region, 9);
        escribirCSV(new File(sub, "2_18").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_18", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv_interMensual(region, 10);
        escribirCSV(new File(sub, "2_19").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_19", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv_interMensual(region, 11);
        escribirCSV(new File(sub, "2_20").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_20", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv_interMensual(region, 12);
        escribirCSV(new File(sub, "2_21").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_21", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv1_M2();
        escribirCSV(new File(sub, "2_22").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_22", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv2_23(region);
        escribirCSV(new File(sub, "2_23").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_23", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv2_24(region);
        escribirCSV(new File(sub, "2_24").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_24", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv1_M3("rep");
        escribirCSV(new File(sub, "2_25").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_25", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv2_26();
        escribirTabla(new File(tablas, "2_26").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_26", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv_interAnual(region, 0);
        escribirCSV(new File(sub, "2_27").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_27", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv2_28(region);
        escribirCSV(new File(sub, "2_28").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_28", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv2_29(region);
        escribirCSV(new File(sub, "2_29").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_29", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv2_30(region);
        escribirCSV(new File(sub, "2_30").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_30", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv_interAnual(region, 1);
        escribirCSV(new File(sub, "2_31").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_31", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv_interAnual(region, 2);
        escribirCSV(new File(sub, "2_32").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_32", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv_interAnual(region, 3);
        escribirCSV(new File(sub, "2_33").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_33", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv_interAnual(region, 4);
        escribirCSV(new File(sub, "2_34").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_34", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv_interAnual(region, 5);
        escribirCSV(new File(sub, "2_35").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_35", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv_interAnual(region, 6);
        escribirCSV(new File(sub, "2_36").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_36", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv_interAnual(region, 7);
        escribirCSV(new File(sub, "2_37").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_37", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv_interAnual(region, 8);
        escribirCSV(new File(sub, "2_38").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_38", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv_interAnual(region, 9);
        escribirCSV(new File(sub, "2_39").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_39", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv_interAnual(region, 10);
        escribirCSV(new File(sub, "2_40").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_40", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv_interAnual(region, 11);
        escribirCSV(new File(sub, "2_41").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_41", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv_interAnual(region, 12);
        escribirCSV(new File(sub, "2_42").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_42", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv1_M4();
        escribirCSV(new File(sub, "2_43").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_43", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv2_44(region);
        escribirCSV(new File(sub, "2_44").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_44", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv2_45(region);
        escribirCSV(new File(sub, "2_45").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_45", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv1_M5(region);
        escribirCSV(new File(sub, "2_46").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_46", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        resultado = csv2_47(region);
        escribirCSV(new File(sub, "2_47").getAbsolutePath(), resultado[0]);
        escribirTEX(new File(texs, new File("2_47", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        for(int i=1; i<=8; i++){
            region = "reg" + i;
            sub = new File(f, "");

            resultado = csv2_01(region);
            escribirTabla(new File(tablas, (i+2) + "_01").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_01", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv2_02(region);
            escribirCSV(new File(sub, (i+2) +"_02").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_02", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv2_03(region);
            escribirCSV(new File(sub, (i+2) +"_03").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_03", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csvr_04(region);
            escribirTabla(new File(tablas, (i+2) +"_04").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_04", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv_interMensual(region, 0);
            escribirCSV(new File(sub, (i+2) +"_05").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_05", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv2_07(region);
            escribirCSV(new File(sub, (i+2) +"_06").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_06", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv2_08(region);
            escribirCSV(new File(sub, (i+2) +"_07").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_07", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv2_09(region);
            escribirCSV(new File(sub, (i+2) +"_08").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_08", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv_interMensual(region, 1);
            escribirCSV(new File(sub, (i+2) +"_09").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_09", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv_interMensual(region, 2);
            escribirCSV(new File(sub, (i+2) +"_10").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_10", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv_interMensual(region, 3);
            escribirCSV(new File(sub, (i+2) +"_11").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_11", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv_interMensual(region, 4);
            escribirCSV(new File(sub, (i+2) +"_12").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_12", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv_interMensual(region, 5);
            escribirCSV(new File(sub, (i+2) +"_13").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_13", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv_interMensual(region, 6);
            escribirCSV(new File(sub, (i+2) +"_14").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_14", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv_interMensual(region, 7);
            escribirCSV(new File(sub, (i+2) +"_15").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_15", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv_interMensual(region, 8);
            escribirCSV(new File(sub, (i+2) +"_16").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_16", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv_interMensual(region, 9);
            escribirCSV(new File(sub, (i+2) +"_17").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_17", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv_interMensual(region, 10);
            escribirCSV(new File(sub, (i+2) +"_18").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_18", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv_interMensual(region, 11);
            escribirCSV(new File(sub, (i+2) +"_19").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_19", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv_interMensual(region, 12);
            escribirCSV(new File(sub, (i+2) +"_20").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_20", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv2_23(region);
            escribirCSV(new File(sub, (i+2) +"_21").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_21", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv2_24(region);
            escribirCSV(new File(sub, (i+2) +"_22").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_22", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csvr_23(region);
            escribirTabla(new File(tablas, (i+2) +"_23").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_23", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv_interAnual(region, 0);
            escribirCSV(new File(sub, (i+2) +"_24").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_24", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv2_28(region);
            escribirCSV(new File(sub, (i+2) +"_25").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_25", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv2_29(region);
            escribirCSV(new File(sub, (i+2) +"_26").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_26", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv2_30(region);
            escribirCSV(new File(sub, (i+2) +"_27").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_27", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv_interAnual(region, 1);
            escribirCSV(new File(sub, (i+2) +"_28").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_28", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv_interAnual(region, 2);
            escribirCSV(new File(sub, (i+2) +"_29").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_29", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv_interAnual(region, 3);
            escribirCSV(new File(sub, (i+2) +"_30").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_30", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv_interAnual(region, 4);
            escribirCSV(new File(sub, (i+2) +"_31").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_31", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv_interAnual(region, 5);
            escribirCSV(new File(sub, (i+2) +"_32").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_32", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv_interAnual(region, 6);
            escribirCSV(new File(sub, (i+2) +"_33").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_33", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv_interAnual(region, 7);
            escribirCSV(new File(sub, (i+2) +"_34").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_34", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv_interAnual(region, 8);
            escribirCSV(new File(sub, (i+2) +"_35").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_35", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv_interAnual(region, 9);
            escribirCSV(new File(sub, (i+2) +"_36").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_36", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv_interAnual(region, 10);
            escribirCSV(new File(sub, (i+2) +"_37").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_37", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv_interAnual(region, 11);
            escribirCSV(new File(sub, (i+2) +"_38").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_38", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv_interAnual(region, 12);
            escribirCSV(new File(sub, (i+2) +"_39").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_39", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv2_44(region);
            escribirCSV(new File(sub, (i+2) +"_40").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_40", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv2_45(region);
            escribirCSV(new File(sub, (i+2) +"_41").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_41", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

            resultado = csv2_47(region);
            escribirCSV(new File(sub, (i+2) +"_42").getAbsolutePath(), resultado[0]);
            escribirTEX(new File(texs, new File((i+2)+"_42", "descripcion").getPath()).getAbsolutePath(), resultado[1]);

        }

    }

     private void setIPCRepAnteriores(){
            Connection conn;
        try {
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            String sql = "SELECT i.rep, i.reg1, i.reg2, i.reg3, i.reg4, i.reg5, i.reg6, i.reg7, i.reg8"
                    + " FROM FECHA f, IPC i"
                    + " WHERE i.fecha = f.fecha"
                    + " AND i.codigo = 0";
            if(esEnero){
                sql += " AND (f.anio = " + anioAnterior
                    + " AND f.mes = " + mesAnterior + ")";
            }
            else{
                sql += " AND (f.anio = " + anioActual
                    + " AND f.mes = " + mesAnterior + ")";
            }
            PreparedStatement preStatement = conn.prepareStatement(sql);
            ResultSet result = preStatement.executeQuery();
            if(result.next()){
                ipcRepMesAnterior = Double.parseDouble(result.getString("rep"));
                ipcReg1MesAnterior = Double.parseDouble(result.getString("reg1"));
                ipcReg2MesAnterior = Double.parseDouble(result.getString("reg2"));
                ipcReg3MesAnterior = Double.parseDouble(result.getString("reg3"));
                ipcReg4MesAnterior = Double.parseDouble(result.getString("reg4"));
                ipcReg5MesAnterior = Double.parseDouble(result.getString("reg5"));
                ipcReg6MesAnterior = Double.parseDouble(result.getString("reg6"));
                ipcReg7MesAnterior = Double.parseDouble(result.getString("reg7"));
                ipcReg8MesAnterior = Double.parseDouble(result.getString("reg8"));
            }
            result.close();
            preStatement.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }

        //rep año anterior
        try {
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            String sql = "SELECT i.rep, i.reg1, i.reg2, i.reg3, i.reg4, i.reg5, i.reg6, i.reg7, i.reg8"
                    + " FROM FECHA f, IPC i"
                    + " WHERE i.fecha = f.fecha"
                    + " AND i.codigo = 0"
                    + " AND (f.anio = " + (anioActual - 1)
                    + " AND f.mes = " + mesActual + ")";

            PreparedStatement preStatement = conn.prepareStatement(sql);
            ResultSet result = preStatement.executeQuery();
            if(result.next()){
                ipcRepAnioAnterior = Double.parseDouble(result.getString("rep"));
                ipcReg1AnioAnterior = Double.parseDouble(result.getString("reg1"));
                ipcReg2AnioAnterior = Double.parseDouble(result.getString("reg2"));
                ipcReg3AnioAnterior = Double.parseDouble(result.getString("reg3"));
                ipcReg4AnioAnterior = Double.parseDouble(result.getString("reg4"));
                ipcReg5AnioAnterior = Double.parseDouble(result.getString("reg5"));
                ipcReg6AnioAnterior = Double.parseDouble(result.getString("reg6"));
                ipcReg7AnioAnterior = Double.parseDouble(result.getString("reg7"));
                ipcReg8AnioAnterior = Double.parseDouble(result.getString("reg8"));
            }
            result.close();
            preStatement.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }
     }

     private String getPuntosEEUU(Double actual, Double anterior){
         String res = "";
             res += getNumeroDosDecimales(anterior) + "\\%, por lo que este indicador " + acelero(actual,anterior)
                     ;
         return res;
     }
     
     private String acelero(Double a, Double b){
         if(Math.abs(a-b) < 0.01) return " no tuvo una variación significativa en el último año.";
         else{
             if(a>b) return "se aceleró " + getNumeroDosDecimales(a-b) + " puntos porcentuales en el último año.";
            else return "se desaceleró " + getNumeroDosDecimales(b-a) + " puntos porcentuales en el último año.";
         }
     }
     
     private String getAumento(Double actual, Double valAnioAnterior, Double valMesAnterior){
         String res = "";
         if(actual>valAnioAnterior) res += " lo que representa un aumento de "
                 + getNumeroDosDecimales(Math.round(((actual-valAnioAnterior)*100.0))/100.0)
                 + " puntos porcentuales respecto a "
                 + getMesCadenaMin(mesActual) + " " + anioAnterior ;
         else if(anioAnterior>actual) res += " lo que representa una disminución de "
                 + getNumeroDosDecimales(Math.round(((valAnioAnterior - actual)*100.0))/100.0)
                 + " puntos porcentuales respecto a "
                 + getMesCadenaMin(mesActual) + " " + anioAnterior ;
         else return " porcentaje que iguala a la del año anterior";
         /*if(actual>valMesAnterior) res+= " y un aumento de "
                                    + getNumeroDosDecimales(Math.round(((actual-valMesAnterior)*100.0))/100.0)
                                           + " puntos porcentuales respecto a "
                                           + getMesCadenaMin(mesAnterior) + " " + anioAnterior + ".";
         else if(anioAnterior>actual) res+= " y una disminución de "
                                    + getNumeroDosDecimales(Math.round(((valMesAnterior - actual)*100.0))/100.0)
                                           + " puntos porcentuales respecto a "
                                           + getMesCadenaMin(mesAnterior) + " " + anioAnterior + ".";
         else return " y el mes anterior.";
         */return res;
     }

     private void setAnioMesActual() {
            String sql1 = "insert into FECHA(anio,mes_letras,mes)SELECT distinct año, mes,\"-1\"\n" +
"FROM AUXILIAR_DATOS;";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql1);
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }


            String sql2 = "insert into IPC(fecha,codigo,rep,reg1,reg2,reg3,reg4,reg5,reg6,reg7,reg8) SELECT F.fecha,AD.código,AD.rep,AD.reg_I,AD.reg_II,AD.reg_III,AD.reg_IV,AD.reg_V,AD.reg_VI,AD.reg_VII,AD.reg_VIII \n" +
"FROM FECHA F, AUXILIAR_DATOS AD WHERE F.mes_letras = AD.mes AND F.anio = AD.año;";

        try {
            Statement preStatement2= conn.createStatement();
            preStatement2.execute(sql2);
            preStatement2.close();
        } catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }

        //set fecha actual en fecha_fecha
        String sql = "SELECT fecha"
                    + " FROM FECHA"
                    + " WHERE anio = " + anioActual
                    + " AND mes = " + mesActual
                    ;
        System.out.println("DA CLAVO: " + sql);
            PreparedStatement preStatement;
        try {
            preStatement = conn.prepareStatement(sql);
            ResultSet result = preStatement.executeQuery();
            if(result.next()){
                fecha_fecha = Integer.parseInt(result.getString("fecha"));
            }
            result.close();
            preStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }




     }

     private String getMesAbreviacion(int mes){
         if(mes == 1)return "Ene";
         else if(mes == 2)return "Feb";
         else if(mes == 3)return "Mar";
         else if(mes == 4)return "Abr";
         else if(mes == 5)return "May";
         else if(mes == 6)return "Jun";
         else if(mes == 7)return "Jul";
         else if(mes == 8)return "Ago";
         else if(mes == 9)return "Sep";
         else if(mes == 10)return "Oct";
         else if(mes == 11)return "Nov";
         else if(mes == 12)return "Dic";
         return "";
     }

     private void escribirCSV(String nombre, String texto){
         String fileName = nombre + ".csv";
         Path textFile = Paths.get(fileName);
         List<String> lines = new ArrayList<>();
         lines.add(texto);
        try {
            Files.write(textFile, lines, StandardCharsets.ISO_8859_1);
        } catch (IOException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }
     }

     private void escribirTabla(String nombre, String texto){
         String fileName = nombre + ".csv";
         Path textFile = Paths.get(fileName);
         List<String> lines = new ArrayList<>();
         lines.add(texto);
        try {
            Files.write(textFile, lines, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }
     }

     private void escribirTEX(String nombre, String texto){
         File file = new File(nombre);
         file.getParentFile().setReadable(true, false);
        file.getParentFile().setExecutable(true, false);
        file.getParentFile().setWritable(true, false);
        file.getParentFile().mkdirs();
        String fileName = nombre + ".tex";
         Path textFile = Paths.get(fileName);
         List<String> lines = new ArrayList<>();
         lines.add(texto);
        try {
            Files.write(textFile, lines, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }
     }

     private String[] csv2_02(String region){
         String texto = "x;y\n";
         String descripcion = "";
            String sql = "SELECT f.anio, f.mes, i." + region
                    + " FROM FECHA f, IPC i"
                    + " WHERE i.fecha = f.fecha"
                    + " AND ((f.anio = " + anioActual
                    + " AND f.mes <= " + mesActual + ")"
                    + " OR (f.anio = " + (anioActual - 1)
                    + " AND f.mes >= " + mesActual + "))"
                    + " AND i.codigo = 0"
                    + " ORDER BY f.anio, f.mes";
            System.out.println("2_02: " + sql);
            PreparedStatement preStatement;
        try {
            preStatement = conn.prepareStatement(sql);
            ResultSet result = preStatement.executeQuery();
            while(result.next()){
                texto += getMesAbreviacion(Integer.parseInt(result.getString("mes"))) + "-" + result.getString("anio").substring(2,4) + ";" + getNumero(Double.parseDouble(result.getString(region))) + "\n";
            }
            result.close();
            preStatement.close();
            descripcion = "El Índice de Precios al Consumidor\\footnote{"
                    + "Por construcción, el Índice de Precios al Consumidor es 100 en"
                    + " diciembre de 2010.} a " + getMesCadenaMin(mesActual) + " " + anioActual
                    + " se ubicó en " + getIPCActual(region) + ", " 
                    + mayor(getIPCActual(region), getIpcAnioAnterior(region)) +" a lo observado en el mismo mes del año anterior"
                    + "(" + getIpcAnioAnterior(region) +").";
        } catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }
        String[] resultado = {texto, descripcion};
        return resultado;
     }
     
     private String mayor(Double a, Double b){
         if(a>b) return "mayor";
         else if(b>a) return "menor";
         else return "igual";
     }

     private Double getIPCActual(String region){
        if(region.equals("rep")) return ipcActual;
        else if(region.equals("reg1")) return ipcActual1;
        else if(region.equals("reg2")) return ipcActual2;
        else if(region.equals("reg3")) return ipcActual3;
        else if(region.equals("reg4")) return ipcActual4;
        else if(region.equals("reg5")) return ipcActual5;
        else if(region.equals("reg6")) return ipcActual6;
        else if(region.equals("reg7")) return ipcActual7;
        else if(region.equals("reg8")) return ipcActual8;
        return 0.0;
     }

     private Double getVarAnual(String region){
        if(region.equals("rep")) return varAnual;
        else if(region.equals("reg1")) return varAnual1;
        else if(region.equals("reg2")) return varAnual2;
        else if(region.equals("reg3")) return varAnual3;
        else if(region.equals("reg4")) return varAnual4;
        else if(region.equals("reg5")) return varAnual5;
        else if(region.equals("reg6")) return varAnual6;
        else if(region.equals("reg7")) return varAnual7;
        else if(region.equals("reg8")) return varAnual8;
        return 0.0;
     }

     private String[] csv2_03(String region){
        String texto = "x;y\n";
            String sql = "SELECT i." + region + ", p.alias"
                    + " FROM FECHA f, IPC i, PONDERACION p"
                    + " WHERE i.fecha = f.fecha"
                    + " AND f.anio = " + anioActual
                    + " AND f.mes = " + mesActual
                    + " AND i.codigo > 0"
                    + " AND i.codigo < 13"
                    + " AND i.codigo = p.codigo"
                    + " ORDER BY i.codigo";
            System.out.println("2_03: " + sql);
            PreparedStatement preStatement;
            List<Intermensual> listaIPCs = new ArrayList<Intermensual>() {};
        try {
            preStatement = conn.prepareStatement(sql);
            ResultSet result = preStatement.executeQuery();
            while(result.next()){
                texto += result.getString("alias") + ";" + getNumero(Double.parseDouble(result.getString(region))) + "\n";
                listaIPCs.add(new Intermensual(result.getString("alias"), (result.getDouble(region))));
            }
            result.close();
            preStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }
        for(int i=0; i<listaIPCs.size(); i++){
            for(int j=0; j<listaIPCs.size(); j++){
                if(listaIPCs.get(i).getValor() > listaIPCs.get(j).getValor()){
                    Intermensual temp = listaIPCs.get(i);
                    listaIPCs.set(i, listaIPCs.get(j));
                    listaIPCs.set(j, temp);
                }
            }
        }
        String descripcion = "La división de gasto con mayor IPC en la " + getRegionCadena(region) + " a " + getMesCadenaMin(mesActual)
                + " de " + anioActual + " fue la de " + listaIPCs.get(0).getAlias().toLowerCase()
                + " con un índice de " + getNumeroUnDecimal(listaIPCs.get(0).getValor()) + ". La división de gasto que presentó el menor IPC fue la de "
                + listaIPCs.get(listaIPCs.size()-1).getAlias().toLowerCase() + " con un índice de "
                + getNumeroUnDecimal(listaIPCs.get(listaIPCs.size()-1).getValor()) + ".";
        String[] resultado = {texto, descripcion};
        return resultado;
     }

     private String getRegionCadena(String region){
         switch (region) {
            case "rep":
                return "república";
            case "reg1":
                return "región I";
            case "reg2":
                return "región II";
            case "reg3":
                return "región III";
            case "reg4":
                return "región IV";
            case "reg5":
                return "región V";
            case "reg6":
                return "región VI";
            case "reg7":
                return "región VII";
            case "reg8":
                return "región VIII";
        }
        return "";
     }

     private String[] csv_interMensual(String region, int codigo) throws SQLException{
        String texto = "x;y\n";
        String sql = "SELECT f.anio, f.mes, i." + region
                    + " FROM FECHA f, IPC i"
                    + " WHERE i.fecha = f.fecha"
                    + " AND i.codigo = " + codigo;
            if(esEnero){
                sql += " AND ((f.anio = " + anioActual
                    + " AND f.mes = " + mesActual + ")"
                    + " OR (f.anio = " + (anioActual - 1)
                    + " AND f.mes >= " + mesActual + ")"
                    + " OR (f.anio = " + (anioActual - 2)
                    + " AND f.mes = 12))"
                    + " ORDER BY f.anio, f.mes";
            }
            else{
                sql += " AND ((f.anio = " + anioActual
                    + " AND f.mes <= " + mesActual + ")"
                    + " OR (f.anio = " + (anioActual - 1)
                    + " AND f.mes >= " + (mesActual - 1) + "))"
                    + " ORDER BY f.anio, f.mes";
            }
            System.out.println("intermensual " + codigo + " : " + sql);
            PreparedStatement preStatement;
            preStatement = conn.prepareStatement(sql);
            ResultSet result = preStatement.executeQuery();
            Double ipcAnterior = 0.0;
            Double ipc = 0.0;
            Double var = 0.0, varAnterior = 0.0, varAnioAnterior = 0.0;
            Boolean varAnioAnteriorFlag = true;
            if(result.next()){
            	ipcAnterior = Double.parseDouble(result.getString(region));
            }
            while(result.next()){
            	ipc = Double.parseDouble(result.getString(region));
                texto += getMesAbreviacion(Integer.parseInt(result.getString("mes"))) + "-" + result.getString("anio").substring(2,4)
                + ";" + getNumero(Math.round(((ipc/ipcAnterior - 1) * 100)*100.0)/100.0) + "\n";
                varAnterior = var;
                var = (Math.round(((ipc/ipcAnterior - 1) * 100)*100.0)/100.0);
                if(varAnioAnteriorFlag){
                    varAnioAnterior = var;
                    varAnioAnteriorFlag = false;
                }
                ipcAnterior = ipc;
            }
            String descripcion = "La variación mensual del IPC " + getDivisionVarMensual(codigo) + " en " + getMesCadenaMin(mesActual) + " " + anioActual
                    + ", se ubicó en " + getNumeroDosDecimales(var) + "\\%. Esta variación representa una " + getDeltaVarMensual(var, varAnterior)
                    + " puntos porcentuales"
                    + " respecto al mes anterior (" + getNumeroDosDecimales(varAnterior) + "\\%), y con respecto a la variación alcanzada en " + getMesCadenaMin(mesActual)
                    + " " + anioAnterior + " (" + varAnioAnterior + "\\%) "+ getDeltaVarAnual(var, varAnioAnterior) + " puntos.";
            result.close();
            preStatement.close();
            String[] resultado = {texto, descripcion};
        return resultado;
    }

    private String getDivisionVarMensual(int codigo){
        if(codigo == 0) return "a nivel nacional";
        for(int i=1; i<=12; i++){
            if(codigo == i) return "en la división de " + getDivision(i);
        }
        return "";
    }

    private String getDeltaVarMensual(Double var, Double varAnterior){
        Double delta = (Math.round((var - varAnterior)*100.0)/100.0);
        if(delta >= 0){
            return "aceleración en el nivel de precios de " + getNumeroDosDecimales(delta);
        }
        else{
            return "desaceleración en el nivel de precios de " + getNumeroDosDecimales((delta * -1));
        }
    }

    private String getDeltaVarAnual(Double var, Double varAnterior){
        Double delta = (Math.round((var - varAnterior)*100.0)/100.0);
        if(delta >= 0){
            return "subió " + getNumeroDosDecimales(delta);
        }
        else{
            return "bajó " + getNumeroDosDecimales((delta * -1));
        }
    }

    private String[] csv2_07(String region){
        String texto = "x;y\n";
            String sql = "SELECT i." + region + ", p.alias, f.anio, f.mes"
                    + " FROM FECHA f, IPC i, PONDERACION p"
                    + " WHERE i.fecha = f.fecha"
                    + " AND i.codigo > 0"
                    + " AND i.codigo < 13"
                    + " AND i.codigo = p.codigo";
            if(esEnero){
                sql += " AND ((f.anio = " + anioActual
                    + " AND f.mes = " + mesActual + ")"
                    + " OR (f.anio = " + anioAnterior
                    + " AND f.mes = " + mesAnterior + "))"
                    + " ORDER BY i.codigo, f.anio, f.mes";
            }
            else{
                sql += " AND ((f.anio = " + anioActual
                    + " AND f.mes = " + mesActual + ")"
                    + " OR (f.anio = " + anioActual
                    + " AND f.mes = " + mesAnterior + "))"
                    + " ORDER BY i.codigo, f.anio, f.mes";
            }
            System.out.println("2_07: " + sql);
            PreparedStatement preStatement;
            List<Intermensual> lista = new ArrayList<>();
        try {
            preStatement = conn.prepareStatement(sql);
            ResultSet result = preStatement.executeQuery();
            Double ipc, ipcAnterior;
                while(result.next()){
            	ipcAnterior = Double.parseDouble(result.getString(region));
                if(result.next()){
            		ipc = Double.parseDouble(result.getString(region));
	                texto += result.getString("alias") + ";" + getNumero(Math.round(((ipc/ipcAnterior - 1) * 100)*100.0)/100.0) + "\n";
                        lista.add(new Intermensual(result.getString("alias"), (Math.round(((ipc/ipcAnterior - 1) * 100)*100.0)/100.0)));
            	}
            }
            result.close();
            preStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }
            for(int i=0; i<lista.size(); i++){
                for(int j=0; j<lista.size(); j++){
                    if(lista.get(i).getValor() > lista.get(j).getValor()){
                        Intermensual temp = lista.get(i);
                        lista.set(i, lista.get(j));
                        lista.set(j, temp);
                    }
                }
            }
        String descripcion = "De las doce divisiones de gasto que integran el IPC, la de " + lista.get(0).getAlias().toLowerCase()
                + " (" + getNumeroDosDecimales(lista.get(0).getValor()) + "\\%) y " + lista.get(1).getAlias().toLowerCase()
                + " (" + getNumeroDosDecimales(lista.get(1).getValor()) + "\\%), registraron la mayor variación mensual en "
                + getMesCadenaMin(mesActual) + " " + anioActual + ".\n\nPor su parte, "
                + lista.get(lista.size() - 1).getAlias().toLowerCase() + " es la división de gasto con"
                + " menor variación mensual (" + getNumeroDosDecimales(lista.get(lista.size() - 1).getValor()) + "\\%).";
        String[] resultado = {texto, descripcion};
        return resultado;
    }

    private Double getVarMensual(String region){
        if(region.equals("rep")) return varMensual;
        else if(region.equals("reg1")) return varMensual1;
        else if(region.equals("reg2")) return varMensual2;
        else if(region.equals("reg3")) return varMensual3;
        else if(region.equals("reg4")) return varMensual4;
        else if(region.equals("reg5")) return varMensual5;
        else if(region.equals("reg6")) return varMensual6;
        else if(region.equals("reg7")) return varMensual7;
        else if(region.equals("reg8")) return varMensual8;
        return 0.0;
    }

    private void setVarMensual(String region, Double variacion){
        if(region.equals("rep")) varMensual = (Math.round(variacion*100.0)/100.0);
        else if(region.equals("reg1")) varMensual1 = (Math.round(variacion*100.0)/100.0);
        else if(region.equals("reg2")) varMensual2 = (Math.round(variacion*100.0)/100.0);
        else if(region.equals("reg3")) varMensual3 = (Math.round(variacion*100.0)/100.0);
        else if(region.equals("reg4")) varMensual4 = (Math.round(variacion*100.0)/100.0);
        else if(region.equals("reg5")) varMensual5 = (Math.round(variacion*100.0)/100.0);
        else if(region.equals("reg6")) varMensual6 = (Math.round(variacion*100.0)/100.0);
        else if(region.equals("reg7")) varMensual7 = (Math.round(variacion*100.0)/100.0);
        else if(region.equals("reg8")) varMensual8 = (Math.round(variacion*100.0)/100.0);
    }

    private void setVarAnual(String region, Double variacion){
        if(region.equals("rep")) varAnual = (Math.round(variacion*100.0)/100.0);
        else if(region.equals("reg1")) varAnual1 = (Math.round(variacion*100.0)/100.0);
        else if(region.equals("reg2")) varAnual2 = (Math.round(variacion*100.0)/100.0);
        else if(region.equals("reg3")) varAnual3 = (Math.round(variacion*100.0)/100.0);
        else if(region.equals("reg4")) varAnual4 = (Math.round(variacion*100.0)/100.0);
        else if(region.equals("reg5")) varAnual5 = (Math.round(variacion*100.0)/100.0);
        else if(region.equals("reg6")) varAnual6 = (Math.round(variacion*100.0)/100.0);
        else if(region.equals("reg7")) varAnual7 = (Math.round(variacion*100.0)/100.0);
        else if(region.equals("reg8")) varAnual8 = (Math.round(variacion*100.0)/100.0);
    }


    private String[] csv2_08(String region){
        String texto = "x;y\n";
            String sql = "SELECT i." + region + ", p.alias"
                    + " FROM FECHA f, IPC i, PONDERACION p"
                    + " WHERE i.fecha = f.fecha"
                    + " AND i.codigo > 12"
                    + " AND i.codigo = p.codigo";
            if(esEnero){
                sql += " AND ((f.anio = " + anioActual
                    + " AND f.mes = " + mesActual + ")"
                    + " OR (f.anio = " + (anioActual - 1)
                    + " AND f.mes = 12))"
                    + " ORDER BY i.codigo, f.anio, f.mes";
            }
            else{
                sql += " AND ((f.anio = " + anioActual
                    + " AND f.mes = " + mesActual + ")"
                    + " OR (f.anio = " + anioActual
                    + " AND f.mes = " + (mesActual - 1) + "))"
                    + " ORDER BY i.codigo, f.anio, f.mes";
            }
            System.out.println("2_08: " + sql);
            PreparedStatement preStatement;
            List<Intermensual> lista = new ArrayList<>();
        try {
            preStatement = conn.prepareStatement(sql);
            ResultSet result = preStatement.executeQuery();
            Double ipc, ipcAnterior;
            while(result.next()){
            	ipcAnterior = Double.parseDouble(result.getString(region));
            	if(result.next()){
            		ipc = Double.parseDouble(result.getString(region));
	                lista.add(new Intermensual(result.getString("alias"), ((ipc/ipcAnterior - 1) * 100)));
                }
            }
            for(int i=0; i<lista.size(); i++){
                for(int j=0; j<lista.size(); j++){
                    if(lista.get(i).getValor() > lista.get(j).getValor()){
                        Intermensual temp = lista.get(i);
                        lista.set(i, lista.get(j));
                        lista.set(j, temp);
                    }
                }
            }
            for(int i=0; i<10; i++){
                texto += lista.get(i).getAlias() + ";" + getNumero(Math.round(lista.get(i).getValor()*100.0)/100.0) + "\n";
            }
            result.close();
            preStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }
        String descripcion = "Los gastos básicos que registraron mayor alza porcentual mensual en " + getMesCadenaMin(mesActual)
                + " " + anioActual + " fueron: " + lista.get(0).getAlias().toLowerCase().trim() + ", " + lista.get(1).getAlias().toLowerCase().trim() + ", "
                + lista.get(2).getAlias().toLowerCase().trim() + " y " + lista.get(3).getAlias().toLowerCase().trim() + " con variaciones de "
                + getNumeroUnDecimal((Math.round(lista.get(0).getValor()*10.0)/10.0)) + "\\%, " + getNumeroUnDecimal((Math.round(lista.get(1).getValor()*10.0)/10.0)) + "\\%, "
                + getNumeroUnDecimal((Math.round(lista.get(2).getValor()*10.0)/10.0)) + "\\% y " + getNumeroUnDecimal((Math.round(lista.get(3).getValor()*10.0)/10.0)) + "\\%, respectivamente.";
        String[] resultado = {texto, descripcion};
        return resultado;
    }

    private String[] csv2_09(String region){
        String texto = "x;y\n";
        String sql = "SELECT i." + region + ", p.alias"
                    + " FROM FECHA f, IPC i, PONDERACION p"
                    + " WHERE i.fecha = f.fecha"
                    + " AND i.codigo > 12"
                    + " AND i.codigo = p.codigo";
            if(esEnero){
                sql += " AND ((f.anio = " + anioActual
                    + " AND f.mes = " + mesActual + ")"
                    + " OR (f.anio = " + (anioActual - 1)
                    + " AND f.mes = 12))"
                    + " ORDER BY i.codigo, f.anio, f.mes";
            }
            else{
                sql += " AND ((f.anio = " + anioActual
                    + " AND f.mes = " + mesActual + ")"
                    + " OR (f.anio = " + anioActual
                    + " AND f.mes = " + (mesActual - 1) + "))"
                    + " ORDER BY i.codigo, f.anio, f.mes";
            }
            System.out.println("2_09: " + sql);
            PreparedStatement preStatement;
            List<Intermensual> lista = new ArrayList<Intermensual>();

        try {
            preStatement = conn.prepareStatement(sql);
            ResultSet result = preStatement.executeQuery();
            Double ipc, ipcAnterior;
            while(result.next()){
                ipcAnterior = Double.parseDouble(result.getString(region));
                if(result.next()){
                    ipc = Double.parseDouble(result.getString(region));
                    lista.add(new Intermensual(result.getString("alias"), ((ipc/ipcAnterior - 1) * 100)));
                }
            }
            for(int i=0; i<lista.size(); i++){
                for(int j=0; j<lista.size(); j++){
                    if(lista.get(i).getValor() < lista.get(j).getValor()){
                        Intermensual temp = lista.get(i);
                        lista.set(i, lista.get(j));
                        lista.set(j, temp);
                    }
                }
            }
            for(int i=0; i<10; i++){
                texto += lista.get(i).getAlias() + ";" + getNumero(Math.round(lista.get(i).getValor()*100.0)/100.0) + "\n";
            }
            result.close();
            preStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }
         String descripcion = "Los gastos básicos que mostraron mayor baja porcentual mensual en " + getMesCadenaMin(mesActual)
                + " " + anioActual + " fueron: " + lista.get(0).getAlias().toLowerCase().trim() + ", " + lista.get(1).getAlias().toLowerCase().trim() + ", "
                + lista.get(2).getAlias().toLowerCase().trim() + " y " + lista.get(3).getAlias().toLowerCase().trim() + " con variaciones negativas de "
                + getNumeroUnDecimal((Math.round(lista.get(0).getValorAbsoluto()*10.0)/10.0))+ "\\%, " + getNumeroUnDecimal((Math.round(lista.get(1).getValorAbsoluto()*10.0)/10.0))+ "\\%, "
                + getNumeroUnDecimal((Math.round(lista.get(2).getValorAbsoluto()*10.0)/10.0))+ "\\% y " + getNumeroUnDecimal((Math.round(lista.get(3).getValorAbsoluto()*10.0)/10.0))+ "\\%, respectivamente.";
        String[] resultado = {texto, descripcion};
        return resultado;
    }

    private String[] csv2_23(String region){
        String texto = "x;y\n";
        String region2 = "";
        switch (region) {
            case "rep":
                region2 = "republica";
                break;
            case "reg1":
                region2 = "reg_I";
                break;
            case "reg2":
                region2 = "reg_II";
                break;
            case "reg3":
                region2 = "reg_III";
                break;
            case "reg4":
                region2 = "reg_IV";
                break;
            case "reg5":
                region2 = "reg_V";
                break;
            case "reg6":
                region2 = "reg_VI";
                break;
            case "reg7":
                region2 = "reg_VII";
                break;
            case "reg8":
                region2 = "reg_VIII";
                break;
        }
           String sql = "SELECT i." + region + ", p.alias, p." + region2
                    + " FROM FECHA f, IPC i, PONDERACION p"
                    + " WHERE i.fecha = f.fecha"
                    + " AND i.codigo > 0"
                    + " AND i.codigo < 13"
                    + " AND i.codigo = p.codigo";
            if(esEnero){
                 sql += " AND ((f.anio = " + anioActual
                    + " AND f.mes = " + mesActual + ")"
                    + " OR (f.anio = " + anioAnterior
                    + " AND f.mes = " + mesAnterior + "))"
                    + " ORDER BY i.codigo, f.anio, f.mes";
            }
            else{
                sql += " AND ((f.anio = " + anioActual
                    + " AND f.mes = " + mesActual + ")"
                    + " OR (f.anio = " + anioActual
                    + " AND f.mes = " + mesAnterior + "))"
                    + " ORDER BY i.codigo, f.anio, f.mes";
            }

            System.out.println("2_23: " + sql);
            PreparedStatement preStatement;
            List<Intermensual> lista = new ArrayList<>();
        try {
            preStatement = conn.prepareStatement(sql);
            ResultSet result = preStatement.executeQuery();
            Double ipcAnterior, ipc, peso;
            Double ipcMesAnterior = getIpcMesAnterior(region);
            System.out.println("ipc mes anterior: " + ipcMesAnterior);
            while(result.next()){
            	ipcAnterior = Double.parseDouble(result.getString(region));
                peso = Double.parseDouble(result.getString(region2));
                if(result.next()){
            		ipc = Double.parseDouble(result.getString(region));
	                texto += result.getString("alias") + ";" + getNumero(Math.round(peso * ((ipc - ipcAnterior) / ipcMesAnterior)*100.0)/100.0) + "\n";
                        lista.add(new Intermensual(result.getString("alias"), (Math.round(peso * ((ipc - ipcAnterior) / ipcMesAnterior)*100.0)/100.0)));
                }
            }
            result.close();
            preStatement.close();

        } catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }
        for(int i=0; i<lista.size(); i++){
                for(int j=0; j<lista.size(); j++){
                    if(lista.get(i).getValor() > lista.get(j).getValor()){
                        Intermensual temp = lista.get(i);
                        lista.set(i, lista.get(j));
                        lista.set(j, temp);
                    }
                }
        }
        String descripcion = "Las divisiones de gasto que registran mayor incidencia mensual son " + lista.get(0).getAlias().toLowerCase()
                + " y " + lista.get(1).getAlias().toLowerCase() + " con valores de " +  getNumeroDosDecimales(lista.get(0).getValor()) + "\\% y " + getNumeroDosDecimales(lista.get(1).getValor())
                + "\\%, respectivamente. La división de " + lista.get(lista.size()-1).getAlias().toLowerCase() + " registra la menor incidencia de "
                + getNumeroDosDecimales(lista.get(lista.size()-1).getValor()) + "\\%.";
        String[] resultado = {texto, descripcion};
        return resultado;
    }

    private Double getIpcMesAnterior(String region){
        if(region.equals("rep")) return ipcRepMesAnterior;
        else if(region.equals("reg1")) return ipcReg1MesAnterior;
        else if(region.equals("reg2")) return ipcReg2MesAnterior;
        else if(region.equals("reg3")) return ipcReg3MesAnterior;
        else if(region.equals("reg4")) return ipcReg4MesAnterior;
        else if(region.equals("reg5")) return ipcReg5MesAnterior;
        else if(region.equals("reg6")) return ipcReg6MesAnterior;
        else if(region.equals("reg7")) return ipcReg7MesAnterior;
        else if(region.equals("reg8")) return ipcReg8MesAnterior;
        else return 0.0;
    }

    private Double getIpcAnioAnterior(String region){
        if(region.equals("rep")) return ipcRepAnioAnterior;
        else if(region.equals("reg1")) return ipcReg1AnioAnterior;
        else if(region.equals("reg2")) return ipcReg2AnioAnterior;
        else if(region.equals("reg3")) return ipcReg3AnioAnterior;
        else if(region.equals("reg4")) return ipcReg4AnioAnterior;
        else if(region.equals("reg5")) return ipcReg5AnioAnterior;
        else if(region.equals("reg6")) return ipcReg6AnioAnterior;
        else if(region.equals("reg7")) return ipcReg7AnioAnterior;
        else if(region.equals("reg8")) return ipcReg8AnioAnterior;
        else return 0.0;
    }

    private String[] csv2_24(String region){
        String texto = "x;y\n";
        String region2 = "";
        switch (region) {
            case "rep":
                region2 = "republica";
                break;
            case "reg1":
                region2 = "reg_I";
                break;
            case "reg2":
                region2 = "reg_II";
                break;
            case "reg3":
                region2 = "reg_III";
                break;
            case "reg4":
                region2 = "reg_IV";
                break;
            case "reg5":
                region2 = "reg_V";
                break;
            case "reg6":
                region2 = "reg_VI";
                break;
            case "reg7":
                region2 = "reg_VII";
                break;
            case "reg8":
                region2 = "reg_VIII";
                break;
        }
          String sql = "SELECT i." + region + ", p.alias, p." + region2
                    + " FROM FECHA f, IPC i, PONDERACION p"
                    + " WHERE i.fecha = f.fecha"
                    + " AND f.anio = " + anioActual
                    + " AND (f.mes = " + mesActual
                    + " OR f.mes = " + (mesActual - 1) + ")"
                    + " AND i.codigo > 12"
                    + " AND i.codigo = p.codigo"
                    + " ORDER BY i.codigo, f.anio, f.mes";
            System.out.println("2_24: " + sql);
            PreparedStatement preStatement;
            List<Intermensual> lista = new ArrayList<>();
            List<Intermensual> lista2 = new ArrayList<>();

        try {
            preStatement = conn.prepareStatement(sql);
            ResultSet result = preStatement.executeQuery();
            Double ipc, ipcAnterior;
            Double ipcMesAnterior = getIpcMesAnterior(region);
            System.out.println("ipc mes anterior: " + ipcMesAnterior);
            while(result.next()){
            	ipcAnterior = Double.parseDouble(result.getString(region));
            	if(result.next()){
            		ipc = Double.parseDouble(result.getString(region));
                        Double peso = Double.parseDouble(result.getString(region2));
	                lista.add(new Intermensual(result.getString("alias"), ((peso * (ipc - ipcAnterior) / ipcMesAnterior))));
                        lista2.add(new Intermensual(result.getString("alias"), ((peso * (ipc - ipcAnterior) / ipcMesAnterior))));
                }
            }
            //ordenadno por valor absoluto
            for(int i=0; i<lista.size(); i++){
                for(int j=0; j<lista.size(); j++){
                    if(lista.get(i).getValorAbsoluto()> lista.get(j).getValorAbsoluto()){
                        Intermensual temp = lista.get(i);
                        lista.set(i, lista.get(j));
                        lista.set(j, temp);
                    }
                }
            }
            //ordenando por valor
            for(int i=0; i<lista2.size(); i++){
                for(int j=0; j<lista2.size(); j++){
                    if(lista2.get(i).getValor() > lista2.get(j).getValor()){
                        Intermensual temp = lista2.get(i);
                        lista2.set(i, lista2.get(j));
                        lista2.set(j, temp);
                    }
                }
            }
            for(int i=0; i<9; i++){
                texto += lista.get(i).getAlias() + ";" + getNumero(Math.round(lista.get(i).getValor() * 100.0) / 100.0) + "\n";
            }
            result.close();
            preStatement.close();

        } catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }
        String descripcion = "Entre los principales gastos básicos que registran la mayor incidencia positiva mensual se encuentran: "
                + lista2.get(0).getAlias().toLowerCase() + " (" + getNumeroDosDecimales((Math.round(lista2.get(0).getValorAbsoluto()* 100.0) / 100.0)) + "\\%), "
                + lista2.get(1).getAlias().toLowerCase() + " (" + getNumeroDosDecimales((Math.round(lista2.get(1).getValorAbsoluto()* 100.0) / 100.0)) + "\\%) y "
                + lista2.get(2).getAlias().toLowerCase() + " (" + getNumeroDosDecimales((Math.round(lista2.get(2).getValorAbsoluto()* 100.0) / 100.0)) + "\\%). "
                + "Las principales incidencias negativas se presentan en "
                + lista2.get(lista2.size()-1).getAlias().toLowerCase() + " (" + getNumeroDosDecimales((Math.round(lista2.get(lista2.size()-1).getValorAbsoluto()* 100.0)) / 100.0) + "\\%), "
                + lista2.get(lista2.size()-2).getAlias().toLowerCase() + " (" + getNumeroDosDecimales((Math.round(lista2.get(lista2.size()-2).getValorAbsoluto()* 100.0)) / 100.0) + "\\%) y "
                + lista2.get(lista2.size()-3).getAlias().toLowerCase() + " (" + getNumeroDosDecimales((Math.round(lista2.get(lista2.size()-3).getValorAbsoluto()* 100.0)) / 100.0) + "\\%). ";
        String[] resultado = {texto, descripcion};
        return resultado;
    }

    private String[] csv_interAnual(String region, int codigo){
        String texto = "x;y\n";
        String sql = "SELECT f.anio, f.mes, i." + region
                    + " FROM FECHA f, IPC i"
                    + " WHERE i.fecha = f.fecha"
                    + " AND i.codigo = " + codigo
                    + " AND ((f.anio = " + anioActual
                    + " AND f.mes <= " + mesActual + ")"
                    + " OR (f.anio = " + (anioActual - 1) + ")"
                    + " OR (f.anio = " + (anioActual - 2)
                    + " AND f.mes >= " + (mesActual) + "))"
                    + " ORDER BY f.anio, f.mes";
            System.out.println("interAnual " + codigo + ": " + sql);
            PreparedStatement preStatement;
            Double var = 0.0, varAnterior = 0.0, varAnioAnterior = 0.0;

        try {
            preStatement = conn.prepareStatement(sql);
            ResultSet result = preStatement.executeQuery();
            Double ipc, ipcAnterior;
            List<String> listaIPC = new ArrayList<String>() {};
            List<String> listaMes = new ArrayList<String>() {};
            List<String> listaAnio = new ArrayList<String>() {};
            while(result.next()){
                listaIPC.add(result.getString(region));
                listaMes.add(result.getString("mes"));
                listaAnio.add(result.getString("anio"));
            }
            Boolean varAnioAnteriorFlag = true;
            for(int i=12; i<listaIPC.size(); i++){
                ipcAnterior = Double.parseDouble(listaIPC.get(i-12));
                ipc = Double.parseDouble(listaIPC.get(i));
                texto += getMesAbreviacion(Integer.parseInt(listaMes.get(i))) + "-" + listaAnio.get(i) + ";" + getNumero(Math.round(((ipc/ipcAnterior - 1) * 100)*100.0)/100.0) + "\n";

                varAnterior = var;
                var = (Math.round(((ipc/ipcAnterior - 1) * 100)*100.0)/100.0);
                if(varAnioAnteriorFlag){
                    varAnioAnterior = var;
                    varAnioAnteriorFlag = false;
                }
            }
            result.close();
            preStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }
         String descripcion = "La variación interanual " + getDivisionVarMensual(codigo) + " registrada en " + getMesCadenaMin(mesActual) + " " + anioActual
                    + " (" + var + "\\%) se " + getDeltaVarInteranual(var, varAnioAnterior)
                    + " respecto a la observada en el mismo mes del año anterior (" + varAnioAnterior + "\\%) y, con relación a la registrada en " + getMesCadenaMin(mesAnterior)
                    + " " + getAnioAnterior(mesAnterior) + " (" + varAnterior + "\\%), se "+ getDeltaVarInteranual(var, varAnterior) + ".";
        String[] resultado = {texto, descripcion};
        return resultado;
    }

    private int getAnioAnterior(int mes){
        if(mes == 12) return anioAnterior;
        else return anioActual;
    }

    //se usa en csv_interanual; getDeltaVarAnual se usa en csv_intermensual
    private String getDeltaVarInteranual(Double var, Double varAnterior){
        Double delta = (Math.round((var - varAnterior)*100.0)/100.0);
        if(delta >= 0){
            return "aceleró en " + getNumeroDosDecimales(delta);
        }
        else{
            return "desaceleró en " + getNumeroDosDecimales((delta * -1));
        }
    }

    private String[] csv2_28(String region){
        String texto = "x;y\n";
        String sql = "SELECT i." + region + ", p.alias"
                    + " FROM FECHA f, IPC i, PONDERACION p"
                    + " WHERE i.fecha = f.fecha"
                    + " AND i.codigo > 0"
                    + " AND i.codigo < 13"
                    + " AND i.codigo = p.codigo"
                    + " AND ((f.anio = " + anioActual
                    + " AND f.mes = " + mesActual + ")"
                    + " OR (f.anio = " + (anioActual - 1)
                    + " AND f.mes = " + (mesActual) + "))"
                    + " ORDER BY i.codigo, f.anio";
            System.out.println("2_28: " + sql);
            PreparedStatement preStatement;
            List<Intermensual> lista = new ArrayList<>();
        try {
            preStatement = conn.prepareStatement(sql);
            ResultSet result = preStatement.executeQuery();
            Double ipc, ipcAnterior;
            while(result.next()){
                ipcAnterior = Double.parseDouble(result.getString(region));
                if(result.next()){
                    ipc = Double.parseDouble(result.getString(region));
                    texto += result.getString("alias") + ";" + getNumero(Math.round(((ipc/ipcAnterior - 1) * 100)*100.0)/100.0) + "\n";
	            lista.add(new Intermensual(result.getString("alias"), ((ipc/ipcAnterior - 1) * 100)));
                }
            }
            result.close();
            preStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }
        for(int i=0; i<lista.size(); i++){
                for(int j=0; j<lista.size(); j++){
                    if(lista.get(i).getValor() > lista.get(j).getValor()){
                        Intermensual temp = lista.get(i);
                        lista.set(i, lista.get(j));
                        lista.set(j, temp);
                    }
                }
            }
        String descripcion = "La variación interanual por división de gasto en el mes de " + getMesCadenaMin(mesActual) + " de " + anioActual
                + " resalta el comportamiento de " + lista.get(0).getAlias().toLowerCase() + ", el cual registró " + getAlza(lista.get(0).getValor())
                + " de " +getNumeroDosDecimales((Math.round(lista.get(0).getValor()*100.0)/100.0)) + "\\%. Por su parte, la división de "
                + lista.get(lista.size()-1).getAlias().toLowerCase() + " registró " + getReduccion(lista.get(lista.size()-1).getValor())
                + " en su variación interanual (" + getNumeroDosDecimales((Math.round(lista.get(lista.size()-1).getValor()*100.0)/100.0)) + "\\%).";
        String[] resultado = {texto, descripcion};
        return resultado;
     }

    private String getAlza(Double val){
        if(val>0) return "un alza";
        else return "una reducción";
    }

    private String getReduccion(Double val){
        if(val>0) return "el menor alza";
        else return "la mayor reducción";
    }

    private String[] csv2_29(String region){
        String texto = "x;y\n";
        String sql = "SELECT i." + region + ", p.alias"
                    + " FROM FECHA f, IPC i, PONDERACION p"
                    + " WHERE i.fecha = f.fecha"
                    + " AND ((f.anio = " + anioActual
                    + " AND f.mes = " + mesActual + ")"
                    + " OR (f.anio = " + (anioActual - 1)
                    + " AND f.mes = " + mesActual + "))"
                    + " AND i.codigo > 12"
                    + " AND i.codigo = p.codigo"
                    + " ORDER BY i.codigo, f.anio";
            System.out.println("2_29: " + sql);
            PreparedStatement preStatement;
            List<Intermensual> lista = new ArrayList<Intermensual>() {};

        try {
            preStatement = conn.prepareStatement(sql);
            ResultSet result = preStatement.executeQuery();
            Double ipc, ipcAnterior;
            while(result.next()){
                ipcAnterior = Double.parseDouble(result.getString(region));
                if(result.next()){
                    ipc = Double.parseDouble(result.getString(region));
                    lista.add(new Intermensual(result.getString("alias"), ((ipc/ipcAnterior - 1) * 100)));
                }
            }
            for(int i=0; i<lista.size(); i++){
                for(int j=0; j<lista.size(); j++){
                    if(lista.get(i).getValor() > lista.get(j).getValor()){
                        Intermensual temp = lista.get(i);
                        lista.set(i, lista.get(j));
                        lista.set(j, temp);
                    }
                }
            }
            for(int i=0; i<10; i++){
                texto += lista.get(i).getAlias() + ";" + getNumero(Math.round(lista.get(i).getValor()*100.0)/100.0) + "\n";
            }
            result.close();
            preStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }
         String descripcion = "Los gastos básicos que registran la mayor alza porcentual interanual en " + getMesCadenaMin(mesActual)
                + " " + anioActual + " fueron: " + lista.get(0).getAlias().toLowerCase() + ", " + lista.get(1).getAlias().toLowerCase() + ", "
                + lista.get(2).getAlias().toLowerCase() + " y " + lista.get(3).getAlias().toLowerCase() + " con variaciones de "
                + getNumeroUnDecimal((Math.round(lista.get(0).getValorAbsoluto()*100.0)/100.0))+ "\\%, " + getNumeroUnDecimal((Math.round(lista.get(1).getValorAbsoluto()*100.0)/100.0))+ "\\%, "
                + getNumeroUnDecimal((Math.round(lista.get(2).getValorAbsoluto()*100.0)/100.0))+ "\\% y " + getNumeroUnDecimal((Math.round(lista.get(3).getValorAbsoluto()*100.0)/100.0))+ "\\% respectivamente.";
        String[] resultado = {texto, descripcion};
        return resultado;
    }

    private String[] csv2_30(String region){
        String texto = "x;y\n";
        String sql = "SELECT i." + region + ", p.alias"
                    + " FROM FECHA f, IPC i, PONDERACION p"
                    + " WHERE i.fecha = f.fecha"
                    + " AND ((f.anio = " + anioActual
                    + " AND f.mes = " + mesActual + ")"
                    + " OR (f.anio = " + (anioActual - 1)
                    + " AND f.mes = " + mesActual + "))"
                    + " AND i.codigo > 12"
                    + " AND i.codigo = p.codigo"
                    + " ORDER BY i.codigo, f.anio";
            System.out.println("2_30: " + sql);
            PreparedStatement preStatement;
            List<Intermensual> lista = new ArrayList<Intermensual>() {};
        try {
            preStatement = conn.prepareStatement(sql);
            ResultSet result = preStatement.executeQuery();
            Double ipc, ipcAnterior;
            while(result.next()){
                ipcAnterior = Double.parseDouble(result.getString(region));
                if(result.next()){
                    ipc = Double.parseDouble(result.getString(region));
                    lista.add(new Intermensual(result.getString("alias"), ((ipc/ipcAnterior - 1) * 100)));
                }
            }
            for(int i=0; i<lista.size(); i++){
                for(int j=0; j<lista.size(); j++){
                    if(lista.get(i).getValor() < lista.get(j).getValor()){
                        Intermensual temp = lista.get(i);
                        lista.set(i, lista.get(j));
                        lista.set(j, temp);
                    }
                }
            }
            for(int i=0; i<10; i++){
                texto += lista.get(i).getAlias() + ";" + getNumero(Math.round(lista.get(i).getValor()*100.0)/100.0) + "\n";
            }
            result.close();
            preStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }
        String descripcion = "Los gastos básicos que mostraron mayor baja porcentual interanual en " + getMesCadenaMin(mesActual)
                + " " + anioActual + " fueron: " + lista.get(0).getAlias().toLowerCase() + ", " + lista.get(1).getAlias().toLowerCase() + ", "
                + lista.get(2).getAlias().toLowerCase() + " y " + lista.get(3).getAlias().toLowerCase() + " con variaciones de "
                + getNumeroUnDecimal((Math.round(lista.get(0).getValorAbsoluto()*100.0)/100.0))+ "\\%, " + getNumeroUnDecimal((Math.round(lista.get(1).getValorAbsoluto()*100.0)/100.0))+ "\\%, "
                + getNumeroUnDecimal((Math.round(lista.get(2).getValorAbsoluto()*100.0)/100.0))+ "\\% y " + getNumeroUnDecimal((Math.round(lista.get(3).getValorAbsoluto()*100.0)/100.0))+ "\\% respectivamente.";
        String[] resultado = {texto, descripcion};
        return resultado;
    }

    private String[] csv2_44(String region){
        String texto = "x;y\n";
        String region2 = "";
        switch (region) {
            case "rep":
                region2 = "republica";
                break;
            case "reg1":
                region2 = "reg_I";
                break;
            case "reg2":
                region2 = "reg_II";
                break;
            case "reg3":
                region2 = "reg_III";
                break;
            case "reg4":
                region2 = "reg_IV";
                break;
            case "reg5":
                region2 = "reg_V";
                break;
            case "reg6":
                region2 = "reg_VI";
                break;
            case "reg7":
                region2 = "reg_VII";
                break;
            case "reg8":
                region2 = "reg_VIII";
                break;
        }
        String sql = "SELECT i." + region + ", p.alias, p." + region2
                    + " FROM FECHA f, IPC i, PONDERACION p"
                    + " WHERE i.fecha = f.fecha"
                    + " AND i.codigo > 0"
                    + " AND i.codigo < 13"
                    + " AND i.codigo = p.codigo"
                    + " AND ((f.anio = " + anioActual
                    + " AND f.mes = " + mesActual + ")"
                    + " OR (f.anio = " + (anioActual - 1)
                    + " AND f.mes = " + mesActual + "))"
                    + " ORDER BY i.codigo, f.anio, f.mes";

            System.out.println("2_44: " + sql);
            PreparedStatement preStatement;
            List<Intermensual> lista = new ArrayList<>();
        try {
            preStatement = conn.prepareStatement(sql);
            ResultSet result = preStatement.executeQuery();
            Double ipcAnterior, ipc, peso;
            Double ipcAnioAnterior = getIpcAnioAnterior(region);
            System.out.println("ipc año anterior: " + ipcAnioAnterior);
            while(result.next()){
            	ipcAnterior = Double.parseDouble(result.getString(region));
                peso = Double.parseDouble(result.getString(region2));
                if(result.next()){
            		ipc = Double.parseDouble(result.getString(region));
	                texto += result.getString("alias") + ";" + getNumero(Math.round(peso * ((ipc - ipcAnterior) / ipcAnioAnterior)*100.0)/100.0) + "\n";
                        lista.add(new Intermensual(result.getString("alias"), (Math.round(peso * ((ipc - ipcAnterior) / ipcAnioAnterior)*100.0)/100.0)));
                    }
            }
            result.close();
            preStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }
        for(int i=0; i<lista.size(); i++){
                for(int j=0; j<lista.size(); j++){
                    if(lista.get(i).getValor() > lista.get(j).getValor()){
                        Intermensual temp = lista.get(i);
                        lista.set(i, lista.get(j));
                        lista.set(j, temp);
                    }
                }
        }
        String descripcion = "Las divisiones de gasto que registran mayor incidencia interanual son " + lista.get(0).getAlias().toLowerCase()
                + " y " + lista.get(1).getAlias().toLowerCase() + " con valores de " +  getNumeroDosDecimales(lista.get(0).getValor()) + "\\% y " + getNumeroDosDecimales(lista.get(1).getValor())
                + "\\%, respectivamente. La división de " + lista.get(lista.size()-1).getAlias().toLowerCase() + " registra la menor incidencia de "
                + getNumeroDosDecimales(lista.get(lista.size()-1).getValor()) + "\\%.";
        String[] resultado = {texto, descripcion};
        return resultado;
    }

    private String[] csv2_45(String region){
        String texto = "x;y\n";
        String region2 = "";
        switch (region) {
            case "rep":
                region2 = "republica";
                break;
            case "reg1":
                region2 = "reg_I";
                break;
            case "reg2":
                region2 = "reg_II";
                break;
            case "reg3":
                region2 = "reg_III";
                break;
            case "reg4":
                region2 = "reg_IV";
                break;
            case "reg5":
                region2 = "reg_V";
                break;
            case "reg6":
                region2 = "reg_VI";
                break;
            case "reg7":
                region2 = "reg_VII";
                break;
            case "reg8":
                region2 = "reg_VIII";
                break;
        }
        String sql = "SELECT i." + region + ", p.alias, p." + region2
                    + " FROM FECHA f, IPC i, PONDERACION p"
                    + " WHERE i.fecha = f.fecha"
                    + " AND ((f.anio = " + anioActual
                    + " AND f.mes = " + mesActual + ")"
                    + " OR (f.anio = " + (anioActual - 1)
                    + " AND f.mes = " + mesActual + "))"
                    + " AND i.codigo > 12"
                    + " AND i.codigo = p.codigo"
                    + " ORDER BY i.codigo, f.anio, f.mes";
            System.out.println("2_45: " + sql);
            PreparedStatement preStatement;
            List<Intermensual> lista = new ArrayList<>();
            List<Intermensual> lista2 = new ArrayList<>();
        try {
            preStatement = conn.prepareStatement(sql);
            ResultSet result = preStatement.executeQuery();
            Double ipc, ipcAnterior;
            Double ipcAnioAnterior = getIpcAnioAnterior(region);

            System.out.println("ipc año anterior: " + ipcAnioAnterior);
            while(result.next()){
            	ipcAnterior = Double.parseDouble(result.getString(region));
            	if(result.next()){
            		ipc = Double.parseDouble(result.getString(region));
                        Double peso = Double.parseDouble(result.getString(region2));
	                lista.add(new Intermensual(result.getString("alias"), ((peso * (ipc - ipcAnterior) / ipcAnioAnterior))));
                        lista2.add(new Intermensual(result.getString("alias"), ((peso * (ipc - ipcAnterior) / ipcAnioAnterior))));
                }
            }
            //ordenadno por valor absoluto
            for(int i=0; i<lista.size(); i++){
                for(int j=0; j<lista.size(); j++){
                    if(lista.get(i).getValorAbsoluto() > lista.get(j).getValorAbsoluto()){
                        Intermensual temp = lista.get(i);
                        lista.set(i, lista.get(j));
                        lista.set(j, temp);
                    }
                }
            }
            //ordenando por valor
            for(int i=0; i<lista2.size(); i++){
                for(int j=0; j<lista2.size(); j++){
                    if(lista2.get(i).getValor() > lista2.get(j).getValor()){
                        Intermensual temp = lista2.get(i);
                        lista2.set(i, lista2.get(j));
                        lista2.set(j, temp);
                    }
                }
            }
            for(int i=0; i<9; i++){
                texto += lista.get(i).getAlias() + ";" + getNumero(Math.round(lista.get(i).getValor() * 100.0) / 100.0) + "\n";
            }
            result.close();
            preStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }
        String descripcion = "Entre los principales gastos básicos que registran la mayor incidencia positiva interanual se encuentran: "
                + lista2.get(0).getAlias().toLowerCase() + " (" + getNumeroDosDecimales((Math.round(lista2.get(0).getValorAbsoluto()* 100.0) / 100.0)) + "\\%), "
                + lista2.get(1).getAlias().toLowerCase() + " (" + getNumeroDosDecimales((Math.round(lista2.get(1).getValorAbsoluto()* 100.0) / 100.0)) + "\\%) y "
                + lista2.get(2).getAlias().toLowerCase() + " (" + getNumeroDosDecimales((Math.round(lista2.get(2).getValorAbsoluto()* 100.0) / 100.0)) + "\\%). "
                + "Las principales incidencias negativas se presentan en "
                + lista2.get(lista2.size()-1).getAlias().toLowerCase() + " (" + getNumeroDosDecimales((Math.round(lista2.get(lista2.size()-1).getValorAbsoluto()* 100.0) / 100.0)) + "\\%), "
                + lista2.get(lista2.size()-2).getAlias().toLowerCase() + " (" + getNumeroDosDecimales((Math.round(lista2.get(lista2.size()-2).getValorAbsoluto()* 100.0) / 100.0)) + "\\%) y "
                + lista2.get(lista2.size()-3).getAlias().toLowerCase() + " (" + getNumeroDosDecimales((Math.round(lista2.get(lista2.size()-3).getValorAbsoluto()* 100.0) / 100.0)) + "\\%). ";
        String[] resultado = {texto, descripcion};
        return resultado;
    }

    private String[] csv2_47(String region){
         String texto = "x;y\n";
         String sql = "SELECT f.anio, f.mes, i." + region
                    + " FROM FECHA f, IPC i"
                    + " WHERE i.fecha = f.fecha"
                    + " AND ((f.anio = " + anioActual
                    + " AND f.mes <= " + mesActual + ")"
                    + " OR (f.anio = " + (anioActual - 1)
                    + " AND f.mes >= " + mesActual + "))"
                    + " AND i.codigo = 0"
                    + " ORDER BY f.anio, f.mes";
            System.out.println("2_47: " + sql);
            PreparedStatement preStatement;
            Double valor = 0.0, mayor = -100.0, menor = 100.0;
            int mesMayor = 0, mesMenor = 0;
            int anioMayor = anioActual, anioMenor = anioActual;
        try {
            preStatement = conn.prepareStatement(sql);
            ResultSet result = preStatement.executeQuery();
            while(result.next()){
                valor = ((1.0/Double.parseDouble(result.getString(region))) * 100);
                texto += getMesAbreviacion(Integer.parseInt(result.getString("mes"))) + "-" + result.getString("anio").substring(2,4) + ";" + getNumero(valor) + "\n";
            if(valor > mayor){
                             mayor = valor;
                             mesMayor = result.getInt("mes");
                             anioMayor = result.getInt("anio");
                         }
                         else if(valor < menor)
                         {
                             menor = valor;
                             anioMenor = result.getInt("anio");
                             mesMenor = result.getInt("mes");
                         }
            }
            result.close();
            preStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }
        String descripcion = "El poder adquisitivo del quetzal a " + getMesCadenaMin(mesActual) + " de " + anioActual
                + " es de " + getNumeroDosDecimales((Math.round((valor)*100.0)/100.0)) + ". El mayor valor adquisitivo se encuentra en el mes de "
                + getMesCadenaMin(mesMayor) + " de " + anioMayor + " con un valor de " + getNumeroDosDecimales((Math.round((mayor)*100.0)/100.0))
                + " y el menor se encuentra en el mes de "  + getMesCadenaMin(mesMenor) + " de " + anioMenor + " con un valor de " + getNumeroDosDecimales((Math.round((menor)*100.0)/100.0)) + ".";
        String[] resultado = {texto, descripcion};
        return resultado;
     }

    private String[] csv1_M1(String region){
        String texto = "x;y\n";
        String sql = "select reg1,reg2,reg3,reg4,reg5,reg6,reg7,reg8 from IPC i, FECHA f"
                    + " where i.fecha = f.fecha AND "
                    + " f.anio = " +anioActual
                    + " and f.mes = " + mesActual;


            System.out.println("M1: " + sql);
            PreparedStatement preStatement;
            Double mayor = 0.0, menor = 9999999.0;
            String mayorR = "región " + notaMapas(), menorR = "región";
        try {
            preStatement = conn.prepareStatement(sql);
            ResultSet result = preStatement.executeQuery();
            	if(result.next()){
                        Double valor = result.getDouble("reg1");
                        if(valor > mayor){
                            mayor = valor;
                            mayorR += " I";
                        }
                        if(valor < menor){
                            menor = valor;
                            menorR += " I";
                        }
            		texto += "1"+";"+ getNumero(valor) + "\n";
                        valor = result.getDouble("reg2");
                        if(valor > mayor){
                            mayor = valor;
                            mayorR += " II";
                        }
                        if(valor < menor){
                            menor = valor;
                            menorR += " II";
                        }
            		texto += "2"+";"+ getNumero(valor) + "\n";
                        valor = result.getDouble("reg3");
                        if(valor > mayor){
                            mayor = valor;
                            mayorR += " III";
                        }
                        if(valor < menor){
                            menor = valor;
                            menorR += " III";
                        }
            		texto += "3"+";"+ getNumero(valor) + "\n";
                        valor = result.getDouble("reg4");
                        if(valor > mayor){
                            mayor = valor;
                            mayorR += " IV";
                        }
                        if(valor < menor){
                            menor = valor;
                            menorR += " IV";
                        }
            		texto += "4"+";"+ getNumero(valor) + "\n";
                        valor = result.getDouble("reg5");
                        if(valor > mayor){
                            mayor = valor;
                            mayorR += " V";
                        }
                        if(valor < menor){
                            menor = valor;
                            menorR += " V";
                        }
            		texto += "5"+";"+ getNumero(valor) + "\n";
                        valor = result.getDouble("reg6");
                        if(valor > mayor){
                            mayor = valor;
                            mayorR += " VI";
                        }
                        if(valor < menor){
                            menor = valor;
                            menorR += " VI";
                        }
            		texto += "6"+";"+ getNumero(valor) + "\n";
                        valor = result.getDouble("reg7");
                        if(valor > mayor){
                            mayor = valor;
                            mayorR += " VII";
                        }
            		texto += "7"+";"+ getNumero(valor) + "\n";
                        valor = result.getDouble("reg8");
                        if(valor > mayor){
                            mayor = valor;
                            mayorR += " VIII";
                        }
                        if(valor < menor){
                            menor = valor;
                            menorR += " VIII";
                        }
            		texto += "8"+";"+ getNumero(valor) + "\n";
                }
            result.close();
            preStatement.close();

        } catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }
        String descripcion = "En el mes de " + getMesCadenaMin(mesActual) + " del año " + anioActual
                + ", la " + mayorR + " presentó el mayor índice de precios al consumidor, "
                + " el cual fue de " + getNumeroUnDecimal(mayor) + ", mientras que la " + menorR + " presentó el índice"
                + " más bajo, de " + getNumeroUnDecimal(menor) + "\\footnote{PONER REGIONES DONDE CORRESPONDE ahi arribita}.";
        String[] resultado = {texto, descripcion};
        return resultado;
    }

    private String getFecha(){
        return "el mes de " + getMesCadenaMin(mesActual) + " del año " + anioActual;
    }

    private String[] csv1_M2() throws SQLException{
        String texto = "x;y\n";
        String sql = "SELECT f.anio, f.mes, i.reg1, i.reg2, i.reg3, i.reg4, i.reg5, i.reg6, i.reg7, i.reg8"
                    + " FROM FECHA f, IPC i"
                    + " WHERE i.fecha = f.fecha"
                    + " AND i.codigo = 0";
            if(esEnero){
                sql += " AND ((f.anio = " + anioActual
                    + " AND f.mes = " + mesActual + ")"
                    + " OR (f.anio = " + (anioActual - 1)
                    + " AND f.mes = 12))"
                    + " ORDER BY f.anio, f.mes";
            }
            else{
                sql += " AND ((f.anio = " + anioActual
                    + " AND f.mes = " + mesActual + ")"
                    + " OR (f.anio = " + anioActual
                    + " AND f.mes = " + (mesActual - 1) + "))"
                    + " ORDER BY f.anio, f.mes";
            }
            System.out.println("mapa2: " + sql);
            PreparedStatement preStatement;
            preStatement = conn.prepareStatement(sql);
            ResultSet result = preStatement.executeQuery();
            Double ipc = 0.0;
            List<Double> lista = new ArrayList<>();
            List<Double> listaVar = new ArrayList<>();
            List<String> listaRegiones = new ArrayList<>();
            if(result.next()){
                for(int i=1; i<=8; i++){
                    lista.add(Double.parseDouble(result.getString("reg" + i)));
                    listaRegiones.add(getRomano(i));
                }
            }
            if(result.next()){
                for(int i=1; i<=8; i++){
                    ipc = Double.parseDouble(result.getString("reg" + i));
                    texto += i + ";" + getNumero(Math.round(((ipc/lista.get(i-1) - 1) * 100)*100.0)/100.0) + "\n";
                    listaVar.add((Math.round(((ipc/lista.get(i-1) - 1) * 100)*100.0)/100.0));
                }
            }
            Double temp;
            String tempRegion;
            for(int i=0; i<listaVar.size(); i++){
                for(int j=0; j<listaVar.size(); j++){
                    if(listaVar.get(i) > listaVar.get(j)){
                        temp = listaVar.get(i);
                        listaVar.set(i, listaVar.get(j));
                        listaVar.set(j, temp);
                        tempRegion = listaRegiones.get(i);
                        listaRegiones.set(i, listaRegiones.get(j));
                        listaRegiones.set(j, tempRegion);
                    }
                }
            }
            String descripcion = "En el mes de " + getFecha() + ", las regiones " + notaMapas() + listaRegiones.get(0) + " y " + listaRegiones.get(1)
                    + " presentan las variaciones mensuales más altas en el nivel de precios, con valores de "
                    + getNumeroDosDecimales(listaVar.get(0)) + "\\% y " + getNumeroDosDecimales(listaVar.get(1)) + "\\%, respectivamente."
                    + " Por su parte, las regiones "
                    + listaRegiones.get(listaRegiones.size()-1) + " y " + listaRegiones.get(listaRegiones.size()-2)
                    + " muestran las variaciones más bajas con sendos valores de " + getNumeroDosDecimales(listaVar.get(listaVar.size()-1)) + "\\% y "
                    + getNumeroDosDecimales(listaVar.get(listaVar.size()-2)) + " \\%.";
            result.close();
            preStatement.close();
            String[] resultado = {texto, descripcion};
        return resultado;
    }

    private String getRomano(int numero){
         if(numero == 1) return "I";
         else if(numero == 2) return "II";
         else if(numero == 3) return "III";
         else if(numero == 4) return "IV";
         else if(numero == 5) return "V";
         else if(numero == 6) return "VI";
         else if(numero == 7) return "VII";
         else if(numero == 8) return "VIII";
         else return "";
     }

    private String[] csv1_M3(String region){
        String texto = "x;y\n";
        String region2 = "";
        switch (region) {
            case "rep":
                region2 = "republica";
                break;
            case "reg1":
                region2 = "reg_I";
                break;
            case "reg2":
                region2 = "reg_II";
                break;
            case "reg3":
                region2 = "reg_III";
                break;
            case "reg4":
                region2 = "reg_IV";
                break;
            case "reg5":
                region2 = "reg_V";
                break;
            case "reg6":
                region2 = "reg_VI";
                break;
            case "reg7":
                region2 = "reg_VII";
                break;
            case "reg8":
                region2 = "reg_VIII";
                break;
        }
           String sql = "SELECT i.reg1, i.reg2, i.reg3, i.reg4, i.reg5, i.reg6, i.reg7, i.reg8, p.alias"
                    + ", p.reg_I, p.reg_II, p.reg_III, p.reg_IV, p.reg_V, p.reg_VI, p.reg_VII, p.reg_VIII"
                    + " FROM FECHA f, IPC i, PONDERACION p"
                    + " WHERE i.fecha = f.fecha"
                    + " AND i.codigo = 0"
                    + " AND i.codigo = p.codigo";
            if(esEnero){
                 sql += " AND ((f.anio = " + anioActual
                    + " AND f.mes = " + mesActual + ")"
                    + " OR (f.anio = " + anioAnterior
                    + " AND f.mes = " + mesAnterior + "))"
                    + " ORDER BY i.codigo, f.anio, f.mes";
            }
            else{
                sql += " AND ((f.anio = " + anioActual
                    + " AND f.mes = " + mesActual + ")"
                    + " OR (f.anio = " + anioActual
                    + " AND f.mes = " + mesAnterior + "))"
                    + " ORDER BY i.codigo, f.anio, f.mes";
            }

            System.out.println("M3: " + sql);
            PreparedStatement preStatement;
            List<Double> lista = new ArrayList<>();
            List<Double> pesos = new ArrayList<>();
            List<String> listaRegiones = new ArrayList<>();
            List<Double> listaIncidencias = new ArrayList<>();
        try {
            preStatement = conn.prepareStatement(sql);
            ResultSet result = preStatement.executeQuery();
            Double ipc;
            if(result.next()){
                for(int i=1; i<=8; i++){
                    lista.add(Double.parseDouble(result.getString("reg" + i)));
                    pesos.add(Double.parseDouble(result.getString("reg_" + getRomano(i))));
                    listaRegiones.add(getRomano(i));
                }
            }
            if(result.next()){
                for(int i=1; i<=8; i++){
                    ipc = Double.parseDouble(result.getString("reg" + i));
                    texto += (i) + ";" + getNumero(Math.round((pesos.get(i-1) * ((ipc - lista.get(i-1)) / ipcRepMesAnterior))*100.0)/100.0) + "\n";
                    listaIncidencias.add((Math.round((pesos.get(i-1) * ((ipc - lista.get(i-1)) / ipcRepMesAnterior))*100.0)/100.0));
                }
            }

            result.close();
            preStatement.close();

        } catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }
        Double temp;
            String tempRegion;
            for(int i=0; i<listaIncidencias.size(); i++){
                for(int j=0; j<listaIncidencias.size(); j++){
                    if(listaIncidencias.get(i) > listaIncidencias.get(j)){
                        temp = listaIncidencias.get(i);
                        listaIncidencias.set(i, listaIncidencias.get(j));
                        listaIncidencias.set(j, temp);
                        tempRegion = listaRegiones.get(i);
                        listaRegiones.set(i, listaRegiones.get(j));
                        listaRegiones.set(j, tempRegion);
                    }
                }
            }
            String descripcion = "En el mes de " + getFecha() + ", las regiones " + notaMapas() + listaRegiones.get(0) + " y " + listaRegiones.get(1)
                    + " presentan las incidencias más altas en la variación mensual, con incidencias de "
                    + getNumeroDosDecimales(listaIncidencias.get(0)) + "\\% y " + getNumeroDosDecimales(listaIncidencias.get(1)) + "\\% respectivamente. Por su parte, las regiones "
                    + listaRegiones.get(listaRegiones.size()-1) + " y " + listaRegiones.get(listaRegiones.size()-2)
                    + " muestran las incidencias más bajas con valores de " + getNumeroDosDecimales(listaIncidencias.get(listaIncidencias.size()-1))
                    + "\\% y "  + getNumeroDosDecimales(listaIncidencias.get(listaIncidencias.size()-2)) + "\\% respectivamente.";
        String[] resultado = {texto, descripcion};
        return resultado;
    }

    private String[] csv1_M4() throws SQLException{
        String texto = "x;y\n";
        String sql = "SELECT f.anio, f.mes, i.reg1, i.reg2, i.reg3, i.reg4, i.reg5, i.reg6, i.reg7, i.reg8"
                    + " FROM FECHA f, IPC i"
                    + " WHERE i.fecha = f.fecha"
                    + " AND i.codigo = 0"
                    + " AND ((f.anio = " + anioActual
                    + " AND f.mes = " + mesActual + ")"
                    + " OR (f.anio = " + (anioActual - 1)
                    + " AND f.mes = " + mesActual + "))"
                    + " ORDER BY f.anio, f.mes";

            System.out.println("mapa4: " + sql);
            PreparedStatement preStatement;
            preStatement = conn.prepareStatement(sql);
            ResultSet result = preStatement.executeQuery();
            Double ipc = 0.0;
            List<Double> lista = new ArrayList<>();
            List<Double> listaVar = new ArrayList<>();
            List<String> listaRegiones = new ArrayList<>();
            if(result.next()){
                for(int i=1; i<=8; i++){
                    lista.add(Double.parseDouble(result.getString("reg" + i)));
                    listaRegiones.add(getRomano(i));
                }
            }
            if(result.next()){
                for(int i=1; i<=8; i++){
                    ipc = Double.parseDouble(result.getString("reg" + i));
                    texto += (i) + ";" + getNumero(Math.round(((ipc/lista.get(i-1) - 1) * 100)*100.0)/100.0) + "\n";
                    listaVar.add((Math.round(((ipc/lista.get(i-1) - 1) * 100)*100.0)/100.0));
                }
            }
            result.close();
            preStatement.close();
            Double temp;
            String tempRegion;
            for(int i=0; i<listaVar.size(); i++){
                for(int j=0; j<listaVar.size(); j++){
                    if(listaVar.get(i) > listaVar.get(j)){
                        temp = listaVar.get(i);
                        listaVar.set(i, listaVar.get(j));
                        listaVar.set(j, temp);
                        tempRegion = listaRegiones.get(i);
                        listaRegiones.set(i, listaRegiones.get(j));
                        listaRegiones.set(j, tempRegion);
                    }
                }
            }
            String descripcion = "En el mes de " + getFecha() + ", las regiones " + notaMapas() + listaRegiones.get(0) + " y " + listaRegiones.get(1)
                    + " presentan las variaciones interanuales más altas a nivel de precios, con valores de "
                    + getNumeroDosDecimales(listaVar.get(0)) + "\\% y " + getNumeroDosDecimales(listaVar.get(1)) + "\\% respectivamente. Por su parte, las regiones "
                    + listaRegiones.get(listaRegiones.size()-1) + " y " + listaRegiones.get(listaRegiones.size()-1)
                    + " muestran las variaciones más bajas con valores de " + getNumeroDosDecimales(listaVar.get(listaVar.size()-1)) + "\\% y "
                    + getNumeroDosDecimales(listaVar.get(listaVar.size()-2)) + "\\% respectivamente.";
            result.close();
            preStatement.close();
            String[] resultado = {texto, descripcion};
        return resultado;
    }

    private String[] csv1_M5(String region){
        String texto = "x;y\n";
        String region2 = "";
        switch (region) {
            case "rep":
                region2 = "republica";
                break;
            case "reg1":
                region2 = "reg_I";
                break;
            case "reg2":
                region2 = "reg_II";
                break;
            case "reg3":
                region2 = "reg_III";
                break;
            case "reg4":
                region2 = "reg_IV";
                break;
            case "reg5":
                region2 = "reg_V";
                break;
            case "reg6":
                region2 = "reg_VI";
                break;
            case "reg7":
                region2 = "reg_VII";
                break;
            case "reg8":
                region2 = "reg_VIII";
                break;
        }
           String sql = "SELECT i.reg1, i.reg2, i.reg3, i.reg4, i.reg5, i.reg6, i.reg7, i.reg8, p.alias"
                    + ", p.reg_I, p.reg_II, p.reg_III, p.reg_IV, p.reg_V, p.reg_VI, p.reg_VII, p.reg_VIII"
                    + " FROM FECHA f, IPC i, PONDERACION p"
                    + " WHERE i.fecha = f.fecha"
                    + " AND i.codigo = 0"
                    + " AND i.codigo = p.codigo"
                    + " AND ((f.anio = " + anioActual
                    + " AND f.mes = " + mesActual + ")"
                    + " OR (f.anio = " + (anioActual - 1)
                    + " AND f.mes = " + mesActual + "))"
                    + " ORDER BY f.anio, f.mes";

            System.out.println("M3: " + sql);
            PreparedStatement preStatement;
            List<Double> lista = new ArrayList<>();
            List<Double> pesos = new ArrayList<>();
            List<String> listaRegiones = new ArrayList<>();
            List<Double> listaIncidencias = new ArrayList<>();
        try {
            preStatement = conn.prepareStatement(sql);
            ResultSet result = preStatement.executeQuery();
            Double ipc;
            if(result.next()){
                for(int i=1; i<=8; i++){
                    lista.add(Double.parseDouble(result.getString("reg" + i)));
                    pesos.add(Double.parseDouble(result.getString("reg_" + getRomano(i))));
                    listaRegiones.add(getRomano(i));
                }
            }
            if(result.next()){
                for(int i=1; i<=8; i++){
                    ipc = Double.parseDouble(result.getString("reg" + i));
                    texto += (i) + ";" + getNumero(Math.round((pesos.get(i-1) * ((ipc - lista.get(i-1)) / ipcRepMesAnterior))*100.0)/100.0) + "\n";
                    listaIncidencias.add((Math.round((pesos.get(i-1) * ((ipc - lista.get(i-1)) / ipcRepMesAnterior))*100.0)/100.0));
                }
            }

            result.close();
            preStatement.close();

        } catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }
        Double temp;
            String tempRegion;
            for(int i=0; i<listaIncidencias.size(); i++){
                for(int j=0; j<listaIncidencias.size(); j++){
                    if(listaIncidencias.get(i) > listaIncidencias.get(j)){
                        temp = listaIncidencias.get(i);
                        listaIncidencias.set(i, listaIncidencias.get(j));
                        listaIncidencias.set(j, temp);
                        tempRegion = listaRegiones.get(i);
                        listaRegiones.set(i, listaRegiones.get(j));
                        listaRegiones.set(j, tempRegion);
                    }
                }
            }
            String descripcion = "En el mes de " + getFecha() + ", las regiones " + notaMapas() + listaRegiones.get(0) + " y " + listaRegiones.get(1)
                    + " presentan las incidencias más altas en la variación anual, con incidencias de "
                    + getNumeroDosDecimales(listaIncidencias.get(0)) + "\\% y " + getNumeroDosDecimales(listaIncidencias.get(1)) + "\\% respectivamente. Por su parte, las regiones "
                    + listaRegiones.get(listaRegiones.size()-1) + " y " + listaRegiones.get(listaRegiones.size()-1)
                    + " muestran lad incidenciad más bajas con valores de " + getNumeroDosDecimales(listaIncidencias.get(listaIncidencias.size()-1)) + "\\% y "
                    + getNumeroDosDecimales(listaIncidencias.get(listaIncidencias.size()-2)) + "\\% respectivamente.";
        String[] resultado = {texto, descripcion};
        return resultado;
    }

    private String[] csv2_01(String region) throws SQLException{
        String sql2 = "SELECT f.anio, f.mes, i." + region
                    + " FROM FECHA f, IPC i"
                    + " WHERE i.fecha = f.fecha"
                    + " AND i.codigo = 0"
                    + " AND ((f.anio = " + anioActual
                    + " AND f.mes <= " + mesActual + ")"
                    + " OR (f.anio = " + (anioActual - 1) + ")"
                    + " OR (f.anio = " + (anioActual - 2)
                    + " AND f.mes >= " + (mesActual) + "))"
                    + " ORDER BY f.anio, f.mes";
            System.out.println("2_01 interanual: " + sql2);
            PreparedStatement preStatement2;
            
            List<String> listaIPCI = new ArrayList<String>() {};
            try {
            preStatement2 = conn.prepareStatement(sql2);
            ResultSet result2 = preStatement2.executeQuery();
            List<String> listaIPC = new ArrayList<String>() {};
            boolean dic=true;
            
            while(result2.next()){
                listaIPC.add(result2.getString(region));                
            }
            Double ipcAnterior, ipc;
            String anual;
            
            for(int i=12; i<listaIPC.size(); i++){                
                ipcAnterior = Double.parseDouble(listaIPC.get(i-12));
                ipc = Double.parseDouble(listaIPC.get(i));
                anual = (Math.round(((ipc/ipcAnterior - 1) * 100)*100.0)/100.0) + "";
                listaIPCI.add(anual);
            }
            result2.close();
            preStatement2.close();
        } catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }

        String texto = "";
        String sqlDiciembre = "SELECT I."+region+" FROM "
                + "(SELECT FECHA FROM FECHA WHERE MES = 12 AND ANIO BETWEEN "
                +(anioActual-2)+" AND "+(anioActual-1)+")A,"
                + " IPC I WHERE I.FECHA = A.FECHA AND CODIGO =0;";
        
        PreparedStatement preStatement0;
            preStatement0 = conn.prepareStatement(sqlDiciembre);
            ResultSet result0 = preStatement0.executeQuery();
            Double dicAnioPreanterior = 0.0;
            Double dicAnioAnterior=0.0;
            if(result0.next()){
                dicAnioPreanterior=Double.parseDouble(result0.getString(region));
            }
            if(result0.next()){
                dicAnioAnterior=Double.parseDouble(result0.getString(region));
            }
        
        String sql = "SELECT f.anio, f.mes, i." + region
                    + " FROM FECHA f, IPC i"
                    + " WHERE i.fecha = f.fecha"
                    + " AND i.codigo = 0";
            if(esEnero){
                sql += " AND ((f.anio = " + anioActual
                    + " AND f.mes = " + mesActual + ")"
                    + " OR (f.anio = " + (anioActual - 1)
                    + " AND f.mes >= " + mesActual + ")"
                    + " OR (f.anio = " + (anioActual - 2)
                    + " AND f.mes = 12))"
                    + " ORDER BY f.anio, f.mes";
            }
            else{
                sql += " AND ((f.anio = " + anioActual
                    + " AND f.mes <= " + mesActual + ")"
                    + " OR (f.anio = " + (anioActual - 1)
                    + ")"
                    + " OR (f.anio = " + (anioActual - 2)
                    + " AND f.mes = 12))"
                    + " ORDER BY f.anio, f.mes";
            }   
            System.out.println("01: " + sql);
            PreparedStatement preStatement;
            preStatement = conn.prepareStatement(sql);
            ResultSet result = preStatement.executeQuery();
            Double ipcAnterior = 0.0;
            Double ipc = 0.0;
            Double ipcDiciembre =0.0;
            if(result.next()){
            	ipcAnterior = Double.parseDouble(result.getString(region));
            }
            List<String> lista = new ArrayList<>();
            Double acumulada = 0.0;
            Double variacion = 0.0;
            String descripcion = "El índice de precios al consumidor actual se construyó con base en"
                    + " la encuesta nacional de ingresos y gastos familiares 2009/2010. "
                    + "En " + getMesCadena(mesActual).toLowerCase() + " " + this.anioActual
                    + " el índice era "
                    ;
            
                    /*+ " y registró"
                    + "La variación mensual es el cambio relativo del IPC del mes en estudio con respecto al mes anterior. "
                    + "La variación interanual es el cambio relativo del IPC del mes en estudio respecto al mismo mes del año anterior. "
                    + "La variación acumulada es la suma de todas las variaciones mensuales desde el mes de enero del mismo año. "
                    + "A nivel " + getRegionCadena(region) + ", los resultados más importantes a " + getMesCadena(mesActual).toLowerCase() + " de " + anioActual + " son los siguientes: ";
            */while(result.next()){
            	ipc = Double.parseDouble(result.getString(region));
                variacion = ((ipc/ipcAnterior - 1) * 100);
                if(Integer.parseInt(result.getString("mes")) == 1) acumulada = variacion;
                else {
                    //asdfasdfdas
                    if (Integer.parseInt(result.getString("anio"))==(anioActual-1))
                        acumulada=(ipc/dicAnioPreanterior-1)*100;
                    else
                        acumulada=(ipc/dicAnioAnterior-1)*100;
                            }
                lista.add(result.getString("anio") + "&" + getMesCadena(Integer.parseInt(result.getString("mes"))) + "&"
                        + getNumero((Math.round(ipc*100.0)/100.0)) + "&" + getNumero((Math.round(variacion*100.0)/100.0)) + "&"
                        + getNumero((Math.round(acumulada*100.0)/100.0)) + "&"
                        );
                ipcAnterior = ipc;
            }
            setIPCActual(region, ipc);
            setVarMensual(region, variacion);
            setVarAnual(region, Double.parseDouble(listaIPCI.get(listaIPCI.size() - 1)));
            if(region.equalsIgnoreCase("rep")) this.varAcumulada = (Math.round(acumulada*100.0)/100.0);
            descripcion += getNumeroUnDecimal(ipc)
                    + " registrando una variación intermensual\\footnote{La variación intermensual es el cambio relativo"
                    + " del IPC del mes en estudio con respecto al mes anterior.} de " 
                    + getNumeroDosDecimales((Math.round(variacion*100.0)/100.0)) + "\\%, la interanual\\footnote{La variación"
                    + " interanual es el cambio relativo del IPC del mes en estudio respecto al mismo mes del año anterior.} es de "
                    + listaIPCI.get(listaIPCI.size() - 1) + "\\% y la acumulada\\footnote{La variación acumulada"
                    + " es el cambio relativo del IPC del mes en estudio respecto al mes de diciembre"
                    + " del año anterior} es de " + getNumeroDosDecimales((Math.round(acumulada*100.0)/100.0)) + "\\%";
            result.close();
            preStatement.close();
            for(int i=lista.size()-13; i<lista.size(); i++){
                texto += lista.get(i) + getNumero(Double.parseDouble(listaIPCI.get(i + 13 - lista.size()))) + "\\\\\n";
            }
            String[] resultado = new String[2];
            resultado[0] = texto;
            resultado[1] = descripcion;
        return resultado;
    }


    private void setIPCActual(String region, Double val){
        if(region.equals("rep")) ipcActual = (Math.round(val*100.0)/100.0);
        else if(region.equals("reg1")) ipcActual1 = (Math.round(val*100.0)/100.0);
        else if(region.equals("reg2")) ipcActual2 = (Math.round(val*100.0)/100.0);
        else if(region.equals("reg3")) ipcActual3 = (Math.round(val*100.0)/100.0);
        else if(region.equals("reg4")) ipcActual4 = (Math.round(val*100.0)/100.0);
        else if(region.equals("reg5")) ipcActual5 = (Math.round(val*100.0)/100.0);
        else if(region.equals("reg6")) ipcActual6 = (Math.round(val*100.0)/100.0);
        else if(region.equals("reg7")) ipcActual7 = (Math.round(val*100.0)/100.0);
        else if(region.equals("reg8")) ipcActual8 = (Math.round(val*100.0)/100.0);
    }
    /*private void setIPCActual(String region, Double val){
        if(region.equals("rep")) varAnual = (Math.round(val*100.0)/100.0);
        else if(region.equals("reg1")) varAnual1 = (Math.round(val*100.0)/100.0);
        else if(region.equals("reg2")) varAnual2 = (Math.round(val*100.0)/100.0);
        else if(region.equals("reg3")) varAnual3 = (Math.round(val*100.0)/100.0);
        else if(region.equals("reg4")) varAnual4 = (Math.round(val*100.0)/100.0);
        else if(region.equals("reg5")) varAnual5 = (Math.round(val*100.0)/100.0);
        else if(region.equals("reg6")) varAnual6 = (Math.round(val*100.0)/100.0);
        else if(region.equals("reg7")) varAnual7 = (Math.round(val*100.0)/100.0);
        else if(region.equals("reg8")) varAnual8 = (Math.round(val*100.0)/100.0);
    }*/

    private String getMesCadena(int mes){
         if(mes == 1)return "Enero";
         else if(mes == 2)return "Febrero";
         else if(mes == 3)return "Marzo";
         else if(mes == 4)return "Abril";
         else if(mes == 5)return "Mayo";
         else if(mes == 6)return "Junio";
         else if(mes == 7)return "Julio";
         else if(mes == 8)return "Agosto";
         else if(mes == 9)return "Septiembre";
         else if(mes == 10)return "Octubre";
         else if(mes == 11)return "Noviembre";
         else if(mes == 12)return "Diciembre";
         return "";
     }

    private String getMesCadenaMin(int mes){
         if(mes == 1)return "enero";
         else if(mes == 2)return "febrero";
         else if(mes == 3)return "marzo";
         else if(mes == 4)return "abril";
         else if(mes == 5)return "mayo";
         else if(mes == 6)return "junio";
         else if(mes == 7)return "julio";
         else if(mes == 8)return "agosto";
         else if(mes == 9)return "septiembre";
         else if(mes == 10)return "octubre";
         else if(mes == 11)return "noviembre";
         else if(mes == 12)return "diciembre";
         return "";
     }

    private String[] csv2_05(){
        String texto = "";
            String sql = "SELECT i.rep, i.reg1, i.reg2, i.reg3, i.reg4, i.reg5, i.reg6, i.reg7, i.reg8"
                    + " FROM FECHA f, IPC i"
                    + " WHERE i.fecha = f.fecha"
                    + " AND i.codigo >= 0"
                    + " AND i.codigo < 13";
            if(esEnero){
                sql += " AND ((f.anio = " + anioActual
                    + " AND f.mes = " + mesActual + ")"
                    + " OR (f.anio = " + (anioActual - 1)
                    + " AND f.mes = 12))"
                    + " ORDER BY i.codigo, f.anio, f.mes";
            }
            else{
                sql += " AND ((f.anio = " + anioActual
                    + " AND f.mes = " + mesActual + ")"
                    + " OR (f.anio = " + (anioActual)
                    + " AND f.mes = " + (mesActual - 1) + "))"
                    + " ORDER BY i.codigo, f.anio, f.mes";
            }
            System.out.println("2_05: " + sql);
            PreparedStatement preStatement;
            List<String> lista = new ArrayList<>();
            List<String> lista1 = new ArrayList<>();
            List<String> lista2 = new ArrayList<>();
            List<String> lista3 = new ArrayList<>();
            List<String> lista4 = new ArrayList<>();
            List<String> lista5 = new ArrayList<>();
            List<String> lista6 = new ArrayList<>();
            List<String> lista7 = new ArrayList<>();
            List<String> lista8 = new ArrayList<>();

        try {
            preStatement = conn.prepareStatement(sql);
            ResultSet result = preStatement.executeQuery();
            Double ipcAnterior = 0.0;
            Double ipcAnterior1 = 0.0;
            Double ipcAnterior2 = 0.0;
            Double ipcAnterior3 = 0.0;
            Double ipcAnterior4 = 0.0;
            Double ipcAnterior5 = 0.0;
            Double ipcAnterior6 = 0.0;
            Double ipcAnterior7 = 0.0;
            Double ipcAnterior8 = 0.0;
            Double ipc = 0.0;
            Double ipc1 = 0.0;
            Double ipc2 = 0.0;
            Double ipc3 = 0.0;
            Double ipc4 = 0.0;
            Double ipc5 = 0.0;
            Double ipc6 = 0.0;
            Double ipc7 = 0.0;
            Double ipc8 = 0.0;

            while(result.next()){

            	ipcAnterior = Double.parseDouble(result.getString("rep"));
                ipcAnterior1 = Double.parseDouble(result.getString("reg1"));
                ipcAnterior2 = Double.parseDouble(result.getString("reg2"));
                ipcAnterior3 = Double.parseDouble(result.getString("reg3"));
                ipcAnterior4 = Double.parseDouble(result.getString("reg4"));
                ipcAnterior5 = Double.parseDouble(result.getString("reg5"));
                ipcAnterior6 = Double.parseDouble(result.getString("reg6"));
                ipcAnterior7 = Double.parseDouble(result.getString("reg7"));
                ipcAnterior8 = Double.parseDouble(result.getString("reg8"));
                if(result.next()){
                    ipc = Double.parseDouble(result.getString("rep"));
                    ipc1 = Double.parseDouble(result.getString("reg1"));
                    ipc2 = Double.parseDouble(result.getString("reg2"));
                    ipc3 = Double.parseDouble(result.getString("reg3"));
                    ipc4 = Double.parseDouble(result.getString("reg4"));
                    ipc5 = Double.parseDouble(result.getString("reg5"));
                    ipc6 = Double.parseDouble(result.getString("reg6"));
                    ipc7 = Double.parseDouble(result.getString("reg7"));
                    ipc8 = Double.parseDouble(result.getString("reg8"));

                    lista.add(getNumero(Math.round(((ipc/ipcAnterior - 1) * 100)*100.0)/100.0) + "");
                    lista1.add(getNumero(Math.round(((ipc1/ipcAnterior1 - 1) * 100)*100.0)/100.0) + "");
                    lista2.add(getNumero(Math.round(((ipc2/ipcAnterior2 - 1) * 100)*100.0)/100.0) + "");
                    lista3.add(getNumero(Math.round(((ipc3/ipcAnterior3 - 1) * 100)*100.0)/100.0) + "");
                    lista4.add(getNumero(Math.round(((ipc4/ipcAnterior4 - 1) * 100)*100.0)/100.0) + "");
                    lista5.add(getNumero(Math.round(((ipc5/ipcAnterior5 - 1) * 100)*100.0)/100.0) + "");
                    lista6.add(getNumero(Math.round(((ipc6/ipcAnterior6 - 1) * 100)*100.0)/100.0) + "");
                    lista7.add(getNumero(Math.round(((ipc7/ipcAnterior7 - 1) * 100)*100.0)/100.0) + "");
                    lista8.add(getNumero(Math.round(((ipc8/ipcAnterior8 - 1) * 100)*100.0)/100.0) + "");
                 }
            }
            result.close();
            preStatement.close();
            texto += "República&" + lista.get(0) + "&" + lista.get(1) + "&" + lista.get(2) + "&" + lista.get(3) + "&"
                     + lista.get(4) + "&" + lista.get(5) + "&" + lista.get(6) + "&" + lista.get(7) + "&"
                     + lista.get(8) + "&" + lista.get(9) + "&" + lista.get(10) + "&" + lista.get(11) + "&" + lista.get(12) + "\\\\\n";
            texto += "Región I&" + lista1.get(0) + "&" + lista1.get(1) + "&" + lista1.get(2) + "&" + lista1.get(3) + "&"
                     + lista1.get(4) + "&" + lista1.get(5) + "&" + lista1.get(6) + "&" + lista1.get(7) + "&"
                     + lista1.get(8) + "&" + lista1.get(9) + "&" + lista1.get(10) + "&" + lista1.get(11) + "&" + lista1.get(12) + "\\\\\n";
            texto += "Región II&" + lista2.get(0) + "&" + lista2.get(1) + "&" + lista2.get(2) + "&" + lista2.get(3) + "&"
                     + lista2.get(4) + "&" + lista2.get(5) + "&" + lista2.get(6) + "&" + lista2.get(7) + "&"
                     + lista2.get(8) + "&" + lista2.get(9) + "&" + lista2.get(10) + "&" + lista2.get(11) + "&" + lista2.get(12) + "\\\\\n";
            texto += "Región III&" + lista3.get(0) + "&" + lista3.get(1) + "&" + lista3.get(2) + "&" + lista3.get(3) + "&"
                     + lista3.get(4) + "&" + lista3.get(5) + "&" + lista3.get(6) + "&" + lista3.get(7) + "&"
                     + lista3.get(8) + "&" + lista3.get(9) + "&" + lista3.get(10) + "&" + lista3.get(11) + "&" + lista3.get(12) + "\\\\\n";
            texto += "Región IV&" + lista4.get(0) + "&" + lista4.get(1) + "&" + lista4.get(2) + "&" + lista4.get(3) + "&"
                     + lista4.get(4) + "&" + lista4.get(5) + "&" + lista4.get(6) + "&" + lista4.get(7) + "&"
                     + lista4.get(8) + "&" + lista4.get(9) + "&" + lista4.get(10) + "&" + lista4.get(11) + "&" + lista4.get(12) + "\\\\\n";
            texto += "Región V&" + lista5.get(0) + "&" + lista5.get(1) + "&" + lista5.get(2) + "&" + lista5.get(3) + "&"
                     + lista5.get(4) + "&" + lista5.get(5) + "&" + lista5.get(6) + "&" + lista5.get(7) + "&"
                     + lista5.get(8) + "&" + lista5.get(9) + "&" + lista5.get(10) + "&" + lista5.get(11) + "&" + lista5.get(12) + "\\\\\n";
            texto += "Región VI&" + lista6.get(0) + "&" + lista6.get(1) + "&" + lista6.get(2) + "&" + lista6.get(3) + "&"
                     + lista6.get(4) + "&" + lista6.get(5) + "&" + lista6.get(6) + "&" + lista6.get(7) + "&"
                     + lista6.get(8) + "&" + lista6.get(9) + "&" + lista6.get(10) + "&" + lista6.get(11) + "&" + lista6.get(12) + "\\\\\n";
            texto += "Región VII&" + lista7.get(0) + "&" + lista7.get(1) + "&" + lista7.get(2) + "&" + lista7.get(3) + "&"
                     + lista7.get(4) + "&" + lista7.get(5) + "&" + lista7.get(6) + "&" + lista7.get(7) + "&"
                     + lista7.get(8) + "&" + lista7.get(9) + "&" + lista7.get(10) + "&" + lista7.get(11) + "&" + lista7.get(12) + "\\\\\n";
            texto += "Región VIII&" + lista8.get(0) + "&" + lista8.get(1) + "&" + lista8.get(2) + "&" + lista8.get(3) + "&"
                     + lista8.get(4) + "&" + lista8.get(5) + "&" + lista8.get(6) + "&" + lista8.get(7) + "&"
                     + lista8.get(8) + "&" + lista8.get(9) + "&" + lista8.get(10) + "&" + lista8.get(11) + "&" + lista8.get(12) + "\\\\\n";

        } catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }
        Double mayor = -100.0, menor = 100.0;
        String regMayor = "", divisionMayor = "";
        String regMenor = "", divisionMenor = "";
        for(int i=1; i<=12; i++){
            Double val = Double.parseDouble(lista.get(i));
            if(val > mayor){
                mayor = val;
                regMayor = getRegionCadena(0);
                divisionMayor = getDivision(i);
            }
            if(val < menor){
                menor = val;
                regMenor = getRegionCadena(0);
                divisionMenor = getDivision(i);
            }
            val = Double.parseDouble(lista1.get(i));
            if(val > mayor){
                mayor = val;
                regMayor = getRegionCadena(1);
                divisionMayor = getDivision(i);
            }
            if(val < menor){
                menor = val;
                regMenor = getRegionCadena(1);
                divisionMenor = getDivision(i);
            }
            val = Double.parseDouble(lista2.get(i));
            if(val > mayor){
                mayor = val;
                regMayor = getRegionCadena(2);
                divisionMayor = getDivision(i);
            }
            if(val < menor){
                menor = val;
                regMenor = getRegionCadena(2);
                divisionMenor = getDivision(i);
            }
            val = Double.parseDouble(lista3.get(i));
            if(val > mayor){
                mayor = val;
                regMayor = getRegionCadena(3);
                divisionMayor = getDivision(i);
            }
            if(val < menor){
                menor = val;
                regMenor = getRegionCadena(3);
                divisionMenor = getDivision(i);
            }
            val = Double.parseDouble(lista4.get(i));
            if(val > mayor){
                mayor = val;
                regMayor = getRegionCadena(4);
                divisionMayor = getDivision(i);
            }
            if(val < menor){
                menor = val;
                regMenor = getRegionCadena(4);
                divisionMenor = getDivision(i);
            }
            val = Double.parseDouble(lista5.get(i));
            if(val > mayor){
                mayor = val;
                regMayor = getRegionCadena(5);
                divisionMayor = getDivision(i);
            }
            if(val < menor){
                menor = val;
                regMenor = getRegionCadena(5);
                divisionMenor = getDivision(i);
            }
            val = Double.parseDouble(lista6.get(i));
            if(val > mayor){
                mayor = val;
                regMayor = getRegionCadena(6);
                divisionMayor = getDivision(i);
            }
            if(val < menor){
                menor = val;
                regMenor = getRegionCadena(6);
                divisionMenor = getDivision(i);
            }
            val = Double.parseDouble(lista7.get(i));
            if(val > mayor){
                mayor = val;
                regMayor = getRegionCadena(7);
                divisionMayor = getDivision(i);
            }
            if(val < menor){
                menor = val;
                regMenor = getRegionCadena(7);
                divisionMenor = getDivision(i);
            }
            val = Double.parseDouble(lista8.get(i));
            if(val > mayor){
                mayor = val;
                regMayor = getRegionCadena(8);
                divisionMayor = getDivision(i);
            }
            if(val < menor){
                menor = val;
                regMenor = getRegionCadena(8);
                divisionMenor = getDivision(i);
            }
        }
        String descripcion = "Analizando tanto a nivel región como por división de gasto, para " + getFecha() + ", la mayor variación mensual se observó en la división de "
                + divisionMayor + " en la " + regMayor + " que fue de " + getNumeroDosDecimales(mayor) + "\\%. Por su parte, la reducción mas significativa, se registró en la división de "
                + divisionMenor + " en la " + regMenor + " con " + getNumeroDosDecimales((menor * -1)) + "\\%.";
        String[] resultado = {texto, descripcion};
        return resultado;
    }

    private String getDivision(int n){
        if(n==1) return "alimentos";
        else if(n==2) return "bebidas alcohólicas";
        else if(n==3) return "vestuario";
        else if(n==4) return "vivienda";
        else if(n==5) return "muebles";
        else if(n==6) return "salud";
        else if(n==7) return "transporte";
        else if(n==8) return "comunicaciones";
        else if(n==9) return "recreación";
        else if(n==10) return "educación";
        else if(n==11) return "restaurantes";
        else if(n==12) return "bienes diversos";
        return "";
    }

    private String getRegionCadena(int n){
        if(n==0) return "república";
        else if(n==1) return "región I";
        else if(n==2) return "región II";
        else if(n==3) return "región III";
        else if(n==4) return "región IV";
        else if(n==5) return "región V";
        else if(n==6) return "región VI";
        else if(n==7) return "región VII";
        else if(n==8) return "región VIII";
        return "";
    }

    private String[] csv2_26(){
        String texto = "";
            String sql = "SELECT i.rep, i.reg1, i.reg2, i.reg3, i.reg4, i.reg5, i.reg6, i.reg7, i.reg8"
                    + " FROM FECHA f, IPC i"
                    + " WHERE i.fecha = f.fecha"
                    + " AND i.codigo >= 0"
                    + " AND i.codigo < 13"
                    + " AND ((f.anio = " + anioActual
                    + " AND f.mes = " + mesActual + ")"
                    + " OR (f.anio = " + (anioActual - 1)
                    + " AND f.mes = " + (mesActual) + "))"
                    + " ORDER BY i.codigo, f.anio, f.mes";

            System.out.println("2_26: " + sql);
            PreparedStatement preStatement;
            List<String> lista = new ArrayList<>();
            List<String> lista1 = new ArrayList<>();
            List<String> lista2 = new ArrayList<>();
            List<String> lista3 = new ArrayList<>();
            List<String> lista4 = new ArrayList<>();
            List<String> lista5 = new ArrayList<>();
            List<String> lista6 = new ArrayList<>();
            List<String> lista7 = new ArrayList<>();
            List<String> lista8 = new ArrayList<>();

        try {
            preStatement = conn.prepareStatement(sql);
            ResultSet result = preStatement.executeQuery();
            Double ipcAnterior = 0.0;
            Double ipcAnterior1 = 0.0;
            Double ipcAnterior2 = 0.0;
            Double ipcAnterior3 = 0.0;
            Double ipcAnterior4 = 0.0;
            Double ipcAnterior5 = 0.0;
            Double ipcAnterior6 = 0.0;
            Double ipcAnterior7 = 0.0;
            Double ipcAnterior8 = 0.0;
            Double ipc = 0.0;
            Double ipc1 = 0.0;
            Double ipc2 = 0.0;
            Double ipc3 = 0.0;
            Double ipc4 = 0.0;
            Double ipc5 = 0.0;
            Double ipc6 = 0.0;
            Double ipc7 = 0.0;
            Double ipc8 = 0.0;


            while(result.next()){

            	ipcAnterior = Double.parseDouble(result.getString("rep"));
                ipcAnterior1 = Double.parseDouble(result.getString("reg1"));
                ipcAnterior2 = Double.parseDouble(result.getString("reg2"));
                ipcAnterior3 = Double.parseDouble(result.getString("reg3"));
                ipcAnterior4 = Double.parseDouble(result.getString("reg4"));
                ipcAnterior5 = Double.parseDouble(result.getString("reg5"));
                ipcAnterior6 = Double.parseDouble(result.getString("reg6"));
                ipcAnterior7 = Double.parseDouble(result.getString("reg7"));
                ipcAnterior8 = Double.parseDouble(result.getString("reg8"));
                if(result.next()){
                    ipc = Double.parseDouble(result.getString("rep"));
                    ipc1 = Double.parseDouble(result.getString("reg1"));
                    ipc2 = Double.parseDouble(result.getString("reg2"));
                    ipc3 = Double.parseDouble(result.getString("reg3"));
                    ipc4 = Double.parseDouble(result.getString("reg4"));
                    ipc5 = Double.parseDouble(result.getString("reg5"));
                    ipc6 = Double.parseDouble(result.getString("reg6"));
                    ipc7 = Double.parseDouble(result.getString("reg7"));
                    ipc8 = Double.parseDouble(result.getString("reg8"));

                    lista.add(getNumero(Math.round(((ipc/ipcAnterior - 1) * 100)*100.0)/100.0) + "");
                    lista1.add(getNumero(Math.round(((ipc1/ipcAnterior1 - 1) * 100)*100.0)/100.0) + "");
                    lista2.add(getNumero(Math.round(((ipc2/ipcAnterior2 - 1) * 100)*100.0)/100.0) + "");
                    lista3.add(getNumero(Math.round(((ipc3/ipcAnterior3 - 1) * 100)*100.0)/100.0) + "");
                    lista4.add(getNumero(Math.round(((ipc4/ipcAnterior4 - 1) * 100)*100.0)/100.0) + "");
                    lista5.add(getNumero(Math.round(((ipc5/ipcAnterior5 - 1) * 100)*100.0)/100.0) + "");
                    lista6.add(getNumero(Math.round(((ipc6/ipcAnterior6 - 1) * 100)*100.0)/100.0) + "");
                    lista7.add(getNumero(Math.round(((ipc7/ipcAnterior7 - 1) * 100)*100.0)/100.0) + "");
                    lista8.add(getNumero(Math.round(((ipc8/ipcAnterior8 - 1) * 100)*100.0)/100.0) + "");
                 }
            }
            result.close();
            preStatement.close();
            texto += "República&" + lista.get(0) + "&" + lista.get(1) + "&" + lista.get(2) + "&" + lista.get(3) + "&"
                     + lista.get(4) + "&" + lista.get(5) + "&" + lista.get(6) + "&" + lista.get(7) + "&"
                     + lista.get(8) + "&" + lista.get(9) + "&" + lista.get(10) + "&" + lista.get(11) + "&" + lista.get(12) + "\\\\\n";
            texto += "Región I&" + lista1.get(0) + "&" + lista1.get(1) + "&" + lista1.get(2) + "&" + lista1.get(3) + "&"
                     + lista1.get(4) + "&" + lista1.get(5) + "&" + lista1.get(6) + "&" + lista1.get(7) + "&"
                     + lista1.get(8) + "&" + lista1.get(9) + "&" + lista1.get(10) + "&" + lista1.get(11) + "&" + lista1.get(12) + "\\\\\n";
            texto += "Región II&" + lista2.get(0) + "&" + lista2.get(1) + "&" + lista2.get(2) + "&" + lista2.get(3) + "&"
                     + lista2.get(4) + "&" + lista2.get(5) + "&" + lista2.get(6) + "&" + lista2.get(7) + "&"
                     + lista2.get(8) + "&" + lista2.get(9) + "&" + lista2.get(10) + "&" + lista2.get(11) + "&" + lista2.get(12) + "\\\\\n";
            texto += "Región III&" + lista3.get(0) + "&" + lista3.get(1) + "&" + lista3.get(2) + "&" + lista3.get(3) + "&"
                     + lista3.get(4) + "&" + lista3.get(5) + "&" + lista3.get(6) + "&" + lista3.get(7) + "&"
                     + lista3.get(8) + "&" + lista3.get(9) + "&" + lista3.get(10) + "&" + lista3.get(11) + "&" + lista3.get(12) + "\\\\\n";
            texto += "Región IV&" + lista4.get(0) + "&" + lista4.get(1) + "&" + lista4.get(2) + "&" + lista4.get(3) + "&"
                     + lista4.get(4) + "&" + lista4.get(5) + "&" + lista4.get(6) + "&" + lista4.get(7) + "&"
                     + lista4.get(8) + "&" + lista4.get(9) + "&" + lista4.get(10) + "&" + lista4.get(11) + "&" + lista4.get(12) + "\\\\\n";
            texto += "Región V&" + lista5.get(0) + "&" + lista5.get(1) + "&" + lista5.get(2) + "&" + lista5.get(3) + "&"
                     + lista5.get(4) + "&" + lista5.get(5) + "&" + lista5.get(6) + "&" + lista5.get(7) + "&"
                     + lista5.get(8) + "&" + lista5.get(9) + "&" + lista5.get(10) + "&" + lista5.get(11) + "&" + lista5.get(12) + "\\\\\n";
            texto += "Región VI&" + lista6.get(0) + "&" + lista6.get(1) + "&" + lista6.get(2) + "&" + lista6.get(3) + "&"
                     + lista6.get(4) + "&" + lista6.get(5) + "&" + lista6.get(6) + "&" + lista6.get(7) + "&"
                     + lista6.get(8) + "&" + lista6.get(9) + "&" + lista6.get(10) + "&" + lista6.get(11) + "&" + lista6.get(12) + "\\\\\n";
            texto += "Región VII&" + lista7.get(0) + "&" + lista7.get(1) + "&" + lista7.get(2) + "&" + lista7.get(3) + "&"
                     + lista7.get(4) + "&" + lista7.get(5) + "&" + lista7.get(6) + "&" + lista7.get(7) + "&"
                     + lista7.get(8) + "&" + lista7.get(9) + "&" + lista7.get(10) + "&" + lista7.get(11) + "&" + lista7.get(12) + "\\\\\n";
            texto += "Región VIII&" + lista8.get(0) + "&" + lista8.get(1) + "&" + lista8.get(2) + "&" + lista8.get(3) + "&"
                     + lista8.get(4) + "&" + lista8.get(5) + "&" + lista8.get(6) + "&" + lista8.get(7) + "&"
                     + lista8.get(8) + "&" + lista8.get(9) + "&" + lista8.get(10) + "&" + lista8.get(11) + "&" + lista8.get(12) + "\\\\\n";

        } catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }
        Double mayor = -100.0, menor = 100.0;
        String regMayor = "", divisionMayor = "";
        String regMenor = "", divisionMenor = "";
        for(int i=1; i<=12; i++){
            Double val = Double.parseDouble(lista.get(i));
            if(val > mayor){
                mayor = val;
                regMayor = getRegionCadena(0);
                divisionMayor = getDivision(i);
            }
            if(val < menor){
                menor = val;
                regMenor = getRegionCadena(0);
                divisionMenor = getDivision(i);
            }
            val = Double.parseDouble(lista1.get(i));
            if(val > mayor){
                mayor = val;
                regMayor = getRegionCadena(1);
                divisionMayor = getDivision(i);
            }
            if(val < menor){
                menor = val;
                regMenor = getRegionCadena(1);
                divisionMenor = getDivision(i);
            }
            val = Double.parseDouble(lista2.get(i));
            if(val > mayor){
                mayor = val;
                regMayor = getRegionCadena(2);
                divisionMayor = getDivision(i);
            }
            if(val < menor){
                menor = val;
                regMenor = getRegionCadena(2);
                divisionMenor = getDivision(i);
            }
            val = Double.parseDouble(lista3.get(i));
            if(val > mayor){
                mayor = val;
                regMayor = getRegionCadena(3);
                divisionMayor = getDivision(i);
            }
            if(val < menor){
                menor = val;
                regMenor = getRegionCadena(3);
                divisionMenor = getDivision(i);
            }
            val = Double.parseDouble(lista4.get(i));
            if(val > mayor){
                mayor = val;
                regMayor = getRegionCadena(4);
                divisionMayor = getDivision(i);
            }
            if(val < menor){
                menor = val;
                regMenor = getRegionCadena(4);
                divisionMenor = getDivision(i);
            }
            val = Double.parseDouble(lista5.get(i));
            if(val > mayor){
                mayor = val;
                regMayor = getRegionCadena(5);
                divisionMayor = getDivision(i);
            }
            if(val < menor){
                menor = val;
                regMenor = getRegionCadena(5);
                divisionMenor = getDivision(i);
            }
            val = Double.parseDouble(lista6.get(i));
            if(val > mayor){
                mayor = val;
                regMayor = getRegionCadena(6);
                divisionMayor = getDivision(i);
            }
            if(val < menor){
                menor = val;
                regMenor = getRegionCadena(6);
                divisionMenor = getDivision(i);
            }
            val = Double.parseDouble(lista7.get(i));
            if(val > mayor){
                mayor = val;
                regMayor = getRegionCadena(7);
                divisionMayor = getDivision(i);
            }
            if(val < menor){
                menor = val;
                regMenor = getRegionCadena(7);
                divisionMenor = getDivision(i);
            }
            val = Double.parseDouble(lista8.get(i));
            if(val > mayor){
                mayor = val;
                regMayor = getRegionCadena(8);
                divisionMayor = getDivision(i);
            }
            if(val < menor){
                menor = val;
                regMenor = getRegionCadena(8);
                divisionMenor = getDivision(i);
            }
        }
        String descripcion = "Tanto a nivel región como división de gasto, en el mes de " + getFecha() + ", resalta como la mayor variación interanual la división de "
                + divisionMayor + " en la " + regMayor + " con " + getNumeroDosDecimales(mayor) + "\\%. Por su parte, la reducción mas significativa, se registra en la división de "
                + divisionMenor + " de la " + regMenor + " que fue de " + getNumeroDosDecimales((menor * -1)) + "\\%.";
        String[] resultado = {texto, descripcion};
        return resultado;
     }

    private String[] csvr_04(String region){
        String texto = "";
            String sql = "SELECT i." + region
                    + " FROM FECHA f, IPC i"
                    + " WHERE i.fecha = f.fecha"
                    + " AND i.codigo >= 0"
                    + " AND i.codigo < 13";
            if(esEnero){
                sql += " AND ((f.anio = " + anioActual
                    + " AND f.mes <= " + mesActual + ")"
                    + " OR (f.anio = " + (anioActual - 1)
                    + " AND f.mes >= " + mesActual + ")"
                    + " OR (f.anio = " + (anioActual - 2)
                    + " AND f.mes = 12))"
                    + " ORDER BY i.codigo, f.anio, f.mes";
            }
            else{
                sql += " AND ((f.anio = " + anioActual
                    + " AND f.mes <= " + mesActual + ")"
                    + " OR (f.anio = " + (anioActual - 1)
                    + " AND f.mes >= " + (mesActual - 1) + "))"
                    + " ORDER BY i.codigo, f.anio, f.mes";
            }
            //System.out.println("r_04: " + sql);
            PreparedStatement preStatement;
            Double mayor = -100.0, menor = 100.0, valor = 0.0;
            String mesMayor = "", mesMenor = "", divisionMayor = "", divisionMenor = "";
            int anioMayor = anioActual, anioMenor = anioActual;

        try {
            preStatement = conn.prepareStatement(sql);
            ResultSet result = preStatement.executeQuery();
            Double ipcAnterior = 0.0;
            Double ipc = 0.0;

            List<String> lista = new ArrayList<>();
            for(int j=0; j<13; j++){
                if(result.next()){
                    ipcAnterior = Double.parseDouble(result.getString(region));
                }
                for(int i=0; i<13; i++){
                     if(result.next()){
                         ipc = Double.parseDouble(result.getString(region));
                         valor = ((ipc/ipcAnterior - 1) * 100);
                         lista.add(getNumero(Math.round((valor)*100.0)/100.0) + "");
                         //System.out.println(i + "," + j + ": " + valor);
                         if(valor > mayor){
                             mayor = valor;
                             int nMes = mesActual + i;
                             if(nMes > 12){
                                 nMes = nMes - 12;
                                 anioMayor = anioActual;
                             }
                             else anioMayor = anioActual - 1;
                             mesMayor = getMesCadenaMin(nMes);
                             divisionMayor = getDivision(j);
                         }
                         else if(valor < menor)
                         {
                             menor = valor;
                             int nMes = mesActual + i;
                             if(nMes > 12){
                                 nMes = nMes - 12;
                                 anioMenor = anioActual;
                             }
                             else anioMenor = anioActual - 1;
                             mesMenor = getMesCadenaMin(nMes);
                             divisionMenor = getDivision(j);
                         }
                     }
                }
            }

            int mes = mesActual;
            Integer anio = anioActual - 1;
            for(int j=0; j<13; j++){
                texto += getAnioMes(anio, mes) + "&" + lista.get(j) + "&" + lista.get(j+12+1) + "&" + lista.get(j+12*2+2) + "&" + lista.get(j+12*3+3) + "&"
                     + lista.get(j+12*4+4) + "&" + lista.get(j+12*5+5) + "&" + lista.get(j+12*6+6) + "&" + lista.get(j+12*7+7) + "&"
                     + lista.get(j+12*8+8) + "&" + lista.get(j+12*9+9) + "&" + lista.get(j+12*10+10) + "&" + lista.get(j+12*11+11) + "&" + lista.get(j+12*12+12) + "\\\\\n";
                mes++;
                if(mes == 13){
                    mes = 1;
                    anio++;
                }
                /*System.out.println("");
                for(int i=0; i<13; i++){
                     System.out.print(lista.get(j*12 + i + j) + " ");
                }*/
            }


            result.close();
            preStatement.close();


        } catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }
        String descripcion = "En la " + getRegionCadena(region) + ", tanto a nivel de división de gasto como de mes, resalta como la mayor variación mensual la división de "
                + divisionMayor + " en el mes de " + mesMayor + " de " + anioMayor + " que fue de " + getNumeroDosDecimales((Math.round((mayor)*100.0)/100.0)) + "\\%. Por su parte, la reducción mas significativa, se registra en la división de "
                + divisionMenor + " en el mes de " + mesMenor + " de " + anioMenor + " que fue de " + getNumeroDosDecimales((Math.round((menor)*100.0)/100.0)) + "\\%.";
        String[] resultado = {texto, descripcion};
        return resultado;
     }

    private String getAnioMes(Integer anio, int mes){
        String res = "";
        res += getMesAbreviacion(mes) + "-" + anio.toString().substring(2,4);
        return res;
    }

    private String[] csvr_23(String region){
        String texto = "";
            String sql = "SELECT i." + region
                    + " FROM FECHA f, IPC i"
                    + " WHERE i.fecha = f.fecha"
                    + " AND i.codigo >= 0"
                    + " AND i.codigo < 13"
                    + " AND ((f.anio = " + anioActual
                    + " AND f.mes <= " + mesActual + ")"
                    + " OR (f.anio = " + (anioActual - 1)
                    + ")"
                    + " OR (f.anio = " + (anioActual - 2)
                    + " AND f.mes >= " + mesActual + "))"
                    + " ORDER BY i.codigo, f.anio, f.mes";

            System.out.println("r_23: " + sql);
            PreparedStatement preStatement;
            Double mayor = -100.0, menor = 100.0, valor = 0.0;
            String mesMayor = "", mesMenor = "", divisionMayor = "", divisionMenor = "";
            int anioMayor = anioActual, anioMenor = anioActual;
        try {
            preStatement = conn.prepareStatement(sql);
            ResultSet result = preStatement.executeQuery();
            Double ipcAnterior = 0.0;
            Double ipc = 0.0;

            List<String> lista = new ArrayList<>();
            List<Double> aux = new ArrayList<>();

            while(result.next()){
                aux.add(Double.parseDouble(result.getString(region)));
            }

            for(int j=0; j<13; j++){
                for(int i=12; i<25; i++){
                    ipcAnterior = aux.get((i-12)+(25*j));
                    ipc = aux.get(i+(25*j));
                    valor = ((ipc/ipcAnterior - 1) * 100);
                    lista.add(getNumero(Math.round(valor*100.0)/100.0) + "");
                         //System.out.println(i + "," + j + ": " + valor + " mesMayor: " + mesMayor);
                         if(valor > mayor){
                             mayor = valor;
                             int nMes = mesActual + i - 12;
                             if(nMes > 12){
                                 nMes = nMes - 12;
                                 anioMayor = anioActual;
                             }
                             else anioMayor = anioActual - 1;
                             mesMayor = getMesCadenaMin(nMes);
                             divisionMayor = getDivision(j);
                         }
                         else if(valor < menor)
                         {
                             menor = valor;
                             int nMes = mesActual + i - 12;
                             if(nMes > 12){
                                 nMes = nMes - 12;
                                 anioMenor = anioActual;
                             }
                             else anioMenor = anioActual - 1;
                             mesMenor = getMesCadenaMin(nMes);
                             divisionMenor = getDivision(j);
                         }
                }
            }

            int mes = mesActual;
            Integer anio = anioActual - 1;
            for(int j=0; j<13; j++){
                texto += getAnioMes(anio, mes) + "&" + lista.get(j) + "&" + lista.get(j+12+1) + "&" + lista.get(j+12*2+2) + "&" + lista.get(j+12*3+3) + "&"
                     + lista.get(j+12*4+4) + "&" + lista.get(j+12*5+5) + "&" + lista.get(j+12*6+6) + "&" + lista.get(j+12*7+7) + "&"
                     + lista.get(j+12*8+8) + "&" + lista.get(j+12*9+9) + "&" + lista.get(j+12*10+10) + "&" + lista.get(j+12*11+11) + "&" + lista.get(j+12*12+12) + "\\\\\n";
                mes++;
                if(mes == 13){
                    mes = 1;
                    anio++;
                }
                /*System.out.println("");
                for(int i=0; i<13; i++){
                     System.out.print(lista.get(j*12 + i + j) + " ");
                }*/
            }


            result.close();
            preStatement.close();


        } catch (SQLException ex) {
            Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
        }
        String descripcion = "En la " + getRegionCadena(region) + ", tanto a nivel de división de gasto como de mes, resalta como la mayor variación interanual la división de "
                + divisionMayor + " en el mes de " + mesMayor + " de " + anioMayor + " que fue de " + getNumeroDosDecimales((Math.round((mayor)*100.0)/100.0)) + "\\%. Por su parte, la reducción mas significativa, se registra en la división de "
                + divisionMenor + " en el mes de " + mesMenor + " de " + anioMenor + " que fue de " + getNumeroDosDecimales((Math.round((menor)*100.0)/100.0)) + "\\%.";
        String[] resultado = {texto, descripcion};
        return resultado;
     }
    
    private String notaMapas(){
        return "\\footnote{Guatemala se encuentra organizada en 8 regiones;"
                + " La región I o Metropolitana está conformada"
                + " por el departamento de Guatemala, la región II o Norte"
                + " por Alta Verapaz y Baja Verapaz,"
                + " la región III o Nororiental por Chiquimula, El Progreso, Izabal y Zacapa,"
                + " la región IV o Suroriental por Jutiapa, Jalapa y Santa Rosa,"
                + " la región V o Central por Chimaltenango, Sacatepéquez y Escuintla,"
                + " la región VI o Suroccidental por Quetzaltenango, Retalhuleu, San Marcos, Suchitepéquez y Escuintla,"
                + " la región VII o Noroccidental por Huehuetenango y Quiché"
                + " y la región VIII por Petén.}";
    }
}
