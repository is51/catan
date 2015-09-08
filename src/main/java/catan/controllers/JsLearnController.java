package catan.controllers;

import catan.domain.exception.AuthenticationException;
import catan.domain.exception.GameException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/learn")
public class JsLearnController {
    @RequestMapping(value = "games",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String listOfGames() throws AuthenticationException, GameException {
        return "[\n" +
                "    {\n" +
                "        \"id\": 0,\n" +
                "        \"name\": \"IT-Catan\",\n" +
                "        \"description\": \"Super puper game!!!! The best game you ever played.\",\n" +
                "        \"price\": 100,\n" +
                "        \"developers\": [\n" +
                "            \"ryzhenkovskiy\",\n" +
                "            \"petrovich\",\n" +
                "            \"syrovenko\"\n" +
                "        ],\n" +
                "        \"images\": [\n" +
                "            {\n" +
                "                \"big\": \"catan_big_1.jpg\",\n" +
                "                \"small\": \"catan_small1.jpg\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"big\": \"catan_big_2.png\",\n" +
                "                \"small\": \"catan_small2.jpg\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"big\": \"catan_big_3.jpg\",\n" +
                "                \"small\": \"catan_small3.jpg\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"reviews\": [\n" +
                "            {\n" +
                "                \"stars\": 5,\n" +
                "                \"text\": \"Best game in the world\",\n" +
                "                \"author\": \"me\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"stars\": 5,\n" +
                "                \"text\": \"I like it\",\n" +
                "                \"author\": \"me\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": 1,\n" +
                "        \"name\": \"GTA 6\",\n" +
                "        \"description\": \"Fucken shit game!!!! The worst game you ever played.\",\n" +
                "        \"price\": 14.25,\n" +
                "        \"developers\": [\n" +
                "            \"ryzhenkovskiy\",\n" +
                "            \"petrovich\",\n" +
                "            \"syrovenko\"\n" +
                "        ],\n" +
                "        \"images\": [\n" +
                "            {\n" +
                "                \"big\": \"gta_big_1.jpg\",\n" +
                "                \"small\": \"gta_small1.jpg\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"big\": \"gta_big_2.jpg\",\n" +
                "                \"small\": \"gta_small2.jpg\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"big\": \"gta_big_3.jpg\",\n" +
                "                \"small\": \"gta_small3.jpg\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"reviews\": [\n" +
                "            {\n" +
                "                \"stars\": 2,\n" +
                "                \"text\": \"Some shit\",\n" +
                "                \"author\": \"Andrey\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"stars\": 1,\n" +
                "                \"text\": \"I  don't like it\",\n" +
                "                \"author\": \"me\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "]";
    }
}
