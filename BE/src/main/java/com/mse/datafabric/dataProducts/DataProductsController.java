package com.mse.datafabric.dataProducts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.mse.datafabric.auth.AuthenticationService;
import com.mse.datafabric.dataProducts.data.DataProductData;
import com.mse.datafabric.dataProducts.payload.response.RatingMaxLengthsResponse;
import com.mse.datafabric.dataProducts.payload.DataProductAllDTO;
import com.mse.datafabric.dataProducts.payload.RatingDetailsDTO;
import com.mse.datafabric.dataProducts.payload.response.DataProductDetailsReponse;
import com.mse.datafabric.dataProducts.payload.response.DataProductOverviewResponse;
import com.mse.datafabric.dataProducts.payload.response.DataProductSummaryResponse;
import com.mse.datafabric.dataProducts.payload.response.RatingReponse;
import com.mse.datafabric.utils.GoogleMapsAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@ShellComponent
@RestController
@RequestMapping("/api/Gateway")
public class DataProductsController {
    private final Logger myLogger;
    private final IDataProductsService myDataProductsService;
    private final AuthenticationService myAuthenticationService;
    @Autowired
    public JdbcTemplate jdbcTemplate;
    @Autowired
    private DataProductRepository dataProductRepository;
    @Autowired
    public GoogleMapsAPI googleMapsAPI;
    @Autowired
    private DataProductData productData;

    @Autowired
    public DataProductsController(IDataProductsService dataProductsProvider, AuthenticationService authenticationService)
    {
        this(dataProductsProvider, authenticationService, LoggerFactory.getLogger("DataProductsController"));
    }
    public DataProductsController(IDataProductsService dataProductsProvider, AuthenticationService authenticationService, Logger logger)
    {
        myDataProductsService = dataProductsProvider;
        myAuthenticationService = authenticationService;
        myLogger = logger;
    }

    @ShellMethod( "getDataProducts" )
    @GetMapping("/DataProducts")
    public ResponseEntity<List<DataProductOverviewResponse>> getDataProductsOverview() {
        return ResponseEntity.ok(myDataProductsService.getDataProductsOverview());
    }

    @GetMapping("/DataProducts/Categories")
    public ResponseEntity<Map<Long, String>> getDataProductsCategories(){
        return ResponseEntity.ok(dataProductRepository.getCategories());
    }

    @GetMapping("/DataProducts/AccessRights")
    public ResponseEntity<Map<Long, String>> getDataProductsAccessRights(){
        return ResponseEntity.ok(dataProductRepository.getAccessRights());
    }

    @ShellMethod( "getDataProduct" )
    @GetMapping("/DataProduct/{dataProductId}")
    public ResponseEntity<DataProductSummaryResponse> getDataProductSummary(@PathVariable long dataProductId){
        return ResponseEntity.ok(myDataProductsService.getDataProductSummary(dataProductId));
    }

    @ShellMethod( "getDataProduct" )
    @GetMapping("/DataProduct/{dataProductId}/Details")
    public ResponseEntity<DataProductDetailsReponse> getDataProductDetails(@PathVariable long dataProductId){
        return ResponseEntity.ok(myDataProductsService.getDataProductDetails(dataProductId));
    }
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping(value = "/DataProduct/{dataProductId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadDataProductImage(@PathVariable long dataProductId,
                                                         @RequestParam("image") MultipartFile image) {
        if (image.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        if (!image.getContentType().startsWith("image/")) {
            return ResponseEntity.badRequest().body("File is not an image");
        }

        try {
            String imagePath = myDataProductsService.saveDataProductImage(dataProductId, image);

            return ResponseEntity.ok("Image uploaded successfully. Path: " + imagePath);
        } catch (Exception e) {
            // Handle exceptions (e.g., file not saved, DataProduct not found)
            return ResponseEntity.internalServerError().body("Could not upload image");
        }
    }
    @PostMapping(
            value = "/DataProduct",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('USER')")
    public long createDataProduct(@RequestBody String requestBodyJson){
        DataProductAllDTO dto;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            dto = mapper.readValue(requestBodyJson, DataProductAllDTO.class);
            return productData.createDataProduct(dto);
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }
        return -1;
    }
    @PatchMapping(
            value = "/DataProduct/{dataProductId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('USER')")
    public boolean editDataProduct(@PathVariable long dataProductId, @RequestBody String requestBodyJson){
        DataProductAllDTO dto;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            dto = mapper.readValue(requestBodyJson, DataProductAllDTO.class);
            return productData.editDataProduct(dto, dataProductId);
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }
        return false;
    }
    @GetMapping(
            value = "/DataProduct/{dataProductId}/DataAll",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('USER')")
    public String getDataProductDataAll(@PathVariable long dataProductId){
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(productData.getDataProductAll(dataProductId));
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
            return null;
        }
    }

    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping("/DataProduct/{dataProductId}")
    public ResponseEntity<Boolean> deleteDataProduct(@PathVariable long dataProductId){
        return ResponseEntity.ok(myDataProductsService.softDeleteDataProduct(myAuthenticationService.getCurrentUserName(), dataProductId));
    }

    @ShellMethod( "getDataProduct" )
    @GetMapping(
            value = "/DataProduct/{dataProductId}/Data",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String getDataProductDetailData(@PathVariable long dataProductId){
        return dataProductRepository.getData(dataProductId);
    }

    @ShellMethod("getDataProduct")
    @GetMapping("/DataProduct/{dataProductId}/Ratings")
    public ResponseEntity<List<RatingReponse>> getDataProductRatings(@PathVariable long dataProductId){
        return ResponseEntity.ok(myDataProductsService.getDataProductRatings(dataProductId));
    }

    @ShellMethod( "getDataProduct" )
    @GetMapping(
            value = "/DataProduct/{dataProductId}/Ratings/Averages",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Float getAvgRatings(@PathVariable long dataProductId){
        return dataProductRepository.getAvgRatings(dataProductId);
    }

    @ShellMethod("getDataProduct")
    @GetMapping("/DataProduct/Rating/{ratingId}")
    public ResponseEntity<RatingDetailsDTO> getDataProductRating(@PathVariable long ratingId){
        return ResponseEntity.ok(myDataProductsService.getDataProductRatingDetails(ratingId));
    }

    @PreAuthorize("hasAuthority('USER')")
    @ShellMethod("postDataProduct")
    @PostMapping("/DataProduct/{dataProductId}/Rating")
    public ResponseEntity<Boolean> setDataProductRating(@PathVariable long dataProductId, @RequestBody String requestBodyJson){
        RatingDetailsDTO ratingDetails = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            ratingDetails = mapper.readValue(requestBodyJson, RatingDetailsDTO.class);
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }

        return ResponseEntity.ok(myDataProductsService.setDataProductsRating(myAuthenticationService.getCurrentUserName(), dataProductId, ratingDetails));
    }

    @PreAuthorize("hasAuthority('USER')")
    @ShellMethod( "putDataProduct" )
    @PutMapping(
            value = "/DataProduct/Rating/{ratingId}"
    )
    public ResponseEntity<Boolean> updateDataProductRating(@PathVariable long ratingId, @RequestBody String requestBodyJson){
        RatingDetailsDTO ratingDetails = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            ratingDetails = mapper.readValue(requestBodyJson, RatingDetailsDTO.class);
        }
        catch (JsonProcessingException e) {
            myLogger.error("Could not parse json " + e);
        }

        return ResponseEntity.ok(myDataProductsService.updateDataProductsRating(myAuthenticationService.getCurrentUserName(), ratingId, ratingDetails));
    }

    @PreAuthorize("hasAuthority('USER')")
    @ShellMethod("deleteDataProduct")
    @DeleteMapping("/DataProduct/Rating/{ratingId}")
    public ResponseEntity<Boolean> deleteDataProductRating(@PathVariable long ratingId){
        return ResponseEntity.ok(myDataProductsService.markAsDeletedDataProductRating(myAuthenticationService.getCurrentUserName(), ratingId));
    }

    @PreAuthorize("hasAuthority('USER')")
    @ShellMethod("getDataProduct")
    @GetMapping("/DataProduct/{dataProductId}/Rating/CanSubmit")
    public ResponseEntity<Boolean> getDataProductRatingCanSubmit(@PathVariable long dataProductId){
        return ResponseEntity.ok(myDataProductsService.getDataProductRatingCanSubmit(myAuthenticationService.getCurrentUserName(), dataProductId));
    }

    @ShellMethod("getDataProduct")
    @GetMapping("/DataProduct/Rating/MaxLengths")
    public ResponseEntity<RatingMaxLengthsResponse> getDataProductRatingCommentMaxLength(){
        return ResponseEntity.ok(myDataProductsService.getDataProductRatingMaxLengths());
    }
}

