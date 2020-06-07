package com.pdi.tye;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.Display;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;

public class DataBaseHelper extends SQLiteOpenHelper {
    //Base de Dados
    public static final String DATABASE_NAME="tye.db";

    //Tabela utilizador
    public static final String TABLE_NAME="utilizador";
    public static final String COL_1="ID";
    public static final String COL_2="username";
    public static final String COL_3="password";

    //Tabela conta
    public static final String TABLE1_NAME="conta";
    public static final String COL1_1="ID";
    public static final String COL1_2="designacao";
    public static final String COL1_3="saldo";
    public static final String COL1_4="utilizadorID";
    public static final String COL1_5="userFamVersionCod";
    public static final String COL1_6="status";

    //Tabela categoriaDespesa
    public static final String TABLE2_NAME="categoriaDespesa";
    public static final String COL2_1="cat_ID";
    public static final String COL2_2="designacao";
    public static final String COL2_3="utilizadorID";

    //Tabela registoDespesa
    public static final String TABLE3_NAME="registoDespesa";
    public static final String COL3_1="regDesp_ID";
    public static final String COL3_2="conta_ID";
    public static final String COL3_3="cat_ID";
    public static final String COL3_4="valor";
    public static final String COL3_5="data";
    public static final String COL3_6="designacao";
    public static final String COL3_7="userFamVersionCod";
    public static final String COL3_8="status";

    //Tabela categoriaRendimento
    public static final String TABLE4_NAME="categoriaRendimento";
    public static final String COL4_1="cat_rend_ID";
    public static final String COL4_2="designacao";
    public static final String COL4_3="utilizadorID";

    //Tabela registoRendimento
    public static final String TABLE5_NAME="registoRendimento";
    public static final String COL5_1="regRend_ID";
    public static final String COL5_2="conta_ID";
    public static final String COL5_3="cat_rend_ID";
    public static final String COL5_4="valor";
    public static final String COL5_5="data";
    public static final String COL5_6="designacao";
    public static final String COL5_7="userFamVersionCod";
    public static final String COL5_8="status";

    //Tabela excedenciaDespesas
    public static final String TABLE6_NAME="excedenciaDespesas";
    public static final String COL6_1="exc_desp_id";
    public static final String COL6_2="designacao";
    public static final String COL6_3="limite";
    public static final String COL6_4="data_Inicio";
    public static final String COL6_5="data_Fim";
    public static final String COL6_6="cat_ID";
    public static final String COL6_7="contaID";

    //Tabela registoTransferencias
    public static final String TABLE7_NAME="registoTransferencias";
    public static final String COL7_1="regTransf_id";
    public static final String COL7_2="designacao";
    public static final String COL7_3="contaDestino_ID";
    public static final String COL7_4="valor";
    public static final String COL7_5="dataTransf";
    public static final String COL8_6="conta_ID";

    //Tabela userFamVersion
    public static final String TABLE8_NAME="userFamVersion";
    public static final String COL8_1="ID";
    public static final String COL8_2="username";
    public static final String COL8_3="userNatur";
    public static final String COL8_4="codigo";
    public static final String COL8_5="status";

    //tabela contasEliminadas
    public static final String TABLE9_NAME="contasEliminadas";
    public static final String COL9_1="ID";
    public static final String COL9_2="contaID";
    public static final String COL9_3="designacao";
    public static final String COL9_4="userFamVersionCod";
    public static final String COL9_5="status";

    //despesasEliminadas
    public static final String TABLE10_NAME="despesasEliminadas";
    public static final String COL10_1="ID";
    public static final String COL10_2="despID";
    public static final String COL10_3="contaID";
    public static final String COL10_4="valor";
    public static final String COL10_5="userFamVersionCod";
    public static final String COL10_6="status";

    //rendimentosEliminados
    public static final String TABLE11_NAME="rendimentosEliminados";
    public static final String COL11_1="ID";
    public static final String COL11_2="rendID";
    public static final String COL11_3="contaID";
    public static final String COL11_4="valor";
    public static final String COL11_5="userFamVersionCod";
    public static final String COL11_6="status";

    //transferenciasEliminadas
    public static final String TABLE12_NAME="transferenciasEliminadas";
    public static final String COL12_1="ID";
    public static final String COL12_2="transfID";
    public static final String COL12_3="contaID";
    public static final String COL12_4="contaDestinoID";
    public static final String COL12_5="valor";
    public static final String COL12_6="userFamVersionCod";
    public static final String COL12_7="status";


    public DataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 36);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("PRAGMA foreign_keys = ON");

        db.execSQL("CREATE TABLE utilizador (ID INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT)");
        db.execSQL("CREATE TABLE "+TABLE1_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT, designacao TEXT, saldo DOUBLE, utilizadorID INTEGER, " +
                "userFamVersionCod TEXT, status INTEGER, FOREIGN KEY (utilizadorID) REFERENCES utilizador (ID), FOREIGN KEY (userFamVersionCod) REFERENCES userFamVersion (codigo))");
        db.execSQL("CREATE TABLE "+TABLE2_NAME+" (cat_ID INTEGER PRIMARY KEY AUTOINCREMENT, designacao TEXT, utilizadorID INTEGER," +
                " FOREIGN KEY (utilizadorID) REFERENCES utilizador (ID))");
        db.execSQL("CREATE TABLE "+TABLE3_NAME+" (regDesp_ID INTEGER PRIMARY KEY AUTOINCREMENT, conta_ID INTEGER, cat_ID INTEGER, valor DOUBLE, data DATE, designacao TEXT, userFamVersionCod TEXT, status INTEGER," +
                " FOREIGN KEY(conta_ID) REFERENCES conta(ID), FOREIGN KEY(cat_ID) REFERENCES categoriaDespesa(cat_ID), FOREIGN KEY (userFamVersionCod) REFERENCES userFamVersion (codigo))");
        db.execSQL("CREATE TABLE "+TABLE4_NAME+" (cat_rend_ID INTEGER PRIMARY KEY AUTOINCREMENT, designacao TEXT, utilizadorID INTEGER," +
                " FOREIGN KEY (utilizadorID) REFERENCES utilizador (ID))");
        db.execSQL("CREATE TABLE "+TABLE5_NAME+" (regRend_ID INTEGER PRIMARY KEY AUTOINCREMENT, conta_ID INTEGER, cat_rend_ID INTEGER, valor DOUBLE, data DATE, designacao TEXT, userFamVersionCod TEXT, status INTEGER," +
                " FOREIGN KEY(conta_ID) REFERENCES conta(ID), FOREIGN KEY(cat_rend_ID) REFERENCES categoriaRendimento(cat_rend_ID), FOREIGN KEY (userFamVersionCod) REFERENCES userFamVersion (codigo))");
        db.execSQL("CREATE TABLE "+TABLE6_NAME+" (exc_desp_id INTEGER PRIMARY KEY AUTOINCREMENT, designacao TEXT, limite DOUBLE, data_Inicio DATE, data_Fim DATE, cat_ID INTEGER, contaID INTEGER, " +
                "FOREIGN KEY(cat_ID) REFERENCES categoriaDespesa(cat_ID), FOREIGN KEY(contaID) REFERENCES conta(ID))");
        db.execSQL("CREATE TABLE "+TABLE7_NAME+" (regTransf_id INTEGER PRIMARY KEY AUTOINCREMENT, designacao TEXT, contaDestino_ID INTEGER, valor DOUBLE, dataTransf DATE, conta_ID INTEGER, " +
                "FOREIGN KEY(contaDestino_ID) REFERENCES conta(ID), FOREIGN KEY(conta_ID) REFERENCES conta(ID))");
        db.execSQL("CREATE TABLE "+TABLE8_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, userNatur TEXT, codigo TEXT, status INTEGER, FOREIGN KEY (username) REFERENCES utilizador (username))");
        db.execSQL("CREATE TABLE "+TABLE9_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT, contaID INTEGER, designacao TEXT, userFamVersionCod INTEGER, status INTEGER," +
                "FOREIGN KEY (userFamVersionCod) REFERENCES userFamVersion(codigo))");
        db.execSQL("CREATE TABLE "+TABLE10_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT, despID INTEGER, contaID INTEGER, valor DOUBLE, userFamVersionCod INTEGER, status INTEGER," +
                "FOREIGN KEY (userFamVersionCod) REFERENCES userFamVersion(codigo))");
        db.execSQL("CREATE TABLE "+TABLE11_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT, rendID INTEGER, contaID INTEGER, valor DOUBLE, userFamVersionCod INTEGER, status INTEGER," +
                "FOREIGN KEY (userFamVersionCod) REFERENCES userFamVersion(codigo))");
        db.execSQL("CREATE TABLE "+TABLE12_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT, transfID INTEGER, contaID INTEGER, contaDestinoID INTEGER, valor DOUBLE, userFamVersionCod INTEGER, status INTEGER," +
                "FOREIGN KEY (userFamVersionCod) REFERENCES userFamVersion(codigo))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE1_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE2_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE3_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE4_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE5_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE6_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE7_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE8_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE9_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE10_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE11_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE12_NAME);
        onCreate(db);

    }


    public long addUser(String user, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", user);
        contentValues.put("password", password);
        long res = db.insert("utilizador", null, contentValues);
        db.close();
        return res;

    }

    public boolean checkUser(String username, String password){
        String[] columns = { COL_1 };
        SQLiteDatabase db = getReadableDatabase();
        String selection = COL_2 + "=?" + " and " + COL_3 + "=?";
        String[] selectionArgs = { username, password};
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        if (count>0){
            return true;
        }else {
            return false;
        }
    }

    public boolean checkUsername(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM utilizador where username=?", new String[] {username});
        int count = cursor.getCount();

        if (count>0){
            return false;
        }else {
            return true;
        }
    }



    public Cursor getID(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ID FROM utilizador WHERE username=?", new String[] {username});
        return cursor;
    }

    public Cursor getPassword(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT password FROM utilizador WHERE username=?", new String[] {username});
        return cursor;
    }

    public boolean checkDesignacaoConta(String designacao, String utilizadorID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM conta WHERE designacao=? AND utilizadorID=?", new String[] {designacao, utilizadorID});
        int count = cursor.getCount();
        if (count > 0){
            return false;
        }else{
            return true;
        }
    }



    public long criarConta(String designacao, String saldo, String utilizadorID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("designacao", designacao);
        contentValues.put("saldo", saldo);
        contentValues.put("utilizadorID", utilizadorID);
        long res = db.insert("conta", null, contentValues);
        db.close();
        return res;
    }

    public long updateInfoConta(String id, String designacao, String saldo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("designacao", designacao);
        contentValues.put("saldo", saldo);
        long res = db.update("conta", contentValues, "ID=?", new String[] {id});
        db.close();
        return res;
    }

    public Cursor getInfoContas(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT designacao, saldo, userFamVersionCod FROM conta WHERE userFamVersionCod IS NOT NULL", null);
        return cursor;
    }

    public Cursor getContaInfoFromID (String utilizadorID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select designacao, saldo, userFamVersionCod FROM conta WHERE utilizadorID=?", new String[]{utilizadorID});
        return cursor;
    }

    public Cursor getSaldoContaID(String contaID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT saldo FROM conta WHERE ID=?", new String[] {contaID});
        return cursor;
    }

    public Cursor getContasIDs(String utilizadorID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ID FROM conta WHERE utilizadorID=?", new String[] {utilizadorID});
        return cursor;
    }

    public Cursor getUserCodeFromConta(String contaID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT userFamVersionCod FROM conta WHERE ID=?", new String[] {contaID});
        return cursor;
    }


    public long AddCatDespesa(String designacao, String utilizadorID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("designacao", designacao);
        contentValues.put("utilizadorID", utilizadorID);
        long res = db.insert("categoriaDespesa", null, contentValues);
        db.close();
        return res;
    }



    public boolean checkDataCatDespesa(String utilizadorID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT count(*) FROM categoriaDespesa WHERE utilizadorID=?", new String[] {utilizadorID});
        cursor.moveToFirst();
        if (cursor.getInt(0) > 0){
            return  false;
        }else {
            return  true;
        }
    }

    public boolean checkCatDespesaExiste(String designacao, String utilizadorID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT count(*) FROM categoriaDespesa WHERE designacao=? AND utilizadorID=?", new String[] {designacao, utilizadorID});
        cursor.moveToFirst();
        if (cursor.getInt(0) > 0){
            return true;
        } else {
            return false;
        }
    }

    public Cursor getDataCatDespesa(String utilizadorID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM categoriaDespesa WHERE utilizadorID=?", new String[] {utilizadorID});
        return cursor;
    }

    public Cursor getCatID(String designacao, String utilizadorID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT cat_ID FROM categoriaDespesa WHERE designacao=? AND utilizadorID=?", new String[] {designacao, utilizadorID});
        return cursor;
    }

    public long addDespesa(String valor, String data, String catDespID, String contaID, String designacao){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("conta_ID", contaID);
        contentValues.put("cat_ID", catDespID);
        contentValues.put("valor", valor);
        contentValues.put("data", data);
        contentValues.put("designacao", designacao);
        long res = db.insert("registoDespesa", null, contentValues);
        db.close();
        return res;
    }

    public boolean SubtDespesa(String contaID, String saldo, String valorDesp){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Double novoSaldo = Double.valueOf(saldo) - Double.valueOf(valorDesp);
        contentValues.put("saldo", novoSaldo);
        db.update("conta", contentValues, "ID=?", new String[] {contaID});
        db.close();
        return true;
    }

    public Cursor getValorDespesa(String DespID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT valor FROM registoDespesa WHERE regDesp_ID=?", new String[] {DespID});
        return cursor;
    }

    public Cursor getInfoDespesa(String contaID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT regDesp_ID, designacao, valor, data, cat_ID, userFamVersionCod FROM registoDespesa WHERE conta_ID=?", new String[] {contaID});
        return cursor;
    }

    public Cursor getLastDespID(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(regDesp_ID) FROM registoDespesa", null);
        return cursor;
    }

    public Cursor getInfoConta(String contaID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM conta WHERE ID=?", new String[] {contaID});
        return cursor;
    }

    public Cursor getUserCodeFromDesp(String despID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT userFamVersionCod FROM registoDespesa WHERE regDesp_ID=?", new String[] {despID} );
        return cursor;
    }



    public long addCatRendimento(String designacao, String utilizadorID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("designacao", designacao);
        contentValues.put("utilizadorID", utilizadorID);
        long res = db.insert("categoriaRendimento", null, contentValues);
        db.close();
        return res;
    }



    public Cursor getDataCatRendimento(String utilizadorID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM categoriaRendimento WHERE utilizadorID=?", new String[] {utilizadorID});
        return cursor;
    }

    public boolean checkDataCatRendimento(String utilizadorID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT count(*) FROM categoriaRendimento WHERE utilizadorID=?", new String[] {utilizadorID});
        cursor.moveToFirst();
        if (cursor.getInt(0) > 0){
            return  false;
        }else {
            return  true;
        }
    }

    public boolean checkCatRendimentoExiste(String designacao, String utilizadorID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT count(*) FROM categoriaRendimento WHERE designacao=? AND utilizadorID=?", new String[] {designacao, utilizadorID});
        cursor.moveToFirst();
        if (cursor.getInt(0) > 0){
            return true;
        } else {
            return false;
        }
    }

    public Cursor getCatRendID(String designacao, String utilizadorID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT cat_rend_ID FROM categoriaRendimento WHERE designacao=? AND utilizadorID=?", new String[] {designacao, utilizadorID});
        return cursor;
    }

    public long addRendimento(String valorRend, String data, String catRendID, String contaID, String designacao){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("conta_ID", contaID);
        contentValues.put("cat_rend_ID", catRendID);
        contentValues.put("valor", valorRend);
        contentValues.put("data", data);
        contentValues.put("designacao", designacao);
        long res = db.insert("registoRendimento", null, contentValues);
        db.close();
        return res;
    }

    public boolean somaRend(String contaID, String saldo, String valorRend){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Double novoSaldo = Double.valueOf(saldo) + Double.valueOf(valorRend);
        contentValues.put("saldo", novoSaldo);
        db.update("conta", contentValues, "ID=?", new String[] {contaID});
        db.close();
        return true;
    }

    public Cursor getvalorRend(String rendID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT valor FROM registoRendimento WHERE regRend_ID=?", new String[] {rendID});
        return cursor;
    }

    public Cursor getInfoRendimento(String contaID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT regRend_ID, designacao, valor, data, cat_Rend_ID, userFamVersionCod FROM registoRendimento WHERE conta_ID=?", new String[] {contaID});
        return cursor;
    }
    public Cursor getUserCodeFromRend(String rendID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT userFamVersionCod FROM registoRendimento WHERE regRend_ID=?", new String[] {rendID} );
        return cursor;
    }

    public Cursor getLastRendID(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(regRend_ID) FROM registoRendimento", null);
        return cursor;
    }

    public Cursor getContasTransf(String contaID, String utilizadorID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT designacao FROM conta WHERE ID!=? AND utilizadorID=?", new String[] {contaID, utilizadorID});
        return cursor;
    }

    public Cursor getUtilizadorID(String contaID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT utilizadorID FROM conta WHERE ID=?", new String[] {contaID});
        return cursor;
    }

    public Cursor getUsername(String ID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT username FROM utilizador WHERE ID=?", new String[] {ID});
        return cursor;
    }

    public long addTransferencia(String designacao, String contaDestinoID, String valorTransferencia, String dataTransf, String contaID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("designacao", designacao);
        contentValues.put("contaDestino_ID", contaDestinoID);
        contentValues.put("valor", valorTransferencia);
        contentValues.put("dataTransf", dataTransf);
        contentValues.put("Conta_ID", contaID);
        long res = db.insert("registoTransferencias", null, contentValues);
        db.close();
        return res;
    }

    public boolean transfContaOrigem(String contaID, String valorTransf, String saldo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Double novoSaldo = Double.valueOf(saldo) - Double.valueOf(valorTransf);
        contentValues.put("saldo", novoSaldo);
        db.update("conta", contentValues, "ID=?", new String[] {contaID});
        db.close();
        return true;
    }

    public boolean transfContaDestino(String contaDestinoID, String valorTransf, String saldo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Double novoSaldo = Double.valueOf(saldo) + Double.valueOf(valorTransf);
        contentValues.put("saldo", novoSaldo);
        db.update("conta", contentValues, "ID=?", new String[] {contaDestinoID});
        db.close();
        return true;
    }

    public Cursor getValorTransf(String regTransfID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT valor FROM registoTransferencias WHERE regTransf_id=?", new String[] {regTransfID});
        return cursor;
    }

    public Cursor getContaDestinoID(String regTransfID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT contaDestino_ID FROM registoTransferencias WHERE regTransf_id=?", new String[] {regTransfID});
        return cursor;
    }

    public Cursor getContaID(String designacao, String utilizadorID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ID FROM conta WHERE designacao=? AND utilizadorID=?", new String[] {designacao, utilizadorID});
        return cursor;
    }

    public Cursor getSaldoConta(String designacao, String utilizadorID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT saldo FROM conta WHERE designacao=? AND utilizadorID=?", new String[] {designacao, utilizadorID});
        return cursor;
    }

    public ArrayList<String> queryDespesa(String contaID, String dataInicio, String dataFim){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> despesas = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT SUM(valor) FROM registoDespesa WHERE conta_ID=? AND data>=? AND data<=? GROUP BY cat_ID ORDER BY data", new String[] {contaID, dataInicio, dataFim});
        while (cursor.moveToNext()){
            despesas.add(cursor.getString(0));
        }
        cursor.close();
        return despesas;

    }

    public Cursor getTotalDespesas(String contaID, String dataInicio, String dataFim){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(valor) FROM registoDespesa WHERE conta_ID=? AND data>=? AND data <=?", new String[] {contaID,dataInicio,dataFim});
        return cursor;
    }

    public Cursor getTotalDespesasCat(String contaID, String catID, String dataInicio, String dataFim){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(valor) FROM registoDespesa WHERE conta_ID=? AND cat_ID=? AND data>=? AND data <=?", new String[] {contaID,catID,dataInicio,dataFim});
        return cursor;
    }

    public ArrayList<String> queryDespesaCat(String contaID, String dataInicio, String dataFim, String catID){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> despesasCat = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT SUM(valor) FROM registoDespesa WHERE conta_ID=? AND data>=? AND data<=? AND cat_ID=? GROUP BY data ORDER BY data", new String[] {contaID, dataInicio, dataFim, catID});
        while (cursor.moveToNext()){
            despesasCat.add(cursor.getString(0));
        }
        cursor.close();
        return despesasCat;
    }

    public ArrayList<String> queryDatasDespCat(String contaID, String catID, String dataInicio, String dataFim){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> datas = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT data FROM registoDespesa WHERE conta_ID=? AND cat_ID=? AND data>=? AND data<=? GROUP BY data ORDER BY data", new String[] {contaID, catID, dataInicio, dataFim});
        while (cursor.moveToNext()){
            datas.add(cursor.getString(0));
        }
        cursor.close();
        return datas;

    }

    public ArrayList<String> queryCatID(String contaID, String data_Inicio, String data_Fim){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> cat_ID = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT cat_ID FROM registoDespesa WHERE conta_ID=? AND data>=? AND data<=? GROUP BY cat_ID ORDER BY data", new String[] {contaID, data_Inicio, data_Fim});
        while (cursor.moveToNext()){
            cat_ID.add(cursor.getString(0));
        }
        cursor.close();
        return cat_ID;
    }

    public Cursor getDesignacaoCat(String catID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT designacao FROM categoriaDespesa WHERE cat_ID=?", new String[] {catID});
        return cursor;
    }

    public Cursor getTotalRendimentos(String contaID, String dataInicio, String dataFim){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(valor) FROM registoRendimento WHERE conta_ID=? AND data>=? AND data <=?", new String[] {contaID,dataInicio,dataFim});
        return cursor;
    }

    public Cursor getTotalRendimentosCat(String contaID, String catRendID, String dataInicio, String dataFim){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(valor) FROM registoRendimento WHERE conta_ID=? AND cat_rend_ID=? AND data>=? AND data <=?", new String[] {contaID,catRendID,dataInicio,dataFim});
        return cursor;
    }

    public ArrayList<String> queryRendimentos(String contaID, String dataInicio, String dataFim){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> rendimentos = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT SUM(valor) FROM registoRendimento WHERE conta_ID=? AND data>=? AND data<=? GROUP BY cat_rend_ID ORDER BY data", new String[] {contaID, dataInicio, dataFim});
        while (cursor.moveToNext()){
            rendimentos.add(cursor.getString(0));
        }
        cursor.close();
        return rendimentos;

    }

    public ArrayList<String> queryCatRendID(String contaID, String data_Inicio, String data_Fim){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> catRend_ID = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT cat_rend_ID FROM registoRendimento WHERE conta_ID=? AND data>=? AND data<=? GROUP BY cat_rend_ID ORDER BY data", new String[] {contaID, data_Inicio, data_Fim});
        while (cursor.moveToNext()){
            catRend_ID.add(cursor.getString(0));
        }
        cursor.close();
        return catRend_ID;
    }

    public Cursor getDesignacaoCatRend(String catRend_ID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT designacao FROM categoriaRendimento WHERE cat_rend_ID=?", new String[] {catRend_ID});
        return cursor;
    }

    public ArrayList<String> queryRendimentosCat(String contaID, String dataInicio, String dataFim, String catID){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> rendimentosCat = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT SUM(valor) FROM registoRendimento WHERE conta_ID=? AND data>=? AND data<=? AND cat_rend_ID=? GROUP BY data", new String[] {contaID, dataInicio, dataFim, catID});
        while (cursor.moveToNext()){
            rendimentosCat.add(cursor.getString(0));
        }
        cursor.close();
        return rendimentosCat;
    }

    public ArrayList<String> queryDatasRendCat(String contaID, String catID, String dataInicio, String dataFim){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> datas = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT data FROM registoRendimento WHERE conta_ID=? AND cat_rend_ID=? AND data>=? AND data<=? GROUP BY data", new String[] {contaID, catID, dataInicio, dataFim});
        while (cursor.moveToNext()){
            datas.add(cursor.getString(0));
        }
        cursor.close();
        return datas;

    }

    public Cursor getTotalTransferencias(String contaID, String dataInicio, String dataFim){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(valor) FROM registoTransferencias WHERE conta_ID=? AND dataTransf>=? AND dataTransf <=?", new String[] {contaID,dataInicio,dataFim});
        return cursor;
    }

    public Cursor getTotalTransferenciasCat(String contaID, String dataInicio, String dataFim, String contaDestinoID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(valor) FROM registoTransferencias WHERE conta_ID=? AND contaDestino_ID=? AND dataTransf>=? AND dataTransf<=?", new String[] {contaID, contaDestinoID, dataInicio, dataFim});
        return cursor;
    }

    public ArrayList<String> queryTransferencias(String contaID, String dataInicio, String dataFim){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> transferencias = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT SUM(valor) FROM registoTransferencias WHERE conta_ID=? AND dataTransf>=? AND dataTransf<=? GROUP BY contaDestino_ID ORDER BY dataTransf", new String[] {contaID, dataInicio, dataFim});
        while (cursor.moveToNext()){
            transferencias.add(cursor.getString(0));
        }
        cursor.close();
        return transferencias;
    }

    public ArrayList<String> queryContaDestinoID(String contaID, String data_Inicio, String data_Fim){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> contasDestino = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT contaDestino_ID FROM registoTransferencias WHERE conta_ID=? AND dataTransf>=? AND dataTransf<=? GROUP BY contaDestino_ID ORDER BY dataTransf", new String[] {contaID, data_Inicio, data_Fim});
        while (cursor.moveToNext()){
            contasDestino.add(cursor.getString(0));
        }
        cursor.close();
        return contasDestino;
    }

    public Cursor getContaDestinoDesignacao(String contaDestino_ID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT designacao FROM conta WHERE ID=?", new String[] {contaDestino_ID});
        return cursor;
    }

    public ArrayList<String> queryTransferenciaContaDest(String contaID, String dataInicio, String dataFim, String contaDestino_ID){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> transfContaDestino = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT SUM(valor) FROM registoTransferencias WHERE conta_ID=? AND dataTransf>=? AND dataTransf<=? AND contaDestino_ID=? GROUP BY dataTransf", new String[] {contaID, dataInicio, dataFim, contaDestino_ID});
        while (cursor.moveToNext()){
            transfContaDestino.add(cursor.getString(0));
        }
        cursor.close();
        return transfContaDestino;
    }

    public ArrayList<String> queryDatasTransfContaDestino(String contaID, String contaDestino_ID, String dataInicio, String dataFim){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> datas = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT dataTransf FROM registoTransferencias WHERE conta_ID=? AND contaDestino_ID=? AND dataTransf>=? AND dataTransf<=? GROUP BY dataTransf", new String[] {contaID, contaDestino_ID, dataInicio, dataFim});
        while (cursor.moveToNext()){
            datas.add(cursor.getString(0));
        }
        cursor.close();
        return datas;
    }

    public long addLimiteDespesas(String designacao, String limite, String dataInicio, String dataLimite, String catID, String contaID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("designacao", designacao);
        contentValues.put("limite", limite);
        contentValues.put("data_Inicio", dataInicio);
        contentValues.put("data_Fim", dataLimite);
        contentValues.put("cat_ID", catID);
        contentValues.put("contaID", contaID);
        long res = db.insert("excedenciaDespesas", null, contentValues);
        db.close();
        return res;
    }

    public boolean checkLimitesCriados(String contaID, String catID, String dataInicio, String dataFim){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT count(*) FROM excedenciaDespesas WHERE contaID=? AND cat_ID=? AND data_Inicio=? AND data_Fim=?", new String[] {contaID, catID, dataInicio, dataFim});
        cursor.moveToFirst();
        if (cursor.getInt(0) > 0){
            return true;
        } else {
            return false;
        }
    }

    public Integer deleteLimiteDataFim(String dataAtual, String contaID){
        SQLiteDatabase db = this.getWritableDatabase();
        int res = db.delete("excedenciaDespesas", "data_Fim<=? AND contaID=?", new String[] {dataAtual,contaID});
        return res;
    }

    public boolean checkLimitesDesp(String contaID, String catID, String data){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT count(*) FROM excedenciaDespesas WHERE contaID=? AND cat_ID=? AND data_Inicio<=? AND data_Fim>=?", new String[] {contaID, catID, data, data});
        cursor.moveToFirst();
        if (cursor.getInt(0) > 0){
            return  true;
        }else {
            return  false;
        }
    }

    public Cursor getLimitesDesp(String contaID, String catID, String data){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT limite FROM excedenciaDespesas WHERE contaID=? AND cat_ID=? AND data_Inicio<=? AND data_Fim>=?", new String[] {contaID, catID, data, data});
        return cursor;
    }

    public Cursor checkCatLimiteDesp(String contaID, String data){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT cat_ID FROM excedenciaDespesas WHERE contaID=? AND data_Inicio<=? AND data_Fim>=?", new String[] {contaID, data, data});
        return cursor;
    }

    public Cursor getLimiteID(String contaID, String catID, String limite, String data){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT exc_desp_id FROM excedenciaDespesas WHERE contaID=? AND cat_ID=? AND limite=? AND data_Inicio<=? AND data_Fim>=?", new String[] {contaID,catID,limite,data,data});
        return cursor;
    }

    public Cursor getDatasLimite(String limiteDespID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT data_Inicio, data_Fim FROM excedenciaDespesas WHERE exc_desp_id=?", new String[] {limiteDespID});
        return cursor;
    }

    public Cursor getTotalDespesasLimite(String contaID, String dataInicio, String dataFim){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(valor) FROM registoDespesa WHERE conta_ID=? AND data>=? AND data<=?", new String[] {contaID, dataInicio, dataFim});
        return cursor;
    }

    public Cursor getTotalDespesasLimiteCat(String contaID, String catID, String dataInicio, String dataFim){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(valor) FROM registoDespesa WHERE conta_ID=? AND cat_ID=? AND data>=? AND data<=?", new String[] {contaID, catID, dataInicio, dataFim});
        return cursor;
    }

    public Integer deleteContaFin(String contaID){
        SQLiteDatabase db = this.getWritableDatabase();
        int res = db.delete("conta", "ID=?", new String[] {contaID});
        return res;
    }

    public Integer deleteDespesaConta(String contaID){
        SQLiteDatabase db = this.getWritableDatabase();
        int res = db.delete("registoDespesa", "conta_ID=?", new String[] {contaID});
        return res;
    }

    public Integer deleteRendimentoConta(String contaID){
        SQLiteDatabase db = this.getWritableDatabase();
        int res = db.delete("registoRendimento", "conta_ID=?", new String[] {contaID});
        return res;
    }

    public Integer deleteExcDespesaConta(String contaID){
        SQLiteDatabase db = this.getWritableDatabase();
        int res = db.delete("excedenciaDespesas", "contaID=?", new String[] {contaID});
        return res;
    }

    public Integer deleteTransferenciaConta(String contaID){
        SQLiteDatabase db = this.getWritableDatabase();
        int res = db.delete("registoTransferencias", "conta_ID=?", new String[] {contaID});
        return res;
    }

    public Integer deleteLimiteDespesas(String limiteID){
        SQLiteDatabase db = this.getWritableDatabase();
        int res = db.delete("excedenciaDespesas", "exc_desp_id=?", new String[] {limiteID});
        return res;
    }

    public Integer deleteDespesa(String regDespID){
        SQLiteDatabase db = this.getWritableDatabase();
        int res = db.delete("registoDespesa", "regDesp_ID=?", new String[] {regDespID});
        return res;
    }

    public Integer deleteRendimento(String regRendID){
        SQLiteDatabase db = this.getWritableDatabase();
        int res = db.delete("registoRendimento", "regRend_ID=?", new String[] {regRendID});
        return res;
    }

    public Integer deleteTransferencia(String regTransfID){
        SQLiteDatabase db = this.getWritableDatabase();
        int res = db.delete("registoTransferencias", "regTransf_id=?", new String[] {regTransfID});
        return res;
    }

    public long addUserFamVersion(String username, String userNatur, String codigo, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("userNatur", userNatur);
        contentValues.put("codigo", codigo);
        contentValues.put("status", status);
        long res = db.insert("userFamVersion", null, contentValues);
        db.close();
        return res;
    }

    public boolean checkDataUserFamVersion(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT count(*) FROM userFamVersion WHERE username=?", new String[] {username});
        cursor.moveToFirst();
        if (cursor.getInt(0) > 0){
            return  true;
        }else {
            return  false;
        }
    }

    public Cursor getUserNatur_codigo(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT userNatur, codigo FROM userFamVersion WHERE username=?", new String[] {username});
        return cursor;
    }

    public Cursor getUsernameFromCode (String userCode){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT username FROM userFamVersion WHERE codigo=?", new String[] {userCode});
        return cursor;
    }

    public Cursor getUnsyncUserFV(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM userFamVersion WHERE status=?", new String[] {"0"});
        return cursor;
    }

    public long updateUserFVStatus(String id, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", status);
        long res = db.update("userFamVersion", contentValues, "ID=?", new String[] {id});
        db.close();
        return res;
    }

    public long updateContaUserCode(String userCode, String utilizadorID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("userFamVersionCod", userCode);
        long res = db.update("conta", contentValues, "utilizadorID=?", new String[] {utilizadorID});
        db.close();
        return res;
    }

    public long updateContaStatus(String utilizadorID, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", status);
        long res = db.update("conta", contentValues, "utilizadorID=?", new String[] {utilizadorID});
        db.close();
        return res;
    }



    public long updateDespUserCode(String userCode, String contaID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("userFamVersionCod", userCode);
        long res = db.update("registoDespesa", contentValues, "conta_ID=?", new String[] {contaID});
        db.close();
        return res;
    }

    public long updateDespStatus(String contaID, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", status);
        long res = db.update("registoDespesa", contentValues, "conta_ID=?", new String[] {contaID});
        db.close();
        return res;
    }

    public long updateRendUserCode(String userCode, String contaID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("userFamVersionCod", userCode);
        long res = db.update("registoRendimento", contentValues, "conta_ID=?", new String[] {contaID});
        db.close();
        return res;
    }

    public long updateRendStatus(String contaID, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", status);
        long res = db.update("registoRendimento", contentValues, "conta_ID=?", new String[] {contaID});
        db.close();
        return res;
    }

    public Cursor getUserCode(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT codigo FROM userFamVersion WHERE username=?", new String[] {username});
        return cursor;
    }

    public boolean checkUserFamVersion(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT count(*) FROM userFamVersion WHERE username=?", new String[] {username});
        cursor.moveToFirst();
        if (cursor.getInt(0) > 0){
            return true;
        } else {
            return false;
        }
    }



    public long updateSingleContaUserCode(String designacao, String userCode, String utilizadorID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("userFamVersionCod", userCode);
        long res = db.update("conta", contentValues, "designacao=? AND utilizadorID=?", new String[] {designacao,utilizadorID});
        db.close();
        return res;
    }

    public long updateSingleContaStatus(String designacao, String utilizadorID, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", status);
        long res = db.update("conta", contentValues, "designacao=? AND utilizadorID=?", new String[] {designacao,utilizadorID});
        db.close();
        return res;
    }


    public long updateDespesaUserCodeStatus(String userCode, String status, String despID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("userFamVersionCod", userCode);
        contentValues.put("status", status);
        long res = db.update("registoDespesa", contentValues, "regDesp_ID=?", new String[] {despID});
        db.close();
        return res;
    }

    public long updateRendUserCodeStatus(String userCode, String status, String rendID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("userFamVersionCod", userCode);
        contentValues.put("status", status);
        long res = db.update("registoRendimento", contentValues, "regRend_ID=?", new String[] {rendID});
        db.close();
        return res;
    }




    public Cursor getUnsyncContas(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ID, designacao, saldo, userFamVersionCod FROM conta WHERE status=?", new String[] {"0"});
        return cursor;
    }



    public Cursor getUnsyncDesp(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT regDesp_ID, designacao, valor, data, cat_ID, userFamVersionCod FROM registoDespesa WHERE status=?", new String[] {"0"});
        return cursor;
    }

    public Cursor getUnsyncRend(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT regRend_ID, designacao, valor, data, cat_Rend_ID, userFamVersionCod FROM registoRendimento WHERE status=?", new String[] {"0"});
        return cursor;
    }

    public long updateContaSyncStatus(String contaID, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", status);
        long res = db.update("conta", contentValues, "ID=?", new String[] {contaID});
        db.close();
        return res;
    }



    public long updateDespSyncStatus(String despID, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", status);
        long res = db.update("registoDespesa", contentValues, "regDesp_ID=?", new String[] {despID});
        db.close();
        return res;

    }

    public long updateRendSyncStatus(String rendID, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", status);
        long res = db.update("registoRendimento", contentValues, "regRend_ID=?", new String[] {rendID});
        db.close();
        return res;

    }

    public long addContaApagada(String contaID, String designacao, String status, String userCode){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("contaID", contaID);
        contentValues.put("designacao", designacao);
        contentValues.put("status", status);
        contentValues.put("userFamVersionCod", userCode);
        long res = db.insert("contasEliminadas", null, contentValues);
        db.close();
        return res;
    }

    public Cursor getUnsyncContasEliminadas(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ID, designacao, userFamVersionCod FROM contasEliminadas WHERE status=?", new String[] {"0"});
        return cursor;
    }

    public Integer deleteContaEliminada(String ID){
        SQLiteDatabase db = this.getWritableDatabase();
        int res = db.delete("contasEliminadas", "ID=?", new String[] {ID});
        return res;
    }

    public long addDespesaApagada(String despID, String valor, String contaID, String status, String userCode){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("despID", despID);
        contentValues.put("valor", valor);
        contentValues.put("contaID", contaID);
        contentValues.put("status", status);
        contentValues.put("userFamVersionCod", userCode);
        long res = db.insert("despesasEliminadas", null, contentValues);
        db.close();
        return res;
    }

    public Cursor getUnsyncDespesasEliminadas(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ID, contaID, despID, userFamVersionCod FROM despesasEliminadas WHERE status=?", new String[] {"0"});
        return cursor;
    }

    public Integer deleteDespesasEliminada(String ID){
        SQLiteDatabase db = this.getWritableDatabase();
        int res = db.delete("despesasEliminadas", "ID=?", new String[] {ID});
        return res;
    }

    public long addRendimentoApagado(String rendID, String valor, String contaID, String status, String userCode){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("rendID", rendID);
        contentValues.put("valor", valor);
        contentValues.put("contaID", contaID);
        contentValues.put("status", status);
        contentValues.put("userFamVersionCod", userCode);
        long res = db.insert("rendimentosEliminados", null, contentValues);
        db.close();
        return res;
    }

    public Cursor getUnsyncRendimentosEliminados(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ID, contaID, rendID, userFamVersionCod FROM rendimentosEliminados WHERE status=?", new String[] {"0"});
        return cursor;
    }

    public Integer deleteRendimentoEliminado(String ID){
        SQLiteDatabase db = this.getWritableDatabase();
        int res = db.delete("rendimentosEliminados", "ID=?", new String[] {ID});
        return res;
    }













}
