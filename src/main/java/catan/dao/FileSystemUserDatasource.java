package catan.dao;

import catan.domain.Session;
import catan.domain.UserBean;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class FileSystemUserDatasource implements UserDatasource {

    private List<UserBean> users;
    private List<Session> sessions;

    public FileSystemUserDatasource() {
        users = new ArrayList<UserBean>();
        sessions = new ArrayList<Session>();
        try {
            populateUsersFromFile();
            populateSessionsFromFile();
        } catch (JAXBException e) {
            e.printStackTrace();
            //log.error(e);
        } catch (IOException e) {
            e.printStackTrace();
            //log.error(e);
        }
    }

    @Override
    public void addUser(UserBean newUser) {
        users.add(newUser);
        updateUsersToFile();
    }

    @Override
    public List<UserBean> getUsers() {
        return users;
    }

    @Override
    public void addSession(Session newSession) {
        sessions.add(newSession);
        updateSessionsToFile();
    }

    @Override
    public List<Session> getSessions() {
        return sessions;
    }

    @Override
    public void removeSessionByUsername(String username) {
        for(Session session : sessions){
           if(session.getUser().getUsername().equals(username)){
               sessions.remove(session);
               break;
           }
        }
    }

    @Override
    public void removeSessionByToken(String token) {
        for(Session session : sessions){
            if(session.getToken().equals(token)){
                sessions.remove(session);
                break;
            }
        }
    }

    private void populateUsersFromFile() throws JAXBException, IOException {
        JAXBContext jaxbContext = JAXBContext.newInstance(UserBean.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        InputStream fis = new FileInputStream("user.mdl");
        InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
            StringReader reader = new StringReader(line);
            UserBean user = (UserBean) jaxbUnmarshaller.unmarshal(reader);
            users.add(user);
        }
    }

    private void populateSessionsFromFile() throws JAXBException, IOException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Session.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        InputStream fis = new FileInputStream("session.mdl");
        InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
            StringReader reader = new StringReader(line);
            Session session = (Session) jaxbUnmarshaller.unmarshal(reader);
            sessions.add(session);
        }
    }

    public void updateUsersToFile() {
        try {
            File file = new File("user.mdl");

            FileWriter fw = new FileWriter(file);
            fw.write("");
            fw.close();

            fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);

            boolean notFirstLine = false;
            JAXBContext jc = JAXBContext.newInstance(UserBean.class);
            Marshaller m = jc.createMarshaller();
            for (UserBean user : users) {
                if (notFirstLine) {
                    bw.append("\n");
                }
                StringWriter sw = new StringWriter();
                m.marshal(user, sw);
                bw.append(sw.toString());

                notFirstLine = true;
            }

            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void updateSessionsToFile() {
        try {
            File file = new File("session.mdl");

            FileWriter fw = new FileWriter(file);
            fw.write("");
            fw.close();

            fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);

            boolean notFirstLine = false;
            JAXBContext jc = JAXBContext.newInstance(Session.class);
            Marshaller m = jc.createMarshaller();
            for (Session session : sessions) {
                if (notFirstLine) {
                    bw.append("\n");
                }
                StringWriter sw = new StringWriter();
                m.marshal(session, sw);
                bw.append(sw.toString());

                notFirstLine = true;
            }

            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
