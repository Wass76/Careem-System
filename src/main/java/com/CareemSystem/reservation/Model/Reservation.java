package com.CareemSystem.reservation.Model;

import com.CareemSystem.hub.Entity.Hub;
import com.CareemSystem.object.Model.Bicycle;
import com.CareemSystem.reservation.Enum.ReservationStatus;
import com.CareemSystem.user.Model.Client;
import com.CareemSystem.wallet.Enum.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {
    @Id
    @SequenceGenerator(
            name = "reservation_id",
            sequenceName = "reservation_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "reservation_id"
    )
    private Integer id;
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Client client;
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Bicycle bicycle;
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Hub from;
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Hub to;
    private double duration;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private LocalDateTime startTime;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private LocalDateTime endTime;
    @Enumerated
    private ReservationStatus reservationStatus;
    @Enumerated
    private PaymentMethod paymentMethod;



}
