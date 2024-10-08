package com.RideShare.review.Service;

import com.RideShare.Response.ApiResponseClass;
import com.RideShare.utils.Validator.ObjectsValidator;
import com.RideShare.utils.exception.ApiRequestException;
import com.RideShare.object.Model.Bicycle;
import com.RideShare.object.Repository.BicycleRepository;
import com.RideShare.reservation.Enum.ReservationStatus;
import com.RideShare.reservation.Model.Reservation;
import com.RideShare.reservation.Repository.ReservationRepository;
import com.RideShare.review.Model.Review;
import com.RideShare.review.Repository.ReviewRepository;
import com.RideShare.review.Request.ReviewRequest;
import com.RideShare.review.Response.ReviewResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final BicycleRepository bicycleRepository;
    private final ObjectsValidator<ReviewRequest> validator;


    public ApiResponseClass getReviews(Integer bicycleId){
        Bicycle bicycle = bicycleRepository.findById(bicycleId).orElseThrow(()-> new ApiRequestException("Bicycle not found"));
        List<Reservation> reservations = reservationRepository.findByBicycleId(bicycleId);
        if(reservations.isEmpty()){
            return new ApiResponseClass("No Reservation for this Bicycle",HttpStatus.NOT_FOUND,LocalDateTime.now());
        }
        List<ReviewResponse> responses = new ArrayList<>();

        for(Reservation reservation : reservations){
            Review review = reviewRepository.findByReservationId(reservation.getId());
            ReviewResponse response = ReviewResponse.builder()
                    .id(review.getId())
                    .notes(review.getNotes())
                    .wheels(review.getWheels())
                    .breaks(review.getWheels())
                    .extension(review.getExtension())
                    .skeleton(review.getSkeleton())
                    .build();
            responses.add(response);
        }

        return new ApiResponseClass("Review updated Successfully", HttpStatus.OK, LocalDateTime.now(),responses);


    }

    @Transactional
    public ApiResponseClass addReview(ReviewRequest request) {
        validator.validate(request);

        Reservation reservation = reservationRepository.findById(request.getReservationId()).orElseThrow(()->new ApiRequestException(
                "reservation not found"));
        if(reservation.getReservationStatus() != ReservationStatus.FINISHED){
            throw new ApiRequestException("reservation not finished");
        }

        Review review = Review.builder()
                .breaks(request.getBreaks())
                .extension(request.getExtension())
                .wheels(request.getWheels())
                .skeleton(request.getSkeleton())
                .build();

        reviewRepository.save(review);
        ReviewResponse response = ReviewResponse.builder()
                .id(review.getId())
                .breaks(review.getBreaks())
                .extension(review.getExtension())
                .wheels(review.getWheels())
                .skeleton(review.getSkeleton())
                .notes(review.getNotes())
                .build();
        return new ApiResponseClass("Review added Successfully", HttpStatus.CREATED, LocalDateTime.now(),response);
    }


    @Transactional
    public ApiResponseClass updateReview(Integer id,ReviewRequest request) {

        validator.validate(request);

        ReviewResponse response = ReviewResponse.builder()
                .build();
        return new ApiResponseClass("Review updated Successfully", HttpStatus.OK, LocalDateTime.now(),response);

    }

    public ApiResponseClass deleteReview(Integer id) {
        reviewRepository.delete(reviewRepository.findById(id).orElseThrow(()->new ApiRequestException("review not found")));
        return new ApiResponseClass("Review Deleted successfully",HttpStatus.OK,LocalDateTime.now());
    }





}
