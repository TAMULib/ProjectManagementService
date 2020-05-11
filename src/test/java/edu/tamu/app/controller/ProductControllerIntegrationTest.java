package edu.tamu.app.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import edu.tamu.app.model.Product;
import edu.tamu.app.model.RemoteProductInfo;
import edu.tamu.app.model.RemoteProductManager;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.model.repo.ProductRepo;
import edu.tamu.app.model.repo.RemoteProductManagerRepo;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class ProductControllerIntegrationTest {
    private static final String TEST_PRODUCT_SCOPE1 = "0010";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private RemoteProductManagerRepo remoteProductManagerRepo;

    @Before
    public void setup() {
        RemoteProductManager remoteProductManager = remoteProductManagerRepo.create(
            new RemoteProductManager("VersionTwo", ServiceType.VERSION_ONE, new HashMap<String, String>() {
                private static final long serialVersionUID = 2020874481642498006L;
                {
                    put("url", "https://localhost:9101/TexasAMLibrary");
                    put("username", "username");
                    put("password", "password");
                }
            }
        ));

        RemoteProductInfo remoteProductInfo = new RemoteProductInfo(TEST_PRODUCT_SCOPE1, remoteProductManager);

        List<RemoteProductInfo> remoteProductInfoList = new ArrayList<RemoteProductInfo>();
        remoteProductInfoList.add(remoteProductInfo);

        Product product = productRepo.create(new Product("Test"));
        product.setRemoteProductInfo(remoteProductInfoList);
        product = productRepo.update(product);
    }

    @Test
    public void testGetProducts() throws Exception {
        // @formatter:off
        mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("meta.status", equalTo("SUCCESS")))
            .andExpect(jsonPath("payload.ArrayList<Product>[0].id", equalTo(1)))
                .andExpect(jsonPath("payload.ArrayList<Product>[0].name", equalTo("Test")))
                .andExpect(jsonPath("payload.ArrayList<Product>[0].remoteProductInfo[0].scopeId", equalTo(TEST_PRODUCT_SCOPE1)))
                .andExpect(jsonPath("payload.ArrayList<Product>[0].remoteProductInfo[0].remoteProductManager.id", equalTo(1)))
                .andExpect(jsonPath("payload.ArrayList<Product>[0].remoteProductInfo[0].remoteProductManager.name", equalTo("VersionTwo")))
                .andExpect(jsonPath("payload.ArrayList<Product>[0].remoteProductInfo[0].remoteProductManager.type", equalTo("VERSION_ONE")))
                .andExpect(jsonPath("payload.ArrayList<Product>[0].remoteProductInfo[0].remoteProductManager.settings").doesNotExist());
        // @formatter:on
    }

    @Test
    public void testGetProductById() throws Exception {
        // @formatter:off
        mockMvc.perform(get("/products/2").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("meta.status", equalTo("SUCCESS")))
            .andExpect(jsonPath("payload.Product.id", equalTo(2)))
                .andExpect(jsonPath("payload.Product.name", equalTo("Test")))
                .andExpect(jsonPath("payload.Product.remoteProductInfo[0].scopeId", equalTo(TEST_PRODUCT_SCOPE1)))
                .andExpect(jsonPath("payload.Product.remoteProductInfo[0].remoteProductManager.id", equalTo(2)))
                .andExpect(jsonPath("payload.Product.remoteProductInfo[0].remoteProductManager.name", equalTo("VersionTwo")))
                .andExpect(jsonPath("payload.Product.remoteProductInfo[0].remoteProductManager.type", equalTo("VERSION_ONE")))
                .andExpect(jsonPath("payload.Product.remoteProductInfo[0].remoteProductManager.settings").doesNotExist());
        // @formatter:on
    }

    @After
    public void cleanup() {
        productRepo.deleteAll();
        remoteProductManagerRepo.deleteAll();
    }

}
