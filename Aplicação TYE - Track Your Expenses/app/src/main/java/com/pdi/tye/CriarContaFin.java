package com.pdi.tye;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CriarContaFin extends AppCompatActivity {

    public static final String EXTRA_TEXT = "com.pdi.tye.EXTRA_TEXT";

    public static final String URL_SAVE_CONTA = "http://192.168.56.1/TYE/conta.php";

    public static final String SYNCED_WITH_SERVER = "1";
    public static final String NOT_SYNCED_WITH_SERVER = "0";

    DataBaseHelper db;

    ActionBar actionBar;

    private ImageView image_logout;


    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_conta_fin);

        Intent intent = getIntent();
        final String username = intent.getStringExtra(ContaFinanceira.EXTRA_TEXT);



        db = new DataBaseHelper(this);

        populateSampleDataDespesas(username);
        populateSampleDataRendimentos(username);

        actionBar = getSupportActionBar();
        actionBar.hide();


        final EditText txt_designacao = findViewById(R.id.txt_designacao);
        final  EditText txt_saldo = findViewById(R.id.txt_saldo);
        final Button btn_adicionar = findViewById(R.id.btn_adicionar);
        image_logout = findViewById(R.id.img_logoutCriarContaFin);


        btn_adicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String designacao = txt_designacao.getText().toString().trim();
                String saldo = txt_saldo.getText().toString().trim();
                Cursor cursor = db.getID(username);
                while (cursor.moveToNext()){
                    String utilizadorID = cursor.getString(0);
                    Boolean designacaoValida = db.checkDesignacaoConta(designacao, utilizadorID);
                    if (designacao.isEmpty() || saldo.isEmpty()){
                        Toast.makeText(CriarContaFin.this, "Campos por preencher!",Toast.LENGTH_SHORT).show();
                    }else if (designacaoValida==true){
                        long val = db.criarConta(designacao, saldo, utilizadorID);
                        Cursor username_cursor = db.getUsername(utilizadorID);
                        while (username_cursor.moveToNext()){
                            String username = username_cursor.getString(0);
                            Boolean userFamVersion = db.checkUserFamVersion(username);
                            if (userFamVersion == true){
                                Cursor userCode_cursor = db.getUserCode(username);
                                while (userCode_cursor.moveToNext()){
                                    String userCode = userCode_cursor.getString(0);
                                    long val1 = db.updateSingleContaUserCode(designacao,userCode, utilizadorID);
                                    saveContaToServer(designacao, saldo, userCode, utilizadorID, username);
                                }
                                if(val>0){
                                    Toast.makeText(CriarContaFin.this, "Conta criada com sucesso!",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(CriarContaFin.this, ContaFinanceira.class);
                                    intent.putExtra(EXTRA_TEXT, username);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(CriarContaFin.this, "Conta não adicionada",Toast.LENGTH_SHORT).show();
                                }
                            } else if (userFamVersion == false){
                                if(val>0){
                                    Toast.makeText(CriarContaFin.this, "Conta criada com sucesso!",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(CriarContaFin.this, ContaFinanceira.class);
                                    intent.putExtra(EXTRA_TEXT, username);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(CriarContaFin.this, "Conta não adicionada",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }


                    } else {
                        Toast.makeText(CriarContaFin.this, "Já existe uma conta registada com a designação "+designacao, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        image_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Shared.logout(CriarContaFin.this);
                Intent logout_intent = new Intent(CriarContaFin.this, MainActivity.class);
                startActivity(logout_intent);
                finish();
            }
        });
    }

    private void saveContaToServer(final String designacao, final String saldo, final String userCode, final String utilizadorID, final String username) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_CONTA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                Toast.makeText(CriarContaFin.this, "Conta guardada com sucesso!", Toast.LENGTH_SHORT).show();
                                db.updateSingleContaStatus(designacao,utilizadorID, SYNCED_WITH_SERVER);
                            } else {
                                Toast.makeText(CriarContaFin.this, "Conta não guardada!", Toast.LENGTH_SHORT).show();
                                db.updateSingleContaStatus(designacao,utilizadorID, NOT_SYNCED_WITH_SERVER);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CriarContaFin.this, "Sem ligação á Internet", Toast.LENGTH_SHORT).show();
                        db.updateSingleContaStatus(designacao,utilizadorID, NOT_SYNCED_WITH_SERVER);
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

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        final String username = intent.getStringExtra(ContaFinanceira.EXTRA_TEXT);
        Intent intentback = new Intent(CriarContaFin.this, ContaFinanceira.class);
        intentback.putExtra(EXTRA_TEXT, username);
        startActivity(intentback);
        finish();
    }

    private void populateSampleDataRendimentos(String username) {
        Cursor id_cursor = db.getID(username);
        while (id_cursor.moveToNext()){
            String utilizadorID = id_cursor.getString(0);
            Boolean isEmpty = db.checkDataCatRendimento(utilizadorID);
            if (isEmpty==true){
                db.addCatRendimento("Salário", utilizadorID);
                db.addCatRendimento("Investimentos", utilizadorID);
                db.addCatRendimento("Prémios Monetários", utilizadorID);
                db.addCatRendimento("Vendas", utilizadorID);
                db.addCatRendimento("Mesada", utilizadorID);
                db.addCatRendimento("Semanada", utilizadorID);
                db.addCatRendimento("Horas Extra", utilizadorID);
            }
        }
    }

    private void populateSampleDataDespesas(String username) {
        Cursor id_cursor = db.getID(username);
        while (id_cursor.moveToNext()) {
            String utilizadorID = id_cursor.getString(0);
            Boolean isEmpty = db.checkDataCatDespesa(utilizadorID);
            if (isEmpty==true){
                db.AddCatDespesa("Todas", utilizadorID);
                db.AddCatDespesa("Mercearia", utilizadorID);
                db.AddCatDespesa("Entertenimento", utilizadorID);
                db.AddCatDespesa("Vestuário e Calçado", utilizadorID);
                db.AddCatDespesa("Saúde", utilizadorID);
                db.AddCatDespesa("Carro e Transporte", utilizadorID);
                db.AddCatDespesa("Comer Fora", utilizadorID);
                db.AddCatDespesa("Fitness", utilizadorID);
                db.AddCatDespesa("Subscrições", utilizadorID);
                db.AddCatDespesa("Despesa de Alojamento", utilizadorID);
                db.AddCatDespesa("Cuidados Pessoais", utilizadorID);
            }
        }
    }

}
