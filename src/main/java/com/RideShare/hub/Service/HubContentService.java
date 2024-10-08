package com.RideShare.hub.Service;

import com.RideShare.Response.ApiResponseClass;
import com.RideShare.object.Response.ClientBicycleResponse;
import com.RideShare.object.Service.BicycleService;
import com.RideShare.utils.Validator.ObjectsValidator;
import com.RideShare.utils.exception.ApiRequestException;
import com.RideShare.hub.Entity.HubContent;
import com.RideShare.hub.Repository.HubContentRepository;
import com.RideShare.hub.Repository.HubRepository;
import com.RideShare.hub.Request.HubContentRequest;
import com.RideShare.hub.Response.HubContentResponse;
import com.RideShare.object.Enum.BicycleCategory;
import com.RideShare.object.Model.Bicycle;
import com.RideShare.object.Repository.BicycleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HubContentService {

    private final HubContentRepository hubContentRepository;
    private final ObjectsValidator<HubContentRequest> hubContentValidator;
    private final HubRepository hubRepository;
    private final BicycleRepository bicycleRepository;
    private final BicycleService bicycleService;

    private HubContentResponse extractHubContentResponse(HubContent hubContent) {
        List<Bicycle> hubBicycles = hubContent.getBicycles();
        List<ClientBicycleResponse> clientBicycleResponses = new ArrayList<>();
        for (Bicycle bicycle : hubBicycles) {
            clientBicycleResponses.add(bicycleService.extractToClientResponse(bicycle));
        }
        return HubContentResponse.builder()
                .id(hubContent.getId())
                .hubId(hubContent.getHub().getId())
                .bicycleList(clientBicycleResponses)
                .note(hubContent.getNote())
                .build();

    }

    public ApiResponseClass getHubContentByHubId(Integer hubId , String category) {
        try {
            BicycleCategory.valueOf(category);
        }catch (IllegalArgumentException e){
            throw new ApiRequestException("category name is invalid");
        }
        HubContent hubContent = hubContentRepository.findContentByBicycleTypeAndHubId(
                hubId,
                BicycleCategory.valueOf(category)
        ).orElseThrow(
                ()-> new ApiRequestException("hub content does not exist, or there is no bicycles with category " +"(" + category + ")" + " in this hub")
        );
        HubContentResponse response = extractHubContentResponse(hubContent);

        return new ApiResponseClass("Get Hub content by id done successfully" , HttpStatus.OK, LocalDateTime.now(),response);
    }

//    public ApiResponseClass createHubContent( HubContentRequest request) {
//        hubContentValidator.validate(request);
//
//        Hub hub = hubRepository.findById(request.getHub_id()).orElseThrow(
//                ()-> new ApiRequestException("hub does not exist")
//        );
//        List<Bicycle> bicycles = new ArrayList<>();
//        for(Integer bicycle : request.getBicycles()){
//            bicycles.add(bicycleRepository.findById(bicycle).orElseThrow(
//                    ()-> new ApiRequestException("bicycle with id: "+ bicycle + "does not exist")
//            ));
//        }
//
//        HubContent hubContent = HubContent.builder()
//                .hub(hub)
//                .bicycles(bicycles)
//                .note(request.getNote())
//                .build();
//        hubContentRepository.save(hubContent);
//
//        HubContentResponse hubContentResponse = HubContentResponse.builder()
//                .id(hubContent.getId())
//                .hubId(hubContent.getHub().getId())
//                .bicycleList(hubContent.getBicycles())
//                .note(hubContent.getNote())
//                .build();
//
//        return new ApiResponseClass("Create hub content successfully" , HttpStatus.CREATED, LocalDateTime.now(),hubContentResponse);
//    }

    public ApiResponseClass updateHubContent(Integer hubId, HubContentRequest request) {
        hubContentValidator.validate(request);

        HubContent hubContent = hubContentRepository.findById(hubId).orElseThrow(
                ()-> new ApiRequestException("hub with this id does not exist")
        );

        List<Bicycle> bicycles = new ArrayList<>();
        for(Integer bicycle : request.getBicycles()){
            bicycles.add(bicycleRepository.findById(bicycle).orElseThrow(
                    ()-> new ApiRequestException("bicycle with id: "+ bicycle + "does not exist")
            ));
        }
        hubContent.setNote(request.getNote());
//        hubContent.setHub(hub);
        hubContent.setBicycles(bicycles);
        hubContentRepository.save(hubContent);

        HubContentResponse response = extractHubContentResponse(hubContent);

        return new ApiResponseClass("Update hub content successfully" , HttpStatus.OK, LocalDateTime.now(),response );
    }



    public ApiResponseClass deleteHubContent(Integer id) {
            HubContent hubContent = hubContentRepository.findById(id).orElseThrow(
                    ()-> new ApiRequestException("hub content does not exist")
            );
            hubContentRepository.delete(hubContent);
            return new ApiResponseClass("Delete hub content successfully" , HttpStatus.NO_CONTENT, LocalDateTime.now());
    }
}
