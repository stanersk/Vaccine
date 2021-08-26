package vaccinereservation;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="VaccineMgmt_table")
public class VaccineMgmt {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long reservationId;
    private Date availableDate;
    private String hospital;
    private String vaccineStatus;
    private Long userId;
    private Long qty;

    @PostPersist
    public void onPostPersist(){
        VaccineRegistered vaccineRegistered = new VaccineRegistered();
        vaccineRegistered.setId(this.getId());
        vaccineRegistered.setUserId(this.getUserId());
        vaccineRegistered.setHospital(this.getHospital());
        vaccineRegistered.setAvailableDate(this.getAvailableDate());        
        // vaccineRegistered.setQty(this.getQty());
        vaccineRegistered.setVaccineStatus("registered");

        BeanUtils.copyProperties(this, vaccineRegistered);
        vaccineRegistered.publishAfterCommit();

    }
    @PrePersist
    public void onPrePersist(){
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }
    public Date getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(Date availableDate) {
        this.availableDate = availableDate;
    }
    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }
    public String getVaccineStatus() {
        return vaccineStatus;
    }

    public void setVaccineStatus(String vaccineStatus) {
        this.vaccineStatus = vaccineStatus;
    }
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getQty() {
        return qty;
    }

    public void setQty(Long qty) {
        this.qty = qty;
    }


}