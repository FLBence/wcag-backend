package hu.wcag.wcagbackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/*
* Egyedi WCAG link szöveg ellenőrző komponenshez kapcsolódó adatok beállítása.
*/
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
