package com.RideShare.object.Service;

import com.RideShare.Response.ApiResponseClass;
import com.RideShare.favourite.Favourite;
import com.RideShare.favourite.FavouriteRepository;
import com.RideShare.object.Enum.BicycleCategory;
import com.RideShare.object.Model.Bicycle;
import com.RideShare.object.Model.ModelPrice;
import com.RideShare.object.Repository.BicycleRepository;
import com.RideShare.object.Repository.ModelPriceRepository;
import com.RideShare.object.Request.BicycleRequest;
import com.RideShare.object.Response.BicycleResponse;
import com.RideShare.object.Response.ClientBicycleResponse;
import com.RideShare.resource.Enum.ResourceType;
import com.RideShare.resource.Model.FileMetaData;
import com.RideShare.resource.Repository.FileMetaDataRepository;
import com.RideShare.resource.service.FileStorageService;
import com.RideShare.user.Model.Client;
import com.RideShare.utils.Service.UtilsService;
import com.RideShare.utils.exception.ApiRequestException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BicycleService {

    private final BicycleRepository bicycleRepository;
    private final ModelPriceRepository modelPriceRepository;
    private final FileStorageService fileStorageService;
    private final FileMetaDataRepository fileMetaDataRepository;
    private final UtilsService utilsService;
    private final FavouriteRepository favouriteRepository;

    public Boolean isFavourite(Bicycle bicycle) {
        var client = utilsService.extractCurrentUser();
        if(client instanceof Client) {
            Integer clientId = client.getId();
            Favourite favourite = favouriteRepository.findByClientIdAndBicycleId(clientId, bicycle.getId())
                    .orElse(null);
            return favourite != null;
        }
        else
            throw new AuthorizationServiceException("User is not a client");
    }

    public ClientBicycleResponse extractToClientResponse(Bicycle bicycle) {
        return ClientBicycleResponse.builder()
                .id(bicycle.getId())
                .type(bicycle.getType().toString())
                .size(bicycle.getSize())
                .photoPath(fileMetaDataRepository.findById(bicycle.getPhoto_id()).get().getFilePath())
                .note(bicycle.getNote())
                .model_price(bicycle.getModel_price())
                .maintenance(bicycle.getMaintenance())
                .isFavourite(isFavourite(bicycle))
                .build();
//        return response;
    }

    public ApiResponseClass getAllBicyclesForClient(){
       List<Bicycle> bicycleList = bicycleRepository.findAll();
       List<ClientBicycleResponse> responseList = new ArrayList<>();
       for (Bicycle bicycle : bicycleList) {
           responseList.add(extractToClientResponse(bicycle));
       }
       return new ApiResponseClass("Get all objects done successfully" , HttpStatus.ACCEPTED , LocalDateTime.now(),responseList);
    }
    public ApiResponseClass getAllBicyclesForManager () {
        List<Bicycle> bicycleList = bicycleRepository.findAll();
        List<BicycleResponse> responseList = new ArrayList<>();
        for (Bicycle bicycle : bicycleList) {
            responseList.add(BicycleResponse.builder()
                    .id(bicycle.getId())
                    .type(bicycle.getType().toString())
                    .size(bicycle.getSize())
                    .photoPath(fileMetaDataRepository.findById(bicycle.getPhoto_id()).get().getFilePath())
                    .note(bicycle.getNote())
                    .model_price(bicycle.getModel_price())
                    .maintenance(bicycle.getMaintenance())
                    .build());
        }
        return new ApiResponseClass("Get all objects done successfully" , HttpStatus.ACCEPTED , LocalDateTime.now(),responseList);

    }
    public ApiResponseClass getBicycleByIdForClient(int id){
        Bicycle bicycle = bicycleRepository.findById(id).orElseThrow(
                ()-> new ApiRequestException("Bicycle with id: " + id + "not found")
        );
        ClientBicycleResponse response = extractToClientResponse(bicycle);
        return new ApiResponseClass("Get object by id" , HttpStatus.ACCEPTED , LocalDateTime.now(),response);
    }
    public ApiResponseClass getBicycleByIdForManager(int id){
        Bicycle bicycle = bicycleRepository.findById(id).orElseThrow(
                ()-> new RuntimeException("Bicycle with id: " + id + "not found")
        );
        ClientBicycleResponse response = ClientBicycleResponse.builder()
                .id(bicycle.getId())
                .type(bicycle.getType().toString())
                .size(bicycle.getSize())
                .photoPath(fileMetaDataRepository.findById(bicycle.getPhoto_id()).get().getFilePath())
                .note(bicycle.getNote())
                .model_price(bicycle.getModel_price())
                .maintenance(bicycle.getMaintenance())
                .isFavourite(isFavourite(bicycle))
                .build();
        return new ApiResponseClass("Get bicycle by id" , HttpStatus.ACCEPTED , LocalDateTime.now(),response);
    }

    public ApiResponseClass getBicycleByCategoryForClient(String name){
        List<Bicycle> bicycleList = bicycleRepository.findBicycleByType(BicycleCategory.valueOf(name));
        List<ClientBicycleResponse> responseList = new ArrayList<>();

        for (Bicycle bicycle : bicycleList) {
            responseList.add(extractToClientResponse(bicycle));
        }
        return new ApiResponseClass("Get bicycles by category" , HttpStatus.ACCEPTED , LocalDateTime.now(),responseList);

    }

    public ApiResponseClass getAllCategories(){
//        BicycleCategory[] bicycleCategories = BicycleCategory.values();
        List<BicycleCategory> bicycleCategoriesList = List.of(BicycleCategory.values());
//        List<BicycleCategory> bicycleCategoriesList = new ArrayList<>(Arrays.asList(bicycleCategories));

        return new ApiResponseClass("Get all categories", HttpStatus.ACCEPTED , LocalDateTime.now(),bicycleCategoriesList);
    }

    @Transactional
    public ApiResponseClass createObject(BicycleRequest request){

        ModelPrice modelPrice = ModelPrice.builder()
                .model(request.getModel_price().getModelName())
                .price(request.getModel_price().getPrice())
                .build();


        Bicycle bicycle = Bicycle.builder()
                .note(request.getNote())
                .size(request.getSize())
                .model_price(modelPrice)
//                .category(BicycleCategory.valueOf(request.getCategory()))
                .type(BicycleCategory.valueOf(request.getType()))
                .build();

        FileMetaData bicyclePhoto = fileStorageService.storeFileFromOtherEntity(request.getPhoto(), ResourceType.Bicycle);
        bicycle.setPhoto_id(bicyclePhoto.getId());

        modelPriceRepository.save(modelPrice);
        bicycleRepository.save(bicycle);

        BicycleResponse response = BicycleResponse.builder()
                .id(bicycle.getId())
                .type(bicycle.getType().toString())
                .size(bicycle.getSize())
                .photoPath(fileMetaDataRepository.findById(bicycle.getPhoto_id()).get().getFilePath())
                .note(bicycle.getNote())
//                .category(bicycle.getCategory().toString())
                .model_price(bicycle.getModel_price())
                .maintenance(bicycle.getMaintenance())
                .build();

        return new ApiResponseClass("Create bicycle done successfully" , HttpStatus.CREATED , LocalDateTime.now(),response);
    }

    @Transactional
    public ApiResponseClass updateObject(Integer id, BicycleRequest request){
        Bicycle bicycle = bicycleRepository.findById(id).orElseThrow(
                ()-> new RuntimeException("Bicycle with id: " + id + "not found")
        );

        ModelPrice modelPrice = modelPriceRepository.findByModel(bicycle.getModel_price().getModel()).orElseThrow(
                ()-> new RuntimeException("ModelPrice with id: " + id + "not found")
        );
        modelPrice.setModel(request.getModel_price().getModelName());
        modelPrice.setPrice(request.getModel_price().getPrice());
        modelPriceRepository.save(modelPrice);

        bicycle.setNote(request.getNote());
        bicycle.setSize(request.getSize());
        bicycle.setModel_price(modelPrice);
//        bicycle.setCategory(BicycleCategory.valueOf(request.getCategory()));
        bicycle.setType(BicycleCategory.valueOf(request.getType()));
        bicycleRepository.save(bicycle);

        BicycleResponse response = BicycleResponse.builder()
                .id(bicycle.getId())
                .type(bicycle.getType().toString())
                .size(bicycle.getSize())
                .photoPath(fileMetaDataRepository.findById(bicycle.getPhoto_id()).get().getFilePath())
                .note(bicycle.getNote())
//                .category(bicycle.getCategory().toString())
                .model_price(bicycle.getModel_price())
                .maintenance(bicycle.getMaintenance())
                .build();
        return new ApiResponseClass("Update bicycle with id: " + id + " done successfully", HttpStatus.ACCEPTED , LocalDateTime.now(),response);
    }

    public ApiResponseClass deleteObject(Integer id){
        Bicycle bicycle = bicycleRepository.findById(id).orElseThrow(
                ()-> new RuntimeException("Bicycle with id: " + id + "not found")
        );
        bicycleRepository.delete(bicycle);
        return new ApiResponseClass("Delete bicycle with id: " + id + " done successfully" , HttpStatus.NO_CONTENT,LocalDateTime.now());
    }

    public ApiResponseClass getOfferBicycles(){
        List<Bicycle> bicycleList = bicycleRepository.findBicyclesByHasOfferTrue();
        if(bicycleList.isEmpty()){
            return new ApiResponseClass("There is no offers now", HttpStatus.NO_CONTENT , LocalDateTime.now());
        }
       List<ClientBicycleResponse> response = new ArrayList<>();
        for(Bicycle bicycle : bicycleList){
            response.add(extractToClientResponse(bicycle));
        }
        return new ApiResponseClass("Get offers", HttpStatus.OK , LocalDateTime.now(),response);
    }


}
