package com.mse.datafabric.immobilien.dtos.wgSuche;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class WgSucheDto {

    private long id;
    private String title;
    private Boolean addressHidden;
    private String borough;
    private Integer boroughId;
    private String from;
    private Double flatSize;
    private Double size;
    private Double rent;
    private String type;

    //image

    private String status;
    private String cityName;
    private String furnished;
    private String description;
    private String creationDate;

    private Integer wantedAmountEven;
    private Integer membersManCount;
    private Integer membersWomanCount;
    private Integer wantedAgeFrom;
    private Integer wantedAgeTo;
    private Integer imageCount;

    /*
          "city": {
        "id": 3663,
                "country": "Deutschland",
                "state": "Bayern",
                "name": "Erlangen",
                "boroughs": [],
        "vicinity": [],
        "campaigns": [],
        "metropols": [],
        "slug": "deutschland-bayern-erlangen"
    },
            "profile": {
        "id": 965297,
                "name": "WG Bruck",
                "gender": "undefined",
                "image": {
            "id": 103192332,
                    "url": "https://cdn.wg-suche.de/cdn.wg-suche.de/profile/12c91bd9-fcaf-4f2c-bb8b-5f58cbdfccdc",
                    "urls": {
                "S": {
                    "width": 150,
                            "height": 150,
                            "url": "https://cdn.wg-suche.de/cdn.wg-suche.de/profile/12c91bd9-fcaf-4f2c-bb8b-5f58cbdfccdc-150x150.jpg"
                },
                "XL": {
                    "width": 400,
                            "height": 400,
                            "url": "https://cdn.wg-suche.de/cdn.wg-suche.de/profile/12c91bd9-fcaf-4f2c-bb8b-5f58cbdfccdc-400x400.jpg"
                },
                "ORIGINAL": {
                    "width": 1023,
                            "height": 768,
                            "url": "https://cdn.wg-suche.de/cdn.wg-suche.de/profile/12c91bd9-fcaf-4f2c-bb8b-5f58cbdfccdc-original.jpg"
                },
                "XS": {
                    "width": 40,
                            "height": 40,
                            "url": "https://cdn.wg-suche.de/cdn.wg-suche.de/profile/12c91bd9-fcaf-4f2c-bb8b-5f58cbdfccdc-40x40.jpg"
                },
                "L": {
                    "width": 400,
                            "height": 300,
                            "url": "https://cdn.wg-suche.de/cdn.wg-suche.de/profile/12c91bd9-fcaf-4f2c-bb8b-5f58cbdfccdc-400x300.jpg"
                },
                "M": {
                    "width": 200,
                            "height": 200,
                            "url": "https://cdn.wg-suche.de/cdn.wg-suche.de/profile/12c91bd9-fcaf-4f2c-bb8b-5f58cbdfccdc-200x200.jpg"
                }
            }
        },
        "commercial": false,
                "userVerified": false
    },
            "memo": false,
            "owner": false,
            "inclusionary": true
     */

}
