package vaccinereservation;

import javax.persistence.*;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="MyPage_table")
public class MyPage {

        @Id
        @GeneratedValue(strategy=GenerationType.AUTO)
        private Long reservationId;
        private Long userId;
        private String hospital;
        private Date reservedDate;
        private String reservationStatus;


        public Long getReservationId() {
            return reservationId;
        }

        public void setReservationId(Long reservationId) {
            this.reservationId = reservationId;
        }
        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }
        public String getHospital() {
            return hospital;
        }

        public void setHospital(String hospital) {
            this.hospital = hospital;
        }
        public Date getReservedDate() {
            return reservedDate;
        }

        public void setReservedDate(Date reservedDate) {
            this.reservedDate = reservedDate;
        }
        public String getReservationStatus() {
            return reservationStatus;
        }

        public void setReservationStatus(String reservationStatus) {
            this.reservationStatus = reservationStatus;
        }

}