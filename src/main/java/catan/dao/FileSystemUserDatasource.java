package catan.dao;

import catan.domain.UserBean;

import javax.jws.soap.SOAPBinding;
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

public class FileSystemUserDatasource implements UserDatasource{

    private List<UserBean> users;

    public FileSystemUserDatasource() {
        users = new ArrayList<UserBean>();
        try {
            populateUsersFromFile();
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
}
