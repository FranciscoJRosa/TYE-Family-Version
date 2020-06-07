package com.pdi.tye;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.ULocale;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.BoringLayout;
import android.view.Display;
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
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegDespesa extends AppCompatActivity implements AdicionarCatDialog.AdicionarCatDialogListener{
    public static final String EXTRA_TEXT = "com.pdi.tye.EXTRA_TEXT";
    public static final String EXTRA_TEXT1 = "com.pdi.tye.EXTRA_TEXT1";
    public static final String EXTRA_TEXT2 = "com.pdi.tye.EXTRA_TEXT2";
    public static final String TEXT_DESP = "com.pdi.tye.TEXT_DESP";
    public static final String TEXT_DESP_ID = "com.pdi.tye.TEXT_DESP";

    public static final String URL_SAVE_DESP = "http://192.168.56.1/TYE/registoDespesas.php";
    public static final String URL_UPDATE_CONTA = "http://192.168.56.1/TYE/updateConta.php";

    public static final String SYNCED_WITH_SERVER = "1";
    public static final String NOT_SYNCED_WITH_SERVER = "0";

    public static final String DATA_SAVED_BROADCAST = "com.pdi.sync.datasaved";



    DataBaseHelper db;



    private static final String TAG = "RegDespesa";
    private TextView txt_DisplayDate;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private Button btn_registarDesp;
    private EditText et_valor;
    private EditText et_designacaoDespesa;


    private Spinner spinnerCategoria;

    ConstraintLayout mainLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_despesa);

        db = new DataBaseHelper(this);


        Intent regDesp_intent = getIntent();
        final String contaID = regDesp_intent.getStringExtra(EXTRA_TEXT1);
        final String saldoConta = regDesp_intent.getStringExtra(EXTRA_TEXT2);

        final String desp = "despesa";

        mainLayout = findViewById(R.id.mainLayout);

        getSupportActionBar().setElevation(0);




        et_valor = findViewById(R.id.et_valor);
        btn_registarDesp = findViewById(R.id.btn_registarDespesa);

        txt_DisplayDate = (TextView) findViewById(R.id.txt_data);
        et_designacaoDespesa = findViewById(R.id.et_designaçãoDespesa);

        spinnerCategoria = findViewById(R.id.spn_categoria);

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


                DatePickerDialog dialog = new DatePickerDialog(RegDespesa.this, android.R.style.Theme_Holo_Dialog_MinWidth, dateSetListener,
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

        btn_registarDesp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valor = et_valor.getText().toString().trim();
                String categoria = spinnerCategoria.getSelectedItem().toString();
                String data = txt_DisplayDate.getText().toString().trim();
                String designacaoDespesa = et_designacaoDespesa.getText().toString().trim();
                if (data.isEmpty() || designacaoDespesa.isEmpty() || valor.isEmpty() || categoria.equals("+ Adicionar Categoria") || categoria.equals("Selecione a Categoria...") ){
                    Toast.makeText(RegDespesa.this, "Campos por preencher!", Toast.LENGTH_SHORT).show();
                } else {
                    Cursor utilizadorID_cursor = db.getUtilizadorID(contaID);
                    while (utilizadorID_cursor.moveToNext()){
                        String utilizadorID = utilizadorID_cursor.getString(0);
                        Cursor cursor = db.getCatID(categoria, utilizadorID);
                        while (cursor.moveToNext()) {
                            String cat_ID = cursor.getString(0);
                            Cursor username_cursor = db.getUsername(utilizadorID);
                            while (username_cursor.moveToNext()) {
                                String username = username_cursor.getString(0);
                                Boolean userFamVersion = db.checkUserFamVersion(username);
                                if (userFamVersion==false){
                                    long res = db.addDespesa(valor, data, cat_ID, contaID, designacaoDespesa);
                                    Boolean updateSaldo = db.SubtDespesa(contaID, saldoConta, valor);
                                    Cursor catLimiteDesp_cursor = db.checkCatLimiteDesp(contaID, data);
                                    if (catLimiteDesp_cursor.getCount() > 0) {
                                        while (catLimiteDesp_cursor.moveToNext()) {
                                            String catLimite = catLimiteDesp_cursor.getString(0);
                                            Cursor designacaoCatLimite_Cursor = db.getDesignacaoCat(catLimite);
                                            while (designacaoCatLimite_Cursor.moveToNext()){
                                                String designacaoCatLimite = designacaoCatLimite_Cursor.getString(0);
                                                if (designacaoCatLimite.equals("Todas")) {
                                                    Cursor limiteDesp_cursor = db.getLimitesDesp(contaID, catLimite, data);
                                                    while (limiteDesp_cursor.moveToNext()) {
                                                        String limiteDesp = limiteDesp_cursor.getString(0);
                                                        Cursor limiteDespID_cursor = db.getLimiteID(contaID, catLimite, limiteDesp, data);
                                                        while (limiteDespID_cursor.moveToNext()) {
                                                            String limiteDespID = limiteDespID_cursor.getString(0);
                                                            Cursor datasLimiteDesp_cursor = db.getDatasLimite(limiteDespID);
                                                            while (datasLimiteDesp_cursor.moveToNext()) {
                                                                String dataInicioLimite = datasLimiteDesp_cursor.getString(0);
                                                                String dataFimLimite = datasLimiteDesp_cursor.getString(1);
                                                                Cursor somaDespesasLimite_cursor = db.getTotalDespesasLimite(contaID, dataInicioLimite, dataFimLimite);
                                                                while (somaDespesasLimite_cursor.moveToNext()) {
                                                                    String somaDespesasLimite = somaDespesasLimite_cursor.getString(0);
                                                                    if (Double.valueOf(somaDespesasLimite) >= Double.valueOf(limiteDesp)) {
                                                                        Cursor designacaoCat_cursor = db.getDesignacaoCat(catLimite);
                                                                        while (designacaoCat_cursor.moveToNext()) {
                                                                            String catLimiteDesignacao = designacaoCat_cursor.getString(0);
                                                                            Snackbar.make(mainLayout, "Limite de despesas de " + limiteDesp + "€ na categoria " + catLimiteDesignacao + " atingido!", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View v) {
                                                                                    intent(desp, contaID);
                                                                                }
                                                                            }).setActionTextColor(getResources().getColor(R.color.LightBlue)).show();
                                                                        }
                                                                    } else {
                                                                        intent(desp, contaID);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    Cursor limiteDesp_cursor = db.getLimitesDesp(contaID, cat_ID, data);
                                                    if (limiteDesp_cursor.getCount() >0){
                                                        while (limiteDesp_cursor.moveToNext()) {
                                                            String limiteDesp = limiteDesp_cursor.getString(0);
                                                            Cursor limiteDespID_cursor = db.getLimiteID(contaID, catLimite, limiteDesp, data);
                                                            while (limiteDespID_cursor.moveToNext()) {
                                                                String limiteDespID = limiteDespID_cursor.getString(0);
                                                                Cursor datasLimiteDesp_cursor = db.getDatasLimite(limiteDespID);
                                                                while (datasLimiteDesp_cursor.moveToNext()) {
                                                                    String dataInicioLimite = datasLimiteDesp_cursor.getString(0);
                                                                    String dataFimLimite = datasLimiteDesp_cursor.getString(1);
                                                                    Cursor somaDespesasLimiteCat_cursor = db.getTotalDespesasLimiteCat(contaID, catLimite, dataInicioLimite, dataFimLimite);
                                                                    while (somaDespesasLimiteCat_cursor.moveToNext()) {
                                                                        String somaDespesasLimiteCat = somaDespesasLimiteCat_cursor.getString(0);
                                                                        if (Double.valueOf(somaDespesasLimiteCat) >= Double.valueOf(limiteDesp)){
                                                                            Cursor designacaoCat_cursor = db.getDesignacaoCat(catLimite);
                                                                            while (designacaoCat_cursor.moveToNext()){
                                                                                String catLimiteDesignacao = designacaoCat_cursor.getString(0);
                                                                                Snackbar.make(mainLayout, "Limite de despesas de "+limiteDesp+"€ na categoria "+catLimiteDesignacao+" atingido!", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(View v) {
                                                                                        intent(desp, contaID);
                                                                                    }
                                                                                }).setActionTextColor(getResources().getColor(R.color.LightBlue)).show();
                                                                            }
                                                                        } else {
                                                                            intent(desp, contaID);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        intent(desp, contaID);
                                                    }

                                                }

                                            }

                                        }
                                    } else if (res > 0){
                                        intent(desp, contaID);
                                    }
                                } else if (userFamVersion==true){
                                    long res = db.addDespesa(valor, data, cat_ID, contaID, designacaoDespesa);
                                    Boolean updateSaldo = db.SubtDespesa(contaID, saldoConta, valor);
                                    updateContaOnServer(contaID, username);
                                    saveDespesaToServer(valor, data, cat_ID, contaID, designacaoDespesa, username);
                                    Cursor catLimiteDesp_cursor = db.checkCatLimiteDesp(contaID, data);
                                    if (catLimiteDesp_cursor.getCount() > 0) {
                                        while (catLimiteDesp_cursor.moveToNext()) {
                                            String catLimite = catLimiteDesp_cursor.getString(0);
                                            Cursor designacaoCatLimite_cursor = db.getDesignacaoCat(catLimite);
                                            while (designacaoCatLimite_cursor.moveToNext()){
                                                String designacaoCatLimite = designacaoCatLimite_cursor.getString(0);
                                                if (designacaoCatLimite.equals("Todas")) {
                                                    Cursor limiteDesp_cursor = db.getLimitesDesp(contaID, catLimite, data);
                                                    while (limiteDesp_cursor.moveToNext()) {
                                                        String limiteDesp = limiteDesp_cursor.getString(0);
                                                        Cursor limiteDespID_cursor = db.getLimiteID(contaID, catLimite, limiteDesp, data);
                                                        while (limiteDespID_cursor.moveToNext()) {
                                                            String limiteDespID = limiteDespID_cursor.getString(0);
                                                            Cursor datasLimiteDesp_cursor = db.getDatasLimite(limiteDespID);
                                                            while (datasLimiteDesp_cursor.moveToNext()) {
                                                                String dataInicioLimite = datasLimiteDesp_cursor.getString(0);
                                                                String dataFimLimite = datasLimiteDesp_cursor.getString(1);
                                                                Cursor somaDespesasLimite_cursor = db.getTotalDespesasLimite(contaID, dataInicioLimite, dataFimLimite);
                                                                while (somaDespesasLimite_cursor.moveToNext()) {
                                                                    String somaDespesasLimite = somaDespesasLimite_cursor.getString(0);
                                                                    if (Double.valueOf(somaDespesasLimite) >= Double.valueOf(limiteDesp)) {
                                                                        Cursor designacaoCat_cursor = db.getDesignacaoCat(catLimite);
                                                                        while (designacaoCat_cursor.moveToNext()) {
                                                                            String catLimiteDesignacao = designacaoCat_cursor.getString(0);
                                                                            Snackbar.make(mainLayout, "Limite de despesas de " + limiteDesp + "€ na categoria " + catLimiteDesignacao + " atingido!", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View v) {
                                                                                    intent(desp, contaID);
                                                                                }
                                                                            }).setActionTextColor(getResources().getColor(R.color.LightBlue)).show();
                                                                        }
                                                                    } else {
                                                                        intent(desp, contaID);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    Cursor limiteDesp_cursor = db.getLimitesDesp(contaID, cat_ID, data);
                                                    if (limiteDesp_cursor.getCount() >0){
                                                        while (limiteDesp_cursor.moveToNext()) {
                                                            String limiteDesp = limiteDesp_cursor.getString(0);
                                                            Cursor limiteDespID_cursor = db.getLimiteID(contaID, catLimite, limiteDesp, data);
                                                            while (limiteDespID_cursor.moveToNext()) {
                                                                String limiteDespID = limiteDespID_cursor.getString(0);
                                                                Cursor datasLimiteDesp_cursor = db.getDatasLimite(limiteDespID);
                                                                while (datasLimiteDesp_cursor.moveToNext()) {
                                                                    String dataInicioLimite = datasLimiteDesp_cursor.getString(0);
                                                                    String dataFimLimite = datasLimiteDesp_cursor.getString(1);
                                                                    Cursor somaDespesasLimiteCat_cursor = db.getTotalDespesasLimiteCat(contaID, catLimite, dataInicioLimite, dataFimLimite);
                                                                    while (somaDespesasLimiteCat_cursor.moveToNext()) {
                                                                        String somaDespesasLimiteCat = somaDespesasLimiteCat_cursor.getString(0);
                                                                        if (Double.valueOf(somaDespesasLimiteCat) >= Double.valueOf(limiteDesp)){
                                                                            Cursor designacaoCat_cursor = db.getDesignacaoCat(catLimite);
                                                                            while (designacaoCat_cursor.moveToNext()){
                                                                                String catLimiteDesignacao = designacaoCat_cursor.getString(0);
                                                                                Snackbar.make(mainLayout, "Limite de despesas de "+limiteDesp+"€ na categoria "+catLimiteDesignacao+" atingido!", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(View v) {
                                                                                        intent(desp, contaID);
                                                                                    }
                                                                                }).setActionTextColor(getResources().getColor(R.color.LightBlue)).show();
                                                                            }
                                                                        } else {
                                                                            intent(desp, contaID);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        intent(desp, contaID);
                                                    }

                                                }
                                            }
                                        }
                                    } else {
                                        intent(desp, contaID);
                                    }
                                }
                            }

                        }
                    }
                }
            }
        });
    }

    private void populateSpinner(String utilizadorID) {
        spinnerCategoria = findViewById(R.id.spn_categoria);
        final List<String> listaCat = new ArrayList<>();
        listaCat.add("Selecione a Categoria...");
        Cursor categorias = db.getDataCatDespesa(utilizadorID);
        if(categorias.getCount()==0){
            Toast.makeText(RegDespesa.this, "Não existem categorias definidas", Toast.LENGTH_SHORT).show();
        }else {
            while (categorias.moveToNext()){
                listaCat.add(categorias.getString(1));
                listaCat.remove("Todas");
                final ArrayAdapter<String> adapter = new ArrayAdapter<String >(this, android.R.layout.simple_spinner_item, listaCat){
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
                spinnerCategoria.setAdapter(adapter);
                spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        listaCat.add(listaCat.size(),"+ Adicionar Categoria");
    }

    private void updateContaOnServer(String contaID, String username) {
        Cursor userCode_cursor = db.getUserCode(username);
        while (userCode_cursor.moveToNext()){
            final String userCode = userCode_cursor.getString(0);
            Cursor saldoConta_cursor = db.getSaldoContaID(contaID);
            while (saldoConta_cursor.moveToNext()){
                final String saldo = saldoConta_cursor.getString(0);
                Cursor designacaoConta_cursor = db.getContaDestinoDesignacao(contaID);
                while (designacaoConta_cursor.moveToNext()){
                    final String designacao = designacaoConta_cursor.getString(0);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPDATE_CONTA,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (!obj.getBoolean("error")) {
                                            Toast.makeText(RegDespesa.this, "Conta atualizada com sucesso!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(RegDespesa.this, "Conta não atualizada!", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(RegDespesa.this, "Sem ligação á Internet", Toast.LENGTH_SHORT).show();
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

    private void saveDespesaToServer(final String valor, final String data, final String cat_id, final String contaID, final String designacaoDespesa, final String username) {
        Cursor userCode_cursor = db.getUserCode(username);
        while (userCode_cursor.moveToNext()){
            final String userCode = userCode_cursor.getString(0);
            Cursor despID_cursor = db.getLastDespID();
            while (despID_cursor.moveToNext()){
                final String despID = despID_cursor.getString(0);
                Cursor catDesignacao_cursor = db.getDesignacaoCat(cat_id);
                while (catDesignacao_cursor.moveToNext()){
                    final String categoria = catDesignacao_cursor.getString(0);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_DESP,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (!obj.getBoolean("error")) {
                                            Toast.makeText(RegDespesa.this, "Despesas guardadas com sucesso!", Toast.LENGTH_SHORT).show();
                                            db.updateDespesaUserCodeStatus(userCode,SYNCED_WITH_SERVER,despID);
                                        } else {
                                            Toast.makeText(RegDespesa.this, "Despesas não guardadas!", Toast.LENGTH_SHORT).show();
                                            db.updateDespesaUserCodeStatus(userCode,NOT_SYNCED_WITH_SERVER,despID);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(RegDespesa.this, "Sem ligação á Internet", Toast.LENGTH_SHORT).show();
                                    db.updateDespesaUserCodeStatus(userCode,NOT_SYNCED_WITH_SERVER,despID);
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("despID_App", despID);
                            params.put("designacao", designacaoDespesa);
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
            Boolean catExiste = db.checkCatDespesaExiste(designacaoCat, utilizadorID);
            if (catExiste==true){
                Toast.makeText(RegDespesa.this, "Já existe uma categoria com a designação: "+designacaoCat, Toast.LENGTH_LONG).show();
                openDialog();
            } else {
                long res = db.AddCatDespesa(designacaoCat, utilizadorID);
                if (res > 0){
                    Toast.makeText(RegDespesa.this, "Nova Categoria adicionada com sucesso ", Toast.LENGTH_SHORT).show();
                    populateSpinner(utilizadorID);
                } else {
                    Toast.makeText(RegDespesa.this, "Nova categoria não adicionada", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }


    private void intent(String desp, String contaID){
            Toast.makeText(RegDespesa.this, "Despesa registada com sucesso!", Toast.LENGTH_LONG).show();
            Intent intentDesp = new Intent(RegDespesa.this, MenuFuncionalidades.class);
            intentDesp.putExtra(TEXT_DESP, desp);
            intentDesp.putExtra(TEXT_DESP_ID, contaID);
            startActivity(intentDesp);
            finish();
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
                        Intent logout_intent = new Intent(RegDespesa.this, MainActivity.class);
                        startActivity(logout_intent);
                        finish();
                        return true;
                    case R.id.carteiraContasItem:
                        Intent carteira_intent = new Intent(RegDespesa.this, ContaFinanceira.class);
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

        Intent intentResumos = new Intent(RegDespesa.this, MenuFuncionalidades.class);
        intentResumos.putExtra(TEXT_DESP, desp);
        intentResumos.putExtra(TEXT_DESP_ID, contaID);
        startActivity(intentResumos);
        finish();
    }




}



