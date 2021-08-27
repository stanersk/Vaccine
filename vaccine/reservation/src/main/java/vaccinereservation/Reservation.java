package vaccinereservation;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Reservation_table")
public class Reservation {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long userId;
    private String hospital;
    private Date reservedDate;
    private String reservationStatus;
    private Long vaccineId;

    @PostPersist
    public void onPostPersist(){

        ReservationApplication.applicationContext.getBean(vaccinereservation.external.VaccineMgmtService.class)
                .updateVaccine(this.vaccineId);

        final VaccineReserved vaccineReserved = new VaccineReserved();
        vaccineReserved.setId(this.getId());
        vaccineReserved.setUserId(this.getUserId());
        vaccineReserved.setHospital(this.getHospital());
        vaccineReserved.setReservedDate(this.getReservedDate());
        vaccineReserved.setVaccineId(this.getVaccineId());
        vaccineReserved.setReservationStatus("reserved");
        BeanUtils.copyProperties(this, vaccineReserved);
        vaccineReserved.publishAfterCommit();

        // Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.
        // final vaccinereservation.external.VaccineMgmt vaccine = new vaccinereservation.external.VaccineMgmt();
        // vaccine.setId(this.getId());
        // vaccine.setUserId(this.getUserId());
        // vaccine.setHospital(this.getHospital());
        // vaccine.setAvailableDate(this.getReservedDate());
        // vaccine.setReservationStatus("reserved");

        // vaccinereservation.external.VaccineMgmt vaccineMgmt = new
        // vaccinereservation.external.VaccineMgmt();
        // mappings goes here


    }

    @PostUpdate
    public void onPostUpdate() {
        final ReservationCancelled reservationCancelled = new ReservationCancelled();
        reservationCancelled.setId(this.getId());
        reservationCancelled.setUserId(this.getUserId());
        reservationCancelled.setHospital(this.getHospital());
        reservationCancelled.setReservedDate(this.getReservedDate());
        reservationCancelled.setReservationStatus("reserved");

        BeanUtils.copyProperties(this, reservationCancelled);
        reservationCancelled.publishAfterCommit();

    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(final String hospital) {
        this.hospital = hospital;
    }

    public Date getReservedDate() {
        return reservedDate;
    }

    public void setReservedDate(final Date reservedDate) {
        this.reservedDate = reservedDate;
    }

    public String getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(final String reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public Long getVaccineId() {
        return vaccineId;
    }

    public void setVaccineId(Long vaccineId) {
        this.vaccineId = vaccineId;
    }




}