package com.example.new_iwdms;

import android.app.Activity;
import android.os.StrictMode;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class KIOSK extends Activity {



    private static final String NAMESPACE = "http://tempuri.org/";
    private static final String URL = "https://demo.seminalsoftwarepvt.in/IWDMSMiddleware/api/";

    // private String NAMESPACE = "http://tempuri.org/";
  //  private String NAMESPACE = "https://demo.seminalsoftwarepvt.in/";
    public static String URL1 = "";

   // public static final String URL = "https://demo.seminalsoftwarepvt.in/kalburgih2ope/kiosk.asmx";
    //public static  String URL="https://demo.seminalsoftwarepvt.in/IWDMSMiddleware/api/Tank/GetCityDetails";

    public static String SetURL(String URLVal) {
        URL1 = URLVal;
        return URL1;
    }
    /*-------------------------------------General---------------------------------*/

    public String SOAP_ACTION_HelloWorld = "http://tempuri.org/HelloWorld";
    public String METHOD_HelloWorld = "HelloWorld";

    public String SOAP_ACTION_API = "https://demo.seminalsoftwarepvt.in/IWDMSMiddleware/API/ValveDetails/GetValveId";
    public String METHOD_GetvalveDetails = "GetValveId";


    public String SOAP_ACTION_Login = "http://tempuri.org/Login";
    public String METHOD_Login = "Login";

    public String SOAP_ACTION_Register = "http://tempuri.org/Register";
    public String METHOD_Register = "Register";


    public String SOAP_ACTION_GetData = "http://tempuri.org/alldata";
    public String METHOD_GetData = "alldata";

    public String SOAP_ACTION_fogpassword = "http://tempuri.org/fogpassword";
    public String METHOD_fogpassword = "fogpassword";

    public String SOAP_ACTION_Delete = "http://tempuri.org/Delete";
    public String METHOD_Delete = "Delete";


    public SoapObject ExecuteWebServiceSOGRP(String MethodName, String ActionName, String PI[][]) {
        SoapObject request = new SoapObject(NAMESPACE, MethodName);
        for (int i = 0; i < PI.length; i++) {
            request.addProperty(PI[i][0], PI[i][1]);
        }
        //Declare the version of the SOAP request
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //this is the actual part that will call the webservice
            androidHttpTransport.call(ActionName, envelope);
            // Get the SoapResult from the envelope body.
            SoapObject result = (SoapObject) envelope.getResponse();

            if (result != null) {
                return result;
            } else {
                return null;
                //Toast.makeText(getApplicationContext(), "No Response",Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public SoapObject ExecuteWebServiceSOBIP(String MethodName, String ActionName, String PI[][]) {

        SoapObject request = new SoapObject(NAMESPACE, MethodName);
        for (int i = 0; i < PI.length; i++) {
            request.addProperty(PI[i][0], PI[i][1]);
        }

        //Declare the version of the SOAP request
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            //this is the actual part that will call the webservice
            androidHttpTransport.call(ActionName, envelope);
            // Get the SoapResult from the envelope body.
            SoapObject result = (SoapObject) envelope.bodyIn;

            if (result != null) {
                return result;

            } else {
                return null;
                // Toast.makeText(getApplicationContext(), "No Response",Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public SoapObject ExecuteWebServiceSOBI(String ActionName, String MethodName) {

        SoapObject request = new SoapObject(NAMESPACE, MethodName);
        // Declare the version of the SOAP request
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            // This is the actual part that will call the webservice
            androidHttpTransport.call(ActionName, envelope);
            // Get the SoapResult from the envelope body.
            SoapObject result = (SoapObject) envelope.bodyIn;

            if (result != null) {
                return result;
            } else {
                Toast.makeText(getApplicationContext(), "No Response", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public SoapObject ExecuteWebServiceSOGR(String MethodName, String ActionName) {

        SoapObject request = new SoapObject(NAMESPACE, MethodName);
        //Declare the version of the SOAP request
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //this is the actual part that will call the webservice
            androidHttpTransport.call(ActionName, envelope);
            // Get the SoapResult from the envelope body.
            SoapObject result = (SoapObject) envelope.getResponse();
            if (result != null) {
                return result;
            } else {
                Toast.makeText(getApplicationContext(), "No Response", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public SoapPrimitive ExecuteWebServiceSPBIP(String MethodName, String ActionName, String PI[][]) {

        SoapObject request = new SoapObject(NAMESPACE, MethodName);


        for (int i = 0; i < PI.length; i++) {
            request.addProperty(PI[i][0], PI[i][1]);

        }


        //Declare the version of the SOAP request
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL1);
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            //this is the actual part that will call the webservice
            androidHttpTransport.call(ActionName, envelope);

            // Get the SoapResult from the envelope body.
            SoapPrimitive result = (SoapPrimitive) envelope.bodyIn;

            if (result != null) {

                return result;

            } else {
                Toast.makeText(getApplicationContext(), "No Response", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public SoapPrimitive ExecuteWebServiceSPGR(String MethodName, String ActionName) {

        SoapObject request = new SoapObject(NAMESPACE, MethodName);

        //Declare the version of the SOAP request
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);

            //this is the actual part that will call the webservice
            androidHttpTransport.call(ActionName, envelope);

            // Get the SoapResult from the envelope body.
            SoapPrimitive result = (SoapPrimitive) envelope.getResponse();

            if (result != null) {

                return result;

            } else {
                Toast.makeText(getApplicationContext(), "No Response", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public SoapPrimitive ExecuteWebServiceSPGRP(String MethodName, String ActionName, String PI[][]) {

        SoapObject request = new SoapObject(NAMESPACE, MethodName);


        for (int i = 0; i < PI.length; i++) {
            request.addProperty(PI[i][0], PI[i][1]);

        }


        //Declare the version of the SOAP request
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL1);
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);

            //this is the actual part that will call the webservice
            androidHttpTransport.call(ActionName, envelope);

            // Get the SoapResult from the envelope body.
            SoapPrimitive result = (SoapPrimitive) envelope.getResponse();

            if (result != null) {

                return result;

            } else {
                Toast.makeText(getApplicationContext(), "No Response", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }
}