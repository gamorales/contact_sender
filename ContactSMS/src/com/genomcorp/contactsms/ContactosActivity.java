package com.genomcorp.contactsms;

import com.genomcorp.clases.BaseDatos;
import com.genomcorp.clases.Titular;
import com.google.ads.AdRequest;
import com.google.ads.AdView;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ContactosActivity extends Activity {
	TextView lblMensaje;

    private Titular[] datos;    
    final BaseDatos dbCursor = new BaseDatos(this, "contactos", null, 1);
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);
        
        // Look up the AdView as a resource and load a request.
        AdView adView = (AdView)this.findViewById(R.id.Publicidad);
        adView.loadAd(new AdRequest());        
        
        lblMensaje = (TextView)findViewById(R.id.lblMensaje);
        
        // En esta se cargan los contactos consultados en el archivo
        // contactos.db para llenar el ListView de ConfigurationActivity        
        Cursor c;
        c = dbCursor.leerContactos(true);        
       	if (c.moveToFirst()){
       		datos = new Titular[c.getCount()];
       		int n = 0;
       		do{
   				datos[n] = new Titular(" "+c.getString(1), "  "+c.getString(2)+" - "+c.getString(3));
   				n++;
   			}while(c.moveToNext());
   		} else {
   			datos = new Titular[]{
   			new Titular(" 0 "+lblMensaje.getText().toString(), getBaseContext().getString(R.string.recuerde))
   			};
   		}

        AdaptadorTitulares adaptador = new AdaptadorTitulares(this);        
        ListView lstOpciones = (ListView)findViewById(R.id.LstOpciones);        
        lstOpciones.setAdapter(adaptador);
        
    }        
    
    class AdaptadorTitulares extends ArrayAdapter {
    	
    	Activity context;
    	
    	@SuppressWarnings("unchecked")
		AdaptadorTitulares(Activity context) {
    		super(context, R.layout.activity_contactos_titulos, datos);
    		this.context = context;
    	}
    	
    	public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = context.getLayoutInflater();
			View item = inflater.inflate(R.layout.activity_contactos_titulos, null);
			
			try{
				TextView lblTitulo = (TextView)item.findViewById(R.id.LblTitulo);
				lblTitulo.setText(datos[position].getTitulo());
				
				TextView lblSubtitulo = (TextView)item.findViewById(R.id.LblSubTitulo);
				lblSubtitulo.setText(datos[position].getSubtitulo());				
			} catch(Exception e){
				Log.d("Error", "No imprimió en el Listview");
			}
			
			return(item);
		}
    }
}
