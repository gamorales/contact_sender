package com.genomcorp.clases;

import java.util.Date;

import com.genomcorp.contactsms.ConfigurationActivity2;
import com.genomcorp.contactsms.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class ReceiverSMSActivity extends BroadcastReceiver {	
	private static final int NOTIF_ALERTA_ID = 1;
	
	@Override
	public void onReceive(Context context, Intent arg1) {
		// Toma el SMS Aprobado
		Bundle bundle = arg1.getExtras();
		SmsMessage [] messageSMS = null;
		
		if (bundle!=null){
			Object[] pdus = (Object[])bundle.get("pdus");
			messageSMS = new SmsMessage[pdus.length];
			messageSMS[0] = SmsMessage.createFromPdu((byte[])pdus[0]);
			
			String numero = messageSMS[0].getOriginatingAddress();
			String contacto = "";
			String mail = "";
			try{
				// Separamos el mensaje en 2, el nombre del contacto y el mail donde se debe envíar
				String[] msg = messageSMS[0].getMessageBody().toString().split(";");
								
				// Si colocaron la segunda parte (el mail) entonces se envía también un mensaje
				// a ese mail, más el que está por defecto en el SharedPreferences
				if (msg.length==3){
					String partemail = msg[2];
					contacto = msg[0].toString().toLowerCase();
					mail = partemail.toString();					
				} else {
					contacto = msg[0].toString();
			    }
				// Obtengo el password que será el segundo valor de la cadena
				String password = msg[1].toString();
				
				// Tomamos lo predefinido en el SharedPreferences para validar el password
				SharedPreferences prefs = context.getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
				String PasswordEnvios = prefs.getString("PasswordEnvios", "lerolero");
				
				if (password.equals(PasswordEnvios.toString())){
					// Con este cursor obtenemos todos los contactos del celular
					ContentResolver cr = context.getContentResolver();
			        Cursor c = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
			        
			        // Verificamos que por lo menos haya 1 para recorrer los contactos
			        if (c.getCount()>0){
			        	while(c.moveToNext()){
			        		// Extraemos el id y nombre de usuario
				        	String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
				        	String nameusu = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)).toLowerCase();
				        	
				        	// Si tienen teléfono que es lo que interesa se hará el Toast
				        	if (Integer.parseInt(c.getString(
			                        c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
				        		
				        		// Con este cursor obtenemos el número de teléfono del id asociado
				        		Cursor pCur = cr.query(
			                               ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
			                               null,
			                               ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
			                               new String[]{id}, null);
				        		
				        		// Recorremos todos los usuarios que se validen con el id
				        		while(pCur.moveToNext()){
				        			// Extraemos el teléfono del cursor
			                        String phoneNo = pCur.getString(pCur.getColumnIndex(
						        				    ContactsContract.CommonDataKinds.Phone.NUMBER));
			                        
			                        // Si el nombre de usuario es el mismo al envíado en el SMS
			                        // entonces se imprime el Toast, se envía el SMS y el mail al 
			                        // que está guardado en el SharedPreference y al envíado en el SMS
			                        if (nameusu.indexOf(contacto)!=-1){
			                    		EnviarInfo(nameusu, numero, phoneNo, mail, contacto, context);
			                        }
				        		}			      
				        		pCur.close();
				        	}			        	
			        	}
					}								

				} else {
					// Enviamos una notificación a la barra de estado
					String ns = Context.NOTIFICATION_SERVICE;
					NotificationManager notManager = (NotificationManager) context.getSystemService(ns);
					int icono = R.drawable.ic_launcher;
					CharSequence estado = context.getString(R.string.alerta);
					long hora = System.currentTimeMillis();
					@SuppressWarnings("deprecation")
					Notification notif = new Notification(icono, estado, hora);
					CharSequence titulo = context.getString(R.string.passwordAlerta);
					CharSequence descripcion = context.getString(R.string.passwordInfo).replace("***", numero).replace("---", contacto);
					
					Intent intent = new Intent(context, ConfigurationActivity2.class);
					PendingIntent contIntent = PendingIntent.getActivity(context, 0, intent, 0);
					notif.setLatestEventInfo(context, titulo, descripcion, contIntent);
					notif.flags |= Notification.FLAG_AUTO_CANCEL;
					
					notManager.notify(NOTIF_ALERTA_ID, notif);
										
				}
				
			}catch(Exception e){
				Log.d("SMS", messageSMS[0].getMessageBody().toString());
			}
		}
		
	}

	private void EnviarInfo(String usuario, String numero, String telefono, 
			                String mail, String contacto, Context context){
		/*
		 * Guardamos los registros en la db
		 */
		final BaseDatos dbCursor = new BaseDatos(context, "contactos", null, 1);
		Date fecha = new Date();
		dbCursor.insertarContacto(usuario+"/"+telefono, numero, fecha.toString());
		
		
		String mensaje = context.getString(R.string.contacto)+" "+usuario+"\n"+
		                 context.getString(R.string.telefono)+": "+telefono;
		Log.d("MENSAJE", mensaje);
		
		// Tomamos lo predefinido en el SharedPreferences para saber qué podemos envíar
		SharedPreferences prefs = context.getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
		String EMailEnvios = prefs.getString("EmailEnvios", "info@gmail.com");
		String PasswordEnvios = prefs.getString("PasswordEnvios", "lerolero");
		
		boolean boolSMS  = prefs.getBoolean("SMS", true);		
		boolean boolMail  = prefs.getBoolean("Mail", true);
		
		if (boolSMS==true){
			try{
				// Envíamos el SMS
				SmsManager sms = SmsManager.getDefault();
				sms.sendTextMessage(numero, null, mensaje, null, null);
			} catch (Exception e){
				Log.d("SMS", "No se puede enviar el SMS pedido por "+numero+" del teléfono "+telefono);
			}
		}
		
		if (boolMail==true){	
			// Este segundo mail será para tomar el mail de SharedPreference
			try{		
				MailPHP sender = new MailPHP();
				sender.datosEnviar(context.getString(R.string.contactoInfo)+" "+contacto,   
                        mensaje,   
                        EMailEnvios,   
                        EMailEnvios);
				Log.d("Email1", "Datos del contacto "+contacto+" - "+mensaje+" - "+EMailEnvios);
				//GMailSender sender = new GMailSender(EMailEnvios, PasswordEnvios);
                /*sender.sendMail("Datos del contacto "+contacto,   
                        mensaje,   
                        EMailEnvios,   
                        EMailEnvios);*/
			} catch (Exception e){
				Log.d("Email", "No se puede enviar a "+EMailEnvios+" el número "+telefono);
			}					
		}

		// El mail colocado en el SMS que llega
		if (mail!=""){
			try{
				MailPHP sender = new MailPHP();
				sender.datosEnviar(context.getString(R.string.contactoInfo)+" "+contacto,   
                        mensaje,   
                        EMailEnvios,   
                        mail);
				Log.d("Email2", "Datos del contacto "+contacto+" - "+mensaje+" - "+mail);
				GMailSender sender = new GMailSender(EMailEnvios, PasswordEnvios);
                sender.sendMail("Datos del contacto "+contacto,   
                        mensaje,   
                        EMailEnvios,   
                        mail);*/
			} catch (Exception e){
				Log.d("Email", "No se puede enviar a "+mail+" el número "+telefono);
			}
		}				
						
		// Mostramos el mensaje
    	Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show();
	}
	
}
