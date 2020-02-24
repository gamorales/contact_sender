package com.genomcorp.clases;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDatos extends SQLiteOpenHelper{
	
	private String SQLCreate = "CREATE TABLE contactos " +
                                    "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
			                        " contacto VARCHAR(25)," +
			                        " numero VARCHAR(15), " +
			                        " fecha DATE) ";
	
	@SuppressLint("NewApi")
	public BaseDatos(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(SQLCreate);
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		android.util.Log.v("contactos", "Destruyendo la información anterior");
		db.execSQL("DROP TABLE IF EXISTS contactos");
		onCreate(db);
	}
	
	public void insertarContacto(String contacto, String numero, String fecha){
		SQLiteDatabase db = getWritableDatabase();
		if(db!=null){
		  db.execSQL("INSERT INTO contactos (contacto, numero, fecha) " +
		             "VALUES('" + contacto + "', '" + numero +"', '" + fecha +"') ");
		  db.close();   
		}
	}
	
	public Cursor leerContactos(boolean todos){
		String limite;
		if (todos==true){
			limite = " ORDER BY id DESC LIMIT 50";
		} else {
			limite = " ORDER BY id DESC LIMIT 1";
		}
		
		SQLiteDatabase db = getReadableDatabase();
		Cursor datos = db.rawQuery("SELECT id, contacto, numero, fecha" +
				                   " FROM contactos" + limite, null);
		return datos;
	}
}
