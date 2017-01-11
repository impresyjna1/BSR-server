package bsr.server.outerServices;

import bsr.server.exceptions.AccountServiceException;
import bsr.server.properties.Config;
import org.glassfish.jersey.internal.util.Base64;
import org.json.JSONObject;
import sun.misc.IOUtils;

import java.io.IOException;
import java.io.InputStream;
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
        //TODO: Handle server error response
        switch (status) {
            case 201:
                requestSuccess = true;
                return requestSuccess;
            default:
                InputStream response = connection.getErrorStream();
                if(response != null) {
                    String message = new String(IOUtils.readFully(response, -1, true));
                    throw new AccountServiceException(message);
                } else {
                    throw new AccountServiceException();
                }
        }
    }

}
