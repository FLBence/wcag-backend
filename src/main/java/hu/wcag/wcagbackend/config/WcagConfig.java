package hu.wcag.wcagbackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "wcag")
public class WcagConfig {
    private List<String> vagueLinks;

    public List<String> getVagueLinks(){
        return vagueLinks;
    }

    public void setVagueLinks(List<String> vagueLinks){
        this.vagueLinks = vagueLinks;
    }
}
