package guru.sfg.brewery.web.controllers;

import guru.sfg.brewery.repositories.BeerInventoryRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.repositories.CustomerRepository;
import guru.sfg.brewery.services.BeerService;
import guru.sfg.brewery.services.BreweryService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

// @WebMvcTest // This annotation brings up the Spring context to get access to the web application context during testing
public abstract class BaseIT {
    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    //    We can use the @MockBean to add mock objects to the Spring application context.
    //    The mock will replace any existing bean of the same type in the application context.
    //    If no bean of the same type is defined, a new one will be added.
    //    This annotation is useful in integration tests where a particular bean, like an external service, needs to be mocked.
    @MockBean
    BeerRepository beerRepository;

    @MockBean
    BeerInventoryRepository beerInventoryRepository;

    @MockBean
    BreweryService breweryService;

    @MockBean
    CustomerRepository customerRepository;

    @MockBean
    BeerService beerService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac) // injects web application context into test builder
                .apply(springSecurity()) // activates the spring security filters
                .build();
    }
}
