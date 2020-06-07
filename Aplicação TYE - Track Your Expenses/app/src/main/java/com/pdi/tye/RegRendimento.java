package com.pdi.tye;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegRendimento extends AppCompatActivity implements AdicionarCatDialog.AdicionarCatDialogListener{
    public static final String EXTRA_TEXT = "com.pdi.tye.EXTRA_TEXT";
    public static final String EXTRA_TEXT1 = "com.pdi.tye.EXTRA_TEXT1";
    public static final String EXTRA_TEXT2 = "com.pdi.tye.EXTRA_TEXT2";
    public static final String TEXT_DESP = "com.pdi.tye.TEXT_DESP";
    public static final String TEXT_DESP_ID = "com.pdi.tye.TEXT_DESP";

    public static final String URL_SAVE_REND = "http://192.168.56.1/TYE/registoRendimentos.php";
    public static final String URL_UPDATE_CONTA = "http://192.168.56.1/TYE/updateConta.php";

    public static final String SYNCED_WITH_SERVER = "1";
    public static final String NOT_SYNCED_WITH_SERVER = "0";

    public static final String DATA_SAVED_BROADCAST = "com.pdi.sync.datasaved";

    DataBaseHelper db;



    private static final String TAG = "RegDespesa";
    private TextView txt_DisplayDate;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private Button btn_registarRend;
    private EditText et_valorRend;
    private Spinner spinnerCategoriaRend;
    private EditText et_designacaoRend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_rendimento);
        db = new DataBaseHelper(this);

        //registerReceiver(new NetworkStateChecker(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        getSupportActionBar().setElevation(0);

        Intent regDesp_intent = getIntent();
        final String contaID = regDesp_intent.getStringExtra(EXTRA_TEXT1);
        final String saldoConta = regDesp_intent.getStringExtra(EXTRA_TEXT2);

        final String desp = "despesa";

        et_valorRend = findViewById(R.id.et_valorRend);
        txt_DisplayDate = (TextView) findViewById(R.id.txt_dataRend);
        btn_registarRend = findViewById(R.id.btn_registarRend);
        spinnerCategoriaRend = findViewById(R.id.sp_categoriaRend);
        et_designacaoRend = findViewById(R.id.et_designacaoRend);


        Cursor utilizadorID_cursor = db.getUtilizadorID(contaID);
        while (utilizadorID_cursor.moveToNext()){
            String utilizadorID = utilizadorID_cursor.getString(0);
            populateSpinner(utilizadorID);
        }

        txt_DisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(RegRendimento.this, android.R.style.Theme_Holo_Dialog_MinWidth, dateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                if (month < 10 && dayOfMonth < 10){
                    String date = year + "/0" + month + "/0" + dayOfMonth;
                    txt_DisplayDate.setText(date);
                } else if (dayOfMonth < 10){
                    String date = year + "/" + month + "/0" + dayOfMonth;
                    txt_DisplayDate.setText(date);
                } else if (month <10){
                    String date = year + "/0" + month + "/" + dayOfMonth;
                    txt_DisplayDate.setText(date);
                } else {
                    String date = year + "/" + month + "/" + dayOfMonth;
                    txt_DisplayDate.setText(date);
                }
            }
        };

        btn_registarRend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valorRend = et_valorRend.getText().toString().trim();
                String categoriaRend = spinnerCategoriaRend.getSelectedItem().toString();
                String data = txt_DisplayDate.getText().toString().trim();
                String designacaoRend = et_designacaoRend.getText().toString().trim();
                if (valorRend.isEmpty() || data.isEmpty() || designacaoRend.isEmpty() || categoriaRend.equals("+ Adicionar Categoria") || categoriaRend.equals("Selecione a Categoria...")){
                    Toast.makeText(RegRendimento.this, "Campos por preencher!", Toast.LENGTH_SHORT).show();
                } else {
                    Cursor utilizadorID_cursor = db.getUtilizadorID(contaID);
                    while (utilizadorID_cursor.moveToNext()){
                        String utilizadorID = utilizadorID_cursor.getString(0);
                        Cursor cursor = db.getCatRendID(categoriaRend, utilizadorID);
                        while (cursor.moveToNext()){
                            String cat_ID = cursor.getString(0);
                            Cursor username_cursor = db.getUsername(utilizadorID);
                            while (username_cursor.moveToNext()){
                                String username = username_cursor.getString(0);
                                Boolean checkUserFamVersion = db.checkUserFamVersion(username);
                                if (checkUserFamVersion==false){
                                    long res = db.addRendimento(valorRend,data,cat_ID,contaID, designacaoRend);
                                    Boolean updateSaldo = db.somaRend(contaID,saldoConta,valorRend);
                                    if(res > 0){
                                        Toast.makeText(RegRendimento.this, "Rendimento registado com sucesso!", Toast.LENGTH_SHORT).show();
                                        Intent intentRend = new Intent(RegRendimento.this, MenuFuncionalidades.class);
                                        intentRend.putExtra(TEXT_DESP, desp);
                                        intentRend.putExtra(TEXT_DESP_ID, contaID);
                                        startActivity(intentRend);
                                        finish();
                                    } else {
                                        Toast.makeText(RegRendimento.this, "Rendimento não registado!", Toast.LENGTH_SHORT).show();
                                    }
                                } else if (checkUserFamVersion==true){
                                    long res = db.addRendimento(valorRend,data,cat_ID,contaID, designacaoRend);
                                    Boolean updateSaldo = db.somaRend(contaID,saldoConta,valorRend);
                                    updateContaOnServer(contaID, username);
                                    saveRendToServer(designacaoRend, valorRend, data, cat_ID, contaID, username);
                                    Toast.makeText(RegRendimento.this, "Rendimento registado com sucesso!", Toast.LENGTH_SHORT).show();
                                    Intent intentRend = new Intent(RegRendimento.this, MenuFuncionalidades.class);
                                    intentRend.putExtra(TEXT_DESP, desp);
                                    intentRend.putExtra(TEXT_DESP_ID, contaID);
                                    startActivity(intentRend);
                                    finish();
                                }
                            }

                        }
                    }
                }
            }
        });

    }

    private void updateContaOnServer(String contaID, String username) {
        Cursor userCode_cursor = db.getUserCode(username);
        while (userCode_cursor.moveToNext()) {
            final String userCode = userCode_cursor.getString(0);
            Cursor saldoConta_cursor = db.getSaldoContaID(contaID);
            while (saldoConta_cursor.moveToNext()) {
                final String saldo = saldoConta_cursor.getString(0);
                Cursor designacaoConta_cursor = db.getContaDestinoDesignacao(contaID);
                while (designacaoConta_cursor.moveToNext()) {
                    final String designacao = designacaoConta_cursor.getString(0);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPDATE_CONTA,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (!obj.getBoolean("error")) {
                                            Toast.makeText(RegRendimento.this, "Conta atualizada com sucesso!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(RegRendimento.this, "Conta não atualizada!", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(RegRendimento.this, "Sem ligação á Internet", Toast.LENGTH_SHORT).show();
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("designacao", designacao);
                            params.put("saldo", saldo);
                            params.put("userCode", userCode);
                            return params;
                        }
                    };

                    VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);

                }
            }
        }
    }

    private void saveRendToServer(final String designacaoRend, final String valorRend, final String data, final String cat_id, final String contaID, final String username) {
        Cursor userCode_cursor = db.getUserCode(username);
        while (userCode_cursor.moveToNext()) {
            final String userCode = userCode_cursor.getString(0);
            Cursor rendID_cursor = db.getLastRendID();
            while (rendID_cursor.moveToNext()){
                final String rendID = rendID_cursor.getString(0);
                Cursor catDesignacao_cursor = db.getDesignacaoCatRend(cat_id);
                while (catDesignacao_cursor.moveToNext()){
                    final String categoria = catDesignacao_cursor.getString(0);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_REND,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (!obj.getBoolean("error")) {
                                            Toast.makeText(RegRendimento.this, "Rendimentos guardados com sucesso!", Toast.LENGTH_SHORT).show();
                                            db.updateRendUserCodeStatus(userCode,SYNCED_WITH_SERVER,rendID);
                                        } else {
                                            Toast.makeText(RegRendimento.this, "Despesas não guardadas!", Toast.LENGTH_SHORT).show();
                                            db.updateRendUserCodeStatus(userCode,NOT_SYNCED_WITH_SERVER,rendID);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(RegRendimento.this, "Sem ligação á Internet", Toast.LENGTH_SHORT).show();
                                    db.updateRendUserCodeStatus(userCode,NOT_SYNCED_WITH_SERVER,rendID);
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("rendID_App", rendID);
                            params.put("designacao", designacaoRend);
                            params.put("valor", valorRend);
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

    }

    private void openDialog() {
        AdicionarCatDialog adicionarCatDialog = new AdicionarCatDialog();
        adicionarCatDialog.show(getSupportFragmentManager(), "Dialog");
    }

    @Override
    public void applyText(String designacaoCat) {
        Intent regDesp_intent = getIntent();
        final String contaID = regDesp_intent.getStringExtra(EXTRA_TEXT1);
        final String saldoConta = regDesp_intent.getStringExtra(EXTRA_TEXT2);

        Cursor cursorUserID = db.getUtilizadorID(contaID);
        while (cursorUserID.moveToNext()){
            String utilizadorID = cursorUserID.getString(0);
            Boolean catRendExiste = db.checkCatRendimentoExiste(designacaoCat, utilizadorID);
            if (catRendExiste==true){
                Toast.makeText(RegRendimento.this, "Já existe uma categoria com a designação: "+designacaoCat, Toast.LENGTH_LONG).show();
                openDialog();
            } else {
                long res = db.addCatRendimento(designacaoCat, utilizadorID);
                if (res > 0){
                    Toast.makeText(RegRendimento.this, "Nova Categoria adicionada com sucesso ", Toast.LENGTH_SHORT).show();
                    populateSpinner(utilizadorID);
                } else {
                    Toast.makeText(RegRendimento.this, "Nova categoria não adicionada", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void populateSpinner(String utilizadorID) {
        spinnerCategoriaRend = findViewById(R.id.sp_categoriaRend);
        List<String> listaCatRend = new ArrayList<>();
        listaCatRend.add("Selecione a Categoria...");
        Cursor categoriasRend = db.getDataCatRendimento(utilizadorID);
        if(categoriasRend.getCount()==0){
            Toast.makeText(RegRendimento.this, "Não existem categorias definidas", Toast.LENGTH_SHORT).show();
        }else {
            while (categoriasRend.moveToNext()){
                listaCatRend.add(categoriasRend.getString(1));
                final ArrayAdapter<String> adapter = new ArrayAdapter<String >(this, android.R.layout.simple_spinner_item, listaCatRend){
                    @Override
                    public boolean isEnabled(int position) {
                        if (position==0)
                        {
                            return false;
                        } else {
                            return true;
                        }
                    }

                    @Override
                    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView textView = (TextView) view;
                        if (position==0){
                            textView.setTextColor(Color.GRAY);
                        } else {
                            textView.setTextColor(Color.BLACK);
                        }
                        return view;
                    }
                };
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategoriaRend.setAdapter(adapter);
                spinnerCategoriaRend.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String categoriaSelecionada = parent.getItemAtPosition(position).toString();
                        if (categoriaSelecionada.equals("+ Adicionar Categoria")){
                            openDialog();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        }
        listaCatRend.add(listaCatRend.size(),"+ Adicionar Categoria");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent limiteDesp_intent = getIntent();
        final String contaID = limiteDesp_intent.getStringExtra(EXTRA_TEXT1);
        Cursor utilizadorID_cursor = db.getUtilizadorID(contaID);
        while (utilizadorID_cursor.moveToNext()){
            String utilizadorID = utilizadorID_cursor.getString(0);
            Cursor username_cursor = db.getUsername(utilizadorID);
            while (username_cursor.moveToNext()){
                String username = username_cursor.getString(0);
                switch (item.getItemId()){
                    case R.id.logoutItemOption:
                        Shared.logout(this);
                        Intent logout_intent = new Intent(RegRendimento.this, MainActivity.class);
                        startActivity(logout_intent);
                        finish();
                        return true;
                    case R.id.carteiraContasItem:
                        Intent carteira_intent = new Intent(RegRendimento.this, ContaFinanceira.class);
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
        Intent res_intent = getIntent();
        final String contaID = res_intent.getStringExtra(EXTRA_TEXT1);
        final String desp = "despesa";

        Intent intentResumos = new Intent(RegRendimento.this, MenuFuncionalidades.class);
        intentResumos.putExtra(TEXT_DESP, desp);
        intentResumos.putExtra(TEXT_DESP_ID, contaID);
        startActivity(intentResumos);
        finish();
    }


}
