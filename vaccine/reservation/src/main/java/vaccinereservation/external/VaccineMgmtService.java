package vaccinereservation.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@FeignClient(name="vaccine", url="${api.url.vaccine}")
public interface VaccineMgmtService {
    @RequestMapping(method= RequestMethod.PUT, path="/vaccineMgmts/updateVaccine")
    public void updateVaccine(@RequestParam("vaccineId") long vaccineId);

}

