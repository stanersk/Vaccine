package vaccinereservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

 @RestController
 public class VaccineMgmtController {

    @Autowired
    VaccineMgmtRepository vaccineMgmtRepository;

    @RequestMapping(value = "/vaccineMgmts/updateVaccine",
                    method = RequestMethod.PUT,
                    produces = "application/json;charset=UTF-8")
    // public boolean updateVaccine(HttpServletRequest request, HttpServletResponse response) throws Exception {
    public boolean updateVaccine(HttpServletRequest request, HttpServletResponse response) throws Exception {
            System.out.println("##### /vaccineMgmts/updateVaccine  called #####");

            // Parameter로 받은 RoomID 추출
            // long id = Long.valueOf(request.getParameter("id"));
            // long id = Long.valueOf(vaccineMgmt.getId());
            long id = Long.valueOf(request.getParameter("vaccineId"));
            System.out.println("######################## updateVaccine id : " + id);


            Optional<VaccineMgmt> res = vaccineMgmtRepository.findById(id);
            VaccineMgmt vaccine = res.get();

            // vaccine.setUserId(vaccineMgmt.getUserId());
            vaccine.setQty(vaccine.getQty() - 1);
            // // DB Update
            vaccineMgmtRepository.save(vaccine);

            boolean result = true;
            return result;
            // // RoomID 데이터 조회
            // Optional<VaccineMgmt> res = vaccineMgmtRepository.findById(id);
            // VaccineMgmt vaccineMgmt = res.get(); // 조회한 ROOM 데이터
            // System.out.println("######################## chkAndReqReserve room.getStatus() : " + room.getStatus());

            // room의 상태가 available이면 true
            // boolean result = false;
            // if(vaccineMgmt.getStatus().equals("available")) {
            //         result = true;
            // } 

            // System.out.println("######################## chkAndReqReserve Return : " + result);
            // return result;
    }

 }