package com.example.mysl_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "control0102";
    private static final String ipv4 = "192.168.0.6";

    private RequestQueue requestQueue;
    private EditText id, nombre, edad;
    private Button bt1, bt2, bt3, bt4, btLimpiar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        id = findViewById(R.id.id);
        nombre = findViewById(R.id.nombre);
        edad = findViewById(R.id.edad);
        bt1 = findViewById(R.id.bt1);
        bt2 = findViewById(R.id.bt2);
        bt3 = findViewById(R.id.bt3);
        bt4 = findViewById(R.id.bt4);
        btLimpiar = findViewById(R.id.btLimpiar);

        requestQueue = Volley.newRequestQueue(this);

        Toast.makeText(MainActivity.this, "Conexión exitosa a la base de datos", Toast.LENGTH_SHORT).show();

        leerDatos();

        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminar("http://" + ipv4 + "/ejemplo1/eliminar.php?id=" + id.getText().toString());
            }
        });

        bt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizar("http://" + ipv4 + "/ejemplo1/actualizar.php?id=" + id.getText().toString()
                        + "&nombre=" + nombre.getText().toString() + "&edad=" + edad.getText().toString());
            }
        });

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscar("http://" + ipv4 + "/ejemplo1/buscar.php?id=" + Integer.parseInt(id.getText().toString().trim()));
            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertar("http://" + ipv4 + "/ejemplo1/insertar.php");
            }
        });

        btLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpiarFormulario();
            }
        });
    }

    private void leerDatos() {
        String url = "http://" + ipv4 + "/ejemplo1/index.php";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);

                                int id = jsonObject.getInt("id");
                                String nombre = jsonObject.getString("nombre");
                                int edad = jsonObject.getInt("edad");

                                Log.d("Datos", "ID: " + id + ", Nombre: " + nombre + ", Edad: " + edad);
                                Toast.makeText(MainActivity.this, "Datos: " + "ID: " + id + ", Nombre: " + nombre + ", Edad: " + edad, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Error datos: " + e.toString());
                            Toast.makeText(MainActivity.this, "ERROR datos:" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error php: " + error.toString());
                        Toast.makeText(MainActivity.this, "ERROR php:" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    private void insertar(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "respuesta: " + response.toString(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "respuesta: " + response.toString());
                        leerDatos();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("nombre", nombre.getText().toString());
                parametros.put("edad", edad.getText().toString());
                return parametros;
            }
        };

        requestQueue.add(stringRequest);
        limpiarFormulario();
    }

    private void actualizar(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "respuesta: " + response.toString(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "respuesta: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                });

        requestQueue.add(stringRequest);
    }

    private void buscar(String url) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response.length() > 0) {
                                JSONObject jsonObject = response.getJSONObject(0);
                                String idres = jsonObject.getString("id");
                                String nombreres = jsonObject.getString("nombre");
                                String edadres = jsonObject.getString("edad");

                                id.setText(jsonObject.getString("id"));
                                nombre.setText(jsonObject.getString("nombre"));
                                edad.setText(jsonObject.getString("edad"));
                                Toast.makeText(MainActivity.this, "Búsqueda encontrada", Toast.LENGTH_SHORT).show();
                                Toast.makeText(MainActivity.this, "datos: " + idres + " nom:" + nombreres + " edad:" + edadres, Toast.LENGTH_SHORT).show();
                            } else {
                                limpiarFormulario();
                                Toast.makeText(MainActivity.this, "Búsqueda no encontrada", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error búsqueda: " + error.toString());
                        Toast.makeText(MainActivity.this, "ERROR búsqueda:" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    private void eliminar(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "respuesta: " + response.toString(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "respuesta: " + response.toString());
                        leerDatos();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                });

        requestQueue.add(stringRequest);
        limpiarFormulario();
    }

    private void limpiarFormulario() {
        id.setText("");
        nombre.setText("");
        edad.setText("");
    }
}
