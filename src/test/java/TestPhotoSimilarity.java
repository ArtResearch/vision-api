/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.jupiter.api.DisplayName;

/**
 *
 * @author Manolis Fragiadoulakis <fragiadoulakis at smartupds.com>
 */
public class TestPhotoSimilarity {
    
    private static final String PASTEC_URL = "http://localhost:4212";
    
    public TestPhotoSimilarity() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    @DisplayName("Check Pastec") 
    public void checkPastec() {
        System.out.println("Checking Pastec localhost connection.");
        try {
            URL url = new URL(this.PASTEC_URL);
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            String data = "{\"type\":\"PING\"}";
            String expectedResponse = "{\"type\":\"PONG\"}";
            byte[] out = data.getBytes(StandardCharsets.UTF_8);
            OutputStream stream = http.getOutputStream();
            stream.write(out);
            System.out.println(http.getResponseCode() + " " + http.getResponseMessage());
            InputStream inputStream = http.getInputStream();
            String response = new BufferedReader( new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                                .lines()
                                .collect(Collectors.joining("\n"));
            assertEquals(response, expectedResponse);
            http.disconnect();
        } catch (MalformedURLException ex) {
            Logger.getLogger(TestPhotoSimilarity.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestPhotoSimilarity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
