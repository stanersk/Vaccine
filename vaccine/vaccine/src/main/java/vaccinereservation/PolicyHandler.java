package vaccinereservation;

import vaccinereservation.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class PolicyHandler{
    @Autowired VaccineMgmtRepository vaccineMgmtRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverReservationCancelled_IncreaseVaccine(@Payload ReservationCancelled reservationCancelled){

        // if(!reservationCancelled.validate()) return;

        // System.out.println("\n\n##### listener IncreaseVaccine : " + reservationCancelled.toJson() + "\n\n");

        if(reservationCancelled.validate()){

            /////////////////////////////////////////////
            // 취소 요청이 왔을 때 -> status -> cancelled 
            /////////////////////////////////////////////
            System.out.println("##### listener CancelPayment : " + reservationCancelled.toJson());
            
            // 취소시킬 payId 추출
            long id = reservationCancelled.getId(); // 취소시킬 payId

            Optional<VaccineMgmt> res = vaccineMgmtRepository.findById(id);
            VaccineMgmt vaccine = res.get();

            // vaccine.setReservationId(reservationCancelled.getId());
            // vaccine.setUserId(reservationCancelled.getUserId());
            vaccine.setQty(vaccine.getQty() + 1); 
            

            // DB Update
            vaccineMgmtRepository.save(vaccine);

        }


        // Sample Logic //
        // VaccineMgmt vaccineMgmt = new VaccineMgmt();
        // vaccineMgmtRepository.save(vaccineMgmt);

    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}