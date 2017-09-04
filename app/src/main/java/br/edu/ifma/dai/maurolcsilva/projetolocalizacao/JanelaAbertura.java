package br.edu.ifma.dai.maurolcsilva.projetolocalizacao;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JanelaAbertura extends Activity implements LocationListener,View.OnClickListener{
    private TextView lblLatituderesposta;
    private TextView lblLongituderesposta;
    private TextView lblEnderecoresposta;
    private Button btnBuscarEndereco;
    private Button btnMostrarMapa;
    @SuppressWarnings("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_janela_abertura);
        lblLatituderesposta = (TextView) findViewById(R.id.lblResultadoLatitude);
        lblLongituderesposta = (TextView) findViewById(R.id.lblResultadoLongitude);
        lblEnderecoresposta = (TextView) findViewById(R.id.lblEndereco);
        btnBuscarEndereco = (Button) findViewById(R.id.btnBuscarEndereco);
        btnMostrarMapa = (Button) findViewById(R.id.btnMostrarMapa);
        btnBuscarEndereco.setOnClickListener(this);
        btnMostrarMapa.setOnClickListener(this);
        //Criacao do objeto de Servico de Localizacao
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //Configuracao
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,3000,0,this);
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        lblLatituderesposta.setText(String.valueOf(latitude));
        lblLongituderesposta.setText(String.valueOf(longitude));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onClick(View v) {
        String latitude,longitude;

        latitude = lblLatituderesposta.getText().toString();
        longitude = lblLongituderesposta.getText().toString();

        if (v.getId() == R.id.btnBuscarEndereco){
            new BuscaEndereco().execute(latitude,longitude);
        }
        else {
            Intent it = new Intent(this,JanelaMapa.class);
            Bundle params = new Bundle();
            params.putString("latitude",latitude);
            params.putString("longitude",longitude);
            it.putExtras(params);
            startActivity(it);
        }


    }

    private class BuscaEndereco extends AsyncTask<String, Integer,String>{

        @Override
        protected String doInBackground(String... params) {
            String latitude = params[0];
            String longitude = params[1];
            //String latitude = "-2.5375443";
            //String longitude = "-44.2771319";

            String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng=";
            url = url + latitude + "," + longitude;
            //Irá guardar o resultado da requisição do servico (um conteudo em JSON)
            String resultado = null;
            //Irá guardar o endereco obtido a partir da requisição
            String endereco = null;

            try {
                //Cria uma url de servico para que possamos usar a requisicao
                URL urlservico = new URL(url);
                //Abre uma conexao
                HttpURLConnection conexao = (HttpURLConnection) urlservico.openConnection();
                //Define qual o verbo HTTP da requisicao
                conexao.setRequestMethod("GET");
                //Dispara a chamada
                conexao.connect();
                //Verifica qual o retorno desta chamada
                if(conexao.getResponseCode()==HttpURLConnection.HTTP_OK){
                    //Deu tudo certo e o retorno da chamada foi 200
                    //StringBuilder ideal para concatenações
                    //Cada mudança de conteúdo de uma String, um novo objeto é criado
                    StringBuilder sb = new StringBuilder();
                    //InputStreamReader leitura em caracter a caracter ou byte a byte
                    //BufferedReader leitura de um bloco, fazendo menos chamadas ao Sistema Operacional
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
                    //Realiza a leitura de uma linha do buffer (bloco) e atribui a uma String
                    String linha = buffer.readLine();
                    while (linha != null){
                        //Concatena com o objeto StringBuilder
                        sb.append(linha);
                        linha = buffer.readLine();
                    }
                    resultado = sb.toString();
                    //Encerra a comunicação com o serviço
                    conexao.disconnect();
                }
                else {
                    resultado = "ERRO";
                }

            }
            catch (Exception e){

            }
            if (resultado != null){
                //Resultado veio com alguma informação
                //Definimos um objeto jresp do tipo Objeto JSON
                JSONObject jresp = null;

                try {
                    //Criacao de um objeto JSON
                    //Toda a resposta da requisição vira um objeto JSON
                    jresp = new JSONObject(resultado);
                    //Verificamos se o valor da propriedade status é OK
                    //Se for, indica que a requisição foi realizada com sucesso
                    if (jresp.optString("status").equals("OK")){
                        //Buscamos então entre os objetos dentro do jresp um array chamado results
                        JSONArray arrayderesults = jresp.getJSONArray("results");
                        for (int i=0; i < arrayderesults.length();i++){
                            //Pegamos um item dentro do array
                            JSONObject objJSON = arrayderesults.getJSONObject(i);
                            //Deste objeto pegamos um atraibuto chamado formatted_address
                            endereco = objJSON.getString("formatted_address");
                            if (endereco != null)
                                //Ao encontrarmos um valor, saimos do laço
                                break;
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return endereco;

        }

        @Override
        protected void onPostExecute(String s) {
            //super.onPostExecute(s);
            //pegar o endereco e jogar na tela
            lblEnderecoresposta.setText(s);

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }
}
