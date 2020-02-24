package com.genomcorp.contactsms;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.*;

public class ConfigurationActivity2 extends Activity {
	CheckBox chkSMS, chkGPS, chkMail;
	Button btnGuardar, btnCancel;
	EditText txtEMailEnvios, txtPassword;
	ListView lstContactos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        //setTitle(R.string.correo);
        
        // Look up the AdView as a resource and load a request.
        AdView adView = (AdView)this.findViewById(R.id.Publicidad);
        adView.loadAd(new AdRequest());
        
        chkSMS = (CheckBox)findViewById(R.id.chkSMS);
        //chkGPS = (CheckBox)findViewById(R.id.chkGPS);
        chkMail = (CheckBox)findViewById(R.id.chkMail);
        txtEMailEnvios = (EditText)findViewById(R.id.txtEMailEnvios);
        txtPassword = (EditText)findViewById(R.id.txtPassword);
        btnGuardar = (Button)findViewById(R.id.btnGuardar);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        
        // Establecemos el resultado por defecto (si se pulsa el botón 'Atrás'
        // del teléfono será éste el resultado devuelto).
        setResult(RESULT_CANCELED);
        
        btnCancel.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				finish();				
			}
        	
        });
        
        chkMail.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked==true){
					txtEMailEnvios.setEnabled(true);
				} else {
					txtEMailEnvios.setEnabled(false);
				}
			}
        	
        });
        

        final SharedPreferences prefs = getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
		// Si no hay valores guardados pasaremos unos por defecto a los views
		boolean boolSMS  = prefs.getBoolean("SMS", true);		
		boolean boolGPS  = prefs.getBoolean("GPS", true);		
		boolean boolMail  = prefs.getBoolean("Mail", true);
		String EMailEnvios = prefs.getString("EmailEnvios", "info@gmail.com");
		final String PasswordEnvios = prefs.getString("PasswordEnvios", "lerolero");
		
		/*if (!PasswordEnvios.equals("lerolero")){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			//Obtenemos el layout inflater
			LayoutInflater inflater = this.getLayoutInflater();
			// Cargamos el layout personalizado para el dialogo
			// Pasamos null como padre pues este layout ira en el dialogo
			View layout = inflater.inflate(R.layout.dialog_password, null);
			//Definimos el EditText para poder acceder a el posteriormente
			final EditText txtPassDialog = ((EditText) layout.findViewById(R.id.txtPassDialog));
			ImageView image = (ImageView) layout.findViewById(R.id.ivGenomCorp);
			//image.setImageResource(R.drawable.genomcorp);
			//Substituimos la vista por la que acabamos de cargar.
			builder.setView(layout);
			builder.setTitle(R.string.acceso);
			builder.setMessage(R.string.digitar);
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (txtPassDialog.getText().toString().equals(PasswordEnvios.toString())){
						dialog.cancel();
					} else {
						finish();
						Toast.makeText(getApplicationContext(), 
			    			       "Password Erroneo\nSe cierra la aplicación por seguridad", 
			    			       Toast.LENGTH_LONG).show();	
					}
				}
			});
			builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			   public void onClick(DialogInterface dialog, int which) {
			      //Acciones a realizar al pulsar el boton Cancelar
			      finish();
			   }
			});
			
			AlertDialog dialogo = builder.create();
			dialogo.show();
		}*/
		
		if (boolSMS==true){
			chkSMS.setChecked(true);
		}
		/*if (boolGPS==true){	
			chkGPS.setChecked(true);
		}*/
		if (boolMail==true){	
			chkMail.setChecked(true);
			txtEMailEnvios.setEnabled(true);
		} else {
			txtEMailEnvios.setEnabled(false);
		}
		txtEMailEnvios.setText(EMailEnvios);
		txtPassword.setText(PasswordEnvios);
		
        btnGuardar.setOnClickListener(new OnClickListener(){

			@SuppressLint("ShowToast")
			public void onClick(View v) {
				// Se guarda todo como preferencias dentro del sistema de la App				
				SharedPreferences.Editor editor = prefs.edit();
				if (chkSMS.isChecked()){
					editor.putBoolean("SMS", true);	
				} else {
					editor.putBoolean("SMS", false);
				}
				/*if (chkGPS.isChecked()){
					editor.putBoolean("GPS", true);	
				} else {
					editor.putBoolean("GPS", false);
				}*/
				if (chkMail.isChecked()){
					editor.putBoolean("Mail", true);
				} else {
					editor.putString("email", "");
					editor.putBoolean("Mail", false);
				}
			    editor.putString("EmailEnvios", txtEMailEnvios.getText().toString());
			    editor.putString("PasswordEnvios", txtPassword.getText().toString());
				editor.commit();
				// Mostramos el mensaje
		    	Toast.makeText(getApplicationContext(), 
		    			       R.string.saved, 
		    			       Toast.LENGTH_LONG).show();		    	
			}
        	
        });
    }
    
    /*
     * Creando un menú de opciones a partir de variables
     */
    private static final int MNU_OPC1 = 1;
    private static final int MNU_OPC2 = 2;
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	menu.add(Menu.NONE, MNU_OPC1, Menu.NONE, R.string.consultado).setIcon(R.drawable.ic_launcher_contacts);
    	menu.add(Menu.NONE, MNU_OPC2, Menu.NONE, R.string.acercade).setIcon(R.drawable.acerca_de);
    	
    	return true;    	
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch(item.getItemId()){
    	  case MNU_OPC1:
    		  Intent intent = new Intent(ConfigurationActivity2.this, ContactosActivity.class);
    		  startActivity(intent);
    		  return true;
    	  case MNU_OPC2:
    		  AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		  //Obtenemos el layout inflater
    		  LayoutInflater inflater = this.getLayoutInflater();
    		  // Cargamos el layout personalizado para el dialogo
    		  // Pasamos null como padre pues este layout ira en el dialogo
    		  View layout = inflater.inflate(R.layout.dialog_about, null);
    		  //Definimos el EditText para poder acceder a el posteriormente
    		  //final EditText txtPassDialog = ((EditText) layout.findViewById(R.id.txtPassDialog));
    		  ImageView image = (ImageView) layout.findViewById(R.id.ivGenomCorp);
    		  image.setImageResource(R.drawable.genomcorp);
    		  TextView text = (TextView) layout.findViewById(R.id.txtAbout);
    		  text.setText(R.string.acerca);
    		  //Substituimos la vista por la que acabamos de cargar.
    		  builder.setView(layout);
    		  builder.setTitle(R.string.app_name);
    		  builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
  				public void onClick(DialogInterface dialog, int which) {
  					dialog.cancel();
  				}
  			  });
    		  AlertDialog dialogo = builder.create();
    		  dialogo.show();
    		  return true;
          default:
        	  return super.onOptionsItemSelected(item);
    	
    	}
    }
    /*@Override
    public void onDestroy() {
      if (adView != null) {
        adView.destroy();
      }
      super.onDestroy();
    }*/

}
