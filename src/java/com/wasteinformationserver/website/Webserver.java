package com.wasteinformationserver.website;

import com.sun.net.httpserver.HttpServer;
import com.wasteinformationserver.basicutils.Log;
import com.wasteinformationserver.website.datarequests.AdminRequests;
import com.wasteinformationserver.website.datarequests.DataRequest;
import com.wasteinformationserver.website.datarequests.NewDateRequest;
import com.wasteinformationserver.website.datarequests.RegisterRequest;
import com.wasteinformationserver.website.datarequests.login.CheckLoginState;
import com.wasteinformationserver.website.datarequests.login.LoginRequest;

import java.io.IOException;
import java.net.InetSocketAddress;


public class Webserver {
    public void startserver() {
        Log.info("starting Webserver");
        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(8000), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }


        server.createContext("/", new MainPage());

        server.createContext("/senddata/loginget", new LoginRequest());
        server.createContext("/senddata/registerpost", new RegisterRequest());
        server.createContext("/senddata/checkloginstate", new CheckLoginState());
        server.createContext("/senddata/wastedata", new DataRequest());
        server.createContext("/senddata/admindata", new AdminRequests());
        server.createContext("/senddata/newdate", new NewDateRequest());

        server.setExecutor(null); // creates a default executor
        server.start();
        Log.info("Server available at http://127.0.0.1:8000 now");

        /*

            try {
                server = HttpsServer.create(new InetSocketAddress(8000), 0);

                // initialise the HTTPS server
                SSLContext sslContext = SSLContext.getInstance("TLS");

                // initialise the keystore
                char[] password = "password".toCharArray();
                KeyStore ks = KeyStore.getInstance("JKS");
                FileInputStream fis = new FileInputStream("testkey.jks");
                ks.load(fis, password);

                // setup the key manager factory
                KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                kmf.init(ks, password);

                // setup the trust manager factory
                TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
                tmf.init(ks);

                // setup the HTTPS context and parameters
                sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
                server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
                    public void configure(HttpsParameters params) {
                        try {
                            // initialise the SSL context
                            SSLContext context = getSSLContext();
                            SSLEngine engine = context.createSSLEngine();
                            params.setNeedClientAuth(false);
                            params.setCipherSuites(engine.getEnabledCipherSuites());
                            params.setProtocols(engine.getEnabledProtocols());

                            // Set the SSL parameters
                            SSLParameters sslParameters = context.getSupportedSSLParameters();
                            params.setSSLParameters(sslParameters);

                        } catch (Exception ex) {
                            System.out.println("Failed to create HTTPS port");
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            } catch (CertificateException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnrecoverableKeyException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
         */

    }
}
