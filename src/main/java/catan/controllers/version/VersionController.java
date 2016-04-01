package catan.controllers.version;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Properties;


@RestController
@RequestMapping("/api/info")
public class VersionController {

    @Autowired
    private Environment environment;

    @RequestMapping(value = "full",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BuildInfoDetails getFullEnvironmentInfo() {
        Properties gitProperties = getGitProperties();

        GitRepositoryStateDetails gitRepositoryState = new GitRepositoryStateDetails(gitProperties);
        DatabasePropertiesDetails databaseProps = new DatabasePropertiesDetails(environment);

        return new BuildInfoDetails(databaseProps, gitRepositoryState);
    }

    @RequestMapping(value = "short",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public String getShortEnvironmentInfo() {
        Properties gitProperties = getGitProperties();

        GitRepositoryStateDetails gitRepositoryState = new GitRepositoryStateDetails(gitProperties);
        DatabasePropertiesDetails databaseProps = new DatabasePropertiesDetails(environment);

        return "Branch: '" + gitRepositoryState.getBranch() + "', database: " + databaseProps.getUrl();
    }

    private Properties getGitProperties() {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("git.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }

}
