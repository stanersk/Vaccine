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
    @Autowired NotificationRepository notificationRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverVaccineRegistered_SendSms(@Payload VaccineRegistered vaccineRegistered){

        // if(!vaccineRegistered.validate()) return;

        System.out.println("\n\n##### listener SendSms : " + vaccineRegistered.toJson() + "\n\n");

        if(vaccineRegistered.validate()){

            /////////////////////////////////////////////
            // 취소 요청이 왔을 때 -> status -> cancelled 
            /////////////////////////////////////////////
            System.out.println("##### listener vaccineRegistered : " + vaccineRegistered.toJson());
            Notification noti = new Notification();
            // 취소시킬 payId 추출
            // long id = vaccineRegistered.getId(); // 취소시킬 payId

            // Optional<Notification> res = notificationRepository.findById(id);
            // Notification noti = res.get();

            noti.setUserId(vaccineRegistered.getUserId());
            noti.setMessage("관리자에 의해 백신이 등록되었습니다.");
            noti.setVaccineStatus("registered"); 

            // DB Update
            notificationRepository.save(noti);

        }


        // Sample Logic //
        // Notification notification = new Notification();
        // notificationRepository.save(notification);

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverReservationCancelled_SendSms(@Payload ReservationCancelled reservationCancelled){

        // if(!reservationCancelled.validate()) return;

        System.out.println("\n\n##### listener SendSms : " + reservationCancelled.toJson() + "\n\n");

        if(reservationCancelled.validate()){

            /////////////////////////////////////////////
            // 취소 요청이 왔을 때 -> status -> cancelled 
            /////////////////////////////////////////////
            System.out.println("##### listener CancelPayment : " + reservationCancelled.toJson());
            
            // 취소시킬 payId 추출
            long id = reservationCancelled.getId(); // 취소시킬 payId

            Optional<Notification> res = notificationRepository.findById(id);
            Notification noti = res.get();

            noti.setUserId(reservationCancelled.getUserId());
            noti.setMessage("예약이 취소되었습니다.");
            // noti.setVaccineStatus("reservationcancelled"); 

            // DB Update
            notificationRepository.save(noti);

        }

        // Sample Logic //
        // Notification notification = new Notification();
        // notificationRepository.save(notification);

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverVaccineReserved_SendSms(@Payload VaccineReserved vaccineReserved){

        // if(!vaccineReserved.validate()) return;

        System.out.println("\n\n##### listener SendSms : " + vaccineReserved.toJson() + "\n\n");

        if(vaccineReserved.validate()){

            /////////////////////////////////////////////
            // 취소 요청이 왔을 때 -> status -> cancelled 
            /////////////////////////////////////////////
            System.out.println("##### listener CancelPayment : " + vaccineReserved.toJson());
            
            // 취소시킬 payId 추출
            long id = vaccineReserved.getId(); // 취소시킬 payId

            Optional<Notification> res = notificationRepository.findById(id);
            Notification noti = res.get();

            noti.setUserId(vaccineReserved.getUserId());
            noti.setMessage("접종이 예약되었습니다.");
            // noti.setVaccineStatus("reservationcancelled"); 

            // DB Update
            notificationRepository.save(noti);

        }

        // Sample Logic //
        // Notification notification = new Notification();
        // notificationRepository.save(notification);

    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}