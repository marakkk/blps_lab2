package com.blps.lab2.config;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class XmlUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {

            File file = new File("/home/marakkk/Downloads/Telegram Desktop/lab2-blps/lab2/src/main/resources/users.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);


            NodeList userList = doc.getElementsByTagName("user");
            for (int i = 0; i < userList.getLength(); i++) {
                Element userElement = (Element) userList.item(i);
                String xmlUsername = userElement.getElementsByTagName("username").item(0).getTextContent();
                if (xmlUsername.equals(username)) {
                    String password = userElement.getElementsByTagName("password").item(0).getTextContent();
                    String authorities = userElement.getElementsByTagName("authorities").item(0).getTextContent();
                    String roles = userElement.getElementsByTagName("roles").item(0).getTextContent();

                    return User.builder()
                            .username(xmlUsername)
                            .password("{noop}" + password)
                            .authorities(authorities.split(","))
                            .roles(roles.split(","))
                            .build();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new UsernameNotFoundException("User not found: " + username);
    }
}