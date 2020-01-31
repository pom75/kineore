

import db.HibernateUtil;
import service.CabService;
import service.LoginService;
import service.RDVService;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


//Defines the base URI for all resource URIs.
@ApplicationPath("/rest")
//The java class declares root resource and provider classes
public class KineOre extends Application {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    //The method returns a non-empty collection with classes, that must be included in the published JAX-RS application
    @Override
    public Set<Class<?>> getClasses() {
        HashSet h = new HashSet<Class<?>>();
        h.add(LoginService.class);
        h.add(CabService.class);
        h.add(RDVService.class);
        h.add(CorsFilter.class);
        h.add(HibernateUtil.class);
        return h;
    }

    @Override
    public Map<String, Object> getProperties() {
        final Map<String, Object> properties = new HashMap<>();
        properties.put("jersey.config.server.provider.packages", "service.*");//api-->here mention your yourpackage.to.scan"
        return properties;
    }
}
