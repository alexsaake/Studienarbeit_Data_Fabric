package com.mse.datafabric.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.stereotype.Component;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Map;

public class RestClient{

    private final Logger myLogger = LoggerFactory.getLogger(RestClient.class);

    private RestClient(){
        throw new RuntimeException("Utilitiy class should not be instanciated.");
    }

    public static String execute(String targetURL) {
        return execute(targetURL, "GET", 60000,null, null, null, null, false);
    }

    public static String execute(String targetURL,String user,String passwort) {
        return execute(targetURL, "GET", 60000,null, null, user, passwort, false);
    }

    public static String execute(String targetURL, String requestMethod, int timeoutInMs, String contentType, String body, String username, String password, Boolean ignoreSSL) {
        return execute(targetURL, requestMethod, timeoutInMs, contentType, body, username, password, null, ignoreSSL);
    }

    public static String execute(String targetURL, String requestMethod, int timeoutInMs, String contentType, String body, String username, String password, final Map<String, String> cutomRequestHeaders, Boolean ignoreSSL) {
        return new String(requestByte(targetURL, requestMethod, timeoutInMs, contentType, body, username, password, cutomRequestHeaders, ignoreSSL));
    }

    public static byte[] requestByte(String targetURL, String requestMethod, int timeoutInMs, String contentType, String body, String username, String password, final Map<String, String> cutomRequestHeaders, Boolean ignoreSSL) {
        URL url;
        InputStream inputStream;
        BufferedWriter writer;
        HttpURLConnection connection = null;

        try{
            url = new URL(targetURL);
            if (targetURL.contains("https")) {
                if (ignoreSSL) {
                    try {
                        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
                            public boolean verify(String hostname, SSLSession session) {
                                return true;
                            }
                        });
                        SSLContext context = SSLContext.getInstance("TLS");
                        context.init(null, new X509TrustManager[]{new X509TrustManager(){
                            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                            public X509Certificate[] getAcceptedIssuers() {
                                return new X509Certificate[0];
                            }
                        }}, new SecureRandom());
                        HttpsURLConnection.setDefaultSSLSocketFactory(
                                context.getSocketFactory());
                    } catch (Exception e) { // should never happen
                        e.printStackTrace();
                    }
                }
                connection = (HttpsURLConnection) url.openConnection();
            } else {
                connection = (HttpURLConnection) url.openConnection();
            }
            connection.setRequestMethod(requestMethod);
            if ((username != null) && (password != null)) {
                String encoded = Base64.getEncoder().encodeToString((username+":"+password).getBytes(StandardCharsets.UTF_8));
                connection.setRequestProperty("Authorization", "Basic "+encoded);
            }
            connection.setReadTimeout(timeoutInMs);
            connection.setConnectTimeout(timeoutInMs);

            if (cutomRequestHeaders != null) {
                for (final Map.Entry<String, String> entry : cutomRequestHeaders.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            //Send Request
            if (body != null) {
                if (contentType != null) {
                    connection.setRequestProperty("Content-Type", contentType); // z.B. "application/json; charset=utf-8"
                }
                connection.setRequestProperty("Content-Length",
                        Integer.toString(body.getBytes().length));
                connection.setUseCaches(false);
                connection.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream (
                        connection.getOutputStream());
                writer = new BufferedWriter(new OutputStreamWriter(wr, "UTF-8"));
                writer.write(body);
                writer.close();
            }

            //Get Response
            if (connection.getResponseCode() / 100 == 2) {
                inputStream = connection.getInputStream();
            } else {
                throw new RuntimeException(inputStreamToString(connection.getErrorStream()));
            }

            return readAllBytes(inputStream);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static byte[] readAllBytes(InputStream inputStream) throws IOException {
        final int bufLen = 4 * 0x400; // 4KB
        byte[] buf = new byte[bufLen];
        int readLen;
        IOException exception = null;

        try {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                while ((readLen = inputStream.read(buf, 0, bufLen)) != -1)
                    outputStream.write(buf, 0, readLen);

                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            exception = e;
            throw e;
        } finally {
            if (exception == null) inputStream.close();
            else try {
                inputStream.close();
            } catch (IOException e) {
                exception.addSuppressed(e);
            }
        }
    }

    private static String inputStreamToString(InputStream stream){
        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = stream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString("UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}