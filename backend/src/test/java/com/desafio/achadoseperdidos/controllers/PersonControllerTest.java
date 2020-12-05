package com.desafio.achadoseperdidos.controllers;

import com.desafio.achadoseperdidos.dto.ItemDTO;
import com.desafio.achadoseperdidos.dto.PersonDTO;
import com.desafio.achadoseperdidos.entities.Item;
import com.desafio.achadoseperdidos.entities.Person;
import com.desafio.achadoseperdidos.services.PersonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(controllers = PersonController.class)
class PersonControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private PersonService personService;

    @Test
    void shouldReturnsAPersonById() throws Exception {
        Person person = new Person(1L, "validName", "valid@mail.com", "83 9 1234-5678");

        when(personService.getPersonById(eq(1L))).thenReturn(person);

        String response = mockMvc.perform(get("/person").queryParam("id", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedPerson = objectMapper.writeValueAsString(person);
        assertEquals(expectedPerson, response);
    }

    @Test
    void shouldReturns404IfPersonDoesNotExists() throws Exception {
        when(personService.getPersonById(eq(1L))).thenThrow(new NotFoundException(""));

        mockMvc.perform(get("/person").queryParam("id", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateAnewPerson() throws Exception {
        Person person = new Person(1L, "validPerson", "validPerson@mail.com", "83 9 8743-1234");

        when(personService.savePerson(any(PersonDTO.class))).thenReturn(person);

        PersonDTO personDTO = new PersonDTO("validPerson", "validPerson@mail.com", "83 9 8743-1234");
        MvcResult result = mockMvc.perform(post("/person")
                .content(objectMapper.writeValueAsString(personDTO))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andReturn();

        String response = result.getResponse().getContentAsString();

        String expectedPerson = objectMapper.writeValueAsString(person);
        assertEquals(expectedPerson, response);
    }

    @Test
    void shouldReturns400IfInvalidNameIsProvided() throws Exception {
        PersonDTO personDTO = new PersonDTO("", "valid@mail.com", "83 9 87432112");

        when(personService.savePerson(personDTO)).thenThrow(new BadRequestException(""));

        mockMvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(personDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturns400IfInvalidEmailIsProvided() throws Exception {
        PersonDTO personDTO = new PersonDTO("validName", "", "83 9 87432112");

        when(personService.savePerson(personDTO)).thenThrow(new BadRequestException(""));

        mockMvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(personDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturns400IfInvalidTelephoneIsProvided() throws Exception {
        PersonDTO personDTO = new PersonDTO("validName", "valid@mail.com", "");

        when(personService.savePerson(personDTO)).thenThrow(new BadRequestException(""));

        mockMvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(personDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAddLostItemToPerson() throws Exception {
        Person person = new Person(1L, "validPerson", "valid@mail.com", "83 9 0984-2314");
        Item item = new Item("validItem", "perdido na esqueina", "desconhecido", "cg", "pb", true);
        person.getLostItems().add(item);

        when(personService.addLostItemToPerson(eq(1L), any(ItemDTO.class))).thenReturn(person);

        ItemDTO itemDTO = new ItemDTO("validItem", "perdido na esqueina", "desconhecido", "cg", "pb");
        MvcResult result = mockMvc.perform(patch("/person/1/lost-item")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(itemDTO)))
                            .andExpect(status().isOk())
                            .andReturn();

        String response = result.getResponse().getContentAsString();
        String expectedPerson = objectMapper.writeValueAsString(person);
        assertEquals(expectedPerson, response);
    }

    @Test
    void shouldAddFoundItemToPerson() throws Exception {
        Person person = new Person(1L, "validPerson", "valid@mail.com", "83 9 0984-2314");
        Item item = new Item("validItem", "perdido na esqueina", "desconhecido", "cg", "pb", false);
        person.getLostItems().add(item);

        when(personService.addFoundItemToPerson(eq(1L), any(ItemDTO.class))).thenReturn(person);

        ItemDTO itemDTO = new ItemDTO("validItem", "perdido na esquina", "desconhecido", "cg", "pb");
        MvcResult result = mockMvc.perform(patch("/person/1/found-item")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(itemDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        String expectedPerson = objectMapper.writeValueAsString(person);
        assertEquals(expectedPerson, response);
    }
}