package com.pdi.tye;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ContaFinanceira extends AppCompatActivity implements EditContaDialog.EditContaDialogListener{
    DataBaseHelper db;

    public static final String EXTRA_TEXT = "com.pdi.tye.EXTRA_TEXT";

    public static final String URL_DELETE_CONTA = "http://192.168.56.1/TYE/apagarConta.php";

    private long backPressedTime;

    ActionBar actionBar;
    RecyclerView rv_contas;
    String username;

    ArrayList<Model> modelArrayList;

    Adapter adapter;

    private ImageView image_logout;

    NetworkStateChecker receiver;

    private static final String TAP = "MyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conta_financeira);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_TEXT)){
            username = intent.getStringExtra(MainActivity.EXTRA_TEXT);
        } else {
            username = Shared.getUsername(this);
            Toast.makeText(ContaFinanceira.this, "Bem-vindo "+username, Toast.LENGTH_LONG).show();
        }

        if (receiver == null){
            receiver = new NetworkStateChecker();
            IntentFilter filter = new IntentFilter();
            filter.addAction(getResources().getString(R.string.action_connectivity_change));
            registerReceiver(receiver, filter);
            Log.e(TAP, "Ativou Conta Financeira");
        }




        actionBar = getSupportActionBar();
        actionBar.hide();

        image_logout = findViewById(R.id.image_logout);


        rv_contas = findViewById(R.id.rv_contas);

        modelArrayList = new ArrayList<>();

        showContas(null);


        FloatingActionButton fab = findViewById(R.id.fbtn_add);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nova = new Intent(ContaFinanceira.this, CriarContaFin.class);
                nova.putExtra(EXTRA_TEXT, username);
                startActivity(nova);
                finish();
            }
        });

        if(modelArrayList.size()!=0){
            adapter.setOnItemClickedListener(new Adapter.OnItemClickedListener() {
                @Override
                public void onItemClick(int position) {
                    Intent intent = new Intent(ContaFinanceira.this, MenuFuncionalidades.class);
                    intent.putExtra("ID", modelArrayList.get(position));
                    startActivity(intent);
                    finish();
                }
            });

        }

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rv_contas);
        ItemTouchHelper itemTouchHelper2 = new ItemTouchHelper(simpleCallback2);
        itemTouchHelper2.attachToRecyclerView(rv_contas);

        image_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Shared.logout(ContaFinanceira.this);
                Intent logout_intent = new Intent(ContaFinanceira.this, MainActivity.class);
                startActivity(logout_intent);
                finish();
            }
        });


    }

    @Override
    protected void onPause() {
        if (receiver != null){
            unregisterReceiver(receiver);
            receiver = null;
            Log.e(TAP, "Desativou onPause ContaFinanceira");
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (receiver != null){
            unregisterReceiver(receiver);
            receiver = null;
            Log.e(TAP, "Desativou onDestroy ContaFinanceira");
        }
        super.onDestroy();
    }



    public void showContas(View view)
    {
        try {
            DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
            SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();
            if (sqLiteDatabase!=null){
                Cursor cursorID = dataBaseHelper.getID(username);
                while (cursorID.moveToNext()){
                    String utilizadorID = cursorID.getString(0);
                    Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM conta WHERE utilizadorID=?", new String[] {utilizadorID});
                    if (cursor.getCount()==0) {
                        Toast.makeText(this, "Não existem contas criadas", Toast.LENGTH_SHORT).show();
                    }else {
                        while (cursor.moveToNext()) {
                            modelArrayList.add(new Model(cursor.getString(0),
                                    cursor.getString(1),
                                    cursor.getString(2)));
                        }
                        adapter = new Adapter(this, modelArrayList);
                        rv_contas.hasFixedSize();
                        rv_contas.setLayoutManager(new LinearLayoutManager(this));
                        rv_contas.setAdapter(adapter);
                    }
                }
            }else {
                Toast.makeText(this, "Base Dados null", Toast.LENGTH_SHORT).show();
            }
            dataBaseHelper.close();
        } catch (Exception e){
            Toast.makeText(this, "ShowValuesFromDB: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    Model contaApagada = null;

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            db = new DataBaseHelper(getApplicationContext());
            final int position = viewHolder.getAdapterPosition();
            if (direction == ItemTouchHelper.LEFT){
                contaApagada = modelArrayList.get(position);
                modelArrayList.remove(position);
                adapter.notifyItemRemoved(position);
                final AlertDialog.Builder builder = new AlertDialog.Builder(ContaFinanceira.this);
                builder.setTitle("AVISO");
                builder.setMessage("Tem a certeza que pretende eliminar permanentemente a conta: "+contaApagada.getDesignacao()+"?");
                builder.setCancelable(true);
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Cursor utilizadorID_cursor = db.getUtilizadorID(contaApagada.getId());
                        while (utilizadorID_cursor.moveToNext()){
                            String utilizadorID = utilizadorID_cursor.getString(0);
                            Cursor username_cursor = db.getUsername(utilizadorID);
                            while (username_cursor.moveToNext()){
                                String username = username_cursor.getString(0);
                                Boolean userFamVersion = db.checkUserFamVersion(username);
                                if (userFamVersion == true){
                                    Cursor userCode_cursor = db.getUserCodeFromConta(contaApagada.getId());
                                    while (userCode_cursor.moveToNext()){
                                        String userCode = userCode_cursor.getString(0);
                                        deleteFromServer(contaApagada.getId(), contaApagada.getDesignacao(), userCode);
                                    }
                                    int res = db.deleteContaFin(contaApagada.getId());
                                    int res1 = db.deleteDespesaConta(contaApagada.getId());
                                    int res2 = db.deleteRendimentoConta(contaApagada.getId());
                                    int res3 = db.deleteExcDespesaConta(contaApagada.getId());
                                    int res4 = db.deleteTransferenciaConta(contaApagada.getId());

                                    if (res > 0){
                                        Toast.makeText(ContaFinanceira.this, "Conta eliminada258 com sucesso", Toast.LENGTH_SHORT).show();
                                    }
                                } else if (userFamVersion == false){
                                    int res = db.deleteContaFin(contaApagada.getId());
                                    int res1 = db.deleteDespesaConta(contaApagada.getId());
                                    int res2 = db.deleteRendimentoConta(contaApagada.getId());
                                    int res3 = db.deleteExcDespesaConta(contaApagada.getId());
                                    int res4 = db.deleteTransferenciaConta(contaApagada.getId());
                                    if (res > 0){
                                        Toast.makeText(ContaFinanceira.this, "Conta eliminada com sucesso", Toast.LENGTH_SHORT).show();
                                    }


                                }
                            }
                        }

                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        modelArrayList.add(position, contaApagada);
                        adapter.notifyItemInserted(position);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }

        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(ContaFinanceira.this, R.color.colorAccent))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_black_24dp)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(ContaFinanceira.this, R.color.colorPrimaryDark))
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    Model contaEdit = null;

    ItemTouchHelper.SimpleCallback simpleCallback2 = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            db = new DataBaseHelper(getApplicationContext());
            final int position = viewHolder.getAdapterPosition();
            if (direction == ItemTouchHelper.RIGHT){
                contaEdit = modelArrayList.get(position);
                openDialog(contaEdit.getId(), contaEdit.getDesignacao(), contaEdit.getSaldo());

            }

        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(ContaFinanceira.this, R.color.colorAccent))
                    .addSwipeRightActionIcon(R.drawable.ic_mode_edit_black_24dp)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(ContaFinanceira.this, R.color.colorPrimaryDark))
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void openDialog(String contaID, String designacaoConta, String saldoConta) {
        EditContaDialog editContaDialog = new EditContaDialog();
        editContaDialog = EditContaDialog.newInstance(contaID, designacaoConta, saldoConta);
        editContaDialog.show(getSupportFragmentManager(), "Dialog");
    }

    public void applyTexts(String contaID, String designacaoConta, String saldoConta){
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_TEXT)){
            username = intent.getStringExtra(MainActivity.EXTRA_TEXT);
        } else {
            username = Shared.getUsername(this);
            Toast.makeText(ContaFinanceira.this, "Bem-vindo "+username, Toast.LENGTH_LONG).show();
        }
        if (contaID.equals("Cancelar")){
            Intent intentback = new Intent(ContaFinanceira.this, ContaFinanceira.class);
            intentback.putExtra(EXTRA_TEXT, username);
            startActivity(intentback);
            finish();
        } else {
            long res = db.updateInfoConta(contaID, designacaoConta, saldoConta);
            if (res > 0){

                Toast.makeText(ContaFinanceira.this, "Informação da conta alterada com sucesso!",Toast.LENGTH_SHORT).show();
                Intent intentback = new Intent(ContaFinanceira.this, ContaFinanceira.class);
                intentback.putExtra(EXTRA_TEXT, username);
                startActivity(intentback);
                finish();
            } else {
                Toast.makeText(ContaFinanceira.this, "Informação da conta não alterada!",Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void deleteFromServer(final String contaID, final String designacao, final String userCode) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DELETE_CONTA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                Toast.makeText(ContaFinanceira.this, "Contas apagada com sucesso!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ContaFinanceira.this, "Contas não guardadas!", Toast.LENGTH_SHORT).show();
                                db.addContaApagada(contaID,designacao,FamVersion.NOT_SYNCED_WITH_SERVER,userCode);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ContaFinanceira.this, "Sem ligação á Internet", Toast.LENGTH_SHORT).show();
                        db.addContaApagada(contaID,designacao,FamVersion.NOT_SYNCED_WITH_SERVER,userCode);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("designacao", designacao);
                params.put("userCode", userCode);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()){
            super.onBackPressed();
        } else {
            Toast.makeText(ContaFinanceira.this, "Pressione Voltar novamente para sair", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}
