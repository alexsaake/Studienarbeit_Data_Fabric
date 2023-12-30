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
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    //@PreAuthorize("hasAuthority('USER')")
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
            return ResponseEntity.internalServerError().body("Could not upload image" + ' ' + e.getMessage());
        }
    }
    @GetMapping(value = "/DataProduct/{dataProductId}/image", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<Resource> getDataProductImage(@PathVariable long dataProductId) {
        try {
            // Use the service method to get the image path
            String imagePath = myDataProductsService.getDataProductImagePath(dataProductId);

            // Create a Resource object for the image
            Path path = Paths.get(imagePath);
            Resource imageResource = new UrlResource(path.toUri());

            // Check if the resource exists and is readable
            if (!imageResource.exists() || !imageResource.isReadable()) {
                throw new FileNotFoundException("Could not read file: " + imagePath);
            }

            // Determine the content type of the image
            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                contentType = "application/octet-stream"; // default content type
            }

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(imageResource);

        } catch (Exception e) {
            myLogger.error("Error loading image for data product ID " + dataProductId, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PostMapping(
            value = "/DataProduct",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasAuthority('USER')")
    public long createDataProduct(
            @RequestPart("dataProductInfo") String dataProductInfo,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            DataProductAllDTO dto = mapper.readValue(dataProductInfo, DataProductAllDTO.class);

            long dataProductId = productData.createDataProduct(dto); // Using productData from DataProductsController

            if (dataProductId != -1 && image != null && !image.isEmpty()) {
                // Handle image uploading
                // Assuming saveDataProductImage method handles saving the image and associating it with the data product
                myDataProductsService.saveDataProductImage(dataProductId, image);
            }

            return dataProductId; // Returns the ID of the created product or -1 in case of error
        } catch (Exception e) {
            myLogger.error("Error in createDataProduct", e);
            return -1;
        }
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

