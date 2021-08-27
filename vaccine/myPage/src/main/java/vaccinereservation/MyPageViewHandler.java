package vaccinereservation;

import vaccinereservation.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import java.util.Date;

@Service
public class MyPageViewHandler {


    @Autowired
    private MyPageRepository myPageRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenVaccineReserved_then_CREATE_1 (@Payload VaccineReserved vaccineReserved) {
        try {

            if (!vaccineReserved.validate()) return;

            // view 객체 생성
            MyPage myPage = new MyPage();
            // view 객체에 이벤트의 Value 를 set 함
            // myPage.setId(vaccineReserved.getId());
            myPage.setUserId(vaccineReserved.getUserId());
            myPage.setHospital(vaccineReserved.getHospital());
            myPage.setReservedDate(vaccineReserved.getReservedDate());
            myPage.setReservationStatus(vaccineReserved.getReservationStatus());
            myPage.setUserId(vaccineReserved.getUserId());
            myPage.setHospital(vaccineReserved.getHospital());
            myPage.setReservedDate(vaccineReserved.getReservedDate());
            myPage.setReservationStatus(vaccineReserved.getReservationStatus());
            // view 레파지 토리에 save
            myPageRepository.save(myPage);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whenVaccineReserved_then_UPDATE_1(@Payload VaccineReserved vaccineReserved) {
        try {
            if (!vaccineReserved.validate()) return;
                // view 객체 조회

                    List<MyPage> myPageList = myPageRepository.findByUserId(vaccineReserved.getUserId());
                    for(MyPage myPage : myPageList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    myPage.setReservationStatus(vaccineReserved.getReservationStatus());
                // view 레파지 토리에 save
                myPageRepository.save(myPage);
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenReservationCancelled_then_UPDATE_2(@Payload ReservationCancelled reservationCancelled) {
        try {
            if (!reservationCancelled.validate()) return;
                // view 객체 조회

                    List<MyPage> myPageList = myPageRepository.findByUserId(reservationCancelled.getUserId());
                    for(MyPage myPage : myPageList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    myPage.setReservationStatus(reservationCancelled.getReservationStatus());
                // view 레파지 토리에 save
                myPageRepository.save(myPage);
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenReservationCancelled_then_UPDATE_3(@Payload ReservationCancelled reservationCancelled) {
        try {
            if (!reservationCancelled.validate()) return;
                // view 객체 조회

                    List<MyPage> myPageList = myPageRepository.findByUserId(reservationCancelled.getUserId());
                    for(MyPage myPage : myPageList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    myPage.setReservationStatus(reservationCancelled.getReservationStatus());
                // view 레파지 토리에 save
                myPageRepository.save(myPage);
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

