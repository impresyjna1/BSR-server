package bsr.server.outerServices;

import bsr.server.exceptions.AccountServiceException;
import bsr.server.properties.Config;
import org.glassfish.jersey.internal.util.Base64;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Impresyjna on 11.01.2017.
 */
public class SendTransferToOtherBank {
    private String charset = "UTF-8";

    public String createJsonMessage(String sourceAccountNumber, int amount, String title) {
        JSONObject requestParams = new JSONObject();
        requestParams.put("amount", amount);
        requestParams.put("from", sourceAccountNumber);
        requestParams.put("title", title);

        return requestParams.toString();
    }

    public HttpURLConnection createConnection(String bankServiceAddress, String targetAccountNumber, String sourceAccountNumber, String title, int amount) throws IOException {
        String url = bankServiceAddress + "/accounts/" + targetAccountNumber;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Accept-Charset", charset);
        connection.setRequestProperty("Content-Type", "application/json;charset=" + charset);
        connection.setRequestProperty("Authorization", "Basic " +
                Base64.encodeAsString(Config.AUTH_BANK_USERNAME + ":" + Config.AUTH_BANK_PASSWORD));

        return connection;
    }

    public boolean makeRequest(String bankServiceAddress, String targetAccountNumber, String sourceAccountNumber, String title, int amount) throws IOException, AccountServiceException {
        HttpURLConnection connection = createConnection(bankServiceAddress, targetAccountNumber, sourceAccountNumber, title, amount);
        OutputStream requestBody = connection.getOutputStream();
        requestBody.write(createJsonMessage(sourceAccountNumber, amount, title).getBytes(charset));
        requestBody.close();
        connection.connect();

        int status = connection.getResponseCode();
        boolean requestSuccess = false;
        switch (status) {
            case 404:
                throw new AccountServiceException("Account not found");
            case 403:
                throw new AccountServiceException("Forbidden");
            case 400:
                throw new AccountServiceException("Bad request");
            case 500:
                throw new AccountServiceException("Internal server error");
            case 418:
                throw new AccountServiceException("Teapot");
            case 201:
                requestSuccess = true;
        }

        return requestSuccess;
    }

}
