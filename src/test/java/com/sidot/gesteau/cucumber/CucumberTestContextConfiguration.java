package com.sidot.gesteau.cucumber;

import com.sidot.gesteau.GesteauApp;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

@CucumberContextConfiguration
@SpringBootTest(classes = GesteauApp.class)
@WebAppConfiguration
public class CucumberTestContextConfiguration {}
