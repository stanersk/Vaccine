package vaccinereservation.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@FeignClient(name="vaccine", url="http://vaccine:8080")
public interface VaccineMgmtService {
    @RequestMapping(method= RequestMethod.PATCH, path="/vaccineMgmts")
    public void updateVaccine(@RequestBody VaccineMgmt vaccineMgmt);

}

