package vaccinereservation;

import java.util.Date;

public class ReservationCancelled extends AbstractEvent {

    private Long id;
    private Long userId;
    private String hospital;
    private Date reservedDate;
    private String reservationStatus;
    private Long vaccineId;
    // private Long qty;


    public ReservationCancelled(){
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getVaccineId() {
        return vaccineId;
    }

    public void setVaccineId(Long vaccineId) {
        this.vaccineId = vaccineId;
    }

    // public Long getQty() {
    //     return qty;
    // }

    // public void setQty(Long qty) {
    //     this.qty = qty;
    // }
}