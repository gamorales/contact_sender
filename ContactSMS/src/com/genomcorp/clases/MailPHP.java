package com.genomcorp.clases;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.R;

//import android.os.Message;

public class MailPHP {
    public void datosEnviar(String subject, String body, String sender, String recipients){    	
    	//Utilizamos la clase Httpclient para conectar
        HttpClient httpclient = new DefaultHttpClient();   
        //Utilizamos la HttpPost para enviar los datos al servidor mail        
        HttpPost httppost = new HttpPost("http://www.genom-corp.com/quinta/index.php");      
        try {         
        	//Añadimos los datos a enviar en este caso solo uno
        	//que le llamamos de nombre 'a'
        	//La segunda linea podría repetirse tantas veces como queramos
        	//siempre cambiando el nombre ('a')
        	List<NameValuePair> postValues = new ArrayList<NameValuePair>(2);   
        	postValues.add(new BasicNameValuePair("a", subject));
        	postValues.add(new BasicNameValuePair("b", body));
        	postValues.add(new BasicNameValuePair("d", recipients));
        	//Encapsulamos
        	httppost.setEntity(new UrlEncodedFormEntity(postValues));
        	//Lanzamos la petición
        	HttpResponse respuesta = httpclient.execute(httppost);
        	//Conectamos para recibir datos de respuesta
        	HttpEntity entity = respuesta.getEntity();
        	//Creamos el InputStream como su propio nombre indica
        	InputStream is = entity.getContent();
        	//Limpiamos el codigo obtenido atraves de la funcion
        	//StreamToString explicada más abajo
        	String resultado= StreamToString(is);
        	
        	//Enviamos el resultado LIMPIO al Handler para mostrarlo
        	/*Message sms = new Message();
        	sms.obj = resultado;
        	puente.sendMessage(sms);*/
        }catch (IOException e) {         
            //TODO Auto-generated catch block
        } 
    }
  //Funcion para 'limpiar' el codigo recibido
    public String StreamToString(InputStream is) {
        //Creamos el Buffer
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
        	//Bucle para leer todas las líneas
        	//En este ejemplo al ser solo 1 la respuesta
        	//Pues no haría falta
        	while ((line = reader.readLine()) != null) {
        		sb.append(line + "\n");
        	}
        } catch (IOException e) {
        	e.printStackTrace();
        } finally {
        	try {
        		is.close();
        	} catch (IOException e) {
        		e.printStackTrace();
        	}
        }
        //retornamos el codigo límpio
        return sb.toString();
    }
    	/*//subject = subject.replace(" ", "%20");
    	//body = body.replace(" ", "%20");    	
        BufferedReader in=null;
        String urlDatos = "http://www.genom-corp.com/quinta/index.php?a="+subject+"&b="+body+
        		                                       "&d="+recipients+"&sender="+sender;
        //System.out.println(urlDatos);
        try {
          URL url = new URL(urlDatos);
          in = new BufferedReader(new InputStreamReader(url.openStream()));
          String line=null;
          while ((line=in.readLine()) != null){
           // System.out.println(line);
          }
        }
        catch (MalformedURLException ex) {
          //System.err.println(ex);
        }
        catch (FileNotFoundException ex) {
          //System.err.println("Failed to open stream to URL: "+ex);
        }
        catch (IOException ex) {
          //System.err.println("Error reading URL content: "+ex);
        }
        if (in != null)
          try {in.close();} catch (IOException ex) {}
          
      }*/
}
