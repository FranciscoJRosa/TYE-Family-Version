package com.pdi.tye;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FamVersion extends AppCompatActivity {
    public static final String EXTRA_TEXT = "com.pdi.tye.EXTRA_TEXT";
    public static final String EXTRA_TEXT1 = "com.pdi.tye.EXTRA_TEXT1";
    public static final String TEXT_DESP = "com.pdi.tye.TEXT_DESP";
    public static final String TEXT_DESP_ID = "com.pdi.tye.TEXT_DESP";

    DataBaseHelper db;

    public static final String URL_SAVE_USERFAMVERSION = "http://192.168.56.1/TYE/userFamVersion.php";
    public static final String URL_SAVE_CONTA = "http://192.168.56.1/TYE/conta.php";
    public static final String URL_SAVE_DESP = "http://192.168.56.1/TYE/registoDespesas.php";
    public static final String URL_SAVE_REND = "http://192.168.56.1/TYE/registoRendimentos.php";

    RadioGroup radioGroup;
    RadioButton radioButton;
    Button btn_aderir;
    Button btn_gerarCodigo;
    TextView txt_codigo;

    ConstraintLayout mainLayout;

    public static final String SYNCED_WITH_SERVER = "1";
    public static final String NOT_SYNCED_WITH_SERVER = "0";

    public static final String DATA_SAVED_BROADCAST = "com.pdi.sync.datasaved";

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fam_version);


        db = new DataBaseHelper(this);

        getSupportActionBar().setElevation(0);

        mainLayout = findViewById(R.id.mainLayout);

        Intent famVersion_intent = getIntent();
        final String contaID = famVersion_intent.getStringExtra(EXTRA_TEXT1);

        final String desp = "despesa";

        radioGroup = findViewById(R.id.radioGroup);
        btn_aderir = findViewById(R.id.btn_aderirFV);
        txt_codigo = findViewById(R.id.txt_codigo);
        btn_gerarCodigo = findViewById(R.id.btn_gerarCodigo);

        btn_aderir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int radioID = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(radioID);
                String codigo = txt_codigo.getText().toString();
                Boolean existsInternetCon = checkInternetConn();
                if (radioGroup.getCheckedRadioButtonId() == -1){
                    Toast.makeText(FamVersion.this, "Por favor selcione a natureza do membro da família!", Toast.LENGTH_LONG).show();
                } else if (codigo.equals("")) {
                    Toast.makeText(FamVersion.this, "Por favor requisite um código!", Toast.LENGTH_LONG).show();
                } else {
                    String userNatur = radioButton.getText().toString();
                    Cursor utilizadorID_cursor = db.getUtilizadorID(contaID);
                    while (utilizadorID_cursor.moveToNext()) {
                        String utilizadorID = utilizadorID_cursor.getString(0);
                        Cursor username_cursor = db.getUsername(utilizadorID);
                        while (username_cursor.moveToNext()) {
                            String username = username_cursor.getString(0);
                            Boolean userExists = db.checkDataUserFamVersion(username);
                            if (userExists==true){
                                Cursor cursor = db.getUserNatur_codigo(username);
                                while (cursor.moveToNext()){
                                    String userN = cursor.getString(0);
                                    String userCod = cursor.getString(1);
                                    Snackbar.make(mainLayout, "Já aderiu à Family Version como "+userN+" e código: "+userCod, Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                        }
                                    }).setActionTextColor(getResources().getColor(R.color.LightBlue)).show();
                                }

                            } else if (existsInternetCon == true){
                                if (userNatur.equals("Membro")){
                                    String password = "";
                                    saveUserToServer(username,userNatur,codigo, password);
                                    long res = db.updateContaUserCode(codigo, utilizadorID);
                                    saveContaToServer(utilizadorID, username);
                                    Cursor contasIDs_cursor = db.getContasIDs(utilizadorID);
                                    while (contasIDs_cursor.moveToNext()){
                                        String contaID = contasIDs_cursor.getString(0);
                                        long res3 = db.updateDespUserCode(codigo, contaID);
                                        saveDespToServer(contaID, username);
                                        long res4 = db.updateRendUserCode(codigo, contaID);
                                        saveRendToServer(contaID, username);
                                    }
                                    Intent intentFamVersion = new Intent(FamVersion.this, MenuFuncionalidades.class);
                                    intentFamVersion.putExtra(TEXT_DESP, desp);
                                    intentFamVersion.putExtra(TEXT_DESP_ID, contaID);
                                    startActivity(intentFamVersion);
                                    finish();
                                } else if (userNatur.equals("Moderador")){
                                    Cursor password_cursor = db.getPassword(username);
                                    while (password_cursor.moveToNext()){
                                        String password = password_cursor.getString(0);
                                        saveUserToServer(username,userNatur,codigo,password);
                                        long res = db.updateContaUserCode(codigo, utilizadorID);
                                        saveContaToServer(utilizadorID, username);
                                        Cursor contasIDs_cursor = db.getContasIDs(utilizadorID);
                                        while (contasIDs_cursor.moveToNext()){
                                            String contaID = contasIDs_cursor.getString(0);
                                            long res3 = db.updateDespUserCode(codigo, contaID);
                                            saveDespToServer(contaID, username);
                                            long res4 = db.updateRendUserCode(codigo, contaID);
                                            saveRendToServer(contaID, username);
                                        }
                                        Intent intentFamVersion = new Intent(FamVersion.this, MenuFuncionalidades.class);
                                        intentFamVersion.putExtra(TEXT_DESP, desp);
                                        intentFamVersion.putExtra(TEXT_DESP_ID, contaID);
                                        startActivity(intentFamVersion);
                                        finish();
                                    }
                                }

                            } else {
                                Snackbar.make(mainLayout, "Não existe ligação á Internet!", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    }
                                }).setActionTextColor(getResources().getColor(R.color.LightBlue)).show();
                            }

                        }
                    }
                }
            }
        });

        btn_gerarCodigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_codigo.setText(getRandomCode(8));
            }
        });
    }




    private Boolean checkInternetConn() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            } else {
                return false;
            }
        }else {
            return false;
        }
    }



    private String getRandomCode(int i) {
        final String characters = "ABCDEFGHIJKLMNOPRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder();
        while(i >0){
            Random rand = new Random();
            result.append(characters.charAt(rand.nextInt(characters.length())));
            i--;
        }
        return result.toString();
    }

    private void saveUserToServer(final String username, final String userNatur, final String codigo, final String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_USERFAMVERSION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                Toast.makeText(FamVersion.this, "Adesão efetuada com sucesso!", Toast.LENGTH_SHORT).show();
                                db.addUserFamVersion(username, userNatur, codigo, SYNCED_WITH_SERVER);
                            } else {
                                Toast.makeText(FamVersion.this, "Adesão não efetuada!", Toast.LENGTH_SHORT).show();
                                db.addUserFamVersion(username, userNatur, codigo, NOT_SYNCED_WITH_SERVER);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(FamVersion.this, "Sem ligação á Internet", Toast.LENGTH_SHORT).show();
                        db.addUserFamVersion(username, userNatur, codigo, NOT_SYNCED_WITH_SERVER);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("userNatur", userNatur);
                params.put("codigo", codigo);
                params.put("password", password);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void saveContaToServer(final String utilizadorID, final String username) {
        Cursor infoConta_cursor = db.getContaInfoFromID(utilizadorID);
        while (infoConta_cursor.moveToNext()){
            final String designacao = infoConta_cursor.getString(0);
            final String saldo = infoConta_cursor.getString(1);
            final String userCode = infoConta_cursor.getString(2);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_CONTA,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject obj = new JSONObject(response);
                                if (!obj.getBoolean("error")) {
                                    Toast.makeText(FamVersion.this, "Contas guardadas com sucesso!", Toast.LENGTH_SHORT).show();
                                    db.updateContaStatus(utilizadorID, SYNCED_WITH_SERVER);
                                } else {
                                    Toast.makeText(FamVersion.this, "Contas não guardadas!", Toast.LENGTH_SHORT).show();
                                    db.updateContaStatus(utilizadorID, NOT_SYNCED_WITH_SERVER);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(FamVersion.this, "Sem ligação á Internet", Toast.LENGTH_SHORT).show();
                            db.updateContaStatus(utilizadorID, NOT_SYNCED_WITH_SERVER);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("designacao", designacao);
                    params.put("saldo", saldo);
                    params.put("userCode", userCode);
                    params.put("username", username);
                    return params;
                }
            };

            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        }
    }

    private void saveDespToServer(final String contaID, final String username) {
        Cursor infoDesp_cursor = db.getInfoDespesa(contaID);
        while (infoDesp_cursor.moveToNext()){
            final String despID = infoDesp_cursor.getString(0);
            final String designacao = infoDesp_cursor.getString(1);
            final String valor = infoDesp_cursor.getString(2);
            final String data = infoDesp_cursor.getString(3);
            final String catID = infoDesp_cursor.getString(4);
            final String userCode = infoDesp_cursor.getString(5);
            Cursor catDesignacao_cursor = db.getDesignacaoCat(catID);
            while (catDesignacao_cursor.moveToNext()){
                final String categoria = catDesignacao_cursor.getString(0);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_DESP,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    if (!obj.getBoolean("error")) {
                                        Toast.makeText(FamVersion.this, "Despesas guardadas com sucesso!", Toast.LENGTH_SHORT).show();
                                        db.updateDespStatus(contaID, SYNCED_WITH_SERVER);
                                    } else {
                                        Toast.makeText(FamVersion.this, "Despesas não guardadas!", Toast.LENGTH_SHORT).show();
                                        db.updateDespStatus(contaID, NOT_SYNCED_WITH_SERVER);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(FamVersion.this, "Sem ligação á Internet", Toast.LENGTH_SHORT).show();
                                db.updateDespStatus(contaID, NOT_SYNCED_WITH_SERVER);
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("despID_App", despID);
                        params.put("designacao", designacao);
                        params.put("valor", valor);
                        params.put("data", data);
                        params.put("categoria", categoria);
                        params.put("userCode", userCode);
                        params.put("username", username);
                        return params;
                    }
                };

                VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
            }

        }
    }

    private void saveRendToServer(final String contaID, final String username) {
        Cursor infoRend_cursor = db.getInfoRendimento(contaID);
        while (infoRend_cursor.moveToNext()){
            final String rendID = infoRend_cursor.getString(0);
            final String designacao = infoRend_cursor.getString(1);
            final String valor = infoRend_cursor.getString(2);
            final String data = infoRend_cursor.getString(3);
            final String catID = infoRend_cursor.getString(4);
            final String userCode = infoRend_cursor.getString(5);
            Cursor catDesignacao_cursor = db.getDesignacaoCatRend(catID);
            while (catDesignacao_cursor.moveToNext()){
                final String categoria = catDesignacao_cursor.getString(0);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_REND,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    if (!obj.getBoolean("error")) {
                                        Toast.makeText(FamVersion.this, "Rendimentos guardados com sucesso!", Toast.LENGTH_SHORT).show();
                                        db.updateRendStatus(contaID, SYNCED_WITH_SERVER);
                                    } else {
                                        Toast.makeText(FamVersion.this, "Despesas não guardadas!", Toast.LENGTH_SHORT).show();
                                        db.updateRendStatus(contaID, NOT_SYNCED_WITH_SERVER);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(FamVersion.this, "Sem ligação á Internet", Toast.LENGTH_SHORT).show();
                                db.updateRendStatus(contaID, NOT_SYNCED_WITH_SERVER);
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("rendID_App", rendID);
                        params.put("designacao", designacao);
                        params.put("valor", valor);
                        params.put("data", data);
                        params.put("categoria", categoria);
                        params.put("userCode", userCode);
                        params.put("username", username);
                        return params;
                    }
                };

                VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);

            }


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent famVersion_intent = getIntent();
        final String contaID = famVersion_intent.getStringExtra(EXTRA_TEXT1);
        Cursor utilizadorID_cursor = db.getUtilizadorID(contaID);
        while (utilizadorID_cursor.moveToNext()){
            String utilizadorID = utilizadorID_cursor.getString(0);
            Cursor username_cursor = db.getUsername(utilizadorID);
            while (username_cursor.moveToNext()){
                String username = username_cursor.getString(0);
                switch (item.getItemId()){
                    case R.id.logoutItemOption:
                        Shared.logout(this);
                        Intent logout_intent = new Intent(FamVersion.this, MainActivity.class);
                        startActivity(logout_intent);
                        finish();
                        return true;
                    case R.id.carteiraContasItem:
                        Intent carteira_intent = new Intent(FamVersion.this, ContaFinanceira.class);
                        carteira_intent.putExtra(EXTRA_TEXT, username);
                        startActivity(carteira_intent);
                        finish();
                    default:
                        return super.onOptionsItemSelected(item);
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent famVersion_intent = getIntent();
        final String contaID = famVersion_intent.getStringExtra(EXTRA_TEXT1);
        final String desp = "despesa";

        Intent intentFamVersion = new Intent(FamVersion.this, MenuFuncionalidades.class);
        intentFamVersion.putExtra(TEXT_DESP, desp);
        intentFamVersion.putExtra(TEXT_DESP_ID, contaID);
        startActivity(intentFamVersion);
        finish();
    }
}
