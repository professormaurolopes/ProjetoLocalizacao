package br.edu.ifma.dai.maurolcsilva.projetolocalizacao;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class JanelaAbertura extends Activity implements LocationListener,View.OnClickListener{
    private TextView lblLatituderesposta;
    private TextView lblLongituderesposta;
    private TextView lblEnderecoresposta;
    private Button btnBuscarEndereco;
    @SuppressWarnings("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_janela_abertura);
        lblLatituderesposta = (TextView) findViewById(R.id.lblResultadoLatitude);
        lblLongituderesposta = (TextView) findViewById(R.id.lblResultadoLongitude);
        lblEnderecoresposta = (TextView) findViewById(R.id.lblEndereco);
        btnBuscarEndereco = (Button) findViewById(R.id.btnBuscarEndereco);
        btnBuscarEndereco.setOnClickListener(this);
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

        new BuscaEndereco().execute(latitude,longitude);
    }

    private class BuscaEndereco extends AsyncTask<String, Integer,String>{

        @Override
        protected String doInBackground(String... params) {
            String endereco="";
            //Passar a Latitude e Longitude
            //params[0] - latitude
            //params[1] - longitude
            //Passar isso para o web service
            //Pegar o endereco e passar para o onPostExecute
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
