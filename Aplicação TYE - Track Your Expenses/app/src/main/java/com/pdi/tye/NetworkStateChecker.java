package com.pdi.tye;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NetworkStateChecker extends BroadcastReceiver {
    private Context context;
    DataBaseHelper db;

    private static final String TAG = "MyActivity";

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        db = new DataBaseHelper(context);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnected()) {
            Log.e(TAG, "Ativado");
            Cursor contasUnsync_cursor = db.getUnsyncContas();
            while (contasUnsync_cursor.moveToNext()){
                saveContas(
                        contasUnsync_cursor.getString(0),
                        contasUnsync_cursor.getString(1),
                        contasUnsync_cursor.getString(2),
                        contasUnsync_cursor.getString(3)
                );
            }
            Cursor despUnsync_cursor = db.getUnsyncDesp();
            while (despUnsync_cursor.moveToNext()){
                saveDesp(
                        despUnsync_cursor.getString(0),
                        despUnsync_cursor.getString(1),
                        despUnsync_cursor.getString(2),
                        despUnsync_cursor.getString(3),
                        despUnsync_cursor.getString(4),
                        despUnsync_cursor.getString(5)

                );
            }
            Cursor rendUnsync_cursor = db.getUnsyncRend();
            while (rendUnsync_cursor.moveToNext()){
                saveRend(
                        rendUnsync_cursor.getString(0),
                        rendUnsync_cursor.getString(1),
                        rendUnsync_cursor.getString(2),
                        rendUnsync_cursor.getString(3),
                        rendUnsync_cursor.getString(4),
                        rendUnsync_cursor.getString(5)
                );
            }
            Cursor contasEliminadasUnsync_cursor = db.getUnsyncContasEliminadas();
            while (contasEliminadasUnsync_cursor.moveToNext()){
                deleteContaOnServer(
                        contasEliminadasUnsync_cursor.getString(0),
                        contasEliminadasUnsync_cursor.getString(1),
                        contasEliminadasUnsync_cursor.getString(2)
                );
            }
            Cursor despesasEliminadasUnsync_cursor = db.getUnsyncDespesasEliminadas();
            while (despesasEliminadasUnsync_cursor.moveToNext()){
                deleteDespesasOnServer(
                        despesasEliminadasUnsync_cursor.getString(0),
                        despesasEliminadasUnsync_cursor.getString(1),
                        despesasEliminadasUnsync_cursor.getString(2),
                        despesasEliminadasUnsync_cursor.getString(3)
                );
            }
            Cursor rendimentosEliminadosUnsync_cursor = db.getUnsyncRendimentosEliminados();
            while (rendimentosEliminadosUnsync_cursor.moveToNext()){
                deleteRendimentosOnServer(
                        rendimentosEliminadosUnsync_cursor.getString(0),
                        rendimentosEliminadosUnsync_cursor.getString(1),
                        rendimentosEliminadosUnsync_cursor.getString(2),
                        rendimentosEliminadosUnsync_cursor.getString(3)
                );
            }
            Cursor contasUpdate_cursor = db.getInfoContas();
            while (contasUpdate_cursor.moveToNext()){
                updateAllContasOnServer(
                    contasUpdate_cursor.getString(0),
                    contasUpdate_cursor.getString(1),
                    contasUpdate_cursor.getString(2)
                );
            }

        }
    }

    private void updateAllContasOnServer(final String designacao, final String saldo, final String userCode) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Resumos.URL_UPDATE_CONTA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);

    }

    private void deleteRendimentosOnServer(final String id, String contaID, final String rendID_App, final String userCode) {
        //updateContaOnServer(contaID, userCode);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Resumos.URL_DELETE_REND,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                               db.deleteRendimentoEliminado(id);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("rendID_App", rendID_App);
                params.put("userCode", userCode);
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    private void deleteDespesasOnServer(final String id, final String contaID, final String despID_App, final String userCode) {
        //updateContaOnServer(contaID, userCode);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Resumos.URL_DELETE_DESP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                               db.deleteDespesasEliminada(id);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("despID_App", despID_App);
                params.put("userCode", userCode);
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    private void updateContaOnServer(String contaID, final String userCode) {
        Cursor saldoConta_cursor = db.getSaldoContaID(contaID);
        while (saldoConta_cursor.moveToNext()) {
            final String saldo = saldoConta_cursor.getString(0);
            Cursor designacaoConta_cursor = db.getContaDestinoDesignacao(contaID);
            while (designacaoConta_cursor.moveToNext()) {
                final String designacao = designacaoConta_cursor.getString(0);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Resumos.URL_UPDATE_CONTA,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    if (!obj.getBoolean("error")) {

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
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

                VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
            }
        }
    }

    private void deleteContaOnServer(final String id, final String designacao, final String userCode) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ContaFinanceira.URL_DELETE_CONTA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                db.deleteContaEliminada(id);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    private void saveRend(final String rendID, final String designacao, final String valor, final String data, final String catRendID, final String userCode) {
        Cursor username_cursor = db.getUsernameFromCode(userCode);
        while(username_cursor.moveToNext()){
            final String username = username_cursor.getString(0);
            Cursor catDesignacao_cursor = db.getDesignacaoCatRend(catRendID);
            while (catDesignacao_cursor.moveToNext()){
                final String categoria = catDesignacao_cursor.getString(0);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, RegRendimento.URL_SAVE_REND,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    if (!obj.getBoolean("error")) {
                                        db.updateRendSyncStatus(rendID, RegRendimento.SYNCED_WITH_SERVER);
                                        context.sendBroadcast(new Intent(RegRendimento.DATA_SAVED_BROADCAST));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
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

                VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
            }
        }


    }

    private void saveDesp(final String despID, final String designacao, final String valor, final String data, final String catID, final String userCode) {
        Cursor username_cursor = db.getUsernameFromCode(userCode);
        while(username_cursor.moveToNext()) {
            final String username = username_cursor.getString(0);
            Cursor catDesignacao_cursor = db.getDesignacaoCat(catID);
            while (catDesignacao_cursor.moveToNext()){
                final String categoria = catDesignacao_cursor.getString(0);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, RegDespesa.URL_SAVE_DESP,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    if (!obj.getBoolean("error")) {
                                        db.updateDespSyncStatus(despID, RegDespesa.SYNCED_WITH_SERVER);
                                        context.sendBroadcast(new Intent(RegDespesa.DATA_SAVED_BROADCAST));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
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

                VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
            }
        }



    }


    private void saveContas(final String contaID, final String designacao, final String saldo, final String userCode) {
        Cursor username_cursor = db.getUsernameFromCode(userCode);
        while(username_cursor.moveToNext()) {
            final String username = username_cursor.getString(0);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, FamVersion.URL_SAVE_CONTA,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject obj = new JSONObject(response);
                                if (!obj.getBoolean("error")) {
                                    db.updateContaSyncStatus(contaID,FamVersion.SYNCED_WITH_SERVER);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
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

            VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
        }


    }

    private void saveUser(final String id, final String username, final String userNatur, final String codigo) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, FamVersion.URL_SAVE_CONTA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                db.updateUserFVStatus(id, FamVersion.SYNCED_WITH_SERVER);
                                context.sendBroadcast(new Intent(FamVersion.DATA_SAVED_BROADCAST));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("userNatur", userNatur);
                params.put("codigo", codigo);
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }
}
